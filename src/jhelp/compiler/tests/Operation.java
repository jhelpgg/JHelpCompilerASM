package jhelp.compiler.tests;

/**
 * Generic operation
 *
 * @author JHelp <br>
 */
public interface Operation
{
   /**
    * Calculate the operation result
    *
    * @param first
    *           First value
    * @param second
    *           Second value
    * @return Calculation result
    */
   public int calculate(int first, int second);
}