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
  k = 4;
}

tokens
{
  "bool";
  "break";
  "extern";
  "continue";
  "else";
  "for";
  "while";
  "if";
  "int";
  "return";
  "sizeof";
  "void";
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
    /*if(_returnToken != null) {
        System.out.print(_returnToken);
      _returnToken.setText(rname + " " + _returnToken.getText());
    }*/
  }
}

LCURLY options { paraphrase = "{"; } : "{";
RCURLY options { paraphrase = "}"; } : "}";
LPAREN: '(';
RPAREN: ')';
LBRACKET: '[';
RBRACKET: ']';
COMMA: ',';
SEMI: ';';
POUND: '#';
protected DASH: '-';
protected EXCLAMATION: '!';

// Note that here, the {} syntax allows you to literally command the lexer
// to skip mark this token as skipped, or to advance to the next line
// by directly adding Java commands.
WS_ : (' ' | '\n' {newline();} | '\t') {_ttype = Token.SKIP; };
protected SL_COMMENT : SLASH_COMMENT | POUND_COMMENT;
SLASH_COMMENT: '/''/' (~'\n')* '\n' {_ttype = Token.SKIP; newline (); };
POUND_COMMENT: POUND (~'\n')* '\n' {_ttype = Token.SKIP; newline (); };

OPERATOR: ('=' ~'=') => "=" {_ttype = ASSIGN_OP;}
| (COMPOUND_ASSIGN_OP) => COMPOUND_ASSIGN_OP {_ttype = COMPOUND_ASSIGN_OP;}
| BIN_OP {_ttype = BIN_OP;};
protected ASSIGN_OP: "=" | COMPOUND_ASSIGN_OP;
protected COMPOUND_ASSIGN_OP: "+=" | "-=";
protected BIN_OP: ARITH_OP | REL_OP | EQ_OP | COND_OP;
protected ARITH_OP: '+' | '-' | '*' | '/' | '%';
protected REL_OP: '<' | '>' | "<=" | ">=";
protected EQ_OP: "==" | "!=";
protected COND_OP: "&&" | "||";

LITERAL: INT_LITERAL ("ll")? {_ttype = INT_LITERAL;}
| CHAR_LITERAL {_ttype = CHAR_LITERAL;}
| (TRUE NOT_ALPHA_NUM) => BOOL_LITERAL {_ttype = BOOL_LITERAL;}
| (FALSE NOT_ALPHA_NUM) => BOOL_LITERAL {_ttype = BOOL_LITERAL;}
| ID {_ttype = ID;};

protected ID: ALPHA (ALPHA_NUM)*;
protected ALPHA_NUM: ALPHA | DIGIT;
protected ALPHA: 'a'..'z' | 'A'..'Z' | '_';
protected DIGIT: '0'..'9';
protected HEX_DIGIT: DIGIT | 'a'..'f' | 'A'..'F';
protected INT_LITERAL: DECIMAL_LITERAL | HEX_LITERAL;
protected DECIMAL_LITERAL: (DIGIT)+;
protected HEX_LITERAL: "0x" (HEX_DIGIT)+;
protected BOOL_LITERAL: TRUE | FALSE;
protected TRUE: "true";
protected FALSE: "false";
protected CHAR_LITERAL: '\'' (ESC_CHAR|NOT_SPECIAL_CHAR) '\'';
STRING_LITERAL: '"' (ESC_CHAR|NOT_SPECIAL_CHAR)* '"' {_ttype = STRING_LITERAL;};

protected ESC_CHAR:  '\\' ('"'|'\''|'\\'|'t'|'n');
protected NOT_SPECIAL_CHAR: (' '..'!' | '#'..'&' | '('..'[' | ']'..'~');
protected NOT_ALPHA_NUM: ~('a'..'z' | 'A'..'Z' | '_' | '0'..'9');