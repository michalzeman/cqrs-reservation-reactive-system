package com.mz.common.components

import com.mz.common.components.internal.ApplicationChannelStreamImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CommonComponentsConfiguration {

    @Bean
    fun messageChannelStream(): ApplicationChannelStream {
        return ApplicationChannelStreamImpl()
    }
}