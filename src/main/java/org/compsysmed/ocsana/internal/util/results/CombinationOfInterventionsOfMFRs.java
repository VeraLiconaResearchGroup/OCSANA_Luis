package org.compsysmed.ocsana.internal.util.results;

//Java imports
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

//Cytoscape imports
import org.cytoscape.model.CyNode;

/**
* Class representing a combination of interventions in a signaling
* network
**/
public class CombinationOfInterventionsOfMFRs {
 private final Set<CyNode> ciNodes;
 private final Set<CyNode> targetNodes;
 private final Function<CyNode, String> nodeNameFunction;
 private final Function<CyNode, String> nodeIDFunction;


 private Collection<SignedIntervention> optimalSignings;

 /**
  * Constructor
  *
  * @param ciNodes  the nodes in this CI
  * @param targetNodes  the target nodes that this CI dominates
  * @param nodeNameFunction  function returning the name of a given
  * node (if null, use Cytoscape's automatic name, which is based
  * on SUID)
  * @param nodeIDFunction  function returning the biomolecule ID of
  * a given node (if null, use Cytoscape's automatic name, which is
  * based on SUID)
  * @param ocsanaScore the OCSANA score of this CI
  **/
 public CombinationOfInterventionsOfMFRs (Set<CyNode> ciNodes,
                                    Set<CyNode> targetNodes,
                                    Function<CyNode, String> nodeNameFunction,
                                    Function<CyNode, String> nodeIDFunction) {
 	Objects.requireNonNull(ciNodes, "CI nodes cannot be null");
     this.ciNodes = ciNodes;

     Objects.requireNonNull(targetNodes, "Target nodes collection cannot be null");
     this.targetNodes = targetNodes;

     if (nodeNameFunction == null) {
         this.nodeNameFunction = node -> node.toString();
     } else {
         this.nodeNameFunction = nodeNameFunction;
     }

     if (nodeIDFunction == null) {
         this.nodeIDFunction = node -> node.toString();
     } else {
         this.nodeIDFunction = nodeIDFunction;
     }


 }
 

 /**
  * Copy constructor
  *
  * @param other  the CombinationOfInterventions to copy
  **/
 public CombinationOfInterventionsOfMFRs (CombinationOfInterventionsOfMFRs other) {
     ciNodes = new HashSet<>(other.ciNodes);
     targetNodes = new HashSet<>(other.targetNodes);

     nodeNameFunction = other.nodeNameFunction;
     nodeIDFunction = other.nodeIDFunction;


     if (other.optimalSignings != null) {
         optimalSignings = new HashSet<>(other.optimalSignings);
     }
 }

 /**
  * Get the nodes in this CI
  **/
 public Set<CyNode> getNodes () {
     return ciNodes;
 }

 /**
  * Get the targets of this CI
  **/
 public Set<CyNode> getTargets () {
     return targetNodes;
 }

 /**
  * Get the size of this CI
  **/
 public Integer size () {
     return ciNodes.size();
 }



 /**
  * Get a string representation of the nodes of this CI.
  *
  * @see #nodeSetString
  **/
 public String interventionNodesString () {
     return nodeSetString(getNodes());
 }

 /**
  * Return the name of a node
  **/
 public String nodeName (CyNode node) {
     return nodeNameFunction.apply(node);
 }

 /**
  * Return the biomolecule ID of a node
  **/
 public String nodeID (CyNode node) {
     return nodeIDFunction.apply(node);
 }

 /**
  * Get a string representation of a collection of nodes
  *
  * The current format is "[node1 node2 node3]".
  *
  * @param nodes  the Collection of nodes
  **/
 public String nodeSetString(Collection<CyNode> nodes) {
     return nodes.stream().map(node -> nodeName(node)).collect(Collectors.joining(" ", "[", "]"));
 }

}
