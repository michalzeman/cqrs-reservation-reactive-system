FROM liquibase/liquibase:4.23.2

ENV URL='jdbc:cassandra://cassandra-db:9042/liquibase_keyspace;DefaultKeyspace=liquibase_keyspace'
ENV CHANGELOG_FILE=changelog.xml
ENV USERNAME=cassandra
ENV PASSWORD=cassandra
ENV DRIVER=com.simba.cassandra.jdbc42.Driver
ENV DEFAULT_SCHEMA_NAME=liquibase_keyspace

COPY ./lib /liquibase/lib

COPY entrypoint.sh /entrypoint.sh

USER root

RUN chmod +x /entrypoint.sh

USER liquibase

ENTRYPOINT ["/entrypoint.sh"]