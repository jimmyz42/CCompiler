package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

class IrIntLiteral extends IrLiteral {
    private int terminal;

    public IrIntLiteral(int terminal) {
        this.terminal = terminal;
    }

    public static IrIntLiteral create(DecafSemanticChecker checker, DecafParser.IntLiteralContext ctx) {
         String text = ctx.INT_LITERAL().getText();
         Integer terminal = parseInt(text);
         return new IrIntLiteral(terminal);
    }
    
    public static int parseInt(String text) {
        return Integer.decode(text.replace("ll", ""));
    }
    
    @Override
    public Type getExpressionType() {
        return TypeScalar.INT;
    }
    
    @Override
    public String toString() {
        return Integer.toString(terminal);
    }
}