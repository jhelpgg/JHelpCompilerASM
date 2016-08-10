class jhelp.asm.Div

import jhelp.compiler.tests.Operation

implements Operation

//Overwrite the method calculate
method calculate
	parameter	int	first
	parameter	int	second
	return		int
{                                                                       ; []
	ILOAD first																				; [first]
	ILOAD second																			; [first, second]
	DUP																						; [first, second, second]
	IFEQ zero																				; [first, second]
	IDIV 																						; [first/second]
	IRETURN																					; [] EXIT
	LABEL zero                                                           ; [first, second]
	PUSH 0																					; [first, second, 0]
	IRETURN																					; [] EXIT
}