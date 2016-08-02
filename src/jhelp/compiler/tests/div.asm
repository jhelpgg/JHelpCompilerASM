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
	PUSH 1																					; [first, second, 1]
	ANEWARRAY Object																		; [first, second, arrayref]
	DUP																						; [first, second, arrayref, arrayref]
	PUSH 0																					; [first, second, arrayref, arrayref, 0]
	PUSH	"OUPS Division by zero"														; [first, second, arrayref, arrayref, 0, constref]
	AASTORE																					; [first, second, arrayref]
	INVOKESTATIC jhelp.compiler.compil.Print.print([Ljava/lang/Object;)V ; [first, second]
	IRETURN																					; [] EXIT
}