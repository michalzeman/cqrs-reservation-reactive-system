create keyspace if not exists ddd_testing_keyspace with replication = {'class': 'org.apache.cassandra.locator.SimpleStrategy', 'replication_factor': '1'};

USE ddd_testing_keyspace;

CREATE TABLE event_journal
(
    aggregate_id text,
    sequence_nr  bigint,
    created_at   timestamp,
    tag          text,
    payload      blob,
    payload_type text,
    PRIMARY KEY (aggregate_id, sequence_nr)
);

CREATE TABLE snapshot_aggregate
(
    aggregate_id text,
    sequence_nr  bigint,
    created_at   timestamp,
    tag          text,
    payload      blob,
    payload_type text,
    PRIMARY KEY (aggregate_id, sequence_nr)
);