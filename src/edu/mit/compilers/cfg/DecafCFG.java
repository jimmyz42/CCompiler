// Generated from /home/firescar/Documents/MIT/6.035/nchinda26035/src/edu/mit/compilers/grammar/Decaf.g4 by ANTLR 4.5.3

package edu.mit.compilers.cfg;

import java.util.Stack;
import java.util.Set;

/**
* This class walks through a high level DecafSemanticChecker graph a low level control
* flow graph which can be used to output assembly code
*/
public class DecafCFG {
    private Stack<Set<CFGAble>> visited = new Stack<>();
    public Set<CFGAble> currentVisited() {
        return visited.peek();
    }
    public Set<CFGAble> popVisited() {
        return visited.pop();
    }
    public void pushVisited(Set<CFGAble> set) {
        visited.push(set);
    }
}