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

package org.springframework.boot.gradle.tasks.bundling;

import java.io.File;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.gradle.api.Project;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;

import org.springframework.util.StringUtils;

/**
 * Encapsulates the configuration of the launch script for an executable jar or war.
 *
 * @author Andy Wilkinson
 * @since 2.0.0
 */
@SuppressWarnings("serial")
public class LaunchScriptConfiguration implements Serializable {

	private static final Pattern WHITE_SPACE_PATTERN = Pattern.compile("\\s+");

	private static final Pattern LINE_FEED_PATTERN = Pattern.compile("\n");

	// We don't care about the order, but Gradle's configuration cache currently does.
	// https://github.com/gradle/gradle/pull/17863
	private final Map<String, String> properties = new TreeMap<>();

	private File script;

	public LaunchScriptConfiguration() {
	}

	LaunchScriptConfiguration(AbstractArchiveTask archiveTask) {
		Project project = archiveTask.getProject();
		String baseName = archiveTask.getArchiveBaseName().get();
		putIfMissing(this.properties, "initInfoProvides", baseName);
		putIfMissing(this.properties, "initInfoShortDescription", removeLineBreaks(project.getDescription()), baseName);
		putIfMissing(this.properties, "initInfoDescription", augmentLineBreaks(project.getDescription()), baseName);
	}

	/**
	 * Returns the properties that are applied to the launch script when it's being
	 * including in the executable archive.
	 * @return the properties
	 */
	@Input
	public Map<String, String> getProperties() {
		return this.properties;
	}

	/**
	 * Sets the properties that are applied to the launch script when it's being including
	 * in the executable archive.
	 * @param properties the properties
	 */
	public void properties(Map<String, String> properties) {
		this.properties.putAll(properties);
	}

	/**
	 * Returns the script {@link File} that will be included in the executable archive.
	 * When {@code null}, the default launch script will be used.
	 * @return the script file
	 */
	@Optional
	@InputFile
	@PathSensitive(PathSensitivity.RELATIVE)
	public File getScript() {
		return this.script;
	}

	/**
	 * Sets the script {@link File} that will be included in the executable archive. When
	 * {@code null}, the default launch script will be used.
	 * @param script the script file
	 */
	public void setScript(File script) {
		this.script = script;
	}

	private String removeLineBreaks(String string) {
		return (string != null) ? WHITE_SPACE_PATTERN.matcher(string).replaceAll(" ") : null;
	}

	private String augmentLineBreaks(String string) {
		return (string != null) ? LINE_FEED_PATTERN.matcher(string).replaceAll("\n#  ") : null;
	}

	private void putIfMissing(Map<String, String> properties, String key, String... valueCandidates) {
		if (!properties.containsKey(key)) {
			for (String candidate : valueCandidates) {
				if (StringUtils.hasLength(candidate)) {
					properties.put(key, candidate);
					return;
				}
			}
		}
	}

}
