/**
 * Abstract base class for all MFRs algorithms
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/
package org.compsysmed.ocsana.internal.algorithms.mfrs;

// Java imports
import java.util.*;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
// Cytoscape imports
import org.cytoscape.model.CyNode;
// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.AbstractOCSANAAlgorithm;

/**
 * Public abstract base class for all MFRs algorithms.
 *
 * @param network  the CyNetwork to compute on
 **/

public abstract class AbstractMFRalgorithm
    extends AbstractOCSANAAlgorithm {
    protected static final String UNDIRECTED_ERROR_MESSAGE = "Undirected edges are not supported";

    protected final CyNetwork network;

    public AbstractMFRalgorithm (CyNetwork network) {
        this.network = network;
    }

    /**
     * Compute MHSes of a given collection of sets
     *
     * @param sets  the sets to hit
     * @return the collection of MHSes of the input sets
     **/
    public abstract Collection<List<CyEdge>> MFRs (Set<CyNode> sources,
            Set<CyNode> targets);
}
