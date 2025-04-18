/* Generated By:JJTree: Do not edit this line. ASTSqr.java */

package anl.verdi.parser;

import anl.verdi.formula.IllegalFormulaException;
import anl.verdi.util.DoubleFunction;
import anl.verdi.util.FormulaArray;

public class ASTSqr extends SimpleNode {

	static class Sqr implements DoubleFunction {
		public double apply(double val) {
			return Math.pow(val, 2);
		}
	}

	public ASTSqr(int id) {
		super(id);
	}

	public ASTSqr(Parser p, int id) {
		super(p, id);
	}

	/**
	 * Evaluates this Node.
	 *
	 * @param frame
	 * @return the result of the evaluation.
	 */
	@Override
	public FormulaArray evaluate(Frame frame) throws IllegalFormulaException {
		return jjtGetChild(0).evaluate(frame).foreach(new Sqr());
	}
}
