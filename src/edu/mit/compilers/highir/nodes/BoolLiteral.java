package edu.mit.compilers.highir.nodes;

import java.util.List;

import edu.mit.compilers.cfg.Condition;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.ImmediateValue;
import edu.mit.compilers.lowir.Memory;
import edu.mit.compilers.lowir.Storage;
import edu.mit.compilers.lowir.instructions.Instruction;

public class BoolLiteral extends Literal {
    private boolean terminal;

    public BoolLiteral(boolean terminal) {
        this.terminal = terminal;
    }

    public static BoolLiteral create(DecafSemanticChecker checker, DecafParser.BoolLiteralContext ctx) {
         String text = ctx.BOOL_LITERAL().getText();
         Boolean terminal = Boolean.valueOf(text);
         return new BoolLiteral(terminal);
    }
    
    @Override
    public Type getExpressionType() {
        return ScalarType.BOOL;
    }
    
    @Override
    public String toString() {
        return Boolean.toString(terminal);
    }
    
    @Override
    public BasicBlock shortCircuit(CFG trueBranch, CFG falseBranch) {
    	return (terminal ? trueBranch : falseBranch).getEntryBlock();
    }

    @Override
    public void generateAssembly(AssemblyContext ctx){
        ctx.pushStack(this, ImmediateValue.create(terminal));
    }

    @Override
    public ImmediateValue getLocation(AssemblyContext ctx) {
        return ImmediateValue.create(terminal);
    }
}