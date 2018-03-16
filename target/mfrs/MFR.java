package org.compsysmed.ocsana.internal.algorithms.mfrs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.*;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyRow;
public class MFR 
	extends AbstractMFRalgorithm {
    private static final String NAME = "Compute minimal functional routes from source nodes to target nodes t";
    private static final String SHORTNAME = "Single MFRs";
	public MFR(CyNetwork network) {
		super(network);
		// TODO Auto-generated constructor stub
		for (CyNode node: network.getNodeList()) {
			Net.put(node, network.getAdjacentEdgeList(node,  CyEdge.Type.DIRECTED));
			
		}
	}
	
	  
	LinkedHashMap<CyNode,List<CyEdge>> Net=new LinkedHashMap<CyNode,List<CyEdge>>();
	


	List<CyNode> indexed = new ArrayList<CyNode>(Net.keySet());

	@Override
	public Collection<List<CyEdge>> MFRs(Set<CyNode> sources, Set<CyNode> targets) {
		 return computeAllMFRs(sources, targets);
	}
	
	
	
    public Collection<List<CyEdge>> computeAllMFRs (Set<CyNode> sources,
            Set<CyNode> targets)
            {List<List<CyEdge>> MFRs = new ArrayList<>();
    		int node_count=network.getNodeCount();
    		if (isCanceled()) {
				return null;
			}

                return MFRs;
        		
            }
        		
    public Collection<List<CyEdge>> singlesourceMFR (CyNode source, CyNode target, LinkedHashMap Net )
            {
		List<List<CyEdge>> MFR= new ArrayList<>();
		int pointer = 0;
		int n_mfrs=1;
		Vector<Integer> tag = new Vector<>();
		tag.add(1);
		int ctag;
		
		MFR.add((List<CyEdge>) Net.get(indexed.get(pointer)));
		
		boolean flag = false;
		while (pointer < n_mfrs) {
			flag =false;
			ctag = tag.get(pointer);
			
		}
		while (!flag);
		{
			
			
			
			
			
			
		}
		}
    
    
//    private static Set<CyNode> getNodesWithValue(
//    		final CyNetwork net, final CyTable table,
//    		final String colname, final Object value)
//    		{
//    		final Collection<CyRow> matchingRows = table.getMatchingRows(colname, value);
//    		final Set<CyNode> nodes = new HashSet<CyNode>();
//    		final String primaryKeyColname = table.getPrimaryKey().getName();
//    		for (final CyRow row : matchingRows)
//    		{
//    			final Long nodeId = row.get(primaryKeyColname, Long.class);
//    			if (nodeId == null)
//    				continue;
//    			final CyNode node = net.getNode(nodeId);
//    			if (node == null)
//    				continue;
//    			nodes.add(node);
//    		}
//    		return nodes;
//    		}
//    	
//    	


    
    
    

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
        StringBuilder result = new StringBuilder(fullName());

        result.append(" (");




        result.append(")");
        return result.toString();
	}
	
}


