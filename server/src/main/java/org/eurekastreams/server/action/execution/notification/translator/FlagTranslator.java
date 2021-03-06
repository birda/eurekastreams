/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eurekastreams.server.action.execution.notification.translator;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.eurekastreams.server.action.execution.notification.NotificationBatch;
import org.eurekastreams.server.action.execution.notification.NotificationPropertyKeys;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Translates the event of someone flagging an activity to appropriate notifications.
 */
public class FlagTranslator implements NotificationTranslator<CreateNotificationsRequest>
{
    /** For getting activity details. */
    private final DomainMapper<List<Long>, List<ActivityDTO>> activitiesMapper;

    /**
     * Mapper to get a list of system admin ids.
     */
    private final DomainMapper<Serializable, List<Long>> systemAdminMapper;

    /**
     * Constructor.
     *
     * @param inActivitiesMapper
     *            For getting activity details.
     * @param inSystemAdminMapper
     *            mapper to get all the system admin ids
     */
    public FlagTranslator(final DomainMapper<List<Long>, List<ActivityDTO>> inActivitiesMapper,
            final DomainMapper<Serializable, List<Long>> inSystemAdminMapper)
    {
        activitiesMapper = inActivitiesMapper;
        systemAdminMapper = inSystemAdminMapper;
    }

    /**
     * This method takes the activity and gets a list of all the admins (since they have authority over the person or
     * group stream where the activity was posted. Those will be the recipients.
     *
     * {@inheritDoc}
     */
    @Override
    public NotificationBatch translate(final CreateNotificationsRequest inRequest)
    {
        // Get the activity
        ActivityDTO activity = activitiesMapper.execute(Collections.singletonList(inRequest.getActivityId())).get(0);
        StreamEntityDTO stream = activity.getDestinationStream();

        // Get the list of admins
        List<Long> adminIds = systemAdminMapper.execute(null);

        NotificationBatch batch = new NotificationBatch(NotificationType.FLAG_ACTIVITY, adminIds);
        batch.setProperty(NotificationPropertyKeys.ACTOR, PersonModelView.class, inRequest.getActorId());
        batch.setProperty("activity", activity);
        batch.setProperty("stream", stream);
        batch.setPropertyAlias(NotificationPropertyKeys.SOURCE, "stream");
        return batch;
    }
}
