package jhelp.compiler.compil;

import com.sun.org.apache.bcel.internal.generic.Type;

/**
 * Describe a method parameter
 *
 * @author JHelp <br>
 */
class Parameter
{
   /** Special parameter for make an empty space due double and long take 2 places */
   public static final Parameter SPACE = new Parameter("", Type.UNKNOWN, -1);

   /** Parameter declaration line number */
   private final int             lineNumber;
   /** Name */
   private final String          name;
   /** Type */
   private final Type            type;

   /**
    * Create a new instance of Parameter
    *
    * @param name
    *           name
    * @param type
    *           Type
    * @param lineNumber
    *           Parameter declaration line number
    */
   public Parameter(final String name, final Type type, final int lineNumber)
   {
      this.name = name;
      this.type = type;
      this.lineNumber = lineNumber;
   }

   /**
    * Actual lineNumber value
    *
    * @return Actual lineNumber value
    */
   public int getLineNumber()
   {
      return this.lineNumber;
   }

   /**
    * Name
    *
    * @return Name
    */
   public String getName()
   {
      return this.name;
   }

   /**
    * Type
    *
    * @return type
    */
   public Type getType()
   {
      return this.type;
   }
}