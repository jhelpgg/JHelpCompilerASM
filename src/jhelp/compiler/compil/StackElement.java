package jhelp.compiler.compil;

import com.sun.org.apache.bcel.internal.generic.ArrayType;
import com.sun.org.apache.bcel.internal.generic.ObjectType;
import com.sun.org.apache.bcel.internal.generic.Type;

/**
 * Element of stack
 *
 * @author JHelp <br>
 */
class StackElement
{
   /** Embed type */
   private final Type type;

   /**
    * Create a new instance of StackElement
    *
    * @param type
    *           Type carry by stack element
    */
   public StackElement(final Type type)
   {
      this.type = type;
   }

   /**
    * Indicates if given type can be use with the embed one
    *
    * @param type
    *           Type tested
    * @return {@code true} if given type can be use with the embed one
    */
   public boolean compatibleWtih(final Type type)
   {
      if(type == null)
      {
         return false;
      }

      if(this.type.equals(type) == true)
      {
         return true;
      }

      if(this.isInt() == true)
      {
         return (type == Type.BOOLEAN) || (type == Type.CHAR) || (type == Type.BYTE) || (type == Type.SHORT) || (type == Type.INT);
      }

      if(this.type == Type.NULL)
      {
         return (type instanceof ObjectType) || (type instanceof ArrayType);
      }

      if(this.type instanceof ObjectType)
      {
         return (type == Type.NULL) || (type instanceof ObjectType);
      }

      if(this.type instanceof ArrayType)
      {
         return (type == Type.NULL) || (type instanceof ArrayType);
      }

      return false;
   }

   /**
    * Embed type
    *
    * @return Embed type
    */
   public Type getType()
   {
      return this.type;
   }

   /**
    * Indicates if can be use as reference on array
    *
    * @return {@code true} if can be use as reference on array
    */
   public boolean isArrayRef()
   {
      return (this.type == Type.NULL) || (this.type instanceof ArrayType);
   }

   /**
    * Indicates if it is a double
    *
    * @return {@code true} if it is a double
    */
   public boolean isDouble()
   {
      return this.type == Type.DOUBLE;
   }

   /**
    * Indicates if it is a double or a long
    *
    * @return {@code true} if it is a double or a long
    */
   public boolean isDoubleOrLong()
   {
      return (this.type == Type.LONG) || (this.type == Type.DOUBLE);
   }

   /**
    * Indicates if it is a float
    *
    * @return {@code true} if it is a float
    */
   public boolean isFloat()
   {
      return this.type == Type.FLOAT;
   }

   /**
    * Indicates if can be consider as int
    *
    * @return {@code true} if can be consider as int
    */
   public boolean isInt()
   {
      return (this.type == Type.BOOLEAN) || (this.type == Type.CHAR) || (this.type == Type.BYTE) || (this.type == Type.SHORT) || (this.type == Type.INT);
   }

   /**
    * Indicates if it is a long
    *
    * @return {@code true} if it is a long
    */
   public boolean isLong()
   {
      return this.type == Type.LONG;
   }

   /**
    * Indicates if reference on object
    *
    * @return {@code true} if reference on object
    */
   public boolean isObjectRef()
   {
      return (this.type == Type.NULL) || (this.type instanceof ObjectType) || (this.type instanceof ArrayType);
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
      return this.type.toString();
   }
}