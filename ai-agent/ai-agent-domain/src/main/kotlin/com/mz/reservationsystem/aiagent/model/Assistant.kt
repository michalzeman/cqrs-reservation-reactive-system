package com.mz.reservationsystem.aiagent.model

import dev.langchain4j.service.TokenStream

interface Assistant {

     fun chat(message: String): TokenStream
}