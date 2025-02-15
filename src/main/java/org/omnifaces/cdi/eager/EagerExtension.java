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
package org.omnifaces.cdi.eager;

import static org.omnifaces.util.Beans.getReference;
import static org.omnifaces.util.BeansLocal.getAnnotation;
import static org.omnifaces.util.BeansLocal.getReference;

import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.AfterDeploymentValidation;
import jakarta.enterprise.inject.spi.Annotated;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.ProcessBean;
import jakarta.faces.view.ViewScoped;

import org.omnifaces.cdi.Eager;
import org.omnifaces.cdi.eager.EagerBeansRepository.EagerBeans;

/**
 * CDI extension that collects beans annotated with <code>&#64;</code>{@link Eager}. After deployment
 * collected beans are transferred to the {@link EagerBeansRepository}.
 *
 * @author Arjan Tijms
 * @since 1.8
 *
 */
public class EagerExtension implements Extension {

	private static final Logger logger = Logger.getLogger(EagerExtension.class.getName());

	// Private constants ----------------------------------------------------------------------------------------------

	private static final String ERROR_EAGER_UNAVAILABLE =
		"@Eager is unavailable. The EagerBeansRepository could not be obtained from CDI bean manager.";

	// Variables ------------------------------------------------------------------------------------------------------

	private EagerBeansRepository.EagerBeans eagerBeans = new EagerBeansRepository.EagerBeans();

	// Actions --------------------------------------------------------------------------------------------------------

	/**
	 * Collect beans annotated with {@link Eager} into {@link EagerBeans}.
	 * @param <T> The generic bean type.
	 * @param event The process bean event.
	 * @param beanManager The involved bean manager.
	 */
	public <T> void collect(@Observes ProcessBean<T> event, BeanManager beanManager) {

		Annotated annotated = event.getAnnotated();
		Eager eager = getAnnotation(beanManager, annotated, Eager.class);

		if (eager != null) {

			Bean<?> bean = event.getBean();

			if (getAnnotation(beanManager, annotated, ApplicationScoped.class) != null) {
				eagerBeans.addApplicationScoped(bean);
			}
			else if (getAnnotation(beanManager, annotated, SessionScoped.class) != null) {
				eagerBeans.addSessionScoped(bean);
			}
			else if (getAnnotation(beanManager, annotated, ViewScoped.class) != null || getAnnotation(beanManager, annotated, org.omnifaces.cdi.ViewScoped.class) != null) {
				eagerBeans.addByViewId(bean, eager.viewId());
			}
			else if (getAnnotation(beanManager, annotated, RequestScoped.class) != null) {
				eagerBeans.addByRequestURIOrViewId(bean, eager.requestURI(), eager.viewId());
			}
		}
	}

	/**
	 * Load collected beans annotated with {@link Eager} into {@link EagerBeansRepository} via
	 * {@link EagerBeansRepository#setEagerBeans(org.omnifaces.cdi.eager.EagerBeansRepository.EagerBeans)}.
	 * @param event The after deployment validation event.
	 * @param beanManager The involved bean manager.
	 */
	public void load(@Observes AfterDeploymentValidation event, BeanManager beanManager) {

		if (eagerBeans.isEmpty()) {
			return;
		}

		EagerBeansRepository eagerBeansRepository = getReference(beanManager, EagerBeansRepository.class);

		if (eagerBeansRepository == null) {
			eagerBeansRepository = getReference(EagerBeansRepository.class); // #744: Work around for Payara. Apparently its beanManager instance has become "stale" somehow?

			if (eagerBeansRepository == null) {
				logger.warning(ERROR_EAGER_UNAVAILABLE);
				return;
			}
		}

		eagerBeansRepository.setEagerBeans(eagerBeans);
	}

}