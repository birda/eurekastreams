/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.commons.messaging;

import java.io.Serializable;

import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.commons.task.TaskExecutor;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import javax.jms.Message;
import javax.jms.ObjectMessage;


/**
 * Test for MessageConsumer class.
 *
 */
public class TaskExecutorTest 
{
    /**
     * Context for building mock objects. 
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    }; 
    /**
     * Mock objects.
     */
    
    /**
     * The message mock object.
     */
    private Message messageMock = context.mock(ObjectMessage.class);
        
    /**
     * The real time executer mock.
     */
    private TaskExecutor taskExecuterMock = context.mock(TaskExecutor.class);
    
    /**
     * The user aciton request mock object.
     */
    private UserActionRequest userActionRequestMock = context.mock(UserActionRequest.class);
    
    /**
     * SUT.
     */
    private AsyncActionProcessorMDB sut = null;
    
    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut = new AsyncActionProcessorMDB();
        sut.setTaskExecutor(taskExecuterMock);
    }
    
    /**
     * Test onMessage(message).
     * @throws Exception  not expected
     */
	@Test
    public void testOnMessage() throws Exception
    {
    	final Serializable param1 = null;
    	final Serializable[] params = new Serializable[1];
    	params[0] = param1;
    	
//    	final UserDetails userDetails = null;
    	final String actionKey = "TestAction";
    	
        context.checking(new Expectations()
        {
            {
            	oneOf((ObjectMessage) messageMock).getObject();
            	will(returnValue(userActionRequestMock));
            	
            	oneOf(userActionRequestMock).getActionKey();
            	will(returnValue(actionKey));
            	            	
            	oneOf(taskExecuterMock).execute(userActionRequestMock);
            }
        });
        
        sut.onMessage(messageMock);
        context.assertIsSatisfied();
    }    
    
}
