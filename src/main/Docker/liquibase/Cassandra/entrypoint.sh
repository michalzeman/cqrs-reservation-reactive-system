#!/bin/bash

# Wait for the volume to be mounted
while [ ! -f /liquibase/changelog/$CHANGELOG_FILE ]
do
  sleep 1
done

# Run Liquibase
liquibase --url=$URL --changeLogFile=changelog/$CHANGELOG_FILE --username=$USERNAME --password=$PASSWORD --driver=$DRIVER --defaultSchemaName=$DEFAULT_SCHEMA_NAME update