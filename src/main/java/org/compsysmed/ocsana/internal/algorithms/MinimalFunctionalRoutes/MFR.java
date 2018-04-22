package org.compsysmed.ocsana.internal.algorithms.MinimalFunctionalRoutes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.TreeMap;

//import static org.junit.Assert.assertNotNull;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import java.util.HashMap;
import java.util.HashSet;

import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyRow;
public class MFR extends AbstractMFRalgorithm {
	private static final String NAME = "Compute minimal functional routes from source nodes to target nodes t";
	private static final String SHORTNAME = "Single MFRs";
	private int[] visitableNodes;
	private int[][] adjacencyMatrix = null; 
	private int[][] workMatrix = null;
	private int Nnodes;

	private int Nedges;
	
	//private int[] curr_pred;
	int curr_mfr_index;//pointer
	int mfr_count;
	boolean is_mfr_complete; // aka flag
	// 
	
	public MFR(CyNetwork network) {
		super(network);
		this.Nnodes = network.getNodeCount();
		this.Nedges = network.getEdgeCount();
		this.visitableNodes = new int[Nnodes];
	}
	
//	private void initializeEdgeIDMaps(List<CyEdge> edgeList) {
//		edgeSUIDToIndex = new HashMap<Long,Integer>(Nnodes);
//		edgeIndexToSUID = new HashMap<Integer,Long>(Nnodes);
////		compositeNodes = new int[Nnodes];
//		int curr_index = 0;
//		for(CyEdge  e : edgeList) {
//			edgeSUIDToIndex.put(e.getSUID(), curr_index);
//			edgeIndexToSUID.put(curr_index, e.getSUID());
//			curr_index++;
//		}
//		maps_initialized = true;
//	}
	
//	private void initializeNodeIDMaps(List<CyNode> nodeList) {
//		nodeSUIDToIndex = new HashMap<Long,Integer>(Nnodes);
//		nodeIndexToSUID = new HashMap<Integer,Long>(Nnodes);
////		compositeNodes = new int[Nnodes];
//		int curr_index = 0;
//		for(CyNode u : nodeList) {
//			nodeSUIDToIndex.put(u.getSUID(), curr_index);
//			nodeIndexToSUID.put(curr_index, u.getSUID());
//			curr_index++;
//		}
//		maps_initialized = true;
//	}
	
//	private void initializeAdjMatrix(CyNetwork network) {
//		adjacencyMatrix = new int[Nnodes][Nnodes];
//		workMatrix = new int[Nnodes][Nnodes];
//		List<CyNode> nodeList = network.getNodeList();
//		int indexU, indexV;
//		for (CyNode v : nodeList ) {
//			indexV = nodeSUIDToIndex.get(v.getSUID());
//			for (CyNode u :network.getNeighborList(v, CyEdge.Type.INCOMING)) {
//				indexU = nodeSUIDToIndex.get(u.getSUID());
//				adjacencyMatrix[indexV][indexU] = 1;
//			}
//		}
//	}
//	
//private void initialize_adj_list(int[][] adjacency_matrix) {
//	ArrayList<SimpleEntry<Integer,ArrayList<Integer>>> invadj_list=new ArrayList<SimpleEntry<Integer,ArrayList<Integer>>>();
//	
//	for (int i=0; i<Nnodes;i++) {
//		
//
//		ArrayList<Integer> incoming = new ArrayList<Integer>();
//		for (int j=0;j<Nnodes;i++) {
//			if (adjacency_matrix[i][j]==1) {
//				incoming.add(j);
//			}
//		}
//		invadj_list.add(new AbstractMap.SimpleEntry(i,incoming));
//	}
//	
//	
//	
//	
//	
//}
//	private void generate_link_array(CyNetwork network )
//	{
//		Integer[][] link_array =new Integer[Nedges][2];
//	for(Integer i=0; i<Nedges;i++) {
//		Integer source;
//		Integer target; 
//		source = nodeSUIDToIndex.get(network.getEdge(edgeIndexToSUID.get(i)).getSource().getSUID());
//		target = nodeSUIDToIndex.get(network.getEdge(edgeIndexToSUID.get(i)).getTarget().getSUID());
//		link_array[i][0]=source;
//		link_array[i][1]=target;
//	}
//	
//	}
//	
//	private void initializeCompositeNodeList(CyNetwork network, HashMap<Long, Integer> nodeSUIDToIndex) {
//		final String IDcolumnName = network.getDefaultNodeTable().getPrimaryKey().getName();
//		
//		final Collection<CyRow> compositeRows = network.getDefaultNodeTable().getMatchingRows("composite", true);
//		for (final CyRow row : compositeRows) {
//			final Long nodeID = row.get(IDcolumnName, Long.class);
//		
//			if (nodeID == null)
//				continue;
//			final CyNode node = network.getNode(nodeID);
//			if (node==null)
//			continue;
//			compositeNodes.add(nodeSUIDToIndex.get(node.getSUID()));
//			
//			
//		
			
//			compositeNodes.add(nodeSUIDToIndex.get(nodeID));
//		}
//	}

//	public void MFRInit(CyNetwork network) {
//		List<CyNode> nodeList =network.getNodeList();
//		List<CyEdge> edgeList =network.getEdgeList();
//		if(!maps_initialized)
//			initializeNodeIDMaps(nodeList);
//			initializeEdgeIDMaps(edgeList);
//		if(null == adjacencyMatrix) 
//			initializeAdjMatrix(network);
//		if(null == compositeNodes)
//		initializeCompositeNodeList(network,nodeSUIDToIndex);
////		generate_link_array(network );
////		initialize_adj_list(adjacencyMatrix);
////		
//			
//	}
	
	
//	CyTable nodeTable=network.getDefaultNodeTable();

	@Override
	public Collection<List<CyEdge>> MinimalFunctionalRoutes(Set<CyNode> sources, Set<CyNode> targets) {
		Collection<List<CyEdge>> mfs = null;
		for (CyNode sourceNode : sources) {
			for (CyNode targetNode : targets) {
				mfs = computeMFRs(sourceNode, targetNode);
			}
		}
		return mfs;
	}

	public Collection<List<CyEdge>> computeMFRs (CyNode source,
            CyNode target)
            {   List<List<CyEdge>> completeMFRs = new ArrayList<>();
            
            
        //Creating hashmap to back and forth between suid and index.    
        List<CyNode> nodeList = network.getNodeList();
        ArrayList<Integer> compositeNodes=new ArrayList<Integer>();
//    		nodeSUIDToIndex = new HashMap<Long,Integer>(Nnodes);
//    		nodeIndexToSUID = new HashMap<Integer,Long>(Nnodes);
////    		compositeNodes = new int[Nnodes];
//    		int curr_index = 0;
//    		for(CyNode u : nodeList) {
//    			nodeSUIDToIndex.put(u.getSUID(), curr_index);
//    			nodeIndexToSUID.put(curr_index, u.getSUID());
//    			curr_index++;
//    		}        
    		
    		
    		
    		
    		//Creating list of composite nodes
    		
    		final String IDcolumnName = network.getDefaultNodeTable().getPrimaryKey().getName();
    		
    		final Collection<CyRow> compositeRows = network.getDefaultNodeTable().getMatchingRows("composite", true);
    		for (final CyRow row : compositeRows) {
    			final Long nodeID = row.get(IDcolumnName, Long.class);
    		
    			if (nodeID == null)
    				continue;
    			final CyNode node = network.getNode(nodeID);
    			if (node==null)
    			continue;
    			int t =nodeList.indexOf(node);
    			compositeNodes.add(t);
    		}
    			
    			List<SimpleEntry<Integer,ArrayList<Integer>>> invadj_list=new ArrayList<SimpleEntry<Integer,ArrayList<Integer>>>();

    				
    				for (CyNode v : nodeList ) {
    					List<Integer> incoming = new ArrayList<Integer>();
    					int indexV = nodeList.indexOf(v);
    					for (CyNode u :network.getNeighborList(v, CyEdge.Type.INCOMING)) {
    						incoming.add(nodeList.indexOf(u));
    				}
    				invadj_list.add(new AbstractMap.SimpleEntry(nodeList.indexOf(v),incoming));
    			}
    		
    		
    		
    		
            
            
            
            
            int target_index =nodeList.indexOf(target);
			int source_index=nodeList.indexOf(source);
			int m;
			int cnode;
			List<Integer> cpred;
            //Pointer to the current partial MFR
            int pointer =0;
            
            //Number of MFRs
            
            
            int mfr_count =1;
            
            
            //Indicator that current partial MFR is complete
            boolean flag =false;
            
            List<ArrayList<SimpleEntry<Integer,ArrayList<Integer>>>> mfrs =new ArrayList<ArrayList<SimpleEntry<Integer,ArrayList<Integer>>>>();
            
            List<Integer> tags= new ArrayList<Integer>();
            
            SimpleEntry<Integer, ArrayList<Integer>> to_target = new SimpleEntry<Integer, ArrayList<Integer>>(target_index, new ArrayList<Integer>()) ;
            for (int i:invadj_list.get(target_index).getValue()) {
            	to_target.getValue().add(i);
            }
          
            ArrayList<SimpleEntry<Integer,ArrayList<Integer>>> mfr =new ArrayList<SimpleEntry<Integer,ArrayList<Integer>>>();
            mfr.add(to_target);
            mfrs.add(mfr);
            tags.add(0);
            while(pointer<mfr_count)
            {
            	flag =false;
            		//copying mfrs.get(pointer) to cmfr
//            	ArrayList<SimpleEntry<Integer,ArrayList<Integer>>>cmfr=new ArrayList<SimpleEntry<Integer,ArrayList<Integer>>>(mfrs.get(pointer));
            		ArrayList<SimpleEntry<Integer,ArrayList<Integer>>>cmfr=new ArrayList<>();
            	for (SimpleEntry<Integer, ArrayList<Integer>> entry:mfrs.get(pointer)) {
            		int x=entry.getKey();
            		cmfr.add(new SimpleEntry<Integer, ArrayList<Integer>>(x, new ArrayList<Integer>()) );
            		int last_index =cmfr.size()-1;
            			for (int i:entry.getValue()) {
                    cmfr.get(last_index).getValue().add(i);
            		
            	}
            	}
                    
            	
            	int ctag=tags.get(pointer);
            	
            	while(!flag) {
            		int p=cmfr.get(ctag).getKey();
            		cnode=p;
            		//Make copy of cmfr.get(ctag).getValue())
//            		cpred = new ArrayList<Integer>(cmfr.get(ctag).getValue()) ;
            		cpred = new ArrayList<Integer>();
            		for (int i:cmfr.get(ctag).getValue()) {
            			cpred.add(i);
            		}
            		
            		
            		
            		
            		if (cpred.size()==0) { 
            			if (ctag==cmfr.size()-1) {
            				flag=true;
            				
            			}
            			else {
            				ctag++;
            			}
            			
            		}
            		else {
            			
            			// split current MFR into many with one in-link each
            			if (!compositeNodes.contains(cnode)){
            				m=cpred.size();
            				cmfr.get(ctag).getValue().clear();
            				cmfr.get(ctag).getValue().add(cpred.get(0));
//            				ArrayList<Integer> temp = new ArrayList<Integer>();
//            				temp.add(cpred.get(0));
//            				//Reason for having temp is we can't do .add for simple map
//            				cmfr.add(ctag, new SimpleEntry<Integer, ArrayList<Integer>>(cmfr.get(ctag).getKey(), temp)); 
            				mfrs.remove(pointer);
//            				
//            				
            				tags.remove(pointer);
            				
            				
            				
            				for (int i = 0; i < m; i++) {
            					
            				//Making copy of cmfr
            			     ArrayList<SimpleEntry<Integer,ArrayList<Integer>>>temp1=new ArrayList<SimpleEntry<Integer,ArrayList<Integer>>>();
            	            	for (SimpleEntry<Integer, ArrayList<Integer>> entry:cmfr) {
            	            		int x=entry.getKey();
            	            		temp1.add(new SimpleEntry<Integer, ArrayList<Integer>>(x, new ArrayList<Integer>()) );
            	            		int last_index =temp1.size()-1;
            	            			for (int u:entry.getValue()) {
            	                    temp1.get(last_index).getValue().add(u);
            	            		
            	            	}
            	            	}
            	            	
            					
            					
            					
            					
            					
            					
            					
            					temp1.get(ctag).getValue().clear();
            					temp1.get(ctag).getValue().add(cpred.get(i));
            	
            					
            					mfrs.add(pointer+i, new ArrayList<SimpleEntry<Integer,ArrayList<Integer>>>(temp1));
            					//mfrs.add(pointer+i, temp1);
            					
            					
            					tags.add(pointer+i, ctag);
            					
            				}
            				mfr_count=mfr_count+m-1;
            				
            			}
            			cpred.clear();
            			for (int k:cmfr.get(ctag).getValue()) {
            				cpred.add(k);
            			}
            			
            			boolean all_done=true;
            			boolean chas;
            			for (int i = 0; i < cpred.size(); i++) {
            			int v=cpred.get(i);
            			chas=false;
            			for (int j = 0; j < cmfr.size(); j++) {
            					if(cmfr.get(j).getKey().equals(v)) {
            						chas=true;
            					}
            			}
            			if(chas) {
            				continue;
            			}
            			all_done=false;
            			
//            			SimpleEntry<Integer,ArrayList<Integer>> temp2 =new SimpleEntry<Integer,ArrayList<Integer>>(invadj_list.get(v));
            			int t=invadj_list.get(v).getKey();
            			cmfr.add(new SimpleEntry<Integer, ArrayList<Integer>>(t, new ArrayList<Integer>()));
            			int size=cmfr.size();
            			for (int temp_int:invadj_list.get(v).getValue()) {
            				cmfr.get(size-1).getValue().add(temp_int);
            			}
            			}
            			
            			if(all_done) {
            				if(ctag==cmfr.size()-1) {
            					flag=true;
            					
            				}
            				else {
            					ctag++;
            				}
            				
            			}
            			else {ctag++;
            			
            			
            			}
            			
            			
            				
            			}
            		}
            		mfrs.set(pointer, cmfr);
            		pointer++;
            		
            	}
            
            	
            	
            	for (int i = mfrs.size() - 1; i >= 0; i--) {
            	    boolean has_source = false;
            	    for (int j = 0; j < mfrs.get(i).size(); j++) {
            	    	if(mfrs.get(i).get(j).getKey().equals(source_index))
            	    	{
            	    		has_source=true;
            	    		
            	    	}
            	    }
            	    if (!has_source) {
            	    	mfrs.remove(i);
            	    	mfr_count--;
            	    	
            	    	
            	    }
            	}
            	
            	
            	
            	//NOTICE THAT THIS ONLY RETURNS ONE EDGE CONNECTING TWO DISTINCT NODES. I.E. THIS MIGHT FAIL FOR MULTIGRAPHS!!
            int temp_count=0;
            	for (ArrayList<SimpleEntry<Integer, ArrayList<Integer>>> temp_mfr:mfrs) {
            		completeMFRs.add(new ArrayList<>());
            		for (SimpleEntry<Integer, ArrayList<Integer>> ego:temp_mfr) {
            			//make sure there is a list of target nodes
            			if (ego.getValue().size()==0){continue;}
            			int in_node=ego.getKey();
            			for(int target_node:ego.getValue()) {
            				completeMFRs.get(temp_count).add(network.getConnectingEdgeList(nodeList.get(in_node),nodeList.get(target_node) ,CyEdge.Type.DIRECTED).get(0));
            				
            			}
            			
            			
            		}
            		temp_count++;
            	}
            

            	return completeMFRs;
            }
            
            
    		
            

	
			
	@Override
	public String fullName() {
		return NAME;
	}

	@Override
	public String shortName() {
		return SHORTNAME;
	}

	@Override
	public String description() {
		// TODO Auto-generated meth
		return null;
	}

}