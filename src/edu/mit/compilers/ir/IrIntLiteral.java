package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

class IrIntLiteral extends IrLiteral {
    private boolean longLong;
    private int terminal;

    public IrIntLiteral(int terminal, boolean longlong) {
        this.terminal = terminal;
        this.longLong = longlong;
    }

    public static IrIntLiteral create(DecafSemanticChecker checker, DecafParser.IntLiteralContext ctx) {
         String text = ctx.INT_LITERAL().getText();
         boolean longlong = text.endsWith("ll");
         Integer terminal = Integer.decode(text.replace("ll", ""));
         return new IrIntLiteral(terminal, longlong);
    }
}