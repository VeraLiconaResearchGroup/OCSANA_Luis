package org.compsysmed.ocsana.internal.tasks.mhsofmfrs;

import java.util.*;
import java.util.stream.Collectors;

// Cytoscape imports
import org.cytoscape.work.TaskMonitor;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyEdge;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.AbstractOCSANATask;
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;
import org.compsysmed.ocsana.internal.tasks.runner.RunnerTask;

import org.compsysmed.ocsana.internal.util.context.ContextBundle;
import org.compsysmed.ocsana.internal.util.results.ResultsBundle;
import org.compsysmed.ocsana.internal.util.results.CombinationOfInterventions;

public class MHSOFMFRAlgorithmTask extends AbstractOCSANATask {
    private static final OCSANAStep algStep = OCSANAStep.FIND_MHSES_OF_MFRS;

    private final RunnerTask runnerTask;
    private final ContextBundle contextBundle;
    private final ResultsBundle resultsBundle;

    public MHSOFMFRAlgorithmTask (RunnerTask runnerTask,
                             ContextBundle contextBundle,
                             ResultsBundle resultsBundle) {
        super(contextBundle.getNetwork());

        Objects.requireNonNull(runnerTask, "Runner task cannot be null");
        this.runnerTask = runnerTask;

        Objects.requireNonNull(contextBundle, "Context bundle cannot be null");
        this.contextBundle = contextBundle;

        Objects.requireNonNull(resultsBundle, "Context results cannot be null");
        this.resultsBundle = resultsBundle;
    }

    @Override
    public void run (TaskMonitor taskMonitor) {
        if (resultsBundle.pathFindingWasCanceled()) {
            return;
        }
        taskMonitor.setTitle("Minimal Hitting Sets of MFRs");
        if (contextBundle.getcomputeMFRs()) {
        taskMonitor.setTitle("Minimal Hitting Sets of MFRs");

        Objects.requireNonNull(resultsBundle.getMFRs(), "MFRs not set.");

        taskMonitor.setStatusMessage(String.format("Converting %d MFRs to node sets.", resultsBundle.getMFRs().size()));
        Long preConversionTime = System.nanoTime();
        List<Set<CyNode>> nodeSets = new ArrayList<>();
        Set<CyNode> sourceNodes = contextBundle.getSourceNodes();
        Set<CyNode> targetNodes = contextBundle.getTargetNodes();

        for (List<CyEdge> path: resultsBundle.getMFRs()) {
            Set<CyNode> nodes = new HashSet<>();
    		//Creating list of composite nodes
    		
    		final String IDcolumnName = network.getDefaultNodeTable().getPrimaryKey().getName();
    		List<CyNode> compositeNodes=new ArrayList<>();
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
            // Scan every edge in the path, adding its nodes as
            // appropriate
            for (int i = 0; i <= path.size() - 1; i++) {
                CyEdge edge = path.get(i);
                // Since we're using a Set, we don't have to worry
                // about multiple addition, so we'll just go ahead and
                // add the source and target every time
                if (contextBundle.getIncludeEndpointsInCIs() ||
                    (!sourceNodes.contains(edge.getSource()) && !targetNodes.contains(edge.getSource()))) {
                		if (contextBundle.getIncludecomposite()|!compositeNodes.contains(edge.getSource())) {
                			
                    nodes.add(edge.getSource());
                    }
                }

              
                if (
                    (!sourceNodes.contains(edge.getTarget()) && !targetNodes.contains(edge.getTarget()))) {
                	if (contextBundle.getIncludecomposite()||!compositeNodes.contains(edge.getTarget())) {    
                	nodes.add(edge.getTarget());
                	}
                }
            }

            if (!nodes.isEmpty()) {
                nodeSets.add(nodes);
            }
        }
        Long postConversionTime = System.nanoTime();

        Double conversionTime = (postConversionTime - preConversionTime) / 1E9;
        taskMonitor.setStatusMessage(String.format("Converted MFRs in %f s.", conversionTime));

        taskMonitor.setStatusMessage(String.format("Finding minimal combinations of interventions (algorithm: %s).", contextBundle.getMHSAlgorithm().shortName()));
        
        Long preMHSOFMFRSTime = System.nanoTime();
        Collection<Set<CyNode>> MHSOFMFRS = contextBundle.getMHSAlgorithm().MHSes(nodeSets);
        if (MHSOFMFRS != null) {
          
        	
          resultsBundle.setMHSOFMFRS(MHSOFMFRS.stream().map(mhsofmfrs -> new CombinationOfInterventions(mhsofmfrs, targetNodes, contextBundle.getNodeHandler()::getNodeName, contextBundle.getNodeHandler()::getNodeID, resultsBundle.getOCSANAScores().OCSANA(mhsofmfrs))).collect(Collectors.toList()));
          //THE LINE ABOVE IS GIVING AN EXCEPTION. I have already checked that MHSes is not null.
        }
        
        Long postMHSOFMFRSTime = System.nanoTime();

        Double mhsofmfrsTime = (postMHSOFMFRSTime - preMHSOFMFRSTime) / 1E9;

        taskMonitor.showMessage(TaskMonitor.Level.INFO, String.format("Found %d minimal CIs in %f s.", resultsBundle.getMHSOFMFRS().size(), mhsofmfrsTime));

        resultsBundle.setMHSOFMFRSExecutionSeconds(mhsofmfrsTime);
    }
        else {
        	taskMonitor.setStatusMessage("Minimal Functional Routes were not requested");}
    }
    
    
    
    
    
    

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getResults (Class<? extends T> type) {
        if (type.isAssignableFrom(OCSANAStep.class)) {
            return (T) algStep;
        } else {
            return (T) resultsBundle.getMHSOFMFRS();
        }
    }

    @Override
    public void cancel () {
        super.cancel();
        contextBundle.getMHSAlgorithm().cancel();
        resultsBundle.setMHSOFMFRSFindingWasCanceled();
        runnerTask.cancel();
    }
}
