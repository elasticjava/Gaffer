/*
 * Copyright 2016 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gaffer.accumulostore.key.core.impl.byteEntity;

import gaffer.accumulostore.utils.AccumuloStoreConstants;
import gaffer.accumulostore.utils.IteratorOptionsBuilder;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.iterators.Filter;
import org.apache.accumulo.core.iterators.IteratorEnvironment;
import org.apache.accumulo.core.iterators.SortedKeyValueIterator;
import java.io.IOException;
import java.util.Map;

public class ByteEntityRangeElementPropertyFilterIterator extends Filter {

    private boolean edges = false;
    private boolean entities = false;
    private boolean unDirectedEdges = false;
    private boolean directedEdges = false;
    private boolean incomingEdges = false;
    private boolean outgoingEdges = false;
    private boolean correctWayEdges = false;

    @Override
    public boolean accept(final Key key, final Value value) {
        final byte flag = getFlag(key);
        final boolean isEdge = flag != ByteEntityPositions.ENTITY;
        if (!edges && isEdge) {
            return false;
        } else if (!entities && !isEdge) {
            return false;
        }
        return !isEdge || checkEdge(flag);
    }

    private byte getFlag(final Key key) {
        final byte[] rowID = key.getRowData().getBackingArray();
        return rowID[rowID.length - 1];
    }

    private boolean checkEdge(final byte flag) {
        if (correctWayEdges) {
            return isCorrectWayEdge(flag);
        } else if (unDirectedEdges) {
            return isUndirectedEdge(flag);
        } else if (directedEdges) {
            return isDirectedEdge(flag) && checkDirection(flag);
        } else {
            return checkDirection(flag);
        }
    }

    private boolean isDirectedEdge(final byte flag) {
        return ByteEntityPositions.CORRECT_WAY_DIRECTED_EDGE == flag
                || ByteEntityPositions.INCORRECT_WAY_DIRECTED_EDGE == flag;
    }

    private boolean isUndirectedEdge(final byte flag) {
        return ByteEntityPositions.CORRECT_WAY_UNDIRECTED_EDGE == flag
                || ByteEntityPositions.INCORRECT_WAY_UNDIRECTED_EDGE == flag;
    }

    private boolean isCorrectWayEdge(final byte flag) {
        return (!directedEdges && ByteEntityPositions.CORRECT_WAY_UNDIRECTED_EDGE == flag)
                || (!unDirectedEdges && ByteEntityPositions.CORRECT_WAY_DIRECTED_EDGE == flag);
    }

    private boolean checkDirection(final byte flag) {
        if (incomingEdges) {
            if (flag == ByteEntityPositions.CORRECT_WAY_DIRECTED_EDGE) {
                return false;
            }
        } else if (outgoingEdges) {
            if (flag == ByteEntityPositions.INCORRECT_WAY_DIRECTED_EDGE) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void init(final SortedKeyValueIterator<Key, Value> source, final Map<String, String> options,
                     final IteratorEnvironment env) throws IOException {
        super.init(source, options, env);
        validateOptions(options);
    }

    @Override
    public boolean validateOptions(final Map<String, String> options) {
        if (!super.validateOptions(options)) {
            return false;
        }
        if (options.containsKey(AccumuloStoreConstants.DIRECTED_EDGE_ONLY)
                && options.containsKey(AccumuloStoreConstants.UNDIRECTED_EDGE_ONLY)) {
            throw new IllegalArgumentException("Must specify ONLY ONE of " + AccumuloStoreConstants.DIRECTED_EDGE_ONLY + " or "
                    + AccumuloStoreConstants.UNDIRECTED_EDGE_ONLY);
        }
        if (options.containsKey(AccumuloStoreConstants.INCOMING_EDGE_ONLY)
                && options.containsKey(AccumuloStoreConstants.OUTGOING_EDGE_ONLY)) {
            throw new IllegalArgumentException(
                    "Must specify ONLY ONE of "
                            + AccumuloStoreConstants.INCOMING_EDGE_ONLY
                            + " or "
                            + AccumuloStoreConstants.OUTGOING_EDGE_ONLY);
        }
        if (options.containsKey(AccumuloStoreConstants.INCOMING_EDGE_ONLY)) {
            incomingEdges = true;
        } else if (options.containsKey(AccumuloStoreConstants.OUTGOING_EDGE_ONLY)) {
            outgoingEdges = true;
        }
        if (options.containsKey(AccumuloStoreConstants.DIRECTED_EDGE_ONLY)) {
            directedEdges = true;
        } else if (options.containsKey(AccumuloStoreConstants.UNDIRECTED_EDGE_ONLY)) {
            unDirectedEdges = true;
        }
        if (options.containsKey(AccumuloStoreConstants.INCLUDE_ENTITIES)) {
            entities = true;
        }
        if (!options.containsKey(AccumuloStoreConstants.NO_EDGES)) {
            edges = true;
        }
        if (options.containsKey(AccumuloStoreConstants.CORRECT_WAY_EDGES_ONLY)) {
            correctWayEdges = true;
        }
        return true;
    }

    @Override
    public IteratorOptions describeOptions() {
        return new IteratorOptionsBuilder(super.describeOptions())
                .addNamedOption(AccumuloStoreConstants.DIRECTED_EDGE_ONLY,
                        "Optional : Set if only directed edges should be returned")
                .addNamedOption(AccumuloStoreConstants.UNDIRECTED_EDGE_ONLY,
                        "Optional: Set if only undirected edges should be returned")
                .addNamedOption(AccumuloStoreConstants.INCLUDE_ENTITIES,
                        "Optional: Set if entities should be returned")
                .addNamedOption(AccumuloStoreConstants.INCOMING_EDGE_ONLY,
                        "Optional: Set if only incoming edges should be returned")
                .addNamedOption(AccumuloStoreConstants.OUTGOING_EDGE_ONLY,
                        "Optional: Set if only outgoing edges should be returned")
                .addNamedOption(AccumuloStoreConstants.NO_EDGES,
                        "Optional: Set if no edges should be returned")
                .setIteratorName(AccumuloStoreConstants.RANGE_ELEMENT_PROPERTY_FILTER_ITERATOR_NAME)
                .setIteratorDescription(
                        "Only returns Entities or Edges that are directed undirected incoming or outgoing as specified by the user's options")
                .build();
    }

}
