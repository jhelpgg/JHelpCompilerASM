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

import java.util.ArrayList;
import java.util.List;

import jhelp.util.list.Pair;

/**
 * List of code block intervals
 *
 * @author JHelp <br>
 */
class Intervals
{
    /**
     * List of code block intervals
     */
    private final List<Interval> intervals;

    /**
     * Create a new instance of Intervals
     */
    public Intervals()
    {
        this.intervals = new ArrayList<>();
    }

    /**
     * Define the end line number of current block interval
     *
     * @param lineNumber End line number
     */
    public void endInterval(final int lineNumber)
    {
        Interval interval;

        for (int i = this.intervals.size() - 1; i >= 0; i--)
        {
            interval = this.intervals.get(i);

            if (interval.lineEnd < 0)
            {
                interval.lineEnd = lineNumber;
                return;
            }
        }

        throw new IllegalStateException("No open interval !");
    }

    /**
     * Obtain the interval where is a given line number
     *
     * @param lineNumber Line number
     * @return Interval where is the given line number
     */
    public Interval obtainInterval(final int lineNumber)
    {
        Interval interval = null;

        for (final Interval interval2 : this.intervals)
        {
            if ((lineNumber >= interval2.lineStart) && (lineNumber <= interval2.lineEnd))
            {
                interval = interval2;
            }
        }

        return interval;
    }

    /**
     * Resolve lines handle with lines table
     *
     * @param linesTable Lines table
     */
    public void resolveInvervals(final List<Pair<InstructionHandle, Integer>> linesTable)
    {
        for (final Interval interval : this.intervals)
        {
            interval.handleStart = UtilCompiler.obtainInstructionAtOrAfter(interval.lineStart, linesTable);
            interval.handleEnd = UtilCompiler.obtainIntstructionAtOrBefore(interval.lineEnd, linesTable);
        }
    }

    /**
     * Start a block line at given line number
     *
     * @param lineNumber Start line number
     */
    public void startInterval(final int lineNumber)
    {
        final Interval interval = new Interval();
        interval.lineStart = lineNumber;
        this.intervals.add(interval);
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
        return this.intervals.toString();
    }
}
