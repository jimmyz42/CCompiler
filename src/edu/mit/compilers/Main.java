package edu.mit.compilers;

import java.io.*;
import java.util.BitSet;
import java.util.List;

import edu.mit.compilers.grammar.DecafScanner;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.grammar.DecafParser.ProgramContext;
import edu.mit.compilers.ir.*;
import edu.mit.compilers.tools.CLI;
import edu.mit.compilers.tools.CLI.Action;
import exceptions.SemanticError;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.dfa.*;
import org.antlr.v4.runtime.atn.*;

class Main {
    public static void main(String[] args) {
        try {
            CLI.parse(args, new String[0]);
            ANTLRInputStream inputStream = CLI.infile == null ?
                    new ANTLRInputStream(System.in) : new ANTLRInputStream(new java.io.FileInputStream(CLI.infile));
            PrintStream outputStream = CLI.outfile == null ? System.out : new java.io.PrintStream(new java.io.FileOutputStream(CLI.outfile));
            if (CLI.target == Action.SCAN) {
                DecafScanner scanner = new DecafScanner(inputStream);
                Token token;
                boolean done = false;
                while (!done) {
                    for (token = scanner.nextToken();
                            token.getType() != DecafScanner.EOF;
                            token = scanner.nextToken()) {
                        String type = "";
                        String text = token.getText();
                        switch (token.getType()) {
                        // TODO: add strings for the other types here...
                        case DecafScanner.ID:
                            type = " IDENTIFIER";
                            break;
                        case DecafScanner.CHAR_LITERAL:
                            type = " CHARLITERAL";
                            break;
                        case DecafScanner.INT_LITERAL:
                            type = " INTLITERAL";
                            break;
                        case DecafScanner.STRING_LITERAL:
                            type = " STRINGLITERAL";
                            break;
                        case DecafScanner.BOOL_LITERAL:
                            type = " BOOLEANLITERAL";
                            break;
                        }
                        //outputStream.println(token.getType());
                        outputStream.println(token.getLine() + type + " " + text);
                    }
                    done = true;
                }
            } else if (CLI.target == Action.PARSE ||
                    CLI.target == Action.DEFAULT) {
                DecafScanner scanner =
                        new DecafScanner(inputStream);
                CommonTokenStream tokens = new CommonTokenStream(scanner);
                DecafParser parser = new DecafParser(tokens);
                parser.setTrace(CLI.debug);
                parser.addErrorListener(new ANTLRErrorListener() {
                    public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {}
                    public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, ATNConfigSet configs){}
                    public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs){}
                    public void syntaxError(Recognizer<?,?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                        System.exit(1);
                    }
                });
                ProgramContext context = parser.program();
                if (CLI.debug) {
                    org.antlr.v4.gui.Trees.inspect(context, parser);
                }
            } else if (CLI.target == Action.INTER) {
                DecafScanner scanner = new DecafScanner(inputStream);
                CommonTokenStream tokens = new CommonTokenStream(scanner);
                DecafParser parser = new DecafParser(tokens);
                ProgramContext context = parser.program();
                DecafSemanticChecker loader = new DecafSemanticChecker();
                Ir ir = (Ir)loader.visit(context);
                List<SemanticError> errors = loader.getSemanticErrors();
                if (errors.isEmpty()) {
                    if (CLI.debug) {
                        System.out.print(ir.toString());
                    }
                } else {
                    for (SemanticError e : errors) {
                        if (CLI.debug) {
                            e.printStackTrace();
                        } else {
                            System.err.println(e);
                        }
                    }
                    System.exit(1);
                }
            }
        } catch(Exception e) {
            // print the error:
            System.err.println(CLI.infile);
            e.printStackTrace();
        }
    }
}