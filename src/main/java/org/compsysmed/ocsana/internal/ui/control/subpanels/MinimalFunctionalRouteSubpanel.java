/**
 * Subpanel configuring Minimal Functional Route algorithm in OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.ui.control.subpanels;

// Java imports
import java.util.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

// Cytoscape imports
import org.cytoscape.work.swing.PanelTaskManager;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.MinimalFunctionalRoutes.*;





import org.compsysmed.ocsana.internal.ui.control.OCSANAControlPanel;

import org.compsysmed.ocsana.internal.util.context.ContextBundleBuilder;

/**
 * Subpanel for user configuration of path-finding algorithm
 **/
public class MinimalFunctionalRouteSubpanel
    extends AbstractControlSubpanel
    implements ActionListener {
    private ContextBundleBuilder contextBundleBuilder;
    private PanelTaskManager taskManager;

    // UI elements
    private JPanel algSelectionPanel;
    private JComboBox<AbstractMFRalgorithm> algorithmSelecter;
    private JCheckBox computeMFRs;
    private JCheckBox includeCompositeNodes;
    private JPanel tunablePanel;

    /**
     * Constructor
     *
     * @param contextBundleBuilder  the context bundle builder
     * @param taskManager  a PanelTaskManager to provide @Tunable panels
     **/
    public MinimalFunctionalRouteSubpanel (OCSANAControlPanel controlPanel,
                                ContextBundleBuilder contextBundleBuilder,
                                PanelTaskManager taskManager) {
        super(controlPanel);

        // Initial setup
        this.contextBundleBuilder = contextBundleBuilder;
        this.taskManager = taskManager;

        setStandardLayout(this);

        add(makeHeader("Configure Minimal Functional Routes"));
        // Algorithm configuration panel
        tunablePanel = new JPanel();
        setStandardLayout(tunablePanel);
        

        
        
        
        // Compute MFRs?
        computeMFRs = new JCheckBox("Compute MFRs", contextBundleBuilder.getMFRs());
        add(computeMFRs);
        
        includeCompositeNodes = new JCheckBox("Include Composite Nodes in MHS of MFRs", contextBundleBuilder.getIncludecomposite());
        add(includeCompositeNodes);
        
        
        add(tunablePanel);
        // Algorithm selecter
        algSelectionPanel = new JPanel();
        setStandardLayout(algSelectionPanel);
        add(algSelectionPanel);

        algSelectionPanel.add(new JLabel("Algorithm:"));

        List<AbstractMFRalgorithm> algorithms = new ArrayList<>();
        algorithms.add(new MFR(contextBundleBuilder.getNetwork()));

        algorithmSelecter = new JComboBox<>(algorithms.toArray(new AbstractMFRalgorithm[algorithms.size()]));
        algSelectionPanel.add(algorithmSelecter);
        algorithmSelecter.addActionListener(this);


        updateTunablePanel();
    }

    private void updateTunablePanel () {
        tunablePanel.removeAll();
        JPanel content = taskManager.getConfiguration(null, getAlgorithm());
        if (content != null) {
            tunablePanel.add(content);
        }

     

        tunablePanel.revalidate();
        tunablePanel.repaint();
    }

    private AbstractMFRalgorithm getAlgorithm () {
        return (AbstractMFRalgorithm) algorithmSelecter.getSelectedItem();
    }

    @Override
    public void actionPerformed (ActionEvent e) {
        updateTunablePanel();
    }

    @Override
    public void updateContextBuilder () {
    		contextBundleBuilder.setcomputeMFRs(computeMFRs.isSelected());
        contextBundleBuilder.setIncludecomposite(includeCompositeNodes.isSelected());
        contextBundleBuilder.setMFRalgorithm(getAlgorithm()); 
    }
}