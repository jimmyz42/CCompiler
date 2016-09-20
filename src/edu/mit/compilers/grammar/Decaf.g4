grammar Decaf;

import DecafScanner;

options
{
  language = Java;
}

@supression{
  @SuppressWarnings("unchecked")
}

program: (extern_decl)* (field_decl)* (method_decl)* {System.exit(0);};
catch [RecognitionException ex] {
   System.out.println("completely failed to parse your garbage");
   System.exit(1);
}
extern_decl: TK_extern ID LPAREN RPAREN SEMI;
method_decl: (type | TK_void) ID LPAREN type ID (COMMA type ID)* RPAREN block
| (type | TK_void) ID LPAREN RPAREN block;
catch [RecognitionException ex] {
   System.out.println("attempted and failed to parse a method");
   System.exit(1);
}

block: LCURLY (field_decl)* (statement)* RCURLY;

field_decl: type field (COMMA field)* SEMI;
catch [RecognitionException ex] {
   System.out.println(ex.toString());
   System.out.println("failed to parse the field");
   System.exit(1);
}

field: ID | (ID LBRACKET INT_LITERAL RBRACKET);
catch [RecognitionException ex] {
  System.out.println(ex.toString());
   System.out.println("missing array size");
   System.exit(1);
}

type: TK_int | TK_bool;
statement: TK_if LPAREN expr RPAREN block (TK_else block)?
| TK_for LPAREN ID EQUALS expr SEMI expr SEMI ID COMPOUND_ASSIGN_OP expr RPAREN block
| TK_while LPAREN expr RPAREN block
| TK_return expr SEMI
| TK_break SEMI
| TK_continue SEMI
| method_call SEMI
| location assign_op expr SEMI;
catch [RecognitionException ex] {
   System.out.println(ex.toString());
   System.out.println("failed to parse statement");
   System.exit(1);
}

method_call: method_name LPAREN expr (COMMA expr)* RPAREN
|  method_name LPAREN extern_arg (COMMA extern_arg)* RPAREN
| method_name LPAREN RPAREN;
catch [RecognitionException ex] {
   System.out.println(ex.toString());
   System.out.println("failed to parse");
   System.exit(1);
}

method_name: ID;
location: ID LBRACKET expr RBRACKET | ID;

expr: (expr_terminal BIN_OP expr) | (expr_terminal DASH expr) | expr_terminal;
expr_terminal: location
| TK_sizeof LPAREN ID RPAREN
| TK_sizeof LPAREN type RPAREN
| method_call
| DASH expr
| EXCLAMATION expr
| LPAREN expr RPAREN
| literal;
literal: INT_LITERAL | CHAR_LITERAL | BOOL_LITERAL;

extern_arg: expr | STRING_LITERAL;
catch [RecognitionException ex] {
   System.out.println(ex.toString());
   System.out.println("failed to parse function argument");
   System.exit(1);
}
assign_op: EQUALS | COMPOUND_ASSIGN_OP;