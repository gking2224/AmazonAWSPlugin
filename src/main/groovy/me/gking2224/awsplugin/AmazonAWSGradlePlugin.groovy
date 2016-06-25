package me.gking2224.awsplugin

import org.gradle.api.Plugin
import org.gradle.api.Project


class AmazonAWSGradlePlugin implements Plugin<Project> {

	void apply(Project project) {
		project.extensions.create("awsplugin", AmazonAWSPluginExtension)
		project.task('hello') << {
			println "Hello from amazon-aws plugin"
		}
	}
}

class AmazonAWSPluginExtension {
}