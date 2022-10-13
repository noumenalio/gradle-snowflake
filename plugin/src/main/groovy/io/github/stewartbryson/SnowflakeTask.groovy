package io.github.stewartbryson

import com.snowflake.snowpark_java.Session
import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import org.gradle.api.tasks.options.Option

/**
 * A Cacheable Gradle task for publishing Java-based applications as UDFs to Snowflake.
 */
@Slf4j
@CacheableTask
abstract class SnowflakeTask extends DefaultTask {

    @Internal
    String PLUGIN = 'snowflake'

    /**
     * A helper for getting the plugin extension.
     *
     * @return A reference to the plugin extension.
     */
    @Internal
    def getExtension() {
        project.extensions."$PLUGIN"
    }

    /**
     * The Snowflake account URL, for instance: https://gradle-snowflake.us-east-1.snowflakecomputing.com:443. Overrides {@link SnowflakeExtension#account}.
     */
    @Optional
    @Input
    @Option(option = "account",
            description = "Override the URL of the Snowflake account."
    )
    String account = extension.account

    /**
     * The Snowflake user to connect as. Overrides {@link SnowflakeExtension#user}.
     */
    @Optional
    @Input
    @Option(option = "user",
            description = "Override the Snowflake user to connect as."
    )
    String user = extension.user

    /**
     * The Snowflake password to connect with. Overrides {@link SnowflakeExtension#password}.
     */
    @Optional
    @Input
    @Option(option = "password",
            description = "Override the Snowflake password to connect with."
    )
    String password = extension.password

    /**
     * The Snowflake database to connect to. Overrides {@link SnowflakeExtension#database}.
     */
    @Optional
    @Input
    @Option(option = "database",
            description = "Override the Snowflake database to connect to."
    )
    String database = extension.database

    /**
     * The Snowflake schema to connect with. Overrides {@link SnowflakeExtension#schema}.
     */
    @Input
    @Option(option = "schema",
            description = "Override the Snowflake schema to connect with."
    )
    String schema = extension.schema

    /**
     * The Snowflake warehouse to use. Overrides {@link SnowflakeExtension#warehouse}.
     */
    @Input
    @Option(option = "warehouse",
            description = "Override the Snowflake warehouse to use."
    )
    String warehouse = extension.warehouse

    /**
     * The Snowflake role to connect with. Overrides {@link SnowflakeExtension#role}.
     */
    @Input
    @Option(option = "role",
            description = "Override the Snowflake role to connect with."
    )
    String role = extension.role

    /**
     * Create a Snowflake session.
     *
     * @return a Snowflake session.
     */
    @Internal
    Session getSession() {
        Map props = [
                url      : account,
                user     : user,
                password : password,
                role     : role,
                warehouse: warehouse,
                db       : database,
                schema   : schema
        ]
        Map printable = props.clone()
        printable.password = "*********"
        log.info "Snowflake config: $printable"

        Session session
        // get a Snowflake session
        try {
            session = Session.builder().configs(props).create()
        } catch (NullPointerException npe) {
            throw new Exception("Snowflake connection details are missing.", npe)
        }
        return session
    }
}
