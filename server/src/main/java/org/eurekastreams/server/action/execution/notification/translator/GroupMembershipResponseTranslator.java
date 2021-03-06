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

import org.eurekastreams.server.action.execution.notification.NotificationBatch;
import org.eurekastreams.server.action.execution.notification.NotificationPropertyKeys;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Translates the event of a coordinator approving or denying a private group membership request to to the appropriate
 * notification.
 */
public class GroupMembershipResponseTranslator implements NotificationTranslator<CreateNotificationsRequest>
{
    /** Notification type to generate. */
    private final NotificationType type;

    /**
     * Constructor.
     *
     * @param inType
     *            Notification type to generate.
     */
    public GroupMembershipResponseTranslator(final NotificationType inType)
    {
        type = inType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationBatch translate(final CreateNotificationsRequest inRequest)
    {
        // TODO: Make sense out of the inputs by using a specific request type.

        NotificationBatch batch = new NotificationBatch(type, inRequest.getActivityId());
        batch.setProperty(NotificationPropertyKeys.ACTOR, PersonModelView.class, inRequest.getActorId());
        batch.setProperty("group", DomainGroupModelView.class, inRequest.getDestinationId());
        // TODO: add appropriate properties
        return batch;
    }
}
