package edu.mit.compilers;

import java.io.*;
import java.util.BitSet;
import java.util.List;

import edu.mit.compilers.grammar.DecafScanner;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.grammar.DecafParser.ProgramContext;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.highir.nodes.Ir;
import edu.mit.compilers.highir.nodes.Program;
import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.instructions.Instruction;
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
            } else if (CLI.target == Action.CFG) {
                DecafScanner scanner = new DecafScanner(inputStream);
                CommonTokenStream tokens = new CommonTokenStream(scanner);
                DecafParser parser = new DecafParser(tokens);
                ProgramContext context = parser.program();
                DecafSemanticChecker loader = new DecafSemanticChecker();
                Program ir = (Program)loader.visit(context);
                CFGContext ctx = new CFGContext();
                CFG cfg = ir.generateCFG(ctx);
                
                //Comment out this if you don't want to use the optimization
                cfg.clearPrevBlocks();
                cfg.genPrevBlocks();
                cfg.mergeBasicBlocks();
                cfg.eliminateEmptyBlocks();
                
                StringWriter sw = new StringWriter();
                cfg.cfgPrint(new PrintWriter(sw), "");
                System.out.println(sw.toString());
                if(CLI.debug){
                    //pretty graphics!
                	cfg.exportDOT("foo.dot");
                }
            } else if (CLI.target == Action.ASSEMBLY) {
                DecafScanner scanner = new DecafScanner(inputStream);
                CommonTokenStream tokens = new CommonTokenStream(scanner);
                DecafParser parser = new DecafParser(tokens);
                ProgramContext context = parser.program();
                DecafSemanticChecker loader = new DecafSemanticChecker();
                Program ir = (Program)loader.visit(context);
                CFGContext ctx = new CFGContext();
                CFG cfg = ir.generateCFG(ctx);
                
                cfg.clearPrevBlocks();
                cfg.genPrevBlocks();
                cfg.mergeBasicBlocks();
                cfg.eliminateEmptyBlocks();
                
                AssemblyContext actx = new AssemblyContext();
                List<Instruction> instructions = cfg.generateAssembly(actx);
                StringWriter sw = new StringWriter();
                for(Instruction instruction: instructions) {
                    instruction.printAssembly(new PrintWriter(sw), "    ");
                }
                
                if (CLI.debug){
                	//if debug is on, print output to console
                	System.out.println(sw.toString());
                }
                
                //write to .s file
                String outputAssemblyPath = CLI.infile;
                int extension = outputAssemblyPath.lastIndexOf(".");
                outputAssemblyPath = outputAssemblyPath.substring(0,extension +1) + "s";
                
                //if -o outputfile given, write to that. Else, use standard output
                outputAssemblyPath= CLI.outfile == null ? outputAssemblyPath : CLI.outfile;
                
                File assemblyFile = new File(outputAssemblyPath);
                
                FileWriter assemblyWriter = new FileWriter(assemblyFile, false); // true to append, false to overwrite.
                assemblyWriter.write(sw.toString());
                assemblyWriter.close();
                System.out.println("Assembly file is at " + outputAssemblyPath);
            }
        } catch(Exception e) {
            // print the error:
            System.err.println(CLI.infile);
            e.printStackTrace();
        }
    }
}
