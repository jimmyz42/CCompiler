package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.io.StringWriter;

import edu.mit.compilers.PrettyPrintable;
import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.components.BasicBlock;

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

    public BasicBlock generateCFG() {
        return new BasicBlock(null);
    }
}