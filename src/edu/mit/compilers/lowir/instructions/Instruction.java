package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;
import java.io.StringWriter;

import edu.mit.compilers.PrettyPrintable;

abstract public class Instruction implements PrettyPrintable {
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
    
    abstract public void printAssembly(PrintWriter pw, String prefix);
}