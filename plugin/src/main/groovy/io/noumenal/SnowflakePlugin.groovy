/*
 * This Groovy source file was generated by the Gradle 'init' task.
 */
package io.noumenal

import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.gradle.api.Plugin

@Slf4j
class SnowflakePlugin implements Plugin<Project> {
    private static String PLUGIN = 'snowflake'
    void apply(Project project) {
        project.extensions.create(PLUGIN, SnowflakeExtension)
        project.apply plugin: 'com.redpillanalytics.gradle-properties'
        project.apply plugin: 'maven-publish'
        // Register a task
        project.afterEvaluate {
            project.pluginProps.setParameters(project, PLUGIN)
            project.task("snowflakePublish", type: SnowflakePublish)
        }

    }
}
