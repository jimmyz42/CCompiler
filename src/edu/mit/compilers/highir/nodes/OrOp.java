package edu.mit.compilers.highir.nodes;

import org.antlr.v4.runtime.tree.TerminalNode;

import edu.mit.compilers.highir.DecafSemanticChecker;

public class OrOp extends BinOp {
    private String terminal;

    public OrOp(String terminal) {
        super(terminal);
    }

    public static OrOp create(DecafSemanticChecker checker, TerminalNode ctx) {
        String terminal = ctx.getText();
        return new OrOp(terminal);
    }
}