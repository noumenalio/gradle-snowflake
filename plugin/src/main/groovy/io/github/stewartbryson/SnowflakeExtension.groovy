package io.github.stewartbryson

import be.vbgn.gradle.cidetect.CiInformation
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.ObjectUtils
import org.apache.commons.lang3.RandomStringUtils
import org.gradle.api.Project

/**
 * The plugin configuration extension that is applied to the Gradle project as 'snowflake'.
 */
@Slf4j
class SnowflakeExtension {

    private Project project
    private String projectName
    private CiInformation ci

    SnowflakeExtension(Project project) {
        this.project = project
        this.projectName = project.rootProject.name
        this.ci = CiInformation.detect(project)
        this.ephemeralName = snowflakeCloneName
    }

    /**
     * The Snowsql connection to use.
     */
    String connection

    /**
     * The Snowflake stage to upload to. Default: 'maven'.
     */
    String stage = 'maven'
    /**
     * Optional: specify the URL of {@link #stage} if it is external. The plugin will apply the 'maven-publish' plugin and handle publishing artifacts there.
     */
    String publishUrl
    /**
     * Optional: specify an artifact groupId when using the 'maven-publish' plugin.
     */
    String groupId = project.getGroup()
    /**
     * Optional: specify an artifactId when using the 'maven-publish' plugin.
     */
    String artifactId = project.getName()
    /**
     * Optional: do not automatically apply 'maven-publish' and allow the user to apply that plugin in the 'build.gradle' file.
     */
    Boolean useCustomMaven = false
    /**
     * Create and use an ephemeral Snowflake clone for the entire build. Useful for CI/CD processes. Default: false
     */
    Boolean useEphemeral = false
    /**
     * Don't drop the ephemeral Snowflake clone at the end of the build. Default: false
     */
    Boolean keepEphemeral = false
    /**
     * The name of the cloned Snowflake database. Default: auto-generated.
     *
     * Dynamically generated name for an ephemeral Snowflake clone to create. Uses CICD properties when they are available, and a simple unique name when they are not.
     */
    String ephemeralName

    /**
     * Return the name of the Maven publication task associated with the external stage.
     */
    String getPublishTask() {
        "publishSnowflakePublicationTo${stage.capitalize()}Repository"
    }

    /**
     * Return the database clone name based on GitHub actions when available.
     */
    String getSnowflakeCloneName() {
        // determine the base name for the clone
        String baseName = ObjectUtils.firstNonNull(ci.getPullRequest(), ci.reference, RandomStringUtils.randomAlphanumeric(9))
        // determine the reference type
        String refType = ci.isPullRequest() ? 'pr_' : (ci.isTag() ? 'tag_' : (ci.isCi() ? 'branch_' : ''))
        "ephemeral_${projectName.replace('-', '')}_${refType}${baseName}"
    }
}