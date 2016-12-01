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

package uk.gov.gchq.gaffer.accumulostore.spark.operation.handler;

import org.junit.Test;
import uk.gov.gchq.gaffer.accumulostore.spark.dataframe.GetDataFrameOfElements;
import uk.gov.gchq.gaffer.accumulostore.spark.javardd.GetJavaRDDOfAllElements;
import uk.gov.gchq.gaffer.accumulostore.spark.javardd.GetJavaRDDOfElements;
import uk.gov.gchq.gaffer.accumulostore.spark.operation.handler.dataframe.GetDataFrameOfElementsHandler;
import uk.gov.gchq.gaffer.accumulostore.spark.operation.handler.javardd.GetJavaRDDOfAllElementsHandler;
import uk.gov.gchq.gaffer.accumulostore.spark.operation.handler.javardd.GetJavaRDDOfElementsHandler;
import uk.gov.gchq.gaffer.accumulostore.spark.operation.handler.scalardd.GetRDDOfAllElementsHandler;
import uk.gov.gchq.gaffer.accumulostore.spark.operation.handler.scalardd.GetRDDOfElementsHandler;
import uk.gov.gchq.gaffer.accumulostore.spark.scalardd.GetRDDOfAllElements;
import uk.gov.gchq.gaffer.accumulostore.spark.scalardd.GetRDDOfElements;
import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.store.operationdeclaration.OperationDeclaration;
import uk.gov.gchq.gaffer.store.operationdeclaration.OperationDeclarations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SparkOperationDeclarationsTest {
    public static final String ACCUMULO_OP_DECLARATIONS_JSON_PATH = "sparkAccumuloOperationsDeclarations.json";

    @Test
    public void shouldContainAllSparkOperationsAndHandlers() throws SerialisationException {
        // Given
        final JSONSerialiser jsonSerialiser = new JSONSerialiser();

        // When
        final OperationDeclarations deserialised = jsonSerialiser.deserialise(StreamUtil.openStream(getClass(), ACCUMULO_OP_DECLARATIONS_JSON_PATH), OperationDeclarations.class);

        // Then
        assertEquals(5, deserialised.getOperations().size());

        final OperationDeclaration od0 = deserialised.getOperations().get(0);
        assertEquals(GetJavaRDDOfElements.class, od0.getOperation());
        assertTrue(od0.getHandler() instanceof GetJavaRDDOfElementsHandler);

        final OperationDeclaration od1 = deserialised.getOperations().get(1);
        assertEquals(GetRDDOfElements.class, od1.getOperation());
        assertTrue(od1.getHandler() instanceof GetRDDOfElementsHandler);

        final OperationDeclaration od2 = deserialised.getOperations().get(2);
        assertEquals(GetRDDOfAllElements.class, od2.getOperation());
        assertTrue(od2.getHandler() instanceof GetRDDOfAllElementsHandler);

        final OperationDeclaration od3 = deserialised.getOperations().get(3);
        assertEquals(GetJavaRDDOfAllElements.class, od3.getOperation());
        assertTrue(od3.getHandler() instanceof GetJavaRDDOfAllElementsHandler);

        final OperationDeclaration od4 = deserialised.getOperations().get(4);
        assertEquals(GetDataFrameOfElements.class, od4.getOperation());
        assertTrue(od4.getHandler() instanceof GetDataFrameOfElementsHandler);
    }
}
