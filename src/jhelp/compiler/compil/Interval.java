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

import jhelp.util.text.UtilText;

/**
 * Define a block code interval
 *
 * @author JHelp <br>
 */
class Interval
{
    /**
     * Last instruction of the block handle
     */
    InstructionHandle handleEnd;
    /**
     * First instruction of the block handle
     */
    InstructionHandle handleStart;
    /**
     * Last instruction of the block line number
     */
    int lineEnd   = -1;
    /**
     * First instruction of the block line number
     */
    int lineStart = -1;

    /**
     * Create a new instance of Interval
     */
    public Interval()
    {
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
        return UtilText.concatenate(this.lineStart, "<->", this.lineEnd, " : ", this.handleStart, " <=> ", this.handleEnd);
    }
}