package com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.json

import com.mz.ddd.common.api.domain.Document
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.Snapshot
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.SnapshotSerDesAdapter

/**
 * JSON snapshot serialization/deserialization adapter.
 */
class JsonSnapshotSerDesAdapter<S : Document>(
    encode: Encoder<S>,
    decode: Decode<S>
) : JsonSerDesAdapter<S>(encode, decode), SnapshotSerDesAdapter<S> {
    override fun serialize(document: S): ByteArray = encode(document)

    override fun deserialize(snapshot: Snapshot): S {
        val rawPayload = snapshot.payload
        return decode(rawPayload.decodeToString())
    }
}