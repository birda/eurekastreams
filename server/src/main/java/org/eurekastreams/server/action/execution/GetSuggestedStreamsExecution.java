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
package org.eurekastreams.server.action.execution;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.domain.dto.StreamDTO;
import org.eurekastreams.server.persistence.comparators.StreamDTOFollowerCountDescendingComparator;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.SuggestedStreamsRequest;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Execution strategy to get suggested group and people streams for a user.
 */
public class GetSuggestedStreamsExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Mapper to get suggested people streams.
     */
    private DomainMapper<SuggestedStreamsRequest, List<PersonModelView>> suggestedPersonMapper;

    /**
     * Mapper to get suggested group streams.
     */
    private DomainMapper<SuggestedStreamsRequest, List<DomainGroupModelView>> suggestedGroupMapper;

    /**
     * Constructor.
     * 
     * @param inSuggestedPersonMapper
     *            mapper to get suggested people streams
     * @param inSuggestedGroupMapper
     *            mapper to get suggested group streams
     */
    public GetSuggestedStreamsExecution(
            final DomainMapper<SuggestedStreamsRequest, List<PersonModelView>> inSuggestedPersonMapper,
            final DomainMapper<SuggestedStreamsRequest, List<DomainGroupModelView>> inSuggestedGroupMapper)
    {
        suggestedPersonMapper = inSuggestedPersonMapper;
        suggestedGroupMapper = inSuggestedGroupMapper;
    }

    /**
     * Get the suggested people and groups as StreamDTOs that represent the suggested streams to follow. Mapper takes an
     * Integer representing how many suggestions to get
     * 
     * @param inActionContext
     *            the action context
     * @return ArrayList of StreamDTO representing the streams suggested for the current user to follow
     * @throws ExecutionException
     *             (never)
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        Long personId = inActionContext.getPrincipal().getId();
        Integer suggestionCount = (Integer) inActionContext.getParams();

        SuggestedStreamsRequest mapperRequest = new SuggestedStreamsRequest(personId, suggestionCount.intValue());
        ArrayList<StreamDTO> suggestions = new ArrayList<StreamDTO>();

        suggestions.addAll(suggestedPersonMapper.execute(mapperRequest));
        suggestions.addAll(suggestedGroupMapper.execute(mapperRequest));

        // sort the list
        Collections.sort(suggestions, new StreamDTOFollowerCountDescendingComparator());

        // return those requested
        suggestions = new ArrayList<StreamDTO>(suggestions.subList(0, suggestionCount));
        return suggestions;
    }
}
