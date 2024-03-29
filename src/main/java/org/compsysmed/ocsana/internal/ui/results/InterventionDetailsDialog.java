/**
 * Dialog to contain details about a CI
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.ui.results;

// Java imports
import java.util.*;
import java.awt.BorderLayout;
import java.awt.Component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

// Cytoscape imports
import org.cytoscape.model.CyNetwork;

// OCSANA improts
import org.compsysmed.ocsana.internal.ui.OCSANADialog;

import org.compsysmed.ocsana.internal.ui.results.subpanels.*;

import org.compsysmed.ocsana.internal.util.results.*;

import org.compsysmed.ocsana.internal.util.drugability.*;

/**
 * Dialog presenting details of a given CombinationOfInterventions
 **/
public class InterventionDetailsDialog
    extends OCSANADialog
    implements ActionListener {
    private CombinationOfInterventions ci;
    private Collection<SignedIntervention> signedInterventions;
    private CyNetwork network;
    private DrugabilityDataBundleFactory drugabilityDataBundleFactory;

    // UI elements
    private JComboBox<SignedIntervention> siSelecter;
    private JPanel contentPanel;
    private SignedInterventionReportSubpanel signedInterventionPanel;
    private DrugabilityReportSubpanel drugabilityPanel;

    /**
     * Constructor
     * <p>
     * NOTE: the dialog will be shown immediately on construction!
     *
     * @param parentFrame the parent JFrame of this dialog (used for
     * positioning)
     * @param ci  the CombinationOfInterventions
     * @param signedInterventions  the optimal SignedInterventions of the CI
    **/
    public InterventionDetailsDialog (JFrame parentFrame,
                                      CyNetwork network,
                                      CombinationOfInterventions ci,
                                      Collection<SignedIntervention> signedInterventions) {
        super(parentFrame, "Intervention details report");

        Objects.requireNonNull(network, "Network cannot be null");
        this.network = network;

        Objects.requireNonNull(ci, "CI cannot be null");
        this.ci = ci;

        Objects.requireNonNull(signedInterventions, "Collection of signed interventions cannot be null");
        this.signedInterventions = signedInterventions;

        drugabilityDataBundleFactory = new DrugabilityDataBundleFactory();

        // Set up page skeleton
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        siSelecter = new JComboBox<SignedIntervention>(signedInterventions.toArray(new SignedIntervention[0]));
        siSelecter.setRenderer(new SignedInterventionRenderer());
        siSelecter.addActionListener(this);
        add(siSelecter);

        contentPanel = new JPanel();
        add(contentPanel);
        add(getButtonPanel());
        contentPanel.setLayout(new BorderLayout());
        signedInterventionPanel = new SignedInterventionReportSubpanel(this);
        contentPanel.add(signedInterventionPanel);

        updateSignedInterventionPanel();

        // Format for presentation
        pack();
        setLocationRelativeTo(getOwner());
    }

    /**
     * Respond to a node click event in the signed intervention dialog
     * panel
     **/
    public void processNodeClick (SignedInterventionNode signedNode) {
        if (drugabilityPanel == null) {
            drugabilityPanel = new DrugabilityReportSubpanel();
            
        }

        DrugabilityDataBundle bundle = drugabilityDataBundleFactory.getBundle(signedNode);
        drugabilityPanel.showReport(signedNode, bundle);
        
        add(drugabilityPanel);
        pack();
        setLocationRelativeTo(getOwner());
    }

    private void updateSignedInterventionPanel () {
        SignedIntervention selectedSI = (SignedIntervention) siSelecter.getSelectedItem();
        signedInterventionPanel.updateIntervention(selectedSI);
    }

    @Override
    public void actionPerformed (ActionEvent e) {
        updateSignedInterventionPanel();
    }

    private static final class SignedInterventionRenderer
        extends JLabel
        implements ListCellRenderer<SignedIntervention> {
        public SignedInterventionRenderer () {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent (JList<? extends SignedIntervention> list,
                                                       SignedIntervention value,
                                                       int index,
                                                       boolean isSelected,
                                                       boolean cellHasFocus) {

            String siText = String.format("Activate %d nodes, inhibit %d nodes", value.getNodesToActivate().size(), value.getNodesToInhibit().size());
            setText(siText);

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            return this;
        }
    }
}
