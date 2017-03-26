package jhelp.compiler.compil;

import com.sun.org.apache.bcel.internal.Constants;

/**
 * Constants used in compiler
 *
 * @author JHelp <br>
 */
public interface CompilerConstants
      extends OpcodeConstants
{
   /** Flags applied to the class */
   public static final int    ACCES_FLAGS_CLASS   = Constants.ACC_PUBLIC | Constants.ACC_SUPER;
   /** Check if public, private or protected are already specified */
   public static final int    ACCES_FLAGS_CONTROL = Constants.ACC_PUBLIC | Constants.ACC_PRIVATE | Constants.ACC_PROTECTED;
   /** Flags applied to class fields */
   public static final int    ACCES_FLAGS_FIELD   = Constants.ACC_PRIVATE;
   /** Flags applied to class methods */
   public static final int    ACCES_FLAGS_METHOD  = Constants.ACC_PUBLIC;

   // *********************
   // *** ASM key words ***
   // *********************

   /**
    * Declare the class in ASM files:<br>
    * <code>class &lt;ClassCompleteName&gt;</code>
    */
   public static final String CLASS               = "class";
   /** Close the code for a method (End method declaration) */
   public static final String CLOSE_BLOCK         = "}";
   /**
    * Specifies the parent class:<br>
    * <code>extends &lt;ClassName&gt;</code>
    */
   public static final String EXTENDS             = "extends";
   /**
    * Declare a field:<br>
    * <code>field &lt;type&gt; &lt;name&gt;</code>
    */
   public static final String FIELD               = "field";
   /**
    * Declare a reference to an external field:<br>
    * <code>field_reference &lt;className&gt; &lt;type&gt; &lt;name&gt; &lt;alias&gt;</code>
    */
   public static final String FIELD_REFERENCE     = "field_reference";
   /**
    * Add interface to implements:<br>
    * <code>implements &lt;ClassName&gt;</code>
    */
   public static final String IMPLEMENTS          = "implements";
   /**
    * Add an import:<br>
    * <code>import &lt;ClassCompleteName&gt;</code>
    */
   public static final String IMPORT              = "import";
   /**
    * Start method declaration:<br>
    * <code>method &lt;name&gt;</code>
    */
   public static final String METHOD              = "method";
   /** Start the method code */
   public static final String OPEN_BLOCK          = "{";
   /** For make a method or field package access */
   public static final String PACKAGE             = "package";
   /**
    * Add method parameter:<br>
    * <code>parameter &lt;type&gt; &lt;name&gt;</code>
    */
   public static final String PARAMETER           = "parameter";
   /** Make method or filed private access */
   public static final String PRIVATE             = "private";
   /** Make method or filed protected access */
   public static final String PROTECTED           = "protected";
   /** Make method or filed public access */
   public static final String PUBLIC              = "public";
   /**
    * Declare method return type:<br>
    * <code>return &lt;type&gt;</code>
    */
   public static final String RETURN_TYPE         = "return";
   /** Make method or filed static */
   public static final String STATIC              = "static";
   /**
    * Add exception throws by method:<br>
    * <code>throws &lt;ClassName&gt;</code>
    */
   public static final String THROWS              = "throws";
}