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

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

public class MFR 
	extends AbstractMFRalgorithm {
    private static final String NAME = "Compute minimal functional routes from source nodes to target nodes t";
    private static final String SHORTNAME = "Single MFRs";
	public MFR(CyNetwork network) {
		super(network);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Collection<List<CyEdge>> MFRs(Set<CyNode> sources, Set<CyNode> targets) {

		 return computeAllMFRs(sources, targets);
	}
	
	
	
    public Collection<List<CyEdge>> computeAllMFRs (Set<CyNode> sources,
            Set<CyNode> targets)
            {
    		List<List<CyEdge>> completeMFRs = new ArrayList<>();
    		Deque<List<CyEdge>> incompleteMFRs = new LinkedList<>();
        		for (CyNode sourceNode: sources) {
        			for (CyEdge outEdge: network.getAdjacentEdgeIterable(sourceNode, CyEdge.Type.OUTGOING)) {
                // Handle cancellation
        				if (isCanceled()) {
        					return null;
        				}
        				assert incompleteMFRs.isEmpty();

        				return completeMFRs;
        				}
        			}
        		
            }
        		
    public Collection<List<CyEdge>> singlesourceMFR (CyNode source, CyNode target)
            {
			List<List<CyEdge>> MFRs = new ArrayList<>();
			return MFRs;
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
		// TODO Auto-generated method stub
		return null;
	}

}
