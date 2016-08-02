package jhelp.compiler.compil;

import java.util.ArrayList;
import java.util.List;

import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.Type;

import jhelp.util.list.Pair;
import jhelp.util.text.UtilText;

/**
 * Describe a method
 *
 * @author JHelp <br>
 */
class MethodDescription
      implements CompilerConstants
{
   /**
    * Compute label name used by subroutine : {@link #Z_SUB_S}, {@link #Z_SUB_E}, {@link #Z_SUB_C}
    *
    * @param subroutineName
    *           Subroutine name
    * @return Label name
    */
   private static String subroutineLabel(final String subroutineName)
   {
      return UtilText.concatenate("jhelpSubroutine_", subroutineName, "_Label");
   }

   /**
    * Compute variable name that store return address used by subroutine : {@link #Z_SUB_S}, {@link #Z_SUB_E}, {@link #Z_SUB_C}
    *
    * @param subroutineName
    *           Subroutine name
    * @return Variable name
    */
   private static String subroutineReturnValue(final String subroutineName)
   {
      return UtilText.concatenate("jhelpSubroutine_", subroutineName, "_ReturnValue");
   }

   /** Method code */
   private final List<CodeLine>  code;
   /** Indicates if inside the code block */
   private boolean               insideCode;
   /** Method name */
   private final String          name;
   /** Method parameters */
   private final List<Parameter> parameters;
   /** Return type */
   private Type                  returnType;

   /**
    * Create a new instance of MethodDescription
    *
    * @param name
    *           Method name
    */
   public MethodDescription(final String name)
   {
      this.name = name;
      this.insideCode = false;
      this.returnType = Type.VOID;
      this.parameters = new ArrayList<Parameter>();
      this.code = new ArrayList<CodeLine>();
   }

   /**
    * Add a parameter
    *
    * @param name
    *           Parameter name
    * @param type
    *           Parameter type
    */
   public void addParameter(final String name, final Type type)
   {
      this.parameters.add(new Parameter(name, type));
   }

   /**
    * Add a code line instruction to current code
    *
    * @param compilerContext
    *           Compiler context
    * @param instruction
    *           Code instruction
    * @param parameters
    *           Instruction parameters
    * @param lineNumber
    *           Code line number where is the instruction
    * @throws CompilerException
    *            On add issue
    */
   public void appendCode(final CompilerContext compilerContext, final String instruction, final List<String> parameters, final int lineNumber)
         throws CompilerException
   {
      if(OpcodeConstants.Z_SUB_S.equals(instruction) == true)
      {
         if(parameters.size() < 1)
         {
            throw new CompilerException(lineNumber, "SUB_S miss subroutine name !");
         }

         final String subroutineName = parameters.get(0);
         parameters.clear();
         parameters.add(MethodDescription.subroutineLabel(subroutineName));
         this.code.add(new CodeLine(OpcodeConstants.Z_LABEL, parameters, lineNumber));
         parameters.clear();
         parameters.add("Object");
         parameters.add(MethodDescription.subroutineReturnValue(subroutineName));
         this.code.add(new CodeLine(OpcodeConstants.Z_VAR, parameters, lineNumber));
         parameters.remove(0);
         this.code.add(new CodeLine(OpcodeConstants.ASTORE, parameters, lineNumber));
         return;
      }

      if(OpcodeConstants.Z_SUB_E.equals(instruction) == true)
      {
         if(parameters.size() < 1)
         {
            throw new CompilerException(lineNumber, "SUB_E miss subroutine name !");
         }

         final String subroutineName = parameters.get(0);
         parameters.set(0, MethodDescription.subroutineReturnValue(subroutineName));
         this.code.add(new CodeLine(OpcodeConstants.RET, parameters, lineNumber));
         return;
      }

      if(OpcodeConstants.Z_SUB_C.equals(instruction) == true)
      {
         if(parameters.size() < 1)
         {
            throw new CompilerException(lineNumber, "SUB_C miss subroutine name !");
         }

         final String subroutineName = parameters.get(0);
         parameters.set(0, MethodDescription.subroutineLabel(subroutineName));
         this.code.add(new CodeLine(OpcodeConstants.JSR, parameters, lineNumber));
         return;
      }

      this.code.add(new CodeLine(instruction, parameters, lineNumber));
   }

   /**
    * Compile the method and add it
    *
    * @param compilerContext
    *           Compiler context
    * @param lineNumber
    *           End block code line number
    * @throws CompilerException
    *            On compilation issue
    */
   public void compile(final CompilerContext compilerContext, final int lineNumber) throws CompilerException
   {
      final int length = this.parameters.size();
      final Type[] parmetersType = new Type[length];
      final String[] parametersName = new String[length];
      Parameter parameter;

      // A method will be add
      compilerContext.initializeForMethod(true, lineNumber);

      // Collect method parameters
      for(int i = 0; i < length; i++)
      {
         parameter = this.parameters.get(i);
         parmetersType[i] = parameter.getType();
         parametersName[i] = parameter.getName();
         compilerContext.addGetLocalReference(parametersName[i], parmetersType[i].toString(), lineNumber);
      }

      // Here start the real local variable (not this nor parameter)
      compilerContext.markStartReference();

      // Initialize label to define
      compilerContext.setLabelToDefine(null, lineNumber);

      // Parse the code
      final InstructionList instructionList = new InstructionList();
      InstructionHandle instructionHandle;
      final List<Pair<InstructionHandle, Integer>> linesTable = new ArrayList<Pair<InstructionHandle, Integer>>();

      for(final CodeLine codeLine : this.code)
      {
         instructionHandle = codeLine.parseCode(instructionList, compilerContext);

         if(instructionHandle != null)
         {
            linesTable.add(new Pair<InstructionHandle, Integer>(instructionHandle, codeLine.getLineNumber()));
         }
      }

      // Create and add the method since now we have all need for it
      compilerContext.createMethod(this.returnType, this.name, parmetersType, parametersName, instructionList, linesTable);
   }

   /**
    * Enter in code block
    */
   public void enterCode()
   {
      this.insideCode = true;
   }

   /**
    * Exit from code block
    */
   public void exitCode()
   {
      this.insideCode = false;
   }

   /**
    * Return type
    *
    * @return Return type
    */
   public Type getReturnType()
   {
      return this.returnType;
   }

   /**
    * Indicates if inside code block
    *
    * @return {@code true} if inside code block
    */
   public boolean insideCode()
   {
      return this.insideCode;
   }

   /**
    * Return type
    *
    * @param retrunType
    *           Return type
    */
   public void setReturnType(final Type retrunType)
   {
      this.returnType = retrunType;
   }
}