package edu.mit.compilers.highir.nodes;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.Storage;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.BasicBlock;

abstract public class Statement extends Ir {
    public static Statement create(DecafSemanticChecker checker, DecafParser.StatementContext ctx) {
        return (Statement) checker.visit(ctx);
    }

    @Override
    public CFG generateCFG(CFGContext context) {
        return BasicBlock.create(this);
    }
    
    public Storage allocateLocation(AssemblyContext ctx) {
    	return ctx.allocateRegister(this);
    }
    
    public void deallocateLocation(AssemblyContext ctx) {
    	ctx.deallocateRegister(this);
    }
}