class jhelp.asm.Mul

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
	IMUL
	IRETURN
}