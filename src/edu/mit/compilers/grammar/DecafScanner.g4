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

TK_bool: 'bool';
TK_break: 'break';
TK_extern: 'extern';
TK_continue: 'continue';
TK_else: 'else';
TK_for: 'for';
TK_while: 'while';
TK_if: 'if';
TK_int: 'int';
TK_return: 'return';
TK_sizeof: 'sizeof';
TK_void: 'void';

LCURLY: '{';
RCURLY : '}';
LPAREN: '(';
RPAREN: ')';
LBRACKET: '[';
RBRACKET: ']';
COMMA: ',';
SEMI: ';';
POUND: '#';
DASH: '-';
EXCLAMATION: '!';
EQUALS: '=';

// Note that here, the {} syntax allows you to literally command the lexer
// to skip mark this token as skipped, or to advance to the next line
// by directly adding Java commands.
WS_: (' ' | '\r' ('\n')? | '\n' | '\t' | '') -> skip; //that looks like an empty string, but it's not!
fragment SL_COMMENT : SLASH_COMMENT | POUND_COMMENT;
SLASH_COMMENT: '/''/' (~'\n')* '\n' -> skip;
POUND_COMMENT: POUND (~'\n')* '\n' -> skip;

COMPOUND_ASSIGN_OP: '+=' | '-=';
BIN_OP: ARITH_OP | REL_OP | EQ_OP | COND_OP;
fragment ARITH_OP: '+' | DASH | '*' | '/' | '%';
fragment REL_OP: '<' | '>' | '<=' | '>=';
fragment EQ_OP: '==' | '!=';
fragment COND_OP: '&&' | '||';

BOOL_LITERAL: 'true' | 'false';
INT_LITERAL: (DECIMAL_LITERAL | HEX_LITERAL) ('ll')?;
fragment DECIMAL_LITERAL: (DIGIT)+;
fragment HEX_LITERAL: '0x' (HEX_DIGIT)+;
CHAR_LITERAL: '\'' (ESC_CHAR|NOT_SPECIAL_CHAR) '\'';
STRING_LITERAL: '"' (ESC_CHAR|NOT_SPECIAL_CHAR)* '"';
ID: ALPHA (ALPHA_NUM)*;

fragment ALPHA_NUM: ALPHA | DIGIT;
fragment HEX_DIGIT: DIGIT | 'a'..'f' | 'A'..'F';
fragment DIGIT: '0'..'9';
fragment ALPHA: 'a'..'z' | 'A'..'Z' | '_';

fragment ESC_CHAR:  '\\' ('"'|'\''|'\\'|'t'|'n');
fragment NOT_SPECIAL_CHAR: (' '..'!' | '#'..'&' | '('..'[' | ']'..'~');