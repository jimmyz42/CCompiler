package edu.mit.compilers.highir.nodes;

import edu.mit.compilers.PrettyPrintable;

public interface Type extends PrettyPrintable{
    long getSize();

    public String toString();
}
