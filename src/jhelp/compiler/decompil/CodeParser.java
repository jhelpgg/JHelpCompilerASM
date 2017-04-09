/*
 * License :
 * The following code is deliver as is. I take care that code compile and work, but I am not responsible about any damage it may cause.
 * You can use, modify, the code as your need for any usage.
 * But you can't do any action that avoid me or other person use, modify this code.
 * The code is free for usage and modification, you can't change that fact.
 * JHelp
 */

/**
 * <h1>License :</h1> <br>
 * The following code is deliver as is. I take care that code compile and work, but I am not responsible about any
 * damage it may
 * cause.<br>
 * You can use, modify, the code as your need for any usage. But you can't do any action that avoid me or other person use,
 * modify this code. The code is free for usage and modification, you can't change that fact.<br>
 * <br>
 *
 * @author JHelp
 */
package jhelp.compiler.decompil;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.classfile.Code;
import com.sun.org.apache.bcel.internal.classfile.Constant;
import com.sun.org.apache.bcel.internal.classfile.ConstantDouble;
import com.sun.org.apache.bcel.internal.classfile.ConstantFloat;
import com.sun.org.apache.bcel.internal.classfile.ConstantInteger;
import com.sun.org.apache.bcel.internal.classfile.ConstantLong;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.classfile.ConstantString;
import com.sun.org.apache.bcel.internal.classfile.ConstantUtf8;
import com.sun.org.apache.bcel.internal.generic.ANEWARRAY;
import com.sun.org.apache.bcel.internal.generic.ArrayType;
import com.sun.org.apache.bcel.internal.generic.BIPUSH;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.CPInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.FieldInstruction;
import com.sun.org.apache.bcel.internal.generic.IINC;
import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.InvokeInstruction;
import com.sun.org.apache.bcel.internal.generic.LocalVariableInstruction;
import com.sun.org.apache.bcel.internal.generic.MULTIANEWARRAY;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.NEWARRAY;
import com.sun.org.apache.bcel.internal.generic.RET;
import com.sun.org.apache.bcel.internal.generic.SIPUSH;
import com.sun.org.apache.bcel.internal.generic.Select;

import jhelp.util.text.UtilText;

/**
 * Parser of method embed code
 *
 * @author JHelp <br>
 */
public class CodeParser
{
    /**
     * Create a new instance of CodeParser
     */
    public CodeParser()
    {
    }

    /**
     * Parse a method code
     *
     * @param className             Class where method lies
     * @param constantPool          Constant pool reference
     * @param localesVariablesNames Name of local variables
     * @param code                  Method code
     * @return Method lines parsed. Some lines will may be empty, just ignore them
     */
    public String[] parse(final String className, final ConstantPool constantPool, final String[] localesVariablesNames,
                          final Code code)
    {
        // Initialization
        final ConstantPoolGen     constantPoolGen = new ConstantPoolGen(constantPool);
        final InstructionList     instructionList = new InstructionList(code.getCode());
        final InstructionHandle[] handles         = instructionList.getInstructionHandles();

        // We creates the double of lines to write real instruction one line od tow and let opportunity to add a label
        // declaration
        // before each of them
        final int      size      = handles.length << 1;
        final String[] codeLines = new String[size];

        for (int i = 0; i < size; i++)
        {
            codeLines[i] = "";
        }

        int                 indexCode  = 0;
        Instruction         instruction;
        String              string;
        int                 integer, length;
        Select              select;
        StringBuilder       stringBuilder;
        int[]               integers;
        InstructionHandle[] instructionHandles;
        InstructionHandle   handle;
        Constant            constant;
        int                 labelCount = 0;

        for (final InstructionHandle instructionHandle : handles)
        {
            instruction = instructionHandle.getInstruction();
            indexCode++;

            switch (instruction.getOpcode())
            {
                // Operation codes without parameters
                case Constants.AALOAD:
                case Constants.AASTORE:
                case Constants.DALOAD:
                case Constants.DASTORE:
                case Constants.IALOAD:
                case Constants.IASTORE:
                case Constants.FALOAD:
                case Constants.FASTORE:
                case Constants.LALOAD:
                case Constants.LASTORE:
                case Constants.ACONST_NULL:
                case Constants.ARETURN:
                case Constants.DRETURN:
                case Constants.IRETURN:
                case Constants.FRETURN:
                case Constants.LRETURN:
                case Constants.RETURN:
                case Constants.ARRAYLENGTH:
                case Constants.ATHROW:
                case Constants.BALOAD:
                case Constants.BASTORE:
                case Constants.CALOAD:
                case Constants.CASTORE:
                case Constants.D2F:
                case Constants.D2I:
                case Constants.D2L:
                case Constants.I2B:
                case Constants.I2C:
                case Constants.I2D:
                case Constants.I2F:
                case Constants.I2L:
                case Constants.I2S:
                case Constants.F2D:
                case Constants.F2I:
                case Constants.F2L:
                case Constants.L2D:
                case Constants.L2F:
                case Constants.L2I:
                case Constants.DADD:
                case Constants.IADD:
                case Constants.FADD:
                case Constants.LADD:
                case Constants.DSUB:
                case Constants.ISUB:
                case Constants.FSUB:
                case Constants.LSUB:
                case Constants.DMUL:
                case Constants.IMUL:
                case Constants.FMUL:
                case Constants.LMUL:
                case Constants.DDIV:
                case Constants.IDIV:
                case Constants.FDIV:
                case Constants.LDIV:
                case Constants.DREM:
                case Constants.IREM:
                case Constants.FREM:
                case Constants.LREM:
                case Constants.DNEG:
                case Constants.INEG:
                case Constants.FNEG:
                case Constants.LNEG:
                case Constants.IAND:
                case Constants.IOR:
                case Constants.IXOR:
                case Constants.ISHL:
                case Constants.ISHR:
                case Constants.IUSHR:
                case Constants.LAND:
                case Constants.LOR:
                case Constants.LXOR:
                case Constants.LSHL:
                case Constants.LSHR:
                case Constants.LUSHR:
                case Constants.DCMPG:
                case Constants.DCMPL:
                case Constants.FCMPG:
                case Constants.FCMPL:
                case Constants.LCMP:
                case Constants.DUP:
                case Constants.DUP_X1:
                case Constants.DUP_X2:
                case Constants.DUP2:
                case Constants.DUP2_X1:
                case Constants.DUP2_X2:
                case Constants.MONITORENTER:
                case Constants.MONITOREXIT:
                case Constants.POP:
                case Constants.POP2:
                case Constants.SWAP:
                    codeLines[indexCode++] = instruction.getName()
                                                        .toUpperCase();
                    break;
                // Local variables load and store
                case Constants.ALOAD:
                case Constants.DLOAD:
                case Constants.ILOAD:
                case Constants.FLOAD:
                case Constants.LLOAD:
                case Constants.ASTORE:
                case Constants.DSTORE:
                case Constants.ISTORE:
                case Constants.FSTORE:
                case Constants.LSTORE:
                    codeLines[indexCode++] = UtilText.concatenate(instruction.getName()
                                                                             .toUpperCase(), " ",
                                                                  localesVariablesNames[((LocalVariableInstruction)
                                                                                                 instruction).getIndex()]);
                    break;
                case Constants.ALOAD_0:
                    codeLines[indexCode++] = UtilText.concatenate("ALOAD ", localesVariablesNames[0]);
                    break;
                case Constants.ALOAD_1:
                    codeLines[indexCode++] = UtilText.concatenate("ALOAD ", localesVariablesNames[1]);
                    break;
                case Constants.ALOAD_2:
                    codeLines[indexCode++] = UtilText.concatenate("ALOAD ", localesVariablesNames[2]);
                    break;
                case Constants.ALOAD_3:
                    codeLines[indexCode++] = UtilText.concatenate("ALOAD ", localesVariablesNames[3]);
                    break;
                case Constants.DLOAD_0:
                    codeLines[indexCode++] = UtilText.concatenate("DLOAD ", localesVariablesNames[0]);
                    break;
                case Constants.DLOAD_1:
                    codeLines[indexCode++] = UtilText.concatenate("DLOAD ", localesVariablesNames[1]);
                    break;
                case Constants.DLOAD_2:
                    codeLines[indexCode++] = UtilText.concatenate("DLOAD ", localesVariablesNames[2]);
                    break;
                case Constants.DLOAD_3:
                    codeLines[indexCode++] = UtilText.concatenate("DLOAD ", localesVariablesNames[3]);
                    break;
                case Constants.ILOAD_0:
                    codeLines[indexCode++] = UtilText.concatenate("ILOAD ", localesVariablesNames[0]);
                    break;
                case Constants.ILOAD_1:
                    codeLines[indexCode++] = UtilText.concatenate("ILOAD ", localesVariablesNames[1]);
                    break;
                case Constants.ILOAD_2:
                    codeLines[indexCode++] = UtilText.concatenate("ILOAD ", localesVariablesNames[2]);
                    break;
                case Constants.ILOAD_3:
                    codeLines[indexCode++] = UtilText.concatenate("ILOAD ", localesVariablesNames[3]);
                    break;
                case Constants.FLOAD_0:
                    codeLines[indexCode++] = UtilText.concatenate("FLOAD ", localesVariablesNames[0]);
                    break;
                case Constants.FLOAD_1:
                    codeLines[indexCode++] = UtilText.concatenate("FLOAD ", localesVariablesNames[1]);
                    break;
                case Constants.FLOAD_2:
                    codeLines[indexCode++] = UtilText.concatenate("FLOAD ", localesVariablesNames[2]);
                    break;
                case Constants.FLOAD_3:
                    codeLines[indexCode++] = UtilText.concatenate("FLOAD ", localesVariablesNames[3]);
                    break;
                case Constants.LLOAD_0:
                    codeLines[indexCode++] = UtilText.concatenate("LLOAD ", localesVariablesNames[0]);
                    break;
                case Constants.LLOAD_1:
                    codeLines[indexCode++] = UtilText.concatenate("LLOAD ", localesVariablesNames[1]);
                    break;
                case Constants.LLOAD_2:
                    codeLines[indexCode++] = UtilText.concatenate("LLOAD ", localesVariablesNames[2]);
                    break;
                case Constants.LLOAD_3:
                    codeLines[indexCode++] = UtilText.concatenate("LLOAD ", localesVariablesNames[3]);
                    break;
                case Constants.ASTORE_0:
                    codeLines[indexCode++] = UtilText.concatenate("ASTORE ", localesVariablesNames[0]);
                    break;
                case Constants.ASTORE_1:
                    codeLines[indexCode++] = UtilText.concatenate("ASTORE ", localesVariablesNames[1]);
                    break;
                case Constants.ASTORE_2:
                    codeLines[indexCode++] = UtilText.concatenate("ASTORE ", localesVariablesNames[2]);
                    break;
                case Constants.ASTORE_3:
                    codeLines[indexCode++] = UtilText.concatenate("ASTORE ", localesVariablesNames[3]);
                    break;
                case Constants.DSTORE_0:
                    codeLines[indexCode++] = UtilText.concatenate("DSTORE ", localesVariablesNames[0]);
                    break;
                case Constants.DSTORE_1:
                    codeLines[indexCode++] = UtilText.concatenate("DSTORE ", localesVariablesNames[1]);
                    break;
                case Constants.DSTORE_2:
                    codeLines[indexCode++] = UtilText.concatenate("DSTORE ", localesVariablesNames[2]);
                    break;
                case Constants.DSTORE_3:
                    codeLines[indexCode++] = UtilText.concatenate("DSTORE ", localesVariablesNames[3]);
                    break;
                case Constants.ISTORE_0:
                    codeLines[indexCode++] = UtilText.concatenate("ISTORE ", localesVariablesNames[0]);
                    break;
                case Constants.ISTORE_1:
                    codeLines[indexCode++] = UtilText.concatenate("ISTORE ", localesVariablesNames[1]);
                    break;
                case Constants.ISTORE_2:
                    codeLines[indexCode++] = UtilText.concatenate("ISTORE ", localesVariablesNames[2]);
                    break;
                case Constants.ISTORE_3:
                    codeLines[indexCode++] = UtilText.concatenate("ISTORE ", localesVariablesNames[3]);
                    break;
                case Constants.FSTORE_0:
                    codeLines[indexCode++] = UtilText.concatenate("FSTORE ", localesVariablesNames[0]);
                    break;
                case Constants.FSTORE_1:
                    codeLines[indexCode++] = UtilText.concatenate("FSTORE ", localesVariablesNames[1]);
                    break;
                case Constants.FSTORE_2:
                    codeLines[indexCode++] = UtilText.concatenate("FSTORE ", localesVariablesNames[2]);
                    break;
                case Constants.FSTORE_3:
                    codeLines[indexCode++] = UtilText.concatenate("FSTORE ", localesVariablesNames[3]);
                    break;
                case Constants.LSTORE_0:
                    codeLines[indexCode++] = UtilText.concatenate("LSTORE ", localesVariablesNames[0]);
                    break;
                case Constants.LSTORE_1:
                    codeLines[indexCode++] = UtilText.concatenate("LSTORE ", localesVariablesNames[1]);
                    break;
                case Constants.LSTORE_2:
                    codeLines[indexCode++] = UtilText.concatenate("LSTORE ", localesVariablesNames[2]);
                    break;
                case Constants.LSTORE_3:
                    codeLines[indexCode++] = UtilText.concatenate("LSTORE ", localesVariablesNames[3]);
                    break;
                // Create object array
                case Constants.ANEWARRAY:
                    codeLines[indexCode++] = UtilText.concatenate("ANEWARRAY ",
                                                                  Decompiler.shortName(
                                                                          ((ANEWARRAY) instruction).getLoadClassType(
                                                                                  constantPoolGen)
                                                                                                   .toString()));
                    break;
                // Push byte constant
                case Constants.BIPUSH:
                    codeLines[indexCode++] = UtilText.concatenate("BIPUSH ", ((BIPUSH) instruction).getValue()
                                                                                                   .byteValue());
                    break;
                // Cast an object
                case Constants.CHECKCAST:
                    codeLines[indexCode++] = UtilText.concatenate("CHECKCAST ", Decompiler.shortName(
                            ((CHECKCAST) instruction).getType(constantPoolGen)
                                                     .toString()));
                    break;
                // Constants push
                case Constants.DCONST_0:
                    codeLines[indexCode++] = "DCONST 0";
                    break;
                case Constants.DCONST_1:
                    codeLines[indexCode++] = "DCONST 1";
                    break;
                case Constants.ICONST_M1:
                    codeLines[indexCode++] = "ICONST -1";
                    break;
                case Constants.ICONST_0:
                    codeLines[indexCode++] = "ICONST 0";
                    break;
                case Constants.ICONST_1:
                    codeLines[indexCode++] = "ICONST 1";
                    break;
                case Constants.ICONST_2:
                    codeLines[indexCode++] = "ICONST 2";
                    break;
                case Constants.ICONST_3:
                    codeLines[indexCode++] = "ICONST 3";
                    break;
                case Constants.ICONST_4:
                    codeLines[indexCode++] = "ICONST 4";
                    break;
                case Constants.ICONST_5:
                    codeLines[indexCode++] = "ICONST 5";
                    break;
                case Constants.FCONST_0:
                    codeLines[indexCode++] = "FCONST 0";
                    break;
                case Constants.FCONST_1:
                    codeLines[indexCode++] = "FCONST 1";
                    break;
                case Constants.FCONST_2:
                    codeLines[indexCode++] = "FCONST 2";
                    break;
                case Constants.LCONST_0:
                    codeLines[indexCode++] = "LCONST 0";
                    break;
                case Constants.LCONST_1:
                    codeLines[indexCode++] = "LCONST 1";
                    break;
                // Fields access
                case Constants.GETFIELD:
                case Constants.GETSTATIC:
                case Constants.PUTFIELD:
                case Constants.PUTSTATIC:
                    string = ((FieldInstruction) instruction).getClassName(constantPoolGen);

                    if (className.equals(string))
                    {
                        codeLines[indexCode++] = UtilText.concatenate(instruction.getName()
                                                                                 .toUpperCase(), " ",
                                                                      ((FieldInstruction) instruction).getFieldName(
                                                                              constantPoolGen));
                    }
                    else
                    {
                        codeLines[indexCode++] = UtilText.concatenate(instruction.getName()
                                                                                 .toUpperCase(), " ",
                                                                      Decompiler.shortName(string), ".",
                                                                      ((FieldInstruction) instruction).getFieldName(
                                                                              constantPoolGen));
                    }

                    break;
                // Jump instructions
                case Constants.GOTO:
                case Constants.GOTO_W:
                case Constants.IF_ACMPEQ:
                case Constants.IF_ACMPNE:
                case Constants.IF_ICMPEQ:
                case Constants.IF_ICMPGE:
                case Constants.IF_ICMPGT:
                case Constants.IF_ICMPLE:
                case Constants.IF_ICMPLT:
                case Constants.IF_ICMPNE:
                case Constants.IFEQ:
                case Constants.IFGE:
                case Constants.IFGT:
                case Constants.IFLE:
                case Constants.IFLT:
                case Constants.IFNE:
                case Constants.IFNONNULL:
                case Constants.IFNULL:
                case Constants.JSR:
                case Constants.JSR_W:
                    // Compute where jump
                    integer = CodeParser.indexOf(((BranchInstruction) instruction).getTarget(), handles) << 1;
                    string = codeLines[integer];

                    if (string.length() == 0)
                    {
                        // Add label for jump destination
                        codeLines[integer] = UtilText.concatenate("LABEL label_", labelCount);
                        labelCount++;
                    }

                    codeLines[indexCode++] = UtilText.concatenate(instruction.getName()
                                                                             .toUpperCase(), " ",
                                                                  codeLines[integer].substring(6));
                    break;
                // Switch instructions
                case Constants.SWITCH:
                case Constants.LOOKUPSWITCH:
                case Constants.TABLESWITCH:
                    select = (Select) instruction;
                    stringBuilder = new StringBuilder("SWITCH");

                    integers = select.getMatchs();
                    instructionHandles = select.getTargets();
                    length = integers.length;
                    handle = select.getTarget();

                    for (int i = 0; i < length; i++)
                    {
                        if (handle.equals(instructionHandles[i]))
                        {
                            continue;
                        }

                        stringBuilder.append(' ');
                        stringBuilder.append(integers[i]);
                        stringBuilder.append(' ');
                        integer = CodeParser.indexOf(instructionHandles[i], handles) << 1;
                        string = codeLines[integer];

                        if (string.length() == 0)
                        {
                            // Add label for jump destination
                            codeLines[integer] = UtilText.concatenate("LABEL label_", labelCount);
                            labelCount++;
                        }

                        stringBuilder.append(codeLines[integer].substring(6));
                    }

                    stringBuilder.append("   ");
                    integer = CodeParser.indexOf(handle, handles) << 1;
                    string = codeLines[integer];

                    if (string.length() == 0)
                    {
                        // Add label for jump destination
                        codeLines[integer] = UtilText.concatenate("LABEL label_", labelCount);
                        labelCount++;
                    }

                    stringBuilder.append(codeLines[integer].substring(6));
                    codeLines[indexCode++] = stringBuilder.toString();
                    break;
                // Increment
                case Constants.IINC:
                    codeLines[indexCode++] = UtilText.concatenate("IINC ",
                                                                  localesVariablesNames[((IINC) instruction).getIndex()],
                                                                  " ",
                                                                  ((IINC) instruction).getIncrement());
                    break;
                // Instance of
                case Constants.INSTANCEOF:
                    codeLines[indexCode++] = UtilText.concatenate("INSTANCEOF ",
                                                                  Decompiler.shortName(
                                                                          ((INSTANCEOF) instruction).getLoadClassType(
                                                                                  constantPoolGen)
                                                                                                    .toString()));
                    break;
                // Invok instructions
                case Constants.INVOKEINTERFACE:
                    stringBuilder = new StringBuilder();
                    CodeParser.parse((InvokeInstruction) instruction, stringBuilder, constantPoolGen);
                    stringBuilder.append(' ');
                    stringBuilder.append(((INVOKEINTERFACE) instruction).getCount());
                    codeLines[indexCode++] = stringBuilder.toString();
                    break;
                case Constants.INVOKESPECIAL:
                case Constants.INVOKESTATIC:
                case Constants.INVOKEVIRTUAL:
                    stringBuilder = new StringBuilder();
                    CodeParser.parse((InvokeInstruction) instruction, stringBuilder, constantPoolGen);
                    codeLines[indexCode++] = stringBuilder.toString();
                    break;
                // Generic push instructions
                case Constants.LDC:
                case Constants.LDC_W:
                case Constants.LDC2_W:
                case Constants.PUSH:
                    constant = constantPool.getConstant(((CPInstruction) instruction).getIndex());

                    switch (constant.getTag())
                    {
                        case Constants.CONSTANT_String:
                            codeLines[indexCode++] = UtilText.concatenate("PUSH \"",
                                                                          UtilText.putAntiSlash(
                                                                                  ((ConstantUtf8) constantPool.getConstant(
                                                                                          ((ConstantString) constant)
                                                                                                  .getStringIndex()))
                                                                                          .getBytes(),
                                                                                  '"', '\\'),
                                                                          '"');
                            break;
                        case Constants.CONSTANT_Float:
                            codeLines[indexCode++] = UtilText.concatenate("PUSH ", ((ConstantFloat) constant).getBytes(),
                                                                          'f');
                            break;
                        case Constants.CONSTANT_Integer:
                            codeLines[indexCode++] = UtilText.concatenate("PUSH ",
                                                                          ((ConstantInteger) constant).getBytes());
                            break;
                        case Constants.CONSTANT_Long:
                            codeLines[indexCode++] = UtilText.concatenate("PUSH ", ((ConstantLong) constant).getBytes(),
                                                                          'l');
                            break;
                        case Constants.CONSTANT_Double:
                            codeLines[indexCode++] = UtilText.concatenate("PUSH ", ((ConstantDouble) constant).getBytes(),
                                                                          'd');
                            break;
                    }

                    break;
                // Multiple size array
                case Constants.MULTIANEWARRAY:
                    codeLines[indexCode++] = UtilText.concatenate("MULTIANEWARRAY ",
                                                                  Decompiler.shortName(
                                                                          ((MULTIANEWARRAY) instruction).getLoadClassType(
                                                                                  constantPoolGen)
                                                                                                        .toString()), ' ',
                                                                  ((MULTIANEWARRAY) instruction).getDimensions());
                    break;
                // new
                case Constants.NEW:
                    codeLines[indexCode++] = UtilText.concatenate("NEW ", Decompiler.shortName(
                            ((NEW) instruction).getLoadClassType(constantPoolGen)
                                               .toString()));
                    break;
                // New native array
                case Constants.NEWARRAY:
                    codeLines[indexCode++] = UtilText.concatenate("NEWARRAY ",
                                                                  ((ArrayType) ((NEWARRAY) instruction).getType())
                                                                          .getBasicType()
                                                                                                                  .toString());
                    break;
                // RET
                case Constants.RET:
                    codeLines[indexCode++] = UtilText.concatenate("RET ",
                                                                  localesVariablesNames[((RET) instruction).getIndex()]);
                    break;
                // Short push
                case Constants.SIPUSH:
                    codeLines[indexCode++] = UtilText.concatenate("SIPUSH ", ((SIPUSH) instruction).getValue()
                                                                                                   .shortValue());
                    break;
                // Ignored/unknown instruction
                default:
                    codeLines[indexCode++] = UtilText.concatenate("// ", instruction.getName()
                                                                                    .toUpperCase(), " : ",
                                                                  instruction.toString(constantPool));
                    break;
            }
        }

        return codeLines;
    }

    /**
     * Compute index of handle inside handle array
     *
     * @param instructionHandle Handle searched
     * @param handles           Handles where search
     * @return Handle index OR -1 if not found
     */
    static int indexOf(final InstructionHandle instructionHandle, final InstructionHandle[] handles)
    {
        for (int index = handles.length - 1; index >= 0; index--)
        {
            if (instructionHandle.equals(handles[index]))
            {
                return index;
            }
        }

        return -1;
    }

    /**
     * Parse an invoke instruction
     *
     * @param invokeInstruction Invoke instruction to parse
     * @param stringBuilder     String builder where write
     * @param constantPoolGen   Constant pool reference
     */
    static void parse(final InvokeInstruction invokeInstruction, final StringBuilder stringBuilder,
                      final ConstantPoolGen constantPoolGen)
    {
        stringBuilder.append(invokeInstruction.getName()
                                              .toUpperCase());
        stringBuilder.append(' ');
        stringBuilder.append(Decompiler.shortName(invokeInstruction.getClassName(constantPoolGen)));
        stringBuilder.append('.');
        stringBuilder.append(invokeInstruction.getMethodName(constantPoolGen));
        CodeParser.appendSignature(invokeInstruction.getSignature(constantPoolGen), stringBuilder);
    }

    /**
     * Append method signature
     *
     * @param signature     Signature to append
     * @param stringBuilder String builder where append
     */
    static void appendSignature(final String signature, final StringBuilder stringBuilder)
    {
        final char[] characters = signature.toCharArray();
        final int    length     = characters.length;
        int          start      = -1;
        char         character;

        boolean             returnType = false;
        final StringBuilder array      = new StringBuilder();

        for (int index = 0; index < length; index++)
        {
            character = characters[index];

            switch (character)
            {
                case '(':
                    stringBuilder.append('(');
                    break;
                case '[':
                    array.append("[]");
                    break;
                case 'Z':
                    if (start >= 0)
                    {
                        break;
                    }

                    stringBuilder.append("boolean");
                    stringBuilder.append(array);
                    array.delete(0, array.length());

                    if ((!returnType) && (characters[index + 1] != ')'))
                    {
                        stringBuilder.append(", ");
                    }

                    break;
                case 'C':
                    if (start >= 0)
                    {
                        break;
                    }

                    stringBuilder.append("char");
                    stringBuilder.append(array);
                    array.delete(0, array.length());

                    if ((!returnType) && (characters[index + 1] != ')'))
                    {
                        stringBuilder.append(", ");
                    }

                    break;
                case 'B':
                    if (start >= 0)
                    {
                        break;
                    }

                    stringBuilder.append("byte");
                    stringBuilder.append(array);
                    array.delete(0, array.length());

                    if ((!returnType) && (characters[index + 1] != ')'))
                    {
                        stringBuilder.append(", ");
                    }

                    break;
                case 'S':
                    if (start >= 0)
                    {
                        break;
                    }

                    stringBuilder.append("short");
                    stringBuilder.append(array);
                    array.delete(0, array.length());

                    if ((!returnType) && (characters[index + 1] != ')'))
                    {
                        stringBuilder.append(", ");
                    }

                    break;
                case 'I':
                    if (start >= 0)
                    {
                        break;
                    }

                    stringBuilder.append("int");
                    stringBuilder.append(array);
                    array.delete(0, array.length());

                    if ((!returnType) && (characters[index + 1] != ')'))
                    {
                        stringBuilder.append(", ");
                    }

                    break;
                case 'J':
                    if (start >= 0)
                    {
                        break;
                    }

                    stringBuilder.append("long");
                    stringBuilder.append(array);
                    array.delete(0, array.length());

                    if ((!returnType) && (characters[index + 1] != ')'))
                    {
                        stringBuilder.append(", ");
                    }

                    break;
                case 'F':
                    if (start >= 0)
                    {
                        break;
                    }

                    stringBuilder.append("float");
                    stringBuilder.append(array);
                    array.delete(0, array.length());

                    if ((!returnType) && (characters[index + 1] != ')'))
                    {
                        stringBuilder.append(", ");
                    }

                    break;
                case 'D':
                    if (start >= 0)
                    {
                        break;
                    }

                    stringBuilder.append("double");
                    stringBuilder.append(array);
                    array.delete(0, array.length());

                    if ((!returnType) && (characters[index + 1] != ')'))
                    {
                        stringBuilder.append(", ");
                    }

                    break;
                case 'L':
                    if (start >= 0)
                    {
                        break;
                    }
                    // No break
                case '/':
                    start = index + 1;
                    break;
                case ';':
                    stringBuilder.append(characters, start, index - start);
                    stringBuilder.append(array);
                    array.delete(0, array.length());

                    if ((!returnType) && (characters[index + 1] != ')'))
                    {
                        stringBuilder.append(", ");
                    }

                    start = -1;
                    break;
                case ')':
                    stringBuilder.append(')');
                    returnType = true;

                    if (characters[index + 1] == 'V')
                    {
                        return;
                    }

                    stringBuilder.append(':');
                    break;
            }
        }
    }
}