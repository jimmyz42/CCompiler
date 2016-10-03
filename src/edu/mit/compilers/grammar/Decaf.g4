
grammar Decaf;

import DecafScanner;

options
{
  language = Java;
}

@supression{
  @SuppressWarnings("unchecked")
}

program: (extern_decl)* (field_decl)* (method_decl)*;
catch [RecognitionException ex] {
   System.out.println("completely failed to parse your garbage");
   System.exit(1);
}
extern_decl: TK_extern ID LPAREN RPAREN SEMI;
method_decl: (type | TK_void) ID LPAREN (method_argument_decl (COMMA method_argument_decl)*)? RPAREN block;
method_argument_decl: type ID;
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
statement: TK_if LPAREN expr RPAREN block (TK_else block)? #IfStmt
         | TK_for LPAREN init_id=ID EQUALS init_expr=expr SEMI condition=expr SEMI update_id=ID update_op=COMPOUND_ASSIGN_OP update_expr=expr RPAREN block #ForStmt
         | TK_while LPAREN expr RPAREN block #WhileStmt
         | TK_return expr SEMI #ReturnStmt
         | TK_break SEMI #BreakStmt
         | TK_continue SEMI #ContinueStmt
         | method_call SEMI #MethodCallStmt
         | location assign_op expr SEMI #AssignStmt
;
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
location: ID LBRACKET expr RBRACKET #ArrayLocation
| ID #IdLocation
;

expr: location #LocationExpr
    | TK_sizeof LPAREN ID RPAREN #SizeofIdExpr
    | TK_sizeof LPAREN type RPAREN #SizeofTypeExpr
    | method_call #CallExpr
    | literal #LiteralExpr
    | LPAREN expr RPAREN #GroupExpr
    | DASH expr #NegExpr
    | EXCLAMATION expr #NotExpr
    | expr MUL_OP expr #MulOpExpr
    | expr (PLUS_OP | DASH) expr #PlusOpExpr
    | expr REL_OP expr #RelExpr
    | expr EQ_OP expr #EqExpr
    | expr AND_OP expr #AndExpr
    | expr OR_OP expr #OrExpr
    ;
literal: INT_LITERAL #IntLiteral
| CHAR_LITERAL #CharLiteral
| BOOL_LITERAL #BoolLiteral
;

extern_arg: expr | STRING_LITERAL;
catch [RecognitionException ex] {
   System.out.println(ex.toString());
   System.out.println("failed to parse function argument");
   System.exit(1);
}
assign_op: EQUALS | COMPOUND_ASSIGN_OP;