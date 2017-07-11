/*
 * Copyright 2016-2017 Crown Copyright
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

package uk.gov.gchq.gaffer.rest.service.v2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.gchq.gaffer.jobtracker.JobDetail;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.job.GetAllJobDetails;
import uk.gov.gchq.gaffer.operation.impl.job.GetJobDetails;
import uk.gov.gchq.gaffer.operation.impl.job.GetJobResults;
import uk.gov.gchq.gaffer.rest.factory.GraphFactory;
import uk.gov.gchq.gaffer.rest.factory.UserFactory;
import uk.gov.gchq.gaffer.user.User;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

import static uk.gov.gchq.gaffer.rest.service.v2.ServiceConstants.GAFFER_MEDIA_TYPE;
import static uk.gov.gchq.gaffer.rest.service.v2.ServiceConstants.GAFFER_MEDIA_TYPE_HEADER;

/**
 * An implementation of {@link IJobServiceV2}. By default it will use a singleton
 * {@link uk.gov.gchq.gaffer.graph.Graph} generated using the {@link uk.gov.gchq.gaffer.rest.factory.GraphFactory}.
 * All operations are simply delegated to the graph.
 * Pre and post operation hooks are available by extending this class and implementing preOperationHook and/or
 * postOperationHook.
 * <p>
 * By default queries will be executed with an UNKNOWN user containing no auths.
 * The userFactory.createUser() method should be overridden and a {@link uk.gov.gchq.gaffer.user.User} object should
 * be created from the http request.
 * </p>
 */
public class JobServiceV2 implements IJobServiceV2 {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobServiceV2.class);

    @Inject
    private GraphFactory graphFactory;

    @Inject
    private UserFactory userFactory;

    @Context
    private UriInfo context;

    @Override
    public Response executeJob(final OperationChain opChain) throws OperationException {
        final User user = userFactory.createUser();
        preOperationHook(opChain, user);

        try {
            final JobDetail jobDetail = graphFactory.getGraph()
                                                    .executeJob(opChain, user);
            LOGGER.info("Job started = {}", jobDetail);

            final URI location = context.getAbsolutePathBuilder()
                                        .path(jobDetail.getJobId())
                                        .build();

            return Response.created(location)
                           .header(GAFFER_MEDIA_TYPE_HEADER, GAFFER_MEDIA_TYPE)
                           .build();
        } finally {
            postOperationHook(opChain, user);
        }
    }

    @Override
    public Response details() throws OperationException {
        return Response.ok(graphFactory.getGraph()
                                       .execute(new GetAllJobDetails(), userFactory
                                               .createUser()))
                       .header(GAFFER_MEDIA_TYPE_HEADER, GAFFER_MEDIA_TYPE)
                       .build();
    }

    @Override
    public Response details(final String id) throws OperationException {
        return Response.ok(graphFactory.getGraph().execute(
                new GetJobDetails.Builder()
                        .jobId(id)
                        .build(),
                userFactory.createUser()))
                       .header(GAFFER_MEDIA_TYPE_HEADER, GAFFER_MEDIA_TYPE)
                       .build();
    }

    @Override
    public Response results(final String id) throws OperationException {
        return Response.ok(graphFactory.getGraph().execute(
                new GetJobResults.Builder()
                        .jobId(id)
                        .build(),
                userFactory.createUser())).build();
    }

    protected void preOperationHook(final OperationChain<?> opChain, final User user) {
        // no action by default
    }

    protected void postOperationHook(final OperationChain<?> opChain, final User user) {
        // no action by default
    }
}