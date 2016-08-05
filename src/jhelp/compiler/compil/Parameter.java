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
   public static final Parameter SPACE = new Parameter("", Type.UNKNOWN);

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
    */
   public Parameter(final String name, final Type type)
   {
      this.name = name;
      this.type = type;
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