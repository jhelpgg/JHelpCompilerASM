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

/**
 * @author JHelp <br>
 */
public class TestException
{

    /**
     * Create a new instance of TestException
     */
    public TestException()
    {
        try
        {
            System.out.println("try");
        }
        catch (final ArithmeticException exception)
        {
            System.out.println("catch ArithmeticException");
        }
        catch (final Exception exception)
        {
            System.out.println("catch exception");
        }
        catch (final Error error)
        {
            System.out.println("catch error");
        }
        finally
        {
            System.out.println("finally");
        }
    }

}
