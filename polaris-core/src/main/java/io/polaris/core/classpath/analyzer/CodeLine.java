package io.polaris.core.classpath.analyzer;

import javax.annotation.concurrent.NotThreadSafe;

import io.polaris.core.collection.ObjectArrays;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.objectweb.asm.Label;


/**
 * @author Qt
 * @since Aug 12, 2025
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode
@NotThreadSafe
public class CodeLine {

	private Label label;
	private int opcode;
	private String opcodeStr;
	private Object[] args;


	public CodeLine() {
	}

	public CodeLine(int opcode, Object[] args) {
		this.opcode = opcode;
		this.opcodeStr = EntryAnalyzer.asOpcodeString(opcode);
		this.args = args;
	}

	public CodeLine(int opcode, String unknownStr, Object[] args) {
		this.opcode = opcode;
		this.opcodeStr = EntryAnalyzer.asOpcodeString(opcode, unknownStr);
		this.args = args;
	}

	public String dump() {
		StringBuilder sb = new StringBuilder();
		CodeLine line = this;
		if (line.getLabel() != null) {
			sb.append("<").append(line.getLabel()).append("> ");
		}
		sb.append(line.getOpcodeStr()).append(" ").append(ObjectArrays.toString(line.getArgs()));
		return sb.toString();
	}

}
