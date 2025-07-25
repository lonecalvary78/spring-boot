/*
 * Copyright 2002-present the original author or authors.
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
package org.springframework.boot

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.getBean
import org.springframework.boot.kotlinsample.TestKotlinApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.StandardEnvironment

/**
 * Tests for `SpringApplicationExtensions`.
 *
 * @author Sebastien Deleuze
 */
class SpringApplicationExtensionsTests {

	@Test
	fun `Kotlin runApplication() top level function`() {
		val context = runApplication<ExampleConfig>()
		assertThat(context).isNotNull()
	}

	@Test
	fun `Kotlin runApplication() top level function with a custom environment`() {
		val environment = StandardEnvironment()
		val context = runApplication<ExampleConfig> {
			setEnvironment(environment)
		}
		assertThat(context).isNotNull()
		assertThat(environment).isEqualTo(context.environment)
	}

	@Test
	fun `Kotlin runApplication(arg1, arg2) top level function`() {
		val context = runApplication<ExampleConfig>("--debug", "spring", "boot")
		val args = context.getBean<ApplicationArguments>()
		assertThat(args.nonOptionArgs.toTypedArray()).containsExactly("spring", "boot")
		assertThat(args.containsOption("debug")).isTrue()
	}

	@Test
	fun `Kotlin runApplication(arg1, arg2) top level function with a custom environment`() {
		val environment = StandardEnvironment()
		val context = runApplication<ExampleConfig>("--debug", "spring", "boot") {
			setEnvironment(environment)
		}
		val args = context.getBean<ApplicationArguments>()
		assertThat(args.nonOptionArgs.toTypedArray()).containsExactly("spring", "boot")
		assertThat(args.containsOption("debug")).isTrue()
		assertThat(environment).isEqualTo(context.environment)
	}

	@Test
	fun `Kotlin fromApplication() top level function`() {
		val context = fromApplication<TestKotlinApplication>().with(ExampleConfig::class).run().applicationContext
		assertThat(context.getBean<ExampleBean>()).isNotNull()
	}

	@Test
	fun `Kotlin fromApplication() top level function with multiple sources`() {
		val context = fromApplication<TestKotlinApplication>()
				.with(ExampleConfig::class, AnotherExampleConfig::class).run().applicationContext
		assertThat(context.getBean<ExampleBean>()).isNotNull()
		assertThat(context.getBean<AnotherExampleBean>()).isNotNull()
	}

	@Test
	fun `Kotlin fromApplication() top level function when no main`() {
		assertThatIllegalStateException().isThrownBy { fromApplication<ExampleConfig>().run() }
			.withMessage("Unable to use 'fromApplication' with " +
					"org.springframework.boot.SpringApplicationExtensionsTests.ExampleConfig")
	}

	@Configuration(proxyBeanMethods = false)
	internal open class ExampleConfig {

		@Bean
		open fun exampleBean(): ExampleBean {
			return ExampleBean()
		}

	}

	@Configuration(proxyBeanMethods = false)
	internal open class AnotherExampleConfig {

		@Bean
		open fun anotherExampleBean(): AnotherExampleBean {
			return AnotherExampleBean()
		}

	}

	class ExampleBean {

	}

	class AnotherExampleBean {

	}

}

