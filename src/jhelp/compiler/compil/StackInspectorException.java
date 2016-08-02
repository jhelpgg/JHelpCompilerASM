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
package jhelp.compiler.compil;

import java.util.List;

import jhelp.util.text.UtilText;

/**
 * Exception happen while stack inspection
 *
 * @author JHelp <br>
 */
public class StackInspectorException
      extends CompilerException
{
   /** Path follow in code source to reach the exception */
   private final List<StackInfo>    path;
   /** Stack status when exception happen */
   private final List<StackElement> stackStatus;

   /**
    * Create a new instance of StackInspectorException
    *
    * @param lineNumber
    *           Line number where error happen
    * @param stackStatus
    *           Stack status when exception happen
    * @param path
    *           Path follow in code source to reach the exception
    * @param message
    *           Message
    */
   public StackInspectorException(final int lineNumber, final List<StackElement> stackStatus, final List<StackInfo> path, final String message)
   {
      super(lineNumber, UtilText.concatenate(message, "\nStack state : ", stackStatus, "\nPath=", path));
      this.stackStatus = stackStatus;
      this.path = path;
   }

   /**
    * Create a new instance of StackInspectorException
    *
    * @param lineNumber
    *           Line number where error happen
    * @param stackStatus
    *           Stack status when exception happen
    * @param path
    *           Path follow in code source to reach the exception
    * @param message
    *           Message
    * @param cause
    *           Source of the issue
    */
   public StackInspectorException(final int lineNumber, final List<StackElement> stackStatus, final List<StackInfo> path, final String message,
         final Throwable cause)
   {
      super(lineNumber, UtilText.concatenate(message, "\nStack state : ", stackStatus, "\nPath=", path), cause);
      this.stackStatus = stackStatus;
      this.path = path;
   }

   /**
    * Path follow in code source to reach the exception
    *
    * @return Path follow in code source to reach the exception
    */
   public List<StackInfo> getPath()
   {
      return this.path;
   }

   /**
    * Stack status when exception happen
    *
    * @return Stack status when exception happen
    */
   public List<StackElement> getStackStatus()
   {
      return this.stackStatus;
   }
}