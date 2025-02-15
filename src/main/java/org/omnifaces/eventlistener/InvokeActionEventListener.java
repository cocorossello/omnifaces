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
package org.omnifaces.eventlistener;

import static org.omnifaces.util.Events.subscribeToApplicationEvent;
import static org.omnifaces.util.Utils.isEmpty;

import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.faces.component.UICommand;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIForm;
import jakarta.faces.component.UIInput;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.event.PostValidateEvent;
import jakarta.faces.event.SystemEvent;
import jakarta.faces.event.SystemEventListener;

import org.omnifaces.event.PostInvokeActionEvent;
import org.omnifaces.event.PreInvokeActionEvent;

/**
 * <p>
 * The {@link InvokeActionEventListener} will add support for new <code>&lt;f:event&gt;</code> types
 * <code>preInvokeAction</code> and <code>postInvokeAction</code>. Those events are published during the beforephase and
 * afterphase of <code>INVOKE_APPLICATION</code> respectively.
 * <p>
 * This event is supported on any {@link UIComponent}, including {@link UIViewRoot}, {@link UIForm}, {@link UIInput} and
 * {@link UICommand} components. This thus also provides the possibility to invoke multiple action listeners on a single
 * {@link UIInput} and {@link UICommand} component on an easy manner.
 * <p>
 * As this phase listener has totally no impact on a webapp's default behavior, this phase listener is already
 * registered by OmniFaces own <code>faces-config.xml</code> and thus gets auto-initialized when the OmniFaces JAR
 * is bundled in a webapp, so endusers do not need to register this phase listener explicitly themselves.
 *
 * @author Bauke Scholtz
 * @see PreInvokeActionEvent
 * @see PostInvokeActionEvent
 * @since 1.1
 */
public class InvokeActionEventListener extends DefaultPhaseListener implements SystemEventListener {

	// Constants ------------------------------------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// Constructors ---------------------------------------------------------------------------------------------------

	/**
	 * This constructor instructs the {@link DefaultPhaseListener} to hook on {@link PhaseId#INVOKE_APPLICATION} and
	 * subscribes this instance as a {@link SystemEventListener} to the {@link PostValidateEvent} event. This allows
	 * collecting the components eligible for {@link PreInvokeActionEvent} or {@link PostInvokeActionEvent} inside the
	 * {@link #processEvent(SystemEvent)} method.
	 */
	public InvokeActionEventListener() {
		super(PhaseId.INVOKE_APPLICATION);
		subscribeToApplicationEvent(PostValidateEvent.class, this);
	}

	// Actions --------------------------------------------------------------------------------------------------------

	/**
	 * Returns <code>true</code> only when the given source is an {@link UIComponent} which has listeners for
	 * {@link PreInvokeActionEvent} or {@link PostInvokeActionEvent}.
	 */
	@Override
	public boolean isListenerForSource(Object source) {
		if (!(source instanceof UIComponent)) {
			return false;
		}

		UIComponent component = (UIComponent) source;

		return !isEmpty(component.getListenersForEventClass(PreInvokeActionEvent.class))
			|| !isEmpty(component.getListenersForEventClass(PostInvokeActionEvent.class));
	}

	/**
	 * If the validation has not failed for the current faces context, then check if the {@link UIComponent} which
	 * passed the {@link #isListenerForSource(Object)} check has any listeners for the {@link PreInvokeActionEvent}
	 * and/or {@link PostInvokeActionEvent} events and then add them to a set in the current faces context.
	 */
	@Override
	public void processEvent(SystemEvent event) {
		FacesContext context = FacesContext.getCurrentInstance();

		if (!context.isValidationFailed()) {
			UIComponent component = (UIComponent) event.getSource();
			checkAndAddComponentWithListeners(context, component, PreInvokeActionEvent.class);
			checkAndAddComponentWithListeners(context, component, PostInvokeActionEvent.class);
		}
	}

	/**
	 * Publish the {@link PreInvokeActionEvent} event on the components which are been collected in
	 * {@link #processEvent(SystemEvent)}.
	 */
	@Override
	public void beforePhase(PhaseEvent event) {
		publishEvent(event.getFacesContext(), PreInvokeActionEvent.class);
	}

	/**
	 * Publish the {@link PostInvokeActionEvent} event on the components which are been collected in
	 * {@link #processEvent(SystemEvent)}.
	 */
	@Override
	public void afterPhase(PhaseEvent event) {
		publishEvent(event.getFacesContext(), PostInvokeActionEvent.class);
	}

	// Helpers --------------------------------------------------------------------------------------------------------

	/**
	 * If {@link UIComponent#getListenersForEventClass(Class)} returns a non-<code>null</code> and non-empty collection,
	 * then add the component to the set of components associated with the given event type.
	 * @param context The involved faces context.
	 * @param component The component to be checked.
	 * @param type The event type.
	 */
	@SuppressWarnings("unchecked") // For the cast on Set<UIComponent>.
	private static <T extends SystemEvent> void checkAndAddComponentWithListeners
		(FacesContext context, UIComponent component, Class<T> type)
	{
		if (!isEmpty(component.getListenersForEventClass(type))) {
			Set<UIComponent> components = (Set<UIComponent>) context.getAttributes().get(type);

			if (components == null) {
				components = new LinkedHashSet<>();
				context.getAttributes().put(type, components);
			}

			components.add(component);
		}
	}

	/**
	 * Obtain the set of components associated with the given event type and publish the event on each of them.
	 * @param context The involved faces context.
	 * @param type The event type.
	 */
	@SuppressWarnings("unchecked") // For the cast on Set<UIComponent>.
	private static <T extends SystemEvent> void publishEvent(FacesContext context, Class<T> type) {
		Set<UIComponent> components = (Set<UIComponent>) context.getAttributes().get(type);

		if (components != null) {
			for (UIComponent component : components) {
				context.getApplication().publishEvent(context, type, component);
			}
		}
	}

}