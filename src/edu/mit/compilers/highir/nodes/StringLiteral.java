package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.BoolValue;
import edu.mit.compilers.lowir.ImmediateValue;
import edu.mit.compilers.lowir.Memory;
import edu.mit.compilers.lowir.Storage;
import edu.mit.compilers.lowir.StringValue;
import edu.mit.compilers.lowir.instructions.Cmove;
import edu.mit.compilers.lowir.instructions.Cmovg;
import edu.mit.compilers.lowir.instructions.Cmovge;
import edu.mit.compilers.lowir.instructions.Cmovl;
import edu.mit.compilers.lowir.instructions.Cmovle;
import edu.mit.compilers.lowir.instructions.Cmovne;
import edu.mit.compilers.lowir.instructions.Cmp;
import edu.mit.compilers.lowir.instructions.Instruction;
import edu.mit.compilers.lowir.instructions.Mov;
import edu.mit.compilers.lowir.instructions.StringInstruction;

public class StringLiteral extends ExternArg {
    private String terminal;
    private String label;

    public StringLiteral(String terminal) {
        this.terminal = terminal;
        this.label = terminal.split(" ")[0].replaceAll("[^a-zA-Z0-9]", "");
        this.label += Integer.toString(ThreadLocalRandom.current().nextInt(0, 500));
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
        pw.print(prefix + terminal);
    }

    public Storage allocateLocation(AssemblyContext ctx) {
    	return StringValue.create(terminal);
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        pw.println(prefix + getClass().getSimpleName() + ": " + terminal);
    }

    @Override
    public void generateAssembly(AssemblyContext ctx) {
        Instruction stringInstruction = new StringInstruction(label, terminal);

        ctx.addInstruction(stringInstruction);
    }

    @Override
    public Memory getLocation(AssemblyContext ctx) {
        return Memory.create("$"+label);
    }
}