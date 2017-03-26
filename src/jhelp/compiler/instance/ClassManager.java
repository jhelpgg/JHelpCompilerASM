/**
 * <h1>License :</h1> <br>
 * The following code is deliver as is. I take care that code compile and work, but I am not responsible about any damage it may
 * cause.<br>
 * You can use, modify, the code as your need for any usage. But you can't do any action that avoid me or other person use,
 * modify this code. The code is free for usage and modification, you can't change that fact.<br>
 * <br>
 *
 * @author JHelp
 */
package jhelp.compiler.instance;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import jhelp.compiler.compil.CompilerException;
import jhelp.util.classLoader.JHelpClassLoader;
import jhelp.util.classLoader.JHelpJarClassLoader;
import jhelp.util.list.HashInt;
import jhelp.util.list.Pair;
import jhelp.util.list.foreach.ForEach;
import jhelp.util.list.foreach.ForEachAsyncListener;
import jhelp.util.reflection.Reflector;
import jhelp.util.text.UtilText;

/**
 * Manage class instance and compilation of ASM files at the fly
 *
 * @author JHelp <br>
 */
public class ClassManager
{
   /**
    * Event manager
    *
    * @author JHelp <br>
    */
   class EventManager
         implements ForEachAsyncListener, ActionCompileListener
   {
      /**
       * Create a new instance of EventManager
       */
      EventManager()
      {
      }

      /**
       * Called when compilation of one ASM done <br>
       * <br>
       * <b>Parent documentation:</b><br>
       * {@inheritDoc}
       *
       * @param compilationID
       *           Compilation ID
       * @param className
       *           Class compiled and ready to use
       * @see jhelp.compiler.instance.ActionCompileListener#compilationDone(int, java.lang.String)
       */
      @Override
      public void compilationDone(final int compilationID, final String className)
      {
         ClassManager.this.addClassFor(className, compilationID);
      }

      /**
       * Called on compilation issue <br>
       * <br>
       * <b>Parent documentation:</b><br>
       * {@inheritDoc}
       *
       * @param compilationID
       *           Compilation group ID
       * @param compilerException
       *           Compilation exception happen
       * @see jhelp.compiler.instance.ActionCompileListener#compilationIssue(int, jhelp.compiler.compil.CompilerException)
       */
      @Override
      public void compilationIssue(final int compilationID, final CompilerException compilerException)
      {
         ClassManager.this.reportCompilationIssue(compilationID, compilerException);
      }

      /**
       * Called on error during compilation process <br>
       * <br>
       * <b>Parent documentation:</b><br>
       * {@inheritDoc}
       *
       * @param compilationID
       *           Compilation group ID
       * @param error
       *           Compilation exception happen
       * @see jhelp.compiler.instance.ActionCompileListener#errorHappen(int, java.lang.Error)
       */
      @Override
      public void errorHappen(final int compilationID, final Error error)
      {
         ClassManager.this.reportError(compilationID, error);
      }

      /**
       * Called on exception can't be report be {@link #compilationIssue(int, CompilerException)} during compilation process
       * <br>
       * <br>
       * <b>Parent documentation:</b><br>
       * {@inheritDoc}
       *
       * @param compilationID
       *           Compilation group ID
       * @param exception
       *           Exception happen
       * @see jhelp.compiler.instance.ActionCompileListener#exceptionHappen(int, java.lang.Exception)
       */
      @Override
      public void exceptionHappen(final int compilationID, final Exception exception)
      {
         ClassManager.this.reportException(compilationID, exception);
      }

      /**
       * Called when all parallel tasks are finished <br>
       * <br>
       * <b>Parent documentation:</b><br>
       * {@inheritDoc}
       *
       * @param forEachID
       *           Parallel task ID
       * @see jhelp.util.list.foreach.ForEachAsyncListener#forEachAsyncTerminated(int)
       */
      @Override
      public void forEachAsyncTerminated(final int forEachID)
      {
         ClassManager.this.compilationDone(forEachID);
      }
   }

   /** Next compilation group ID */
   private static final AtomicInteger                              NEXT_ID = new AtomicInteger(0);

   /** Action for compile one ASM */
   private final ActionCompile                                     actionCompile;
   /** Class loader where store compiled class */
   private JHelpClassLoader                                        classLoader;
   /** Event manager */
   private final EventManager                                      eventManager;
   /** Class loader for external jars */
   private final JHelpJarClassLoader                               jarClassLoader;
   /** Registered listeners to compilation group event (Compilation group ID <=> [listener; succeed compiled class list]) */
   private final HashInt<Pair<ClassManagerListener, List<String>>> listeners;

   /**
    * Create a new instance of ClassManager
    */
   public ClassManager()
   {
      this.eventManager = new EventManager();
      this.classLoader = new JHelpClassLoader();
      this.jarClassLoader = new JHelpJarClassLoader();
      this.classLoader.add(this.jarClassLoader);
      this.actionCompile = new ActionCompile(this, this.eventManager);
      this.listeners = new HashInt<Pair<ClassManagerListener, List<String>>>();
   }

   /**
    * Compile a list of ASM
    *
    * @param compilationID
    *           Compilation group ID
    * @param classManagerListener
    *           Listener to alert for this compilation group
    * @param sources
    *           List of ASM to compile
    * @return Compilation group ID
    * @throws NullPointerException
    *            If given classManagerListener is null
    */
   private int compileASMs(final int compilationID, final ClassManagerListener classManagerListener, final List<SourceInformation> sources)
   {
      if(classManagerListener == null)
      {
         throw new NullPointerException("classManagerListener musn't be null");
      }

      synchronized(this.listeners)
      {
         this.listeners.put(compilationID, new Pair<ClassManagerListener, List<String>>(classManagerListener, new ArrayList<String>()));
      }

      ForEach.forEachAsync(sources, this.actionCompile, this.eventManager, compilationID);
      return compilationID;
   }

   /**
    * Add class and its byte code
    *
    * @param className
    *           Class to add
    * @param data
    *           Byte code
    */
   void addClass(final String className, final byte[] data)
   {
      this.classLoader.addClass(className, data);
   }

   /**
    * Add a class name to succeed compiled class list for a compilation group
    *
    * @param className
    *           Class name to add
    * @param compilationID
    *           Compilation group ID where add
    */
   void addClassFor(final String className, final int compilationID)
   {
      synchronized(this.listeners)
      {
         this.listeners.get(compilationID).element2.add(className);
      }
   }

   /**
    * Called when a compilation group finished
    *
    * @param compilationID
    *           Compilation group finished to compile
    */
   void compilationDone(final int compilationID)
   {
      synchronized(this.listeners)
      {
         final Pair<ClassManagerListener, List<String>> pair = this.listeners.get(compilationID);
         pair.element1.compilationReady(this, compilationID, pair.element2);
         this.listeners.remove(compilationID);
      }
   }

   /**
    * Report compilation issue for a compilation group
    *
    * @param compilationID
    *           Compilation group ID
    * @param compilerException
    *           Issue to report
    */
   void reportCompilationIssue(final int compilationID, final CompilerException compilerException)
   {
      synchronized(this.listeners)
      {
         final Pair<ClassManagerListener, List<String>> pair = this.listeners.get(compilationID);
         pair.element1.compilationIssue(this, compilationID, compilerException);
      }
   }

   /**
    * Report compilation issue for a compilation group
    *
    * @param compilationID
    *           Compilation group ID
    * @param error
    *           Issue to report
    */
   void reportError(final int compilationID, final Error error)
   {
      synchronized(this.listeners)
      {
         final Pair<ClassManagerListener, List<String>> pair = this.listeners.get(compilationID);
         pair.element1.errorHappen(this, compilationID, error);
      }
   }

   /**
    * Report compilation issue for a compilation group
    *
    * @param compilationID
    *           Compilation group ID
    * @param exception
    *           Issue to report
    */
   void reportException(final int compilationID, final Exception exception)
   {
      synchronized(this.listeners)
      {
         final Pair<ClassManagerListener, List<String>> pair = this.listeners.get(compilationID);
         pair.element1.exceptionHappen(this, compilationID, exception);
      }
   }

   /**
    * Add a class file necessary for future class resolution ({@link #newInstance(String)} and
    * {@link #invokeStatic(String, String, Object...)}) because this class can't be resolved with default class loader (Not a
    * class of current class path)<br>
    * The file must be a valid class file<br>
    * The file must be in the same hierarchy as its package.<br>
    * for example for : pack1.pack2.pack3.MyClasss the path must end like this : .../pack1/pack2/pack3/MyClass.class
    *
    * @param file
    *           File to add
    */
   public void addClassFile(final File file)
   {
      this.classLoader.add(file);
   }

   /**
    * Add a jar file necessary for future class resolution ({@link #newInstance(String)} and
    * {@link #invokeStatic(String, String, Object...)}) because the classes of this jar can't be resolved with default class
    * loader (The jar not inside the current class path)
    *
    * @param jarFile
    *           Jar file to add
    * @throws IOException
    *            On reading issue
    */
   public void addJarFile(final File jarFile) throws IOException
   {
      this.jarClassLoader.add(jarFile);
   }

   /**
    * Compile a group of ASM in parallel
    *
    * @param classManagerListener
    *           Listener to report compilation progress
    * @param streamsOnASMs
    *           List of ASM to compile in "same time"
    * @return Compilation group ID. Use it to identify the group inside listener reports
    * @throws NullPointerException
    *            If classManagerListener is null or one of ASM is null
    */
   public int compileASMs(final ClassManagerListener classManagerListener, final Collection<InputStream> streamsOnASMs)
   {
      final int compilationID = ClassManager.NEXT_ID.getAndIncrement();
      final List<SourceInformation> list = new ArrayList<SourceInformation>();

      for(final InputStream inputStream : streamsOnASMs)
      {
         if(inputStream == null)
         {
            throw new NullPointerException("One of given stream is null !");
         }

         list.add(new SourceInformation(compilationID, inputStream));
      }

      return this.compileASMs(compilationID, classManagerListener, list);
   }

   /**
    * Compile a group of ASM in parallel
    *
    * @param classManagerListener
    *           Listener to report compilation progress
    * @param streamsOnASMs
    *           List of ASM to compile in "same time"
    * @return Compilation group ID. Use it to identify the group inside listener reports
    * @throws NullPointerException
    *            If classManagerListener is null or one of ASM is null
    */
   public int compileASMs(final ClassManagerListener classManagerListener, final InputStream... streamsOnASMs)
   {
      final int compilationID = ClassManager.NEXT_ID.getAndIncrement();
      final List<SourceInformation> list = new ArrayList<SourceInformation>();

      for(final InputStream inputStream : streamsOnASMs)
      {
         if(inputStream == null)
         {
            throw new NullPointerException("One of given stream is null !");
         }

         list.add(new SourceInformation(compilationID, inputStream));
      }

      return this.compileASMs(compilationID, classManagerListener, list);
   }

   /**
    * Invoke a public non static method for an object
    *
    * @param <RETURN>
    *           Method return type
    * @param <OBJECT>
    *           Object type
    * @param object
    *           Object instance where call the method
    * @param methodName
    *           Method name
    * @param parameters
    *           Given parameters to method
    * @return Method result
    * @throws NoSuchMethodException
    *            If method with given parameters not exists or not public in given object
    * @throws IllegalArgumentException
    *            If one of method argument have an invalid value
    * @throws IllegalAccessException
    *            If method is not public or object/method are protected by a security manager
    * @throws InvocationTargetException
    *            On invocation issue
    */
   @SuppressWarnings("unchecked")
   public <RETURN, OBJECT> RETURN invoke(final OBJECT object, final String methodName, final Object... parameters)
         throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
   {
      return (RETURN) Reflector.invokePublicMethod(object, methodName, parameters);
   }

   /**
    * Invoke a public static method
    *
    * @param <RETURN>
    *           Method return type
    * @param className
    *           Class name where lies the static method (Compiled with success or from JRE)
    * @param methodName
    *           Method name
    * @param parameters
    *           Method parameters
    * @return Method result
    * @throws NoSuchMethodException
    *            If method with given parameters not exists or not public in given object
    * @throws IllegalArgumentException
    *            If one of method argument have an invalid value
    * @throws IllegalAccessException
    *            If method is not public or object/method are protected by a security manager
    * @throws InvocationTargetException
    *            On invocation issue
    * @throws ClassNotFoundException
    *            If given class can't be resolve by the class manager
    */
   @SuppressWarnings("unchecked")
   public <RETURN> RETURN invokeStatic(final String className, final String methodName, final Object... parameters)
         throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, ClassNotFoundException
   {
      return (RETURN) Reflector.invokePublicMethod(this.classLoader.loadClass(className), methodName, parameters);
   }

   /**
    * Indicates if given class is resolved (Can't be redefined, see {@link #newClassLoader()} for a trick)
    *
    * @param className
    *           Class name
    * @return {@code true} if given class is resolved
    */
   public boolean isResolved(final String className)
   {
      return this.classLoader.isLoaded(className);
   }

   /**
    * List of methods of a class
    *
    * @param className
    *           Class name
    * @return List of methods
    * @throws ClassNotFoundException
    *            If given class name not know by the class manager
    */
   public Method[] listOfMethod(final String className) throws ClassNotFoundException
   {
      return this.classLoader.loadClass(className).getDeclaredMethods();
   }

   /**
    * Make the class manager consider previous class resolution as invalid.<br>
    * This way it is possible to change to code of same class several times, when they are already resolved.<br>
    * Don't abuse this method it improve time and memory.<br>
    * For not resolved class it is not necessary to call it, so check with {@link #isResolved(String)} to know if classes you
    * want to compile are resolved or not !
    */
   public void newClassLoader()
   {
      final JHelpClassLoader classLoader = this.classLoader;
      this.classLoader = new JHelpClassLoader();
      this.classLoader.add(classLoader);
   }

   /**
    * Create instance of class known by the manager (Compiled with success or from JRE)
    *
    * @param <OBJECT>
    *           Object type
    * @param className
    *           Class name to instance (Compiled with success or from JRE)
    * @return Class instance
    * @throws ClassNotFoundException
    *            If class manager can't resolve the class
    */
   @SuppressWarnings("unchecked")
   public <OBJECT> OBJECT newInstance(final String className) throws ClassNotFoundException
   {
      return (OBJECT) Reflector.newInstance(className, this.classLoader);
   }

   /**
    * Create new instance of class on choosing the constructor specifically.<br>
    * The method will search a constructor that match to given parameter and invoke it<br>
    * Beware of usage of {@code null} as one of parameters it, if it ambiguous, the first constructor found that match with be
    * used.<br>
    * By example if class A have 2 constructor A(java.lang.String,int) and A(java.util.List,int) and call
    * classManager.newInstance2("A", null, 2); Will choose one of the constructor, but you can't control witch one is called
    *
    * @param <OBJECT>
    *           Instance type return
    * @param className
    *           Class name
    * @param parameters
    *           Parameters to give to constructor
    * @return Created instance
    * @throws ClassNotFoundException
    *            If class not know by the class manager
    * @throws NoSuchMethodException
    *            If no constructor match for given parameters
    * @throws InstantiationException
    *            If construction failed (Exception happen on construction)
    * @throws IllegalAccessException
    *            If not allowed to call the constructor
    * @throws IllegalArgumentException
    *            If one argument is not valid
    * @throws InvocationTargetException
    *            On other issue
    */
   @SuppressWarnings("unchecked")
   public <OBJECT> OBJECT newInstance2(final String className, final Object... parameters) throws ClassNotFoundException, NoSuchMethodException,
         InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
   {
      final Class<?> claz = this.classLoader.loadClass(className);
      final Class<?>[] types = Reflector.obtainTypes(parameters);
      Constructor<?> constructor = null;

      for(final Constructor<?> cons : claz.getConstructors())
      {
         if(Reflector.typeMatch(types, cons.getParameterTypes()))
         {
            constructor = cons;
            break;
         }
      }

      if(constructor == null)
      {
         throw new NoSuchMethodException(UtilText.concatenate("Constructor of ", className, "with given parameters not found ! : ", parameters));
      }

      return (OBJECT) constructor.newInstance(parameters);
   }

   /**
    * Resolve a class
    *
    * @param className
    *           Class name
    * @return Resolved class
    * @throws ClassNotFoundException
    *            If class not know by the class manager
    */
   public Class<?> obtainClass(final String className) throws ClassNotFoundException
   {
      return this.classLoader.loadClass(className);
   }
}