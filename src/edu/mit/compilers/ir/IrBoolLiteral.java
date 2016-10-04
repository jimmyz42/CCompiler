package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

class IrBoolLiteral extends IrLiteral {
    private boolean terminal;

    public IrBoolLiteral(boolean terminal) {
        this.terminal = terminal;
    }

    public static IrBoolLiteral create(DecafSemanticChecker checker, DecafParser.BoolLiteralContext ctx) {
         String text = ctx.BOOL_LITERAL().getText();
         Boolean terminal = Boolean.valueOf(text);
         return new IrBoolLiteral(terminal);
    }
    
    @Override
    public Type getExpressionType() {
        return TypeScalar.BOOL;
    }
}