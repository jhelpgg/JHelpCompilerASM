package jhelp.compiler.compil;

import jhelp.util.text.UtilText;

/**
 * Exception that may happen in compilation.<br>
 * The line number corresponds to original source assembly code line number. It is most accurate as possible
 *
 * @author JHelp <br>
 */
public class CompilerException
      extends Exception
{
   /** Line number in original source assembly code that cause the exception */
   private final int lineNumber;

   /**
    * Create a new instance of CompilerException
    *
    * @param lineNumber
    *           Line number in original source assembly code that cause the exception
    * @param message
    *           Exception message
    */
   public CompilerException(final int lineNumber, final String message)
   {
      super(UtilText.concatenate(message, "\nError line ", lineNumber));
      this.lineNumber = lineNumber;
   }

   /**
    * Create a new instance of CompilerException
    *
    * @param lineNumber
    *           Line number in original source assembly code that cause the exception
    * @param message
    *           Exception message
    * @param cause
    *           Exception the cause the issue
    */
   public CompilerException(final int lineNumber, final String message, final Throwable cause)
   {
      super(UtilText.concatenate(message, " Near line ", lineNumber), cause);
      this.lineNumber = lineNumber;
   }

   /**
    * Line number in original source assembly code that cause the exception
    *
    * @return Line number in original source assembly code that cause the exception
    */
   public int getLineNumber()
   {
      return this.lineNumber;
   }
}