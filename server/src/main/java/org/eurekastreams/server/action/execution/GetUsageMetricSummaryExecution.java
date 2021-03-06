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
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.date.DateDayExtractor;
import org.eurekastreams.commons.date.WeekdaysInDateRangeStrategy;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.DailyUsageSummary;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.UsageMetricSummaryDTO;
import org.eurekastreams.server.service.actions.requests.UsageMetricStreamSummaryRequest;

import com.ibm.icu.util.Calendar;

/**
 * Execution strategy to get the usage metric information for a stream or for all streams.
 */
public class GetUsageMetricSummaryExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Logger.
     */
    private Log logger = LogFactory.make();

    /**
     * Mapper to get the summary data for a stream, or all streams.
     */
    private DomainMapper<UsageMetricStreamSummaryRequest, List<DailyUsageSummary>> summaryDataMapper;

    /**
     * Strategy to get the number of weekdays between two dates.
     */
    private WeekdaysInDateRangeStrategy weekdaysInDateRangeStrategy;

    /**
     * Constructor.
     * 
     * @param inSummaryDataMapper
     *            mapper to get the summary data for a stream, or all streams
     * @param inWeekdaysInDateRangeStrategy
     *            strategy to get the number of weekdays between two dates
     */
    public GetUsageMetricSummaryExecution(
            final DomainMapper<UsageMetricStreamSummaryRequest, List<DailyUsageSummary>> inSummaryDataMapper,
            final WeekdaysInDateRangeStrategy inWeekdaysInDateRangeStrategy)
    {
        summaryDataMapper = inSummaryDataMapper;
        weekdaysInDateRangeStrategy = inWeekdaysInDateRangeStrategy;
    }

    /**
     * Get the daily usage summary for all streams or for a specific stream.
     * 
     * @param inActionContext
     *            the action context containing the UsageMetricDailyStreamInfoRequest
     * @return the UsageMetricSummaryDTO
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext)
    {
        UsageMetricStreamSummaryRequest request = (UsageMetricStreamSummaryRequest) inActionContext.getParams();

        List<DailyUsageSummary> results = summaryDataMapper.execute(request);
        logger.info("Found " + results.size() + " summary results");

        UsageMetricSummaryDTO result = new UsageMetricSummaryDTO();

        // short-circuit if no results.
        if (results.size() == 0)
        {
            return result;
        }

        long msgCount = 0;
        long pageViewCount = 0;
        long streamContributorCount = 0;
        long streamViewCount = 0;
        long streamViewerCount = 0;
        long uniqueVisitorCount = 0;
        long avgActivityResponseTime = 0;

        Long totalStreamViewCount = null;
        Long totalActivityCount = null;
        Long totalCommentCount = null;
        Long totalContributorCount = null;

        Calendar day = Calendar.getInstance();
        day.add(Calendar.DATE, -request.getNumberOfDays());
        Date oldestAllowableReportDate = DateDayExtractor.getStartOfDay(new Date(day.getTimeInMillis()));

        day = Calendar.getInstance();
        day.add(Calendar.DATE, -1);
        Date latestReportDate = DateDayExtractor.getStartOfDay(new Date(day.getTimeInMillis()));

        Date summaryDate;
        Date oldestAvailableReportDate = null;
        Date newestAvailableReportDate = null;

        int recordCount = 0;
        logger.debug("Looking for data between " + oldestAllowableReportDate + " and " + latestReportDate);
        for (DailyUsageSummary dus : results)
        {
            summaryDate = DateDayExtractor.getStartOfDay(dus.getUsageDate());
            if (summaryDate.before(oldestAllowableReportDate) || summaryDate.after(latestReportDate))
            {
                // can't use this data
                logger.debug("Can't use data for " + summaryDate);
                continue;
            }

            if (newestAvailableReportDate == null || summaryDate.after(newestAvailableReportDate))
            {
                newestAvailableReportDate = summaryDate;

                // this is currently the most recent record - store the totals
                totalStreamViewCount = dus.getTotalStreamViewCount();
                totalActivityCount = dus.getTotalActivityCount();
                totalCommentCount = dus.getTotalCommentCount();
                totalContributorCount = dus.getTotalContributorCount();
            }

            recordCount++;
            if (oldestAvailableReportDate == null || summaryDate.before(oldestAvailableReportDate))
            {
                // this is the earliest reporting date we've seen
                oldestAvailableReportDate = summaryDate;
            }

            msgCount += dus.getMessageCount();
            pageViewCount += dus.getPageViewCount();
            streamContributorCount += dus.getStreamContributorCount();
            streamViewCount += dus.getStreamViewCount();
            streamViewerCount += dus.getStreamViewerCount();
            uniqueVisitorCount += dus.getUniqueVisitorCount();
            avgActivityResponseTime += dus.getAvgActivityResponseTime();
        }

        // number of weekdays between the two dates
        long weekdaysCount = 0;
        if (oldestAvailableReportDate != null)
        {
            weekdaysCount = weekdaysInDateRangeStrategy.getWeekdayCountBetweenDates(oldestAvailableReportDate,
                    DateDayExtractor.getStartOfDay(new Date()));
        }
        result.setWeekdayRecordCount(weekdaysCount);
        result.setTotalStreamViewCount(totalStreamViewCount);
        result.setTotalActivityCount(totalActivityCount);
        result.setTotalCommentCount(totalCommentCount);
        result.setTotalContributorCount(totalContributorCount);

        logger.debug("Found " + weekdaysCount + " weekdays between " + oldestAvailableReportDate + " and "
                + latestReportDate);
        if (weekdaysCount > 0)
        {
            result.setAverageDailyMessageCount(msgCount / weekdaysCount);
            result.setAverageDailyPageViewCount(pageViewCount / weekdaysCount);
            result.setAverageDailyStreamContributorCount(streamContributorCount / weekdaysCount);
            result.setAverageDailyStreamViewCount(streamViewCount / weekdaysCount);
            result.setAverageDailyStreamViewerCount(streamViewerCount / weekdaysCount);
            result.setAverageDailyUniqueVisitorCount(uniqueVisitorCount / weekdaysCount);
            result.setAverageDailyActivityResponseTime(avgActivityResponseTime / weekdaysCount);
        }
        return result;
    }
}
