package de.iaas.grossmann.ccpl.generator.compiler

import de.iaas.grossmann.ccpl.cCPL.EventDefinition
import de.iaas.grossmann.ccpl.cCPL.Expression
import de.iaas.grossmann.ccpl.cCPL.Statement
import de.iaas.grossmann.ccpl.generator.compiler.helper.ExpressionCompilerHelper
import de.iaas.grossmann.ccpl.generator.compiler.helper.FromFieldCompilerHelper
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.ecore.resource.Resource
import de.iaas.grossmann.ccpl.generator.compiler.helper.SequenceCompilerHelper

class EventDefinitionCompiler extends AbstractCompiler {

	new(Resource resource) {
		super(resource)
	}
	
	override compile() {
		var content = new StringBuilder()
		content.append(compileSchemasAndWindows())
		content.append(compileInsert())
		return content.toString
	}
	
	protected def String compileInsert() {
		var builder = new StringBuilder()
		var definitionStatements = resource.allContents.toIterable.filter(Statement).filter[definition !== null];
		for (statement: definitionStatements) {
			var expressions = statement.definition.expressions
			builder.append("insert into " + statement.definition.event.name + " ")
			builder.append("select ")
			builder.append(compileExpressions(expressions, statement))
			if (statement.definition.sequence !== null) {
				builder.append(SequenceCompilerHelper.compileSequence(statement.definition.sequence))			
			} else {
				builder.append(FromFieldCompilerHelper.compile(statement))
			}
			builder.append(";\n\n")
		}
		builder.toString
	}
	
	protected def String compileExpressions(EList<Expression> expressions, Statement statement) {
		var builder = new StringBuilder()
		for (var index = 0; index < expressions.size; index++) {
			builder.append(ExpressionCompilerHelper.compile(expressions.get(index))
				+ " as " 
				+ statement.definition.event.parameters.get(index).name)
			if (index < expressions.size - 1) builder.append(", ")
		}
		builder.append(" ")
		builder.toString
	}
	
	protected def String compileSchemasAndWindows() {
		var content = new StringBuilder()
		for (eventDefinition: resource.allContents.toIterable.filter(EventDefinition)) {
			content.append(compileSchemaAndWindow(eventDefinition))
		}
		content.toString
	}
	
	def compileSchemaAndWindow(EventDefinition eventDefinition) '''
		create schema «eventDefinition.event.name»(«eventDefinition.compileParameters»);
		create window «eventDefinition.event.name»Win.win:length(1) as «eventDefinition.event.name»;
		
	'''
	
	def String compileParameters(EventDefinition eventDefinition) {
		var parameters = eventDefinition.event.parameters
		var contentBuilder = new StringBuilder()
		for (var index = 0; index < parameters.size; index++) {
			contentBuilder.append(parameters.get(index).name)
			contentBuilder.append(" ")
			contentBuilder.append(parameters.get(index).type.literal)
			if (index < parameters.size - 1) contentBuilder.append(", ")
		}
		return contentBuilder.toString
	}	
}
