package com.mz.ddd.common.shared.test.cassandra

import com.datastax.oss.driver.api.core.CqlSession
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.net.InetSocketAddress

/**
 * Wait for the database to be ready.
 */
fun waitForDatabase(keySpace: String, hostName: String, port: Int, localDataCenter: String) {
    repeat(10) {
        try {
            CqlSession.builder()
                .withKeyspace(keySpace)
                .addContactPoint(InetSocketAddress(hostName, port))
                .withLocalDatacenter(localDataCenter)
                .build()
                .close()
            return
        } catch (e: Exception) {
            Thread.sleep(1000)
        }
    }
    throw RuntimeException("Could not connect to the database")
}