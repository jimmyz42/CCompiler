package edu.mit.compilers;

import java.io.PrintWriter;

public interface PrettyPrintable {
    public abstract void println(PrintWriter pw, String prefix);
}
