package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
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
import edu.mit.compilers.optimizer.Optimizable;
import edu.mit.compilers.optimizer.OptimizerContext;

public class StringLiteral extends ExternArg {
    private String terminal;
    private String label;

    public StringLiteral(String terminal) {
        this.terminal = terminal;
        this.label = terminal.split(" ")[0].replaceAll("[^a-zA-Z0-9]", "");
        //this.label += Integer.toString(ThreadLocalRandom.current().nextInt(0, 100000));
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
        int stringNum = ctx.getStringNum();
        label = label + Integer.toString(stringNum);
        ctx.setStringNum(stringNum +1);
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

	@Override
	public boolean isLinearizable() {
		return true;
	}

	@Override
	public List<Optimizable> generateTemporaries(OptimizerContext context, boolean skipGeneration) {
		return Collections.emptyList();
	}

	@Override
	public void doCSE(OptimizerContext ctx) {
	}

    @Override
    public void doCopyPropagation(OptimizerContext ctx){
    }

    @Override
    public void doConstantPropagation(OptimizerContext ctx){
    }
    
	@Override
    public boolean canEliminate() {
    	return true;
    }

	@Override
	public int hashCode() {
		return ("stringterminal" + terminal).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return hashCode() == obj.hashCode();
	}
}