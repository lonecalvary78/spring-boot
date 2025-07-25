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

package org.springframework.boot.testcontainers.context;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.testcontainers.containers.Container;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.testcontainers.properties.TestcontainersPropertySourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

/**
 * Imports idiomatic Testcontainers declaration classes into the Spring
 * {@link ApplicationContext}. The following elements will be considered from the imported
 * classes:
 * <ul>
 * <li>All static fields that declare {@link Container} values.</li>
 * <li>All {@code @DynamicPropertySource} annotated methods.</li>
 * </ul>
 *
 * @author Phillip Webb
 * @since 3.1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ImportTestcontainersRegistrar.class)
@ImportAutoConfiguration(TestcontainersPropertySourceAutoConfiguration.class)
public @interface ImportTestcontainers {

	/**
	 * The declaration classes to import. If no {@code value} is defined then the class
	 * that declares the {@link ImportTestcontainers @ImportTestcontainers} annotation
	 * will be searched.
	 * @return the definition classes to import
	 */
	Class<?>[] value() default {};

}
