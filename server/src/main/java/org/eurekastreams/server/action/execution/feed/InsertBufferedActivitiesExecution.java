/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution.feed;

import java.io.Serializable;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.persistence.mappers.cache.AddBufferedActivitiesToCache;

/**
 * This action is necessary so we can queue up the execution of this mapper on a timer.
 * 
 */
@SuppressWarnings("unchecked")
public class InsertBufferedActivitiesExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * Mapper to execute.
     */
    private AddBufferedActivitiesToCache addActivitiesMapper;

    /**
     * Default constructor.
     */
    public InsertBufferedActivitiesExecution()
    {    
    }
    
    /**
     * Constructor.
     * 
     * @param inAddActivitiesMapper
     *            the mapper.
     */
    public InsertBufferedActivitiesExecution(final AddBufferedActivitiesToCache inAddActivitiesMapper)
    {
        addActivitiesMapper = inAddActivitiesMapper;
    }

    /**
     * {@inheritDoc}
     */
    public Serializable execute(final ActionContext inActionContext) throws ExecutionException
    {
        return addActivitiesMapper.execute();
    }

}
