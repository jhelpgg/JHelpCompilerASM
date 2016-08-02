package jhelp.compiler.compil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.org.apache.bcel.internal.generic.ArrayType;
import com.sun.org.apache.bcel.internal.generic.BasicType;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ClassGen;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.FieldGen;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.ObjectType;
import com.sun.org.apache.bcel.internal.generic.Type;

import jhelp.util.list.Pair;
import jhelp.util.reflection.Reflector;
import jhelp.util.text.StringCutter;
import jhelp.util.text.UtilText;

/**
 * Context of compilation.<br>
 * It carries lot of information to share them easily throw compilation process.<br>
 * Labels not exists in real opcodes, so we have to attach them to the next real opcode instruction handle, but for have it we
 * must previously add it. Thats why when meet label instruction, we store temporary the label name, then we wait next real
 * opcode instruction to know how solve the label. That explain also why it is impossible to declare 2 labels without separate
 * them with at least one real opcode instruction.<br>
 * Branches instruction (GOTO, IF_*, ...) target label that can be resolved when all code is finished, thats why they are stored
 * with null target and then resolve when we know the real position of labels. Same trick is apply for "switch" instructions.
 *
 * @author JHelp <br>
 */
class CompilerContext
      implements CompilerConstants
{
   /**
    * Pattern for signature like : <br>
    * <code>(int,boolean,Object,java.util.List):String</code><br>
    * <ol>
    * <li>Group 1 : Parameter list</li>
    * <li>Group 2 : If exists => Just convenient for capture return type if exists</li>
    * <li>Group 3 : If exists => return type</li>
    * </ol>
    */
   private static final Pattern                 PATTERN_SIGNATURE_JAVA = Pattern.compile("\\(([a-zA-Z0-9_.,]*)\\)(:([a-zA-Z0-9_.]+))?");
   /** List of instruction branches to solve destination target later. */
   private final List<BranchInformation>        branches;
   /** Class actually compiled */
   private ClassGen                             classGen;
   /** Class complete name */
   private String                               className;
   /** Class constant pool */
   private ConstantPoolGen                      constantPoolGen;
   /** Current method throws exceptions */
   private final List<String>                   exceptions;
   /** Class fields */
   private final Map<String, FieldInformation>  fields;
   /** List of imported classes */
   private final List<String>                   imports;
   /** Start index in local variable of current method that delimit parameters from real local variables */
   private int                                  indexMarkReference;
   /** Interfaces that compiled class implements */
   private final List<String>                   interfaces;
   /** Current method resolved labels */
   private final Map<String, InstructionHandle> labels;
   /** Name of label to attach to next real opcode instruction */
   private String                               labeltoDefine;
   /** Current method local variables */
   private final List<Parameter>                localeVariables;
   /** Current compiled class package name */
   private String                               packageName;
   /** Current class parent */
   private String                               parent;
   /** "Switch" instructions to resolve later */
   private final List<SelectInformation>        switches;

   /** Resolved types */
   private final Map<String, Type>              types;

   /**
    * Create a new instance of CompilerContext
    */
   public CompilerContext()
   {
      this.parent = "java.lang.Object";
      this.imports = new ArrayList<String>();
      this.interfaces = new ArrayList<String>();
      this.localeVariables = new ArrayList<Parameter>();
      this.fields = new HashMap<String, FieldInformation>();
      this.types = new HashMap<String, Type>();
      this.branches = new ArrayList<BranchInformation>();
      this.labels = new HashMap<String, InstructionHandle>();
      this.exceptions = new ArrayList<String>();
      this.switches = new ArrayList<SelectInformation>();
   }

   /**
    * Add or get a reference
    *
    * @param name
    *           Reference name
    * @param typeName
    *           Reference type. can be {@code null} if only get
    * @param lineNumber
    *           Line number of code where reference is add or get
    * @param reference
    *           List of reference where add or get
    * @return The reference add or get
    * @throws CompilerException
    *            If the reference can't be add nor get
    */
   private int addGetReference(final String name, final String typeName, final int lineNumber, final List<Parameter> reference) throws CompilerException
   {
      Type type = null;

      if(typeName != null)
      {
         type = this.stringToType(typeName);
      }

      this.constantPoolGen.addUtf8(name);

      if(type != null)
      {
         this.constantPoolGen.addUtf8(type.getSignature());

         if(type instanceof ObjectType)
         {
            this.constantPoolGen.addClass((ObjectType) type);
         }
      }

      final int size = reference.size();

      for(int i = 0; i < size; i++)
      {
         if(name.equals(reference.get(i).getName()) == true)
         {
            return i;
         }
      }

      if(type == null)
      {
         throw new CompilerException(lineNumber, "Reference '" + name + "' not found !");
      }

      final Parameter parameter = new Parameter(name, type);
      reference.add(parameter);
      return size;
   }

   /**
    * Add branch instruction
    *
    * @param branchHandle
    *           Handle of the instruction
    * @param label
    *           Label target of the branch
    * @param lineNumber
    *           Line number where branch is declare
    */
   public void addBranch(final BranchHandle branchHandle, final String label, final int lineNumber)
   {
      this.branches.add(new BranchInformation(branchHandle, label, lineNumber));
   }

   /**
    * Add/get reference to a class in constant pool
    *
    * @param typeName
    *           Class name
    * @param lineNumber
    *           Line number where reference add/use
    * @return Reference on class
    * @throws CompilerException
    *            If typeName not a valid class name
    */
   public int addClassReference(final String typeName, final int lineNumber) throws CompilerException
   {
      final Type type = this.stringToType(typeName);

      if(type instanceof ObjectType)
      {
         return this.constantPoolGen.addClass((ObjectType) type);
      }

      throw new CompilerException(lineNumber, typeName + " not a class reference !");
   }

   /**
    * Add/get reference to constant in constant pool
    *
    * @param value
    *           Serialized constant value
    * @param lineNumber
    *           Line number where constant meet
    * @return Reference to constant
    * @throws CompilerException
    *            If value not a valid constant value
    */
   public int addConstant(final String value, final int lineNumber) throws CompilerException
   {
      final int length = value.length();

      if(length > 1)
      {
         if((value.charAt(0) == '"') && (value.charAt(length - 1) == '"'))
         {
            return this.constantPoolGen.addString(UtilText.interpretAntiSlash(value.substring(1, length - 1)));
         }

         if(length > 2)
         {
            if((value.charAt(0) == '\'') && (value.charAt(length - 1) == '\''))
            {
               final String character = UtilText.interpretAntiSlash(value.substring(1, length - 1));

               if(character.length() != 1)
               {
                  throw new CompilerException(lineNumber, "Invalid character !");
               }

               return this.constantPoolGen.addInteger(character.charAt(0) & 0xFFFF);
            }
         }
      }

      if("true".equals(value) == true)
      {
         return this.constantPoolGen.addInteger(1);
      }

      if("false".equals(value) == true)
      {
         return this.constantPoolGen.addInteger(0);
      }

      if((value.endsWith("f") == true) || (value.endsWith("F") == true))
      {
         try
         {
            return this.constantPoolGen.addFloat(Float.parseFloat(value.substring(0, length - 1)));
         }
         catch(final Exception exception)
         {
            throw new CompilerException(lineNumber, "Invalid float !", exception);
         }
      }

      if((value.endsWith("l") == true) || (value.endsWith("L") == true))
      {
         try
         {
            return this.constantPoolGen.addLong(Long.parseLong(value.substring(0, length - 1)));
         }
         catch(final Exception exception)
         {
            throw new CompilerException(lineNumber, "Invalid long !", exception);
         }
      }

      if((value.endsWith("d") == true) || (value.endsWith("D") == true))
      {
         try
         {
            return this.constantPoolGen.addDouble(Double.parseDouble(value.substring(0, length - 1)));
         }
         catch(final Exception exception)
         {
            throw new CompilerException(lineNumber, "Invalid double !", exception);
         }
      }

      if(value.contains(".") == true)
      {
         try
         {
            return this.constantPoolGen.addDouble(Double.parseDouble(value));
         }
         catch(final Exception exception)
         {
            throw new CompilerException(lineNumber, "Invalid double !", exception);
         }
      }

      try
      {
         return this.constantPoolGen.addInteger(Integer.parseInt(value));
      }
      catch(final Exception exception)
      {
         throw new CompilerException(lineNumber, "Invalid int !", exception);
      }
   }

   /**
    * Add an exception throws by current method
    *
    * @param className
    *           Throws exception class
    */
   public void addException(String className)
   {
      className = ((ObjectType) this.stringToType(className)).getClassName();

      if(this.exceptions.contains(className) == false)
      {
         this.exceptions.add(className);
      }
   }

   /**
    * Add field to constant pool
    *
    * @param name
    *           Field name
    * @param typeName
    *           Field type
    * @param lineNumber
    *           Line field declaration
    * @throws CompilerException
    *            If field with same name already exists
    */
   public void addField(final String name, final String typeName, final int lineNumber) throws CompilerException
   {
      final FieldInformation fieldInformation = this.fields.get(name);

      if(fieldInformation != null)
      {
         throw new CompilerException(lineNumber, "Filed with alias/name " + name + " already defined at " + fieldInformation.getLineDeclaration());
      }

      final Type type = this.stringToType(typeName);
      final FieldGen fieldGen = new FieldGen(CompilerConstants.ACCES_FLAGS_FIELD, type, name, this.constantPoolGen);
      this.classGen.addField(fieldGen.getField());
      final int reference = this.constantPoolGen.addFieldref(this.className, name, type.getSignature());
      this.fields.put(name, new FieldInformation(name, type, reference, lineNumber));
   }

   /**
    * Add external field reference to constant pool
    *
    * @param className
    *           Class name where find the field
    * @param typeName
    *           Field type
    * @param name
    *           Filed name in object
    * @param alias
    *           Alias give to the reference
    * @param lineNumber
    *           Declaration line number
    * @throws CompilerException
    *            If field with same name/alias already exists or class name not valid
    */
   public void addFieldReference(final String className, final String typeName, final String name, final String alias, final int lineNumber)
         throws CompilerException
   {
      final FieldInformation fieldInformation = this.fields.get(alias);

      if(fieldInformation != null)
      {
         throw new CompilerException(lineNumber, "Filed with alias/name " + alias + " already defined at " + fieldInformation.getLineDeclaration());
      }

      final Type type = this.stringToType(typeName);
      final Type classReference = this.stringToType(className);

      if((classReference instanceof ObjectType) == false)
      {
         throw new CompilerException(lineNumber, className + " not a class reference !");
      }

      final int reference = this.constantPoolGen.addFieldref(classReference.toString(), name, type.getSignature());
      this.fields.put(alias, new FieldInformation(alias, name, classReference.toString(), type, reference, lineNumber));
   }

   /**
    * Add/get a current method local variable reference
    *
    * @param name
    *           Variable name
    * @param typeName
    *           Variable type
    * @param lineNumber
    *           Line number of declaration/usage
    * @return Current method local variable reference
    * @throws CompilerException
    *            If can add/get the local variable
    */
   public int addGetLocalReference(final String name, final String typeName, final int lineNumber) throws CompilerException
   {
      return this.addGetReference(name, typeName, lineNumber, this.localeVariables);
   }

   /**
    * Add import class
    *
    * @param className
    *           Class to import
    * @param lineNumber
    *           Line number where import declare
    * @throws CompilerException
    *            If import declaration after first field or method
    */
   public void addImport(final String className, final int lineNumber) throws CompilerException
   {
      if(this.classGen != null)
      {
         throw new CompilerException(lineNumber, "import must be declare earlier in file !");
      }

      if(this.imports.contains(className) == false)
      {
         this.imports.add(className);
      }
   }

   /**
    * Add implements interface to class
    *
    * @param className
    *           Implemented interface
    * @param lineNumber
    *           Line declaration number
    * @throws CompilerException
    *            If interface declaration after first field or method
    */
   public void addInterface(String className, final int lineNumber) throws CompilerException
   {
      if(this.classGen != null)
      {
         throw new CompilerException(lineNumber, "interface must be declare earlier in file !");
      }

      className = ((ObjectType) this.stringToType(className)).getClassName();

      if(this.interfaces.contains(className) == false)
      {
         this.interfaces.add(className);
      }
   }

   /**
    * Resolve a label
    *
    * @param label
    *           Label to resolve
    * @param instructionHandle
    *           Label target
    * @param lineNumber
    *           Line where label is declare
    * @throws CompilerException
    *            If label already resolved for current method
    */
   public void addLabel(final String label, final InstructionHandle instructionHandle, final int lineNumber) throws CompilerException
   {
      if(this.labels.containsKey(label) == true)
      {
         throw new CompilerException(lineNumber, "Label " + label + " already defined !");
      }

      this.labels.put(label, instructionHandle);
   }

   /**
    * Add method reference for invoke it.<br>
    * Method reference is compose of <code>&lt;ClassCompleteName&gt;.&lt;methodName&gt;&lt;methodSignature&gt;</code>. Remember
    * that signature is form like : ()V , (I,I)J, (Ljava.lang.String;)V, ...
    *
    * @param completeMethodReference
    *           Method reference
    * @param lineNumber
    *           Line number of declaration
    * @return Method reference
    * @throws CompilerException
    *            If method reference invalid
    */
   public int addMethodReference(final String completeMethodReference, final int lineNumber) throws CompilerException
   {
      final int indexSignature = completeMethodReference.indexOf('(');

      if(indexSignature < 0)
      {
         throw new CompilerException(lineNumber, "No signature !");
      }

      final int indexClassName = completeMethodReference.lastIndexOf('.', indexSignature);

      if(indexClassName < 1)
      {
         throw new CompilerException(lineNumber, "No class name !");
      }

      if((indexSignature - indexClassName) < 2)
      {
         throw new CompilerException(lineNumber, "No method name !");
      }

      return this.addMethodReference(completeMethodReference.substring(0, indexClassName),
            completeMethodReference.substring(indexClassName + 1, indexSignature), completeMethodReference.substring(indexSignature), lineNumber);
   }

   /**
    * Add method reference for invoke it
    *
    * @param className
    *           Class complete name
    * @param method
    *           Method name
    * @param signature
    *           Signature in form like : ()V , (I,I)J, (Ljava.lang.String;)V, ... OR (int, char, String):List
    * @param lineNumber
    *           Line number of declaration
    * @return Method reference
    * @throws CompilerException
    *            If class name not valid or method signature not valid
    */
   public int addMethodReference(final String className, final String method, final String signature, final int lineNumber) throws CompilerException
   {
      final Type type = this.stringToType(className);

      if((type instanceof ObjectType) == false)
      {
         throw new CompilerException(lineNumber, "Not a reference to a class : " + className);
      }

      String goodSignature = signature.replace('.', '/');

      try
      {
         Type.getArgumentTypes(goodSignature);
         Type.getReturnType(goodSignature);
      }
      catch(final Exception exception)
      {
         final Matcher matcher = CompilerContext.PATTERN_SIGNATURE_JAVA.matcher(signature);

         if(matcher.matches() == false)
         {
            throw new CompilerException(lineNumber, "Not valid method signature :\n" + signature);
         }

         Type returnType = Type.VOID;
         final List<Type> parameters = new ArrayList<Type>();
         final int count = matcher.groupCount();

         if(count == 0)
         {
            throw new CompilerException(lineNumber, "Not valid method signature :\n" + signature);
         }

         final String parametersTypeName = matcher.group(1);

         if((parametersTypeName != null) && (parametersTypeName.length() > 0))
         {
            final StringCutter stringCutter = new StringCutter(parametersTypeName, ',');
            String typeName = stringCutter.next();

            while(typeName != null)
            {
               parameters.add(this.stringToType(typeName));
               typeName = stringCutter.next();
            }
         }

         if(count >= 3)
         {
            final String returnTypeName = matcher.group(3);

            if((returnTypeName != null) && (returnTypeName.length() > 0))
            {
               returnType = this.stringToType(returnTypeName);
            }
         }

         goodSignature = Type.getMethodSignature(returnType, parameters.toArray(new Type[parameters.size()]));
      }

      return this.constantPoolGen.addMethodref(type.toString(), method, goodSignature);
   }

   /**
    * Add switch to resolve later
    *
    * @param selectInformation
    *           Switch to add
    */
   public void addSwitch(final SelectInformation selectInformation)
   {
      this.switches.add(selectInformation);
   }

   /**
    * Add/get reference to a type (Class or array)
    *
    * @param typeName
    *           Type name. For arrays use [ notation
    * @param lineNumber
    *           Line declaration/usage
    * @return Reference to type
    * @throws CompilerException
    *            If given type invalid
    */
   public int addTypeReference(final String typeName, final int lineNumber) throws CompilerException
   {
      final Type type = this.stringToType(typeName);

      if(type instanceof ArrayType)
      {
         return this.constantPoolGen.addArrayClass((ArrayType) type);
      }

      if(type instanceof ObjectType)
      {
         return this.constantPoolGen.addClass((ObjectType) type);
      }

      throw new CompilerException(lineNumber, typeName + " not a reference type !");
   }

   /**
    * Check if a local reference have the good type to be use with the instruction
    *
    * @param name
    *           Local reference name
    * @param type
    *           Base type to use with instruction (For not primitive use Type.OBJECT)
    * @param isArray
    *           Indicates if the waiting is an array of base type
    * @param nullAllowed
    *           Indicates if null is allowed as value
    * @param lineNumber
    *           Instruction line number
    * @throws CompilerException
    *            If check failed
    */
   public void checkType(final String name, final Type type, final boolean isArray, final boolean nullAllowed, final int lineNumber) throws CompilerException
   {
      final String realName = type + (isArray == true
            ? "[]"
            : "");
      Type parameterType = this.getLocalReferenceType(name);

      if(parameterType == null)
      {
         throw new CompilerException(lineNumber, "Reference '" + name + "' not found !");
      }

      if(parameterType instanceof ArrayType)
      {
         if(isArray == false)
         {
            throw new CompilerException(lineNumber, "Reference '" + name + "' is a " + parameterType + " not compatible with " + realName);
         }

         parameterType = ((ArrayType) parameterType).getBasicType();
      }
      else if(isArray == true)
      {
         if(parameterType.equals(Type.NULL) == true)
         {
            if(nullAllowed == false)
            {
               throw new CompilerException(lineNumber, "Null value forbidden !");
            }

            return;
         }

         throw new CompilerException(lineNumber, "Reference '" + name + "' is a " + parameterType + " not compatible with " + realName);
      }

      if(parameterType.equals(type) == true)
      {
         return;
      }

      if(parameterType.equals(Type.NULL) == true)
      {
         if(nullAllowed == false)
         {
            throw new CompilerException(lineNumber, "Null value forbidden !");
         }

         if(type == Type.OBJECT)
         {
            return;
         }

         throw new CompilerException(lineNumber, "Reference '" + name + "' is a " + parameterType + " not compatible with " + realName);
      }

      if(parameterType instanceof ObjectType)
      {
         if(type == Type.OBJECT)
         {
            return;
         }

         throw new CompilerException(lineNumber, "Reference '" + name + "' is a " + parameterType + " not compatible with " + realName);
      }

      if((type instanceof BasicType) == false)
      {
         throw new CompilerException(lineNumber, "Reference '" + name + "' is a " + parameterType + " not compatible with " + realName);
      }

      if(isArray == true)
      {
         throw new CompilerException(lineNumber, "Reference '" + name + "' is a " + parameterType + " not compatible with " + realName);
      }

      if(((type == Type.BOOLEAN) || (type == Type.BYTE) || (type == Type.CHAR) || (type == Type.INT) || (type == Type.SHORT))
            && ((parameterType == Type.BOOLEAN) || (parameterType == Type.BYTE) || (parameterType == Type.CHAR) || (parameterType == Type.INT)
                  || (parameterType == Type.SHORT)))
      {
         return;
      }

      throw new CompilerException(lineNumber, "Reference '" + name + "' is a " + parameterType + " not compatible with " + realName);
   }

   /**
    * Get and consume the label to define
    *
    * @return Label to define. {@code null} if no label to define
    */
   public String consumeLabelToDefine()
   {
      final String label = this.labeltoDefine;
      this.labeltoDefine = null;
      return label;
   }

   /**
    * Create the class generator if not already done
    */
   public void createClassGenIfNeed()
   {
      if(this.classGen != null)
      {
         return;
      }

      this.classGen = new ClassGen(this.className, this.parent, null, CompilerConstants.ACCES_FLAGS_CLASS,
            this.interfaces.toArray(new String[this.interfaces.size()]));
      this.classGen.addEmptyConstructor(CompilerConstants.ACCES_FLAGS_METHOD);
      this.constantPoolGen = this.classGen.getConstantPool();
   }

   /**
    * Create and add a method
    *
    * @param returnType
    *           Method return type
    * @param methodName
    *           Method name
    * @param parametersType
    *           Method parameters type
    * @param parametersName
    *           Method parameters name
    * @param instructionList
    *           Method code
    * @param linesTable
    *           Method source code lines
    * @throws CompilerException
    *            On method creation failed
    */
   public void createMethod(final Type returnType, final String methodName, final Type[] parametersType, final String[] parametersName,
         final InstructionList instructionList, final List<Pair<InstructionHandle, Integer>> linesTable) throws CompilerException
   {
      // Resolve branches
      InstructionHandle instructionHandle;

      for(final BranchInformation branchInformation : this.branches)
      {
         instructionHandle = this.labels.get(branchInformation.label);

         if(instructionHandle == null)
         {
            throw new CompilerException(branchInformation.lineNumber, "Undefined label : " + branchInformation.label);
         }

         branchInformation.branchHandle.setTarget(instructionHandle);
      }

      // Resolve switches
      int number;
      String label;

      for(final SelectInformation selectInformation : this.switches)
      {
         number = selectInformation.numberOfCases();

         for(int i = 0; i < number; i++)
         {
            label = selectInformation.getCaseLabel(i);
            instructionHandle = this.labels.get(label);

            if(instructionHandle == null)
            {
               throw new CompilerException(selectInformation.getLineNumber(), label + " not defined !");
            }

            selectInformation.resolveCase(i, instructionHandle);
         }

         label = selectInformation.getDefaultLabel();
         instructionHandle = this.labels.get(label);

         if(instructionHandle == null)
         {
            throw new CompilerException(selectInformation.getLineNumber(), label + " not defined !");
         }

         selectInformation.resolveDefaultLabel(instructionHandle);
      }

      // Create and initialize the method generator
      final MethodGen methodGen = new MethodGen(CompilerConstants.ACCES_FLAGS_METHOD, returnType, parametersType, parametersName, methodName, this.className,
            instructionList, this.constantPoolGen);

      for(final String exception : this.exceptions)
      {
         methodGen.addException(exception);
      }

      // Add local variables
      // 'this' and method parameters are already add by method generator, so have to start to add local variables after them
      // (Declared variables with 'var')
      final InstructionHandle start = instructionList.getStart();
      final InstructionHandle end = instructionList.getEnd();
      final int size = this.localeVariables.size();
      Parameter parameter;

      for(int i = this.indexMarkReference; i < size; i++)
      {
         parameter = this.localeVariables.get(i);
         methodGen.addLocalVariable(parameter.getName(), parameter.getType(), start, end);
      }

      // Add line code reference
      for(final Pair<InstructionHandle, Integer> line : linesTable)
      {
         methodGen.addLineNumber(line.element1, line.element2);
      }

      final StackInspector stackInspector = new StackInspector(instructionList, linesTable);
      stackInspector.checkStack(this.constantPoolGen);

      // Create and add the method to the class
      this.constantPoolGen.addMethodref(methodGen);
      methodGen.setMaxLocals();
      methodGen.setMaxStack();
      this.classGen.addMethod(methodGen.getMethod());
   }

   /**
    * Create a push constant instruction
    *
    * @param value
    *           Serialized constant value to push
    * @param lineNumber
    *           Instruction line number
    * @return Created push
    * @throws CompilerException
    *            If value not a valid constant
    */
   public com.sun.org.apache.bcel.internal.generic.PUSH createPush(final String value, final int lineNumber) throws CompilerException
   {
      final int length = value.length();

      if(length > 1)
      {
         if((value.charAt(0) == '"') && (value.charAt(length - 1) == '"'))
         {
            return new com.sun.org.apache.bcel.internal.generic.PUSH(this.constantPoolGen, value.substring(1, length - 1));
         }

         if(length > 2)
         {
            if((value.charAt(0) == '\'') && (value.charAt(length - 1) == '\''))
            {
               final String character = UtilText.interpretAntiSlash(value.substring(1, length - 1));

               if(character.length() != 1)
               {
                  throw new CompilerException(lineNumber, "Invalid character !");
               }

               return new com.sun.org.apache.bcel.internal.generic.PUSH(this.constantPoolGen, 0xFFFF & character.charAt(0));
            }
         }
      }

      if("true".equals(value) == true)
      {
         return new com.sun.org.apache.bcel.internal.generic.PUSH(this.constantPoolGen, true);
      }

      if("false".equals(value) == true)
      {
         return new com.sun.org.apache.bcel.internal.generic.PUSH(this.constantPoolGen, false);
      }

      if((value.endsWith("f") == true) || (value.endsWith("F") == true))
      {
         try
         {
            return new com.sun.org.apache.bcel.internal.generic.PUSH(this.constantPoolGen, Float.parseFloat(value.substring(0, length - 1)));
         }
         catch(final Exception exception)
         {
            throw new CompilerException(lineNumber, "Invalid float !", exception);
         }
      }

      if((value.endsWith("l") == true) || (value.endsWith("L") == true))
      {
         try
         {
            return new com.sun.org.apache.bcel.internal.generic.PUSH(this.constantPoolGen, Long.parseLong(value.substring(0, length - 1)));
         }
         catch(final Exception exception)
         {
            throw new CompilerException(lineNumber, "Invalid long !", exception);
         }
      }

      if((value.endsWith("d") == true) || (value.endsWith("D") == true))
      {
         try
         {
            return new com.sun.org.apache.bcel.internal.generic.PUSH(this.constantPoolGen, Double.parseDouble(value.substring(0, length - 1)));
         }
         catch(final Exception exception)
         {
            throw new CompilerException(lineNumber, "Invalid double !", exception);
         }
      }

      if(value.contains(".") == true)
      {
         try
         {
            return new com.sun.org.apache.bcel.internal.generic.PUSH(this.constantPoolGen, Double.parseDouble(value));
         }
         catch(final Exception exception)
         {
            throw new CompilerException(lineNumber, "Invalid double !", exception);
         }
      }

      try
      {
         return new com.sun.org.apache.bcel.internal.generic.PUSH(this.constantPoolGen, Integer.parseInt(value));
      }
      catch(final Exception exception)
      {
         throw new CompilerException(lineNumber, "Invalid int !", exception);
      }
   }

   /**
    * Class generator
    *
    * @return Class generator
    */
   public ClassGen getClassGen()
   {
      return this.classGen;
   }

   /**
    * Class name
    *
    * @return Class name
    */
   public String getClassName()
   {
      return this.className;
   }

   /**
    * Obtain a field
    *
    * @param fieldName
    *           Filed name
    * @param lineNumber
    *           Line number where field called
    * @return Field
    * @throws CompilerException
    *            If field not exists
    */
   public FieldInformation getField(final String fieldName, final int lineNumber) throws CompilerException
   {
      final FieldInformation fieldInformation = this.fields.get(fieldName);

      if(fieldInformation == null)
      {
         throw new CompilerException(lineNumber, "Field " + fieldName + " not defined !");
      }

      return fieldInformation;
   }

   /**
    * Obtain a local variable in the current method
    *
    * @param name
    *           Variable name
    * @param lineNumber
    *           Line number where variable get
    * @return The reference
    * @throws CompilerException
    *            If local variable not exists
    */
   public int getLocalReference(final String name, final int lineNumber) throws CompilerException
   {
      return this.addGetReference(name, null, lineNumber, this.localeVariables);
   }

   /**
    * Obtain the type of a local reference
    *
    * @param name
    *           Local reference name
    * @return Local reference type OR null if local reference not found
    */
   public Type getLocalReferenceType(final String name)
   {
      final int size = this.localeVariables.size();

      for(int i = 0; i < size; i++)
      {
         if(name.equals(this.localeVariables.get(i).getName()) == true)
         {
            return this.localeVariables.get(i).getType();
         }
      }

      return null;
   }

   /**
    * Initialize the context to be ready to compile a new class
    */
   public void initialize()
   {
      this.parent = "java.lang.Object";
      this.className = null;
      this.packageName = null;
      this.classGen = null;
      this.constantPoolGen = null;
      this.fields.clear();
      this.imports.clear();
      this.localeVariables.clear();
      this.types.clear();
      this.interfaces.clear();
      this.branches.clear();
      this.labels.clear();
      this.exceptions.clear();
      this.switches.clear();
   }

   /**
    * Initialize context for create a new method inside current class
    *
    * @param addThis
    *           Indicates if add this reference (Non static method put it at true. Static method put it at false)
    * @param lineNumber
    *           Line number of start method declaration
    * @throws CompilerException
    *            If add 'this' failed
    */
   public void initializeForMethod(final boolean addThis, final int lineNumber) throws CompilerException
   {
      this.localeVariables.clear();
      this.branches.clear();
      this.labels.clear();
      this.exceptions.clear();
      this.switches.clear();

      if(addThis == true)
      {
         this.addGetLocalReference("this", this.className, lineNumber);
      }
   }

   /**
    * Mark index in local reference as the start of real local references (Not 'thsi' or method parameter)
    */
   public void markStartReference()
   {
      this.indexMarkReference = this.localeVariables.size();
   }

   /**
    * Define class name
    *
    * @param className
    *           Class name
    * @param lineNumber
    *           Class name line number
    * @throws CompilerException
    *            If class name already defined
    */
   public void setClassName(final String className, final int lineNumber) throws CompilerException
   {
      if(this.className != null)
      {
         throw new CompilerException(lineNumber, "Class name already defined !");
      }

      this.className = ((ObjectType) this.stringToType(className)).getClassName();

      final int index = this.className.lastIndexOf('.');

      if(index >= 0)
      {
         this.packageName = this.className.substring(0, index);
      }
      else
      {
         this.packageName = "";
      }
   }

   /**
    * Define the next label to resolve
    *
    * @param labeltoDefine
    *           Label to resolve (Use {@code null} for initialize)
    * @param lineNumber
    *           Line number declaration
    * @throws CompilerException
    *            If a label waiting to be resolve (Not already consumed by {@link #consumeLabelToDefine()})
    */
   public void setLabelToDefine(final String labeltoDefine, final int lineNumber) throws CompilerException
   {
      if((this.labeltoDefine != null) && (labeltoDefine != null))
      {
         throw new CompilerException(lineNumber, "Can't define 2 followings labels without at least one real opcode instruction between them ! ");
      }

      this.labeltoDefine = labeltoDefine;
   }

   /**
    * Define parent class
    *
    * @param parent
    *           Parent class name
    * @param lineNumber
    *           Line number declaration
    * @throws CompilerException
    *            If extends after first filed or first method declaration
    */
   public void setParent(final String parent, final int lineNumber) throws CompilerException
   {
      if(this.classGen != null)
      {
         throw new CompilerException(lineNumber, "extends must be declare earlier in file !");
      }

      this.parent = ((ObjectType) this.stringToType(parent)).getClassName();
   }

   /**
    * Convert a String to Type.<br>
    * It resolve primitive types (int, boolean, ...), class complete name type (java.lang.String, jhelp.util.math.UtilMath,
    * ...), signature type (Ljava/lang/String;, [I, ...). If name is single word (String, StringBulider, UtilMath, ...), it
    * search inside imports, if not found search in "java.lang" package and if not found it considers on same package that the
    * class itself.
    *
    * @param string
    *           String to convert
    * @return Converted type
    */
   public Type stringToType(final String string)
   {
      // Test if it is a primitive or well known type
      if(Reflector.PRIMITIVE_BOOLEAN.equals(string) == true)
      {
         return Type.BOOLEAN;
      }

      if(Reflector.PRIMITIVE_BYTE.equals(string) == true)
      {
         return Type.BYTE;
      }

      if(Reflector.PRIMITIVE_CHAR.equals(string) == true)
      {
         return Type.CHAR;
      }

      if(Reflector.PRIMITIVE_DOUBLE.equals(string) == true)
      {
         return Type.DOUBLE;
      }

      if(Reflector.PRIMITIVE_FLOAT.equals(string) == true)
      {
         return Type.FLOAT;
      }

      if(Reflector.PRIMITIVE_INT.equals(string) == true)
      {
         return Type.INT;
      }

      if(Reflector.PRIMITIVE_LONG.equals(string) == true)
      {
         return Type.LONG;
      }

      if(Reflector.PRIMITIVE_SHORT.equals(string) == true)
      {
         return Type.SHORT;
      }

      if("null".equals(string) == true)
      {
         return Type.NULL;
      }

      if("void".equals(string) == true)
      {
         return Type.VOID;
      }

      if(("Object".equals(string) == true) || ("java.lang.Object".equals(string) == true))
      {
         return Type.OBJECT;
      }

      if(("String".equals(string) == true) || ("java.lang.String".equals(string) == true))
      {
         return Type.STRING;
      }

      if(("StringBuffer".equals(string) == true) || ("java.lang.StringBuffer".equals(string) == true))
      {
         return Type.STRINGBUFFER;
      }

      if(("Throwable".equals(string) == true) || ("java.lang.Throwable".equals(string) == true))
      {
         return Type.THROWABLE;
      }

      // Get type form resolved types
      Type type = this.types.get(string);

      // If type not already know
      if(type == null)
      {
         if((string.startsWith("[") == true) || (string.startsWith("L") == true))
         {
            // signature type
            type = Type.getType(string);
            this.types.put(string, type);
         }
         else
         {
            String search;

            if(string.indexOf('.') < 0)
            {
               // Type is a single word
               search = null;

               // Look in imports
               for(final String imported : this.imports)
               {
                  if(imported.endsWith(string) == true)
                  {
                     search = imported;
                     break;
                  }
               }

               if(search == null)
               {
                  if(UtilCompiler.isJavaLangClass(string) == true)
                  {
                     // If inside java.lang package
                     search = "java.lang." + string;
                  }
                  else if((this.packageName != null) && (this.packageName.length() > 0))
                  {
                     // Inside same package
                     search = UtilText.concatenate(this.packageName, '.', string);
                  }
                  else
                  {
                     // No package, consider default package
                     search = string;
                  }
               }
            }
            else
            {
               // Type is complete name
               search = string;
            }

            type = this.types.get(search);

            if(type == null)
            {
               type = new ObjectType(search);
               this.types.put(search, type);
            }

            this.types.put(string, type);
         }
      }

      return type;
   }
}