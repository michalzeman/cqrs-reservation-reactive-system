package com.mz.customer

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@Configuration
@Import(
    CustomerConfiguration::class,
)
@ComponentScan("com.mz.customer.**")
@ActiveProfiles("test")
class CustomerTestConfiguration {
}