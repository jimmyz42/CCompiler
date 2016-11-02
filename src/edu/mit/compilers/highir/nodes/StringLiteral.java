package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.ImmediateValue;
import edu.mit.compilers.lowir.Memory;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.Storage;
import edu.mit.compilers.lowir.instructions.Instruction;
import edu.mit.compilers.lowir.instructions.Lea;
import edu.mit.compilers.lowir.instructions.StringInstruction;

public class StringLiteral extends ExternArg {
    private String terminal;
    private String label;

    public StringLiteral(String terminal) {
        this.terminal = terminal;
        this.label = terminal.split(" ")[0].replaceAll("[^a-zA-Z0-9]", "");
        this.label += Integer.toString(ThreadLocalRandom.current().nextInt(0, 100000));
        //TODO: BREAKING make the list of numbers deterministic
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
        pw.print(prefix + terminal);
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        pw.println(prefix + getClass().getSimpleName() + ": " + terminal);
    }

    @Override
    public void generateAssembly(AssemblyContext ctx) {
        Instruction stringInstruction = new StringInstruction(label, terminal);
        ctx.addInstruction(Lea.create(Memory.create("."+label+"(%rip)"), Register.create("%rdx")));
        ctx.storeStack(this, Register.create("%rdx"));
        ctx.addFooterInstruction(stringInstruction);
    }

	@Override
	public int getNumStackAllocations() {
		return 1;
	}
}