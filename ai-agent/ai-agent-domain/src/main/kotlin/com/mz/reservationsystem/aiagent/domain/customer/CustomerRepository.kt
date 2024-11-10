package com.mz.reservationsystem.aiagent.domain.customer

import com.mz.customer.domain.api.CustomerDocument
import com.mz.ddd.common.api.domain.Email
import com.mz.ddd.common.api.domain.FirstName
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.LastName

data class RegisterCustomer(
    val lastName: LastName,
    val firstName: FirstName,
    val email: Email
)

data class Customer(val document: CustomerDocument)

interface CustomerRepository {
    suspend fun registerCustomer(registerCustomer: RegisterCustomer): CustomerDocument

    suspend fun findCustomer(id: Id): Customer?
}
