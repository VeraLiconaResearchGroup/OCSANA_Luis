/**
 * Task to run path-finding algorithm in OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.mfrs;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.work.TaskMonitor;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.AbstractOCSANATask;
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;
import org.compsysmed.ocsana.internal.tasks.runner.RunnerTask;

import org.compsysmed.ocsana.internal.util.context.ContextBundle;
import org.compsysmed.ocsana.internal.util.results.ResultsBundle;

public class MFRFindingAlgorithmTask extends AbstractOCSANATask {
    private final RunnerTask runnerTask;
    private final ContextBundle contextBundle;
    private final ResultsBundle resultsBundle;
    private final OCSANAStep algStep;
    private Collection<List<CyEdge>> MFRs;

    public MFRFindingAlgorithmTask (RunnerTask runnerTask,
                                     ContextBundle contextBundle,
                                     ResultsBundle resultsBundle,
                                     OCSANAStep algStep) {
        super(contextBundle.getNetwork());

        Objects.requireNonNull(runnerTask, "Runner task cannot be null");
        this.runnerTask = runnerTask;

        Objects.requireNonNull(contextBundle, "Context bundle cannot be null");
        this.contextBundle = contextBundle;

        Objects.requireNonNull(resultsBundle, "Context results cannot be null");
        this.resultsBundle = resultsBundle;

        Objects.requireNonNull(algStep, "Algorithm step cannot be null");
        this.algStep = algStep;
    }

    @Override
    public void run (TaskMonitor taskMonitor) {
        Objects.requireNonNull(taskMonitor, "Task monitor cannot be null");
//        contextBundle.getMFRs()
        String targetType;
        Set<CyNode> sourceNodes = contextBundle.getSourceNodes();
        Set<CyNode> targetsForThisRun;

            targetType = "target";
            targetsForThisRun = contextBundle.getTargetNodes();


        Objects.requireNonNull(sourceNodes, "Source nodes not set by user");
        Objects.requireNonNull(targetsForThisRun, "Target nodes not set by user");

        taskMonitor.setTitle(String.format("Minimal Functional Routes to %ss", targetType));

        taskMonitor.setStatusMessage(String.format("Finding paths from %d source nodes to %d %s nodes (algorithm: %s).", sourceNodes.size(), targetsForThisRun.size(), targetType, contextBundle.getPathFindingAlgorithm().shortName()));

        Long preTime = System.nanoTime();
        MFRs = contextBundle.getMFRAlgorithm().MinimalFunctionalRoutes(sourceNodes, targetsForThisRun);
        Long postTime = System.nanoTime();

        Double runTime = (postTime - preTime) / 1E9;

        if (MFRs == null) {
            return;
        }
        
        resultsBundle.setMFRs(MFRs);
        resultsBundle.setMFRExecutionSeconds(runTime);
        
        
        
        
//        switch (algStep) {
//        case FIND_PATHS_TO_TARGETS:
//            resultsBundle.setPathsToTargets(paths);
//            break;
//
//        case FIND_PATHS_TO_OFF_TARGETS:
//            resultsBundle.setPathsToOffTargets(paths);
//            resultsBundle.setPathsToOffTargetsExecutionSeconds(runTime);
//            break;
//
//        default:
            throw new IllegalStateException("Invalid algorithm step for path-finding");
        }

       
    

    /**
     * Return the paths found by this task (or null if the task has
     * not completed)
     **/
    public Collection<List<CyEdge>> getPaths () {
        return MFRs;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getResults (Class<? extends T> type) {
        if (type.isAssignableFrom(OCSANAStep.class)) {
            return (T) algStep;
        } else {
            return (T) getPaths();
        }
    }

    @Override
    public void cancel () {
        super.cancel();
        contextBundle.getPathFindingAlgorithm().cancel();
        resultsBundle.setPathFindingWasCanceled();
        runnerTask.cancel();
    }
}
