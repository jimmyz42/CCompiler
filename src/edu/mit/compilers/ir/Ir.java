package edu.mit.compilers.ir;

import java.io.PrintWriter;
import java.io.StringWriter;

import edu.mit.compilers.PrettyPrintable;

public abstract class Ir implements PrettyPrintable {
    @Override
    public String toString() {
        StringWriter sw = new StringWriter();
        println(new PrintWriter(sw), "");
        return sw.toString();
    }
    
    @Override
    public void println(PrintWriter pw, String prefix) {
        pw.println(prefix + "// " + getClass().getSimpleName());
    }
}