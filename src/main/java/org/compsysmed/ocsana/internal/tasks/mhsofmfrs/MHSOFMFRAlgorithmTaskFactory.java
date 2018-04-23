package org.compsysmed.ocsana.internal.tasks.mhsofmfrs;
import java.util.*;

//Cytoscape imports
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

//OCSANA imports
import org.compsysmed.ocsana.internal.tasks.runner.RunnerTask;

import org.compsysmed.ocsana.internal.util.context.ContextBundle;
import org.compsysmed.ocsana.internal.util.results.ResultsBundle;

public class MHSOFMFRAlgorithmTaskFactory extends AbstractTaskFactory {
   private final RunnerTask runnerTask;
private final ContextBundle contextBundle;
private final ResultsBundle resultsBundle;

public MHSOFMFRAlgorithmTaskFactory (RunnerTask runnerTask,
                               ContextBundle contextBundle,
                               ResultsBundle resultsBundle) {
   super();

   Objects.requireNonNull(runnerTask, "Runner task cannot be null");
   this.runnerTask = runnerTask;

   Objects.requireNonNull(contextBundle, "Context bundle cannot be null");
   this.contextBundle = contextBundle;

   Objects.requireNonNull(resultsBundle, "Context results cannot be null");
   this.resultsBundle = resultsBundle;
}

@Override
public TaskIterator createTaskIterator () {
   TaskIterator tasks = new TaskIterator();
   tasks.append(new MHSOFMFRAlgorithmTask(runnerTask, contextBundle, resultsBundle));
   return tasks;
}
}
