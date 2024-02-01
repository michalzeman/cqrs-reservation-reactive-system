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

## Event-Sourced Persistence Model in CQRS-based Microservices

The data model in the context of Command Query Responsibility Segregation (CQRS) and Event Sourcing in this system is
designed to be flexible and robust. It is built around the concept of Aggregates, which are clusters of domain objects
that can be treated as a single unit. These aggregates manage their own states and handle commands that trigger events,
changing the state of the aggregate.

The system leverages Apache Cassandra for data persistence, providing a flexible DB storage for event sourcing. Each
event is tagged with a domain tag, which is a unique identifier for the domain entity that the event is related to. This
allows for efficient querying and retrieval of events related to a specific domain entity.

The view model in this system is also designed to be flexible. It provides a storage mechanism for the current state of
the aggregates, which is updated as events are processed. This view model can be used as an alternative to querying the
event store for the current state of an aggregate. However, it is also designed to be customizable per microservice,
allowing each microservice to implement its own view model based on its specific needs.

In summary, the data model in this system is designed to support the principles of CQRS and Event Sourcing, providing a
flexible and efficient mechanism for managing the state of aggregates and handling domain events. The use of a domain
tag and a flexible view model further enhances the robustness and adaptability of the system.

## View Model

The view model in this system, also known as the read model in the context of Command Query Responsibility Segregation (
CQRS), is designed to be highly flexible and adaptable. It provides a storage mechanism for the current state of the
aggregates, which is updated as events are processed. This view model can be used as an alternative to querying the
event store for the current state of an aggregate.

The view model is implemented using a set of queryable data
classes (`QueryableString`, `QueryableBoolean`, `QueryableInstant`, `QueryableLong`, `QueryableDouble`) that can store
different types of data. Each piece of data is associated with a domain tag, which is a unique identifier for the domain
entity that the data is related to. This allows for efficient querying and retrieval of data related to a specific
domain entity.

The view model is also designed to be customizable per microservice, allowing each microservice to implement its own
view model based on its specific needs. This is achieved by providing a `DomainViewRepository` interface for saving
queryable data to the view model.

In summary, the view model in this system provides a flexible and efficient mechanism for querying the current state of
aggregates. It supports complex queries and is customizable per microservice, enhancing the robustness and adaptability
of the system.

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
