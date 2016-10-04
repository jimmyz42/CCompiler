package edu.mit.compilers.ir;

import org.antlr.v4.runtime.tree.TerminalNode;

class IrRelOp extends IrBinOp {
    private String terminal;

    public IrRelOp(String terminal) {
        super(terminal);
    }

    public static IrRelOp create(DecafSemanticChecker checker, TerminalNode ctx) {
        String terminal = ctx.getText();
        return new IrRelOp(terminal);
    }
}