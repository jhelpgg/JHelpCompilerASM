package jhelp.compiler.instance;

import java.util.List;

import jhelp.compiler.compil.CompilerException;

/**
 * Listener of events during a compilation group
 *
 * @author JHelp <br>
 */
public interface ClassManagerListener
{
   /**
    * Called when one of ASM in compilation group failed to compile
    *
    * @param classManager
    *           Class manager where compilation was launched
    * @param compilationID
    *           Compilation group ID
    * @param compilerException
    *           Compilation exception description
    */
   public void compilationIssue(ClassManager classManager, int compilationID, CompilerException compilerException);

   /**
    * Indicates when all ASM in group are compiled and give list of succeed ones
    *
    * @param classManager
    *           Class manager where compilation was launched
    * @param compilationID
    *           Compilation group ID
    * @param classesName
    *           List of classes that have succeed in compile.<br>
    *           You can instance them with given class manager. It also means that class in group not in this list failed to
    *           compile and so can't be used
    */
   public void compilationReady(ClassManager classManager, int compilationID, List<String> classesName);

   /**
    * Called when an error happen during compilation process
    *
    * @param classManager
    *           Class manager where compilation was launched
    * @param compilationID
    *           Compilation group ID
    * @param error
    *           Error happen
    */
   public void errorHappen(ClassManager classManager, int compilationID, Error error);

   /**
    * Called when an exception happen during compilation process.<br>
    * It is an exception that are {@link #compilationIssue(ClassManager, int, CompilerException)} can't report
    *
    * @param classManager
    *           Class manager where compilation was launched
    * @param compilationID
    *           Compilation group ID
    * @param exception
    *           Exception happen
    */
   public void exceptionHappen(ClassManager classManager, int compilationID, Exception exception);
}