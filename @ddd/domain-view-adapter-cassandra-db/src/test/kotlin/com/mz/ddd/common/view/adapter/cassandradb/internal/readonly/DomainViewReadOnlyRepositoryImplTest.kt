package com.mz.ddd.common.view.adapter.cassandradb.internal.readonly

import com.mz.ddd.common.view.*
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import reactor.core.publisher.Flux
import java.time.Instant


@ExtendWith(MockitoExtension::class)
internal class DomainViewReadOnlyRepositoryImplTest {

    @Mock
    lateinit var queryableTextViewEntityRepository: QueryableTextViewEntityRepository

    @Mock
    lateinit var queryableBooleanViewEntityRepository: QueryableBooleanViewEntityRepository

    @Mock
    lateinit var queryableTimestampViewEntityRepository: QueryableTimestampViewEntityRepository

    @Mock
    lateinit var queryableLongViewEntityRepository: QueryableLongViewEntityRepository

    @Mock
    lateinit var queryableDoubleViewEntityRepository: QueryableDoubleViewEntityRepository

    lateinit var cut: DomainViewReadOnlyRepositoryImpl

    @BeforeEach
    fun setUp() {
        cut = DomainViewReadOnlyRepositoryImpl(
            queryableTextViewEntityRepository,
            queryableBooleanViewEntityRepository,
            queryableTimestampViewEntityRepository,
            queryableLongViewEntityRepository,
            queryableDoubleViewEntityRepository
        )
    }

    @Test
    fun `find when DomainViewQuery, then result is returned`() {
        // given
        val queryString1 = QueryString("propertyText", "domainTag", "value1")
        val queryString2 = QueryString("propertyText2", "domainTag", "value2")
        val queryBoolean = QueryBoolean("propertyBoolean", "domainTag", true)
        val queryDouble = QueryDouble("propertyDouble", "domainTag", 1.0)
        val queryLong = QueryLong("propertyLong", "domainTag", 1)
        val query = DomainViewQuery(
            setOf(
                queryString1, queryString2, queryBoolean, queryDouble, queryLong
            )
        )

        val aggregateId1 = "aggregateId1"
        val aggregateId2 = "aggregateId2"

        val queryableTextResult1 = QueryableTextViewEntity().apply {
            aggregateId = aggregateId1
            propertyName = "propertyText"
            domainTag = "domainTag"
            value = "value1"
            timestamp = Instant.now()
        }

        val queryableTextResult2 = QueryableTextViewEntity().apply {
            aggregateId = aggregateId1
            propertyName = "propertyText2"
            domainTag = "domainTag"
            value = "value2"
            timestamp = Instant.now()
        }

        val queryableBooleanResult = QueryableBooleanViewEntity().apply {
            aggregateId = aggregateId1
            propertyName = "propertyBoolean"
            domainTag = "domainTag"
            value = true
            timestamp = Instant.now()
        }

        val queryableDoubleResult = QueryableDoubleViewEntity().apply {
            aggregateId = aggregateId2
            propertyName = "propertyDouble"
            domainTag = "domainTag"
            value = 1.0
            timestamp = Instant.now()
        }

        val queryableLongResult = QueryableLongViewEntity().apply {
            aggregateId = aggregateId2
            propertyName = "propertyLong"
            domainTag = "domainTag"
            value = 1
            timestamp = Instant.now()
        }

        whenever(
            queryableTextViewEntityRepository.findByPropertyNameAndDomainTagAndValue(
                queryString1.propertyName,
                queryString1.domainTag,
                queryString1.value
            )
        ).thenReturn(Flux.just(queryableTextResult1))

        whenever(
            queryableTextViewEntityRepository.findByPropertyNameAndDomainTagAndValue(
                queryString2.propertyName,
                queryString2.domainTag,
                queryString2.value
            )
        ).thenReturn(Flux.just(queryableTextResult2))

        whenever(
            queryableBooleanViewEntityRepository.findByPropertyNameAndDomainTagAndValue(
                queryBoolean.propertyName,
                queryBoolean.domainTag,
                queryBoolean.value
            )
        ).thenReturn(Flux.just(queryableBooleanResult))

        whenever(
            queryableDoubleViewEntityRepository.findByPropertyNameAndDomainTagAndValue(
                queryDouble.propertyName,
                queryDouble.domainTag,
                queryDouble.value
            )
        ).thenReturn(Flux.just(queryableDoubleResult))

        whenever(
            queryableLongViewEntityRepository.findByPropertyNameAndDomainTagAndValue(
                queryLong.propertyName,
                queryLong.domainTag,
                queryLong.value
            )
        ).thenReturn(Flux.just(queryableLongResult))

        // when
        val result = cut.find(query).collectList().block()

        // then
        assertThat(result?.size).isEqualTo(2)
        assertThat(result?.find { it.aggregateId.value == aggregateId1 }?.views?.size).isEqualTo(3)
        assertThat(result?.find { it.aggregateId.value == aggregateId2 }?.views?.size).isEqualTo(2)
    }

    @Test
    fun `find when DomainViewQuery and AND operation, then result is returned when all condition are meat`() {
        // given
        val queryString1 = QueryString("propertyText", "domainTag", "value1")
        val queryString2 = QueryString("propertyText2", "domainTag", "value2")
        val queryBoolean = QueryBoolean("propertyBoolean", "domainTag", true)
        val queryDouble = QueryDouble("propertyDouble", "domainTag", 1.0)
        val queryLong = QueryLong("propertyLong", "domainTag", 1)
        val query = DomainViewQuery(
            setOf(
                queryString1, queryString2, queryBoolean, queryDouble, queryLong
            ),
            OperationType.AND
        )

        val aggregateId1 = "aggregateId1"
        val aggregateId2 = "aggregateId2"

        val queryableTextResult1 = QueryableTextViewEntity().apply {
            aggregateId = aggregateId1
            propertyName = "propertyText"
            domainTag = "domainTag"
            value = "value1"
            timestamp = Instant.now()
        }

        val queryableTextResult2 = QueryableTextViewEntity().apply {
            aggregateId = aggregateId1
            propertyName = "propertyText2"
            domainTag = "domainTag"
            value = "value2"
            timestamp = Instant.now()
        }

        val queryableBooleanResult = QueryableBooleanViewEntity().apply {
            aggregateId = aggregateId1
            propertyName = "propertyBoolean"
            domainTag = "domainTag"
            value = true
            timestamp = Instant.now()
        }

        val queryableDoubleResult1 = QueryableDoubleViewEntity().apply {
            aggregateId = aggregateId1
            propertyName = "propertyDouble"
            domainTag = "domainTag"
            value = 1.0
            timestamp = Instant.now()
        }

        val queryableDoubleResult2 = QueryableDoubleViewEntity().apply {
            aggregateId = aggregateId2
            propertyName = "propertyDouble"
            domainTag = "domainTag"
            value = 1.0
            timestamp = Instant.now()
        }

        val queryableLongResult1 = QueryableLongViewEntity().apply {
            aggregateId = aggregateId1
            propertyName = "propertyLong"
            domainTag = "domainTag"
            value = 1
            timestamp = Instant.now()
        }

        val queryableLongResult2 = QueryableLongViewEntity().apply {
            aggregateId = aggregateId2
            propertyName = "propertyLong"
            domainTag = "domainTag"
            value = 1
            timestamp = Instant.now()
        }

        whenever(
            queryableTextViewEntityRepository.findByPropertyNameAndDomainTagAndValue(
                queryString1.propertyName,
                queryString1.domainTag,
                queryString1.value
            )
        ).thenReturn(Flux.just(queryableTextResult1))

        whenever(
            queryableTextViewEntityRepository.findByPropertyNameAndDomainTagAndValue(
                queryString2.propertyName,
                queryString2.domainTag,
                queryString2.value
            )
        ).thenReturn(Flux.just(queryableTextResult2))

        whenever(
            queryableBooleanViewEntityRepository.findByPropertyNameAndDomainTagAndValue(
                queryBoolean.propertyName,
                queryBoolean.domainTag,
                queryBoolean.value
            )
        ).thenReturn(Flux.just(queryableBooleanResult))

        whenever(
            queryableDoubleViewEntityRepository.findByPropertyNameAndDomainTagAndValue(
                queryDouble.propertyName,
                queryDouble.domainTag,
                queryDouble.value
            )
        ).thenReturn(Flux.just(queryableDoubleResult1, queryableDoubleResult2))

        whenever(
            queryableLongViewEntityRepository.findByPropertyNameAndDomainTagAndValue(
                queryLong.propertyName,
                queryLong.domainTag,
                queryLong.value
            )
        ).thenReturn(Flux.just(queryableLongResult2, queryableLongResult1))

        // when
        val result = cut.find(query).collectList().block()

        // then
        assertThat(result?.size).isEqualTo(1)
        assertThat(result?.find { it.aggregateId.value == aggregateId1 }?.views?.size).isEqualTo(5)
        assertThat(result?.filter { it.aggregateId.value == aggregateId2 }?.size).isEqualTo(0)
    }

    @Test
    fun `find when BetweenInstantQuery, then result is returned`() {
        // given
        val betweenInstantQuery = BetweenInstantQuery(
            startTimePropertyName = "startTime",
            endTimePropertyName = "endTime",
            domainTag = "domainTag",
            startTime = Clock.System.now(),
            endTime = Clock.System.now()
        )
        val query = DomainViewQuery(
            setOf(
                betweenInstantQuery
            )
        )

        val aggregateId1 = "aggregateId1"
        val aggregateId2 = "aggregateId2"

        val queryableTimestampResult1 = QueryableTimestampViewEntity().apply {
            aggregateId = aggregateId1
            propertyName = "startTime"
            domainTag = "domainTag"
            value = Instant.now()
            timestamp = Instant.now()
        }

        val queryableTimestampResult2 = QueryableTimestampViewEntity().apply {
            aggregateId = aggregateId1
            propertyName = "endTime"
            domainTag = "domainTag"
            value = Instant.now()
            timestamp = Instant.now()
        }

        val queryableTimestampResult3 = QueryableTimestampViewEntity().apply {
            aggregateId = aggregateId2
            propertyName = "startTime"
            domainTag = "domainTag"
            value = Instant.now()
            timestamp = Instant.now()
        }

        val queryableTimestampResult4 = QueryableTimestampViewEntity().apply {
            aggregateId = aggregateId2
            propertyName = "endTime"
            domainTag = "domainTag"
            value = Instant.now()
            timestamp = Instant.now()
        }

        whenever(
            queryableTimestampViewEntityRepository.findByPropertyNameAndDomainTagAndValueBetween(
                "startTime",
                "domainTag",
                betweenInstantQuery.startTime.toJavaInstant(),
                betweenInstantQuery.endTime.toJavaInstant()
            )
        ).thenReturn(Flux.just(queryableTimestampResult1, queryableTimestampResult3))

        whenever(
            queryableTimestampViewEntityRepository.findByPropertyNameAndDomainTagAndValueBetween(
                "endTime",
                "domainTag",
                betweenInstantQuery.startTime.toJavaInstant(),
                betweenInstantQuery.endTime.toJavaInstant()
            )
        ).thenReturn(Flux.just(queryableTimestampResult2, queryableTimestampResult4))

        // when
        val result = cut.find(query).collectList().block()

        // then
        assertThat(result?.size).isEqualTo(2)
        assertThat(
            result?.find { it.aggregateId.value == aggregateId1 }
                ?.views?.filterIsInstance<QueryableBetweenInstant>()
                ?.size
        ).isEqualTo(1)
        assertThat(
            result?.find { it.aggregateId.value == aggregateId2 }
                ?.views?.filterIsInstance<QueryableBetweenInstant>()
                ?.size
        ).isEqualTo(1)
    }

    @Test
    fun `find when QueryInstant, then result is returned`() {
        // given
        val queryInstant = QueryInstant("propertyName", "domainTag", Clock.System.now())
        val query = DomainViewQuery(
            setOf(
                queryInstant
            )
        )

        val aggregateId1 = "aggregateId1"
        val aggregateId2 = "aggregateId2"

        val queryableTimestampResult1 = QueryableTimestampViewEntity().apply {
            aggregateId = aggregateId1
            propertyName = "propertyName"
            domainTag = "domainTag"
            value = Instant.now()
            timestamp = Instant.now()
        }

        val queryableTimestampResult2 = QueryableTimestampViewEntity().apply {
            aggregateId = aggregateId2
            propertyName = "propertyName"
            domainTag = "domainTag"
            value = Instant.now()
            timestamp = Instant.now()
        }

        whenever(
            queryableTimestampViewEntityRepository.findByPropertyNameAndDomainTagAndValue(
                queryInstant.propertyName,
                queryInstant.domainTag,
                queryInstant.value.toJavaInstant()
            )
        ).thenReturn(Flux.just(queryableTimestampResult1, queryableTimestampResult2))

        // when
        val result = cut.find(query).collectList().block()

        // then
        assertThat(result?.size).isEqualTo(2)
        assertThat(result?.find { it.aggregateId.value == aggregateId1 }?.views?.size).isEqualTo(1)
        assertThat(result?.find { it.aggregateId.value == aggregateId2 }?.views?.size).isEqualTo(1)
    }
}