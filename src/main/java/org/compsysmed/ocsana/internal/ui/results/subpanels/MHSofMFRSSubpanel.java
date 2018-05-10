/**
 * Panel to contain OCSANA CIs report
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.ui.results.subpanels;

// Java imports
import java.util.*;

import java.awt.BorderLayout;
import java.awt.Point;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SortOrder;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

// OCSANA imports
import org.compsysmed.ocsana.internal.ui.results.InterventionDetailsDialog;
import org.compsysmed.ocsana.internal.ui.results.InterventionDetailsDialogOfMFRs;
import org.compsysmed.ocsana.internal.util.context.ContextBundle;
import org.compsysmed.ocsana.internal.util.results.ResultsBundle;
import org.compsysmed.ocsana.internal.util.results.CombinationOfInterventions;
import org.compsysmed.ocsana.internal.util.results.CombinationOfInterventionsOfMFRs;
public class MHSofMFRSSubpanel
    extends JPanel {
    private final ContextBundle contextBundle;
    private final ResultsBundle resultsBundle;
    private final JFrame cytoscapeFrame;

    public MHSofMFRSSubpanel (ContextBundle contextBundle,
                           ResultsBundle resultsBundle,
                           JFrame cytoscapeFrame) {
        Objects.requireNonNull(contextBundle, "Context bundle cannot be null");
        this.contextBundle = contextBundle;

        Objects.requireNonNull(resultsBundle, "Context results cannot be null");
        this.resultsBundle = resultsBundle;

        Objects.requireNonNull(cytoscapeFrame, "Cytoscape frame cannot be null");
        this.cytoscapeFrame = cytoscapeFrame;

        if (resultsBundle.getMHSOFMFRS() != null) {
            MHSOFMFRSTable mhsofmfrsTable = new MHSOFMFRSTable();

            JScrollPane mhsofmfrsScrollPane = new JScrollPane(mhsofmfrsTable);

            setLayout(new BorderLayout());
            String mhsofmfrsText = String.format("Found %d optimal CIs in %f s.", resultsBundle.getMHSOFMFRS().size(), resultsBundle.getMHSOFMFRSExecutionSeconds());
            add(new JLabel(mhsofmfrsText), BorderLayout.PAGE_START);
            add(mhsofmfrsScrollPane, BorderLayout.CENTER);
        }
    }

    private class MHSOFMFRSTable extends JTable {
        private final List<CombinationOfInterventionsOfMFRs> MHSOFMFRS;

        public MHSOFMFRSTable () {
            this.MHSOFMFRS = new ArrayList<>(resultsBundle.getMHSOFMFRS());
            Collections.sort(MHSOFMFRS, new SortbySize());
            MHSOFMFRSTableModel mhsofmfrsModel = new MHSOFMFRSTableModel(contextBundle, resultsBundle, MHSOFMFRS);
            setModel(mhsofmfrsModel);



            // Handle double click events per row
            MouseListener mouseListener = new MouseAdapter() {
                    public void mousePressed(MouseEvent me) {
                        Point p = me.getPoint();
                        int row = rowAtPoint(p);

                        if (me.getClickCount() == 2 && row != -1) {
                            handleUserDoubleClick(row);
                        }
                    }};

            addMouseListener(mouseListener);

            setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        }

        public void handleUserDoubleClick (Integer row) {
        		
            CombinationOfInterventionsOfMFRs mhsofmfr = MHSOFMFRS.get(row);
//            InterventionDetailsDialogOfMFRs detailsDialog = new InterventionDetailsDialogOfMFRs(cytoscapeFrame, contextBundle.getNetwork(), mhsofmfr);
            
        }
    }

    class SortbySize implements Comparator<CombinationOfInterventionsOfMFRs>
    {

		@Override
		public int compare(CombinationOfInterventionsOfMFRs o1, CombinationOfInterventionsOfMFRs o2) {
			return Integer.compare(o1.size(), o2.size());

		}
    }
    private static class MHSOFMFRSTableModel extends AbstractTableModel {
        private final ContextBundle contextBundle;
        private final ResultsBundle resultsBundle;
        private final List<CombinationOfInterventionsOfMFRs> MHSOFMFRS;

        public MHSOFMFRSTableModel (ContextBundle contextBundle,
                              ResultsBundle resultsBundle,
                              List<CombinationOfInterventionsOfMFRs> MHSOFMFRS) {
            Objects.requireNonNull(contextBundle, "Context bundle cannot be null");
            this.contextBundle = contextBundle;

            Objects.requireNonNull(resultsBundle, "Context results cannot be null");
            this.resultsBundle = resultsBundle;

            Objects.requireNonNull(MHSOFMFRS, "CIs collection cannot be null");
            this.MHSOFMFRS = MHSOFMFRS;
        }
        String[] colNames = {"MHS", "Size"};

        @Override
        public String getColumnName (int col) {
            return colNames[col];
        }

        @Override
        public int getRowCount () {
            return MHSOFMFRS.size();
        }

        @Override
        public int getColumnCount () {
            return colNames.length;
        }

        @Override
        public Object getValueAt (int row, int col) {
            CombinationOfInterventionsOfMFRs mhs = MHSOFMFRS.get(row);
            switch (col) {
            case 0:
                return mhs.interventionNodesString();

            case 1:
                return mhs.size();

            default:
                throw new IllegalArgumentException(String.format("Table does not have %d columns", col));
            }
        }

//        private Double getSIPriorityScore (CombinationOfInterventions ci) {
//            Double maxPriorityScore = resultsBundle.getOptimalInterventionSignings(ci).stream().mapToDouble(contextBundle.getSIScoringAlgorithm()::computePriorityScore).max().orElse(0d);
//            return maxPriorityScore;
//        }

        @Override
        public Class<?> getColumnClass(int column) {
            try {
                return getValueAt(0, column).getClass();
            } catch (IndexOutOfBoundsException|NullPointerException exception) {
                return Object.class;
            }
        }

        // Disable cell editing
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
}