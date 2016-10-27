package edu.mit.compilers.cfg.components;

public class Edge {

	private BasicBlock from;
	private BasicBlock to; 
	
	public Edge(BasicBlock from, BasicBlock to){
		this.from = from;
		this.to = to;
	}
	
	public BasicBlock getFrom(){ 
		return this.from;
	}
	
	public BasicBlock getTo(){
		return this.to;
	}
}
