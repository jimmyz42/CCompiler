package edu.mit.compilers.ir;

import org.antlr.v4.runtime.tree.TerminalNode;

class IrEqOp extends IrBinOp {
    private String terminal;

    public IrEqOp(String terminal) {
        super(terminal);
    }

    public static IrEqOp create(DecafSemanticChecker checker, TerminalNode ctx) {
        String terminal = ctx.getText();
        return new IrEqOp(terminal);
    }
}