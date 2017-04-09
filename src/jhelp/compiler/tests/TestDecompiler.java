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
package jhelp.compiler.tests;

import java.io.File;
import java.io.FileInputStream;

import jhelp.compiler.decompil.Decompiler;
import jhelp.util.debug.Debug;

/**
 * @author JHelp <br>
 */
public class TestDecompiler
{

    /**
     * @param args
     */
    public static void main(final String[] args)
    {
        try
        {
            final File       file       = new File(
                    "/home/jhelp/jhelpapi/JHelpCompilerASM/bin/jhelp/compiler/decompil/Decompiler.class");
            final Decompiler decompiler = new Decompiler();
            decompiler.decompile(new FileInputStream(file), "jhelp.compiler.decompil.Decompiler", System.out);
        }
        catch (final Exception exception)
        {
            Debug.printException(exception, "Failed to decompile !");
        }
    }
}