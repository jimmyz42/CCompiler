package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

class IrCharLiteral extends IrLiteral {
    private char terminal;

    public IrCharLiteral(char terminal) {
        this.terminal = terminal;
    }

    public static IrCharLiteral create(DecafSemanticChecker checker, DecafParser.CharLiteralContext ctx) {
        String text = ctx.CHAR_LITERAL().getText();
        Character terminal = Character.valueOf(text.charAt(0));
        return new IrCharLiteral(terminal);
    }
    
    @Override
    public Type getType() {
        // Chars are treated as ints in decaf
        return Type.INT;
    }
}