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
            				
            				
            				//Check this... line 480 in c++ code?
            				mfrs.remove(pointer);
            				
            				
            				tags.remove(pointer);
            				
            				
            				
            				for (int i = 0; i < m; i++) {
            					ArrayList<SimpleEntry<Integer,ArrayList<Integer>>>temp1=cmfr;	
            					temp1.get(ctag).getValue().clear();
            					temp1.get(ctag).getValue().add(cpred.get(i));
            					mfrs.add(pointer+i, temp1);
            					tags.add(pointer+i,ctag);
            					
            				}
            				mfr_count=mfr_count+m-1;
            				
            			}
            			cpred=cmfr.get(ctag).getValue();
            			boolean all_done=true;
            			boolean chas;
            			for (int i = 0; i < cpred.size(); i++) {
            			int v=cpred.get(i);
            			chas=false;
            			for (int j = 0; j < cmfr.size(); j++) {
            					if(cmfr.get(j).getKey()==v) {
            						chas=true;
            					}
            			}
            			if(chas) {
            				continue;
            			}
            			all_done=false;
            			SimpleEntry<Integer,ArrayList<Integer>> temp2 =invadj_list.get(v);
            			cmfr.add(temp2);
            			
            			if(all_done) {
            				if(ctag==cmfr.size()-1) {
            					flag=true;
            					
            				}
            				else {
            					ctag++;
            				}
            				
            			}
            			else {ctag++;}
            			
            			
            				
            			}
            		}
            		mfrs.set(pointer, cmfr);
            		pointer++;
            		
            	}
            
            	
            	
            	for (int i = mfrs.size() - 1; i >= 0; i--) {
            	    boolean has_source = false;
            	    for (int j = 0; j < mfrs.get(i).size(); j++) {
            	    	if(mfrs.get(i).get(j).getKey()==nodeList.indexOf(source))
            	    	{
            	    		has_source=true;
            	    		
            	    	}
            	    }
            	    if (!has_source) {
            	    	mfrs.remove(i);
            	    	mfr_count--;
            	    	
            	    	
            	    }
            	}
            
            	ArrayList<ArrayList<Integer>> mfrs_egos=new ArrayList<ArrayList<Integer>>();
            	for (int i = 0; i < mfrs.size(); i++) {
            		ArrayList<Integer> mfr_egos=new ArrayList<Integer>();
            		for (int j = 0; j < mfrs.get(i).size(); j++) {
            			mfr_egos.add(mfrs.get(i).get(j).getKey());
            		
            		}
            		mfrs_egos.add(mfr_egos);
            	}
            	
            	ArrayList<CyEdge> mfrs_links=new ArrayList<CyEdge>();
            	for (int i = 0; i < mfrs.size(); i++) {
            		mfr=mfrs.get(i);
            		
            		int link_count =0;
            		for (int j = 0; j < mfr.size(); j++) {
            		      link_count += mfr.get(j).getValue().size();
            		    }
            		int[][] mfr_links  = new int[link_count][2];
            		int link_row = 0;
            		for (int j = 0; j < mfr.size(); j++) {	
            			for (int k = 0; k < mfr.get(j).getValue().size(); k++) {
            				mfr_links[link_row][0] = mfr.get(j).getValue().get(k);
            		        mfr_links[link_row][1] = mfr.get(j).getKey();
            		        link_row++;
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