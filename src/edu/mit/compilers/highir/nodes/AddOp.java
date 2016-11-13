package edu.mit.compilers.highir.nodes;

import org.antlr.v4.runtime.tree.TerminalNode;

import edu.mit.compilers.highir.DecafSemanticChecker;

public class AddOp extends BinOp {
    private String terminal;

    public AddOp(String terminal) {
        super(terminal);
    }

    public static AddOp create(DecafSemanticChecker checker, TerminalNode ctx) {
        String terminal = ctx.getText();
        return new AddOp(terminal);
    }
}