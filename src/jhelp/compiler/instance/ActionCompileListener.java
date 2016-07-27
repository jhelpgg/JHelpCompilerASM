package jhelp.compiler.instance;

import jhelp.compiler.compil.CompilerException;

/**
 * Listener of {@link ActionCompile} events
 *
 * @author JHelp <br>
 */
interface ActionCompileListener
{
   /**
    * Called when compilation succeed
    *
    * @param compilationID
    *           Compilation ID
    * @param className
    *           Class name compiled and ready to use
    */
   public void compilationDone(int compilationID, String className);

   /**
    * Called on compilation issue
    *
    * @param compilationID
    *           Compilation ID
    * @param compilerException
    *           Compilation exception happen
    */
   public void compilationIssue(int compilationID, CompilerException compilerException);

   /**
    * Called on error during compilation process
    *
    * @param compilationID
    *           Compilation ID
    * @param error
    *           Error happen
    */
   public void errorHappen(int compilationID, Error error);

   /**
    * Called on exception can't be report be {@link #compilationIssue(int, CompilerException)} during compilation process
    *
    * @param compilationID
    *           Compilation ID
    * @param exception
    *           Exception happen
    */
   public void exceptionHappen(int compilationID, Exception exception);
}