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
package org.eurekastreams.server.persistence.mappers.db.metrics;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Mapper to get total comment count for a stream by stream scope id.
 */
public class GetStreamTotalCommentCountDbMapper extends BaseArgDomainMapper<Long, Long>
{
    /**
     * Get the total number of comments for a stream by stream scope id.
     * 
     * @param inRecipientStreamScopeId
     *            the recipient stream scope id to check for comment count
     * @return the number of comments in a stream with the input destination stream scope id
     */
    @Override
    public Long execute(final Long inRecipientStreamScopeId)
    {
        return (Long) getEntityManager().createQuery(
                "SELECT COUNT(id) FROM Comment WHERE target.recipientStreamScope.id = :recipientStreamScopeId")
                .setParameter("recipientStreamScopeId", inRecipientStreamScopeId).getSingleResult();
    }

}
