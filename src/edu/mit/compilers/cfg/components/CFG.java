package edu.mit.compilers.cfg.components;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.HashSet;

import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.lowir.instructions.Instruction;

import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.StringEdgeNameProvider;
import org.jgrapht.ext.StringNameProvider;
import org.jgrapht.graph.DefaultEdge; 
import org.jgrapht.graph.SimpleDirectedGraph;
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
        setPreviousBlocks(Collections.singletonList(prevBlock));
    }

    public void setNextBlock(BasicBlock nextBlock) {
        setNextBlocks(Collections.singletonList(nextBlock));
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
                        
                        return s;
                    }
                },
                null);

        
        SimpleDirectedGraph<BasicBlock, DefaultEdge> jgraphtGraph = createJGraphT();

        try {
			exporter.export(new FileWriter(fileName), jgraphtGraph);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public SimpleDirectedGraph<BasicBlock, DefaultEdge> createJGraphT() {
    	SimpleDirectedGraph<BasicBlock, DefaultEdge> g = new SimpleDirectedGraph<>(DefaultEdge.class);
    	return createJGraphT_Sub(null, getEntryBlock(), g);
    }

    public SimpleDirectedGraph<BasicBlock, DefaultEdge> createJGraphT_Sub(BasicBlock pred, BasicBlock bb, SimpleDirectedGraph<BasicBlock, DefaultEdge> g) {
    	if (!g.addVertex(bb))
    		return g;
    	
    	if (!(pred == null))
    		g.addEdge(pred, bb);
    	
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
    public List<Instruction> generateAssembly() {
    	// TODO walk through CFG visit each basic block once,
    	// generate list of instructions
    	return null;
    }
}
