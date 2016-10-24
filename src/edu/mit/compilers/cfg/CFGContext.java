package edu.mit.compilers.cfg;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.highir.descriptor.FunctionDescriptor;

public class CFGContext {
	private Stack<CFG> loops;
	private Map<FunctionDescriptor, CFG> methods;
	private CFG currentMethod;

	public CFGContext() {
		this.loops = new Stack<>();
		this.methods = new HashMap<>();
		currentMethod = null;
	}
	
	public CFG currentLoopCFG() {
		return loops.peek();
	}
	
	public void pushLoopCFG(CFG cfg) {
		loops.push(cfg);
	}
	
	public void popLoopCFG() {
		loops.pop();
	}
	
	public CFG currentMethodCFG() {
		return currentMethod;
	}
	
	public void addMethodCFG(FunctionDescriptor desc, CFG cfg) {
		currentMethod = cfg;
		methods.put(desc, cfg);
	}
	
	public CFG getMethodCFG(FunctionDescriptor desc) {
		return methods.get(desc);
	}
}
