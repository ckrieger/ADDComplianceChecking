package de.iaas.grossmann.ccpl.generator.compiler.helper

import de.iaas.grossmann.ccpl.cCPL.Expression
import de.iaas.grossmann.ccpl.cCPL.OrExpression
import de.iaas.grossmann.ccpl.cCPL.AndExpression
import de.iaas.grossmann.ccpl.cCPL.NotExpression
import de.iaas.grossmann.ccpl.cCPL.CompareExpression
import de.iaas.grossmann.ccpl.cCPL.MathExpression
import de.iaas.grossmann.ccpl.cCPL.MathTerm
import de.iaas.grossmann.ccpl.cCPL.Literal
import de.iaas.grossmann.ccpl.cCPL.ParameterReference

class ExpressionCompilerHelper {
	
	static def compile(Expression expression) {
		switch (expression) {
			OrExpression: {
				compile(expression.left) + ' or ' + compile(expression.right);
			}
			AndExpression: {
				compile(expression.left) + ' and ' + compile(expression.right);
			}
			NotExpression: {
				expression.expression.compile
			}
			CompareExpression: {
				compile(expression.left) + " " + expression.operator.toString + " " + compile(expression.right)
			}
			MathExpression: {
				compile(expression.left) + " " + expression.operator.toString + " " + compile(expression.right)
			}
			MathTerm: {
				compileMathTerm(expression)
			}
		}
	}
	
	static private def String compileMathTerm(MathTerm expression) {
		if (expression.child !== null) {
			'(' + compile(expression.child) + ')'
		} else if (expression.ref !== null) {
			expression.ref.buildReferenceString
		} else if (expression.literal !== null) {
			expression.literal.buildLiteralString
		} else if (expression.function !== null) {
			if (expression.function.aggregateFunction !== null && expression.function.value !== null) {
				expression.function.aggregateFunction.literal
				+ '('
				+ expression.function.value.buildReferenceString
				+ ')'
			} else {
				'cast(count(*),int)'
			}
		} else if (expression.variable !== null) {
			'${' + expression.variable.name + '}'
		}
	}
	
	static private def String buildLiteralString(Literal literal) {
		if (literal.text !== null) {
			"'" + literal.text + "'"
		} else if (literal.doubleNumber !== null) {
			literal.doubleNumber
		} else if (literal.booleanValue !== null) {
			if (literal.booleanValue.trueValue !== null) {
				literal.booleanValue.trueValue
			} else {
				literal.booleanValue.falseValue
			}
		} else {
			Integer.toString(literal.intNumber)
		}
	}
	
	static private def String buildReferenceString(ParameterReference parameterReference) {
		var literalString = "";
		if (parameterReference.ref !== null) {
			literalString += parameterReference.ref.name + "."
		}
		literalString + parameterReference.parameter.name
	}
}