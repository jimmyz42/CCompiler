package edu.mit.compilers.ir;

import org.antlr.v4.runtime.tree.TerminalNode;

class IrOrOp extends IrBinOp {
    private String terminal;

    public IrOrOp(String terminal) {
        super(terminal);
    }

    public static IrOrOp create(DecafSemanticChecker checker, TerminalNode ctx) {
        String terminal = ctx.getText();
        return new IrOrOp(terminal);
    }
}