package jhelp.compiler.compil;

import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.LOOKUPSWITCH;
import com.sun.org.apache.bcel.internal.generic.SWITCH;
import com.sun.org.apache.bcel.internal.generic.Select;
import com.sun.org.apache.bcel.internal.generic.TABLESWITCH;

import jhelp.util.list.SortedArray;

/**
 * Describe a select/switch
 *
 * @author JHelp <br>
 */
class SelectInformation
{
   /** List of cases */
   private final SortedArray<SelectCase> cases;
   /** Label to go if no match */
   private String                        defaultLabel;
   /** Line declaration number */
   private final int                     lineNumber;
   /** Generated select instruction */
   private Select                        select;

   /**
    * Create a new instance of SelectInformation
    *
    * @param lineNumber
    *           Line declaration number
    */
   public SelectInformation(final int lineNumber)
   {
      this.cases = new SortedArray<SelectCase>(SelectCase.class, true);
      this.lineNumber = lineNumber;
   }

   /**
    * Create empty targets (They will be filled later)
    *
    * @return Empty targets
    */
   private InstructionHandle[] createEmptyTargets()
   {
      return new InstructionHandle[this.cases.getSize()];
   }

   /**
    * Collect all matches cases
    *
    * @return All matches case
    */
   private int[] createsMatches()
   {
      final int size = this.cases.getSize();
      final int[] matches = new int[size];

      for(int i = 0; i < size; i++)
      {
         matches[i] = this.cases.getElement(i).getMatch();
      }

      return matches;
   }

   /**
    * Add a case
    *
    * @param match
    *           Value to match
    * @param label
    *           Label to go
    */
   public void addCase(final int match, final String label)
   {
      this.cases.add(new SelectCase(match, label));
   }

   /**
    * Create a LOOKUPSWITCH instruction from current information
    *
    * @return Created LOOKUPSWITCH
    */
   public Select createLOOKUPSWITCH()
   {
      this.select = new LOOKUPSWITCH(this.createsMatches(), this.createEmptyTargets(), null);
      return this.select;
   }

   /**
    * Create a SWITCH instruction from current information
    *
    * @return Created SWITCH
    */
   public Select createSWITCH()
   {
      final SWITCH switch1 = new SWITCH(this.createsMatches(), this.createEmptyTargets(), null);
      this.select = (Select) switch1.getInstruction();
      return this.select;
   }

   /**
    * Create a TABLESWITCH instruction from current information
    *
    * @return Created TABLESWITCH
    */
   public Select createTABLESWITCH()
   {
      this.select = new TABLESWITCH(this.createsMatches(), this.createEmptyTargets(), null);
      return this.select;
   }

   /**
    * Obtain label of a case
    *
    * @param index
    *           Case index
    * @return Label associated
    */
   public String getCaseLabel(final int index)
   {
      return this.cases.getElement(index).getLabel();
   }

   /**
    * Default label to go
    *
    * @return Default label to go
    */
   public String getDefaultLabel()
   {
      return this.defaultLabel;
   }

   /**
    * Declaration line number
    *
    * @return Declaration line number
    */
   public int getLineNumber()
   {
      return this.lineNumber;
   }

   /**
    * Number of cases
    *
    * @return Number of cases
    */
   public int numberOfCases()
   {
      return this.cases.getSize();
   }

   /**
    * Resolve a case
    *
    * @param index
    *           Case index
    * @param instructionHandle
    *           Instruction handle associated
    */
   public void resolveCase(final int index, final InstructionHandle instructionHandle)
   {
      this.select.setTarget(index, instructionHandle);
   }

   /**
    * Resolve the default label
    *
    * @param instructionHandle
    *           Instruction handle associated
    */
   public void resolveDefaultLabel(final InstructionHandle instructionHandle)
   {
      this.select.setTarget(instructionHandle);
   }

   /**
    * Define the default label
    *
    * @param defaultLabel
    *           Default label
    */
   public void setDefaultLabel(final String defaultLabel)
   {
      this.defaultLabel = defaultLabel;
   }
}