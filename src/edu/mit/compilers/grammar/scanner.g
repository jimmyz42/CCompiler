header {
  package edu.mit.compilers.grammar;
}

options
{
  mangleLiteralPrefix = "TK_";
  language = "Java";
}

{@SuppressWarnings("unchecked")}
class DecafScanner extends Lexer;
options
{
  k = 2;
}

tokens
{
  "class";
}

// Selectively turns on debug tracing mode.
// You can insert arbitrary Java code into your parser/lexer this way.
{
  /** Whether to display debug information. */
  private boolean trace = false;

  public void setTrace(boolean shouldTrace) {
    trace = shouldTrace;
  }
  @Override
  public void traceIn(String rname) throws CharStreamException {
    if (trace) {
      super.traceIn(rname);
    }
  }
  @Override
  public void traceOut(String rname) throws CharStreamException {
    if (trace) {
      super.traceOut(rname);
    }
  }
}

LCURLY options { paraphrase = "{"; } : "{";
RCURLY options { paraphrase = "}"; } : "}";

// Note that here, the {} syntax allows you to literally command the lexer
// to skip mark this token as skipped, or to advance to the next line
// by directly adding Java commands.
WS_ : (' ' | '\n' {newline();}) {_ttype = Token.SKIP; };
SL_COMMENT : "//" (~'\n')* '\n' {_ttype = Token.SKIP; newline (); };

DECIMAL_LITERAL: (DIGIT)+;
HEX_LITERAL: "Ox" (HEX_DIGIT)+;
BOOL_LITERAL: ("true" | "false");
CHAR_LITERAL: '\'' (ESC_CHAR|NOT_SPECIAL_CHAR) '\'';
exception catch [MismatchedCharException e] {System.out.println("Invalid character trying to match char");}
STRING_LITERAL: '"' (ESC_CHAR|NOT_SPECIAL_CHAR)* '"';

ID options { paraphrase = "an identifier"; } :
('a'..'z' | 'A'..'Z')+;

protected DIGIT: '0'..'9';
protected HEX_DIGIT: DIGIT | 'a'..'f';
protected ESC_CHAR:  '\\' ('"'|'\''|'\\'|'t'|'n');
protected NOT_SPECIAL_CHAR: ~('\"' | '\'' | '\\' | '\t');