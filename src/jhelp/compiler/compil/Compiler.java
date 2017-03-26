package jhelp.compiler.compil;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.generic.ClassGen;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import jhelp.util.text.StringExtractor;
import jhelp.util.text.UtilText;

/**
 * Compiler of ASM files.<br>
 * ASM file represents one and only one class (Don't accept any inner class)<br>
 * Their only one instruction per line<br>
 * Blank lines are ignored, 2 types of comments are supported. If comment is the only thing of the line, the line can be
 * started
 * with // or ;. If comment after an instruction, ; is used for separate instruction to comment. Their no way to put
 * comment
 * before instruction in same line.<br>
 * Since blank lines and comments lines are ignored, they can be put any where<br>
 * The first real instruction MUST be the class declaration:<code>class &lt;ClassCompleteName&gt;</code>, the complete name
 * means with the package specification like "java.lang.String", "jhelp.util.math.UtilMath", ... It is highly
 * discouraged to use
 * empty/default package (It will compile, but you may have some difficulty to use the class later).<br>
 * After class declaration you can add reference (import/implements/extends) block, then follow work (fields/methods)
 * block.
 * Instruction in reference block MUST be before any work block.<br>
 * It is possible to use any order inside reference block or inside work block. But remember the file is parse from top to
 * bottom, the compiler didn't know a reference until it didn't read it, so if you implements an interface on using the
 * short
 * way, be sure the corresponding import is doing before. If you talk about filed in code, be sure it declares before the
 * method. Thats why we recommend the following order : class, imports, extends, interfaces, fields, methods.<br>
 * The format accept several extends, but only the last have an effect, for more clarity, we recommends to use only one<br>
 * ASM file grammar is : <code>
 * <pre>
 * ASM                 := &lt;IgnoredLine&gt;* &lt;ClassDecraration&gt; &lt;IgnoredLine&gt;* &lt;DeclarationBlock&gt;
 * &lt;IgnoredLine&gt;* &lt;WorkBlock&gt; &lt;IgnoredLine&gt;*
 * IgnoredLine         := &lt;Space&gt;* '//'|';'  [^\n]* '\n'
 * Space               := ' '|'\t'
 * ClassDecraration    := &lt;Space&gt;* class &lt;Space&gt;+ &lt;ClassCompleteName&gt; &lt;Space&gt;* &lt;
 * FollingComments&gt;? '\n'
 * ClassCompleteName   := &lt;PackageName&gt; &lt;ClassName&gt;
 * PackageName         := ''|[a-zA-Z][a-zA-Z0-9_.]*.
 * ClassName           := [a-zA-Z][a-zA-Z0-9_]*
 * FollingComments     := ; [^\n]* '\n'
 * DeclarationBlock    := ((&lt;Space&gt;* &lt;Import&gt;|&lt;Extends&gt;|&lt;Implements&gt; &lt;Space&gt;* &lt;
 * FollingComments&gt;? '\n')|&lt;IgnoredLine&gt;)*
 * Import              := import &lt;Space&gt;+ &lt;ClassCompleteName&gt;
 * Extends             := extends &lt;Space&gt;+ &lt;ClassCompleteName&gt;|&lt;ClassName&gt;
 * Implements          := implements &lt;Space&gt;+ &lt;ClassCompleteName&gt;|&lt;ClassName&gt;
 * WorkBlock           := (&lt;Field&gt;|&lt;Method&gt;|&lt;IgnoredLine&gt;)*
 * Field               := &lt;Space&gt;* field &lt;Space&gt;+ &lt;Type&gt; &lt;Space&gt;+ &lt;Name&gt;  &lt;Space&gt;*
 * &lt;FollingComments&gt;? '\n'
 * Type                := &lt;Primitive&gt;|&lt;ClassCompleteName&gt;|&lt;ClassName&gt;|&lt;SignatureType&gt;
 * Primitive           := boolean|char|byte|short|int|long|float|double
 * SignatureType       := &lt;SimpleSignatureType&gt;|&lt;ArraySignatureType&gt;
 * SimpleSignatureType := 'L' [a-zA-Z][a-zA-Z0-9_/.]* ';'
 * ArraySignatureType  := '[' &lt;SignatureType&gt;|&lt;SignaturePrimitive&gt;
 * SignaturePrimitive  := Z|C|B|S|I|J|F|D
 * Name                := [a-zA-Z][a-zA-Z0-9_]*
 * Method              := &lt;Space&gt;* method &lt;Space&gt;+ &lt;Name&gt; &lt;Space&gt;* &lt;FollingComments&gt;? '\n'
 *                        &lt;IgnoredLine&gt;*
 *                        (&lt;Space&gt;* parameter &lt;Space&gt;+ &lt;Type&gt; &lt;Space&gt;+ &lt;Name&gt; &lt;
 *                        Space&gt;* &lt;FollingComments&gt;? '\n')|&lt;IgnoredLine&gt;*
 *                        &lt;IgnoredLine&gt;*
 *                        (&lt;Space&gt;* return &lt;Space&gt;+ &lt;Type&gt;  &lt;Space&gt;* &lt;FollingComments&gt;?
 *                        '\n')|&lt;IgnoredLine&gt; ?
 *                        &lt;IgnoredLine&gt;*
 *                        &lt;Space&gt;* '{'  &lt;Space&gt;* &lt;FollingComments&gt;? '\n'
 *                        (&lt;Space&gt;* &lt;CodeInstruction&gt; &lt;Space&gt;* &lt;FollingComments&gt;? '\n')|&lt;
 *                        IgnoredLine&gt; ?
 *                        &lt;Space&gt;* '}'  &lt;Space&gt;* &lt;FollingComments&gt;? '\n'
 * CodeInstruction     := &lt;InstructionCode&gt; (&lt;Space&gt;+ &lt;Parameter&gt;)*
 * InstructionCode     := {See {@link OpcodeConstants} for a list of opcodes and their details}
 * Parameter           := {See {@link OpcodeConstants} for a list of opcodes and their parameters}
 * </pre>
 * </code> Note : {@link Compiler} is not thread safe, it can compile several class one after other. But if you want
 * compile
 * several class in parallel, you have to use a different instance of compiler on each thread.
 *
 * @author JHelp <br>
 */
public class Compiler
        implements CompilerConstants
{
    /**
     * Pattern use to know if a string is a signature
     */
    private static final Pattern PATTERN_SIGNATURE = Pattern.compile("L[a-zA-Z][a-zA-Z0-9_/.]*;");
    /**
     * Compiler context
     */
    private final CompilerContext compilerContext;
    /**
     * Definition block intervals
     */
    private       Intervals       intervals;
    /**
     * Current method
     */
    private MethodDescription methodDescription;
    /**
     * Line where current method code start
     */
    private int               startBlockLineNumber;
    /**
     * Create a new instance of Compiler
     */
    public Compiler()
    {
        this.compilerContext = new CompilerContext();
    }

    /**
     * Compile a stream on ASM file format.<br>
     * Given stream is not close have to close it your self
     *
     * @param inputStream Stream on ASM file format
     * @return Compiled class
     * @throws CompilerException On compilation issue
     */
    public ClassGen compile(final InputStream inputStream) throws CompilerException
    {
        this.compilerContext.initialize();
        this.methodDescription = null;
        this.startBlockLineNumber = -1;
        int mutlineCommentStart = -1;
        int lineNumber          = 0;

        try
        {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String               line           = bufferedReader.readLine();
            int                  index, index1;

            while (line != null)
            {
                lineNumber++;
                index = UtilText.indexOfIgnoreString(line, ';');

                if (index > 0)
                {
                    index1 = line.lastIndexOf('L', index);

                    while ((index > 0) && (index1 > 0) && (this.lookSignature(line, index1, index)))
                    {
                        index = UtilText.indexOfIgnoreString(line, ';', index + 1);

                        if (index > 0)
                        {
                            index1 = line.lastIndexOf('L', index);
                        }
                    }

                    if (index > 0)
                    {
                        line = line.substring(0, index);
                    }
                }

                if (mutlineCommentStart < 0)
                {
                    mutlineCommentStart = UtilText.indexOfIgnoreStrings(line, "/*", 0, UtilText.DEFAULT_STRING_LIMITERS);
                }

                while (mutlineCommentStart >= 0)
                {
                    index = UtilText.indexOfIgnoreStrings(line, "*/", mutlineCommentStart,
                                                          UtilText.DEFAULT_STRING_LIMITERS);

                    if (index >= 0)
                    {
                        line = UtilText.concatenate(line.substring(0, mutlineCommentStart), line.substring(index + 2));
                        mutlineCommentStart = UtilText.indexOfIgnoreStrings(line, "/*", 0,
                                                                            UtilText.DEFAULT_STRING_LIMITERS);
                    }
                    else
                    {
                        line = line.substring(0, mutlineCommentStart);
                        mutlineCommentStart = 0;
                        break;
                    }
                }

                line = line.trim();

                if ((line.length() > 0) && (!line.startsWith("//")))
                {
                    this.parseLine(Compiler.removeWhiteSpaceBetweenParenthesis(line), lineNumber);
                }

                line = bufferedReader.readLine();
            }

            return this.compilerContext.getClassGen();
        }
        catch (final CompilerException exception)
        {
            throw exception;
        }
        catch (final Exception exception)
        {
            throw new CompilerException(lineNumber, "Failed to compile !", exception);
        }
    }

    /**
     * Remove white spaces are between parenthesis to easy allow signature type (int, char, ..):boolean with spaces/tabs
     * before/after the comma
     *
     * @param string String to remove space between parenthesis
     * @return String cleared
     */
    private static String removeWhiteSpaceBetweenParenthesis(final String string)
    {
        final char[] source      = string.toCharArray();
        final int    length      = source.length;
        final char[] result      = new char[length];
        int          size        = 0;
        int          parenthesis = 0;

        for (char character : source)
        {
            switch (character)
            {
                case '(':
                    parenthesis++;
                    result[size] = character;
                    size++;
                    break;
                case ')':
                    parenthesis--;
                    result[size] = character;
                    size++;
                    break;
                default:
                    if (parenthesis > 0)
                    {
                        if (character > ' ')
                        {
                            result[size] = character;
                            size++;
                        }
                    }
                    else
                    {
                        result[size] = character;
                        size++;
                    }
                    break;
            }
        }

        return new String(result, 0, size);
    }

    /**
     * Indicates if substring of given string is a signature type
     *
     * @param line  String where get sub string
     * @param start Offset where start substring
     * @param end   Offset where end substring
     * @return {@code true} if substring of given string is a signature type
     */
    private boolean lookSignature(final String line, final int start, final int end)
    {
        return Compiler.PATTERN_SIGNATURE.matcher(line.substring(start, end + 1))
                                         .matches();
    }

    /**
     * Parse a line.<br>
     * Line is sure : not be a comment, not blank, following comments removed and trim
     *
     * @param line       Line
     * @param lineNumber Line number in code
     * @throws CompilerException If line not valid instruction, instruction meet at unexpected place or context can't
     * resolve something need for
     *                           instruction at this moment
     */
    private void parseLine(final String line, final int lineNumber) throws CompilerException
    {
        // Cut line on "spaces" to have instruction and its parameters
        final StringExtractor extractor = new StringExtractor(line);
        extractor.setCanReturnEmptyString(false);
        extractor.setStopAtString(false);
        final String       instruction = extractor.next();
        String             element;
        final List<String> parameters  = new ArrayList<String>();

        do
        {
            element = extractor.next();

            if (element != null)
            {
                parameters.add(element);
            }
        }
        while (element != null);

        // Do specific parse, depends on instruction

        if (CompilerConstants.CLASS.equals(instruction))
        {
            if (parameters.size() == 0)
            {
                throw new CompilerException(lineNumber, "Miss the class name !");
            }

            this.compilerContext.setClassName(parameters.get(0), lineNumber);
            return;
        }

        // The first real instruction must be the class name
        // If we reach here without a class name defined, it means that we meet an instruction before class instruction

        if (this.compilerContext.getClassName() == null)
        {
            throw new CompilerException(lineNumber, "Instruction 'class' must be the first one");
        }

        if (CompilerConstants.IMPORT.equals(instruction))
        {
            if (parameters.size() == 0)
            {
                throw new CompilerException(lineNumber, "Miss the import name !");
            }

            this.compilerContext.addImport(parameters.get(0), lineNumber);
            return;
        }

        if (CompilerConstants.EXTENDS.equals(instruction))
        {
            if (parameters.size() == 0)
            {
                throw new CompilerException(lineNumber, "Miss the parent name !");
            }

            this.compilerContext.setParent(parameters.get(0), lineNumber);
            return;
        }

        if (CompilerConstants.IMPLEMENTS.equals(instruction))
        {
            if (parameters.size() == 0)
            {
                throw new CompilerException(lineNumber, "Miss the interface name !");
            }

            this.compilerContext.addInterface(parameters.get(0), lineNumber);
            return;
        }

        // We have pass all declare instruction, so now we can create the class, if a declare instruction meet later, an
       // exception
        // will happen
        this.compilerContext.createClassGenIfNeed();

        if (CompilerConstants.FIELD.equals(instruction))
        {
            if (parameters.size() < 2)
            {
                throw new CompilerException(lineNumber, "Miss arguments in field declaration !");
            }

            if (parameters.size() == 2)
            {
                this.compilerContext.addField(parameters.get(1), parameters.get(0), lineNumber);
                return;
            }

            int    accessFlags = 0;
            String parameter;

            for (int index = parameters.size() - 1; index >= 2; index--)
            {
                parameter = parameters.get(index);

                if (CompilerConstants.STATIC.equals(parameter))
                {
                    accessFlags |= Constants.ACC_STATIC;
                }

                if (CompilerConstants.PUBLIC.equals(parameter))
                {
                    if ((accessFlags & CompilerConstants.ACCES_FLAGS_CONTROL) != 0)
                    {
                        throw new CompilerException(lineNumber,
                                                    "Only one of public, private, protected or package is possible !");
                    }

                    accessFlags |= Constants.ACC_PUBLIC;
                }

                if (CompilerConstants.PRIVATE.equals(parameter))
                {
                    if ((accessFlags & CompilerConstants.ACCES_FLAGS_CONTROL) != 0)
                    {
                        throw new CompilerException(lineNumber,
                                                    "Only one of public, private, protected or package is possible !");
                    }

                    accessFlags |= Constants.ACC_PRIVATE;
                }

                if (CompilerConstants.PACKAGE.equals(parameter))
                {
                    if ((accessFlags & CompilerConstants.ACCES_FLAGS_CONTROL) != 0)
                    {
                        throw new CompilerException(lineNumber,
                                                    "Only one of public, private, protected or package is possible !");
                    }
                }

                if (CompilerConstants.PROTECTED.equals(parameter))
                {
                    if ((accessFlags & CompilerConstants.ACCES_FLAGS_CONTROL) != 0)
                    {
                        throw new CompilerException(lineNumber,
                                                    "Only one of public, private, protected or package is possible !");
                    }

                    accessFlags |= Constants.ACC_PROTECTED;
                }
            }

            this.compilerContext.addField(parameters.get(1), parameters.get(0), accessFlags, lineNumber);
            return;
        }

        if (CompilerConstants.FIELD_REFERENCE.equals(instruction))
        {
            if (parameters.size() < 4)
            {
                throw new CompilerException(lineNumber, "Miss arguments in field_reference declaration !");
            }

            this.compilerContext.addFieldReference(parameters.get(0), parameters.get(1), parameters.get(2),
                                                   parameters.get(3), lineNumber);
            return;
        }

        if (CompilerConstants.METHOD.equals(instruction))
        {
            if (this.methodDescription != null)
            {
                throw new CompilerException(lineNumber, "Already inside a method declaration !");
            }

            if (parameters.size() == 0)
            {
                throw new CompilerException(lineNumber, "Miss the method name !");
            }

            if (parameters.size() == 1)
            {
                this.methodDescription = new MethodDescription(parameters.get(0));
                return;
            }

            int    accessFlags = 0;
            String parameter;

            for (int index = parameters.size() - 1; index >= 1; index--)
            {
                parameter = parameters.get(index);

                if (CompilerConstants.STATIC.equals(parameter))
                {
                    accessFlags |= Constants.ACC_STATIC;
                }

                if (CompilerConstants.PUBLIC.equals(parameter))
                {
                    if ((accessFlags & CompilerConstants.ACCES_FLAGS_CONTROL) != 0)
                    {
                        throw new CompilerException(lineNumber,
                                                    "Only one of public, private, protected or package is possible !");
                    }

                    accessFlags |= Constants.ACC_PUBLIC;
                }

                if (CompilerConstants.PRIVATE.equals(parameter))
                {
                    if ((accessFlags & CompilerConstants.ACCES_FLAGS_CONTROL) != 0)
                    {
                        throw new CompilerException(lineNumber,
                                                    "Only one of public, private, protected or package is possible !");
                    }

                    accessFlags |= Constants.ACC_PRIVATE;
                }

                if (CompilerConstants.PACKAGE.equals(parameter))
                {
                    if ((accessFlags & CompilerConstants.ACCES_FLAGS_CONTROL) != 0)
                    {
                        throw new CompilerException(lineNumber,
                                                    "Only one of public, private, protected or package is possible !");
                    }
                }

                if (CompilerConstants.PROTECTED.equals(parameter))
                {
                    if ((accessFlags & CompilerConstants.ACCES_FLAGS_CONTROL) != 0)
                    {
                        throw new CompilerException(lineNumber,
                                                    "Only one of public, private, protected or package is possible !");
                    }

                    accessFlags |= Constants.ACC_PROTECTED;
                }
            }

            this.methodDescription = new MethodDescription(parameters.get(0), accessFlags);
            return;
        }

        // Followings instructions are linked to current method, so need have one

        if (this.methodDescription == null)
        {
            throw new CompilerException(lineNumber, "Outside a method declaration  !");
        }

        if (CompilerConstants.PARAMETER.equals(instruction))
        {
            if (this.methodDescription.insideCode())
            {
                throw new CompilerException(lineNumber, "Can't declare parameter inside the code !");
            }

            if (parameters.size() < 2)
            {
                throw new CompilerException(lineNumber, "Miss arguments in parameter declaration !");
            }

            this.methodDescription.addParameter(parameters.get(1), this.compilerContext.stringToType(parameters.get(0)),
                                                lineNumber);
            return;
        }

        if (CompilerConstants.THROWS.equals(instruction))
        {
            if (this.methodDescription.insideCode())
            {
                throw new CompilerException(lineNumber, "Can't declare throws exception inside the code !");
            }

            if (parameters.size() == 0)
            {
                throw new CompilerException(lineNumber, "Miss exception class name !");
            }

            this.compilerContext.addException(parameters.get(0));
            return;
        }

        if (CompilerConstants.RETURN_TYPE.equals(instruction))
        {
            if (this.methodDescription.insideCode())
            {
                throw new CompilerException(lineNumber, "Can't declare return type inside the code !");
            }

            if (parameters.size() == 0)
            {
                throw new CompilerException(lineNumber, "Miss return type information !");
            }

            this.methodDescription.setReturnType(this.compilerContext.stringToType(parameters.get(0)));
            return;
        }

        // If reach here, instruction is for code of current method

        if (CompilerConstants.OPEN_BLOCK.equals(instruction))
        {
            if (this.methodDescription.insideCode())
            {
                throw new CompilerException(lineNumber, "Already inside the code !");
            }

            this.startBlockLineNumber = lineNumber;
            this.methodDescription.enterCode();
            this.intervals = new Intervals();
            this.intervals.startInterval(lineNumber);
            return;
        }

        // Here we must be inside the method code
        if (!this.methodDescription.insideCode())
        {
            throw new CompilerException(lineNumber, "Instruction invalid outside a method code !");
        }

        if ("(".equals(instruction))
        {
            this.intervals.startInterval(lineNumber);
            return;
        }

        if (")".equals(instruction))
        {
            this.intervals.endInterval(lineNumber);
            return;
        }

        // While instruction is not the end code, it is real code to add
        if (!CompilerConstants.CLOSE_BLOCK.equals(instruction))
        {
            this.methodDescription.appendCode(this.compilerContext, instruction, parameters, lineNumber);
            return;
        }

        this.intervals.endInterval(lineNumber);

        // Here left only the close instruction, so now we have all method information, we can create it and make ready
       // for next
        // one
        this.methodDescription.exitCode();
        this.methodDescription.compile(this.compilerContext, this.startBlockLineNumber, this.intervals);
        this.methodDescription = null;
        this.startBlockLineNumber = -1;
    }
}