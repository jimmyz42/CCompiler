package edu.mit.compilers.highir.nodes;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;

import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.cfg.components.BasicBlock;

abstract public class Statement extends Ir {
    public static Statement create(DecafSemanticChecker checker, DecafParser.StatementContext ctx) {
        return (Statement) checker.visit(ctx);
    }

    @Override
    public CFG generateCFG() {
        return BasicBlock.create(this);
    }
}