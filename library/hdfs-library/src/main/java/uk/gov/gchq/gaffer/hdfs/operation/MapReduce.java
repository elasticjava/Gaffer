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
package uk.gov.gchq.gaffer.hdfs.operation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.Lists;
import org.apache.hadoop.mapreduce.Partitioner;
import uk.gov.gchq.gaffer.hdfs.operation.handler.job.initialiser.JobInitialiser;
import uk.gov.gchq.gaffer.operation.Operation;
import java.util.ArrayList;
import java.util.List;


/**
 * This <code>MapReduce</code> class should be implemented for any Operations that run map reduce jobs.
 * {@link JobInitialiser}.
 * <p>
 * <b>NOTE</b> - currently this job has to be run as a hadoop job.
 * <p>
 * If you want to specify the number of mappers and/or the number of reducers
 * then either set the exact number or set a min and/or max value.
 *
 * @see Builder
 */
public interface MapReduce {
    List<String> getInputPaths();

    void setInputPaths(final List<String> inputPaths);

    default void addInputPaths(final List<String> inputPaths) {
        if (null == getInputPaths()) {
            setInputPaths(new ArrayList<>(inputPaths));
        } else {
            getInputPaths().addAll(inputPaths);
        }
    }

    default void addInputPath(final String inputPath) {
        if (null == getInputPaths()) {
            setInputPaths(Lists.newArrayList(inputPath));
        } else {
            getInputPaths().add(inputPath);
        }
    }

    String getOutputPath();

    void setOutputPath(final String outputPath);

    /**
     * A job initialiser allows additional job initialisation to be carried out in addition to that done by the
     * store.
     * Most stores will probably require the Job Input to be configured in this initialiser as this is specific to the
     * type of data store in Hdfs.
     * For Avro data see {@link uk.gov.gchq.gaffer.hdfs.operation.handler.job.initialiser.AvroJobInitialiser}.
     * For Text data see {@link uk.gov.gchq.gaffer.hdfs.operation.handler.job.initialiser.TextJobInitialiser}.
     *
     * @return the job initialiser
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "class")
    JobInitialiser getJobInitialiser();

    void setJobInitialiser(final JobInitialiser jobInitialiser);

    Integer getNumMapTasks();

    void setNumMapTasks(final Integer numMapTasks);

    Integer getNumReduceTasks();

    void setNumReduceTasks(final Integer numReduceTasks);

    Integer getMinMapTasks();

    void setMinMapTasks(final Integer minMapTasks);

    Integer getMaxMapTasks();

    void setMaxMapTasks(final Integer maxMapTasks);

    Integer getMinReduceTasks();

    void setMinReduceTasks(final Integer minReduceTasks);

    Integer getMaxReduceTasks();

    void setMaxReduceTasks(final Integer maxReduceTasks);

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    boolean isUseProvidedSplits();

    void setUseProvidedSplits(boolean useProvidedSplits);

    String getSplitsFilePath();

    void setSplitsFilePath(String splitsFile);

    Class<? extends Partitioner> getPartitioner();

    void setPartitioner(final Class<? extends Partitioner> partitioner);

    interface Builder<OP extends MapReduce, B extends Builder<OP, ?>> extends Operation.Builder<OP, B> {
        default B inputPaths(final List<String> inputPaths) {
            _getOp().setInputPaths(inputPaths);
            return _self();
        }

        default B addInputPaths(final List<String> inputPaths) {
            _getOp().addInputPaths(inputPaths);
            return _self();
        }

        default B addInputPath(final String inputPath) {
            _getOp().addInputPath(inputPath);
            return _self();
        }

        default B outputPath(final String outputPath) {
            _getOp().setOutputPath(outputPath);
            return _self();
        }

        default B splitsFilePath(final String splitsFile) {
            _getOp().setSplitsFilePath(splitsFile);
            return _self();
        }

        default B useProvidedSplits(final boolean useProvidedSplits) {
            _getOp().setUseProvidedSplits(useProvidedSplits);
            return _self();
        }

        default B jobInitialiser(final JobInitialiser jobInitialiser) {
            _getOp().setJobInitialiser(jobInitialiser);
            return _self();
        }

        default B reducers(final Integer numReduceTasks) {
            if (null != _getOp().getMinReduceTasks() || null != _getOp().getMaxReduceTasks()) {
                throw new IllegalArgumentException("Invalid combination of fields. " +
                        "Either provide the number of reducers to use or provide a min and max value.");
            }
            _getOp().setNumReduceTasks(numReduceTasks);
            return _self();
        }

        default B minReducers(final Integer minReduceTasks) {
            if (null != _getOp().getNumReduceTasks()) {
                throw new IllegalArgumentException("Invalid combination of fields. " +
                        "Either provide the number of reducers to use or provide a min and max value.");
            }
            _getOp().setMinReduceTasks(minReduceTasks);
            return _self();
        }

        default B maxReducers(final Integer maxReduceTasks) {
            if (null != _getOp().getNumReduceTasks()) {
                throw new IllegalArgumentException("Invalid combination of fields. " +
                        "Either provide the number of reducers to use or provide a min and max value.");
            }
            _getOp().setMaxReduceTasks(maxReduceTasks);
            return _self();
        }

        default B mappers(final Integer numMapTasks) {
            if (null != _getOp().getMinMapTasks() || null != _getOp().getMaxMapTasks()) {
                throw new IllegalArgumentException("Invalid combination of fields. " +
                        "Either provide the number of mappers to use or provide a min and max value.");
            }
            _getOp().setNumMapTasks(numMapTasks);
            return _self();
        }

        default B minMappers(final Integer minMapTasks) {
            if (null != _getOp().getNumMapTasks()) {
                throw new IllegalArgumentException("Invalid combination of fields. " +
                        "Either provide the number of mappers to use or provide a min and max value.");
            }
            _getOp().setMinMapTasks(minMapTasks);
            return _self();
        }

        default B maxMappers(final Integer maxMapTasks) {
            if (null != _getOp().getNumMapTasks()) {
                throw new IllegalArgumentException("Invalid combination of fields. " +
                        "Either provide the number of mappers to use or provide a min and max value.");
            }
            _getOp().setMaxMapTasks(maxMapTasks);
            return _self();
        }

        default B partitioner(final Class<? extends Partitioner> partitioner) {
            _getOp().setPartitioner(partitioner);
            return _self();
        }
    }
}
