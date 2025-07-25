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

package org.springframework.boot.zipkin.testcontainers;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.testsupport.container.TestImage;
import org.springframework.boot.testsupport.container.ZipkinContainer;
import org.springframework.boot.zipkin.autoconfigure.ZipkinAutoConfiguration;
import org.springframework.boot.zipkin.autoconfigure.ZipkinConnectionDetails;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ZipkinContainerConnectionDetailsFactory}.
 *
 * @author Eddú Meléndez
 * @author Moritz Halbritter
 */
@SpringJUnitConfig
@Testcontainers(disabledWithoutDocker = true)
class ZipkinContainerConnectionDetailsFactoryIntegrationTests {

	@Container
	@ServiceConnection
	static final GenericContainer<?> zipkin = TestImage.container(ZipkinContainer.class);

	@Autowired(required = false)
	private ZipkinConnectionDetails connectionDetails;

	@Test
	void connectionCanBeMadeToZipkinContainer() {
		assertThat(this.connectionDetails).isNotNull();
		assertThat(this.connectionDetails.getSpanEndpoint())
			.startsWith("http://" + zipkin.getHost() + ":" + zipkin.getMappedPort(9411));
	}

	@Configuration(proxyBeanMethods = false)
	@ImportAutoConfiguration(ZipkinAutoConfiguration.class)
	static class TestConfiguration {

	}

}
