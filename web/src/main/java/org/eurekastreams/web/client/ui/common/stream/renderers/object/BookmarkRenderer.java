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
package org.eurekastreams.web.client.ui.common.stream.renderers.object;

import java.util.HashMap;

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.ui.common.stream.transformers.HashtagLinkTransformer;
import org.eurekastreams.web.client.ui.common.stream.transformers.HyperlinkTransformer;
import org.eurekastreams.web.client.ui.common.stream.transformers.StreamSearchLinkBuilder;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Renders a bookmark object.
 *
 */
public class BookmarkRenderer implements ObjectRenderer
{
    /**
     * Renders the bookmark attachment.
     *
     * @param activity
     *            the activity.
     * @return the widget.
     */
    public Widget getAttachmentWidget(final ActivityDTO activity)
    {
        HashMap<String, String> props = activity.getBaseObjectProperties();
        String thumbnailUrl = props.get("thumbnail");
        String targetUrl = props.get("targetUrl");
        String sourceUrl = props.get("source");
        if (sourceUrl == null)
        {
            sourceUrl = getBaseUrl(targetUrl);
        }

        FlowPanel main = new FlowPanel();
        main.addStyleName(StaticResourceBundle.INSTANCE.coreCss().messageLink());
        if (thumbnailUrl != null && !thumbnailUrl.isEmpty())
        {
            main.addStyleName(StaticResourceBundle.INSTANCE.coreCss().hasThumbnail());

            Image image = new Image(thumbnailUrl);
            image.addStyleName(StaticResourceBundle.INSTANCE.coreCss().thumbnail());
            main.add(image);
        }

        Anchor primaryLink = new Anchor(props.get("targetTitle"), props.get("targetUrl"), "_blank");
        primaryLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().title());
        main.add(primaryLink);

        FlowPanel sourcePanel = new FlowPanel();
        sourcePanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().url());
        sourcePanel.add(new InlineLabel("source: "));
        sourcePanel.add(new Anchor(sourceUrl, sourceUrl, "_blank"));
        main.add(sourcePanel);

        Label meta = new Label(props.get("description"));
        meta.addStyleName(StaticResourceBundle.INSTANCE.coreCss().metaDescription());
        main.add(meta);

        return main;
    }

    // public Widget OLDgetAttachmentWidget(final ActivityDTO activity)
    // {
    // String html = "<div class='" + StaticResourceBundle.INSTANCE.coreCss().messageLink() + " "
    // + StaticResourceBundle.INSTANCE.coreCss().hasThumbnail() + "'><div>";
    // String targetUrl = activity.getBaseObjectProperties().get("targetUrl");
    // String sourceUrl = activity.getBaseObjectProperties().get("source");
    // if (sourceUrl == null)
    // {
    // sourceUrl = getBaseUrl(targetUrl);
    // }
    //
    // if (activity.getBaseObjectProperties().get("thumbnail") != null
    // && activity.getBaseObjectProperties().get("thumbnail").length() > 0)
    // {
    // html += "<img class='" + StaticResourceBundle.INSTANCE.coreCss().thumbnail() + "' src='"
    // + activity.getBaseObjectProperties().get("thumbnail") + "'></div>";
    // }
    //
    // html += "<div><a class=\"" + StaticResourceBundle.INSTANCE.coreCss().title() + "\" href=\""
    // + activity.getBaseObjectProperties().get("targetUrl") + "\" target=\"_blank\">"
    // + activity.getBaseObjectProperties().get("targetTitle") + "</a>"
    // + "</div><div class='url'>source: <a href=\"" + sourceUrl + "\" target=\"_blank\">" + sourceUrl
    // + "</a></div>" + "<div class='gwt-Label " + StaticResourceBundle.INSTANCE.coreCss().metaDescription()
    // + "'>" + activity.getBaseObjectProperties().get("description") + "</div></div>";
    //
    // return new HTML(html);
    // }

    /**
     * Gets the base URL (host name and protocol) from a url.
     *
     * @param url
     *            the url.
     * @return the base url.
     */
    private static native String getBaseUrl(final String url)
    /*-{
        try {
            var re = new RegExp('^([a-z]+://[^/]+)', 'im');
            return url.match(re)[1].toString();
        } catch(e)
        {
            return url;
        }
    }-*/;

    /**
     * Renders the content widget.
     *
     * @param activity
     *            the activity.
     * @return the widget.
     */
    public Widget getContentWidget(final ActivityDTO activity)
    {
        String activityContent = activity.getBaseObjectProperties().get("content");
        if (activityContent == null || activityContent.trim().isEmpty())
        {
            return null;
        }

        // first transform links to hyperlinks
        String html = new HyperlinkTransformer(new WidgetJSNIFacadeImpl()).transform(activityContent);

        // then transform hashtags to hyperlinks
        html = new HashtagLinkTransformer(new StreamSearchLinkBuilder()).transform(html);

        HTML widget = new HTML(html);
        widget.addStyleName(StaticResourceBundle.INSTANCE.coreCss().messageBody());

        return widget;
    }

}
