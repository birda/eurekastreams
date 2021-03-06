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
package org.eurekastreams.server.action.execution.start;

import java.util.Collections;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.persistence.TabGroupMapper;
import org.eurekastreams.server.persistence.TabMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;

/**
 * Deletes a Tab, with the ID provided by parameter.
 */
public class DeleteTabExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Logger instance.
     */
    private final Log logger = LogFactory.make();

    /**
     * The TabMapper used to find the tab by id.
     */
    private final TabMapper tabMapper;

    /**
     * The TabGroupMapper that's used to delete the tab.
     */
    private final TabGroupMapper tabGroupMapper;

    /**
     * Domain mapper to delete keys.
     */
    private DomainMapper<Set<String>, Boolean> deleteKeysMapper;

    /**
     * Constructor.
     * 
     * @param inTabGroupMapper
     *            used to delete the tab
     * @param inTabMapper
     *            used to find the tab to be deleted
     * @param inDeleteKeysMapper
     *            mapper to delete cache keys.
     */
    public DeleteTabExecution(final TabGroupMapper inTabGroupMapper, final TabMapper inTabMapper,
            final DomainMapper<Set<String>, Boolean> inDeleteKeysMapper)
    {
        tabGroupMapper = inTabGroupMapper;
        tabMapper = inTabMapper;
        deleteKeysMapper = inDeleteKeysMapper;
    }

    @Override
    public Boolean execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        Boolean success;

        try
        {
            Long tabId = (Long) inActionContext.getParams();

            // will throw NoResultException if no tab by that ID
            Tab tab = tabMapper.findById(tabId);

            tabGroupMapper.deleteTab(tab);

            deleteKeysMapper.execute(Collections.singleton(CacheKeys.PERSON_PAGE_PROPERTIES_BY_ID
                    + inActionContext.getPrincipal().getId()));

            success = true;
        }
        catch (Exception ex)
        {
            logger.error("Error occurred deleting tab.", ex);
            throw new ExecutionException("Error occurred deleting tab.", ex);
        }
        return success;
    }

}
