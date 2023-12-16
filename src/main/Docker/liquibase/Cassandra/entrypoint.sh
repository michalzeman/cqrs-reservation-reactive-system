#!/bin/bash

# Initialize counter
counter=0

# Wait for the volume to be mounted or until 30 seconds have passed
while [ ! -f /liquibase/changelog/$CHANGELOG_FILE ] && [ $counter -lt 30 ]
do
  sleep 1
  ((counter++))
done

# Run Liquibase
liquibase --url=$URL --changeLogFile=changelog/$CHANGELOG_FILE --username=$USERNAME --password=$PASSWORD --driver=$DRIVER --defaultSchemaName=$DEFAULT_SCHEMA_NAME update