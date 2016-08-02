package jhelp.compiler.compil;

import com.sun.org.apache.bcel.internal.generic.Type;

/**
 * Describe a field of class
 *
 * @author JHelp <br>
 */
class FieldInformation
{
   /** Alias give to field (For external fields) */
   private final String alias;
   /** Class name where find the field (For external fields) */
   private final String className;
   /** Line number where the field was declare */
   private final int    lineDeclaration;
   /** Field name */
   private final String name;
   /** Field reference in constant pool */
   private final int    reference;
   /** Field type */
   private final Type   type;

   /**
    * Create a new instance of FieldInformation for external field
    *
    * @param alias
    *           Alias give to field
    * @param name
    *           Field name on class
    * @param className
    *           Class name where find the field
    * @param type
    *           Field type
    * @param reference
    *           Reference tin constant pool
    * @param lineDeclaration
    *           Line number where field declare
    */
   public FieldInformation(final String alias, final String name, final String className, final Type type, final int reference, final int lineDeclaration)
   {
      this.alias = alias;
      this.name = name;
      this.className = className;
      this.type = type;
      this.reference = reference;
      this.lineDeclaration = lineDeclaration;
   }

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
      this(name, name, null, type, reference, lineDeclaration);
   }

   /**
    * Alias give to field (For external fields)
    *
    * @return Alias give to field (For external fields)
    */
   public String getAlias()
   {
      return this.alias;
   }

   /**
    * Class name where find the field (For external fields)
    *
    * @return Class name where find the field (For external fields)
    */
   public String getClassName()
   {
      return this.className;
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