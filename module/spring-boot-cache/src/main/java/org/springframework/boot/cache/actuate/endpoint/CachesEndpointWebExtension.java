/*
 * Copyright 2012-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.cache.actuate.endpoint;

import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;
import org.springframework.boot.actuate.endpoint.annotation.OptionalParameter;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.boot.actuate.endpoint.web.annotation.EndpointWebExtension;
import org.springframework.boot.cache.actuate.endpoint.CachesEndpoint.CacheEntryDescriptor;

/**
 * {@link EndpointWebExtension @EndpointWebExtension} for the {@link CachesEndpoint}.
 *
 * @author Stephane Nicoll
 * @since 4.0.0
 */
@EndpointWebExtension(endpoint = CachesEndpoint.class)
public class CachesEndpointWebExtension {

	private final CachesEndpoint delegate;

	public CachesEndpointWebExtension(CachesEndpoint delegate) {
		this.delegate = delegate;
	}

	@ReadOperation
	public WebEndpointResponse<CacheEntryDescriptor> cache(@Selector String cache,
			@OptionalParameter String cacheManager) {
		try {
			CacheEntryDescriptor entry = this.delegate.cache(cache, cacheManager);
			int status = (entry != null) ? WebEndpointResponse.STATUS_OK : WebEndpointResponse.STATUS_NOT_FOUND;
			return new WebEndpointResponse<>(entry, status);
		}
		catch (NonUniqueCacheException ex) {
			return new WebEndpointResponse<>(WebEndpointResponse.STATUS_BAD_REQUEST);
		}
	}

	@DeleteOperation
	public WebEndpointResponse<Void> clearCache(@Selector String cache, @OptionalParameter String cacheManager) {
		try {
			boolean cleared = this.delegate.clearCache(cache, cacheManager);
			int status = (cleared ? WebEndpointResponse.STATUS_NO_CONTENT : WebEndpointResponse.STATUS_NOT_FOUND);
			return new WebEndpointResponse<>(status);
		}
		catch (NonUniqueCacheException ex) {
			return new WebEndpointResponse<>(WebEndpointResponse.STATUS_BAD_REQUEST);
		}
	}

}
