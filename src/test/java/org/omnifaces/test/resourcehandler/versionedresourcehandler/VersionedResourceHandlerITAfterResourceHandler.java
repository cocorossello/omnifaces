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
package org.omnifaces.test.resourcehandler.versionedresourcehandler;

import static org.omnifaces.util.Utils.formatURLWithQueryString;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;

import org.omnifaces.resourcehandler.DefaultResourceHandler;
import org.omnifaces.resourcehandler.RemappedResource;

public class VersionedResourceHandlerITAfterResourceHandler extends DefaultResourceHandler {

	public VersionedResourceHandlerITAfterResourceHandler(ResourceHandler wrapped) {
		super(wrapped);
	}

	@Override
	public Resource decorateResource(Resource resource) {
		return new RemappedResource(resource, formatURLWithQueryString(resource.getRequestPath(), "after=true"));
	}
}