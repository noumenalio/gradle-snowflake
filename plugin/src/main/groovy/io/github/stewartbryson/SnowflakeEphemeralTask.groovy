package io.github.stewartbryson

import com.snowflake.snowpark_java.Session
import groovy.util.logging.Slf4j
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.options.Option

/**
 * A Cacheable Gradle task for publishing Java-based applications as UDFs to Snowflake.
 */
@Slf4j
@CacheableTask
abstract class SnowflakeEphemeralTask extends SnowflakeTask {

    /**
     * When enabled, run using an ephemeral Snowflake database cloned from {@link #database}. Useful with CI/CD testing workflows.
     */
    @Optional
    @Input
    @Option(option = "use-ephemeral", description = "When enabled, run using an ephemeral Snowflake database clone.")
    Boolean useEphemeral = extension.useEphemeral

    /**
     * Optional: specify the ephemeral database name instead of relying on an autogenerated value. The plugin detects CI/CD environments and uses things like PR numbers, branch names, etc. when available.
     */
    @Optional
    @Input
    @Option(option = "ephemeral-name", description = "Optional: specify the ephemeral database name instead of relying on an autogenerated value.")
    String ephemeralName = extension.ephemeralName

    /**
     * Create an ephemeral Snowflake clone and return a session to it.
     *
     * @return a session to the ephemeral Snowflake clone.
     */
    Session createClone() {
        if (useEphemeral) {
            session.jdbcConnection().createStatement().execute("create or replace database ${ephemeralName} clone $database")
            session.jdbcConnection().createStatement().execute("grant ownership on database ${ephemeralName} to $role")
            log.warn "Ephemeral clone $ephemeralName created."
        }
        return useEphemeral ? createSession(ephemeralName) : session
    }

    /**
     * Drop the ephemeral Snowflake clone and return a session to it.
     */
    def dropClone(Session session) {
        if (useEphemeral) {
            // close the ephemeral session
            session?.close()
            // drop the ephemeral database
            this.session.jdbcConnection().createStatement().execute("drop database if exists ${extension.ephemeralName}")
            log.warn "Ephemeral clone $ephemeralName dropped."
        }
    }
}