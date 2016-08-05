/**
 * <h1>License :</h1> <br>
 * The following code is deliver as is. I take care that code compile and work, but I am not responsible about any damage it may
 * cause.<br>
 * You can use, modify, the code as your need for any usage. But you can't do any action that avoid me or other person use,
 * modify this code. The code is free for usage and modification, you can't change that fact.<br>
 * <br>
 *
 * @author JHelp
 */
package jhelp.compiler.compil;

/**
 * Operands code supported
 *
 * @author JHelp <br>
 */
public interface OpcodeConstants
{
   /**
    * Description : Load object reference from array<br>
    * Syntax :<br>
    * <code>AALOAD</code><br>
    * Operand stack : ..., array_reference, index => ..., value<br>
    * Where :
    * <ul>
    * <li>arrayref : Reference of array elements type</li>
    * <li>index : Array index (int)</li>
    * <li>value : Reference of the desired object inside the array</li>
    * </ul>
    */
   public static final String AALOAD          = "AALOAD";
   /**
    * Description : Store into reference array<br>
    * Syntax :<br>
    * <code>AASTORE</code><br>
    * Operand stack : ..., array_reference, index, value => ...<br>
    * Where :
    * <ul>
    * <li>arrayref : Reference of array elements type</li>
    * <li>index : Array index (int)</li>
    * <li>value : Reference object to put inside the array at desired index</li>
    * </ul>
    */
   public static final String AASTORE         = "AASTORE";
   /**
    * Description : Push null<br>
    * Syntax :<br>
    * <code>ACONST_NULL</code><br>
    * Operand stack : ... => ..., null<br>
    * <br>
    * Details: Push null reference on stack
    */
   public static final String ACONST_NULL     = "ACONST_NULL";
   /**
    * Description : Load reference from local variable<br>
    * Syntax :<br>
    * <code>ALOAD &ltname&gt;</code><br>
    * Where :
    * <ul>
    * <li>name : Current method variable name OR current method parameter name OR 'this'</li>
    * </ul>
    * <br>
    * Operand stack : ... => ..., objectref<br>
    * Where :
    * <ul>
    * <li>objectref : Reference object loaded</li>
    * </ul>
    * <br>
    * Details: If <code>name</code> is a method variable name, the corresponding variable must have previously initialized (Even
    * with null)
    */
   public static final String ALOAD           = "ALOAD";
   /**
    * Description : Create new array of reference<br>
    * Syntax :<br>
    * <code>ANEWARRAY &lt;Type&gt;</code><br>
    * Where :
    * <ul>
    * <li>Type : is the variable type (see {@link Compiler ASM grammar definition of "Type"}.</li>
    * </ul>
    * <br>
    * Operand stack : ..., count => ..., arrayref<br>
    * Where :
    * <ul>
    * <li>count : Array size (int)</li>
    * <li>arrayref : Reference on created array</li>
    * </ul>
    */
   public static final String ANEWARRAY       = "ANEWARRAY";
   /**
    * Description : Return reference from method <br>
    * Syntax :<br>
    * <code>ARETURN</code><br>
    * Operand stack : ..., objectref => [empty]<br>
    * Where :
    * <ul>
    * <li>objectref : reference of object to return</li>
    * </ul>
    * <br>
    * Details:If the current method is a synchronized method, the monitor entered or reentered on invocation of the method is
    * updated and possibly exited as if by execution of a monitorexit instruction in the current thread. If no exception is
    * thrown, <b>objectref</b> is popped from the operand stack of the current frame and pushed onto the operand stack of the
    * frame of the invoker. Any other values on the operand stack of the current method are discarded.
    */
   public static final String ARETURN         = "ARETURN";
   /**
    * Description : Get length of array<br>
    * Syntax :<br>
    * <code>ARRAYLENGTH</code><br>
    * Operand stack : ..., arrayref => ..., length<br>
    * Where :
    * <ul>
    * <li>arrayref : Reference on aarray</li>
    * <li>length : Array length (int)</li>
    * </ul>
    * <br>
    * Details:
    */
   public static final String ARRAYLENGTH     = "ARRAYLENGTH";
   /**
    * Description : Store reference into local variable<br>
    * Syntax :<br>
    * <code>ASTORE &lt;name&gt;</code><br>
    * Where :
    * <ul>
    * <li>name : Current method variable name OR current method parameter name</li>
    * </ul>
    * <br>
    * Operand stack : ..., objectref => ...<br>
    * Where :
    * <ul>
    * <li>objectref : Object reference to push on local variable</li>
    * </ul>
    */
   public static final String ASTORE          = "ASTORE";
   /**
    * Description : Throw exception or error<br>
    * Syntax :<br>
    * <code>ATHROW</code><br>
    * Operand stack : .., objectref => objectref<br>
    * Where :
    * <ul>
    * <li>objectref : Reference on exception to throw</li>
    * </ul>
    */
   public static final String ATHROW          = "ATHROW";
   /**
    * Description : Load byte or boolean from array<br>
    * Syntax :<br>
    * <code>BALOAD</code><br>
    * Operand stack : ..., arrayref, index => ..., value<br>
    * Where :
    * <ul>
    * <li>arrayref : reference on array</li>
    * <li>index : Array index (int)</li>
    * <li>value : Value in array at given index (int)</li>
    * </ul>
    * <br>
    * Details: The byte value in the component of the array at <b>index</b> is retrieved, sign-extended to an int value, and
    * pushed onto the top of the operand stack.
    */
   public static final String BALOAD          = "BALOAD";
   /**
    * Description : Store into byte or boolean array<br>
    * Syntax :<br>
    * <code>BASTORE</code><br>
    * Operand stack : ..., arrayref, index, value => ...<br>
    * Where :
    * <ul>
    * <li>arrayref : reference on array</li>
    * <li>index : Array index (int)</li>
    * <li>value : Value to store in array at given index (int)</li>
    * </ul>
    * <br>
    * Details:The int <b>value</b> is truncated to a byte and stored as the component of the array indexed by <b>index</b>.
    */
   public static final String BASTORE         = "BASTORE";
   /**
    * Description : Push byte<br>
    * Syntax :<br>
    * <code>BIPUSH &lt;value&gt;</code><br>
    * Where :
    * <ul>
    * <li>value : Constant value to push</li>
    * </ul>
    * <br>
    * Operand stack : ... => ..., value<br>
    * Where :
    * <ul>
    * <li>value: pushed value (int)</li>
    * </ul>
    * <br>
    * Details: The immediate <b>value</b> is sign-extended to an int <b>value</b>. That <b>value</b> is pushed onto the operand
    * stack.
    */
   public static final String BIPUSH          = "BIPUSH";
   /**
    * Description : Make a break point. Here does nothing<br>
    * Syntax :<br>
    * <code>BREAKPOINT</code>
    */
   public static final String BREAKPOINT      = "BREAKPOINT";
   /**
    * Description : Load char from array<br>
    * Syntax :<br>
    * <code>CALOAD</code><br>
    * Operand stack : ..., arrayref, index => ..., value<br>
    * Where :
    * <ul>
    * <li>arrayref : reference on array</li>
    * <li>index : Array index (int)</li>
    * <li>value : Value in array at given index (int)</li>
    * </ul>
    * <br>
    * Details: The component of the array at index is retrieved and zero-extended to an int value. That value is pushed onto the
    * operand stack.
    */
   public static final String CALOAD          = "CALOAD";
   /**
    * Description : Store into char array<br>
    * Syntax :<br>
    * <code>CASTORE</code><br>
    * Operand stack : ..., arrayref, index, value => ...<br>
    * Where :
    * <ul>
    * <li>arrayref : reference on array</li>
    * <li>index : Array index (int)</li>
    * <li>value : Value to put in array at given index (int)</li>
    * </ul>
    * <br>
    * Details: The int value is truncated to a char and stored as the component of the array indexed by index.
    */
   public static final String CASTORE         = "CASTORE";
   /**
    * Description : Cast object to given type<br>
    * Syntax :<br>
    * <table border=1>
    * <tr>
    * <td>CHECKCAST &lt;ClassName&gt;</td>
    * </tr>
    * </table>
    * <br>
    * Where :
    * <ul>
    * <li>ClassName : The complete class name (with the package) or the short version (Must be in imports, "java.lang" or same
    * package of the class)</li>
    * </ul>
    * <br>
    * Operand stack :
    * <table border=1>
    * <tr>
    * <td>..., objectref1 => ..., objectref2</td>
    * </tr>
    * </table>
    * <br>
    * Where :
    * <ul>
    * <li>objectref1 : reference to the object to cast</li>
    * <li>objectref2 : casted reference</li>
    * </ul>
    * <br>
    * Details: The cast must be possible else a ClassCastException happen
    */
   public static final String CHECKCAST       = "CHECKCAST";
   /**
    * Description : Convert double to float<br>
    * Syntax :<br>
    * <code>D2F</code><br>
    * Operand stack : ..., value => ..., result<br>
    * Where :
    * <ul>
    * <li>value : double to convert</li>
    * <li>result : float converted</li>
    * </ul>
    * <br>
    * Details: The value is converted to a float result using IEEE 754 round to nearest mode
    */
   public static final String D2F             = "D2F";
   /**
    * Description : Convert double to int<br>
    * Syntax :<br>
    * <code>D2I</code><br>
    * Operand stack : ..., value => ..., result<br>
    * Where :
    * <ul>
    * <li>value : double to convert</li>
    * <li>result : int converted</li>
    * </ul>
    * <br>
    * Details:
    * <ul>
    * <li>If the value' is NaN, the result of the conversion is an int 0</li>
    * <li>Otherwise, if the value is not an infinity, it is rounded to an integer value V, rounding towards zero using IEEE 754
    * round towards zero mode. If this integer value V can be represented as an int, then the result is the int value V.</li>
    * <li>Otherwise, either the value must be too small (a negative value of large magnitude or negative infinity), and the
    * result is the smallest representable value of type int, or the value must be too large (a positive value of large
    * magnitude or positive infinity), and the result is the largest representable value of type int.</li>
    * </ul>
    */
   public static final String D2I             = "D2I";
   /**
    * Description : Convert double to long<br>
    * Syntax :<br>
    * <code>D2L</code><br>
    * Operand stack : ..., value => ..., result<br>
    * Where :
    * <ul>
    * <li>value : double to convert</li>
    * <li>result : long converted</li>
    * </ul>
    * <br>
    * Details:
    * <ul>
    * <li>If the value' is NaN, the result of the conversion is a long 0.</li>
    * <li>Otherwise, if the value' is not an infinity, it is rounded to an integer value V, rounding towards zero using IEEE 754
    * round towards zero mode. If this integer value V can be represented as a long, then the result is the long value V.</li>
    * <li>Otherwise, either the value' must be too small (a negative value of large magnitude or negative infinity), and the
    * result is the smallest representable value of type long, or the value' must be too large (a positive value of large
    * magnitude or positive infinity), and the result is the largest representable value of type long.</li>
    * </ul>
    */
   public static final String D2L             = "D2L";
   /**
    * Description : Add double<br>
    * Syntax :<br>
    * <code>DADD</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first double</li>
    * <li>value2 : second double</li>
    * <li>result : addition result (double)</li>
    * </ul>
    */
   public static final String DADD            = "DADD";
   /**
    * Description : Load double from array<br>
    * Syntax :<br>
    * <code>DALOAD</code><br>
    * Operand stack : ..., arrayref, index => ..., value<br>
    * Where :
    * <ul>
    * <li>arrayref : reference on array</li>
    * <li>index : Array index (int)</li>
    * <li>value : Value in array at given index (double)</li>
    * </ul>
    */
   public static final String DALOAD          = "DALOAD";
   /**
    * Description : Store into double array<br>
    * Syntax :<br>
    * <code>DASTORE</code><br>
    * Operand stack : ..., arrayref, index, value => ...<br>
    * Where :
    * <ul>
    * <li>arrayref : reference on array</li>
    * <li>index : Array index (int)</li>
    * <li>value : Value to put in array at given index (double)</li>
    * </ul>
    */
   public static final String DASTORE         = "DASTORE";
   /**
    * Description : Compare double (Great if NaN)<br>
    * Syntax :<br>
    * <code>DCMPG</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first double</li>
    * <li>value2 : second double</li>
    * <li>result : comparison result (int)</li>
    * </ul>
    * <br>
    * Details:
    * <ul>
    * <li>If value1' is greater than value2', the int value 1 is pushed onto the operand stack.</li>
    * <li>Otherwise, if value1' is equal to value2', the int value 0 is pushed onto the operand stack.</li>
    * <li>Otherwise, if value1' is less than value2', the int value -1 is pushed onto the operand stack.</li>
    * <li>Otherwise, at least one of value1' or value2' is NaN pushes the int value 1 onto the operand stack</li>
    * </ul>
    */
   public static final String DCMPG           = "DCMPG";
   /**
    * Description : Compare double (Low if NaN)<br>
    * Syntax :<br>
    * <code>DCMPG</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first double</li>
    * <li>value2 : second double</li>
    * <li>result : comparison result (int)</li>
    * </ul>
    * <br>
    * Details:
    * <ul>
    * <li>If value1' is greater than value2', the int value 1 is pushed onto the operand stack.</li>
    * <li>Otherwise, if value1' is equal to value2', the int value 0 is pushed onto the operand stack.</li>
    * <li>Otherwise, if value1' is less than value2', the int value -1 is pushed onto the operand stack.</li>
    * <li>Otherwise, at least one of value1' or value2' is NaN pushes the int value -1 onto the operand stack</li>
    * </ul>
    */
   public static final String DCMPL           = "DCMPL";
   /**
    * Description : Push double 0 or 1<br>
    * Syntax :<br>
    * <code>DCONST &lt;value&gt;</code><br>
    * Where :
    * <ul>
    * <li>value : Must be 0 or 1</li>
    * </ul>
    * <br>
    * Operand stack : ... => ..., value<br>
    * Where :
    * <ul>
    * <li>value : the value pushed (0 or 1) (double)</li>
    * </ul>
    */
   public static final String DCONST          = "DCONST";
   /**
    * Description : Divide double<br>
    * Syntax :<br>
    * <code>DADD</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first double</li>
    * <li>value2 : second double</li>
    * <li>result : division result (double)</li>
    * </ul>
    */
   public static final String DDIV            = "DDIV";
   /**
    * Description : Load double from local variable<br>
    * Syntax :<br>
    * <code>DLOAD &lt;name&gt;</code><br>
    * Where :
    * <ul>
    * <li>name : Current method variable name OR current method parameter name</li>
    * </ul>
    * <br>
    * Operand stack : ... => ..., value<br>
    * Where :
    * <ul>
    * <li>value : value of the method variable or parameter</li>
    * </ul>
    * <br>
    * Details: If <code>name</code> is a method variable name, the corresponding variable must have previously initialized (Even
    * with 0)
    */
   public static final String DLOAD           = "DLOAD";
   /**
    * Description : Multiply double<br>
    * Syntax :<br>
    * <code>DMUL</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first double</li>
    * <li>value2 : second double</li>
    * <li>result : multiplication result (double)</li>
    * </ul>
    */
   public static final String DMUL            = "DMUL";
   /**
    * Description : Negate double<br>
    * Syntax :<br>
    * <code>DNEG</code><br>
    * Operand stack : ..., value => ..., result<br>
    * Where :
    * <ul>
    * <li>value : Value to negate (double)</li>
    * <li>result : Negated result (double)</li>
    * </ul>
    */
   public static final String DNEG            = "DNEG";
   /**
    * Description : Remainder on double<br>
    * Syntax :<br>
    * <code>DREM</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first double</li>
    * <li>value2 : second double</li>
    * <li>result : remainder result (double)</li>
    * </ul>
    */
   public static final String DREM            = "DREM";
   /**
    * Description : Return double from method<br>
    * Syntax :<br>
    * <code>DRETURN</code><br>
    * Operand stack : ..., value => [empty]<br>
    * Where :
    * <ul>
    * <li>value : value to return (double)</li>
    * </ul>
    */
   public static final String DRETURN         = "DRETURN";
   /**
    * Description : Store double to local variable<br>
    * Syntax :<br>
    * <code>DSTORE &lt;name&gt;</code><br>
    * Where :
    * <ul>
    * <li>name : Current method variable name OR current method parameter name</li>
    * </ul>
    * Operand stack : ...,value => ...<br>
    * Where :
    * <ul>
    * <li>value : value to store in the method variable or parameter</li>
    * </ul>
    */
   public static final String DSTORE          = "DSTORE";
   /**
    * Description : Subtract double<br>
    * Syntax :<br>
    * <code>DSUB</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first double</li>
    * <li>value2 : second double</li>
    * <li>result : subtraction result (double)</li>
    * </ul>
    */
   public static final String DSUB            = "DSUB";
   /**
    * Description : Duplicate the top operand stack value<br>
    * Syntax :<br>
    * <code>DUP</code><br>
    * Operand stack : ..., value => ..., value, value<br>
    * Where :
    * <ul>
    * <li>value : value to duplicate</li>
    * </ul>
    * <br>
    * Details: Value can't be a double or long
    */
   public static final String DUP             = "DUP";
   /**
    * Description : Duplicate the top operand stack value and insert two values down<br>
    * Syntax :<br>
    * <code>DUP_X1</code><br>
    * Operand stack : ..., value2, value1 => ..., value1, value2, value1<br>
    * Where :
    * <ul>
    * <li>value1 : value to duplicate</li>
    * <li>value2 : value to just move</li>
    * </ul>
    * <br>
    * Details: value1 and value2 can't be double or long
    */
   public static final String DUP_X1          = "DUP_X1";
   /**
    * Description : Duplicate the top operand stack value and insert two or three values down<br>
    * Syntax :<br>
    * <code>DUP_X2</code><br>
    * Operand stack : .., value3, value2, value1 => ..., value1, value3, value2, value1<br>
    * Where :
    * <ul>
    * <li>value1 : value to duplicate</li>
    * <li>value2 : value to just move</li>
    * <li>value3 : value to just move</li>
    * </ul>
    * <br>
    * Details: value1, value2 and value3 can't be double or long <br>
    * <br>
    * <h1><b>OR</b></h1> <br>
    * Operand stack : .., value2, value1 => ..., value1, value2, value1<br>
    * Where :
    * <ul>
    * <li>value1 : value to duplicate</li>
    * <li>value2 : value to just move</li>
    * </ul>
    * <br>
    * Details: value1 not a long nor double, value2 double or long
    */
   public static final String DUP_X2          = "DUP_X2";
   /**
    * Description : Duplicate the top one or two operand stack values<br>
    * Syntax :<br>
    * <code>DUP2</code><br>
    * Operand stack : ..., value2, value1 => ..., value2, value1, value2, value1<br>
    * Where :
    * <ul>
    * <li>value1 : value to duplicate</li>
    * <li>value2 : value to duplicate</li>
    * </ul>
    * <br>
    * Details:value1 and value2 can't be double or long<br>
    * <br>
    * <h1><b>OR</b></h1> <br>
    * Operand stack : ..., value => ...,value, value<br>
    * Where
    * <ul>
    * <li>value : value to duplicate</li> *
    * </ul>
    * <br>
    * Details:value double or long
    */
   public static final String DUP2            = "DUP2";
   /**
    * Description : Duplicate the top one or two operand stack values and insert two or three values down<br>
    * Syntax :<br>
    * <code>DUP2_X1</code><br>
    * Operand stack : .., value3, value2, value1 => ..., value2, value1, value3, value2, value1<br>
    * Where :
    * <ul>
    * <li>value1 : value to duplicate</li>
    * <li>value2 : value to duplicate</li>
    * <li>value3 : value to just move</li>
    * </ul>
    * Details: value1, value2 and value3 not long nor double<br>
    * <br>
    * <h1><b>OR</b></h1> <br>
    * Operand stack : ..., value2, value1 => ..., value1, value2, value1<br>
    * Where
    * <ul>
    * <li>value1 : value to duplicate</li>
    * <li>value2 : value to just move</li>
    * </ul>
    * Details: value1 long or double and value2 not long nor double
    */
   public static final String DUP2_X1         = "DUP2_X1";
   /**
    * Description : Duplicate the top one or two operand stack values and insert two, three, or four values down<br>
    * Syntax :<br>
    * <code>DUP2_X2</code><br>
    * Operand stack : ..., value4, value3, value2, value1 => ..., value2, value1, value4, value3, value2, value1<br>
    * Where :
    * <ul>
    * <li>value1 : not long nor double</li>
    * <li>value2 : not long nor double</li>
    * <li>value3 : not long nor double</li>
    * <li>value4 : not long nor double</li>
    * </ul>
    * <br>
    * <h1><b>OR</b></h1> <br>
    * Operand stack : ..., value3, value2, value1 => ..., value1, value3, value2, value1 <br>
    * Where :
    * <ul>
    * <li>value1 : long or double</li>
    * <li>value2 : not long nor double</li>
    * <li>value3 : not long nor double</li>
    * </ul>
    * <br>
    * <h1><b>OR</b></h1> <br>
    * Operand stack : ..., value3, value2, value1 => ..., value2, value1, value3, value2, value1 <br>
    * Where :
    * <ul>
    * <li>value1 : not long nor double</li>
    * <li>value2 : not long nor double</li>
    * <li>value3 : long or double</li>
    * </ul>
    * <br>
    * <h1><b>OR</b></h1> <br>
    * Operand stack : ..., value2, value1 => ..., value1, value2, value1 <br>
    * Where :
    * <ul>
    * <li>value1 : long or double</li>
    * <li>value2 : long or double</li>
    * </ul>
    */
   public static final String DUP2_X2         = "DUP2_X2";
   /**
    * Description : Convert float to double<br>
    * Syntax :<br>
    * <code>F2D</code><br>
    * Operand stack : ..., value => ..., result<br>
    * Where :
    * <ul>
    * <li>value : Value to convert (float)</li>
    * <li>result : Converted value (double)</li>
    * </ul>
    */
   public static final String F2D             = "F2D";
   /**
    * Description : Convert float to int<br>
    * Syntax :<br>
    * <code>F2I</code><br>
    * Operand stack : ..., value => ..., result<br>
    * Where :
    * <ul>
    * <li>value : Value to convert (float)</li>
    * <li>result : Converted value (int)</li>
    * </ul>
    * Details:
    * <ul>
    * <li>If the value' is NaN, the result of the conversion is an int 0</li>
    * <li>Otherwise, if the value' is not an infinity, it is rounded to an integer value V, rounding towards zero using IEEE 754
    * round towards zero mode. If this integer value V can be represented as an int, then the result is the int value V.</li>
    * <li>Otherwise, either the value' must be too small (a negative value of large magnitude or negative infinity), and the
    * result is the smallest representable value of type int, or the value' must be too large (a positive value of large
    * magnitude or positive infinity), and the result is the largest representable value of type int.</li>
    * </ul>
    */
   public static final String F2I             = "F2I";
   /**
    * Description : Convert float to long<br>
    * Syntax :<br>
    * <code>F2L</code><br>
    * Operand stack : ..., value => ..., result<br>
    * Where :
    * <ul>
    * <li>value : Value to convert (float)</li>
    * <li>result : Converted value (long)</li>
    * </ul>
    * Details:
    * <ul>
    * <li>If the value' is NaN, the result of the conversion is a long 0.</li>
    * <li>Otherwise, if the value' is not an infinity, it is rounded to an integer value V, rounding towards zero using IEEE 754
    * round towards zero mode. If this integer value V can be represented as a long, then the result is the long value V.</li>
    * <li>Otherwise, either the value' must be too small (a negative value of large magnitude or negative infinity), and the
    * result is the smallest representable value of type long, or the value' must be too large (a positive value of large
    * magnitude or positive infinity), and the result is the largest representable value of type long.</li>
    * </ul>
    */
   public static final String F2L             = "F2L";
   /**
    * Description : Add float<br>
    * Syntax :<br>
    * <code>FADD</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first float</li>
    * <li>value2 : second float</li>
    * <li>result : addition result (float)</li>
    * </ul>
    */
   public static final String FADD            = "FADD";
   /**
    * Description : Load float from array<br>
    * Syntax :<br>
    * <code>FALOAD</code><br>
    * Operand stack : ..., arrayref, index => ..., value<br>
    * Where :
    * <ul>
    * <li>arrayref: Reference to array</li>
    * <li>index : Array index (int)</li>
    * <li>value : Value in array at given index (float)</li>
    * </ul>
    */
   public static final String FALOAD          = "FALOAD";
   /**
    * Description :Store into float array<br>
    * Syntax :<br>
    * <code>FASTORE</code><br>
    * Operand stack : ..., arrayref, index, value => ...<br>
    * Where :
    * <ul>
    * <li>arrayref: Reference to array</li>
    * <li>index : Array index (int)</li>
    * <li>value : Value to put in array at given index (float)</li>
    * </ul>
    */
   public static final String FASTORE         = "FASTORE";
   /**
    * Description : Compare float (Great NaN)<br>
    * Syntax :<br>
    * <code>FCMPG</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first float</li>
    * <li>value2 : second float</li>
    * <li>result : comparison result (int)</li>
    * </ul>
    * Details:
    * <ul>
    * <li>If value1' is greater than value2', the int value 1 is pushed onto the operand stack.</li>
    * <li>Otherwise, if value1' is equal to value2', the int value 0 is pushed onto the operand stack.</li>
    * <li>Otherwise, if value1' is less than value2', the int value -1 is pushed onto the operand stack.</li>
    * <li>Otherwise, at least one of value1' or value2' is NaN. The instruction pushes the int value 1 onto the operand stack
    * </li>
    * </ul>
    */
   public static final String FCMPG           = "FCMPG";
   /**
    * Description : Compare float (Low NaN)<br>
    * Syntax :<br>
    * <code>FCMPL</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first float</li>
    * <li>value2 : second float</li>
    * <li>result : comparison result (int)</li>
    * </ul>
    * Details:
    * <ul>
    * <li>If value1' is greater than value2', the int value 1 is pushed onto the operand stack.</li>
    * <li>Otherwise, if value1' is equal to value2', the int value 0 is pushed onto the operand stack.</li>
    * <li>Otherwise, if value1' is less than value2', the int value -1 is pushed onto the operand stack.</li>
    * <li>Otherwise, at least one of value1' or value2' is NaN. The instruction pushes the int value -1 onto the operand stack
    * </li>
    * </ul>
    */
   public static final String FCMPL           = "FCMPL";
   /**
    * Description : Push float constant 0,1 or 2<br>
    * Syntax :<br>
    * <code>FCONST &lt;value&gt;</code><br>
    * Where :
    * <ul>
    * <li>value : value to push 0, 1 or 2</li>
    * </ul>
    * <br>
    * Operand stack : ... => ..., value<br>
    * Where :
    * <ul>
    * <li>value : value pushed 0, 1 or 2 (float)</li>
    * </ul>
    */
   public static final String FCONST          = "FCONST";
   /**
    * Description : Divide float<br>
    * Syntax :<br>
    * <code>FDIV</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first float</li>
    * <li>value2 : second float</li>
    * <li>result : division result (float)</li>
    * </ul>
    */
   public static final String FDIV            = "FDIV";
   /**
    * Description : Load float from local variable<br>
    * Syntax :<br>
    * <code>FLOAD &lt;name&gt;</code><br>
    * Where :
    * <ul>
    * <li>name : Current method variable name OR current method parameter name</li>
    * </ul>
    * <br>
    * Operand stack : ... => ..., value<br>
    * Where :
    * <ul>
    * <li>value : value of the method variable or parameter</li>
    * </ul>
    * <br>
    * Details: If <code>name</code> is a method variable name, the corresponding variable must have previously initialized (Even
    * with 0)
    */
   public static final String FLOAD           = "FLOAD";
   /**
    * Description : Multiply float<br>
    * Syntax :<br>
    * <code>FMUL</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first float</li>
    * <li>value2 : second float</li>
    * <li>result : multiplication result (float)</li>
    * </ul>
    */
   public static final String FMUL            = "FMUL";
   /**
    * Description : Negate float<br>
    * Syntax :<br>
    * <code>FNEG</code><br>
    * Operand stack : ..., value => ..., result<br>
    * Where :
    * <ul>
    * <li>value : float to negate</li>
    * <li>result : negated float</li>
    * </ul>
    */
   public static final String FNEG            = "FNEG";
   /**
    * Description : Remainder float<br>
    * Syntax :<br>
    * <code>FREM</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first float</li>
    * <li>value2 : second float</li>
    * <li>result : remainder result (float)</li>
    * </ul>
    */
   public static final String FREM            = "FREM";
   /**
    * Description : Return float from method<br>
    * Syntax :<br>
    * <code>FRETURN</code><br>
    * Operand stack : ..., value => [empty]<br>
    * Where :
    * <ul>
    * <li>value : vlaue to return (float)</li>
    * </ul>
    * <br>
    * Details:
    */
   public static final String FRETURN         = "FRETURN";
   /**
    * Description : Store float to local variable<br>
    * Syntax :<br>
    * <code>FSTORE &lt;name&gt;</code><br>
    * Where :
    * <ul>
    * <li>name : Current method variable name OR current method parameter name</li>
    * </ul>
    * Operand stack : ...,value => ...<br>
    * Where :
    * <ul>
    * <li>value : value to store in the method variable or parameter</li>
    * </ul>
    */
   public static final String FSTORE          = "FSTORE";
   /**
    * Description : Subtract float<br>
    * Syntax :<br>
    * <code>FMUL</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first float</li>
    * <li>value2 : second float</li>
    * <li>result : subtraction result (float)</li>
    * </ul>
    */
   public static final String FSUB            = "FSUB";
   /**
    * Description : Fetch field from object<br>
    * Syntax :<br>
    * <code>GETFIELD &lt;fieldName/fieldAlias&gt;</code><br>
    * Where :
    * <ul>
    * <li>fieldName : Name of the field to get the value</li>
    * </ul>
    * <br>
    * Operand stack : ..., objectref => ..., value<br>
    * Where :
    * <ul>
    * <li>objectref : reference to the object</li>
    * <li>value : field value</li>
    * </ul>
    */
   public static final String GETFIELD        = "GETFIELD";
   /**
    * Description : Get static field from class<br>
    * Syntax :<br>
    * <code>GETSTATIC  &lt;fieldName/fieldAlias&gt;</code><br>
    * Where :
    * <ul>
    * <li>fieldName : Name of the field to get the value</li>
    * </ul>
    * <br>
    * Operand stack : ... => ..., value<br>
    * Where :
    * <ul>
    * <li>value : field value</li>
    * </ul>
    * <br>
    * Details:
    */
   public static final String GETSTATIC       = "GETSTATIC";
   /**
    * Description : Branch always<br>
    * Syntax :<br>
    * <code>GOTO &lt;label&gt;</code><br>
    * Where :
    * <ul>
    * <li>label : Label name to go</li>
    * </ul>
    * Operand stack : No change<br>
    * Details: Be sure the label is declare somewhere in the method with {@link #Z_LABEL}
    */
   public static final String GOTO            = "GOTO";
   /**
    * Description : Branch always<br>
    * Syntax :<br>
    * <code>GOTO_W &lt;label&gt;</code><br>
    * Where :
    * <ul>
    * <li>label : Label name to go</li>
    * </ul>
    * Operand stack : No change<br>
    * Details: Be sure the label is declare somewhere in the method with {@link #Z_LABEL}<br>
    * This instruction designed for big jump, but {@link #GOTO} here auto transform in {@link #GOTO_W} if jump is to big, so use
    * {@link #GOTO} and let compiler manage by it self
    */
   public static final String GOTO_W          = "GOTO_W";
   /**
    * Description : Convert int to byte<br>
    * Syntax :<br>
    * <code>I2B</code><br>
    * Operand stack : ..., value => ..., result<br>
    * Where :
    * <ul>
    * <li>value : value to convert (int)</li>
    * <li>result : converted value (int)</li>
    * </ul>
    * Details: Truncated to a byte then sign-extended to an int result
    */
   public static final String I2B             = "I2B";
   /**
    * Description :Convert int to char<br>
    * Syntax :<br>
    * <code>I2C</code><br>
    * Operand stack : ..., value => ..., result<br>
    * Where :
    * <ul>
    * <li>value : value to convert (int)</li>
    * <li>result : converted value (int)</li>
    * </ul>
    * Details: Truncated to char, then zero-extended to an int result
    */
   public static final String I2C             = "I2C";
   /**
    * Description : Convert int to double<br>
    * Syntax :<br>
    * <code>I2D</code><br>
    * Operand stack : ..., value => ..., result<br>
    * Where :
    * <ul>
    * <li>value : value to convert (int)</li>
    * <li>result : converted value (double)</li>
    * </ul>
    */
   public static final String I2D             = "I2D";
   /**
    * Description : Convert int to float<br>
    * Syntax :<br>
    * <code>I2F</code><br>
    * Operand stack : ..., value => ..., result<br>
    * Where :
    * <ul>
    * <li>value : value to convert (int)</li>
    * <li>result : converted value (float)</li>
    * </ul>
    */
   public static final String I2F             = "I2F";
   /**
    * Description : Convert int to long<br>
    * Syntax :<br>
    * <code>I2L</code><br>
    * Operand stack : ..., value => ..., result<br>
    * Where :
    * <ul>
    * <li>value : value to convert (int)</li>
    * <li>result : converted value (long)</li>
    * </ul>
    */
   public static final String I2L             = "I2L";
   /**
    * Description : Convert int to short<br>
    * Syntax :<br>
    * <code>I2S</code><br>
    * Operand stack : ..., value => ..., result<br>
    * Where :
    * <ul>
    * <li>value : value to convert (int)</li>
    * <li>result : converted value (int)</li>
    * </ul>
    * Details: Truncated to a short, then sign-extended to an int result
    */
   public static final String I2S             = "I2S";
   /**
    * Description : Add int<br>
    * Syntax :<br>
    * <code>IADD</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first int</li>
    * <li>value2 : second int</li>
    * <li>result : addition result (int)</li>
    * </ul>
    */
   public static final String IADD            = "IADD";
   /**
    * Description : Load int from array<br>
    * Syntax :<br>
    * <code>IALOAD</code><br>
    * Operand stack : ..., arrayref, index => ..., value<br>
    * Where :
    * <ul>
    * <li>arrayref : Reference on array</li>
    * <li>index : Array index (int)</li>
    * <li>value : Value in array at given index (int)</li>
    * </ul>
    */
   public static final String IALOAD          = "IALOAD";
   /**
    * Description : And on int<br>
    * Syntax :<br>
    * <code>IAND</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first int</li>
    * <li>value2 : second int</li>
    * <li>result : and result (int)</li>
    * </ul>
    */
   public static final String IAND            = "IAND";
   /**
    * Description : Store int to array<br>
    * Syntax :<br>
    * <code>IASTORE</code><br>
    * Operand stack : ..., arrayref, index, value => ...<br>
    * Where :
    * <ul>
    * <li>arrayref : Reference on array</li>
    * <li>index : Array index (int)</li>
    * <li>value : Value to put in array at given index (int)</li>
    * </ul>
    */
   public static final String IASTORE         = "IASTORE";
   /**
    * Description : Push int constant -1, 0, 1 ,2, 3, 4 or 5<br>
    * Syntax :<br>
    * <code>ICONST &lt;value&gt;</code><br>
    * Where :
    * <ul>
    * <li>value : Value to push -1, 0, 1 ,2, 3, 4 or 5</li>
    * </ul>
    * <br>
    * Operand stack : ... => ..., value<br>
    * Where :
    * <ul>
    * <li>vlaue : value pushed -1, 0, 1 ,2, 3, 4 or 5 (int)</li>
    * </ul>
    */
   public static final String ICONST          = "ICONST";
   /**
    * Description : Divide int<br>
    * Syntax :<br>
    * <code>IDIV</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first int</li>
    * <li>value2 : second int</li>
    * <li>result : division result (int)</li>
    * </ul>
    */
   public static final String IDIV            = "IDIV";
   /**
    * Description : Branch if reference comparison (equal) succeeds<br>
    * Syntax :<br>
    * <code>IF_ACMPEQ &lt;label&gt;</code><br>
    * Where :
    * <ul>
    * <li>label : Label name to go if condition respected</li>
    * </ul>
    * Operand stack : ..., value1, value2 => ...<br>
    * Where :
    * <ul>
    * <li>value1 : reference to first object</li>
    * <li>value2 : reference to second object</li>
    * </ul>
    */
   public static final String IF_ACMPEQ       = "IF_ACMPEQ";
   /**
    * Description : Branch if reference comparison (not equal) succeeds<br>
    * Syntax :<br>
    * <code>IF_ACMPNE &lt;label&gt;</code><br>
    * Where :
    * <ul>
    * <li>label : Label name to go if condition respected</li>
    * </ul>
    * Operand stack : ..., value1, value2 => ...<br>
    * Where :
    * <ul>
    * <li>value1 : reference to first object</li>
    * <li>value2 : reference to second object</li>
    * </ul>
    */
   public static final String IF_ACMPNE       = "IF_ACMPNE";
   /**
    * Description : Branch if int comparison (equal) succeeds<br>
    * Syntax :<br>
    * <code>IF_ICMPEQ &lt;label&gt;</code><br>
    * Where :
    * <ul>
    * <li>label : Label name to go if condition respected</li>
    * </ul>
    * Operand stack : ..., value1, value2 => ...<br>
    * Where :
    * <ul>
    * <li>value1 : first int</li>
    * <li>value2 : second int</li>
    * </ul>
    */
   public static final String IF_ICMPEQ       = "IF_ICMPEQ";
   /**
    * Description : Branch if int comparison (greater or equal) succeeds<br>
    * Syntax :<br>
    * <code>IF_ICMPGE  &lt;label&gt;</code><br>
    * Where :
    * <ul>
    * <li>label : Label name to go if condition respected</li>
    * </ul>
    * Operand stack : ..., value1, value2 => ...<br>
    * Where :
    * <ul>
    * <li>value1 : first int</li>
    * <li>value2 : second int</li>
    * </ul>
    */
   public static final String IF_ICMPGE       = "IF_ICMPGE";
   /**
    * Description : Branch if int comparison (greater) succeeds<br>
    * Syntax :<br>
    * <code>IF_ICMPGT &lt;label&gt;</code><br>
    * Where :
    * <ul>
    * <li>label : Label name to go if condition respected</li>
    * </ul>
    * Operand stack : ..., value1, value2 => ...<br>
    * Where :
    * <ul>
    * <li>value1 : first int</li>
    * <li>value2 : second int</li>
    * </ul>
    */
   public static final String IF_ICMPGT       = "IF_ICMPGT";
   /**
    * Description : Branch if int comparison (lower or equal) succeeds<br>
    * Syntax :<br>
    * <code>IF_ICMPLE &lt;label&gt;</code><br>
    * Where :
    * <ul>
    * <li>label : Label name to go if condition respected</li>
    * </ul>
    * Operand stack : ..., value1, value2 => ...<br>
    * Where :
    * <ul>
    * <li>value1 : first int</li>
    * <li>value2 : second int</li>
    * </ul>
    */
   public static final String IF_ICMPLE       = "IF_ICMPLE";
   /**
    * Description : Branch if int comparison (lower) succeeds<br>
    * Syntax :<br>
    * <code>IF_ICMPLT &lt;label&gt;</code><br>
    * Where :
    * <ul>
    * <li>label : Label name to go if condition respected</li>
    * </ul>
    * Operand stack : ..., value1, value2 => ...<br>
    * Where :
    * <ul>
    * <li>value1 : first int</li>
    * <li>value2 : second int</li>
    * </ul>
    */
   public static final String IF_ICMPLT       = "IF_ICMPLT";
   /**
    * Description : Branch if int comparison (not equal) succeeds<br>
    * Syntax :<br>
    * <code>IF_ICMPNE &lt;label&gt;</code><br>
    * Where :
    * <ul>
    * <li>label : Label name to go if condition respected</li>
    * </ul>
    * Operand stack : ..., value1, value2 => ...<br>
    * Where :
    * <ul>
    * <li>value1 : first int</li>
    * <li>value2 : second int</li>
    * </ul>
    */
   public static final String IF_ICMPNE       = "IF_ICMPNE";
   /**
    * Description : Branch if int comparison (equal) with zero succeeds<br>
    * Syntax :<br>
    * <code>IFEQ &lt;label&gt;</code><br>
    * Where :
    * <ul>
    * <li>label : Label name to go if condition respected</li>
    * </ul>
    * Operand stack : ..., value => ...<br>
    * Where :
    * <ul>
    * <li>value : int to compare at 0</li>
    * </ul>
    */
   public static final String IFEQ            = "IFEQ";
   /**
    * Description : Branch if int comparison (greater or equal) with zero succeeds<br>
    * Syntax :<br>
    * <code>IFGE &lt;label&gt;</code><br>
    * Where :
    * <ul>
    * <li>label : Label name to go if condition respected</li>
    * </ul>
    * Operand stack : ..., value => ...<br>
    * Where :
    * <ul>
    * <li>value : int to compare at 0</li>
    * </ul>
    */
   public static final String IFGE            = "IFGE";
   /**
    * Description : Branch if int comparison (greater) with zero succeeds<br>
    * Syntax :<br>
    * <code>IFGT &lt;label&gt;</code><br>
    * Where :
    * <ul>
    * <li>label : Label name to go if condition respected</li>
    * </ul>
    * Operand stack : ..., value => ...<br>
    * Where :
    * <ul>
    * <li>value : int to compare at 0</li>
    * </ul>
    */
   public static final String IFGT            = "IFGT";
   /**
    * Description : Branch if int comparison (lower or equal) with zero succeeds<br>
    * Syntax :<br>
    * <code>IFLE &lt;label&gt;</code><br>
    * Where :
    * <ul>
    * <li>label : Label name to go if condition respected</li>
    * </ul>
    * Operand stack : ..., value => ...<br>
    * Where :
    * <ul>
    * <li>value : int to compare at 0</li>
    * </ul>
    */
   public static final String IFLE            = "IFLE";
   /**
    * Description : Branch if int comparison (lower) with zero succeeds<br>
    * Syntax :<br>
    * <code>IFLT &lt;label&gt;</code><br>
    * Where :
    * <ul>
    * <li>label : Label name to go if condition respected</li>
    * </ul>
    * Operand stack : ..., value => ...<br>
    * Where :
    * <ul>
    * <li>value : int to compare at 0</li>
    * </ul>
    */
   public static final String IFLT            = "IFLT";
   /**
    * Description : Branch if int comparison (not equal) with zero succeeds<br>
    * Syntax :<br>
    * <code>IFNE &lt;label&gt;</code><br>
    * Where :
    * <ul>
    * <li>label : Label name to go if condition respected</li>
    * </ul>
    * Operand stack : ..., value => ...<br>
    * Where :
    * <ul>
    * <li>value : int to compare at 0</li>
    * </ul>
    */
   public static final String IFNE            = "IFNE";
   /**
    * Description : Branch if reference not null<br>
    * Syntax :<br>
    * <code>IFNONNULL &lt;label&gt;</code><br>
    * Where :
    * <ul>
    * <li>label : Label name to go if condition respected</li>
    * </ul>
    * Operand stack : ..., value => ...<br>
    * Where :
    * <ul>
    * <li>value : reference object to compare with null</li>
    * </ul>
    */
   public static final String IFNONNULL       = "IFNONNULL";
   /**
    * Description : Branch if reference is null<br>
    * Syntax :<br>
    * <code>IFNULL &lt;label&gt;</code><br>
    * Where :
    * <ul>
    * <li>label : Label name to go if condition respected</li>
    * </ul>
    * Operand stack : ..., value => ...<br>
    * Where :
    * <ul>
    * <li>value : reference object to compare with null</li>
    * </ul>
    */
   public static final String IFNULL          = "IFNULL";
   /**
    * Description : Increment local variable by constant<br>
    * Syntax :<br>
    * <code>IINC &lt;name&gt; &lt;value&gt;</code><br>
    * Where :
    * <ul>
    * <li>name : Current method variable name OR current method parameter name</li>
    * <li>value : Value to increment with (int : signed byte)</li>
    * </ul>
    * Operand stack : No change<br>
    */
   public static final String IINC            = "IINC";
   /**
    * Description : Load int from local variable<br>
    * Syntax :<br>
    * <code>ILOAD &lt;name&gt;</code><br>
    * Where :
    * <ul>
    * <li>name : Current method variable name OR current method parameter name</li>
    * </ul>
    * Operand stack : ... => ..., value<br>
    * Where :
    * <ul>
    * <li>value : method variable or parameter value (int)</li>
    * </ul>
    * Details: If method variable, MUST be previously initialized (Even with 0)
    */
   public static final String ILOAD           = "ILOAD";
   /**
    * Description : Implementation dependent instruction (Not recommend to use it)<br>
    * Syntax :<br>
    * <code>IMPDEP1</code><br>
    */
   public static final String IMPDEP1         = "IMPDEP1";
   /**
    * Description : Implementation dependent instruction (Not recommend to use it)<br>
    * Syntax :<br>
    * <code>IMPDEP2</code><br>
    */
   public static final String IMPDEP2         = "IMPDEP2";
   /**
    * Description : Multiply int<br>
    * Syntax :<br>
    * <code>IMUL</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first int</li>
    * <li>value2 : second int</li>
    * <li>result : multiplication result (int)</li>
    * </ul>
    */
   public static final String IMUL            = "IMUL";
   /**
    * Description : Negate int<br>
    * Syntax :<br>
    * <code>INEG</code><br>
    * Operand stack : ..., value => ..., result<br>
    * Where :
    * <ul>
    * <li>value : value to negate (int)</li>
    * <li>result : negative result (int)</li>
    * </ul>
    */
   public static final String INEG            = "INEG";
   /**
    * Description : Determine if object is of given type<br>
    * Syntax :<br>
    * <code>INSTANCEOF &ltClassName&gt;</code><br>
    * Where :
    * <ul>
    * <li>ClassName : class name in complete version or short version (If short version must be in imports, java.lang or current
    * class package)</li>
    * </ul>
    * Operand stack : .., objectref => ..., result<br>
    * Where :
    * <ul>
    * <li>objectref : Reference on object to check the instance</li>
    * <li>result : Comparison result (int)</li>
    * </ul>
    * Details: If objectref is null, the instanceof instruction pushes an int result of 0 as an int on the operand stack.<br>
    * Otherwise, the named class, array, or interface type is resolved (§5.4.3.1). If objectref is an instance of the resolved
    * class or array or implements the resolved interface, the instanceof instruction pushes an int result of 1 as an int on the
    * operand stack; otherwise, it pushes an int result of 0
    */
   public static final String INSTANCEOF      = "INSTANCEOF";
   /**
    * Description : Invoke interface method<br>
    * Syntax :<br>
    * <code>INVOKEINTERFACE &lt;methodCompleteDescription&gt; &lt;number&gt;</code><br>
    * Where :
    * <ul>
    * <li>methodCompleteDescription : Complete reference to a method :
    * &lt;classCompleteName&gt;.&lt;methodName&gt;&lt;methodSignature&gt;</li>
    * <li>number : number of arguments > 0</li>
    * </ul>
    * <br>
    * Operand stack : ..., objectref, [arg1, [arg2 ...]] => ...<br>
    * Where :
    * <ul>
    * <li>objectref : Reference to object to call</li>
    * <li>arg(i) : arguments send to method
    * <li>
    * </ul>
    * Details:Let C be the class of objectref. The actual method to be invoked is selected by the following lookup procedure:
    * <ul>
    * <li>If C contains a declaration for an instance method with the same name and descriptor as the resolved method, then this
    * is the method to be invoked, and the lookup procedure terminates.</li>
    * <li>Otherwise, if C has a superclass, this same lookup procedure is performed recursively using the direct superclass of
    * C; the method to be invoked is the result of the recursive invocation of this lookup procedure.</li>
    * <li>Otherwise, an AbstractMethodError is raised.</li>
    * </ul>
    */
   public static final String INVOKEINTERFACE = "INVOKEINTERFACE";
   /**
    * Description : Invoke instance method; special handling for superclass, private, and instance initialization method
    * invocations<br>
    * Syntax :<br>
    * <code>INVOKESPECIAL &lt;methodCompleteDescription&gt;</code><br>
    * Where :
    * <ul>
    * <li>methodCompleteDescription : Complete reference to a method :
    * &lt;classCompleteName&gt;.&lt;methodName&gt;&lt;methodSignature&gt;</li>
    * </ul>
    * Operand stack : ..., objectref, [arg1, [arg2 ...]] => ...<br>
    * Where :
    * <ul>
    * <li>objectref : Reference to object to call</li>
    * <li>arg(i) : arguments send to method
    * <li>
    * </ul>
    */
   public static final String INVOKESPECIAL   = "INVOKESPECIAL";
   /**
    * Description : Invoke a class (static) method<br>
    * Syntax :<br>
    * <code>INVOKESPECIAL &lt;methodCompleteDescription&gt;</code><br>
    * Where :
    * <ul>
    * <li>methodCompleteDescription : Complete reference to a method :
    * &lt;classCompleteName&gt;.&lt;methodName&gt;&lt;methodSignature&gt;</li>
    * </ul>
    * Operand stack : ..., [arg1, [arg2 ...]] => ...<br>
    * Where :
    * <ul>
    * <li>arg(i) : arguments send to method
    * <li>
    * </ul>
    */
   public static final String INVOKESTATIC    = "INVOKESTATIC";
   /**
    * Description : Invoke instance method; dispatch based on class<br>
    * Syntax :<br>
    * <code>INVOKEVIRTUAL &lt;methodCompleteDescription&gt;</code><br>
    * Where :
    * <ul>
    * <li>methodCompleteDescription : Complete reference to a method :
    * &lt;classCompleteName&gt;.&lt;methodName&gt;&lt;methodSignature&gt;</li>
    * </ul>
    * Operand stack : ..., objectref, [arg1, [arg2 ...]] => ...<br>
    * Where :
    * <ul>
    * <li>objectref : Reference to object to call</li>
    * <li>arg(i) : arguments send to method
    * <li>
    * </ul>
    */
   public static final String INVOKEVIRTUAL   = "INVOKEVIRTUAL";
   /**
    * Description : Or on int<br>
    * Syntax :<br>
    * <code>IOR</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first int</li>
    * <li>value2 : second int</li>
    * <li>result : or result (int)</li>
    * </ul>
    */
   public static final String IOR             = "IOR";
   /**
    * Description : Remainder on int<br>
    * Syntax :<br>
    * <code>IREM</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first int</li>
    * <li>value2 : second int</li>
    * <li>result : remainder result (int)</li>
    * </ul>
    */
   public static final String IREM            = "IREM";
   /**
    * Description : Return int from method<br>
    * Syntax :<br>
    * <code>IRETURN</code><br>
    * Operand stack : ..., value => [empty]<br>
    * Where :
    * <ul>
    * <li>value : Value to return (int)</li>
    * </ul>
    */
   public static final String IRETURN         = "IRETURN";
   /**
    * Description : Shift left int (&lt;&lt;)<br>
    * Syntax :<br>
    * <code>ISHL</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first int</li>
    * <li>value2 : second int</li>
    * <li>result : shift left result (int)</li>
    * </ul>
    */
   public static final String ISHL            = "ISHL";
   /**
    * Description : Shift right int (&gt;&gt;)<br>
    * Syntax :<br>
    * <code>ISHR</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first int</li>
    * <li>value2 : second int</li>
    * <li>result : shift right result (int)</li>
    * </ul>
    */
   public static final String ISHR            = "ISHR";
   /**
    * Description : Store int to local variable<br>
    * Syntax :<br>
    * <code>ISTORE &lt;name&gt;</code><br>
    * Where :
    * <ul>
    * <li>name : Current method variable name OR current method parameter name</li>
    * </ul>
    * Operand stack : ..., value => ... <br>
    * Where :
    * <ul>
    * <li>value : value to put in method variable or parameter (int)</li>
    * </ul>
    */
   public static final String ISTORE          = "ISTORE";
   /**
    * Description : Subtract int<br>
    * Syntax :<br>
    * <code>ISUB</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first int</li>
    * <li>value2 : second int</li>
    * <li>result : subtraction result (int)</li>
    * </ul>
    */
   public static final String ISUB            = "ISUB";
   /**
    * Description : Logical shift right int (&gt;&gt;&gt;)<br>
    * Syntax :<br>
    * <code>ISHR</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first int</li>
    * <li>value2 : second int</li>
    * <li>result : shift right result (int)</li>
    */
   public static final String IUSHR           = "IUSHR";
   /**
    * Description : XOR on int<br>
    * Syntax :<br>
    * <code>IXOR</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first int</li>
    * <li>value2 : second int</li>
    * <li>result : XOR result (int)</li>
    * </ul>
    */
   public static final String IXOR            = "IXOR";
   /**
    * Description : Jump subroutine<br>
    * Syntax :<br>
    * <code>JSR &lt;label&gt;</code><br>
    * Where :
    * <ul>
    * <li>label : Label to jump</li>
    * </ul>
    * Operand stack : ... => ..., address<br>
    * Where :
    * <ul>
    * <li>address : Address to return</li>
    * </ul>
    * Details: In sub routine remember to store return address in local variable to use with {@link #RET}
    */
   public static final String JSR             = "JSR";
   /**
    * Description : Jump subroutine<br>
    * Syntax :<br>
    * <code>JSR_W &lt;label&gt;</code><br>
    * Where :
    * <ul>
    * <li>label : Label to jump</li>
    * </ul>
    * Operand stack : ... => ..., address<br>
    * Where :
    * <ul>
    * <li>address : Address to return</li>
    * </ul>
    * Details: In sub routine remember to store return address in local variable to use with {@link #RET}<br>
    * {@link #JSR_W} are for long jump, use {@link #JSR} and compiler will choose the best one for you
    */
   public static final String JSR_W           = "JSR_W";
   /**
    * Description : Convert long to double<br>
    * Syntax :<br>
    * <code>L2D</code><br>
    * Operand stack : ..., value => ..., result<br>
    * Where :
    * <ul>
    * <li>value : value to convert (long)</li>
    * <li>result : converted value (double)</li>
    * </ul>
    * Details: Converted to a double result using IEEE 754 round to nearest mode
    */
   public static final String L2D             = "L2D";
   /**
    * Description : Convert long to float<br>
    * Syntax :<br>
    * Operand stack : ..., value => ..., result<br>
    * Where :
    * <ul>
    * <li>value : value to convert (long)</li>
    * <li>result : converted value (float)</li>
    * </ul>
    * Details: Converted to a float result using IEEE 754 round to nearest mode.
    */
   public static final String L2F             = "L2F";
   /**
    * Description : Convert long to int<br>
    * Syntax :<br>
    * <code>L2I</code><br>
    * Operand stack : ..., value => ..., result<br>
    * Where :
    * <ul>
    * <li>value : value to convert (long)</li>
    * <li>result : converted value (int)</li>
    * </ul>
    * Details: Converted to an int result by taking the low-order 32 bits of the long value and discarding the high-order 32
    * bits.
    */
   public static final String L2I             = "L2I";
   /**
    * Description : Add long<br>
    * Syntax :<br>
    * <code>LADD</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first long</li>
    * <li>value2 : second long</li>
    * <li>result : addition result (long)</li>
    * </ul>
    */
   public static final String LADD            = "LADD";
   /**
    * Description : Load long from array<br>
    * Syntax :<br>
    * <code>LALOAD</code><br>
    * Operand stack : ..., arrayref, index => ..., value<br>
    * Where :
    * <ul>
    * <li>arrayref : Reference on array</li>
    * <li>index : Array index (int)</li>
    * <li>value : Value in array at given index (long)</li>
    * </ul>
    */
   public static final String LALOAD          = "LALOAD";
   /**
    * Description : And on long<br>
    * Syntax :<br>
    * <code>LAND</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first long</li>
    * <li>value2 : second long</li>
    * <li>result : and result (long)</li>
    * </ul>
    */
   public static final String LAND            = "LAND";
   /**
    * Description : Load long from array<br>
    * Syntax :<br>
    * <code>LASTORE</code><br>
    * Operand stack : ..., arrayref, index, value => ...<br>
    * Where :
    * <ul>
    * <li>arrayref : Reference on array</li>
    * <li>index : Array index (int)</li>
    * <li>value : Value to put in array at given index (long)</li>
    * </ul>
    */
   public static final String LASTORE         = "LASTORE";
   /**
    * Description : Compare long<br>
    * Syntax :<br>
    * <code>LCMP</code><br>
    * Operand stack : ..., value1, value2 => result<br>
    * Where :
    * <ul>
    * <li>value1 : first long</li>
    * <li>value2 : second long</li>
    * <li>result : comparison result (int)</li>
    * </ul>
    * <br>
    * Details:If value1 is greater than value2, the int value 1 is pushed onto the operand stack. If value1 is equal to value2,
    * the int value 0 is pushed onto the operand stack. If value1 is less than value2, the int value -1 is pushed onto the
    * operand stack.
    */
   public static final String LCMP            = "LCMP";
   /**
    * Description : Push long constant 0 or 1<br>
    * Syntax :<br>
    * <code>LCONST &lt;value&gt;</code><br>
    * Where :
    * <ul>
    * <li>value : value to push 0 or 1</li>
    * </ul>
    * Operand stack : ... => ..., value<br>
    * Where :
    * <ul>
    * <li>value pushed value 0 or 1 (long)</li>
    * </ul>
    */
   public static final String LCONST          = "LCONST";
   /**
    * Description : Push item from run-time constant pool. Laod a constant<br>
    * Syntax :<br>
    * <code>LDC &lt;value&gt;</code><br>
    * Where :
    * <ul>
    * <li>value : constant value</li>
    * </ul>
    * Operand stack : ... => ..., value<br>
    * Where :
    * <ul>
    * <li>value reference to value (If String) or value itself</li>
    * </ul>
    * Details: Value of constant can be :
    * <ul>
    * <li>boolean : true or false</li>
    * <li>character : 'a', 'z', '\n', ... (Must be inside ' like Java convention for character)</li>
    * <li>int : 123, 89, 20, -9, ...</li>
    * <li>float : 1.23f, 5f, -.36f, ... Must end with f</li>
    * <li>String : "This is a phrase.", "", "Blabla\n\t\"New line\"", ... Like Java String convention</li>
    * </ul>
    * Since byte and short are carry by int, use int for them<br>
    * For long and double use {@link #LDC2_W}
    */
   public static final String LDC             = "LDC";
   /**
    * Description : Push item from run-time constant pool. Laod a constant<br>
    * Syntax :<br>
    * <code>LDC_W &lt;value&gt;</code><br>
    * Where :
    * <ul>
    * <li>value : constant value</li>
    * </ul>
    * Operand stack : ... => ..., value<br>
    * Where :
    * <ul>
    * <li>value reference to value (If String) or value itself</li>
    * </ul>
    * Details: Value of constant can be :
    * <ul>
    * <li>boolean : true or false</li>
    * <li>character : 'a', 'z', '\n', ... (Must be inside ' like Java convention for character)</li>
    * <li>int : 123, 89, 20, -9, ...</li>
    * <li>float : 1.23f, 5f, -.36f, ... Must end with f</li>
    * <li>String : "This is a phrase.", "", "Blabla\n\t\"New line\"", ... Like Java String convention</li>
    * </ul>
    * Since byte and short are carry by int, use int for them<br>
    * For long and double use {@link #LDC2_W}
    */
   public static final String LDC_W           = "LDC_W";
   /**
    * Description : Push item from run-time constant pool. Laod a constant<br>
    * Syntax :<br>
    * <code>LDC2_W &lt;value&gt;</code><br>
    * Where :
    * <ul>
    * <li>value : constant value</li>
    * </ul>
    * Operand stack : ... => ..., value<br>
    * Where :
    * <ul>
    * <li>value reference to value (If String) or value itself</li>
    * </ul>
    * Details: Value of constant can be :
    * <ul>
    * <li>long : 3l, 40L, -8l, ... end with l like Java</li>
    * <li>double : 12.3, 0.3, 8d, 3.2D, d at end is mandatory if can be confused with int (No decimal separator)</li>
    * </ul>
    * For other constants use {@link #LDC} or {@link #LDC_W}
    */
   public static final String LDC2_W          = "LDC2_W";
   /**
    * Description : Divide long<br>
    * Syntax :<br>
    * <code>LDIV</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first long</li>
    * <li>value2 : second long</li>
    * <li>result : division result (long)</li>
    * </ul>
    */
   public static final String LDIV            = "LDIV";
   /**
    * Description : Load long from local variable<br>
    * Syntax :<br>
    * <code>LLOAD &lt;name&gt;</code><br>
    * Where :
    * <ul>
    * <li>name : Current method variable name OR current method parameter name</li>
    * </ul>
    * Operand stack : ... => ..., value<br>
    * Where :
    * <ul>
    * <li>value : method variable or parameter value (long)</li>
    * </ul>
    * Details: If method variable, MUST be previously initialized (Even with 0)
    */
   public static final String LLOAD           = "LLOAD";
   /**
    * Description : Multiply long<br>
    * Syntax :<br>
    * <code>LMUL</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first long</li>
    * <li>value2 : second long</li>
    * <li>result : multiplication result (long)</li>
    * </ul>
    */
   public static final String LMUL            = "LMUL";
   /**
    * Description : Negate long<br>
    * Syntax :<br>
    * <code>LNEG</code><br>
    * Operand stack : ..., value => ..., result<br>
    * Where :
    * <ul>
    * <li>value : Value to negate (long)</li>
    * <li>result : Negative result (long)</li>
    * </ul>
    * <br>
    * Details:
    */
   public static final String LNEG            = "LNEG";
   /**
    * Description : Switch to label depends on given key<br>
    * Syntax :<br>
    * <code>LOOKUPSWITCH (&lt;match&gt; &lt;label&gt;)* &lt;defaultLabel&gt;  </code><br>
    * Where :
    * <ul>
    * <li>match : Match value (int)</li>
    * <li>label : Label to go if key match to match value</li>
    * <li>defaultLabel : Label to go if key not match</li>
    * </ul>
    * <br>
    * Operand stack : ..., key => ...<br>
    * Where :
    * <ul>
    * <li>key : the key value (int)</li>
    * </ul>
    * <br>
    * Details: {@link #LOOKUPSWITCH} is designed for switch can't respect {@link #TABLESWITCH} constraints.<br>
    * If you don't know the best choose between {@link #LOOKUPSWITCH} and {@link #TABLESWITCH} use the {@link #SWITCH}
    * instruction it will choose the best optimized instruction to use
    */
   public static final String LOOKUPSWITCH    = "LOOKUPSWITCH";
   /**
    * Description : Or on long<br>
    * Syntax :<br>
    * <code>LOR</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first long</li>
    * <li>value2 : second long</li>
    * <li>result : or result (long)</li>
    * </ul>
    */
   public static final String LOR             = "LOR";
   /**
    * Description : Remainder long<br>
    * Syntax :<br>
    * <code>LREM</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first long</li>
    * <li>value2 : second long</li>
    * <li>result : remainder result (long)</li>
    * </ul>
    */
   public static final String LREM            = "LREM";
   /**
    * Description : Return long from method<br>
    * Syntax :<br>
    * <code>LRETURN</code><br>
    * Operand stack : ..., value => [empty]<br>
    * Where :
    * <ul>
    * <li>value : Value to return (long)</li>
    * </ul>
    */
   public static final String LRETURN         = "LRETURN";
   /**
    * Description : Shift left long (&lt;&lt;)<br>
    * Syntax :<br>
    * <code>LSHL</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first long</li>
    * <li>value2 : second int</li>
    * <li>result : shift left result (long)</li>
    * </ul>
    */
   public static final String LSHL            = "LSHL";
   /**
    * Description : Shift right long (&gt;&gt;)<br>
    * Syntax :<br>
    * <code>LSHR</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first long</li>
    * <li>value2 : second int</li>
    * <li>result : shift right result (long)</li>
    * </ul>
    */
   public static final String LSHR            = "LSHR";
   /**
    * Description : Store long to local variable<br>
    * Syntax :<br>
    * <code>LLOAD &lt;name&gt;</code><br>
    * Where :
    * <ul>
    * <li>name : Current method variable name OR current method parameter name</li>
    * </ul>
    * Operand stack : ..., value => ...<br>
    * Where :
    * <ul>
    * <li>value : value to put in method variable or parameter (long)</li>
    * </ul>
    */
   public static final String LSTORE          = "LSTORE";
   /**
    * Description : Subtract long <br>
    * Syntax :<br>
    * <code>LSUB</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first long</li>
    * <li>value2 : second long</li>
    * <li>result : subtraction result (long)</li>
    * </ul>
    */
   public static final String LSUB            = "LSUB";
   /**
    * Description : Logical Shift right long (&lt;&lt;&lt;)<br>
    * Syntax :<br>
    * <code>LSHR</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first long</li>
    * <li>value2 : second int</li>
    * <li>result : shift right result (long)</li>
    * </ul>
    */
   public static final String LUSHR           = "LUSHR";
   /**
    * Description : XOR long<br>
    * Syntax :<br>
    * <code>LSHR</code><br>
    * Operand stack : ..., value1, value2 => ..., result<br>
    * Where :
    * <ul>
    * <li>value1 : first long</li>
    * <li>value2 : second long</li>
    * <li>result : XOR result (long)</li>
    * </ul>
    */
   public static final String LXOR            = "LXOR";
   /**
    * Description : Enter monitor for object<br>
    * Syntax :<br>
    * <code>MONITORENTER</code><br>
    * Operand stack : ..., objectref => ...<br>
    * Where :
    * <ul>
    * <li>objectref : reference of object to enter in monitor</li>
    * </ul>
    * Details: Each object is associated with a monitor. A monitor is locked if and only if it has an owner. The thread that
    * executes monitorenter attempts to gain ownership of the monitor associated with objectref, as follows:
    * <ul>
    * <li>If the entry count of the monitor associated with objectref is zero, the thread enters the monitor and sets its entry
    * count to one. The thread is then the owner of the monitor.</li>
    * <li>If the thread already owns the monitor associated with objectref, it reenters the monitor, incrementing its entry
    * count.</li>
    * <li>If another thread already owns the monitor associated with objectref, the thread blocks until the monitor's entry
    * count is zero, then tries again to gain ownership.</li>
    * </ul>
    */
   public static final String MONITORENTER    = "MONITORENTER";
   /**
    * Description : Exit monitor for object<br>
    * Syntax :<br>
    * <code>MONITOREXIT</code><br>
    * Operand stack : ..., objectref => ...<br>
    * Where :
    * <ul>
    * <li>objectref: reference to object to exit monitor</li>
    * </ul>
    * Details: The thread that executes monitorexit must be the owner of the monitor associated with the instance referenced by
    * objectref. <br>
    * The thread decrements the entry count of the monitor associated with objectref. If as a result the value of the entry
    * count is zero, the thread exits the monitor and is no longer its owner. Other threads that are blocking to enter the
    * monitor are allowed to attempt to do so.
    */
   public static final String MONITOREXIT     = "MONITOREXIT";
   /**
    * Description : Create new multidimensional array<br>
    * Syntax :<br>
    * <code>MULTIANEWARRAY &ľt;type&gt; &lt;numberOfDimensions&gt;</code><br>
    * Where :
    * <ul>
    * <li>type : Array type (signature or class name)</li>
    * <li>numberOfDimensions : Number of dimensions (int)</li>
    * </ul>
    * Operand stack : .., count1, [count2, ...] => ..., arrayref<br>
    * Where :
    * <ul>
    * <li>count(i) : size for dimension i</li>
    * <li>arrayref : reference on created array</li>
    * </ul>
    */
   public static final String MULTIANEWARRAY  = "MULTIANEWARRAY";
   /**
    * Description : Create new object<br>
    * Syntax :<br>
    * <code>NEW &lt;type&gt;</code><br>
    * Where :
    * <ul>
    * <li>type : array type class name or signature</li>
    * </ul>
    * Operand stack : ... => ..., objectref<br>
    * Where :
    * <ul>
    * <li>objectref : reference on created object</li>
    * </ul>
    */
   public static final String NEW             = "NEW";
   /**
    * Description : Create new array<br>
    * Syntax :<br>
    * <code>NEWARRAY &lt;primitiveType&gt;</code><br>
    * Where :
    * <ul>
    * <li>primitiveType : primitive array type : boolean, char, byte, short, int, long, float, double</li>
    * </ul>
    * Operand stack : ..., count => ..., arrayref<br>
    * Where :
    * <ul>
    * <li>count : Array size (int)</li>
    * <li>arrayref : Reference on array</li>
    * </ul>
    */
   public static final String NEWARRAY        = "NEWARRAY";
   /**
    * Description : Do nothing<br>
    * Syntax :<br>
    * <code>NOP</code><br>
    * Operand stack : No change
    */
   public static final String NOP             = "NOP";
   /**
    * Description : Pop the top operand stack value<br>
    * Syntax :<br>
    * <code>POP</code><br>
    * Operand stack : .., value => ...<br>
    * Where :
    * <ul>
    * <li>value : value to pop (Not long nor double)</li>
    * </ul>
    */
   public static final String POP             = "POP";
   /**
    * Description : Pop the top one or two operand stack values<br>
    * Syntax :<br>
    * <code>POP2</code><br>
    * Operand stack : ..., value2, value1 => ...<br>
    * Where :
    * <ul>
    * <li>value1 : value to pop (Not long nor double)</li>
    * <li>value2 : value to pop (Not long nor double)</li>
    * </ul>
    * <br>
    * <h1><b>OR</b></h1><br>
    * <br>
    * Operand stack : ..., value => ...<br>
    * Where :
    * <ul>
    * <li>value : value to pop (long or double)</li>
    * </ul>
    */
   public static final String POP2            = "POP2";
   /**
    * Description : Convenient instruction for push a value (It choose the good instruction for push the value)<br>
    * Syntax :<br>
    * <code>PUSH  &lt;constantValue&gt;</code><br>
    * Where :
    * <ul>
    * <li>constantValue : constant value to push</li>
    * </ul>
    * Operand stack : ... => ..., value<br>
    * Where :
    * <ul>
    * <li>value : Value pushed</li>
    * </ul>
    * Details: The constant can have several form :
    * <ul>
    * <li>boolean : true or false (int pushed)</li>
    * <li>char : 'a', 'z', '\t', ... like Java character (int pushed)</li>
    * <li>int : 12, -5, 3, ... (int pushed)</li>
    * <li>long : 15l, -8L, ... L end to indicates it is long (long pushed)</li>
    * <li>float : 1f, 0.2f, .3F, ... F end to indicates it is float (float pushed)</li>
    * <li>double : 1.0, 5d, 3.2D, ... with ., or end with D (double pushed)</li>
    * <li>String : "file", "", "phrase\nhi" Like Java String (reference to String pushed)</li>
    * </ul>
    */
   public static final String PUSH            = "PUSH";
   /**
    * Description : Set field in object<br>
    * Syntax :<br>
    * <code>PUTFIELD &lt;fieldName&gt;</code><br>
    * Where :
    * <ul>
    * <li>fieldName : Name of the field</li>
    * </ul>
    * Operand stack : ..., objectref, value => ...<br>
    * Where :
    * <ul>
    * <li>objectref : Reference on object (Must be this)</li>
    * <li>value : Value to push on field</li>
    * </ul>
    */
   public static final String PUTFIELD        = "PUTFIELD";
   /**
    * Description : Set static field in object<br>
    * Syntax :<br>
    * <code>PUTFIELD &lt;fieldName&gt;</code><br>
    * Where :
    * <ul>
    * <li>fieldName : Name of the field</li>
    * </ul>
    * Operand stack : ..., value => ...<br>
    * Where :
    * <ul>
    * <li>value : Value to push on field</li>
    * </ul>
    */
   public static final String PUTSTATIC       = "PUTSTATIC";
   /**
    * Description : Return from subroutine<br>
    * Syntax :<br>
    * <code>RET &lt;localVaraible&gt;</code><br>
    * Where :
    * <ul>
    * <li>localVaraible : Name of local variable where return address is stored</li>
    * </ul>
    * Operand stack : No change <br>
    * Details: It is used in combination with {@link #JSR}/{@link #JSR_W}.<br>
    * When enter in subroutine, the return address is on the top of the stack, if you want things go properly, you can adopt 2
    * strategies :
    * <ol>
    * <li>The most easy is to store the return address in local variable as first instruction of subroutine and not modify this
    * variable until the end of the subroutine to use it only for {@link #RET}.</li>
    * <li>Or pay attention to stack status in way that the address not delete from the stack and be at top at the end of
    * subroutine and store it in local variable just before call the {@link #RET}.</li>
    * </ol>
    */
   public static final String RET             = "RET";
   /**
    * Description : Return void from method<br>
    * Syntax :<br>
    * <code>RETURN</code><br>
    * Operand stack : ... => [empty]<br>
    */
   public static final String RETURN          = "RETURN";
   /**
    * Description : Load short from array<br>
    * Syntax :<br>
    * <code>SALOAD</code><br>
    * Operand stack : ..., arrayref, index => ..., value<br>
    * Where :
    * <ul>
    * <li>arrayref : Array reference</li>
    * <li>index : Array index (int)</li>
    * <li>value : Value in array at index (int)</li>
    * </ul>
    * Details: The short value is sign-extended to an int value
    */
   public static final String SALOAD          = "SALOAD";
   /**
    * Description : Store short to array<br>
    * Syntax :<br>
    * <code>SASTORE</code><br>
    * Operand stack : ..., arrayref, index, value => ...<br>
    * Where :
    * <ul>
    * <li>arrayref : Array reference</li>
    * <li>index : Array index (int)</li>
    * <li>value : Value to put in array at index (int)</li>
    * </ul>
    * Details: The int value is truncated to short value
    */
   public static final String SASTORE         = "SASTORE";
   /**
    * Description : Push short<br>
    * Syntax :<br>
    * <code>SIPUSH &lt;value&gt;</code><br>
    * Where :
    * <ul>
    * <li>value : Value to push (short)</li>
    * </ul>
    * Operand stack : ... => ..., value<br>
    * Where :
    * <ul>
    * <li>value ; Value pushed (int)</li>
    * </ul>
    * Details: The short value is sign-extended to an int value
    */
   public static final String SIPUSH          = "SIPUSH";
   /**
    * Description : Swap the top two operand stack values<br>
    * Syntax :<br>
    * <code>SWAP</code><br>
    * Operand stack : ..., value1, value2 => ..., value2, value1<br>
    * Where :
    * <ul>
    * <li>value1 : Value to swap (Not double nor long)</li>
    * <li>value2 : Value to swap (Not double nor long)</li>
    * </ul>
    */
   public static final String SWAP            = "SWAP";
   /**
    * Description : Switch to label depends on given key<br>
    * Syntax :<br>
    * <code>SWITCH (&lt;match&gt; &lt;label&gt;)* &lt;defaultLabel&gt;  </code><br>
    * Where :
    * <ul>
    * <li>match : Match value (int)</li>
    * <li>label : Label to go if key match to match value</li>
    * <li>defaultLabel : Label to go if key not match</li>
    * </ul>
    * <br>
    * Operand stack : ..., key => ...<br>
    * Where :
    * <ul>
    * <li>key : the key value (int)</li>
    * </ul>
    * <br>
    * Details: {@link #SWITCH} instruction will choose the best optimized instruction choose between {@link #LOOKUPSWITCH} and
    * {@link #TABLESWITCH} depends on given match/label pairs
    */
   public static final String SWITCH          = "SWITCH";
   /**
    * Description : Switch to label depends on given key<br>
    * Syntax :<br>
    * <code>TABLESWITCH (&lt;match&gt; &lt;label&gt;)* &lt;defaultLabel&gt;  </code><br>
    * Where :
    * <ul>
    * <li>match : Match value (int)</li>
    * <li>label : Label to go if key match to match value</li>
    * <li>defaultLabel : Label to go if key not match</li>
    * </ul>
    * <br>
    * Operand stack : ..., key => ...<br>
    * Where :
    * <ul>
    * <li>key : the key value (int)</li>
    * </ul>
    * <br>
    * Details: {@link #TABLESWITCH} is designed for switch with a limited number of case and gap between case not too big<br>
    * If you don't know the best choose between {@link #LOOKUPSWITCH} and {@link #TABLESWITCH} use the {@link #SWITCH}
    * instruction it will choose the best optimized instruction to use
    */
   public static final String TABLESWITCH     = "TABLESWITCH";

   // -----

   /**
    * Special instruction (Not opcode) for declare a label<br>
    * Syntax :<br>
    * <code>LABEL &lt;name&gt;</code><br>
    * Where <code>name</code> is the label name and respects [a-zA-Z][a-zA-Z0-9_]*
    */
   public static final String Z_LABEL         = "LABEL";
   /**
    * Special instruction for call a subroutine<br>
    * Syntax :<br>
    * <code>SUB_C &lt;name&gt;</code><br>
    * Where <code>name</code> is the subroutine name to call. <br>
    * Operand stack : ... => ..., address<br>
    * Where address is the address to return<br>
    * Details : Instructions {@link #Z_SUB_C}, {@link #Z_SUB_S} and {@link #Z_SUB_E} are to facilitate the subroutine creation
    * to resolve the problem of return address<br>
    * Here it call the subroutine, just use its name.<br>
    * Warning subroutines must be have one {@link #Z_SUB_S} and one {@link #Z_SUB_E} (The sub routine code is between this 2
    * instructions). Then they MUST be call with {@link #Z_SUB_C} (You can use it in several places), other branch instruction
    * outside subroutine code that goes inside subroutine code may cause unexpected result or crash. Same risk apply if exit
    * from subroutine code without {@link #Z_SUB_E} or call an other subroutine with {@link #Z_SUB_C}. The subroutine don't
    * manage well the recursive call here, if you want some, you have to deal with {@link #JSR}/{@link #JSR_W} and {@link #RET}
    * and apply the second strategy explains in {@link #RET}.
    */
   public static final String Z_SUB_C         = "SUB_C";
   /**
    * Special instruction for end a subroutine code<br>
    * Syntax :<br>
    * <code>SUB_E &lt;name&gt;</code><br>
    * Where <code>name</code> is the subroutine name to exit. <br>
    * Operand stack : No change <br>
    * Details : Instructions {@link #Z_SUB_C}, {@link #Z_SUB_S} and {@link #Z_SUB_E} are to facilitate the subroutine creation
    * to resolve the problem of return address<br>
    * Use this instruction to terminate the subroutine code <br>
    * Warning subroutines must be have one {@link #Z_SUB_S} and one {@link #Z_SUB_E} (The sub routine code is between this 2
    * instructions). Then they MUST be call with {@link #Z_SUB_C} (You can use it in several places), other branch instruction
    * outside subroutine code that goes inside subroutine code may cause unexpected result or crash. Same risk apply if exit
    * from subroutine code without {@link #Z_SUB_E} or call an other subroutine with {@link #Z_SUB_C}. The subroutine don't
    * manage well the recursive call here, if you want some, you have to deal with {@link #JSR}/{@link #JSR_W} and {@link #RET}
    * and apply the second strategy explains in {@link #RET}.
    */
   public static final String Z_SUB_E         = "SUB_E";
   /**
    * Special instruction for start a subroutine code<br>
    * Syntax :<br>
    * <code>SUB_S &lt;name&gt;</code><br>
    * Where <code>name</code> is the subroutine name to start. <br>
    * Operand stack : ..., address -> ... <br>
    * Details : Instructions {@link #Z_SUB_C}, {@link #Z_SUB_S} and {@link #Z_SUB_E} are to facilitate the subroutine creation
    * to resolve the problem of return address<br>
    * Use this instruction to start the subroutine code<br>
    * Warning subroutines must be have one {@link #Z_SUB_S} and one {@link #Z_SUB_E} (The sub routine code is between this 2
    * instructions). Then they MUST be call with {@link #Z_SUB_C} (You can use it in several places), other branch instruction
    * outside subroutine code that goes inside subroutine code may cause unexpected result or crash. Same risk apply if exit
    * from subroutine code without {@link #Z_SUB_E} or call an other subroutine with {@link #Z_SUB_C}. The subroutine don't
    * manage well the recursive call here, if you want some, you have to deal with {@link #JSR}/{@link #JSR_W} and {@link #RET}
    * and apply the second strategy explains in {@link #RET}.
    */
   public static final String Z_SUB_S         = "SUB_S";
   /**
    * Special instruction (Not opcode) for declare a variable<br>
    * Syntax:<br>
    * <code>VAR &lt;Type&gt; &lt;name&gt;</code><br>
    * Where <code>Type</code> is the variable type (see {@link Compiler ASM grammar definition of "Type"}. <br>
    * Where <code>name</code> is the variable name and respects [a-zA-Z][a-zA-Z0-9_]*
    */
   public static final String Z_VAR           = "VAR";
}