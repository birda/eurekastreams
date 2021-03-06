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
package org.eurekastreams.web.client.ui.common.notification;

import org.eurekastreams.web.client.events.NotificationCountsAvailableEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.model.NotificationCountModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.notification.dialog.NotificationsDialogContent;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;

/**
 * Widget displaying the notification count in the nav bar.
 */
public class NotificationCountWidget extends Composite
{
    /** Additional style for when there are unread notifications. */
    private static final String UNREAD_STYLE = "notif-count-unread";

    /** The label. */
    private final Label countLabel = new Label();

    /**
     * Constructor.
     */
    public NotificationCountWidget()
    {
        // -- UI setup --
        countLabel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().notifCount());
        initWidget(countLabel);

        // -- MVC setup --

        countLabel.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent inEvent)
            {
                showDialog();
            }
        });

        Session.getInstance().getEventBus()
                .addObserver(NotificationCountsAvailableEvent.class, new Observer<NotificationCountsAvailableEvent>()
                {
                    public void update(final NotificationCountsAvailableEvent ev)
                    {
                        int total = ev.getNormalCount() + ev.getHighPriorityCount();
                        if (total > 0)
                        {
                            String text = (ev.getHighPriorityCount() > 0 ? total + "!" : Integer.toString(total));
                            countLabel.setText(text);
                            countLabel.addStyleName(UNREAD_STYLE);
                        }
                        else
                        {
                            countLabel.setText("");
                            countLabel.removeStyleName(UNREAD_STYLE);
                        }
                    }
                });
    }

    /**
     * Init the data.
     */
    public void init()
    {
        Session.getInstance().getTimer()
                .addTimerJob("getNotificationCountTimerJob", 1, NotificationCountModel.getInstance(), null, true);

        NotificationCountModel.getInstance().fetch(null, true);
    }

    /**
     * Shows the notifications list dialog.
     */
    public void showDialog()
    {
        Dialog.showCentered(new NotificationsDialogContent());
    }
}
