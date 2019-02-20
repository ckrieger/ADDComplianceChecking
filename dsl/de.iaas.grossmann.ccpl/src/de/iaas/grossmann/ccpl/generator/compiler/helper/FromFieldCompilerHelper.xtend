package de.iaas.grossmann.ccpl.generator.compiler.helper

import de.iaas.grossmann.ccpl.cCPL.EventCollection
import de.iaas.grossmann.ccpl.cCPL.Statement
import java.util.List

class FromFieldCompilerHelper {
	
	static def compile(Statement statement) {
		var builder = new StringBuilder("from ")
		var context = statement.scontext.collections
		for (var index = 0; index < context.size; index++) {
			builder.append(compileEventContext(context, index, false))
		}
		builder.toString
	}
	
	static def String compileEventContext(List<EventCollection> context, int index, boolean forSequence) {
		var builder = new StringBuilder()
		if (forSequence) builder.append(context.get(index).name + "=")
		builder.append(context.get(index).event.name)
		builder.append(checkAndAddEventConstraint(context, index))
		builder.append(checkAndAddEventFilter(context, index))
		builder.append(checkAndAddTimeConstraint(context, index))
		builder.append(checkAndAddWindowConstraint(context, index))
		if (!forSequence) builder.append(" " + context.get(index).name)
		if (index < context.size - 1) {
			builder.append(", ")
		}
		builder.toString
	}
	
	private static def String checkAndAddWindowConstraint(List<EventCollection> context, int index) {
		if (context.get(index).window !== null) {
			if (context.get(index).window.^var !== null) {
				".win:length(${" + context.get(index).window.^var.name + "})"
			} else {
				".win:length(" + context.get(index).window.value + ")"
			}
		} else {
			""
		}
	}
	
	private static def String checkAndAddTimeConstraint(List<EventCollection> context, int index) {
		var builder = new StringBuilder()
		if (context.get(index).time !== null) {
			builder.append(".win:time(")
			if (context.get(index).time.^var !== null) {
				builder.append( '${' + context.get(index).time.^var.name + '}' + " ")
			} else {
				builder.append(context.get(index).time.value + " ")
			}
			builder.append(context.get(index).time.timeUnit + ")")
		}
		builder.toString
	}
	
	private static def String checkAndAddEventFilter(List<EventCollection> context, int index) {
		var builder = new StringBuilder()
		if (context.get(index).filter !== null) {
			builder.append(".std:unique(")
			builder.append(context.get(index).filter.parameter.name + ")")
		}
		builder.toString
	}
	
	private static def String checkAndAddEventConstraint(List<EventCollection> context, int index) {
		var builder = new StringBuilder()
		if (context.get(index).constraint !== null) {
			builder.append("(")
			builder.append(ExpressionCompilerHelper.compile(context.get(index).constraint.expression))
			builder.append(")")
		}
		builder.toString
	}
}