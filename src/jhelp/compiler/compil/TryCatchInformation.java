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
package jhelp.compiler.compil;

import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.ObjectType;

/**
 * Information about try/catch block
 *
 * @author JHelp <br>
 */
class TryCatchInformation
{
    /**
     * Exception name
     */
    private final String            exceptionName;
    /**
     * Exception type
     */
    private final ObjectType        exceptionType;
    /**
     * {@link OpcodeConstants#Z_TRY} line code
     */
    private final int               startLine;
    /**
     * Resolved handle of last block instruction
     */
    private       InstructionHandle endInstruction;
    /**
     * {@link OpcodeConstants#Z_CATCH} line code
     */
    private       int               endLine;
    /**
     * Resolved handle where to go on exception
     */
    private       InstructionHandle gotoInstruction;
    /**
     * Label to go on exception
     */
    private       String            gotoLabel;
    /**
     * Resolved handle of first block instruction
     */
    private       InstructionHandle startInstruction;

    /**
     * Create a new instance of TryCatchInformation
     *
     * @param exceptionName Exception name
     * @param startLine     {@link OpcodeConstants#Z_TRY} line code
     * @param exceptionType Exception type
     */
    public TryCatchInformation(final String exceptionName, final int startLine, final ObjectType exceptionType)
    {
        this.exceptionName = exceptionName;
        this.startLine = startLine;
        this.exceptionType = exceptionType;
        this.endLine = -1;
    }

    /**
     * Resolved handle of last block instruction
     *
     * @return Resolved handle of last block instruction
     */
    public InstructionHandle getEndInstruction()
    {
        return this.endInstruction;
    }

    /**
     * Change endInstruction
     *
     * @param endInstruction New endInstruction value
     */
    public void setEndInstruction(final InstructionHandle endInstruction)
    {
        this.endInstruction = endInstruction;
    }

    /**
     * {@link OpcodeConstants#Z_CATCH} line code
     *
     * @return {@link OpcodeConstants#Z_CATCH} line code
     */
    public int getEndLine()
    {
        return this.endLine;
    }

    /**
     * Change endLine
     *
     * @param endLine New endLine value
     */
    public void setEndLine(final int endLine)
    {
        this.endLine = endLine;
    }

    /**
     * Actual exceptionName value
     *
     * @return Actual exceptionName value
     */
    public String getExceptionName()
    {
        return this.exceptionName;
    }

    /**
     * Actual exceptionType value
     *
     * @return Actual exceptionType value
     */
    public ObjectType getExceptionType()
    {
        return this.exceptionType;
    }

    /**
     * Actual gotoInstruction value
     *
     * @return Actual gotoInstruction value
     */
    public InstructionHandle getGotoInstruction()
    {
        return this.gotoInstruction;
    }

    /**
     * Change gotoInstruction
     *
     * @param gotoInstruction New gotoInstruction value
     */
    public void setGotoInstruction(final InstructionHandle gotoInstruction)
    {
        this.gotoInstruction = gotoInstruction;
    }

    /**
     * Actual gotoLabel value
     *
     * @return Actual gotoLabel value
     */
    public String getGotoLabel()
    {
        return this.gotoLabel;
    }

    /**
     * Change gotoLabel
     *
     * @param gotoLabel New gotoLabel value
     */
    public void setGotoLabel(final String gotoLabel)
    {
        this.gotoLabel = gotoLabel;
    }

    /**
     * Actual startInstruction value
     *
     * @return Actual startInstruction value
     */
    public InstructionHandle getStartInstruction()
    {
        return this.startInstruction;
    }

    /**
     * Change startInstruction
     *
     * @param startInstruction New startInstruction value
     */
    public void setStartInstruction(final InstructionHandle startInstruction)
    {
        this.startInstruction = startInstruction;
    }

    /**
     * Actual startLine value
     *
     * @return Actual startLine value
     */
    public int getStartLine()
    {
        return this.startLine;
    }
}