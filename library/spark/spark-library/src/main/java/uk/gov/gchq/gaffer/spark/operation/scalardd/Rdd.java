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
package uk.gov.gchq.gaffer.spark.operation.scalardd;

import org.apache.spark.SparkContext;
import uk.gov.gchq.gaffer.operation.Operation;

public interface Rdd {
    SparkContext getSparkContext();

    void setSparkContext(final SparkContext sparkContext);

    interface Builder<OP extends Rdd,
            B extends Builder<OP, ?>>
            extends Operation.Builder<OP, B> {
        default B sparkContext(final SparkContext sparkContext) {
            _getOp().setSparkContext(sparkContext);
            return _self();
        }
    }
}
