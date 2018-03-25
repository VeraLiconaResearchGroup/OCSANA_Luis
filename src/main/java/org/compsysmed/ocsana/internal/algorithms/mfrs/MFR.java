package org.compsysmed.ocsana.internal.algorithms.mfrs;

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

	public MFR(CyNetwork network) {
		super(network);
		// TODO Auto-generated constructor stub

	}
	CyTable nodeTable=network.getDefaultNodeTable();

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
            {    			List<List<CyEdge>> completeMFRs = new ArrayList<>();
			Deque<List<CyEdge>> incompleteMFRs = new LinkedList<>();
			CyTable nodeTable=network.getDefaultNodeTable();
			
			List<List<CyEdge>> completePaths = new ArrayList<>();
			Collection<CyRow> matchingRows = nodeTable.getMatchingRows("composite", true);
			Set<CyNode> CompositeNodes = new HashSet<CyNode>();
			
			
			
			
			String primaryKeyColname = nodeTable.getPrimaryKey().getName();
			for (final CyRow row : matchingRows)
			{
				Long nodeId = row.get(primaryKeyColname, Long.class);
				if (nodeId == null) {
					
				
					continue;}
				final CyNode node = network.getNode(nodeId);
				if (node == null) {
					continue;
					
				}
				CompositeNodes.add(node);	
			}
			
			
			ArrayList<HashMap<Integer, ArrayList<Integer>>> Net = new ArrayList<HashMap<Integer, ArrayList<Integer>>>();
			int cnode;
			int m;
			
			
			
			List<CyNode> nodeList = network.getNodeList();
            
            
            ArrayList<SimpleEntry<Integer,ArrayList<Integer>>> invadj_list =new ArrayList<SimpleEntry<Integer,ArrayList<Integer>>>();
            for (CyNode v:nodeList) {
            	
            
            	ArrayList<Integer> incoming = new ArrayList<Integer>();
//            	
            	for(CyNode u : network.getNeighborList(v, CyEdge.Type.INCOMING) ) { 		
            		incoming.add(nodeList.indexOf(u));
            	}
            	invadj_list.add(new AbstractMap.SimpleEntry(nodeList.indexOf(v),incoming));
            }
            
            //Pointer to the current partial MFR
            int pointer =0;
            
            //Number of MFRs
            
            
            int mfr_count =1;
            
            
            //Indicator that current partial MFR is complete
            boolean flag =false;
            
            ArrayList<ArrayList<SimpleEntry<Integer,ArrayList<Integer>>>> mfrs =new ArrayList<ArrayList<SimpleEntry<Integer,ArrayList<Integer>>>>();
            
            ArrayList<Integer> tags= new ArrayList<Integer>();
            
            SimpleEntry<Integer,ArrayList<Integer>> to_target=invadj_list.get(nodeList.indexOf(target));
            
          
            ArrayList<SimpleEntry<Integer,ArrayList<Integer>>> mfr =new ArrayList<SimpleEntry<Integer,ArrayList<Integer>>>();
            mfr.add(to_target);
            mfrs.add(mfr);
            
            while(pointer<mfr_count)
            {
            	flag =false;
            	ArrayList<SimpleEntry<Integer,ArrayList<Integer>>>cmfr=mfrs.get(pointer);
            	int ctag=tags.get(pointer);
            	
            	while(!flag) {
            		cnode=cmfr.get(ctag).getKey();
            		ArrayList<Integer> cpred = cmfr.get(ctag).getValue();
            		if (cpred.size()==0) {
            			if (ctag==cmfr.size()-1) {
            				flag=true;
            				
            			}
            			else {
            				ctag+=1;
            			}
            			
            		}
            		else {
            			if (!CompositeNodes.contains(nodeList.get(cnode))){
            				m=cpred.size();
            				cmfr.get(ctag).getValue().clear();
            				cmfr.get(ctag).getValue().add(cpred.get(0));
            			}
            		}
            	}
            }
            
            
            
            
            
            
            return completePaths;
            
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
