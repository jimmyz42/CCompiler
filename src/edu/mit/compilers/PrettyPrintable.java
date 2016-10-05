package edu.mit.compilers;

import java.io.PrintWriter;

public interface PrettyPrintable {
    public abstract void prettyPrint(PrintWriter pw, String prefix);
}
