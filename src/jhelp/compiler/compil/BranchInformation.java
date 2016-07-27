package jhelp.compiler.compil;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;

/**
 * Describe a branch instruction GOT, IF*, ... to resolve later
 *
 * @author JHelp <br>
 */
class BranchInformation
{
   /** Branch handle where put resolved target */
   final BranchHandle branchHandle;
   /** Label target */
   final String       label;
   /** Declaration line number */
   final int          lineNumber;

   /**
    * Create a new instance of BranchInformation
    *
    * @param branchHandle
    *           Branch handle where put resolved target
    * @param label
    *           Label target
    * @param lineNumber
    *           Declaration line number
    */
   public BranchInformation(final BranchHandle branchHandle, final String label, final int lineNumber)
   {
      this.branchHandle = branchHandle;
      this.label = label;
      this.lineNumber = lineNumber;
   }
}