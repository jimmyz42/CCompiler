package edu.mit.compilers.cfg;

import java.util.Stack;
import edu.mit.compilers.cfg.components.CFG;


public class CFGContext {
	private Stack<CFG> loops;
	private Stack<CFG> methods;

	public CFGContext() {
		this.loops = new Stack<>();
		this.methods = new Stack<>();
	}
	
	public CFG currentLoopCFG() {
		if(loops.isEmpty()) return null;
		return loops.peek();
	}
	
	public void pushLoopCFG(CFG cfg) {
		loops.push(cfg);
	}
	
	public void popLoopCFG() {
		loops.pop();
	}
	
	public CFG currentMethodCFG() {
		if(methods.isEmpty()) return null;
		return methods.peek();
	}
	
	public void pushMethodCFG(CFG cfg) {
		methods.push(cfg);
	}
	
	public void popMethodCFG() {
		methods.pop();
	}
}
