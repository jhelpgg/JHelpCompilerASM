package jhelp.compiler.compil;

import com.sun.org.apache.bcel.internal.generic.Type;

/**
 * Describe a field of class
 *
 * @author JHelp <br>
 */
class FieldInformation
{
   /** Line number where the field was declare */
   private final int    lineDeclaration;
   /** Field name */
   private final String name;
   /** Field reference in constant pool */
   private final int    reference;
   /** Field type */
   private final Type   type;

   /**
    * Create a new instance of FieldInformation
    *
    * @param name
    *           Field name
    * @param type
    *           Field type
    * @param reference
    *           Field reference in constant pool
    * @param lineDeclaration
    *           Line number where the field was declare
    */
   public FieldInformation(final String name, final Type type, final int reference, final int lineDeclaration)
   {
      this.name = name;
      this.type = type;
      this.reference = reference;
      this.lineDeclaration = lineDeclaration;
   }

   /**
    * Line number where the field was declare
    *
    * @return Line number where the field was declare
    */
   public int getLineDeclaration()
   {
      return this.lineDeclaration;
   }

   /**
    * Field name
    *
    * @return Field name
    */
   public String getName()
   {
      return this.name;
   }

   /**
    * Field reference in constant pool
    *
    * @return Field reference in constant pool
    */
   public int getReference()
   {
      return this.reference;
   }

   /**
    * Field type
    *
    * @return Field type
    */
   public Type getType()
   {
      return this.type;
   }
}