package de.iaas.grossmann.ccpl.generator.compiler

import de.iaas.grossmann.ccpl.cCPL.ConstraintTemplate
import de.iaas.grossmann.ccpl.cCPL.Statement
import de.iaas.grossmann.ccpl.generator.compiler.helper.ExpressionCompilerHelper
import de.iaas.grossmann.ccpl.generator.compiler.helper.FromFieldCompilerHelper
import de.iaas.grossmann.ccpl.generator.compiler.helper.SequenceCompilerHelper
import org.eclipse.emf.ecore.resource.Resource

class EventValidationCompiler extends AbstractCompiler {

	new(Resource resource) {
		super(resource)
	}
	
	override compile() {
		var builder = new StringBuilder()
		var violationStatements = resource.allContents.toIterable.filter(Statement).filter[violation !== null];
		var patternName = resource.allContents.toIterable.filter(ConstraintTemplate).findFirst[true].patternName
		for (statement: violationStatements) {
			builder.append("@Name('" + patternName + " Violation')")
			builder.append("select * ")
			if (statement.violation.constraint.expression !== null) {
				compileExpression(builder, statement)
			} else if (statement.violation.constraint.sequence !== null) {
				builder.append(SequenceCompilerHelper.compileSequence(statement.violation.constraint.sequence))
				builder.append(";")
			}
			builder.append("\n")
		}
		builder.toString
	}
	
	protected def StringBuilder compileExpression(StringBuilder builder, Statement statement) {
		builder.append(FromFieldCompilerHelper.compile(statement))
		builder.append(" having ")
		builder.append(ExpressionCompilerHelper.compile(statement.violation.constraint.expression))
		builder.append(";")
	}
	
}