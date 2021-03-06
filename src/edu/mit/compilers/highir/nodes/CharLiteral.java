package edu.mit.compilers.highir.nodes;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.ImmediateValue;

public class CharLiteral extends Literal {
    private char terminal;

    public CharLiteral(char terminal) {
        this.terminal = terminal;
    }
    
    public char getValue() {
    	return terminal;
    }

    public static CharLiteral create(DecafSemanticChecker checker, DecafParser.CharLiteralContext ctx) {
        String text = ctx.CHAR_LITERAL().getText();
        Character terminal = Character.valueOf(text.charAt(0));
        return new CharLiteral(terminal);
    }

    @Override
    public Type getType() {
        // Chars are treated as ints in decaf
        return ScalarType.INT;
    }

    @Override
    public String toString() {
        return "'" + terminal + "' /*" + (int)terminal + "*/";
    }

    @Override
    public void generateAssembly(AssemblyContext ctx){
        ctx.storeStack(getStorageTuple(), ImmediateValue.create(terminal));
    }

	@Override
	public long getNumStackAllocations() {
		return 1;
	}

    @Override
    public ImmediateValue getLocation(AssemblyContext ctx) {
        return ImmediateValue.create(terminal);
    }

	@Override
	public int hashCode() {
		return ("charterminal" + terminal).hashCode();
	}
}