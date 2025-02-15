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
package org.omnifaces.event;

import jakarta.faces.component.UICommand;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIForm;
import jakarta.faces.component.UIInput;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.event.ComponentSystemEvent;
import jakarta.faces.event.NamedEvent;
import jakarta.faces.event.PhaseId;

import org.omnifaces.eventlistener.InvokeActionEventListener;

/**
 * Use this event to have a hook on a listener method during the afterphase of the {@link PhaseId#INVOKE_APPLICATION}.
 * This event is supported on {@link UIViewRoot}, {@link UIForm}, {@link UIInput} and {@link UICommand} components.
 * <p>
 * This event is particularly helpful as a replacement of <code>&lt;f:event type="preRenderView"&gt;</code> and also
 * provides the possibility to invoke multiple action listeners on a single {@link UIInput} and {@link UICommand}
 * components on an easy manner.
 * <pre>
 * &lt;f:event type="postInvokeAction" listener="#{bean.postInvokeAction}" /&gt;
 * </pre>
 *
 * @author Bauke Scholtz
 * @see PreInvokeActionEvent
 * @see InvokeActionEventListener
 * @since 1.1
 */
@NamedEvent(shortName = "postInvokeAction")
public class PostInvokeActionEvent extends ComponentSystemEvent {

	// Constants ------------------------------------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// Constructors ---------------------------------------------------------------------------------------------------

	/**
	 * Construct a new post invoke action event on the given component.
	 * @param component The component to invoke the event on.
	 */
	public PostInvokeActionEvent(UIComponent component) {
		super(component);
	}

}