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
  k = 3;
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

program: TK_class ID LCURLY RCURLY EOF;
/*protected PROGRAM: (EXTERN_DECL)* (FIELD_DECL)* (METHOD_DECL)*;
protected EXTERN_DECL: "extern" ID '('')' ';';
protected FIELD_DECL: TYPE (( (ID | ID '['INT_LITERAL']') ',')* (ID | ID '['INT_LITERAL']') )? ';';
protected METHOD_DECL: (TYPE | "void") ID '('  ((TYPE ID ',')* (TYPE ID))? ')' BLOCK;
protected BLOCK: '{' (FIELD_DECL)* (STATEMENT)* '}';*/
//protected TYPE: "int" | "bool";
/*protected STATEMENT: LOCATION ASSIGN_OP EXPR ';' | METHOD_CALL ';' | "if" '(' EXPR ')' BLOCK ("else" BLOCK)? |
"for" '('ID '=' EXPR';' EXPR';' ID COMPOUND_ASSIGN_OP EXPR ')' BLOCK | "while" '(' EXPR ')' BLOCK | "return" EXPR ';' |
"break;"| "continue;";*/
/*protected METHOD_CALL: (METHOD_NAME '(' EXPR) => METHOD_NAME '(' EXPR (',' EXPR)*  ')'
| (METHOD_NAME '(' EXTERN_ARG) => METHOD_NAME '(' EXTERN_ARG (',' EXTERN_ARG)* ')'
| METHOD_NAME "()";
protected METHOD_NAME: ID;
protected LOCATION: (ID '[') => ID '[' EXPR ']'
| ID;
protected EXPR: (ID) => LOCATION
| ("sizeof" '(' ID) => "sizeof" '(' ID ')'
| ("sizeof" '(' TYPE) => "sizeof" '(' TYPE ')'
//| EXPR BIN_OP EXPR
| (METHOD_NAME) => METHOD_CALL
| '-' EXPR
| '!' EXPR
| '(' EXPR ')'
| LITERAL;
protected EXTERN_ARG: EXPR | STRING_LITERAL;*/