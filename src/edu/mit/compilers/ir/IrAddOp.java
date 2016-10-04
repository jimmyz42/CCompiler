package edu.mit.compilers.ir;

import org.antlr.v4.runtime.tree.TerminalNode;

class IrAddOp extends IrBinOp {
    private String terminal;

    public IrAddOp(String terminal) {
        super(terminal);
    }

    public static IrAddOp create(DecafSemanticChecker checker, TerminalNode ctx) {
        String terminal = ctx.getText();
        return new IrAddOp(terminal);
    }
}