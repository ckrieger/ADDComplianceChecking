package de.iaas.grossmann.ccpl.generator.compiler.helper

import de.iaas.grossmann.ccpl.cCPL.EventSequence
import de.iaas.grossmann.ccpl.cCPL.SequenceVariable
import java.util.Arrays

class SequenceCompilerHelper {
	
	private static var String SEQUENCE_DELIMITER = " -> ";
	
	def static compileSequence(EventSequence sequence) {
		var builder = new StringBuilder()
		builder.append("from pattern [ ")
//		if (!sequence.first) builder.append("every ")
		builder.append("every ")
		for (sequenceVariable : sequence.variables) {
			if (sequenceVariable.negation) builder.append('not ')
			compileFrom(sequenceVariable, builder)
			compileSequence(sequenceVariable, builder)
			builder.append(SEQUENCE_DELIMITER)
		}
		builder.delete(builder.lastIndexOf(SEQUENCE_DELIMITER), builder.length)
		builder.append("]")
		builder.toString
	}
	
	protected def static StringBuilder compileSequence(SequenceVariable sequenceVariable, StringBuilder builder) {
		if (sequenceVariable.time !== null) {
			builder.append(" and timer:interval(" )
			if (sequenceVariable.time.^var !== null) {
				builder.append( '${' + sequenceVariable.time.^var.name + '}' )
			} else {
				builder.append( sequenceVariable.time.value )
			}
			builder.append(" ")
			builder.append(sequenceVariable.time.timeUnit + ")"	)
		}
	}
	
	protected def static StringBuilder compileFrom(SequenceVariable sequenceVariable, StringBuilder builder) {
		if (sequenceVariable.event !== null) {
			builder.append(
				FromFieldCompilerHelper
				.compileEventContext(Arrays.asList(sequenceVariable.event), 0, true))
		}
	}
}