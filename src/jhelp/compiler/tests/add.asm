//Operation that does an addition
class jhelp.asm.Add

// *************************
// *** Declaration block ***
// *************************

import jhelp.compiler.tests.Operation

implements Operation

// **************
// *** Fields ***
// **************

 ; No fields

// ***************
// *** Methods ***
// ***************

//Overwrite the method calculate for do the addition
method calculate
	parameter	int	first
	parameter	int	second
	return		int
{
	ILOAD first		;	Put the parameter 'first' on the method stack	:	[]						=>	[first]
	ILOAD second	;	Put the parameter 'second' on the method stack	:	[first]				=>	[first, second]
	IADD				;	Add the parameters										:	[first, second]	=>	[first+second]
	IRETURN			;	Return the result											:	[first+second]		=>	[]
}