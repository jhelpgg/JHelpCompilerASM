package jhelp.compiler.compil;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.TreeSet;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.generic.AALOAD;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ANEWARRAY;
import com.sun.org.apache.bcel.internal.generic.ArrayType;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.GETSTATIC;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.GOTO_W;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.IFGE;
import com.sun.org.apache.bcel.internal.generic.IFGT;
import com.sun.org.apache.bcel.internal.generic.IFLE;
import com.sun.org.apache.bcel.internal.generic.IFLT;
import com.sun.org.apache.bcel.internal.generic.IFNE;
import com.sun.org.apache.bcel.internal.generic.IFNONNULL;
import com.sun.org.apache.bcel.internal.generic.IFNULL;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPNE;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPEQ;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPGE;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPGT;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPLE;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPLT;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPNE;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.JSR;
import com.sun.org.apache.bcel.internal.generic.JSR_W;
import com.sun.org.apache.bcel.internal.generic.LDC;
import com.sun.org.apache.bcel.internal.generic.LDC2_W;
import com.sun.org.apache.bcel.internal.generic.LDC_W;
import com.sun.org.apache.bcel.internal.generic.LOOKUPSWITCH;
import com.sun.org.apache.bcel.internal.generic.MULTIANEWARRAY;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.NEWARRAY;
import com.sun.org.apache.bcel.internal.generic.PUTFIELD;
import com.sun.org.apache.bcel.internal.generic.PUTSTATIC;
import com.sun.org.apache.bcel.internal.generic.TABLESWITCH;
import com.sun.org.apache.bcel.internal.generic.Type;

import jhelp.util.list.Pair;
import jhelp.util.text.UtilText;

/**
 * Inspector to check if stack is respected to signal issues on instruction list at compilation time
 *
 * @author JHelp <br>
 */
class StackInspector
{
   /** List of instructions to inspect */
   private final InstructionList                        instructionList;
   /** Lines table */
   private final List<Pair<InstructionHandle, Integer>> linesTable;
   /** Path follow to reach current instruction */
   private final List<StackInfo>                        path;
   /** Actual stack state */
   private final List<StackElement>                     stack;

   /**
    * Create a new instance of StackInspector
    *
    * @param instructionList
    *           List of instructions to inspect
    * @param linesTable
    *           Lines table
    */
   public StackInspector(final InstructionList instructionList, final List<Pair<InstructionHandle, Integer>> linesTable)
   {
      this.instructionList = instructionList;
      this.linesTable = linesTable;
      this.stack = new ArrayList<StackElement>();
      this.path = new ArrayList<StackInfo>();
   }

   /**
    * Check if stack at current place end with given types
    *
    * @param instructionHandle
    *           Handle of current instruction
    * @param types
    *           Types the stack must end with
    * @throws StackInspectorException
    *            If stack not end with given types
    */
   private void checkTypes(final InstructionHandle instructionHandle, final Type... types) throws StackInspectorException
   {
      final int length = types.length;
      final int size = this.stack.size();

      if(size < length)
      {
         this.throwException(instructionHandle, "Not enough elements in stack need at least :" + length + " and there " + size);
      }

      for(int index = size - length, i = 0; index < size; index++, i++)
      {
         if(this.stack.get(index).compatibleWtih(types[i]) == false)
         {
            this.throwException(instructionHandle,
                  "the argument in stack at " + index + " is type of " + this.stack.get(index).getType() + " but the argument " + i + " need a " + types[i]);
         }
      }
   }

   /**
    * Get handle index inside handle list
    *
    * @param instructionHandle
    *           Handle search
    * @param instructionHandles
    *           List where search
    * @return Handle index OR -1 if not found
    */
   private int indexOf(final InstructionHandle instructionHandle, final InstructionHandle[] instructionHandles)
   {
      for(int index = instructionHandles.length - 1; index >= 0; index--)
      {
         if(instructionHandle == instructionHandles[index])
         {
            return index;
         }
      }

      return -1;
   }

   /**
    * Obtain handle line number in source code
    *
    * @param instructionHandle
    *           Handle to resolve its line number
    * @return Line number of handle OR -1 if resolve failed
    */
   private int obtainLineNumber(final InstructionHandle instructionHandle)
   {
      for(final Pair<InstructionHandle, Integer> pair : this.linesTable)
      {
         if(pair.element1 == instructionHandle)
         {
            return pair.element2;
         }
      }

      return -1;
   }

   /**
    * Pop from the stack a number of elements
    *
    * @param number
    *           Number of elements to pop
    */
   private void pop(final int number)
   {
      final int size = this.stack.size();
      final int limit = size - number;

      for(int index = size - 1; index >= limit; index--)
      {
         this.stack.remove(index);
      }
   }

   /**
    * Push a type on the stack
    *
    * @param type
    *           Type to push
    */
   private void push(final Type type)
   {
      this.stack.add(new StackElement(type));
   }

   /**
    * Format and throw exception
    *
    * @param instructionHandle
    *           Handle where issue happen
    * @param message
    *           Message explain the issue
    * @throws StackInspectorException
    *            Throws created exception
    */
   private void throwException(final InstructionHandle instructionHandle, final String message) throws StackInspectorException
   {
      throw new StackInspectorException(this.obtainLineNumber(instructionHandle), this.stack, this.path, message);
   }

   /**
    * Check if stack respected in embed instruction list
    *
    * @param constantPool
    *           Constant pool that defines constants
    * @throws StackInspectorException
    *            If one instruction in list don't respect the stack
    */
   public void checkStack(final ConstantPoolGen constantPool) throws StackInspectorException
   {
      final InstructionHandle[] instructionHandles = this.instructionList.getInstructionHandles();
      final int length = instructionHandles.length;

      if(length == 0)
      {
         return;
      }

      final TreeSet<Step> already = new TreeSet<Step>();
      final Stack<Step> stackExecution = new Stack<Step>();
      Step step = new Step(0, this.stack, this.path);
      stackExecution.push(step);
      InstructionHandle instructionHandle;
      Instruction instruction;
      Type[] types;
      int size, temp;
      boolean condition;
      StackElement type1, type2, type3, type4;
      StackInfo stackInfo;

      while(stackExecution.isEmpty() == false)
      {
         step = stackExecution.pop();
         step.transferStatus(this.stack, this.path);
         already.add(step);
         instructionHandle = instructionHandles[step.index];
         instruction = instructionHandle.getInstruction();
         stackInfo = new StackInfo(this.obtainLineNumber(instructionHandle), this.stack);
         this.path.add(stackInfo);
         size = this.stack.size();

         switch(instruction.getOpcode())
         {
            // No change
            case Constants.NOP:
            break;
            // ... => ..., null (objectref/arrayref)
            case Constants.ACONST_NULL:
               this.push(Type.NULL);
            break;
            // ... => ..., -1 (int)
            case Constants.ICONST_M1:
               this.push(Type.INT);
            break;
            // ... => ..., 0 (int)
            case Constants.ICONST_0:
               this.push(Type.INT);
            break;
            // ... => ..., 1 (int)
            case Constants.ICONST_1:
               this.push(Type.INT);
            break;
            // ... => ..., 2 (int)
            case Constants.ICONST_2:
               this.push(Type.INT);
            break;
            // ... => ..., 3 (int)
            case Constants.ICONST_3:
               this.push(Type.INT);
            break;
            // ... => ..., 4 (int)
            case Constants.ICONST_4:
               this.push(Type.INT);
            break;
            // ... => ..., 5 (int)
            case Constants.ICONST_5:
               this.push(Type.INT);
            break;
            // ... => ..., 0 (long)
            case Constants.LCONST_0:
               this.push(Type.LONG);
            break;
            // ... => ..., 1 (long)
            case Constants.LCONST_1:
               this.push(Type.LONG);
            break;
            // ... => ..., 0 (float)
            case Constants.FCONST_0:
               this.push(Type.FLOAT);
            break;
            // ... => ..., 1 (float)
            case Constants.FCONST_1:
               this.push(Type.FLOAT);
            break;
            // ... => ..., 2 (float)
            case Constants.FCONST_2:
               this.push(Type.FLOAT);
            break;
            // ... => ..., 0 (double)
            case Constants.DCONST_0:
               this.push(Type.DOUBLE);
            break;
            // ... => ..., 1 (double)
            case Constants.DCONST_1:
               this.push(Type.DOUBLE);
            break;
            // ... => ..., value (int)
            case Constants.BIPUSH:
               this.push(Type.INT);
            break;
            // ... => ..., value (int)
            case Constants.SIPUSH:
               this.push(Type.INT);
            break;
            // ... => ..., value (int/float/objectref)
            case Constants.LDC:
               this.push(((LDC) instruction).getType(constantPool));
            break;
            // ... => ..., value (int/float/objectref)
            case Constants.LDC_W:
               this.push(((LDC_W) instruction).getType(constantPool));
            break;
            // ... => ..., value (long/double)
            case Constants.LDC2_W:
               this.push(((LDC2_W) instruction).getType(constantPool));
            break;
            // ... => ..., value (int)
            case Constants.ILOAD:
               this.push(Type.INT);
            break;
            // ... => ..., value (long)
            case Constants.LLOAD:
               this.push(Type.LONG);
            break;
            // ... => ..., value (float)
            case Constants.FLOAD:
               this.push(Type.FLOAT);
            break;
            // ... => ..., value (double)
            case Constants.DLOAD:
               this.push(Type.DOUBLE);
            break;
            // ... => ..., value (objectref)
            case Constants.ALOAD:
               this.push(((ALOAD) instruction).getType(constantPool));
            break;
            // ... => ..., value (int)
            case Constants.ILOAD_0:
               this.push(Type.INT);
            break;
            // ... => ..., value (int)
            case Constants.ILOAD_1:
               this.push(Type.INT);
            break;
            // ... => ..., value (int)
            case Constants.ILOAD_2:
               this.push(Type.INT);
            break;
            // ... => ..., value (int)
            case Constants.ILOAD_3:
               this.push(Type.INT);
            break;
            // ... => ..., value (long)
            case Constants.LLOAD_0:
               this.push(Type.LONG);
            break;
            // ... => ..., value (long)
            case Constants.LLOAD_1:
               this.push(Type.LONG);
            break;
            // ... => ..., value (long)
            case Constants.LLOAD_2:
               this.push(Type.LONG);
            break;
            // ... => ..., value (long)
            case Constants.LLOAD_3:
               this.push(Type.LONG);
            break;
            // ... => ..., value (float)
            case Constants.FLOAD_0:
               this.push(Type.FLOAT);
            break;
            // ... => ..., value (float)
            case Constants.FLOAD_1:
               this.push(Type.FLOAT);
            break;
            // ... => ..., value (float)
            case Constants.FLOAD_2:
               this.push(Type.FLOAT);
            break;
            // ... => ..., value (float)
            case Constants.FLOAD_3:
               this.push(Type.FLOAT);
            break;
            // ... => ..., value (double)
            case Constants.DLOAD_0:
               this.push(Type.DOUBLE);
            break;
            // ... => ..., value (double)
            case Constants.DLOAD_1:
               this.push(Type.DOUBLE);
            break;
            // ... => ..., value (double)
            case Constants.DLOAD_2:
               this.push(Type.DOUBLE);
            break;
            // ... => ..., value (double)
            case Constants.DLOAD_3:
               this.push(Type.DOUBLE);
            break;
            // ... => ..., value (objectref)
            case Constants.ALOAD_0:
               this.push(((ALOAD) instruction).getType(constantPool));
            break;
            // ... => ..., value (objectref)
            case Constants.ALOAD_1:
               this.push(((ALOAD) instruction).getType(constantPool));
            break;
            // ... => ..., value (objectref)
            case Constants.ALOAD_2:
               this.push(((ALOAD) instruction).getType(constantPool));
            break;
            // ... => ..., value (objectref)
            case Constants.ALOAD_3:
               this.push(((ALOAD) instruction).getType(constantPool));
            break;
            // ..., arrayref, index (int) => ..., value (int)
            case Constants.IALOAD:
               if((size < 2) || //
                     (this.stack.get(size - 2).isArrayRef() == false) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "IALOAD required stack end with 'arrayref' 'int'");
               }

               this.pop(2);
               this.push(Type.INT);
            break;
            // ..., arrayref, index (int) => ..., value (long)
            case Constants.LALOAD:
               if((size < 2) || //
                     (this.stack.get(size - 2).isArrayRef() == false) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "LALOAD required stack end with 'arrayref' 'int'");
               }

               this.pop(2);
               this.push(Type.LONG);
            break;
            // ..., arrayref, index (int) => ..., value (float)
            case Constants.FALOAD:
               if((size < 2) || //
                     (this.stack.get(size - 2).isArrayRef() == false) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "FALOAD required stack end with 'arrayref' 'int'");
               }

               this.pop(2);
               this.push(Type.FLOAT);
            break;
            // ..., arrayref, index (int) => ..., value (double)
            case Constants.DALOAD:
               if((size < 2) || //
                     (this.stack.get(size - 2).isArrayRef() == false) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "DALOAD required stack end with 'arrayref' 'int'");
               }

               this.pop(2);
               this.push(Type.DOUBLE);
            break;
            // ..., arrayref, index (int) => ..., value (objectref)
            case Constants.AALOAD:
               if((size < 2) || //
                     (this.stack.get(size - 2).isArrayRef() == false) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "AALOAD required stack end with 'arrayref' 'int'");
               }

               this.pop(2);
               this.push(((AALOAD) instruction).getType(constantPool));
            break;
            // ..., arrayref, index (int) => ..., value (int)
            case Constants.BALOAD:
               if((size < 2) || //
                     (this.stack.get(size - 2).isArrayRef() == false) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "BALOAD required stack end with 'arrayref' 'int'");
               }

               this.pop(2);
               this.push(Type.INT);
            break;
            // ..., arrayref, index (int) => ..., value (int)
            case Constants.CALOAD:
               if((size < 2) || //
                     (this.stack.get(size - 2).isArrayRef() == false) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "CALOAD required stack end with 'arrayref' 'int'");
               }

               this.pop(2);
               this.push(Type.INT);
            break;
            // ..., arrayref, index (int) => ..., value (int)
            case Constants.SALOAD:
               if((size < 2) || //
                     (this.stack.get(size - 2).isArrayRef() == false) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "SALOAD required stack end with 'arrayref' 'int'");
               }

               this.pop(2);
               this.push(Type.INT);
            break;
            // ..., value(int) => ...
            case Constants.ISTORE:
               if((size < 1) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "ISTORE required stack end with 'int'");
               }

               this.pop(1);
            break;
            // ..., value(long) => ...
            case Constants.LSTORE:
               if((size < 1) || //
                     (this.stack.get(size - 1).isLong() == false))
               {
                  this.throwException(instructionHandle, "LSTORE required stack end with 'long'");
               }

               this.pop(1);
            break;
            // ..., value(float) => ...
            case Constants.FSTORE:
               if((size < 1) || //
                     (this.stack.get(size - 1).isFloat() == false))
               {
                  this.throwException(instructionHandle, "FSTORE required stack end with 'float'");
               }

               this.pop(1);
            break;
            // ..., value(double) => ...
            case Constants.DSTORE:
               if((size < 1) || //
                     (this.stack.get(size - 1).isDouble() == false))
               {
                  this.throwException(instructionHandle, "DSTORE required stack end with 'double'");
               }

               this.pop(1);
            break;
            // ..., value(objectref) => ...
            case Constants.ASTORE:
               if((size < 1) || //
                     (this.stack.get(size - 1).isObjectRef() == false))
               {
                  this.throwException(instructionHandle, "ASTORE required stack end with 'objectref'");
               }

               this.pop(1);
            break;
            // ..., value(int) => ...
            case Constants.ISTORE_0:
               if((size < 1) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "ISTORE required stack end with 'int'");
               }

               this.pop(1);
            break;
            // ..., value(int) => ...
            case Constants.ISTORE_1:
               if((size < 1) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "ISTORE required stack end with 'int'");
               }

               this.pop(1);
            break;
            // ..., value(int) => ...
            case Constants.ISTORE_2:
               if((size < 1) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "ISTORE required stack end with 'int'");
               }

               this.pop(1);
            break;
            // ..., value(int) => ...
            case Constants.ISTORE_3:
               if((size < 1) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "ISTORE required stack end with 'int'");
               }

               this.pop(1);
            break;
            // ..., value(long) => ...
            case Constants.LSTORE_0:
               if((size < 1) || //
                     (this.stack.get(size - 1).isLong() == false))
               {
                  this.throwException(instructionHandle, "LSTORE required stack end with 'long'");
               }

               this.pop(1);
            break;
            // ..., value(long) => ...
            case Constants.LSTORE_1:
               if((size < 1) || //
                     (this.stack.get(size - 1).isLong() == false))
               {
                  this.throwException(instructionHandle, "LSTORE required stack end with 'long'");
               }

               this.pop(1);
            break;
            // ..., value(long) => ...
            case Constants.LSTORE_2:
               if((size < 1) || //
                     (this.stack.get(size - 1).isLong() == false))
               {
                  this.throwException(instructionHandle, "LSTORE required stack end with 'long'");
               }

               this.pop(1);
            break;
            // ..., value(long) => ...
            case Constants.LSTORE_3:
               if((size < 1) || //
                     (this.stack.get(size - 1).isLong() == false))
               {
                  this.throwException(instructionHandle, "LSTORE required stack end with 'long'");
               }

               this.pop(1);
            break;
            // ..., value(float) => ...
            case Constants.FSTORE_0:
               if((size < 1) || //
                     (this.stack.get(size - 1).isFloat() == false))
               {
                  this.throwException(instructionHandle, "FSTORE required stack end with 'float'");
               }

               this.pop(1);
            break;
            // ..., value(float) => ...
            case Constants.FSTORE_1:
               if((size < 1) || //
                     (this.stack.get(size - 1).isFloat() == false))
               {
                  this.throwException(instructionHandle, "FSTORE required stack end with 'float'");
               }

               this.pop(1);
            break;
            // ..., value(float) => ...
            case Constants.FSTORE_2:
               if((size < 1) || //
                     (this.stack.get(size - 1).isFloat() == false))
               {
                  this.throwException(instructionHandle, "FSTORE required stack end with 'float'");
               }

               this.pop(1);
            break;
            // ..., value(float) => ...
            case Constants.FSTORE_3:
               if((size < 1) || //
                     (this.stack.get(size - 1).isFloat() == false))
               {
                  this.throwException(instructionHandle, "FSTORE required stack end with 'float'");
               }

               this.pop(1);
            break;
            // ..., value(double) => ...
            case Constants.DSTORE_0:
               if((size < 1) || //
                     (this.stack.get(size - 1).isDouble() == false))
               {
                  this.throwException(instructionHandle, "DSTORE required stack end with 'double'");
               }

               this.pop(1);
            break;
            // ..., value(double) => ...
            case Constants.DSTORE_1:
               if((size < 1) || //
                     (this.stack.get(size - 1).isDouble() == false))
               {
                  this.throwException(instructionHandle, "DSTORE required stack end with 'double'");
               }

               this.pop(1);
            break;
            // ..., value(double) => ...
            case Constants.DSTORE_2:
               if((size < 1) || //
                     (this.stack.get(size - 1).isDouble() == false))
               {
                  this.throwException(instructionHandle, "DSTORE required stack end with 'double'");
               }

               this.pop(1);
            break;
            // ..., value(double) => ...
            case Constants.DSTORE_3:
               if((size < 1) || //
                     (this.stack.get(size - 1).isDouble() == false))
               {
                  this.throwException(instructionHandle, "DSTORE required stack end with 'double'");
               }

               this.pop(1);
            break;
            // ..., value(objectref) => ...
            case Constants.ASTORE_0:
               if((size < 1) || //
                     (this.stack.get(size - 1).isObjectRef() == false))
               {
                  this.throwException(instructionHandle, "ASTORE required stack end with 'objectref'");
               }

               this.pop(1);
            break;
            // ..., value(objectref) => ...
            case Constants.ASTORE_1:
               if((size < 1) || //
                     (this.stack.get(size - 1).isObjectRef() == false))
               {
                  this.throwException(instructionHandle, "ASTORE required stack end with 'objectref'");
               }

               this.pop(1);
            break;
            // ..., value(objectref) => ...
            case Constants.ASTORE_2:
               if((size < 1) || //
                     (this.stack.get(size - 1).isObjectRef() == false))
               {
                  this.throwException(instructionHandle, "ASTORE required stack end with 'objectref'");
               }

               this.pop(1);
            break;
            // ..., value(objectref) => ...
            case Constants.ASTORE_3:
               if((size < 1) || //
                     (this.stack.get(size - 1).isObjectRef() == false))
               {
                  this.throwException(instructionHandle, "ASTORE required stack end with 'objectref'");
               }

               this.pop(1);
            break;
            // ..., arrayref, index (int), value (int) => ...
            case Constants.IASTORE:
               if((size < 3) || //
                     (this.stack.get(size - 3).isArrayRef() == false) || //
                     (this.stack.get(size - 2).isInt() == false) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "IASTORE need stack end with : arrayref, int, int");
               }

               this.pop(3);
            break;
            // ..., arrayref, index (int), value (long) => ...
            case Constants.LASTORE:
               if((size < 3) || //
                     (this.stack.get(size - 3).isArrayRef() == false) || //
                     (this.stack.get(size - 2).isInt() == false) || //
                     (this.stack.get(size - 1).isLong() == false))
               {
                  this.throwException(instructionHandle, "LASTORE need stack end with : arrayref, int, long");
               }

               this.pop(3);
            break;
            // ..., arrayref, index (int), value (float) => ...
            case Constants.FASTORE:
               if((size < 3) || //
                     (this.stack.get(size - 3).isArrayRef() == false) || //
                     (this.stack.get(size - 2).isInt() == false) || //
                     (this.stack.get(size - 1).isFloat() == false))
               {
                  this.throwException(instructionHandle, "FASTORE need stack end with : arrayref, int, float");
               }

               this.pop(3);
            break;
            // ..., arrayref, index (int), value (double) => ...
            case Constants.DASTORE:
               if((size < 3) || //
                     (this.stack.get(size - 3).isArrayRef() == false) || //
                     (this.stack.get(size - 2).isInt() == false) || //
                     (this.stack.get(size - 1).isDouble() == false))
               {
                  this.throwException(instructionHandle, "DASTORE need stack end with : arrayref, int, double");
               }

               this.pop(3);
            break;
            // ..., arrayref, index (int), value (objectref) => ...
            case Constants.AASTORE:
               if((size < 3) || //
                     (this.stack.get(size - 3).isArrayRef() == false) || //
                     (this.stack.get(size - 2).isInt() == false) || //
                     (this.stack.get(size - 1).isObjectRef() == false))
               {
                  this.throwException(instructionHandle, "AASTORE need stack end with : arrayref, int, objectref");
               }

               this.pop(3);
            break;
            // ..., arrayref, index (int), value (int) => ...
            case Constants.BASTORE:
               if((size < 3) || //
                     (this.stack.get(size - 3).isArrayRef() == false) || //
                     (this.stack.get(size - 2).isInt() == false) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "BASTORE need stack end with : arrayref, int, int");
               }

               this.pop(3);
            break;
            // ..., arrayref, index (int), value (int) => ...
            case Constants.CASTORE:
               if((size < 3) || //
                     (this.stack.get(size - 3).isArrayRef() == false) || //
                     (this.stack.get(size - 2).isInt() == false) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "CASTORE need stack end with : arrayref, int, int");
               }

               this.pop(3);
            break;
            // ..., arrayref, index (int), value (int) => ...
            case Constants.SASTORE:
               if((size < 3) || //
                     (this.stack.get(size - 3).isArrayRef() == false) || //
                     (this.stack.get(size - 2).isInt() == false) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "SASTORE need stack end with : arrayref, int, int");
               }

               this.pop(3);
            break;
            // .., value (not long nor double) => ...
            case Constants.POP:
               if((size < 1) || //
                     (this.stack.get(size - 1).isDoubleOrLong() == true))
               {
                  this.throwException(instructionHandle, "POP need stack end with : notLongNorDouble");
               }

               this.pop(1);
            break;
            // ..., value2 (not long nor double), value1 (not long nor double)=> ...
            // OR
            // ..., value (long or double)=> ...
            case Constants.POP2:
               condition = false;
               temp = 0;

               if(size > 0)
               {
                  if(this.stack.get(size - 1).isDoubleOrLong() == false)
                  {
                     temp = 2;
                     condition = (size > 1) && (this.stack.get(size - 2).isDoubleOrLong() == false);
                  }
                  else
                  {
                     temp = 1;
                     condition = true;
                  }
               }

               if(condition == false)
               {
                  this.throwException(instructionHandle, "POP2 need stack end with : 'notLongNorDouble notLongNorDouble' OR 'longOrDouble'");
               }

               this.pop(temp);
            break;
            // ..., value (not long nor double)=> ..., value, value
            case Constants.DUP:
               if((size < 1) || //
                     (this.stack.get(size - 1).isDoubleOrLong() == true))
               {
                  this.throwException(instructionHandle, "DUP need stack end with : notLongNorDouble");
               }

               this.push(this.stack.get(size - 1).getType());
            break;
            // ..., value2 (not long nor double), value1 (not long nor double) => ..., value1, value2, value1
            case Constants.DUP_X1:
               if((size < 2) || //
                     (this.stack.get(size - 1).isDoubleOrLong() == true) || //
                     (this.stack.get(size - 2).isDoubleOrLong() == true))
               {
                  this.throwException(instructionHandle, "DUP_X1 need stack end with : notLongNorDouble notLongNorDouble");
               }

               type2 = this.stack.get(size - 2);
               type1 = this.stack.get(size - 1);
               this.pop(2);
               this.push(type1.getType());
               this.push(type2.getType());
               this.push(type1.getType());
            break;
            // .., value3 (not long nor double), value2 (not long nor double), value1 (not long nor double) => ..., value1,
            // value3, value2, value1
            // OR
            // .., value2 (long or double), value1 (not long nor double) => ..., value1, value2, value1
            case Constants.DUP_X2:
               type3 = null;
               type2 = null;
               type1 = null;

               if(size > 2)
               {
                  type3 = this.stack.get(size - 3);
               }

               if(size > 1)
               {
                  type2 = this.stack.get(size - 2);
                  type1 = this.stack.get(size - 1);
               }

               if((type1 == null) || //
                     (type1.isDoubleOrLong() == true) || //
                     ((type2.isDoubleOrLong() == false) && ((type3 == null) || (type3.isDoubleOrLong() == true))))
               {
                  this.throwException(instructionHandle,
                        "DUP_X2 need stack end with : 'notLongNorDouble notLongNorDouble notLongNorDouble' OR 'longOrDouble notLongNorDouble'");
               }

               if(type2.isDoubleOrLong() == true)
               {
                  this.pop(2);
                  this.push(type1.getType());
                  this.push(type2.getType());
                  this.push(type1.getType());
               }
               else
               {
                  this.pop(3);
                  this.push(type1.getType());
                  this.push(type3.getType());
                  this.push(type2.getType());
                  this.push(type1.getType());
               }
            break;
            // ..., value2 (not long nor double), value1 (not long nor double) => ..., value2, value1, value2, value1
            // OR
            // ..., value (long or double)=> ...,value, value
            case Constants.DUP2:
               type2 = null;
               type1 = null;

               if(size > 1)
               {
                  type2 = this.stack.get(size - 2);
               }

               if(size > 0)
               {
                  type1 = this.stack.get(size - 1);
               }

               if((type1 == null) || //
                     ((type1.isDoubleOrLong() == false) && ((type2 == null) || (type2.isDoubleOrLong() == true))))
               {
                  this.throwException(instructionHandle, "DUP2 need stack end with : 'notLongNorDouble notLongNorDouble' OR 'longOrDouble'");
               }

               if(type1.isDoubleOrLong() == true)
               {
                  this.push(type1.getType());
               }
               else
               {
                  this.push(type2.getType());
                  this.push(type1.getType());
               }
            break;
            // .., value3 (not long nor double), value2 (not long nor double), value1 (not long nor double) => ..., value2,
            // value1, value3, value2, value1
            // OR
            // ..., value2 (not long nor double), value1 (long or double) => ..., value1, value2, value1
            case Constants.DUP2_X1:
               type3 = null;
               type2 = null;
               type1 = null;

               if(size > 2)
               {
                  type3 = this.stack.get(size - 3);
               }

               if(size > 1)
               {
                  type2 = this.stack.get(size - 2);
                  type1 = this.stack.get(size - 1);
               }

               if((type1 == null) || //
                     (type2.isDoubleOrLong() == true) || //
                     ((type1.isDoubleOrLong() == false) && ((type3 == null) || (type3.isDoubleOrLong() == true))))
               {
                  this.throwException(instructionHandle,
                        "DUP2_X1 need stack end with : 'notLongNorDouble notLongNorDouble notLongNorDouble' OR 'notLongNorDouble longOrDouble'");
               }

               if(type1.isDoubleOrLong() == true)
               {
                  this.pop(2);
                  this.push(type1.getType());
                  this.push(type2.getType());
                  this.push(type1.getType());
               }
               else
               {
                  this.pop(3);
                  this.push(type2.getType());
                  this.push(type1.getType());
                  this.push(type3.getType());
                  this.push(type2.getType());
                  this.push(type1.getType());
               }
            break;
            // ..., value4 (not long nor double), value3 (not long nor double), value2 (not long nor double), value1 (not long
            // nor double) => ..., value2, value1, value4, value3, value2, value1
            // OR
            // ..., value3 (not long nor double), value2 (not long nor double), value1 (long or double) => ..., value1, value3,
            // value2, value1
            // OR
            // ..., value3 (long or double), value2 (not long nor double), value1 (not long nor double) => ..., value2, value1,
            // value3, value2, value1
            // OR
            // ..., value2 (long or double), value1 (long or double) => ..., value1, value2, value1
            case Constants.DUP2_X2:
               type4 = null;
               type3 = null;
               type2 = null;
               type1 = null;

               if(size > 3)
               {
                  type4 = this.stack.get(size - 4);
               }

               if(size > 2)
               {
                  type3 = this.stack.get(size - 3);
               }

               if(size > 1)
               {
                  type2 = this.stack.get(size - 2);
                  type1 = this.stack.get(size - 1);
               }

               if((type1 == null) || //
                     ((type1.isDoubleOrLong() == false) && ((type2.isDoubleOrLong() == true)
                           || ((type3 != null) && (type3.isDoubleOrLong() == false) && ((type4 == null) || (type4.isDoubleOrLong() == true)))))
                     || //
                     ((type1.isDoubleOrLong() == true) && (type2.isDoubleOrLong() == false) && ((type3 == null) || (type3.isDoubleOrLong() == true))))
               {
                  this.throwException(instructionHandle,
                        "DUP2_X2 need stack end with : 'notLongNorDouble notLongNorDouble notLongNorDouble notLongNorDouble' OR 'notLongNorDouble notLongNorDouble longOrDouble' OR 'longOrDouble notLongNorDouble notLongNorDouble' OR 'longOrDouble longOrDouble'");
               }

               if(type1.isDoubleOrLong() == true)
               {
                  if(type2.isDoubleOrLong() == true)
                  {
                     this.pop(2);
                     this.push(type1.getType());
                     this.push(type2.getType());
                     this.push(type1.getType());
                  }
                  else
                  {
                     this.pop(3);
                     this.push(type1.getType());
                     this.push(type3.getType());
                     this.push(type2.getType());
                     this.push(type1.getType());
                  }
               }
               else if(type3.isDoubleOrLong() == true)
               {
                  this.pop(3);
                  this.push(type2.getType());
                  this.push(type1.getType());
                  this.push(type3.getType());
                  this.push(type2.getType());
                  this.push(type1.getType());
               }
               else
               {
                  this.pop(4);
                  this.push(type2.getType());
                  this.push(type1.getType());
                  this.push(type4.getType());
                  this.push(type3.getType());
                  this.push(type2.getType());
                  this.push(type1.getType());
               }
            break;
            // ..., value1 (not long nor double), value2 (not long nor double) => ..., value2, value1
            case Constants.SWAP:
               type2 = null;
               type1 = null;

               if(size > 1)
               {
                  type1 = this.stack.get(size - 2);
                  type2 = this.stack.get(size - 1);
               }

               if((type1 == null) || //
                     (type1.isDoubleOrLong() == true) || (type2.isDoubleOrLong() == true))
               {
                  this.throwException(instructionHandle, "SWAP need stack end with : notLongNorDouble notLongNorDouble");
               }

               this.pop(2);
               this.push(type2.getType());
               this.push(type1.getType());
            break;
            // ..., value1(int), value2(int) => ..., result(int)
            case Constants.IADD:
               if((size < 2) || //
                     (this.stack.get(size - 2).isInt() == false) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "IADD need stack end with : int int");
               }

               this.pop(1);
            break;
            // ..., value1(long), value2(long) => ..., result(long)
            case Constants.LADD:
               if((size < 2) || //
                     (this.stack.get(size - 2).isLong() == false) || //
                     (this.stack.get(size - 1).isLong() == false))
               {
                  this.throwException(instructionHandle, "LADD need stack end with : long long");
               }

               this.pop(1);
            break;
            // ..., value1(float), value2(float) => ..., result(float)
            case Constants.FADD:
               if((size < 2) || //
                     (this.stack.get(size - 2).isFloat() == false) || //
                     (this.stack.get(size - 1).isFloat() == false))
               {
                  this.throwException(instructionHandle, "FADD need stack end with : float float");
               }

               this.pop(1);
            break;
            // ..., value1(double), value2(double) => ..., result(double)
            case Constants.DADD:
               if((size < 2) || //
                     (this.stack.get(size - 2).isDouble() == false) || //
                     (this.stack.get(size - 1).isDouble() == false))
               {
                  this.throwException(instructionHandle, "DADD need stack end with : double double");
               }

               this.pop(1);
            break;
            // ..., value1(int), value2(int) => ..., result(int)
            case Constants.ISUB:
               if((size < 2) || //
                     (this.stack.get(size - 2).isInt() == false) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "ISUB need stack end with : int int");
               }

               this.pop(1);
            break;
            // ..., value1(long), value2(long) => ..., result(long)
            case Constants.LSUB:
               if((size < 2) || //
                     (this.stack.get(size - 2).isLong() == false) || //
                     (this.stack.get(size - 1).isLong() == false))
               {
                  this.throwException(instructionHandle, "LSUB need stack end with : long long");
               }

               this.pop(1);
            break;
            // ..., value1(float), value2(float) => ..., result(float)
            case Constants.FSUB:
               if((size < 2) || //
                     (this.stack.get(size - 2).isFloat() == false) || //
                     (this.stack.get(size - 1).isFloat() == false))
               {
                  this.throwException(instructionHandle, "FSUB need stack end with : float float");
               }

               this.pop(1);
            break;
            // ..., value1(double), value2(double) => ..., result(double)
            case Constants.DSUB:
               if((size < 2) || //
                     (this.stack.get(size - 2).isDouble() == false) || //
                     (this.stack.get(size - 1).isDouble() == false))
               {
                  this.throwException(instructionHandle, "DSUB need stack end with : double double");
               }

               this.pop(1);
            break;
            // ..., value1(int), value2(int) => ..., result(int)
            case Constants.IMUL:
               if((size < 2) || //
                     (this.stack.get(size - 2).isInt() == false) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "IMUL need stack end with : int int");
               }

               this.pop(1);
            break;
            // ..., value1(long), value2(long) => ..., result(long)
            case Constants.LMUL:
               if((size < 2) || //
                     (this.stack.get(size - 2).isLong() == false) || //
                     (this.stack.get(size - 1).isLong() == false))
               {
                  this.throwException(instructionHandle, "LMUL need stack end with : long long");
               }

               this.pop(1);
            break;
            // ..., value1(float), value2(float) => ..., result(float)
            case Constants.FMUL:
               if((size < 2) || //
                     (this.stack.get(size - 2).isFloat() == false) || //
                     (this.stack.get(size - 1).isFloat() == false))
               {
                  this.throwException(instructionHandle, "FMUL need stack end with : float float");
               }

               this.pop(1);
            break;
            // ..., value1(double), value2(double) => ..., result(double)
            case Constants.DMUL:
               if((size < 2) || //
                     (this.stack.get(size - 2).isDouble() == false) || //
                     (this.stack.get(size - 1).isDouble() == false))
               {
                  this.throwException(instructionHandle, "DMUL need stack end with : double double");
               }

               this.pop(1);
            break;
            // ..., value1(int), value2(int) => ..., result(int)
            case Constants.IDIV:
               if((size < 2) || //
                     (this.stack.get(size - 2).isInt() == false) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "IDIV need stack end with : int int");
               }

               this.pop(1);
            break;
            // ..., value1(long), value2(long) => ..., result(long)
            case Constants.LDIV:
               if((size < 2) || //
                     (this.stack.get(size - 2).isLong() == false) || //
                     (this.stack.get(size - 1).isLong() == false))
               {
                  this.throwException(instructionHandle, "LDIV need stack end with : long long");
               }

               this.pop(1);
            break;
            // ..., value1(float), value2(float) => ..., result(float)
            case Constants.FDIV:
               if((size < 2) || //
                     (this.stack.get(size - 2).isFloat() == false) || //
                     (this.stack.get(size - 1).isFloat() == false))
               {
                  this.throwException(instructionHandle, "FDIV need stack end with : float float");
               }

               this.pop(1);
            break;
            // ..., value1(double), value2(double) => ..., result(double)
            case Constants.DDIV:
               if((size < 2) || //
                     (this.stack.get(size - 2).isDouble() == false) || //
                     (this.stack.get(size - 1).isDouble() == false))
               {
                  this.throwException(instructionHandle, "DDIV need stack end with : double double");
               }

               this.pop(1);
            break;
            // ..., value1(int), value2(int) => ..., result(int)
            case Constants.IREM:
               if((size < 2) || //
                     (this.stack.get(size - 2).isInt() == false) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "IREM need stack end with : int int");
               }

               this.pop(1);
            break;
            // ..., value1(long), value2(long) => ..., result(long)
            case Constants.LREM:
               if((size < 2) || //
                     (this.stack.get(size - 2).isLong() == false) || //
                     (this.stack.get(size - 1).isLong() == false))
               {
                  this.throwException(instructionHandle, "LREM need stack end with : long long");
               }

               this.pop(1);
            break;
            // ..., value1(float), value2(float) => ..., result(float)
            case Constants.FREM:
               if((size < 2) || //
                     (this.stack.get(size - 2).isFloat() == false) || //
                     (this.stack.get(size - 1).isFloat() == false))
               {
                  this.throwException(instructionHandle, "FREM need stack end with : float float");
               }

               this.pop(1);
            break;
            // ..., value1(double), value2(double) => ..., result(double)
            case Constants.DREM:
               if((size < 2) || //
                     (this.stack.get(size - 2).isDouble() == false) || //
                     (this.stack.get(size - 1).isDouble() == false))
               {
                  this.throwException(instructionHandle, "DREM need stack end with : double double");
               }

               this.pop(1);
            break;
            // ..., value(int) => ..., result(int)
            case Constants.INEG:
               if((size < 1) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "INEG need stack end with : int");
               }
            break;
            // ..., value(long) => ..., result(long)
            case Constants.LNEG:
               if((size < 1) || //
                     (this.stack.get(size - 1).isLong() == false))
               {
                  this.throwException(instructionHandle, "LNEG need stack end with : long");
               }
            break;
            // ..., value(float) => ..., result(float)
            case Constants.FNEG:
               if((size < 1) || //
                     (this.stack.get(size - 1).isFloat() == false))
               {
                  this.throwException(instructionHandle, "FNEG need stack end with : float");
               }
            break;
            // ..., value(double) => ..., result(double)
            case Constants.DNEG:
               if((size < 1) || //
                     (this.stack.get(size - 1).isDouble() == false))
               {
                  this.throwException(instructionHandle, "DNEG need stack end with : double");
               }
            break;
            // ..., value1(int), value2(int) => ..., result(int)
            case Constants.ISHL:
               if((size < 2) || //
                     (this.stack.get(size - 2).isInt() == false) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "ISHL need stack end with : int int");
               }

               this.pop(1);
            break;
            // ..., value1(long), value2(long) => ..., result(long)
            case Constants.LSHL:
               if((size < 2) || //
                     (this.stack.get(size - 2).isLong() == false) || //
                     (this.stack.get(size - 1).isLong() == false))
               {
                  this.throwException(instructionHandle, "LSHL need stack end with : long long");
               }

               this.pop(1);
            break;
            // ..., value1(int), value2(int) => ..., result(int)
            case Constants.ISHR:
               if((size < 2) || //
                     (this.stack.get(size - 2).isInt() == false) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "ISHR need stack end with : int int");
               }

               this.pop(1);
            break;
            // ..., value1(long), value2(long) => ..., result(long)
            case Constants.LSHR:
               if((size < 2) || //
                     (this.stack.get(size - 2).isLong() == false) || //
                     (this.stack.get(size - 1).isLong() == false))
               {
                  this.throwException(instructionHandle, "LSHR need stack end with : long long");
               }

               this.pop(1);
            break;
            // ..., value1(int), value2(int) => ..., result(int)
            case Constants.IUSHR:
               if((size < 2) || //
                     (this.stack.get(size - 2).isInt() == false) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "IUSHR need stack end with : int int");
               }

               this.pop(1);
            break;
            // ..., value1(long), value2(long) => ..., result(long)
            case Constants.LUSHR:
               if((size < 2) || //
                     (this.stack.get(size - 2).isLong() == false) || //
                     (this.stack.get(size - 1).isLong() == false))
               {
                  this.throwException(instructionHandle, "LUSHR need stack end with : long long");
               }

               this.pop(1);
            break;
            // ..., value1(int), value2(int) => ..., result(int)
            case Constants.IAND:
               if((size < 2) || //
                     (this.stack.get(size - 2).isInt() == false) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "IAND need stack end with : int int");
               }

               this.pop(1);
            break;
            // ..., value1(long), value2(long) => ..., result(long)
            case Constants.LAND:
               if((size < 2) || //
                     (this.stack.get(size - 2).isLong() == false) || //
                     (this.stack.get(size - 1).isLong() == false))
               {
                  this.throwException(instructionHandle, "LAND need stack end with : long long");
               }

               this.pop(1);
            break;
            // ..., value1(int), value2(int) => ..., result(int)
            case Constants.IOR:
               if((size < 2) || //
                     (this.stack.get(size - 2).isInt() == false) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "IOR need stack end with : int int");
               }

               this.pop(1);
            break;
            // ..., value1(long), value2(long) => ..., result(long)
            case Constants.LOR:
               if((size < 2) || //
                     (this.stack.get(size - 2).isLong() == false) || //
                     (this.stack.get(size - 1).isLong() == false))
               {
                  this.throwException(instructionHandle, "LOR need stack end with : long long");
               }

               this.pop(1);
            break;
            // ..., value1(int), value2(int) => ..., result(int)
            case Constants.IXOR:
               if((size < 2) || //
                     (this.stack.get(size - 2).isInt() == false) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "IXOR need stack end with : int int");
               }

               this.pop(1);
            break;
            // ..., value1(long), value2(long) => ..., result(long)
            case Constants.LXOR:
               if((size < 2) || //
                     (this.stack.get(size - 2).isLong() == false) || //
                     (this.stack.get(size - 1).isLong() == false))
               {
                  this.throwException(instructionHandle, "LXOR need stack end with : long long");
               }

               this.pop(1);
            break;
            // No change
            case Constants.IINC:
            break;
            // ..., value(int) => ..., result(long)
            case Constants.I2L:
               if((size < 1) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "I2L need stack end with : int");
               }

               this.pop(1);
               this.push(Type.LONG);
            break;
            // ..., value(int) => ..., result(float)
            case Constants.I2F:
               if((size < 1) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "I2F need stack end with : int");
               }

               this.pop(1);
               this.push(Type.FLOAT);
            break;
            // ..., value(int) => ..., result(double)
            case Constants.I2D:
               if((size < 1) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "I2D need stack end with : int");
               }

               this.pop(1);
               this.push(Type.DOUBLE);
            break;
            // ..., value(long) => ..., result(int)
            case Constants.L2I:
               if((size < 1) || //
                     (this.stack.get(size - 1).isLong() == false))
               {
                  this.throwException(instructionHandle, "L2I need stack end with : long");
               }

               this.pop(1);
               this.push(Type.INT);
            break;
            // ..., value(long) => ..., result(float)
            case Constants.L2F:
               if((size < 1) || //
                     (this.stack.get(size - 1).isLong() == false))
               {
                  this.throwException(instructionHandle, "L2F need stack end with : long");
               }

               this.pop(1);
               this.push(Type.FLOAT);
            break;
            // ..., value(long) => ..., result(double)
            case Constants.L2D:
               if((size < 1) || //
                     (this.stack.get(size - 1).isLong() == false))
               {
                  this.throwException(instructionHandle, "L2D need stack end with : long");
               }

               this.pop(1);
               this.push(Type.DOUBLE);
            break;
            // ..., value(float) => ..., result(int)
            case Constants.F2I:
               if((size < 1) || //
                     (this.stack.get(size - 1).isFloat() == false))
               {
                  this.throwException(instructionHandle, "F2I need stack end with : float");
               }

               this.pop(1);
               this.push(Type.INT);
            break;
            // ..., value(float) => ..., result(long)
            case Constants.F2L:
               if((size < 1) || //
                     (this.stack.get(size - 1).isFloat() == false))
               {
                  this.throwException(instructionHandle, "F2L need stack end with : float");
               }

               this.pop(1);
               this.push(Type.LONG);
            break;
            // ..., value(float) => ..., result(double)
            case Constants.F2D:
               if((size < 1) || //
                     (this.stack.get(size - 1).isFloat() == false))
               {
                  this.throwException(instructionHandle, "F2D need stack end with : float");
               }

               this.pop(1);
               this.push(Type.DOUBLE);
            break;
            // ..., value(double) => ..., result(int)
            case Constants.D2I:
               if((size < 1) || //
                     (this.stack.get(size - 1).isDouble() == false))
               {
                  this.throwException(instructionHandle, "D2I need stack end with : double");
               }

               this.pop(1);
               this.push(Type.INT);
            break;
            // ..., value(double) => ..., result(long)
            case Constants.D2L:
               if((size < 1) || //
                     (this.stack.get(size - 1).isDouble() == false))
               {
                  this.throwException(instructionHandle, "D2L need stack end with : double");
               }

               this.pop(1);
               this.push(Type.LONG);
            break;
            // ..., value(double) => ..., result(float)
            case Constants.D2F:
               if((size < 1) || //
                     (this.stack.get(size - 1).isDouble() == false))
               {
                  this.throwException(instructionHandle, "D2F need stack end with : double");
               }

               this.pop(1);
               this.push(Type.FLOAT);
            break;
            // ..., value(int) => ..., result(int)
            case Constants.I2B:
               if((size < 1) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "I2B need stack end with : int");
               }
            break;
            // ..., value(int) => ..., result(int)
            case Constants.I2C:
               if((size < 1) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "I2C need stack end with : int");
               }
            break;
            // ..., value(int) => ..., result(int)
            case Constants.I2S:
               if((size < 1) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "I2S need stack end with : int");
               }
            break;
            // ..., value1(long), value2(long) => result(int)
            case Constants.LCMP:
               if((size < 2) || //
                     (this.stack.get(size - 2).isLong() == false) || (this.stack.get(size - 1).isLong() == false))
               {
                  this.throwException(instructionHandle, "LCMP need stack end with : long long");
               }

               this.pop(2);
               this.push(Type.INT);
            break;
            // ..., value1(float), value2(float) => result(int)
            case Constants.FCMPL:
               if((size < 2) || //
                     (this.stack.get(size - 2).isFloat() == false) || (this.stack.get(size - 1).isFloat() == false))
               {
                  this.throwException(instructionHandle, "FCMPL need stack end with : float float");
               }

               this.pop(2);
               this.push(Type.INT);
            break;
            // ..., value1(float), value2(float) => result(int)
            case Constants.FCMPG:
               if((size < 2) || //
                     (this.stack.get(size - 2).isFloat() == false) || (this.stack.get(size - 1).isFloat() == false))
               {
                  this.throwException(instructionHandle, "FCMPG need stack end with : float float");
               }

               this.pop(2);
               this.push(Type.INT);
            break;
            // ..., value1(double), value2(double) => result(int)
            case Constants.DCMPL:
               if((size < 2) || //
                     (this.stack.get(size - 2).isDouble() == false) || (this.stack.get(size - 1).isDouble() == false))
               {
                  this.throwException(instructionHandle, "DCMPL need stack end with : double double");
               }

               this.pop(2);
               this.push(Type.INT);
            break;
            // ..., value1(double), value2(double) => result(int)
            case Constants.DCMPG:
               if((size < 2) || //
                     (this.stack.get(size - 2).isDouble() == false) || (this.stack.get(size - 1).isDouble() == false))
               {
                  this.throwException(instructionHandle, "DCMPG need stack end with : double double");
               }

               this.pop(2);
               this.push(Type.INT);
            break;
            // ..., value(int) => ...
            case Constants.IFEQ:
               if((size < 1) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "IFEQ need stack end with : int");
               }

               this.pop(1);

               temp = this.indexOf(((IFEQ) instruction).getTarget(), instructionHandles);

               if(temp < 0)
               {
                  this.throwException(instructionHandle, "Failed to find the IFEQ target : " + ((IFEQ) instruction).getTarget());
               }

               stackExecution.push(new Step(temp, this.stack, this.path));
            break;
            // ..., value(int) => ...
            case Constants.IFNE:
               if((size < 1) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "IFNE need stack end with : int");
               }

               this.pop(1);

               temp = this.indexOf(((IFNE) instruction).getTarget(), instructionHandles);

               if(temp < 0)
               {
                  this.throwException(instructionHandle, "Failed to find the IFNE target : " + ((IFNE) instruction).getTarget());
               }

               stackExecution.push(new Step(temp, this.stack, this.path));
            break;
            // ..., value(int) => ...
            case Constants.IFLT:
               if((size < 1) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "IFLT need stack end with : int");
               }

               this.pop(1);

               temp = this.indexOf(((IFLT) instruction).getTarget(), instructionHandles);

               if(temp < 0)
               {
                  this.throwException(instructionHandle, "Failed to find the IFLT target : " + ((IFLT) instruction).getTarget());
               }

               stackExecution.push(new Step(temp, this.stack, this.path));
            break;
            // ..., value(int) => ...
            case Constants.IFGE:
               if((size < 1) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "IFGE need stack end with : int");
               }

               this.pop(1);

               temp = this.indexOf(((IFGE) instruction).getTarget(), instructionHandles);

               if(temp < 0)
               {
                  this.throwException(instructionHandle, "Failed to find the IFGE target : " + ((IFGE) instruction).getTarget());
               }

               stackExecution.push(new Step(temp, this.stack, this.path));
            break;
            // ..., value(int) => ...
            case Constants.IFGT:
               if((size < 1) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "IFGT need stack end with : int");
               }

               this.pop(1);

               temp = this.indexOf(((IFGT) instruction).getTarget(), instructionHandles);

               if(temp < 0)
               {
                  this.throwException(instructionHandle, "Failed to find the IFGT target : " + ((IFGT) instruction).getTarget());
               }

               stackExecution.push(new Step(temp, this.stack, this.path));
            break;
            // ..., value(int) => ...
            case Constants.IFLE:
               if((size < 1) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "IFLE need stack end with : int");
               }

               this.pop(1);

               temp = this.indexOf(((IFLE) instruction).getTarget(), instructionHandles);

               if(temp < 0)
               {
                  this.throwException(instructionHandle, "Failed to find the IFLE target : " + ((IFLE) instruction).getTarget());
               }

               stackExecution.push(new Step(temp, this.stack, this.path));
            break;
            // ..., value1(int), value2(int) => ...
            case Constants.IF_ICMPEQ:
               if((size < 2) || //
                     (this.stack.get(size - 2).isInt() == false) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "IF_ICMPEQ need stack end with : int int");
               }

               this.pop(2);

               temp = this.indexOf(((IF_ICMPEQ) instruction).getTarget(), instructionHandles);

               if(temp < 0)
               {
                  this.throwException(instructionHandle, "Failed to find the IF_ICMPEQ target : " + ((IF_ICMPEQ) instruction).getTarget());
               }

               stackExecution.push(new Step(temp, this.stack, this.path));
            break;
            // ..., value1(int), value2(int) => ...
            case Constants.IF_ICMPNE:
               if((size < 2) || //
                     (this.stack.get(size - 2).isInt() == false) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "IF_ICMPNE need stack end with : int int");
               }

               this.pop(2);

               temp = this.indexOf(((IF_ICMPNE) instruction).getTarget(), instructionHandles);

               if(temp < 0)
               {
                  this.throwException(instructionHandle, "Failed to find the IF_ICMPNE target : " + ((IF_ICMPNE) instruction).getTarget());
               }

               stackExecution.push(new Step(temp, this.stack, this.path));
            break;
            // ..., value1(int), value2(int) => ...
            case Constants.IF_ICMPLT:
               if((size < 2) || //
                     (this.stack.get(size - 2).isInt() == false) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "IF_ICMPLT need stack end with : int int");
               }

               this.pop(2);

               temp = this.indexOf(((IF_ICMPLT) instruction).getTarget(), instructionHandles);

               if(temp < 0)
               {
                  this.throwException(instructionHandle, "Failed to find the IF_ICMPLT target : " + ((IF_ICMPLT) instruction).getTarget());
               }

               stackExecution.push(new Step(temp, this.stack, this.path));
            break;
            // ..., value1(int), value2(int) => ...
            case Constants.IF_ICMPGE:
               if((size < 2) || //
                     (this.stack.get(size - 2).isInt() == false) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "IF_ICMPGE need stack end with : int int");
               }

               this.pop(2);

               temp = this.indexOf(((IF_ICMPGE) instruction).getTarget(), instructionHandles);

               if(temp < 0)
               {
                  this.throwException(instructionHandle, "Failed to find the IF_ICMPGE target : " + ((IF_ICMPGE) instruction).getTarget());
               }

               stackExecution.push(new Step(temp, this.stack, this.path));
            break;
            // ..., value1(int), value2(int) => ...
            case Constants.IF_ICMPGT:
               if((size < 2) || //
                     (this.stack.get(size - 2).isInt() == false) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "IF_ICMPGT need stack end with : int int");
               }

               this.pop(2);

               temp = this.indexOf(((IF_ICMPGT) instruction).getTarget(), instructionHandles);

               if(temp < 0)
               {
                  this.throwException(instructionHandle, "Failed to find the IF_ICMPGT target : " + ((IF_ICMPGT) instruction).getTarget());
               }

               stackExecution.push(new Step(temp, this.stack, this.path));
            break;
            // ..., value1(int), value2(int) => ...
            case Constants.IF_ICMPLE:
               if((size < 2) || //
                     (this.stack.get(size - 2).isInt() == false) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "IF_ICMPLE need stack end with : int int");
               }

               this.pop(2);

               temp = this.indexOf(((IF_ICMPLE) instruction).getTarget(), instructionHandles);

               if(temp < 0)
               {
                  this.throwException(instructionHandle, "Failed to find the IF_ICMPLE target : " + ((IF_ICMPLE) instruction).getTarget());
               }

               stackExecution.push(new Step(temp, this.stack, this.path));
            break;
            // ..., value1(objectref), value2(objectref) => ...
            case Constants.IF_ACMPEQ:
               if((size < 2) || //
                     (this.stack.get(size - 2).isObjectRef() == false) || //
                     (this.stack.get(size - 1).isObjectRef() == false))
               {
                  this.throwException(instructionHandle, "IF_ACMPEQ need stack end with : objectref objectref");
               }

               this.pop(2);

               temp = this.indexOf(((IF_ACMPEQ) instruction).getTarget(), instructionHandles);

               if(temp < 0)
               {
                  this.throwException(instructionHandle, "Failed to find the IF_ACMPEQ target : " + ((IF_ACMPEQ) instruction).getTarget());
               }

               stackExecution.push(new Step(temp, this.stack, this.path));
            break;
            // ..., value1(objectref), value2(objectref) => ...
            case Constants.IF_ACMPNE:
               if((size < 2) || //
                     (this.stack.get(size - 2).isObjectRef() == false) || //
                     (this.stack.get(size - 1).isObjectRef() == false))
               {
                  this.throwException(instructionHandle, "IF_ACMPNE need stack end with : objectref objectref");
               }

               this.pop(2);

               temp = this.indexOf(((IF_ACMPNE) instruction).getTarget(), instructionHandles);

               if(temp < 0)
               {
                  this.throwException(instructionHandle, "Failed to find the IF_ACMPNE target : " + ((IF_ACMPNE) instruction).getTarget());
               }

               stackExecution.push(new Step(temp, this.stack, this.path));
            break;
            // No change
            case Constants.GOTO:
               temp = this.indexOf(((GOTO) instruction).getTarget(), instructionHandles);

               if(temp < 0)
               {
                  this.throwException(instructionHandle, "Failed to find the GOTO target : " + ((GOTO) instruction).getTarget());
               }

               stackExecution.push(new Step(temp, this.stack, this.path));
               stackInfo.appendEnd(this.stack);
               continue;
            // ... => ..., address
            case Constants.JSR:
               temp = this.indexOf(((JSR) instruction).getTarget(), instructionHandles);

               if(temp < 0)
               {
                  this.throwException(instructionHandle, "Failed to find the JSR target : " + ((JSR) instruction).getTarget());
               }

               this.push(Type.OBJECT);
               stackExecution.push(new Step(temp, this.stack, this.path));
               this.pop(1);
            break;
            // No change
            case Constants.RET:
               stackInfo.appendEnd(this.stack);
               continue;
            // ..., key(int) => ...
            case Constants.TABLESWITCH:
               if((size < 1) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "TABLESWITCH need stack end with : int");
               }

               this.pop(1);

               temp = this.indexOf(((TABLESWITCH) instruction).getTarget(), instructionHandles);

               if(temp < 0)
               {
                  this.throwException(instructionHandle, "Failed to find the TABLESWITCH target : " + ((TABLESWITCH) instruction).getTarget());
               }

               stackExecution.push(new Step(temp, this.stack, this.path));

               for(final InstructionHandle destination : ((TABLESWITCH) instruction).getTargets())
               {
                  temp = this.indexOf(destination, instructionHandles);

                  if(temp < 0)
                  {
                     this.throwException(instructionHandle, "Failed to find the TABLESWITCH target : " + destination);
                  }

                  stackExecution.push(new Step(temp, this.stack, this.path));
               }
            break;
            // ..., key(int) => ...
            case Constants.LOOKUPSWITCH:
               if((size < 1) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "LOOKUPSWITCH need stack end with : int");
               }

               this.pop(1);

               temp = this.indexOf(((LOOKUPSWITCH) instruction).getTarget(), instructionHandles);

               if(temp < 0)
               {
                  this.throwException(instructionHandle, "Failed to find the LOOKUPSWITCH target : " + ((LOOKUPSWITCH) instruction).getTarget());
               }

               stackExecution.push(new Step(temp, this.stack, this.path));

               for(final InstructionHandle destination : ((LOOKUPSWITCH) instruction).getTargets())
               {
                  temp = this.indexOf(destination, instructionHandles);

                  if(temp < 0)
                  {
                     this.throwException(instructionHandle, "Failed to find the LOOKUPSWITCH target : " + destination);
                  }

                  stackExecution.push(new Step(temp, this.stack, this.path));
               }
            break;
            // ..., value(int) => [empty]
            case Constants.IRETURN:
               if((size < 1) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "IRETURN need stack end with : int");
               }

               stackInfo.appendEnd(this.stack);
               continue;
            // ..., value(long) => [empty]
            case Constants.LRETURN:
               if((size < 1) || //
                     (this.stack.get(size - 1).isLong() == false))
               {
                  this.throwException(instructionHandle, "LRETURN need stack end with : long");
               }

               stackInfo.appendEnd(this.stack);
               continue;
            // ..., value(float) => [empty]
            case Constants.FRETURN:
               if((size < 1) || //
                     (this.stack.get(size - 1).isFloat() == false))
               {
                  this.throwException(instructionHandle, "FRETURN need stack end with : float");
               }

               stackInfo.appendEnd(this.stack);
               continue;
            // ..., value(double) => [empty]
            case Constants.DRETURN:
               if((size < 1) || //
                     (this.stack.get(size - 1).isDouble() == false))
               {
                  this.throwException(instructionHandle, "DRETURN need stack end with : double");
               }

               stackInfo.appendEnd(this.stack);
               continue;
            // ..., value(objectref) => [empty]
            case Constants.ARETURN:
               if((size < 1) || //
                     (this.stack.get(size - 1).isObjectRef() == false))
               {
                  this.throwException(instructionHandle, "ARETURN need stack end with : objectref");
               }

               stackInfo.appendEnd(this.stack);
               continue;
            // ... => [empty]
            case Constants.RETURN:
               stackInfo.appendEnd(this.stack);
               continue;
            // ... => ..., value(?)
            case Constants.GETSTATIC:
               this.push(((GETSTATIC) instruction).getType(constantPool));
            break;
            // ..., value(?) => ...
            case Constants.PUTSTATIC:
               if((size < 1) || //
                     (this.stack.get(size - 1).compatibleWtih(((PUTSTATIC) instruction).getType(constantPool)) == false))
               {
                  this.throwException(instructionHandle, "PUTSTATIC need stack end with : " + ((PUTSTATIC) instruction).getType(constantPool));
               }

               this.pop(1);
            break;
            // ..., objectref => ..., value(?)
            case Constants.GETFIELD:
               if((size < 1) || //
                     (this.stack.get(size - 1).isObjectRef() == false))
               {
                  this.throwException(instructionHandle, "GETFIELD need stack end with : objectref");
               }

               this.pop(1);
               this.push(((GETFIELD) instruction).getType(constantPool));
            break;
            // ..., objectref, value(?) => ...
            case Constants.PUTFIELD:
               if((size < 2) || //
                     (this.stack.get(size - 2).isObjectRef() == false) || //
                     (this.stack.get(size - 1).compatibleWtih(((PUTFIELD) instruction).getType(constantPool)) == false))
               {
                  this.throwException(instructionHandle, "PUTFIELD need stack end with : objectref " + ((PUTFIELD) instruction).getType(constantPool));
               }

               this.pop(2);
            break;
            // ..., objectref, [arg1(?), [arg2(?) ...]] => ...
            case Constants.INVOKEVIRTUAL:
               types = ((INVOKEVIRTUAL) instruction).getArgumentTypes(constantPool);
               this.checkTypes(instructionHandle, types);

               if((size < (types.length + 1)) || //
                     (this.stack.get(size - types.length - 1).isObjectRef() == false))
               {
                  final StringBuilder message = new StringBuilder("INVOKEVIRTUAL need stack end with : objectref");

                  for(final Type t : types)
                  {
                     message.append(' ');
                     message.append(t);
                  }

                  this.throwException(instructionHandle, message.toString());
               }

               this.pop(types.length + 1);
            break;
            // ..., objectref, [arg1(?), [arg2(?) ...]] => ...
            case Constants.INVOKESPECIAL:
               types = ((INVOKESPECIAL) instruction).getArgumentTypes(constantPool);
               this.checkTypes(instructionHandle, types);

               if((size < (types.length + 1)) || //
                     (this.stack.get(size - types.length - 1).isObjectRef() == false))
               {
                  final StringBuilder message = new StringBuilder("INVOKESPECIAL need stack end with : objectref");

                  for(final Type t : types)
                  {
                     message.append(' ');
                     message.append(t);
                  }

                  this.throwException(instructionHandle, message.toString());
               }

               this.pop(types.length + 1);
            break;
            // ..., [arg1(?), [arg2(?) ...]] => ...
            case Constants.INVOKESTATIC:
               types = ((INVOKESTATIC) instruction).getArgumentTypes(constantPool);
               this.checkTypes(instructionHandle, types);
               this.pop(types.length);
            break;
            // ..., objectref, [arg1(?), [arg2(?) ...]] => ...
            case Constants.INVOKEINTERFACE:
               types = ((INVOKEINTERFACE) instruction).getArgumentTypes(constantPool);
               this.checkTypes(instructionHandle, types);

               if((size < (types.length + 1)) || //
                     (this.stack.get(size - types.length - 1).isObjectRef() == false))
               {
                  final StringBuilder message = new StringBuilder("INVOKEINTERFACE need stack end with : objectref");

                  for(final Type t : types)
                  {
                     message.append(' ');
                     message.append(t);
                  }

                  this.throwException(instructionHandle, message.toString());
               }

               this.pop(types.length + 1);
            break;
            // .. => ..., objectref
            case Constants.NEW:
               this.push(((NEW) instruction).getType(constantPool));
            break;
            // ..., count(int) => ..., arrayref
            case Constants.NEWARRAY:
               if((size < 1) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "NEWARRAY need stack end with : int");
               }

               this.pop(1);
               this.push(((NEWARRAY) instruction).getType());
            break;
            // ..., count(int) => ..., arrayref
            case Constants.ANEWARRAY:
               if((size < 1) || //
                     (this.stack.get(size - 1).isInt() == false))
               {
                  this.throwException(instructionHandle, "ANEWARRAY need stack end with : int");
               }

               this.pop(1);
               this.push(new ArrayType(((ANEWARRAY) instruction).getType(constantPool), 1));
            break;
            // ..., arrayref => ..., length(int)
            case Constants.ARRAYLENGTH:
               if((size < 1) || //
                     (this.stack.get(size - 1).isArrayRef() == false))
               {
                  this.throwException(instructionHandle, "ARRAYLENGTH need stack end with : arrayref");
               }

               this.pop(1);
               this.push(Type.INT);
            break;
            // .., objectref => objectref
            case Constants.ATHROW:
               if((size < 1) || //
                     (this.stack.get(size - 1).isObjectRef() == false))
               {
                  this.throwException(instructionHandle, "ATHROW need stack end with : objectref");
               }

               stackInfo.appendEnd(this.stack);
               continue;
            // ..., objectref => ..., objectref
            case Constants.CHECKCAST:
               if((size < 1) || //
                     (this.stack.get(size - 1).isObjectRef() == false))
               {
                  this.throwException(instructionHandle, "CHECKCAST need stack end with : objectref");
               }
            break;
            // .., objectref => ..., result(int)
            case Constants.INSTANCEOF:
               if((size < 1) || //
                     (this.stack.get(size - 1).isObjectRef() == false))
               {
                  this.throwException(instructionHandle, "INSTANCEOF need stack end with : objectref");
               }

               this.pop(1);
               this.push(Type.INT);
            break;
            // ..., objectref => ...
            case Constants.MONITORENTER:
               if((size < 1) || //
                     (this.stack.get(size - 1).isObjectRef() == false))
               {
                  this.throwException(instructionHandle, "MONITORENTER need stack end with : objectref");
               }

               this.pop(1);
            break;
            // ..., objectref => ...
            case Constants.MONITOREXIT:
               if((size < 1) || //
                     (this.stack.get(size - 1).isObjectRef() == false))
               {
                  this.throwException(instructionHandle, "MONITOREXIT need stack end with : objectref");
               }

               this.pop(1);
            break;
            // .., count1(int), [count2(int), ...] => ..., arrayref
            case Constants.MULTIANEWARRAY:
               temp = ((MULTIANEWARRAY) instruction).getDimensions() & 0xFFFF;
               condition = size >= temp;

               for(int index = size - temp; (index < size) && (condition == true); index++)
               {
                  condition &= this.stack.get(index).isInt();
               }

               if(condition == false)
               {
                  this.throwException(instructionHandle, UtilText.concatenate("MULTIANEWARRAY need stack end with :", UtilText.repeat(" int", temp)));
               }

               this.pop(temp);
               this.push(new ArrayType(((MULTIANEWARRAY) instruction).getType(constantPool), temp));
            break;
            // ..., objectref => ...
            case Constants.IFNULL:
               if((size < 1) || //
                     (this.stack.get(size - 1).isObjectRef() == false))
               {
                  this.throwException(instructionHandle, "IFNULL need stack end with : objectref");
               }

               this.pop(1);
               temp = this.indexOf(((IFNULL) instruction).getTarget(), instructionHandles);

               if(temp < 0)
               {
                  this.throwException(instructionHandle, "Failed to find the IFNULL target : " + ((IFNULL) instruction).getTarget());
               }

               stackExecution.push(new Step(temp, this.stack, this.path));
            break;
            // ..., objectref => ...
            case Constants.IFNONNULL:
               if((size < 1) || //
                     (this.stack.get(size - 1).isObjectRef() == false))
               {
                  this.throwException(instructionHandle, "IFNONNULL need stack end with : objectref");
               }

               this.pop(1);
               temp = this.indexOf(((IFNONNULL) instruction).getTarget(), instructionHandles);

               if(temp < 0)
               {
                  this.throwException(instructionHandle, "Failed to find the IFNONNULL target : " + ((IFNONNULL) instruction).getTarget());
               }

               stackExecution.push(new Step(temp, this.stack, this.path));
            break;
            // No change
            case Constants.GOTO_W:
               temp = this.indexOf(((GOTO_W) instruction).getTarget(), instructionHandles);

               if(temp < 0)
               {
                  this.throwException(instructionHandle, "Failed to find the GOTO_W target : " + ((GOTO_W) instruction).getTarget());
               }

               stackExecution.push(new Step(temp, this.stack, this.path));
               stackInfo.appendEnd(this.stack);
               continue;
            // ... => ..., address
            case Constants.JSR_W:
               temp = this.indexOf(((JSR_W) instruction).getTarget(), instructionHandles);

               if(temp < 0)
               {
                  this.throwException(instructionHandle, "Failed to find the JSR_W target : " + ((JSR_W) instruction).getTarget());
               }

               this.push(Type.OBJECT);
               stackExecution.push(new Step(temp, this.stack, this.path));
               this.pop(1);
            break;
         }

         stackInfo.appendEnd(this.stack);

         if((step.index + 1) < length)
         {
            step = new Step(step.index + 1, this.stack, this.path);

            if(already.add(step) == true)
            {
               stackExecution.push(step);
            }
         }
      }
   }
}