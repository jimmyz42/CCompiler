package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

class IrIntLiteral extends IrLiteral {
    private int terminal;

    public IrIntLiteral(int terminal) {
        this.terminal = terminal;
    }

    public static IrIntLiteral create(DecafSemanticChecker checker, DecafParser.IntLiteralContext ctx) {
         String text = ctx.INT_LITERAL().getText();
         Integer terminal = Integer.decode(text.replace("ll", ""));
         return new IrIntLiteral(terminal);
    }
    
    @Override
    public Type getType() {
        return Type.INT;
    }
}