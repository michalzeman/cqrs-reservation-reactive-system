#!/bin/bash

KAFKA_TOPICS=("customer.event.customer-document" "reservation-system.event.reservation-document" "reservation-system.event.time-slot-document")

for TOPIC_NAME in "${KAFKA_TOPICS[@]}"
do
  kafka-topics --create --if-not-exists --bootstrap-server kafka:29092 --replication-factor 1 --partitions 6 --topic "$TOPIC_NAME"
  echo "Created topic $TOPIC_NAME if it did not exist"
done
