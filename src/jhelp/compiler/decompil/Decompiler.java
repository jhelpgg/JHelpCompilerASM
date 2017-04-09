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
package jhelp.compiler.decompil;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.classfile.Attribute;
import com.sun.org.apache.bcel.internal.classfile.ClassFormatException;
import com.sun.org.apache.bcel.internal.classfile.ClassParser;
import com.sun.org.apache.bcel.internal.classfile.Code;
import com.sun.org.apache.bcel.internal.classfile.Constant;
import com.sun.org.apache.bcel.internal.classfile.ConstantClass;
import com.sun.org.apache.bcel.internal.classfile.ConstantFieldref;
import com.sun.org.apache.bcel.internal.classfile.ConstantNameAndType;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import com.sun.org.apache.bcel.internal.classfile.LocalVariable;
import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.generic.Type;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import jhelp.compiler.compil.CompilerConstants;
import jhelp.util.text.UtilText;

/**
 * Decompiler of ".class" file.<br>
 * Work great with code compiled with {@link jhelp.compiler.compil.Compiler}, may not work perfectly with other sources.
 *
 * @author JHelp <br>
 */
public class Decompiler
        implements CompilerConstants
{
    /**
     * Create a new instance of Decompiler
     */
    public Decompiler()
    {
    }

    /**
     * Decompile a ".class"
     *
     * @param inputStream  ".class" stream source
     * @param outputStream Stream where write the parsed result
     * @throws ClassFormatException If ".class" not valid or not supported
     * @throws IOException          On read/write issue
     */
    public void decompile(final InputStream inputStream, final OutputStream outputStream)
            throws ClassFormatException, IOException
    {
        this.decompile(inputStream, null, outputStream);
    }

    /**
     * Decompile a ".class"
     *
     * @param inputStream  ".class" stream source
     * @param fileName     ".class" files source name (Can be {@code null} if unknown)
     * @param outputStream Stream where write the parsed result
     * @throws ClassFormatException If ".class" not valid or not supported
     * @throws IOException          On read/write issue
     */
    public void decompile(final InputStream inputStream, String fileName, final OutputStream outputStream)
            throws ClassFormatException, IOException
    {
        // Initialize
        final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        final ClassParser    classParser    = new ClassParser(inputStream, fileName);
        final JavaClass      javaClass      = classParser.parse();
        final String         className      = javaClass.getClassName();
        final String         packageName    = javaClass.getPackageName();
        final CodeParser     codeParser     = new CodeParser();

        // Header
        bufferedWriter.write("/*\n * Decompiled by jhelp.compiler.decompil.Decompiler\n");

        if ((fileName != null) && (fileName.length() > 0))
        {
            bufferedWriter.write(" * File (given) : ");
            bufferedWriter.write(fileName);
            bufferedWriter.newLine();
        }

        fileName = javaClass.getFileName();

        if ((fileName != null) && (fileName.length() > 0))
        {
            bufferedWriter.write(" * File (read) : ");
            bufferedWriter.write(fileName);
            bufferedWriter.newLine();
        }

        fileName = javaClass.getSourceFileName();

        if ((fileName != null) && (fileName.length() > 0))
        {
            bufferedWriter.write(" * File (source) : ");
            bufferedWriter.write(fileName);
            bufferedWriter.newLine();
        }

        bufferedWriter.write(" * Class : ");
        bufferedWriter.write(className);
        bufferedWriter.newLine();
        bufferedWriter.write(" *");
        bufferedWriter.newLine();
        bufferedWriter.write(" * Have fun ;)\n * JHelp\n */");
        bufferedWriter.newLine();
        // Class name
        bufferedWriter.write("class ");
        bufferedWriter.write(className);
        bufferedWriter.newLine();
        bufferedWriter.newLine();

        // Collect external fields reference and write imports
        final List<String> fieldsReferences = new ArrayList<String>();
        final ConstantPool constantPool     = javaClass.getConstantPool();
        String             name;

        for (final Constant constant : constantPool.getConstantPool())
        {
            if (constant == null)
            {
                // Debug.println(DebugLevel.WARNING, "Some constants may be bull !");
                continue;
            }

            switch (constant.getTag())
            {
                case Constants.CONSTANT_Class:
                    name = ((ConstantClass) constant).getBytes(constantPool)
                                                     .replace('/', '.');

                    if ((!Decompiler.insidePackage(name, packageName)) && (!Decompiler.isJavaLang(
                            name)) && (!name.startsWith(
                            "["))
                            && ((!name.startsWith("L")) || (!name.endsWith(";"))))
                    {
                        bufferedWriter.write("import ");
                        bufferedWriter.write(name);
                        bufferedWriter.newLine();
                    }

                    break;
                case Constants.CONSTANT_Fieldref:
                    final ConstantFieldref constantFieldref = (ConstantFieldref) constant;
                    final ConstantNameAndType constantNameAndType = (ConstantNameAndType) constantPool.getConstant(
                            constantFieldref.getNameAndTypeIndex());
                    String fieldClass = constantFieldref.getClass(constantPool);

                    if (!className.equals(fieldClass))
                    {
                        fieldClass = Decompiler.shortName(fieldClass);
                        final String fieldName = constantNameAndType.getName(constantPool);
                        final Type   type      = Type.getType(constantNameAndType.getSignature(constantPool));
                        fieldsReferences.add(UtilText.concatenate("field_reference ", fieldClass, " ",
                                                                  Decompiler.shortName(type.toString()), " ", fieldName,
                                                                  " ",
                                                                  fieldClass, ".", fieldName));
                    }

                    break;
            }
        }

        bufferedWriter.newLine();

        // extends
        name = javaClass.getSuperclassName();

        if (!"java.lang.Object".equals(name))
        {
            bufferedWriter.write("extends ");
            bufferedWriter.write(Decompiler.shortName(javaClass.getSuperclassName()));
            bufferedWriter.newLine();
            bufferedWriter.newLine();
        }

        // Implemented interfaces
        final String[] interfaces = javaClass.getInterfaceNames();

        if ((interfaces != null) && (interfaces.length > 0))
        {
            for (final String interfaceName : interfaces)
            {
                bufferedWriter.write("implements ");
                bufferedWriter.write(Decompiler.shortName(interfaceName));
                bufferedWriter.newLine();
            }

            bufferedWriter.newLine();
        }

        // Class fields
        final Field[] fields = javaClass.getFields();
        int           access;
        boolean       isPackage;

        if ((fields != null) && (fields.length > 0))
        {
            for (final Field field : fields)
            {
                bufferedWriter.write("field ");
                bufferedWriter.write(Decompiler.shortName(field.getType()
                                                               .toString()));
                bufferedWriter.write(" ");
                bufferedWriter.write(field.getName());
                access = field.getAccessFlags();

                if (access != CompilerConstants.ACCES_FLAGS_FIELD)
                {
                    isPackage = true;

                    if ((access & Constants.ACC_PUBLIC) != 0)
                    {
                        bufferedWriter.write(" public");
                        isPackage = false;
                    }

                    if ((access & Constants.ACC_PRIVATE) != 0)
                    {
                        bufferedWriter.write(" private");
                        isPackage = false;
                    }

                    if ((access & Constants.ACC_PROTECTED) != 0)
                    {
                        bufferedWriter.write(" protected");
                        isPackage = false;
                    }

                    if (isPackage)
                    {
                        bufferedWriter.write(" package");
                    }

                    if ((access & Constants.ACC_STATIC) != 0)
                    {
                        bufferedWriter.write(" static");
                    }
                }

                bufferedWriter.newLine();
            }

            bufferedWriter.newLine();
        }

        // External fields reference
        if (fieldsReferences.size() > 0)
        {
            for (final String fieldsReference : fieldsReferences)
            {
                bufferedWriter.write(fieldsReference);
                bufferedWriter.newLine();
            }

            bufferedWriter.newLine();
        }

        // Write methods
        boolean isConstructor;
        boolean signalDuplicateLocalVariables;

        for (final Method method : javaClass.getMethods())
        {
            // Declaration
            isConstructor = "<init>".equals(method.getName());

            if (isConstructor)
            {
                bufferedWriter.write("//\n// Constructors are ignored for moment\n//\n");
                bufferedWriter.write("// ");
            }

            bufferedWriter.write("method ");
            bufferedWriter.write(method.getName());
            access = method.getAccessFlags();
            int start = 1;

            if (access != CompilerConstants.ACCES_FLAGS_METHOD)
            {
                isPackage = true;

                if ((access & Constants.ACC_PUBLIC) != 0)
                {
                    bufferedWriter.write(" public");
                    isPackage = false;
                }

                if ((access & Constants.ACC_PRIVATE) != 0)
                {
                    bufferedWriter.write(" private");
                    isPackage = false;
                }

                if ((access & Constants.ACC_PROTECTED) != 0)
                {
                    bufferedWriter.write(" private");
                    isPackage = false;
                }

                if (isPackage)
                {
                    bufferedWriter.write(" package");
                }

                if ((access & Constants.ACC_STATIC) != 0)
                {
                    bufferedWriter.write(" static");
                    start = 0;
                }
            }

            bufferedWriter.newLine();

            // Initialization
            final TreeSet<String> already             = new TreeSet<String>();
            final Type[]          types               = method.getArgumentTypes();
            final int             length              = types.length;
            int                   number;
            String                parameterName;
            String                baseName;
            final LocalVariable[] localVariableTable  = method.getLocalVariableTable()
                                                              .getLocalVariableTable();
            final int             tableLength         = localVariableTable.length;
            final String[]        localeVariablesName = new String[method.getLocalVariableTable()
                                                                         .getLength()];
            int                   indexName           = 0;
            Type                  type;

            if (start == 1)
            {
                localeVariablesName[indexName++] = "this";
            }

            // Parameters
            for (int i = 0; i < length; i++)
            {
                if (isConstructor)
                {
                    bufferedWriter.write("// ");
                }

                bufferedWriter.write("\tparameter ");
                type = types[i];
                bufferedWriter.write(Decompiler.shortName(type.toString()));
                bufferedWriter.write(" ");
                parameterName = localVariableTable[i + start].getName();
                already.add(parameterName);
                localeVariablesName[indexName++] = parameterName;

                if ((Type.LONG.equals(type)) || (Type.DOUBLE.equals(type)))
                {
                    // Long and double take two places
                    localeVariablesName[indexName++] = parameterName;
                }

                bufferedWriter.write(parameterName);
                bufferedWriter.newLine();
            }

            // Return value
            type = method.getReturnType();

            if (!Type.VOID.equals(type))
            {
                if (isConstructor)
                {
                    bufferedWriter.write("// ");
                }

                bufferedWriter.write("\treturn ");
                bufferedWriter.write(Decompiler.shortName(type.toString()));
                bufferedWriter.newLine();
            }

            // Exception thrown
            if (method.getExceptionTable() != null)
            {
                for (final String exceptionName : method.getExceptionTable()
                                                        .getExceptionNames())
                {
                    if (isConstructor)
                    {
                        bufferedWriter.write("// ");
                    }

                    bufferedWriter.write("\tthrows ");
                    bufferedWriter.write(Decompiler.shortName(exceptionName));
                    bufferedWriter.newLine();
                }
            }

            if (isConstructor)
            {
                bufferedWriter.write("// ");
            }

            // Method code
            bufferedWriter.write("{");
            bufferedWriter.newLine();

            // Locale variables
            LocalVariable localVariable;

            for (int i = length + start; i < tableLength; i++)
            {
                localVariable = localVariableTable[i];

                if (localVariable == null)
                {
                    continue;
                }

                if (isConstructor)
                {
                    bufferedWriter.write("// ");
                }

                bufferedWriter.write("\tVAR ");
                type = Type.getType(localVariable.getSignature());
                bufferedWriter.write(Decompiler.shortName(type.toString()));
                bufferedWriter.write(" ");

                parameterName = localVariable.getName();
                number = 0;
                baseName = parameterName;
                signalDuplicateLocalVariables = true;

                while (!already.add(parameterName))
                {
                    if (signalDuplicateLocalVariables)
                    {
                        bufferedWriter.write(" /* Duplicate variable locale name are not well managed for now */ ");
                    }

                    signalDuplicateLocalVariables = false;
                    parameterName = baseName + number;
                    number++;
                }

                localeVariablesName[indexName++] = parameterName;

                if ((Type.LONG.equals(type)) || (Type.DOUBLE.equals(type)))
                {
                    // Longs and doubles take two places
                    localeVariablesName[indexName++] = parameterName;
                }

                bufferedWriter.write(parameterName);
                bufferedWriter.newLine();
            }

            // Obtain the real code
            Code code = null;

            for (final Attribute attribute : method.getAttributes())
            {
                if (attribute.getTag() == Constants.ATTR_CODE)
                {
                    code = (Code) attribute;
                    break;
                }
            }

            if (code != null)
            {
                for (final String line : codeParser.parse(className, constantPool, localeVariablesName, code))
                {
                    if (line.length() > 0)
                    {
                        if (line.startsWith("LABEL"))
                        {
                            if (isConstructor)
                            {
                                bufferedWriter.write("// ");
                            }

                            bufferedWriter.newLine();
                        }

                        if (isConstructor)
                        {
                            bufferedWriter.write("// ");
                        }

                        bufferedWriter.write("\t");
                        bufferedWriter.write(line);
                        bufferedWriter.newLine();
                    }
                }
            }

            if (isConstructor)
            {
                bufferedWriter.write("// ");
            }

            bufferedWriter.write("}");
            bufferedWriter.newLine();

            if (isConstructor)
            {
                bufferedWriter.write("//\n// ----\n//\n");
            }

            bufferedWriter.newLine();
        }

        bufferedWriter.flush();
    }

    /**
     * Indicates if given class inside given package
     *
     * @param className   Complete name class to test
     * @param packageName Package where class suppose to be
     * @return {@code true} if given class inside given package
     */
    private static boolean insidePackage(final String className, final String packageName)
    {
        if (!className.startsWith(packageName))
        {
            return false;
        }

        final int start = packageName.length();

        if (className.length() <= start)
        {
            return false;
        }

        if (className.charAt(start) != '.')
        {
            return false;
        }

        return className.indexOf('.', start + 1) < 0;
    }

    /**
     * Indicates if class name is inside "java.lang" package
     *
     * @param className Class complete name
     * @return {@code true} if class name is inside "java.lang" package
     */
    private static boolean isJavaLang(final String className)
    {
        return Decompiler.insidePackage(className, "java.lang");
    }

    /**
     * Class short name
     *
     * @param className Class complete name
     * @return Class short name
     */
    static String shortName(final String className)
    {
        final int index = className.lastIndexOf('.');

        if (index < 0)
        {
            return className;
        }

        return className.substring(index + 1);
    }
}