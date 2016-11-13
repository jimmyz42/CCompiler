package edu.mit.compilers.highir.nodes;

import org.antlr.v4.runtime.tree.TerminalNode;

import edu.mit.compilers.highir.DecafSemanticChecker;

public class RelOp extends BinOp {

    public RelOp(String terminal) {
        super(terminal);
    }

    public static RelOp create(DecafSemanticChecker checker, TerminalNode ctx) {
        String terminal = ctx.getText();
        return new RelOp(terminal);
    }
}