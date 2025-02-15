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
package org.omnifaces.cdi.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import org.omnifaces.config.FacesConfigXml;

/**
 * <p>
 * Producer for {@link FacesConfigXml}.
 *
 * @author Bauke Scholtz
 * @since 3.1
 */
@ApplicationScoped
public class FacesConfigXmlProducer {

	/**
	 * @return {@link FacesConfigXml#instance()}.
	 */
	@Produces
	public FacesConfigXml produce() {
		return FacesConfigXml.instance();
	}

}
