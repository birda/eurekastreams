/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.pages.search;

import org.eurekastreams.commons.formatting.DateFormatter;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.avatar.AvatarLinkPanel;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;
import org.eurekastreams.web.client.ui.common.pagedlist.ItemRenderer;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;
import org.eurekastreams.web.client.ui.pages.requestaccess.RequestAccessPanel;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * Renders a group.
 */
public class GroupListItemRenderer implements ItemRenderer<DomainGroupModelView>
{
    /**
     * Renders the group.
     *
     * @param group
     *            the group.
     * @return the group as a widget.
     */
    public FlowPanel render(final DomainGroupModelView group)
    {
        FlowPanel groupPanel = new FlowPanel();
        groupPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().group());
        groupPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().listItem());
        groupPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItem());

        groupPanel.add(new AvatarLinkPanel(EntityType.GROUP, group.getShortName(), group.getEntityId(), group
                .getAvatarId(), Size.Small));

        FlowPanel groupAbout = new FlowPanel();
        groupAbout.addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItemInfo());

        String url;

        url = Session.getInstance().generateUrl(new CreateUrlRequest(Page.GROUPS, group.getShortName()));
        Hyperlink groupNameLink = new Hyperlink(group.getName(), url);
        groupNameLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().displayName());
        groupAbout.add(groupNameLink);

        if (null != group.getDescription())
        {
            Label about = new Label(group.getDescription());
            about.addStyleName(StaticResourceBundle.INSTANCE.coreCss().missionStatement());
            groupAbout.add(about);
        }

        FlowPanel groupMetaData = new FlowPanel();
        groupMetaData.addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItemFollowers());

        groupMetaData.add(new HTML("Followers: <span class='" + StaticResourceBundle.INSTANCE.coreCss().light() + "'>"
                + group.getFollowersCount() + "</span>"));
        insertActionSeparator(groupMetaData);
        groupMetaData.add(new HTML("Added: <span class='" + StaticResourceBundle.INSTANCE.coreCss().light() + "'>"
                + new DateFormatter().timeAgo(group.getDateAdded(), true) + "</span>"));

        groupAbout.add(groupMetaData);

        if (!group.isPublic())
        {
            RequestAccessPanel reqAccess = new RequestAccessPanel(group.getShortName());
            groupAbout.add(reqAccess);
            groupNameLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().privateClass());
        }

        groupPanel.add(groupAbout);

        return groupPanel;
    }

    /**
     * Adds a separator (dot).
     *
     * @param panel
     *            Panel to put the separator in.
     */
    private void insertActionSeparator(final Panel panel)
    {
        Label sep = new InlineLabel("\u2219");
        sep.addStyleName(StaticResourceBundle.INSTANCE.coreCss().actionLinkSeparator());
        panel.add(sep);
    }
}
