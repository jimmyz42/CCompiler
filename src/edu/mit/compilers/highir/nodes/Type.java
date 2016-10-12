package edu.mit.compilers.highir.nodes;
import java.io.PrintWriter;
import java.io.StringWriter;
import edu.mit.compilers.PrettyPrintable;

public interface Type extends PrettyPrintable{
    long getSize();

    public String toString();
}
