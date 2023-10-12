create keyspace if not exists customer_keyspace with replication = {'class': 'org.apache.cassandra.locator.SimpleStrategy', 'replication_factor': '1'};
