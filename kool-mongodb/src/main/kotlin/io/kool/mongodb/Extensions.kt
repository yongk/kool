package io.kool.mongodb

import com.mongodb.BasicDBObject
import com.mongodb.DB
import com.mongodb.DBCollection
import com.mongodb.DBObject
import com.mongodb.Mongo
import io.kool.mongodb.support.ReplicationStream
import io.kool.stream.Stream
import org.bson.types.BSONTimestamp

/**
* Creates a [[Stream<ReplicaEvent>]] from the given Mongo.
*/
public fun Mongo.replicationStream(val databaseName: String? = null, val collectionName: String? = null, val timestamp: BSONTimestamp? = null,
                                   val tail: Boolean = false, val replicaLog: DBCollection = this.replicaLog()): Stream<ReplicaEvent> {
    return ReplicationStream(replicaLog, databaseName, collectionName, timestamp, tail)
}

/**
 * Returns the replica log for this mongo
 */
public fun Mongo.replicaLog(val replicaLogDatabaseName: String = "local", replicaLogCollectionName: String = "oplog.rs"): DBCollection {
    return getDB(replicaLogDatabaseName)!!.getCollection(replicaLogCollectionName)!!
}

/**
 * Creates an active collection from the given database.
 */
public fun DB.observableCollection(val collName: String): ObservableDbCollection {
    val dbCollection = getCollection(collName)!!
    return ObservableDbCollection(dbCollection)
}

/**
 * Creates an active collection for this underlying [[DBCollection]]
 */
public fun DBCollection.observable(): ObservableDbCollection {
    return ObservableDbCollection(this)
}

/**
 * Creates a [[Stream<ReplicaEvent>]] from the given database.
 */
public fun DB.replicationStream(val collectionName: String? = null, val timestamp: BSONTimestamp? = null, val tail: Boolean = false,
                                val replicaLog: DBCollection = getMongo()!!.replicaLog()): Stream<ReplicaEvent> {
    return ReplicationStream(replicaLog, getName(), collectionName, timestamp, tail)
}

/**
 * Creates a [[Stream<ReplicationEntry>]] from the given collection.
 */
public fun DBCollection.replicationStream(val databaseName: String? = null, val collectionName: String? = null, val timestamp: BSONTimestamp? = null,
                                          val tail: Boolean = false, val replicaLog: DBCollection = getDB()!!.getMongo()!!.replicaLog()): Stream<ReplicaEvent> {
    return ReplicationStream(replicaLog, getDB()?.getName(), getName(), timestamp, tail)
}

/**
 * Helper method to create a [[DBObject]] using either tuple notation or using
 * the 'to' infix operator.
 */
public inline fun dbObject(vararg values: Pair<String, *>): DBObject {
    val answer = BasicDBObject()
    for (v in values) {
        answer.put(v.first, v.second)
    }
    return answer
}


/**
 * Creates a new DBObject with this object and the given delta changes applied
 */
public fun DBObject.merge(delta: DBObject): DBObject {
    val answer = BasicDBObject(this.toMap())
    answer.putAll(delta)
    return answer
}