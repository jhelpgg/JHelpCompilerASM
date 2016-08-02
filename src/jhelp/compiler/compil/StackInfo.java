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

/**
 * Information on stack for a line
 *
 * @author JHelp <br>
 */
public class StackInfo
{
   /** Compiled information */
   private final StringBuilder info;
   /** Line number of the information */
   private final int           lineNumber;

   /**
    * Create a new instance of StackInfo
    *
    * @param lineNumber
    *           Line number of the information
    * @param start
    *           Stack state before execute instruction at given line
    */
   public StackInfo(final int lineNumber, final List<StackElement> start)
   {
      this.lineNumber = lineNumber;
      this.info = new StringBuilder(start.toString());
      this.info.append(" => ");
   }

   /**
    * Stack state when exit instruction at line number
    *
    * @param end
    *           Stack state
    */
   void appendEnd(final List<StackElement> end)
   {
      this.info.append(end.toString());
   }

   /**
    * Compiled information
    *
    * @return Compiled information
    */
   public String getInfo()
   {
      return this.info.toString();
   }

   /**
    * Line number of instruction
    *
    * @return Line number of instruction
    */
   public int getLineNumber()
   {
      return this.lineNumber;
   }

   /**
    * String representation <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    *
    * @return String representation
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return String.valueOf(this.lineNumber);
   }
}