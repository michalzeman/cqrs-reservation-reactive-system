package com.mz.reservationsystem.aiagent.adapter.customer

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.web.reactive.function.client.WebClient


@ExtendWith(MockitoExtension::class)
class BackedCustomerRepositoryTest {

    @Mock
    lateinit var webClient: WebClient

    lateinit var cut: BackedCustomerRepository

    @BeforeEach
    fun setUp() {
        cut = BackedCustomerRepository(webClient)
    }

    @Test
    fun registerCustomer() {
    }

    @Test
    fun findCustomer() {
    }
}