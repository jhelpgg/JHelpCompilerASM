package jhelp.compiler.instance;

import java.io.IOException;

import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import com.sun.org.apache.bcel.internal.generic.ClassGen;

import jhelp.compiler.compil.Compiler;
import jhelp.compiler.compil.CompilerException;
import jhelp.util.io.ByteArray;
import jhelp.util.list.foreach.ActionEach;

/**
 * Action that compile one ASM file stream
 *
 * @author JHelp <br>
 */
class ActionCompile
      implements ActionEach<SourceInformation>
{
   /** Listener to report compilation process information */
   private final ActionCompileListener actionCompileListener;
   /** Class manager where store compiled class */
   private final ClassManager          classManager;

   /**
    * Create a new instance of ActionCompile
    *
    * @param classManager
    *           Class manager where store compiled class
    * @param actionCompileListener
    *           Listener to report compilation process information
    */
   public ActionCompile(final ClassManager classManager, final ActionCompileListener actionCompileListener)
   {
      this.classManager = classManager;
      this.actionCompileListener = actionCompileListener;
   }

   /**
    * Compile an ASM stream <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    *
    * @param source
    *           ASM stream information
    * @see jhelp.util.list.foreach.ActionEach#doAction(java.lang.Object)
    */
   @Override
   public void doAction(final SourceInformation source)
   {
      try
      {
         final Compiler compiler = new Compiler();
         final ClassGen classGen = compiler.compile(source.stream);
         final JavaClass javaClass = classGen.getJavaClass();
         final ByteArray byteArray = new ByteArray();
         javaClass.dump(byteArray.getOutputStream());

         synchronized(this.classManager)
         {
            this.classManager.addClass(javaClass.getClassName(), byteArray.toArray());
         }

         synchronized(this.actionCompileListener)
         {
            this.actionCompileListener.compilationDone(source.compilationID, javaClass.getClassName());
         }
      }
      catch(final CompilerException compilerException)
      {
         synchronized(this.actionCompileListener)
         {
            this.actionCompileListener.compilationIssue(source.compilationID, compilerException);
         }
      }
      catch(final IOException exception)
      {
         synchronized(this.actionCompileListener)
         {
            this.actionCompileListener.compilationIssue(source.compilationID, new CompilerException(-1, "Failed to write the class", exception));
         }
      }
   }

   /**
    * Called if error happen while compilation <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    *
    * @param source
    *           ASM source information
    * @param error
    *           Error happen
    * @see jhelp.util.list.foreach.ActionEach#report(java.lang.Object, java.lang.Error)
    */
   @Override
   public void report(final SourceInformation source, final Error error)
   {
      synchronized(this.actionCompileListener)
      {
         this.actionCompileListener.errorHappen(source.compilationID, error);
      }
   }

   /**
    * Called if exception happen while compilation <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    *
    * @param source
    *           ASM source information
    * @param exception
    *           Exception happen
    * @see jhelp.util.list.foreach.ActionEach#report(java.lang.Object, java.lang.Exception)
    */
   @Override
   public void report(final SourceInformation source, final Exception exception)
   {
      synchronized(this.actionCompileListener)
      {
         this.actionCompileListener.exceptionHappen(source.compilationID, exception);
      }
   }
}