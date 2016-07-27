package jhelp.compiler.tests;

import java.util.List;

import jhelp.compiler.compil.CompilerException;
import jhelp.compiler.instance.ClassManager;
import jhelp.compiler.instance.ClassManagerListener;
import jhelp.util.MemorySweeper;
import jhelp.util.debug.Debug;
import jhelp.util.debug.DebugLevel;

/**
 * Launch a test for compile in parallel several class and use them
 *
 * @author JHelp <br>
 */
public class TestClassManager
      implements ClassManagerListener
{
   /**
    * Launch the test
    *
    * @param args
    *           Unused
    */
   public static void main(final String[] args)
   {
      MemorySweeper.launch();

      // Create listener of compilation events
      final TestClassManager testClassManager = new TestClassManager();
      // Create the class manager that will do compilation
      final ClassManager classManager = new ClassManager();
      // Launch the compilation of several class in parallel
      classManager.compileASMs(testClassManager, TestClassManager.class.getResourceAsStream("add.asm"), TestClassManager.class.getResourceAsStream("sub.asm"),
            TestClassManager.class.getResourceAsStream("mul.asm"), TestClassManager.class.getResourceAsStream("div.asm"));
   }

   /**
    * Called on compilation issue <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    *
    * @param classManager
    *           Class manager
    * @param compilationID
    *           Compilation group ID
    * @param compilerException
    *           Issue
    * @see jhelp.compiler.instance.ClassManagerListener#compilationIssue(jhelp.compiler.instance.ClassManager, int,
    *      jhelp.compiler.compil.CompilerException)
    */
   @Override
   public void compilationIssue(final ClassManager classManager, final int compilationID, final CompilerException compilerException)
   {
      Debug.printException(compilerException, "Failed to compile");
   }

   /**
    * Called when a compilation group is finished <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    *
    * @param classManager
    *           Class manager source
    * @param compilationID
    *           Compilation group ID
    * @param classesName
    *           List of succeed compiled class
    * @see jhelp.compiler.instance.ClassManagerListener#compilationReady(jhelp.compiler.instance.ClassManager, int,
    *      java.util.List)
    */
   @Override
   public void compilationReady(final ClassManager classManager, final int compilationID, final List<String> classesName)
   {
      Debug.println(DebugLevel.INFORMATION, "Classes compiled : ", classesName);
      Operation operation;

      for(final String className : classesName)
      {
         try
         {
            operation = classManager.newInstance(className);
            Debug.println(DebugLevel.INFORMATION, className, ".calculate(8,6)=", operation.calculate(8, 6));
         }
         catch(final Exception exception)
         {
            Debug.printException(exception, "Failed to instantiate : ", classesName);
         }
         catch(final Error error)
         {
            Debug.printError(error, "Failed to instantiate : ", classesName);
         }
      }

      MemorySweeper.exit(0);
   }

   /**
    * Called on compilation issue <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    *
    * @param classManager
    *           Class manager
    * @param compilationID
    *           Compilation group ID
    * @param error
    *           Issue
    * @see jhelp.compiler.instance.ClassManagerListener#errorHappen(jhelp.compiler.instance.ClassManager, int, java.lang.Error)
    */
   @Override
   public void errorHappen(final ClassManager classManager, final int compilationID, final Error error)
   {
      Debug.printError(error, "Failed to compile");
   }

   /**
    * Called on compilation issue <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    *
    * @param classManager
    *           Class manager
    * @param compilationID
    *           Compilation group ID
    * @param exception
    *           Issue
    * @see jhelp.compiler.instance.ClassManagerListener#exceptionHappen(jhelp.compiler.instance.ClassManager, int,
    *      java.lang.Exception)
    */
   @Override
   public void exceptionHappen(final ClassManager classManager, final int compilationID, final Exception exception)
   {
      Debug.printException(exception, "Failed to compile");
   }
}