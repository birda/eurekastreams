/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution.notification.notifier;

import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.velocity.context.Context;
import org.eurekastreams.server.domain.stream.ActivityDTO;

/**
 * Class which provides helper routines for building notification messages.
 */
public class NotificationMessageBuilderHelper
{
    /** Eureka variable start marker. */
    private static final String VARIABLE_START_MARKER = "%EUREKA:";

    /** Eureka variable end marker. */
    private static final String VARIABLE_END_MARKER = "%";

    /**
     * Returns a variable-substituted version of the activity's body.
     * 
     * @param activity
     *            Activity.
     * @param context
     *            Velocity context.
     * @return Activity body text.
     */
    public String resolveActivityBody(final ActivityDTO activity, final Context context)
    {
        StrSubstitutor transform = new StrSubstitutor(new StrLookup()
        {
            @Override
            public String lookup(final String variableName)
            {
                if ("ACTORNAME".equals(variableName))
                {
                    return activity.getActor().getDisplayName();
                }
                else
                {
                    return null;
                }
            }
        }, VARIABLE_START_MARKER, VARIABLE_END_MARKER, StrSubstitutor.DEFAULT_ESCAPE);
        String result = transform.replace(activity.getBaseObjectProperties().get("content"));
        return result;
    }
}
