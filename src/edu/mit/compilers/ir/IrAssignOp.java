package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

class IrAssignOp extends Ir {
    private String terminal;

    public IrAssignOp(String terminal) {
        this.terminal = terminal;
    }

    public static IrAssignOp create(DecafSemanticChecker checker, DecafParser.Assign_opContext ctx) {
        return new IrAssignOp(ctx.getText());
    }
}