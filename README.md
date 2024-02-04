Copyright(2024) Michal Zeman, zeman.michal@yahoo.com

Licensed under the Creative Commons Attribution (CC BY) license. You are free to share, copy, distribute, 
and adapt this work, provided you give appropriate credit to the original author Michal Zeman, zeman.michal@yahoo.com.

To view a copy of the license, visit https://creativecommons.org/licenses/by/4.0/


# cqrs-reservation-reactive-system

This project is a reactive microservices system built using Kotlin, Spring Boot, and Gradle. It leverages Apache Kafka
for event-driven communication and Apache Cassandra for data persistence. The entire system is containerized using
Docker.

The system follows several architectural design patterns:

- Command Query Responsibility Segregation (CQRS)
- Event Sourcing
- Event-Driven Architecture
- Domain-Driven Design (DDD)
- Reactive Microservices
- Hexagonal Architecture

The microservices in the system are designed following the Hexagonal Architecture.

The interaction between the Customer MS and the Reservation MS is primarily event-driven, with domain
events being published into Kafka topics. This approach ensures loose coupling between the services and promotes
scalability and resilience.

## Basic Interaction

1. **Customer Registration**: A `CustomerRegistered` event is published to a Kafka topic when a new customer is
   registered.

2. **Reservation Request**: A `CustomerReservationRequested` event is published to a Kafka topic when a customer makes a
   reservation request. The Reservation MS initiates the reservation process upon receiving the event.

3. **Reservation Confirmation or Declination**: The Reservation MS either confirms or declines the reservation based on
   the availability of the requested time slot. It then publishes a `ReservationAccepted` or `ReservationDeclined` event
   to a Kafka topic. The Customer MS updates the customer's reservation status accordingly.

4. **Time Slot Management**: The Reservation MS manages time slots. When a time slot is booked, updated, or created,
   corresponding events (`TimeSlotBooked`, `TimeSlotUpdated`, `TimeSlotCreated`) are published to Kafka topics.

There is no direct communication between the Customer MS and the Reservation MS via REST APIs. All interactions are
asynchronous and event-driven, which allows each service to operate independently and handle its own failures.

## Customer Microservice

The Customer Microservice is designed to handle all operations related to customers. The microservice follows the
principles of Domain-Driven Design (DDD) and Event Sourcing.

### Aggregates

The main aggregate in the Customer Microservice is the `Customer` aggregate. It is represented by two
states: `EmptyCustomer` and `ExistingCustomer`.

- `EmptyCustomer` represents a customer that is not yet registered in the system.
- `ExistingCustomer` represents a customer that is already registered in the system.

The `Customer` aggregate handles commands such
as `RegisterCustomer`, `RequestNewCustomerReservation`, `UpdateCustomerReservationAsConfirmed`,
and `UpdateCustomerReservationAsDeclined`. These commands trigger events that change the state of the `Customer`
aggregate.

### APIs

The main API in the Customer Microservice is the `CustomerApi`. It uses the `AggregateManager` to handle the customer's
commands and events. It also provides a method to find a customer by their ID.

The `CustomerCommand` API defines the commands that can be executed on the `Customer` aggregate. These commands
include `RegisterCustomer`, `RequestNewCustomerReservation`, `UpdateCustomerReservationAsConfirmed`,
and `UpdateCustomerReservationAsDeclined`.

The `CustomerEvent` API defines the events that can occur in the lifecycle of a `Customer` aggregate. These events
include `CustomerRegistered`, `CustomerReservationRequested`, `CustomerReservationConfirmed`,
and `CustomerReservationDeclined`.

### Basic Flows

The basic flows in the Customer Microservice are represented by the `RegisterCustomerFlow`
and `ReservationToCustomerFlow` classes.

- `RegisterCustomerFlow` is responsible for the registration flow of a customer. It uses the `AggregateManager` to
  handle the customer's commands and events. It also uses the `CustomerView` to check if a customer already exists
  before registration.

- `ReservationToCustomerFlow` is responsible for handling reservation events. It listens to reservation events and
  updates the `Customer` aggregate accordingly. It handles events such as `ReservationAccepted`
  and `ReservationDeclined`.

## Reservation Microservice

The Reservation Microservice is designed to handle all operations related to reservations. The microservice is designed
following the principles of Domain-Driven Design (DDD) and Event Sourcing.

### Aggregates

The Reservation Microservice has two main aggregates:

1. **ReservationAggregate**: This aggregate is responsible for handling all operations related to a reservation. It
   includes commands such as `RequestReservation`, `AcceptReservation`, and `DeclineReservation`, and events such
   as `ReservationRequested`, `ReservationAccepted`, and `ReservationDeclined`.

2. **TimeSlotAggregate**: This aggregate is responsible for managing time slots. It includes commands such
   as `BookTimeSlot`, `CreateTimeSlot`, and `UpdateTimeSlot`, and events such as `TimeSlotBooked`, `TimeSlotCreated`,
   and `TimeSlotUpdated`.

### APIs

The Reservation Microservice exposes APIs for managing reservations and time slots:

1. **ReservationApi**: This API allows executing commands on the `ReservationAggregate` and retrieving reservation
   documents by their ID.

2. **TimeSlotApi**: This API allows executing commands on the `TimeSlotAggregate`, retrieving time slot documents by
   their ID, and finding time slots between specific times.

### Basic Flows

The Reservation Microservice has several basic flows:

1. **CustomerToReservationFlow**: This flow handles the creation of a reservation when a `CustomerReservationRequested`
   event is received from the Customer Microservice.

2. **TimeSlotToReservationFlow**: This flow handles the booking of a time slot when a `ReservationRequested` event is
   received from the Reservation Microservice. If no time slot is available, the reservation is declined.

3. **ReservationToTimeSlotFlow**: This flow handles the booking of a time slot when a `ReservationRequested` event is
   received. If the time slot is already booked, the reservation is declined.
