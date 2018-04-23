


package org.compsysmed.ocsana.internal.ui.results.subpanels;


//Java imports
import java.util.*;
import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

//Cytoscape imports
import org.cytoscape.model.CyEdge;
import org.compsysmed.ocsana.internal.ui.results.subpanels.PathsSubpanel.PathType;
//OCSANA imports
import org.compsysmed.ocsana.internal.util.context.ContextBundle;
import org.compsysmed.ocsana.internal.util.results.ResultsBundle;


public class MFRSubpanel
extends JPanel {
public MFRSubpanel (ContextBundle contextBundle,
                   ResultsBundle resultsBundle,
                   PathType pathType) {
    Collection<List<CyEdge>> MFRs;
   


        MFRs = resultsBundle.getMFRs();


        

    

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
        String panelText = String.format("Found Minimal Functional Routes");
        add(new JLabel(panelText), BorderLayout.PAGE_START);
        add(pathScrollPane, BorderLayout.CENTER);
    }
}

public enum PathType {
    TO_TARGETS("targets"),
    TO_OFF_TARGETS("off-targets");

    private final String pluralName;

    private PathType(String pluralName) {
        this.pluralName = pluralName;
    }

    @Override
    public String toString() {
        return pluralName;
    }
}
}