package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.Memory;
import edu.mit.compilers.lowir.Register;
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
    public void prettyPrint(PrintWriter pw, String prefix) {
        pw.println(prefix + getClass().getSimpleName() + ": " + terminal);
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
        pw.print(prefix + terminal);
    }

    @Override
    public CFG generateCFG(CFGContext context) {
        return BasicBlock.createEmpty();
    }

    @Override
    public void generateAssembly(AssemblyContext ctx) {
        Instruction stringInstruction = new StringInstruction(label, terminal);
        ctx.addInstruction(Lea.create(Memory.create("."+label+"(%rip)"), Register.create("%rdx")));
        ctx.storeStack(getStorageTuple(), Register.create("%rdx"));
        ctx.addFooterInstruction(stringInstruction);
    }

	@Override
	public long getNumStackAllocations() {
		return 1;
	}

	@Override
	public Set<Descriptor> getConsumedDescriptors() {
		return Collections.emptySet();
	}

	@Override
	public Set<Descriptor> getGeneratedDescriptors() {
		return Collections.emptySet();
	}
}