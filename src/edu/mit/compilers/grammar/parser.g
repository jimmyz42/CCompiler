header {
package edu.mit.compilers.grammar;
}

options
{
  mangleLiteralPrefix = "TK_";
  language = "Java";
}

class DecafParser extends Parser;
options
{
  importVocab = DecafScanner;
  k = 4;
  buildAST = true;
}

// Java glue code that makes error reporting easier.
// You can insert arbitrary Java code into your parser/lexer this way.
{
  // Do our own reporting of errors so the parser can return a non-zero status
  // if any errors are detected.
  /** Reports if any errors were reported during parse. */
  private boolean error;

  @Override
  public void reportError (RecognitionException ex) {
    // Print the error via some kind of error reporting mechanism.
    error = true;
  }
  @Override
  public void reportError (String s) {
    // Print the error via some kind of error reporting mechanism.
    error = true;
  }
  public boolean getError () {
    return error;
  }

  // Selectively turns on debug mode.

  /** Whether to display debug information. */
  private boolean trace = false;

  public void setTrace(boolean shouldTrace) {
    trace = shouldTrace;
  }
  @Override
  public void traceIn(String rname) throws TokenStreamException {
    if (trace) {
      super.traceIn(rname);
    }
  }
  @Override
  public void traceOut(String rname) throws TokenStreamException {
    if (trace) {
      super.traceOut(rname);
    }
  }
}

program: (extern_decl)* (field_decl)* (method_decl)* {System.exit(0);};
exception catch [RecognitionException ex] {
   System.out.println(ex.toString());
   System.out.println("failed to parse");
}
protected extern_decl: TK_extern ID LPAREN RPAREN SEMI;
protected method_decl: ((type | TK_void) ID LPAREN type) =>
(type | TK_void) ID LPAREN type ID (COMMA type ID)* RPAREN block
| (type | TK_void) ID LPAREN RPAREN block;
exception catch [RecognitionException ex] {
   System.out.println(ex.toString());
   System.out.println("attempted and failed to parse a method");
   System.exit(1);
}
protected block: opening:LCURLY (field_decl)* (statement)* closing:RCURLY;
exception[closing] catch [RecognitionException ex] {
System.out.println(ex.toString());
   System.out.println("missing closing brace");
   System.exit(1);
}

protected field_decl: type field (COMMA field)* semi:SEMI;
exception catch [RecognitionException ex] {
   System.out.println(ex.toString());
   System.out.println("failed to parse the field");
   System.exit(1);
}
exception[semi] catch [RecognitionException ex] {
System.out.println(ex.toString());
   System.out.println("expected a semicolon");
   System.exit(1);
}

protected field: ID | (ID LBRACKET size:INT_LITERAL RBRACKET);
exception[size] catch [RecognitionException ex] {
  System.out.println(ex.toString());
   System.out.println("missing array size");
   System.exit(1);
}
exception catch [RecognitionException ex] {
   throw ex;
}

protected type: TK_int | TK_bool;
protected statement: (TK_if) => TK_if LPAREN expr RPAREN block ("else" block)?
| (TK_for) => TK_for LPAREN ID "=" expr SEMI expr SEMI ID COMPOUND_ASSIGN_OP expr RPAREN block
| (TK_while) => TK_while LPAREN expr RPAREN block
| (TK_return) => TK_return expr SEMI
| (TK_break) => TK_break SEMI
| (TK_continue) => TK_continue SEMI
| (method_name LPAREN) => method_call SEMI
| location ASSIGN_OP expr SEMI;
exception catch [RecognitionException ex] {
   System.out.println(ex.toString());
   System.out.println("failed to parse statement");
   System.exit(1);
}

protected method_call: (method_name LPAREN expr) => method_name LPAREN expr (COMMA expr)* RPAREN
| (method_name LPAREN EXTERN_ARG) => method_name LPAREN EXTERN_ARG (COMMA EXTERN_ARG)* RPAREN
| method_name LPRAREN RPAREN;
exception catch [RecognitionException ex] {
   System.out.println(ex.toString());
   System.out.println("failed to parse");
   System.exit(1);
}

protected method_name: ID;
protected location: (ID LBRACKET) => ID LBRACKET expr RBRACKET
| ID;

/*protected BIN_OP: ARITH_OP | REL_OP | EQ_OP | COND_OP;
protected ARITH_OP: '+' | '-' | '*' | '/' | '%';
protected REL_OP: '<' | '>' | "<=" | ">=";
protected EQ_OP: "==" | "!=";
protected COND_OP: "&&" | "||";*/

protected expr: (. ARITH_OP) => (expr_terminal ARITH_OP expr)
(. REL_OP) => (expr_terminal REL_OP expr)
(. EQ_OP) => (expr_terminal EQ_OP expr)
(. COND_OP) => (expr_terminal COND_OP expr)
| expr_terminal ;
protected expr_terminal: (ID) => location
| (TK_sizeof LPAREN ID) => TK_sizeof LPAREN ID RPAREN
| (TK_sizeof LPAREN type) => TK_sizeof LPAREN type RPAREN
| (method_name) => method_call
| DASH expr
| EXCLAMATION expr
| LPAREN expr RPAREN
| LITERAL;

protected extern_arg: expr | STRING_LITERAL;
exception catch [RecognitionException ex] {
   System.out.println(ex.toString());
   System.out.println("failed to parse function argument");
   System.exit(1);
}