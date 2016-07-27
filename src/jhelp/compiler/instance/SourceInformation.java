package jhelp.compiler.instance;

import java.io.InputStream;

/**
 * Describe ASM stream source
 *
 * @author JHelp <br>
 */
class SourceInformation
{
   /** Compilation ID where stream lies */
   final int         compilationID;
   /** Stream on ASM file */
   final InputStream stream;

   /**
    * Create a new instance of SourceInformation
    *
    * @param compilationID
    *           Compilation ID where stream lies
    * @param stream
    *           Stream on ASM file
    */
   public SourceInformation(final int compilationID, final InputStream stream)
   {
      this.compilationID = compilationID;
      this.stream = stream;
   }
}