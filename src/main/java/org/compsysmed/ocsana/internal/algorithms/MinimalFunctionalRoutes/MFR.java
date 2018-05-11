package org.compsysmed.ocsana.internal.algorithms.MinimalFunctionalRoutes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import java.util.Iterator;

import org.cytoscape.model.CyTable;
import org.cytoscape.work.Tunable;
import org.cytoscape.model.CyRow;
public class MFR extends AbstractMFRalgorithm {
	private static final String NAME = "Algorithm 3 in DOI: 10.1111/itor.12007";
	private static final String SHORTNAME = "Algorithm 3";
    @Tunable(description = "Bound Minimal Functional Route size",
            gravity=260
            )
	public Boolean restrictMFRsize = true;


   @Tunable(description = "Maximum MFR size",
            tooltip = "Maximum number of edges to allow in an MFR",
            gravity = 261,
            dependsOn = "restrictMFRsize=true")
   public Integer maxMFRsize = 20;

	
	
	
	int curr_mfr_index;//pointer
	int mfr_count;

	// 
	
	public MFR(CyNetwork network) {
		super(network);
	}
	

	@Override
	public Collection<List<CyEdge>> MinimalFunctionalRoutes(Set<CyNode> sources, Set<CyNode> targets) {
		
		
		
		Set<Set<CyEdge>> SetOfMfrs =new HashSet();
		Collection<List<CyEdge>> ComputedMinimalFunctionalRoutes=new HashSet() ;
		Collection<List<CyEdge>> mfs = null;
		for (CyNode sourceNode : sources) {
			for (CyNode targetNode : targets) {
				
				mfs = computeMFRs(sourceNode, targetNode, sources);
				//Avoiding duplicates
				for (List<CyEdge>mfr:mfs) {
					if (SetOfMfrs.contains(new HashSet(mfr))){continue;}
					else {
					SetOfMfrs.add(new HashSet(mfr));
					ComputedMinimalFunctionalRoutes.add(mfr);}
					
				}
			}
		}

		
		
		
		
		
		
		
		return ComputedMinimalFunctionalRoutes;
	}

	public Collection<List<CyEdge>> computeMFRs (CyNode source,
            CyNode target,Set<CyNode> sources)
            {   List<List<CyEdge>> completeMFRs = new ArrayList<>();
            
            
    
        List<CyNode> nodeList = network.getNodeList();
        ArrayList<Integer> compositeNodes=new ArrayList<Integer>();
    		
    		final String IDcolumnName = network.getDefaultNodeTable().getPrimaryKey().getName();
    		Objects.requireNonNull(network.getDefaultNodeTable().getColumn("composite"), "There is no column with name composite. Make sure your column for naming composite nodes is titled composite.");
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
    					//If it is not a source node, add all incoming nodes. Otherwise, don't add anything.
    					if(!sources.contains(v)) {
    					for (CyNode u :network.getNeighborList(v, CyEdge.Type.INCOMING)) {
    						incoming.add(nodeList.indexOf(u));
    				}
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

            				mfrs.remove(pointer);
          				
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
            	for (ArrayList<SimpleEntry<Integer, ArrayList<Integer>>> temp_mfr:mfrs) {
            		completeMFRs.add(new ArrayList<>());
            		for (SimpleEntry<Integer, ArrayList<Integer>> ego:temp_mfr) {
            			
            			
            			int in_node=ego.getKey();
            			//if I encounter  a node without an incoming node that is not a source noe, this is not an MFR so do not add to the list.
            			if (ego.getValue().size()==0 && !sources.contains(nodeList.get(in_node))){
            				completeMFRs.remove(completeMFRs.size()-1);
            				break;
            				}
            			//if it has no incoming nodes but the node is a source node, we don't care
            			if (ego.getValue().size()==0){
            				continue;
            				}
            			
            			for(int target_node:ego.getValue()) {
            				completeMFRs.get(completeMFRs.size()-1).add(network.getConnectingEdgeList(nodeList.get(in_node),nodeList.get(target_node) ,CyEdge.Type.DIRECTED).get(0));
            				
            			}
            			
            			
            		}
            		
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
        StringBuilder result = new StringBuilder(fullName());

        result.append(" (");

//        if (dijkstra.restrictPathLength) {
//            result.append(String.format("max path length: %d", dijkstra.maxPathLength));
//        } else {
//            result.append("no max path length");
//        }


        result.append(")");
        return result.toString();
   }

}