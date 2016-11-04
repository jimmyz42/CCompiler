package edu.mit.compilers.highir.nodes;

import edu.mit.compilers.PrettyPrintable;

public interface Type extends PrettyPrintable{
    public long getSize();
    public long getLength();

    public String toString();
}
