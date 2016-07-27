package jhelp.compiler.compil;

import java.util.List;

import com.sun.org.apache.bcel.internal.generic.BasicType;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.Type;

import jhelp.util.math.UtilMath;

/**
 * Line of method code
 *
 * @author JHelp <br>
 */
class CodeLine
      implements CompilerConstants
{
   /** Code instruction */
   private final String       instruction;
   /** Line number where instruction lies */
   private final int          lineNumber;
   /** First parameter */
   private String             parameter1;
   /** Second parameter */
   private String             parameter2;
   /** Instruction parameters */
   private final List<String> parameters;

   /**
    * Create a new instance of CodeLine
    *
    * @param instruction
    *           Code instruction
    * @param parameters
    *           Instruction parameters
    * @param lineNumber
    *           Line number where instruction lies
    */
   public CodeLine(final String instruction, final List<String> parameters, final int lineNumber)
   {
      this.instruction = instruction;
      this.lineNumber = lineNumber;
      final int size = parameters.size();

      if(size > 0)
      {
         this.parameter1 = parameters.get(0);
      }

      if(size > 1)
      {
         this.parameter2 = parameters.get(1);
      }

      this.parameters = parameters;
   }

   /**
    * Parse a select/switch instruction
    *
    * @param compilerContext
    *           Compiler context
    * @return Parsed instruction
    * @throws CompilerException
    *            If the number of parameters is not correct OR a match value not a valid integer
    */
   private SelectInformation parseSelectInformation(final CompilerContext compilerContext) throws CompilerException
   {
      final int size = this.parameters.size();

      if((size & 1) == 0)
      {
         throw new CompilerException(this.lineNumber, "Wrong number of parameters !");
      }

      final int limit = size - 1;
      final SelectInformation selectInformation = new SelectInformation(this.lineNumber);

      for(int i = 0; i < limit; i += 2)
      {
         try
         {
            selectInformation.addCase(Integer.parseInt(this.parameters.get(i)), this.parameters.get(i + 1));
         }
         catch(final Exception exception)
         {
            throw new CompilerException(this.lineNumber, "Wrong match value : " + this.parameters.get(i), exception);
         }
      }

      selectInformation.setDefaultLabel(this.parameters.get(limit));
      compilerContext.addSwitch(selectInformation);
      return selectInformation;
   }

   /**
    * Line number where instruction lies
    *
    * @return Line number where instruction lies
    */
   public int getLineNumber()
   {
      return this.lineNumber;
   }

   /**
    * Parse the code line
    *
    * @param instructionList
    *           Instruction list where happen the instruction
    * @param compilerContext
    *           Compiler context
    * @return Handle on parsed instruction. Can be {@code null} if instruction not a real opcode instruction
    * @throws CompilerException
    *            On parse issue
    */
   public InstructionHandle parseCode(final InstructionList instructionList, final CompilerContext compilerContext) throws CompilerException
   {
      // ************************
      // *** Not real opcodes ***
      // ************************

      // VAR <type> <name>
      if(OpcodeConstants.Z_VAR.equals(this.instruction) == true)
      {
         if((this.parameter1 == null) || (this.parameter2 == null))
         {
            throw new CompilerException(this.lineNumber, "Miss parameters in VAR !");
         }

         compilerContext.addGetLocalReference(this.parameter2, this.parameter1, this.lineNumber);
         return null;
      }

      // LABEL <name>
      if(OpcodeConstants.Z_LABEL.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss the label name");
         }

         compilerContext.setLabelToDefine(this.parameter1, this.lineNumber);
         return null;
      }

      // ********************
      // *** Real opcodes ***
      // ********************

      // Generated instruction
      Instruction instruction = null;
      // Target for branch instructions
      String branchTarget = null;

      if(OpcodeConstants.AALOAD.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.AALOAD();
      }
      else if(OpcodeConstants.AASTORE.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.AASTORE();
      }
      else if(OpcodeConstants.ACONST_NULL.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.ACONST_NULL();
      }
      // ALOAD this|<methodParameter>|<localVariable>
      else if(OpcodeConstants.ALOAD.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss parameter name !");
         }

         instruction = new com.sun.org.apache.bcel.internal.generic.ALOAD(compilerContext.getLocalReference(this.parameter1, this.lineNumber));
      }
      // ANEWARRAY <Type>
      else if(OpcodeConstants.ANEWARRAY.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss array type !");
         }

         instruction = new com.sun.org.apache.bcel.internal.generic.ANEWARRAY(compilerContext.addTypeReference(this.parameter1, this.lineNumber));
      }
      else if(OpcodeConstants.ARETURN.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.ARETURN();
      }
      else if(OpcodeConstants.ARRAYLENGTH.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.ARRAYLENGTH();
      }
      // ASTORE <methodParameter>|<localVariable>
      else if(OpcodeConstants.ASTORE.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss parameter name !");
         }

         if("this".equals(this.parameter1) == true)
         {
            throw new CompilerException(this.lineNumber, "Can't store in 'this' !");
         }

         instruction = new com.sun.org.apache.bcel.internal.generic.ASTORE(compilerContext.getLocalReference(this.parameter1, this.lineNumber));
      }
      else if(OpcodeConstants.ATHROW.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.ATHROW();
      }
      else if(OpcodeConstants.BALOAD.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.BALOAD();
      }
      else if(OpcodeConstants.BASTORE.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.BASTORE();
      }
      // BIPUSH <value>
      else if(OpcodeConstants.BIPUSH.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss the byte value");
         }

         try
         {
            instruction = new com.sun.org.apache.bcel.internal.generic.BIPUSH(Byte.parseByte(this.parameter1));
         }
         catch(final Exception exception)
         {
            throw new CompilerException(this.lineNumber, "Invalid byte constant : " + this.parameter1, exception);
         }
      }
      else if(OpcodeConstants.BREAKPOINT.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.BREAKPOINT();
      }
      else if(OpcodeConstants.CALOAD.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.CALOAD();
      }
      else if(OpcodeConstants.CASTORE.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.CASTORE();
      }
      // CHECKCAST <ClassName>
      else if(OpcodeConstants.CHECKCAST.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss the type to check the cast !");
         }

         instruction = new com.sun.org.apache.bcel.internal.generic.CHECKCAST(compilerContext.addClassReference(this.parameter1, this.lineNumber));
      }
      else if(OpcodeConstants.D2F.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.D2F();
      }
      else if(OpcodeConstants.D2I.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.D2I();
      }
      else if(OpcodeConstants.D2L.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.D2L();
      }
      else if(OpcodeConstants.DADD.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.DADD();
      }
      else if(OpcodeConstants.DALOAD.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.DALOAD();
      }
      else if(OpcodeConstants.DASTORE.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.DASTORE();
      }
      else if(OpcodeConstants.DCMPG.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.DCMPG();
      }
      else if(OpcodeConstants.DCMPL.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.DCMPL();
      }
      // DCONST 0|1
      else if(OpcodeConstants.DCONST.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss the value (0.0 or 1.0)");
         }

         try
         {
            double real = Double.parseDouble(this.parameter1);

            if(UtilMath.isNul(real) == true)
            {
               real = 0.0;
            }
            else if(UtilMath.equals(real, 1.0) == true)
            {
               real = 1.0;
            }

            instruction = new com.sun.org.apache.bcel.internal.generic.DCONST(real);
         }
         catch(final Exception exception)
         {
            throw new CompilerException(this.lineNumber, "Invalid value (Must be 0 or 1) not : " + this.parameter1);
         }
      }
      else if(OpcodeConstants.DDIV.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.DDIV();
      }
      // DLOAD this|<methodParameter>|<localVariable>
      else if(OpcodeConstants.DLOAD.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss the local variable to load !");
         }

         instruction = new com.sun.org.apache.bcel.internal.generic.DLOAD(compilerContext.getLocalReference(this.parameter1, this.lineNumber));
      }
      else if(OpcodeConstants.DMUL.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.DMUL();
      }
      else if(OpcodeConstants.DNEG.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.DMUL();
      }
      else if(OpcodeConstants.DREM.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.DREM();
      }
      else if(OpcodeConstants.DRETURN.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.DRETURN();
      }
      // DSTORE <methodParameter>|<localVariable>
      else if(OpcodeConstants.DSTORE.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss parameter name !");
         }

         if("this".equals(this.parameter1) == true)
         {
            throw new CompilerException(this.lineNumber, "Can't store in 'this' !");
         }

         instruction = new com.sun.org.apache.bcel.internal.generic.DSTORE(compilerContext.getLocalReference(this.parameter1, this.lineNumber));
      }
      else if(OpcodeConstants.DSUB.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.DSUB();
      }
      else if(OpcodeConstants.DUP_X1.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.DUP_X1();
      }
      else if(OpcodeConstants.DUP_X2.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.DUP_X2();
      }
      else if(OpcodeConstants.DUP.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.DUP();
      }
      else if(OpcodeConstants.DUP2_X1.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.DUP2_X1();
      }
      else if(OpcodeConstants.DUP2_X2.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.DUP2_X2();
      }
      else if(OpcodeConstants.DUP2.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.DUP2();
      }
      else if(OpcodeConstants.F2D.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.F2D();
      }
      else if(OpcodeConstants.F2I.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.F2I();
      }
      else if(OpcodeConstants.F2L.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.F2L();
      }
      else if(OpcodeConstants.FADD.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.FADD();
      }
      else if(OpcodeConstants.FALOAD.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.FALOAD();
      }
      else if(OpcodeConstants.FASTORE.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.FASTORE();
      }
      else if(OpcodeConstants.FCMPG.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.FCMPG();
      }
      else if(OpcodeConstants.FCMPL.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.FCMPL();
      }
      // FCONST 0|1|2
      else if(OpcodeConstants.FCONST.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss the value (0.0, 1.0 or 2.0)");
         }

         try
         {
            float real = Float.parseFloat(this.parameter1);

            if(UtilMath.isNul(real) == true)
            {
               real = 0.0f;
            }
            else if(UtilMath.equals(real, 1f) == true)
            {
               real = 1.0f;
            }
            else if(UtilMath.equals(real, 2f) == true)
            {
               real = 2.0f;
            }

            instruction = new com.sun.org.apache.bcel.internal.generic.FCONST(real);
         }
         catch(final Exception exception)
         {
            throw new CompilerException(this.lineNumber, "Invalid value (Must be 0, 1 or 2) not : " + this.parameter1);
         }
      }
      else if(OpcodeConstants.FDIV.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.FDIV();
      }
      // FLOAD this|<methodParameter>|<localVariable>
      else if(OpcodeConstants.FLOAD.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss parameter name !");
         }

         instruction = new com.sun.org.apache.bcel.internal.generic.FLOAD(compilerContext.getLocalReference(this.parameter1, this.lineNumber));
      }
      else if(OpcodeConstants.FMUL.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.FMUL();
      }
      else if(OpcodeConstants.FNEG.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.FNEG();
      }
      else if(OpcodeConstants.FREM.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.FREM();
      }
      else if(OpcodeConstants.FRETURN.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.FRETURN();
      }
      // FSTORE <methodParameter>|<localVariable>
      else if(OpcodeConstants.FSTORE.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss parameter name !");
         }

         if("this".equals(this.parameter1) == true)
         {
            throw new CompilerException(this.lineNumber, "Can't store in 'this' !");
         }

         instruction = new com.sun.org.apache.bcel.internal.generic.FSTORE(compilerContext.getLocalReference(this.parameter1, this.lineNumber));
      }
      else if(OpcodeConstants.FSUB.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.FSUB();
      }
      // GETFIELD <fieldName>
      else if(OpcodeConstants.GETFIELD.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss the field name");
         }

         instruction = new com.sun.org.apache.bcel.internal.generic.GETFIELD(compilerContext.getField(this.parameter1, this.lineNumber).getReference());
      }
      // GETSTATIC <fieldName>
      else if(OpcodeConstants.GETSTATIC.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss the field name");
         }

         instruction = new com.sun.org.apache.bcel.internal.generic.GETSTATIC(compilerContext.getField(this.parameter1, this.lineNumber).getReference());
      }
      // GOTO_W <label>
      else if(OpcodeConstants.GOTO_W.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss label destination !");
         }

         branchTarget = this.parameter1;
         instruction = new com.sun.org.apache.bcel.internal.generic.GOTO_W(null);
      }
      // GOTO <label>
      else if(OpcodeConstants.GOTO.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss label destination !");
         }

         branchTarget = this.parameter1;
         instruction = new com.sun.org.apache.bcel.internal.generic.GOTO(null);
      }
      else if(OpcodeConstants.I2B.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.I2B();
      }
      else if(OpcodeConstants.I2B.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.I2B();
      }
      else if(OpcodeConstants.I2C.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.I2C();
      }
      else if(OpcodeConstants.I2D.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.I2D();
      }
      else if(OpcodeConstants.I2F.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.I2F();
      }
      else if(OpcodeConstants.I2L.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.I2L();
      }
      else if(OpcodeConstants.I2S.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.I2S();
      }
      else if(OpcodeConstants.IADD.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.IADD();
      }
      else if(OpcodeConstants.IALOAD.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.IALOAD();
      }
      else if(OpcodeConstants.IAND.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.IAND();
      }
      else if(OpcodeConstants.IASTORE.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.IASTORE();
      }
      // ICONST -1|0|1|2|3|4|5
      else if(OpcodeConstants.ICONST.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss constant value in {-1,0,1,2,3,4,5} !");
         }

         try
         {
            instruction = new com.sun.org.apache.bcel.internal.generic.ICONST(Integer.parseInt(this.parameter1));
         }
         catch(final Exception exception)
         {
            throw new CompilerException(this.lineNumber, "The value must be in {-1,0,1,2,3,4,5} not " + this.parameter1, exception);
         }
      }
      else if(OpcodeConstants.IDIV.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.IDIV();
      }
      // IF_ACMPEQ <label>
      else if(OpcodeConstants.IF_ACMPEQ.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss label destination !");
         }

         branchTarget = this.parameter1;
         instruction = new com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ(null);
      }
      // IF_ACMPNE <label>
      else if(OpcodeConstants.IF_ACMPNE.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss label destination !");
         }

         branchTarget = this.parameter1;
         instruction = new com.sun.org.apache.bcel.internal.generic.IF_ACMPNE(null);
      }
      // IF_ICMPEQ <label>
      else if(OpcodeConstants.IF_ICMPEQ.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss label destination !");
         }

         branchTarget = this.parameter1;
         instruction = new com.sun.org.apache.bcel.internal.generic.IF_ICMPEQ(null);
      }
      // IF_ICMPGE <label>
      else if(OpcodeConstants.IF_ICMPGE.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss label destination !");
         }

         branchTarget = this.parameter1;
         instruction = new com.sun.org.apache.bcel.internal.generic.IF_ICMPGE(null);
      }
      // IF_ICMPGT <label>
      else if(OpcodeConstants.IF_ICMPGT.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss label destination !");
         }

         branchTarget = this.parameter1;
         instruction = new com.sun.org.apache.bcel.internal.generic.IF_ICMPGT(null);
      }
      // IF_ICMPLE <label>
      else if(OpcodeConstants.IF_ICMPLE.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss label destination !");
         }

         branchTarget = this.parameter1;
         instruction = new com.sun.org.apache.bcel.internal.generic.IF_ICMPLE(null);
      }
      // IF_ICMPLT <label>
      else if(OpcodeConstants.IF_ICMPLT.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss label destination !");
         }

         branchTarget = this.parameter1;
         instruction = new com.sun.org.apache.bcel.internal.generic.IF_ICMPLT(null);
      }
      // IF_ICMPNE <label>
      else if(OpcodeConstants.IF_ICMPNE.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss label destination !");
         }

         branchTarget = this.parameter1;
         instruction = new com.sun.org.apache.bcel.internal.generic.IF_ICMPNE(null);
      }
      // IFEQ <label>
      else if(OpcodeConstants.IFEQ.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss label destination !");
         }

         branchTarget = this.parameter1;
         instruction = new com.sun.org.apache.bcel.internal.generic.IFEQ(null);
      }
      // IFGE <label>
      else if(OpcodeConstants.IFGE.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss label destination !");
         }

         branchTarget = this.parameter1;
         instruction = new com.sun.org.apache.bcel.internal.generic.IFGE(null);
      }
      // IFGT <label>
      else if(OpcodeConstants.IFGT.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss label destination !");
         }

         branchTarget = this.parameter1;
         instruction = new com.sun.org.apache.bcel.internal.generic.IFGT(null);
      }
      // IFLE <label>
      else if(OpcodeConstants.IFLE.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss label destination !");
         }

         branchTarget = this.parameter1;
         instruction = new com.sun.org.apache.bcel.internal.generic.IFLE(null);
      }
      // IFLT <label>
      else if(OpcodeConstants.IFLT.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss label destination !");
         }

         branchTarget = this.parameter1;
         instruction = new com.sun.org.apache.bcel.internal.generic.IFLT(null);
      }
      // IFNE <label>
      else if(OpcodeConstants.IFNE.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss label destination !");
         }

         branchTarget = this.parameter1;
         instruction = new com.sun.org.apache.bcel.internal.generic.IFNE(null);
      }
      // IFNONNULL <label>
      else if(OpcodeConstants.IFNONNULL.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss label destination !");
         }

         branchTarget = this.parameter1;
         instruction = new com.sun.org.apache.bcel.internal.generic.IFNONNULL(null);
      }
      // IFNULL <label>
      else if(OpcodeConstants.IFNULL.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss label destination !");
         }

         branchTarget = this.parameter1;
         instruction = new com.sun.org.apache.bcel.internal.generic.IFNULL(null);
      }
      // IINC <methodParameter>|<localVariable> <value>
      else if(OpcodeConstants.IINC.equals(this.instruction) == true)
      {
         if((this.parameter1 == null) || (this.parameter2 == null))
         {
            throw new CompilerException(this.lineNumber, branchTarget);
         }

         try
         {
            instruction = new com.sun.org.apache.bcel.internal.generic.IINC(compilerContext.getLocalReference(this.parameter1, this.lineNumber),
                  Integer.parseInt(this.parameter2));
         }
         catch(final Exception exception)
         {
            throw new CompilerException(this.lineNumber, "Invalid parameters !");
         }
      }
      // ILOAD this|<methodParameter>|<localVariable>
      else if(OpcodeConstants.ILOAD.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss parameter name !");
         }

         instruction = new com.sun.org.apache.bcel.internal.generic.ILOAD(compilerContext.getLocalReference(this.parameter1, this.lineNumber));
      }
      else if(OpcodeConstants.IMPDEP1.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.IMPDEP1();
      }
      else if(OpcodeConstants.IMPDEP2.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.IMPDEP2();
      }
      else if(OpcodeConstants.IMUL.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.IMUL();
      }
      else if(OpcodeConstants.INEG.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.INEG();
      }
      // INSTANCEOF <ClassName>
      else if(OpcodeConstants.INSTANCEOF.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss the type to test !");
         }

         instruction = new com.sun.org.apache.bcel.internal.generic.INSTANCEOF(compilerContext.addClassReference(this.parameter1, this.lineNumber));
      }
      // INVOKEINTERFACE <methodCompleteDescription> <numberOfArguments>
      else if(OpcodeConstants.INVOKEINTERFACE.equals(this.instruction) == true)
      {
         if((this.parameter1 == null) || (this.parameter2 == null))
         {
            throw new CompilerException(this.lineNumber, "Miss parameters !");
         }

         try
         {
            instruction = new com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE(compilerContext.addMethodReference(this.parameter1, this.lineNumber),
                  Integer.parseInt(this.parameter2));
         }
         catch(final Exception exception)
         {
            throw new CompilerException(this.lineNumber, "Invalid parameters !", exception);
         }
      }
      // INVOKESPECIAL <methodCompleteDescription>
      else if(OpcodeConstants.INVOKESPECIAL.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss method description !");
         }

         instruction = new com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL(compilerContext.addMethodReference(this.parameter1, this.lineNumber));
      }
      // INVOKESTATIC <methodCompleteDescription>
      else if(OpcodeConstants.INVOKESTATIC.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss method description !");
         }

         instruction = new com.sun.org.apache.bcel.internal.generic.INVOKESTATIC(compilerContext.addMethodReference(this.parameter1, this.lineNumber));
      }
      // INVOKEVIRTUAL <methodCompleteDescription>
      else if(OpcodeConstants.INVOKEVIRTUAL.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss method description !");
         }

         instruction = new com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL(compilerContext.addMethodReference(this.parameter1, this.lineNumber));
      }
      else if(OpcodeConstants.IOR.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.IOR();
      }
      else if(OpcodeConstants.IREM.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.IREM();
      }
      else if(OpcodeConstants.IRETURN.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.IRETURN();
      }
      else if(OpcodeConstants.ISHL.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.ISHL();
      }
      else if(OpcodeConstants.ISHR.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.ISHR();
      }
      // ISTORE <methodParameter>|<localVariable>
      else if(OpcodeConstants.ISTORE.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss parameter !");
         }

         if("this".equals(this.parameter1) == true)
         {
            throw new CompilerException(this.lineNumber, "Can't store in 'this' !");
         }

         instruction = new com.sun.org.apache.bcel.internal.generic.ISTORE(compilerContext.getLocalReference(this.parameter1, this.lineNumber));
      }
      else if(OpcodeConstants.ISUB.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.ISUB();
      }
      else if(OpcodeConstants.IUSHR.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.IUSHR();
      }
      else if(OpcodeConstants.IXOR.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.IXOR();
      }
      // JSR_W <label>
      else if(OpcodeConstants.JSR_W.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss the label to go !");
         }

         branchTarget = this.parameter1;
         instruction = new com.sun.org.apache.bcel.internal.generic.JSR_W(null);
      }
      // JSR <label>
      else if(OpcodeConstants.JSR.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss the label to go !");
         }

         branchTarget = this.parameter1;
         instruction = new com.sun.org.apache.bcel.internal.generic.JSR(null);
      }
      else if(OpcodeConstants.L2D.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.L2D();
      }
      else if(OpcodeConstants.L2F.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.L2F();
      }
      else if(OpcodeConstants.L2I.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.L2I();
      }
      else if(OpcodeConstants.LADD.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.LADD();
      }
      else if(OpcodeConstants.LALOAD.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.LALOAD();
      }
      else if(OpcodeConstants.LAND.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.LAND();
      }
      else if(OpcodeConstants.LASTORE.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.LASTORE();
      }
      else if(OpcodeConstants.LCMP.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.LCMP();
      }
      // LCONST 0|1
      else if(OpcodeConstants.LCONST.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss the number 0 or 1");
         }

         try
         {
            instruction = new com.sun.org.apache.bcel.internal.generic.LCONST(Long.parseLong(this.parameter1));
         }
         catch(final Exception exception)
         {
            throw new CompilerException(this.lineNumber, "Invalid parameter must be 0 or 1 not " + this.parameter1, exception);
         }
      }
      // LDC_W <constantValue>
      else if(OpcodeConstants.LDC_W.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss the constant value !");
         }

         instruction = new com.sun.org.apache.bcel.internal.generic.LDC_W(compilerContext.addConstant(this.parameter1, this.lineNumber));
      }
      // LDC <constantValue>
      else if(OpcodeConstants.LDC.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss the constant value !");
         }

         instruction = new com.sun.org.apache.bcel.internal.generic.LDC(compilerContext.addConstant(this.parameter1, this.lineNumber));
      }
      //// LDC2_W <constantValue>
      else if(OpcodeConstants.LDC2_W.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss the constant value !");
         }

         instruction = new com.sun.org.apache.bcel.internal.generic.LDC2_W(compilerContext.addConstant(this.parameter1, this.lineNumber));
      }
      else if(OpcodeConstants.LDIV.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.LDIV();
      }
      // LLOAD this|<methodParameter>|<localVariable>
      else if(OpcodeConstants.LLOAD.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss the local reference !");
         }

         instruction = new com.sun.org.apache.bcel.internal.generic.LLOAD(compilerContext.getLocalReference(this.parameter1, this.lineNumber));
      }
      else if(OpcodeConstants.LMUL.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.LMUL();
      }
      else if(OpcodeConstants.LNEG.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.LNEG();
      }
      // LOOKUPSWITCH (<match> <label>)* <label>
      else if(OpcodeConstants.LOOKUPSWITCH.equals(this.instruction) == true)
      {
         instruction = this.parseSelectInformation(compilerContext).createLOOKUPSWITCH();
      }
      else if(OpcodeConstants.LOR.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.LOR();
      }
      else if(OpcodeConstants.LREM.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.LREM();
      }
      else if(OpcodeConstants.LRETURN.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.LRETURN();
      }
      else if(OpcodeConstants.LSHL.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.LSHL();
      }
      else if(OpcodeConstants.LSHR.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.LSHR();
      }
      // LSTORE <methodParameter>|<localVariable>
      else if(OpcodeConstants.LSTORE.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss the local variable name !");
         }

         if("this".equals(this.parameter1) == true)
         {
            throw new CompilerException(this.lineNumber, "Can't store in 'this' !");
         }

         instruction = new com.sun.org.apache.bcel.internal.generic.LSTORE(compilerContext.getLocalReference(this.parameter1, this.lineNumber));
      }
      else if(OpcodeConstants.LSUB.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.LSUB();
      }
      else if(OpcodeConstants.LUSHR.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.LUSHR();
      }
      else if(OpcodeConstants.LXOR.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.LXOR();
      }
      else if(OpcodeConstants.MONITORENTER.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.MONITORENTER();
      }
      else if(OpcodeConstants.MONITOREXIT.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.MONITOREXIT();
      }
      // MULTIANEWARRAY <Type> <numberDimensions>
      else if(OpcodeConstants.MULTIANEWARRAY.equals(this.instruction) == true)
      {
         if((this.parameter1 == null) || (this.parameter2 == null))
         {
            throw new CompilerException(this.lineNumber, "Miss parameters !");
         }

         try
         {
            instruction = new com.sun.org.apache.bcel.internal.generic.MULTIANEWARRAY(compilerContext.addTypeReference(this.parameter1, this.lineNumber),
                  Short.parseShort(this.parameter2));
         }
         catch(final Exception exception)
         {
            throw new CompilerException(this.lineNumber, "Invalid parameters !", exception);
         }
      }
      // NEW <Type>
      else if(OpcodeConstants.NEW.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss the type name !");
         }

         instruction = new com.sun.org.apache.bcel.internal.generic.NEW(compilerContext.addTypeReference(this.parameter1, this.lineNumber));
      }
      // NEWARRAY <primitiveType>
      else if(OpcodeConstants.NEWARRAY.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss the array type !");
         }

         final Type type = compilerContext.stringToType(this.parameter1);

         if(type instanceof BasicType)
         {
            instruction = new com.sun.org.apache.bcel.internal.generic.NEWARRAY(type.getType());
         }
         else
         {
            throw new CompilerException(this.lineNumber, "Invalid type : " + this.parameter1);
         }
      }
      else if(OpcodeConstants.NOP.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.NOP();
      }
      else if(OpcodeConstants.POP.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.POP();
      }
      else if(OpcodeConstants.POP2.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.POP2();
      }
      // PUSH <value>
      else if(OpcodeConstants.PUSH.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss the value to push !");
         }

         instruction = compilerContext.createPush(this.parameter1, this.lineNumber).getInstruction();
      }
      // PUTFIELD <fieldName>
      else if(OpcodeConstants.PUTFIELD.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss the field name !");
         }

         instruction = new com.sun.org.apache.bcel.internal.generic.PUTFIELD(compilerContext.getField(this.parameter1, this.lineNumber).getReference());
      }
      // PUTSTATIC <fieldName>
      else if(OpcodeConstants.PUTSTATIC.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss the field name !");
         }

         instruction = new com.sun.org.apache.bcel.internal.generic.PUTSTATIC(compilerContext.getField(this.parameter1, this.lineNumber).getReference());
      }
      // RET this|<methodParameter>|<localVariable>
      else if(OpcodeConstants.RET.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miss the local variable name !");
         }

         instruction = new com.sun.org.apache.bcel.internal.generic.RET(compilerContext.getLocalReference(this.parameter1, this.lineNumber));
      }
      else if(OpcodeConstants.RETURN.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.RETURN();
      }
      else if(OpcodeConstants.SALOAD.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.SALOAD();
      }
      else if(OpcodeConstants.SASTORE.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.SASTORE();
      }
      // SIPUSH <value>
      else if(OpcodeConstants.SIPUSH.equals(this.instruction) == true)
      {
         if(this.parameter1 == null)
         {
            throw new CompilerException(this.lineNumber, "Miis the value to push !");
         }

         try
         {
            instruction = new com.sun.org.apache.bcel.internal.generic.SIPUSH(Short.parseShort(this.parameter1));
         }
         catch(final Exception exception)
         {
            throw new CompilerException(this.lineNumber, "Wrong short value : " + this.parameter1, exception);
         }
      }
      else if(OpcodeConstants.SWAP.equals(this.instruction) == true)
      {
         instruction = new com.sun.org.apache.bcel.internal.generic.SWAP();
      }
      // SWITCH (<match> <label>)* <label>
      else if(OpcodeConstants.SWITCH.equals(this.instruction) == true)
      {
         instruction = this.parseSelectInformation(compilerContext).createSWITCH();
      }
      // TABLESWITCH (<match> <label>)* <label>
      else if(OpcodeConstants.TABLESWITCH.equals(this.instruction) == true)
      {
         instruction = this.parseSelectInformation(compilerContext).createTABLESWITCH();
      }

      if(instruction == null)
      {
         throw new CompilerException(this.lineNumber, "Unknown instruction : " + this.instruction);
      }

      try
      {
         final InstructionHandle instructionHandle = (instruction instanceof BranchInstruction)
               ? instructionList.append((BranchInstruction) instruction)
               : instructionList.append(instruction);
         final String label = compilerContext.consumeLabelToDefine();

         if(label != null)
         {
            compilerContext.addLabel(label, instructionHandle, this.lineNumber);
         }

         if(branchTarget != null)
         {
            compilerContext.addBranch((BranchHandle) instructionHandle, branchTarget, this.lineNumber);
         }

         return instructionHandle;
      }
      catch(final Exception exception)
      {
         throw new CompilerException(this.lineNumber, "Issue on creating handle !", exception);
      }
   }
}