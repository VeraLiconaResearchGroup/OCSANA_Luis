/**
 * The OCSANA node-scoring algorithm
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.algorithms.scoring;

// Java imports
import java.util.*;
import java.util.function.Function;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;

// Cytoscape imports
//import org.cytoscape.work.Tunable;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;


import org.cytoscape.model.CyRow;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.AbstractOCSANAAlgorithm;

import org.compsysmed.ocsana.internal.util.results.OCSANAScores;

/**
 * Implementation of the OCSANA scoring algorithm
 *
 * @param network  the network to compute on
 **/

public class OCSANAScoringAlgorithm
    extends AbstractOCSANAAlgorithm {
    private static final String NAME = "OCSANA scoring";
    private static final String SHORTNAME = "OCSANA";

    // Internal data
    private final CyNetwork network;
    private final Collection<OCSANAScoresListener> scoresListeners = new ArrayList<>();

    public OCSANAScoringAlgorithm (CyNetwork network) {
        this.network = network;
    }

    public void addListener (OCSANAScoresListener listener) {
        scoresListeners.add(listener);
    }

    public void removeListener (OCSANAScoresListener listener) {
        scoresListeners.remove(listener);
    }

    public void notifyListeners (OCSANAScores scores) {
        for (OCSANAScoresListener listener: scoresListeners) {
            listener.receiveScores(scores);
        }
    }
    
    

    /**
     * Apply the OCSANA scoring algorithm
     *
     * @param pathsToTargets  the collection of paths to targets
     * @param pathsToOffTargets  the collection of paths to off-targets
     * @param edgeIsInhibition  function returning true if a given edge is an inhibition
     * @return the scores bundle (or null if the algorithm is canceled)
     **/
    public OCSANAScores computeScores (Collection<List<CyEdge>> pathsToTargets,
                                       Collection<List<CyEdge>> pathsToOffTargets,
                                       Function<CyEdge, Boolean> edgeIsInhibition,Collection<List<CyEdge>> MinimalFunctionalRoutes,Collection<CyNode> targets, Collection<CyNode> offtargets) {
        Objects.requireNonNull(pathsToTargets, "Collection of paths to targets cannot be null");
        Objects.requireNonNull(pathsToOffTargets, "Collection of paths to off-targets cannot be null");

        Map<CyNode, Map<CyNode, Double>> effectsOnTargets = new HashMap<>();
        Map<CyNode, Set<CyNode>> targetsHit = new HashMap<>();
        Map<CyNode, Map<CyNode, Integer>> targetPathCountMap = new HashMap<>();
        scoreNodesInPaths(pathsToTargets, edgeIsInhibition, effectsOnTargets, targetsHit, targetPathCountMap);
        

        Map<CyNode, Map<CyNode, Double>> effectsOnOffTargets = new HashMap<>();
        Map<CyNode, Set<CyNode>> offTargetsHit = new HashMap<>();
        Map<CyNode, Map<CyNode, Integer>> offTargetPathCountMap = new HashMap<>();
        scoreNodesInPaths(pathsToOffTargets, (edge -> false), effectsOnOffTargets, offTargetsHit, offTargetPathCountMap);
        
        
        //This part is added for computing OCSANA scores for nodes in MFRs that do not appear in elementary paths
        if (MinimalFunctionalRoutes !=null) {
        	Collection<CyNode> in_paths= getNodes(pathsToTargets);
            List<CyNode> NodesInMFRsUnaccountedFor= new ArrayList<>(SetComplement(MinimalFunctionalRoutes,pathsToTargets));
    		final String IDcolumnName = network.getDefaultNodeTable().getPrimaryKey().getName();
    		List<CyNode> compositeNodes=new ArrayList<>();
    		Objects.requireNonNull(network.getDefaultNodeTable().getColumn("composite"), "There is no column with name composite");
    		final Collection<CyRow> compositeRows = network.getDefaultNodeTable().getMatchingRows("composite", true);
    		for (final CyRow row : compositeRows) {
    			final Long nodeID = row.get(IDcolumnName, Long.class);
    		
    			if (nodeID == null)
    				continue;
    			final CyNode node = network.getNode(nodeID);
    			if (node==null)
    			continue;
    			compositeNodes.add(node);
    		}
    		
    		TargetPathCountMapExtender(NodesInMFRsUnaccountedFor,targetPathCountMap, targets,MinimalFunctionalRoutes);
    		
            for (CyNode node:NodesInMFRsUnaccountedFor) {

    			
    			
            	Set<CyNode> composites=new HashSet<>(compositeNodes);
            		effectsOnTargets.put(node,new HashMap<>());
            		targetsHit.put(node,new HashSet<>());
            		
            		effectsOnOffTargets.put(node,new HashMap<>());
            		offTargetsHit.put(node,new HashSet<>());
            		offTargetPathCountMap.put(node,new HashMap<>());
            		Set<CyNode> outgoing = new HashSet<>(network.getNeighborList(node, CyEdge.Type.OUTGOING));
            		composites.retainAll(in_paths);
            		outgoing.retainAll(composites);
            		//effectsOnTargets will just get the maximum of composite nodes adjacent to the node
            		for(CyNode valid:outgoing) {
            			for (CyNode key:effectsOnTargets.get(valid).keySet()) {
            				if (effectsOnTargets.get(node).containsKey(key))
            				{
            					if (effectsOnTargets.get(node).get(key)<effectsOnTargets.get(valid).get(key))
            					{ double temp =effectsOnTargets.get(valid).get(key);
            						effectsOnTargets.get(node).put(key, temp);
	
            					}
            					
            				}
            				else {
            					double temp = effectsOnTargets.get(valid).get(key);
            					effectsOnTargets.get(node).put(key,temp);
            				}
            			}
            			for(CyNode hit: targetsHit.get(valid))
            			{
            				targetsHit.get(node).add(hit);
            				
            			}
            			


            			
            		}
            }
            }
        
        
        
        
        
        
        
        if (isCanceled()) {
            return null;
        } else {
            OCSANAScores result = new OCSANAScores(network, effectsOnTargets, targetsHit, targetPathCountMap, effectsOnOffTargets, offTargetsHit, offTargetPathCountMap);
            notifyListeners(result);

            return result;
        }
        
    
    }

    /**
     * Compute scores of nodes from the given paths
     *
     * @param paths  the paths to score
     * @param edgeIsInhibition  should return true if the given
     * edge is negative (i.e. inhibition)
     * @param scoreMap  Map to store scores for nodes (updated in-place)
     * @param targetsHit  Map to store endpoints hit for each node
     * in the paths (updated in-place)
     * @param pathCountMap  Map to store number of paths containing each node
     **/
    private void scoreNodesInPaths (Collection<List<CyEdge>> paths,
                                    Function<CyEdge, Boolean> edgeIsInhibition,
                                    Map<CyNode, Map<CyNode, Double>> scoreMap,
                                    Map<CyNode, Set<CyNode>> targetsHit,
                                    Map<CyNode, Map<CyNode, Integer>> pathCountMap) {
        Objects.requireNonNull(paths, "Paths cannot be null");
        Objects.requireNonNull(edgeIsInhibition, "Edge inhibition testing function cannot be null");
        Objects.requireNonNull(scoreMap, "Score map cannot be null");
        Objects.requireNonNull(targetsHit, "Targets hit map cannot be null");
        Objects.requireNonNull(pathCountMap, "Path count map cannot be null");
        
        // Iterate over the paths
        for (List<CyEdge> path: paths) {
            // Handle cancellation
            if (isCanceled()) {
                return;
            }

            // Handle empty and null paths
            Objects.requireNonNull(path, "Cannot score a null path");
            if (path.isEmpty()) {
                continue;
            }

            // Handle the endpoint
            CyNode pathTarget = path.get(path.size() - 1).getTarget();

            // Record the sign of the path
            // This will always be ±1
            Integer pathSign = 1;

            // For each path, we walk backwards from the endpoint,
            // considering the source of each edge
            for (int i = path.size() - 1; i >= 0; i--) {
                if (isCanceled()) {
                    return;
                }

                assert Math.abs(pathSign) == 1;

                CyEdge edge = path.get(i);
                if (!edge.isDirected()) {
                    throw new IllegalArgumentException("Undirected edges are not supported.");
                }

                CyNode edgeSource = edge.getSource();
                Integer subPathLength = path.size() - i;

                if (edgeIsInhibition.apply(edge)) {
                    pathSign *= -1;
                }

                // Update the node score
                scoreMap.putIfAbsent(edgeSource, new HashMap<>());
                Double score = scoreMap.get(edgeSource).getOrDefault(pathTarget, 0d);
                score += (pathSign * 1d / subPathLength.doubleValue());
                scoreMap.get(edgeSource).put(pathTarget, score);

                // Update the target hit record
                targetsHit.putIfAbsent(edgeSource, new HashSet<>());
                targetsHit.get(edgeSource).add(pathTarget);

                // Update the path count map
                pathCountMap.putIfAbsent(edgeSource, new HashMap<>());
                Integer count = pathCountMap.get(edgeSource).getOrDefault(pathTarget, 0);
                count += 1;
                pathCountMap.get(edgeSource).put(pathTarget, count);
            }
        }
        
    }

    private Collection<CyNode> getNodes(Collection<List<CyEdge>>collections){
    	Set<CyNode> nodes=new HashSet<>();
    	for (List<CyEdge> edges: collections) {
            // Handle cancellation

            // Handle empty and null paths
            Objects.requireNonNull(edges, "Cannot score a null path");
            if (edges.isEmpty()) {
                continue;
            }

            for (CyEdge edge:edges) {
                if (!edge.isDirected()) {
                    throw new IllegalArgumentException("Undirected edges are not supported.");
                }
            nodes.add(edge.getSource());
            nodes.add(edge.getTarget());
                
            
            }
        }
    	return nodes;
    	
    }
    	
        private Set<CyNode> getNodes(List<CyEdge> collection){
        	Set<CyNode> nodes=new HashSet<>();
        	for (CyEdge edge: collection) {
                Objects.requireNonNull(edge, "Cannot score a null path");
                    if (!edge.isDirected()) {
                        throw new IllegalArgumentException("Undirected edges are not supported.");
                    }
                nodes.add(edge.getSource());
                nodes.add(edge.getTarget());
                    
                
                }
        	return nodes;
            }
    	
    	
    	

    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	

	
    
    
    
    
    
    private Map<CyNode,Map<CyNode,Integer>> TargetPathCountMapExtender(List<CyNode> unaccountedNodes,Map<CyNode,Map<CyNode,Integer>> targetPathCount, Collection<CyNode> targets,Collection<List<CyEdge>>MinimalFunctionalRoutes){
		for (CyNode node : unaccountedNodes) {
			targetPathCount.put(node,new HashMap<>());
			for (CyNode target:targets) {
				targetPathCount.get(node).put(target, 0);
			}
			
			for (List<CyEdge>mfr:MinimalFunctionalRoutes) {
				Set<CyNode> setOfNodesInMfrs=getNodes(mfr);
				for (CyNode target:targets) {
					if (setOfNodesInMfrs.contains(target) && setOfNodesInMfrs.contains(target) ) {
						targetPathCount.get(node).put(target,targetPathCount.get(node).get(target)+1);
					}
					
				}
			}
			
		}
		
		
		
		return targetPathCount;
	}
    private Collection<CyNode> SetComplement(Collection<List<CyEdge>>set1,Collection<List<CyEdge>>set2){
    	Collection<CyNode>Set1=getNodes(set1);
    	Collection<CyNode>Set2=getNodes(set2);
    Set1.removeAll(Set2);
    return Set1;
    }
    
    // Name methods
    @Override
    public String fullName () {
        return NAME;
    }

    @Override
    public String shortName () {
        return SHORTNAME;
    }

    @Override
    public String description () {
        return fullName();
    }

    public static interface OCSANAScoresListener {
        public void receiveScores (OCSANAScores ocsanaScores);
    }
}
