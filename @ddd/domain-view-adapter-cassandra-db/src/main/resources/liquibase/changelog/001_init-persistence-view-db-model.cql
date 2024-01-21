USE ${key_space};

CREATE TABLE queryable_text
(
    aggregate_id TEXT,
    property_name TEXT,
    value         TEXT,
    domain_tag    TEXT,
    timestamp     TIMESTAMP,
    PRIMARY KEY (property_name, domain_tag, aggregate_id)
);

CREATE MATERIALIZED VIEW queryable_text_view AS
SELECT *
FROM queryable_text
WHERE property_name IS NOT NULL
  AND domain_tag IS NOT NULL
  AND value IS NOT NULL
  AND aggregate_id IS NOT NULL
PRIMARY KEY ((property_name, domain_tag, value), aggregate_id);

CREATE TABLE queryable_boolean
(
    aggregate_id TEXT,
    property_name TEXT,
    value         BOOLEAN,
    domain_tag    TEXT,
    timestamp     TIMESTAMP,
    PRIMARY KEY (property_name, domain_tag, aggregate_id)
);

CREATE MATERIALIZED VIEW queryable_boolean_view AS
SELECT *
FROM queryable_boolean
WHERE property_name IS NOT NULL
  AND domain_tag IS NOT NULL
  AND value IS NOT NULL
  AND aggregate_id IS NOT NULL
PRIMARY KEY ((property_name, domain_tag, value), aggregate_id);

CREATE TABLE queryable_timestamp
(
    aggregate_id TEXT,
    property_name TEXT,
    value         TIMESTAMP,
    domain_tag    TEXT,
    timestamp     TIMESTAMP,
    PRIMARY KEY (property_name, domain_tag, aggregate_id)
);

CREATE MATERIALIZED VIEW queryable_timestamp_view AS
SELECT *
FROM queryable_timestamp
WHERE property_name IS NOT NULL
  AND domain_tag IS NOT NULL
  AND value IS NOT NULL
  AND aggregate_id IS NOT NULL
PRIMARY KEY ((property_name, domain_tag, value), aggregate_id);

CREATE TABLE queryable_long
(
    aggregate_id TEXT,
    property_name TEXT,
    value         BIGINT,
    domain_tag    TEXT,
    timestamp     TIMESTAMP,
    PRIMARY KEY (property_name, domain_tag, aggregate_id)
);

CREATE MATERIALIZED VIEW queryable_long_view AS
SELECT *
FROM queryable_long
WHERE property_name IS NOT NULL
  AND domain_tag IS NOT NULL
  AND value IS NOT NULL
  AND aggregate_id IS NOT NULL
PRIMARY KEY ((property_name, domain_tag, value), aggregate_id);

CREATE TABLE queryable_double
(
    aggregate_id TEXT,
    property_name TEXT,
    value         DOUBLE,
    domain_tag    TEXT,
    timestamp     TIMESTAMP,
    PRIMARY KEY (property_name, domain_tag, aggregate_id)
);

CREATE MATERIALIZED VIEW queryable_double_view AS
SELECT *
FROM queryable_double
WHERE property_name IS NOT NULL
  AND domain_tag IS NOT NULL
  AND value IS NOT NULL
  AND aggregate_id IS NOT NULL
PRIMARY KEY ((property_name, domain_tag, value), aggregate_id);
 