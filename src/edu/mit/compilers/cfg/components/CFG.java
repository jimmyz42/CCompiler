package edu.mit.compilers.cfg.components;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.HashSet;

import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.instructions.Instruction;

import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.StringEdgeNameProvider;
import org.jgrapht.ext.StringNameProvider;
import org.jgrapht.graph.DefaultEdge; 
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.ext.EdgeNameProvider;


public class CFG implements CFGAble {
    protected BasicBlock entryBlock;
    protected BasicBlock exitBlock;

    public CFG(BasicBlock entryBlock, BasicBlock exitBlock) {
        this.entryBlock = entryBlock;
        this.exitBlock = exitBlock;
    }

    // For purposes of creating a NOP start and a NOP end
    // to link to, note that entry and exit block are not linked
    // when creating the actual interior CFG link to the NOPs appropriately
    public CFG() {
    }

    public BasicBlock getEntryBlock() {
        return entryBlock;
    }

    public BasicBlock getExitBlock() {
        return exitBlock;
    }

    public BasicBlock getPreviousBlock() {
        return getPreviousBlocks().get(0);
    }

    public BasicBlock getNextBlock() {
        return getNextBlock(true);
    }

    public BasicBlock getNextBlock(boolean condition) {
        if(getNextBlocks().size() == 0)
        return null;
        if(condition) {
            return exitBlock.getNextBlocks().get(0);
        } else {
            return exitBlock.getNextBlocks().get(1);
        }
    }

    public List<BasicBlock> getPreviousBlocks() {
        return entryBlock.getPreviousBlocks();
    }

    public List<BasicBlock> getNextBlocks() {
        return exitBlock.getNextBlocks();
    }

    public void setPreviousBlocks(List<BasicBlock> prevBlocks) {
        entryBlock.setPreviousBlocks(prevBlocks);
    }

    public void setNextBlocks(List<BasicBlock> nextBlocks) {
        exitBlock.setNextBlocks(nextBlocks);
    }

    public void setPreviousBlock(BasicBlock prevBlock) {
        setPreviousBlocks(Arrays.asList(prevBlock));
    }

    public void setNextBlock(BasicBlock nextBlock) {
        setNextBlocks(Arrays.asList(nextBlock));
    }

    public void addPreviousBlocks(List<BasicBlock> prevBlocks) {
    	List<BasicBlock> blocks = new ArrayList<>();
    	blocks.addAll(getPreviousBlocks());
    	blocks.addAll(prevBlocks);
    	setPreviousBlocks(blocks);
    }

    public void addPreviousBlock(BasicBlock prevBlock) {
    	addPreviousBlocks(Collections.singletonList(prevBlock));
    }
    
    public void addNextBlocks(List<BasicBlock> nextBlocks) {
    	List<BasicBlock> blocks = new ArrayList<>();
    	blocks.addAll(getNextBlocks());
    	blocks.addAll(nextBlocks);
    	setNextBlocks(blocks);
    }
    
    public void addNextBlock(BasicBlock nextBlock) {
    	addNextBlocks(Collections.singletonList(nextBlock));
    }

    @Override
    public CFG generateCFG(CFGContext context) {
        return this;
    }

    private void giveAllBlocksIds(){
    	 HashSet<BasicBlock> visited = new HashSet<BasicBlock>();
         Stack<BasicBlock> blockStack = new Stack<>();
         blockStack.push(getEntryBlock());
         int blockNum = 0;
         
         //step 1: give every BasicBlock an ID
         while(!blockStack.empty()) {
             BasicBlock currentBlock = blockStack.pop();
             if(visited.contains(currentBlock)) continue;
             else visited.add(currentBlock);
             
             currentBlock.setID(blockNum);
             blockNum++;
             //push blocks in reverse order to pop in correct order
             if(currentBlock.getNextBlocks().size() > 1) {
                 blockStack.push(currentBlock.getNextBlock(false));
             }
             if(currentBlock.getNextBlocks().size() > 0){
                 blockStack.push(currentBlock.getNextBlock(true));
             }
         }
    }
    
    public void clearPrevBlocks(){
   	 	HashSet<BasicBlock> visited = new HashSet<BasicBlock>();
        Stack<BasicBlock> blockStack = new Stack<>();
        blockStack.push(getEntryBlock());
        
        while(!blockStack.empty()) {
            BasicBlock currentBlock = blockStack.pop();
            if(visited.contains(currentBlock)) continue;
            else visited.add(currentBlock);
            
            currentBlock.setPreviousBlocks(new ArrayList<BasicBlock>());
            //push blocks in reverse order to pop in correct order
            if(currentBlock.getNextBlocks().size() > 1) {
                blockStack.push(currentBlock.getNextBlock(false));
            }
            if(currentBlock.getNextBlocks().size() > 0){
                blockStack.push(currentBlock.getNextBlock(true));
            }
        }
   }
    
    public void genPrevBlocks(){
   	 	HashSet<BasicBlock> visited = new HashSet<BasicBlock>();
        Stack<BasicBlock> blockStack = new Stack<>();
        blockStack.push(getEntryBlock());
        
        while(!blockStack.empty()) {
            BasicBlock currentBlock = blockStack.pop();
            if(visited.contains(currentBlock)) continue;
            else visited.add(currentBlock);
            
            //push blocks in reverse order to pop in correct order
            if(currentBlock.getNextBlocks().size() > 1) {
            	currentBlock.getNextBlock(false).addPreviousBlock(currentBlock);
                blockStack.push(currentBlock.getNextBlock(false));
            }
            if(currentBlock.getNextBlocks().size() > 0){
            	currentBlock.getNextBlock(true).addPreviousBlock(currentBlock);
                blockStack.push(currentBlock.getNextBlock(true));
            }
        }
   }
    
    // Merge basic blocks optimization
    public void mergeBasicBlocks(){
    	HashSet<BasicBlock> visited = new HashSet<BasicBlock>();
        Stack<BasicBlock> blockStack = new Stack<>();
        blockStack.push(getEntryBlock());
    
        while(!blockStack.empty()) {
            BasicBlock currentBlock = blockStack.pop();
            if(visited.contains(currentBlock)) continue;
            else visited.add(currentBlock);
            
            
            if(currentBlock.getNextBlocks().size() > 0 && 
            		BasicBlock.canMerge(currentBlock, currentBlock.getNextBlock())) {
            	BasicBlock b1 = currentBlock, b2 = currentBlock.getNextBlock();
            	BasicBlock merged = BasicBlock.merge(b1, b2);
            	if(b1 == entryBlock) entryBlock = merged;
            	if(b2 == exitBlock) exitBlock = merged;

            	for(BasicBlock block: merged.getPreviousBlocks()) {
            		List<BasicBlock> list = block.getNextBlocks();
            		list.set(list.indexOf(b1), merged);
            	}
            	for(BasicBlock block: merged.getNextBlocks()) {
            		List<BasicBlock> list = block.getPreviousBlocks();
            		list.set(list.indexOf(b2), merged);
            	}
            	blockStack.push(merged);
            } else {
            	//push blocks in reverse order to pop in correct order
            	if(currentBlock.getNextBlocks().size() > 1) {
                    blockStack.push(currentBlock.getNextBlock(false));
                }
                if(currentBlock.getNextBlocks().size() > 0){
                    blockStack.push(currentBlock.getNextBlock(true));
                }
            }
        }
   }
    
    public void eliminateEmptyBlocks(){
    	HashSet<BasicBlock> visited = new HashSet<BasicBlock>();
        Stack<BasicBlock> blockStack = new Stack<>();
        blockStack.push(getEntryBlock());
    
        while(!blockStack.empty()) {
            BasicBlock currentBlock = blockStack.pop();
            if(visited.contains(currentBlock)) continue;
            else visited.add(currentBlock);
            
            if(currentBlock.isEmpty() && currentBlock.getNextBlocks().size() > 0) {
        		BasicBlock next = currentBlock.getNextBlock();          				
            	for(BasicBlock block: currentBlock.getPreviousBlocks()) {
            		List<BasicBlock> list = block.getNextBlocks();
            		list.set(list.indexOf(currentBlock), next);
            	}
            	next.setPreviousBlocks(currentBlock.getPreviousBlocks());
            	blockStack.push(next);
            } else {
            	//push blocks in reverse order to pop in correct order
            	if(currentBlock.getNextBlocks().size() > 1) {
                    blockStack.push(currentBlock.getNextBlock(false));
                }
                if(currentBlock.getNextBlocks().size() > 0){
                    blockStack.push(currentBlock.getNextBlock(true));
                }
            }
        }
   }
    
    public void exportDOT(String fileName){
        DOTExporter<BasicBlock, DefaultEdge> exporter =
            new DOTExporter<BasicBlock, DefaultEdge>(
                new org.jgrapht.ext.VertexNameProvider<BasicBlock>() {
                    @Override
                    public String getVertexName(BasicBlock bb) {
                        return Integer.toString(bb.getID());
                    }
                },
                new org.jgrapht.ext.VertexNameProvider<BasicBlock>() {
                    @Override
                    public String getVertexName(BasicBlock bb) {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        bb.cfgPrint(pw, "");
                        String s = sw.toString();
                        
                        s = s.replace("\\", "\\\\");
                        s = s.replace("\r\n", "\\l");
                        s = s.replace("\n", "\\l");
                        s = s.replace("\"", "\\\"");
                        s = s.replace("{", "\\{");
                        s = s.replace("}", "\\}");
                        s = s.replace("|", "\\|");
                        s = s.replace("|", "\\|");
                        s = s.replace("<", "\\<");
                        s = s.replace(">", "\\>");
                        s = s.replace("(", "\\(");
                        s = s.replace(")", "\\)");
                        s = s.replace(",", "\\,");
                        s = s.replace(";", "\\;");
                        s = s.replace(":", "\\:");
                        s = s.replace(" ", "\\ ");
                        
                        return Integer.toString(bb.getID()) + ": " + s;
                    }
                },
                null);

        
        DirectedPseudograph<BasicBlock, DefaultEdge> jgraphtGraph = createJGraphT();

        try {
			exporter.export(new FileWriter(fileName), jgraphtGraph);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public DirectedPseudograph<BasicBlock, DefaultEdge> createJGraphT() {
    	DirectedPseudograph<BasicBlock, DefaultEdge> g = new DirectedPseudograph<>(DefaultEdge.class);
    	return createJGraphT_Sub(null, getEntryBlock(), g);
    }

    public DirectedPseudograph<BasicBlock, DefaultEdge> createJGraphT_Sub(BasicBlock pred, BasicBlock bb, DirectedPseudograph<BasicBlock, DefaultEdge> g) {
    	boolean visited = !g.addVertex(bb);
    	if (!(pred == null))
    		g.addEdge(pred, bb);

    	if (visited)
    		return g;
    	
    	for (BasicBlock succ : bb.getNextBlocks())
    		g = createJGraphT_Sub(bb, succ, g);
    	
    	return g;
    }
    
    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
        HashSet<BasicBlock> visited = new HashSet<BasicBlock>();
        Stack<BasicBlock> blockStack = new Stack<>();
        blockStack.push(getEntryBlock());
        
        //step 1: give every BasicBlock an ID
        giveAllBlocksIds();
        
        //step 2: print stuff
        while(!blockStack.empty()) {
            BasicBlock currentBlock = blockStack.pop();
            if(visited.contains(currentBlock)) continue;
            else visited.add(currentBlock);
            
            pw.println(prefix + "BasicBlock " + currentBlock.getID() + ":");
            currentBlock.cfgPrint(pw, prefix + "    ");
            pw.println(prefix + currentBlock.getID() + " points to:");
            
            for(BasicBlock block : currentBlock.getNextBlocks()){
            	pw.println(prefix + "    " + block.getID());
            }
            
            //push blocks in reverse order to pop in correct order
            if(currentBlock.getNextBlocks().size() > 1) {
                blockStack.push(currentBlock.getNextBlock(false));
            }
            if(currentBlock.getNextBlocks().size() > 0){
                blockStack.push(currentBlock.getNextBlock(true));
            }
        }
    }

    @Override
    public List<Instruction> generateAssembly(AssemblyContext ctx) {
    	// TODO walk through CFG visit each basic block once,
    	// generate list of instructions
    	return null;
    }
    
    @Override
    public String toString() {
    	StringWriter sw = new StringWriter();
    	cfgPrint(new PrintWriter(sw), "");
    	return sw.toString();
    }
}
