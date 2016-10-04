package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

class IrType extends Ir {
    private boolean boolTerminal;
    private boolean intTerminal;

    public IrType(boolean boolTerminal, boolean intTerminal) {
        this.boolTerminal = boolTerminal;
        this.intTerminal = intTerminal;
    }

    public static IrType create(DecafSemanticChecker checker, DecafParser.TypeContext ctx) {
        Boolean boolTerminal = ctx.TK_int().getText().isEmpty();
        Boolean intTerminal = ctx.TK_bool().getText().isEmpty();
        return new IrType(boolTerminal, intTerminal);
    }

}