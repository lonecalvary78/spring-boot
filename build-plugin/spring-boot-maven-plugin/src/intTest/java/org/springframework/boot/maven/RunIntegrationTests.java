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

package org.springframework.boot.maven;

import java.io.File;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.contentOf;

/**
 * Integration tests for the Maven plugin's run goal.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
@ExtendWith(MavenBuildExtension.class)
class RunIntegrationTests {

	@TestTemplate
	void whenTheRunGoalIsExecutedTheApplicationIsForkedWithOptimizedJvmArguments(MavenBuild mavenBuild) {
		mavenBuild.project("run").goals("spring-boot:run", "-X").execute((project) -> {
			String jvmArguments = "JVM argument: -XX:TieredStopAtLevel=1";
			assertThat(buildLog(project)).contains("I haz been run").contains(jvmArguments);
		});
	}

	@TestTemplate
	void whenEnvironmentVariablesAreConfiguredTheyAreAvailableToTheApplication(MavenBuild mavenBuild) {
		mavenBuild.project("run-envargs")
			.goals("spring-boot:run")
			.execute((project) -> assertThat(buildLog(project)).contains("I haz been run"));
	}

	@TestTemplate
	void whenExclusionsAreConfiguredExcludedDependenciesDoNotAppearOnTheClasspath(MavenBuild mavenBuild) {
		mavenBuild.project("run-exclude")
			.goals("spring-boot:run")
			.execute((project) -> assertThat(buildLog(project)).contains("I haz been run"));
	}

	@TestTemplate
	void whenSystemPropertiesAndJvmArgumentsAreConfiguredTheyAreAvailableToTheApplication(MavenBuild mavenBuild) {
		mavenBuild.project("run-jvm-system-props")
			.goals("spring-boot:run")
			.execute((project) -> assertThat(buildLog(project)).contains("I haz been run"));
	}

	@TestTemplate
	void whenJvmArgumentsAreConfiguredTheyAreAvailableToTheApplication(MavenBuild mavenBuild) {
		mavenBuild.project("run-jvmargs")
			.goals("spring-boot:run")
			.execute((project) -> assertThat(buildLog(project)).contains("I haz been run"));
	}

	@TestTemplate
	void whenCommandLineSpecifiesJvmArgumentsTheyAreAvailableToTheApplication(MavenBuild mavenBuild) {
		mavenBuild.project("run-jvmargs-commandline")
			.goals("spring-boot:run")
			.systemProperty("spring-boot.run.jvmArguments", "-Dfoo=value-from-cmd")
			.execute((project) -> assertThat(buildLog(project)).contains("I haz been run"));
	}

	@TestTemplate
	void whenPomAndCommandLineSpecifyJvmArgumentsThenPomOverrides(MavenBuild mavenBuild) {
		mavenBuild.project("run-jvmargs")
			.goals("spring-boot:run")
			.systemProperty("spring-boot.run.jvmArguments", "-Dfoo=value-from-cmd")
			.execute((project) -> assertThat(buildLog(project)).contains("I haz been run"));
	}

	@TestTemplate
	void whenProfilesAreConfiguredTheyArePassedToTheApplication(MavenBuild mavenBuild) {
		mavenBuild.project("run-profiles")
			.goals("spring-boot:run", "-X")
			.execute((project) -> assertThat(buildLog(project)).contains("I haz been run with profile(s) 'foo,bar'"));
	}

	@TestTemplate
	void whenUseTestClasspathIsEnabledTheApplicationHasTestDependenciesOnItsClasspath(MavenBuild mavenBuild) {
		mavenBuild.project("run-use-test-classpath")
			.goals("spring-boot:run")
			.execute((project) -> assertThat(buildLog(project)).contains("I haz been run"));
	}

	@TestTemplate
	void whenAWorkingDirectoryIsConfiguredTheApplicationIsRunFromThatDirectory(MavenBuild mavenBuild) {
		mavenBuild.project("run-working-directory")
			.goals("spring-boot:run")
			.execute((project) -> assertThat(buildLog(project)).containsPattern("I haz been run from.*src.main.java"));
	}

	@TestTemplate
	void whenAdditionalClasspathDirectoryIsConfiguredItsResourcesAreAvailableToTheApplication(MavenBuild mavenBuild) {
		mavenBuild.project("run-additional-classpath-directory")
			.goals("spring-boot:run")
			.execute((project) -> assertThat(buildLog(project)).contains("I haz been run"));
	}

	@TestTemplate
	void whenAdditionalClasspathFileIsConfiguredItsContentIsAvailableToTheApplication(MavenBuild mavenBuild) {
		mavenBuild.project("run-additional-classpath-jar")
			.goals("spring-boot:run")
			.execute((project) -> assertThat(buildLog(project)).contains("I haz been run"));
	}

	@TestTemplate
	@DisabledOnOs(OS.WINDOWS)
	void whenAToolchainIsConfiguredItIsUsedToRunTheApplication(MavenBuild mavenBuild) {
		mavenBuild.project("run-toolchains")
			.goals("verify", "-t", "toolchains.xml")
			.execute((project) -> assertThat(buildLog(project)).contains("The Maven Toolchains is awesome!"));
	}

	@TestTemplate
	void whenPomSpecifiesRunArgumentsContainingCommasTheyArePassedToTheApplicationCorrectly(MavenBuild mavenBuild) {
		mavenBuild.project("run-arguments")
			.goals("spring-boot:run")
			.execute((project) -> assertThat(buildLog(project))
				.contains("I haz been run with profile(s) 'foo,bar' and endpoint(s) 'prometheus,info'"));
	}

	@TestTemplate
	void whenCommandLineSpecifiesRunArgumentsContainingCommasTheyArePassedToTheApplicationCorrectly(
			MavenBuild mavenBuild) {
		mavenBuild.project("run-arguments-commandline")
			.goals("spring-boot:run")
			.systemProperty("spring-boot.run.arguments",
					"--management.endpoints.web.exposure.include=prometheus,info,health,metrics --spring.profiles.active=foo,bar")
			.execute((project) -> assertThat(buildLog(project))
				.contains("I haz been run with profile(s) 'foo,bar' and endpoint(s) 'prometheus,info,health,metrics'"));
	}

	@TestTemplate
	void whenPomAndCommandLineSpecifyRunArgumentsThenPomOverrides(MavenBuild mavenBuild) {
		mavenBuild.project("run-arguments")
			.goals("spring-boot:run")
			.systemProperty("spring-boot.run.arguments",
					"--management.endpoints.web.exposure.include=one,two,three --spring.profiles.active=test")
			.execute((project) -> assertThat(buildLog(project))
				.contains("I haz been run with profile(s) 'foo,bar' and endpoint(s) 'prometheus,info'"));
	}

	private String buildLog(File project) {
		return contentOf(new File(project, "target/build.log"));
	}

}
