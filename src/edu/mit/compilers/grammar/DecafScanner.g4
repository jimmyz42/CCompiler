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
  @SuppressWarnings('unchecked')
}

/*tokens
{
BOOL,
BREAK,
EXTERN,
CONTINUE,
ELSE,
FOR,
WHILE,
IF,
INT,
RETURN,
SIZEOF,
VOID
}*/
BOOL: 'bool';
BREAK: 'break';
EXTERN: 'extern';
CONTINUE: 'continue';
ELSE: 'else';
FOR: 'for';
WHILE: 'while';
IF: 'if';
INT: 'int';
RETURN: 'return';
SIZEOF: 'sizeof';
VOID: 'void';

LCURLY: '{';
RCURLY : '}';
LPAREN: '(';
RPAREN: ')';
LBRACKET: '[';
RBRACKET: ']';
COMMA: ',';
SEMI: ';';
POUND: '#';
fragment DASH: '-';
fragment EXCLAMATION: '!';

// Note that here, the {} syntax allows you to literally command the lexer
// to skip mark this token as skipped, or to advance to the next line
// by directly adding Java commands.
WS_: (' ' | '\n' | '\t') -> skip;
fragment SL_COMMENT : SLASH_COMMENT | POUND_COMMENT;
SLASH_COMMENT: '/''/' (~'\n')* '\n' -> skip;
POUND_COMMENT: POUND (~'\n')* '\n' -> skip;

ASSIGN_OP: '=' | COMPOUND_ASSIGN_OP;
COMPOUND_ASSIGN_OP: '+=' | '-=';
BIN_OP: ARITH_OP | REL_OP | EQ_OP | COND_OP;
fragment ARITH_OP: '+' | '-' | '*' | '/' | '%';
fragment REL_OP: '<' | '>' | '<=' | '>=';
fragment EQ_OP: '==' | '!=';
fragment COND_OP: '&&' | '||';

fragment LITERAL: INT_LITERAL | CHAR_LITERAL | BOOL_LITERAL;
ID: ALPHA (ALPHA_NUM)*;

INT_LITERAL: (DECIMAL_LITERAL | HEX_LITERAL) ('ll')?;
fragment DECIMAL_LITERAL: (DIGIT)+;
fragment HEX_LITERAL: '0x' (HEX_DIGIT)+;
BOOL_LITERAL: TRUE | FALSE;
fragment TRUE: 'true';
fragment FALSE: 'false';
CHAR_LITERAL: '\'' (ESC_CHAR|NOT_SPECIAL_CHAR) '\'';
STRING_LITERAL: '"' (ESC_CHAR|NOT_SPECIAL_CHAR)* '"';

fragment ALPHA_NUM: ALPHA | DIGIT;
fragment HEX_DIGIT: DIGIT | 'a'..'f' | 'A'..'F';
fragment DIGIT: '0'..'9';
fragment ALPHA: 'a'..'z' | 'A'..'Z' | '_';

fragment ESC_CHAR:  '\\' ('"'|'\''|'\\'|'t'|'n');
fragment NOT_SPECIAL_CHAR: (' '..'!' | '#'..'&' | '('..'[' | ']'..'~');
fragment NOT_ALPHA_NUM: ~('a'..'z' | 'A'..'Z' | '_' | '0'..'9');