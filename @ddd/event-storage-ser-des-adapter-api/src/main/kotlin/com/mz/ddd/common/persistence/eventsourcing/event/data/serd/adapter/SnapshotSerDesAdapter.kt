package com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter

import com.mz.ddd.common.api.domain.Document
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.Snapshot

/**
 * Snapshot serialization/deserialization adapter.
 */
interface SnapshotSerDesAdapter<S : Document<*>> : SerDesAdapter {

    fun serialize(document: S): ByteArray

    fun deserialize(snapshot: Snapshot): S

}