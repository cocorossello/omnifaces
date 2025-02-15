/*
 * Copyright OmniFaces
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.omnifaces.cdi.push;

import static org.omnifaces.util.Beans.getQualifier;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;

import org.omnifaces.cdi.Push;
import org.omnifaces.cdi.PushContext;

/**
 * <p>
 * This producer prepares the {@link SocketPushContext} instance for injection by <code>&#64;</code>{@link Push}.
 *
 * @author Bauke Scholtz
 * @see Push
 * @since 2.3
 */
@Dependent
public class SocketPushContextProducer {

	// Variables ------------------------------------------------------------------------------------------------------

	@SuppressWarnings("unused") // Workaround for OpenWebBeans not properly passing it as produce() method argument.
	@Inject
	private InjectionPoint injectionPoint;

	@Inject
	private SocketChannelManager socketChannels;

	@Inject
	private SocketSessionManager socketSessions;

	@Inject
	private SocketUserManager socketUsers;

	// Actions --------------------------------------------------------------------------------------------------------

	/**
	 * Returns {@link PushContext} associated with channel name derived from given injection point.
	 * @param injectionPoint Injection point to derive channel name from.
	 * @return {@link PushContext} associated with channel name derived from given injection point.
	 */
	@Produces
	@Push
	public PushContext produce(InjectionPoint injectionPoint) {
		Push push = getQualifier(injectionPoint, Push.class);
		String channel = push.channel().isEmpty() ? injectionPoint.getMember().getName() : push.channel();
		return new SocketPushContext(channel, socketChannels, socketSessions, socketUsers);
	}

}