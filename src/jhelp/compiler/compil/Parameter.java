package jhelp.compiler.compil;

import com.sun.org.apache.bcel.internal.generic.Type;

/**
 * Describe a method parameter
 *
 * @author JHelp <br>
 */
class Parameter
{
   /** Name */
   private final String name;
   /** Type */
   private final Type   type;

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