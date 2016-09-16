lexer grammar DecafScanner;

@header {
  package edu.mit.compilers.grammar;
}

options
{
  language = Java;
  //k = 2;
}

@supression{
  @SuppressWarnings("unchecked")
}

tokens
{
  //"class";
}

// Selectively turns on debug tracing mode.
// You can insert arbitrary Java code into your parser/lexer this way.
@tracing{
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

LCURLY: '{';
RCURLY: '}';

ID: ('a'..'z' | 'A'..'Z')+;

// Note that here, the {} syntax allows you to literally command the lexer
// to skip mark this token as skipped, or to advance to the next line
// by directly adding Java commands.
WS_ : (' ' | '\n') -> skip;
SL_COMMENT : '//' (~'\n')* '\n' -> skip;

DECIMAL_LITERAL: (DIGIT)+;
HEX_LITERAL: 'Ox' (HEX_DIGIT)+;
BOOL_LITERAL: ('true' | 'false');
CHAR_LITERAL: '\'' (ESC_CHAR|NOT_SPECIAL_CHAR) '\'';
//catch [MismatchedCharException e] {System.out.println("Invalid character trying to match char");}
STRING_LITERAL: '"' (ESC_CHAR|NOT_SPECIAL_CHAR)* '"';

fragment DIGIT: '0'..'9';
fragment HEX_DIGIT: DIGIT | 'a'..'f';
fragment ESC_CHAR:  '\\' ('"'|'\''|'\\'|'t'|'n');
fragment NOT_SPECIAL_CHAR: ~('\"' | '\'' | '\\' | '\t');