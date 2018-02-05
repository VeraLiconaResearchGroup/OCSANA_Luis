/**
 * Task to run CI sign assignment algorithm in OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.signassignment;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.work.TaskMonitor;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.AbstractOCSANATask;
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;
import org.compsysmed.ocsana.internal.tasks.runner.RunnerTask;

import org.compsysmed.ocsana.internal.util.context.ContextBundle;
import org.compsysmed.ocsana.internal.util.results.ResultsBundle;

import org.compsysmed.ocsana.internal.util.results.CombinationOfInterventions;
import org.compsysmed.ocsana.internal.util.results.SignedIntervention;

public class SignAssignmentAlgorithmTask
    extends AbstractOCSANATask {
    private static final OCSANAStep algStep = OCSANAStep.ASSIGN_CI_SIGNS;

    private final RunnerTask runnerTask;
    private final ContextBundle contextBundle;
    private final ResultsBundle resultsBundle;

    public SignAssignmentAlgorithmTask (RunnerTask runnerTask,
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
        taskMonitor.setTitle("Computing sign assignments for CIs");

        Long preTime = System.nanoTime();

        for (CombinationOfInterventions ci: resultsBundle.getCIs()) {
            if (cancelled) {
                break;
            }

            Collection<SignedIntervention> optimalSignings = contextBundle.getCISignAlgorithm().bestInterventions(ci, contextBundle.getTargetsToActivate());
            if (optimalSignings != null) {
                resultsBundle.setOptimalInterventionSignings(ci, optimalSignings);
            }
        }
        Long postTime = System.nanoTime();

        Double signingTime = (postTime - preTime) / 1E9;

        taskMonitor.setStatusMessage(String.format("Found optimal sign assignments in %f s.", signingTime));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getResults (Class<? extends T> type) {
        if (type.isAssignableFrom(OCSANAStep.class)) {
            return (T) algStep;
        } else {
            return (T) resultsBundle;
        }
    }

    @Override
    public void cancel () {
        super.cancel();
        contextBundle.getCISignAlgorithm().cancel();
        runnerTask.cancel();
    }
}
