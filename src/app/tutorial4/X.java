package app.tutorial4;

import ec.*;
import ec.gp.*;
import ec.util.*;

public class X extends GPNode {
	public String toString() {
		return "x";
	}

	public int expectedChildren() {
		return 0;
	}

	// Note that there are no children. Now what we'll do is define in our
	// Problem two public doubles called currentX and currentY.
	// These are the values that these nodes return when evaluated. Hence we
	// add:

	public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack,
			final GPIndividual individual, final Problem problem) {
		DoubleData rd = ((DoubleData) (input));
		rd.x = ((MultiValuedRegression)problem).currentX;
	}
}