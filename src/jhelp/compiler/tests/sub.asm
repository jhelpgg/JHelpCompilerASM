class jhelp.asm.Sub

import jhelp.compiler.tests.Operation

implements Operation

//Overwrite the method calculate
method calculate
	parameter	int	first
	parameter	int	second
	return		int
{
	ILOAD first
	ILOAD second
	ISUB
	IRETURN
}