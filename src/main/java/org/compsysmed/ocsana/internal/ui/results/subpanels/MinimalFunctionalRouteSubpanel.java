package org.compsysmed.ocsana.internal.ui.results.subpanels;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.compsysmed.ocsana.internal.ui.results.subpanels.PathsSubpanel.PathType;
import org.compsysmed.ocsana.internal.util.context.ContextBundle;
import org.compsysmed.ocsana.internal.util.results.ResultsBundle;
import org.cytoscape.model.CyEdge;

public class MinimalFunctionalRouteSubpanel
extends JPanel {
public MinimalFunctionalRouteSubpanel (ContextBundle contextBundle,
                   ResultsBundle resultsBundle,
                  MFRType toTargets) {
    Collection<List<CyEdge>> MFRs;

    Double MFRfindingtime;




    switch (toTargets) {
    case TO_TARGETS:
        MFRs = resultsBundle.getMFRs();
        MFRfindingtime=resultsBundle.getMFRExecutionSeconds();
        break;

    case TO_OFF_TARGETS:
        MFRs = resultsBundle.getPathsToOffTargets();
        MFRfindingtime= resultsBundle.getMFRsToOffTargetsExecutionSeconds();
        break;

    default:
        throw new IllegalStateException("Undefined path type");
    }



if (MFRs != null) {
    List<String> MFRLines = new ArrayList<>();
    for (List<CyEdge> mfr: MFRs) {
        MFRLines.add(contextBundle.MFRString(mfr));
    }

    // Sort alphabetically
    Collections.sort(MFRLines);

    // Create panel
    JTextArea pathTextArea = new JTextArea(String.join("\n", MFRLines));

    JScrollPane pathScrollPane = new JScrollPane(pathTextArea);

    setLayout(new BorderLayout());
    String panelText = String.format("Found %d MFRs to %s in %f s.", MFRs.size(), toTargets, MFRfindingtime);
    add(new JLabel(panelText), BorderLayout.PAGE_START);
    add(pathScrollPane, BorderLayout.CENTER);
}
}

public enum MFRType {
TO_TARGETS("targets"),
TO_OFF_TARGETS("off-targets");

private final String pluralName;

private MFRType(String pluralName) {
    this.pluralName = pluralName;
}

@Override
public String toString() {
    return pluralName;
}
}
}
