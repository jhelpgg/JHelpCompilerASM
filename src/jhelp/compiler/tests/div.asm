class jhelp.asm.Div

import jhelp.compiler.tests.Operation
import java.io.PrintStream

implements Operation

field_reference System PrintStream out systemOut

//Overwrite the method calculate
method calculate
	parameter	int	first
	parameter	int	second
	return		int
{																; []
	ILOAD first												; []									->	[first]
	ILOAD second											; [first]							->	[first, second]
	TRY ArithmeticException arithmeticException
		TRY Exception exception
			IDIV 												; [first, second]					->	[first/second]
			IRETURN											; [first/second]					->	[] EXIT
		CATCH exception issue
	CATCH arithmeticException arithmetic

LABEL issue													; [Exception]
	PUSH -1													; [Exception]						->	[Exception, -1]
	IRETURN													; [Exception, -1]					->	[] EXIT

LABEL arithmetic											; [ArithmeticException]
	PUSH -2													; [ArithmeticException]			->	[ArithmeticException, -2]
	IRETURN													; [ArithmeticException, -2]	->	[] EXIT
}