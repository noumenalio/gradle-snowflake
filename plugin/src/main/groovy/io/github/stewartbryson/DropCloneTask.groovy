package io.github.stewartbryson

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.TaskAction

/**
 * A Gradle task for dropping ephemeral testing environments.
 */
@Slf4j
abstract class DropCloneTask extends SnowflakeTask {

   /**
    * Constructor.
    */
   DropCloneTask() {
      description = "A Gradle task for dropping ephemeral testing environments in Snowflake."
      group = "verification"
   }

   /**
    * Drop the ephemeral Snowflake clone.
    */
   @TaskAction
   def dropClone() {
      // create the session
      createSession()
      // set the ephemeral name
      // we do not want to set the context
      snowflake.ephemeral = extension.ephemeralName
      snowflake.setOriginalContext()
      // drop the ephemeral database
      try {
         snowflake.ephemeral = extension.ephemeralName
         snowflake.session.jdbcConnection().createStatement().execute("drop database if exists ${snowflake.ephemeral}")
         snowflake.setOriginalContext()
      } catch (Exception e) {
         throw new Exception("Dropping ephemeral clone failed.", e)
      }
      log.warn "Ephemeral clone $snowflake.ephemeral dropped."
   }
}
