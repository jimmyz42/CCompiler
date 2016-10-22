package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.io.StringWriter;

import edu.mit.compilers.PrettyPrintable;
import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.cfg.components.TerminalBlock;

abstract public class Ir implements PrettyPrintable, CFGAble {
    @Override
    public String toString() {
        StringWriter sw = new StringWriter();
        prettyPrint(new PrintWriter(sw), "");
        return sw.toString();
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        pw.println(prefix + getClass().getSimpleName());
    }

    @Override
    public void concisePrint(PrintWriter pw, String prefix) {
        pw.println("");
    }

    @Override
    public CFG generateCFG() {
        return new TerminalBlock();
    }
}
