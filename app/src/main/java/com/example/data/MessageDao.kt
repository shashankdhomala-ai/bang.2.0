package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for BANG local mesh message and thread persistence.
 */
@Dao
interface MessageDao {

    @Query("SELECT * FROM messages WHERE threadId = :threadId ORDER BY timestamp ASC")
    fun getMessagesForThread(threadId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<MessageEntity>>

    @Query("SELECT * FROM threads ORDER BY timestamp DESC")
    fun getAllThreads(): Flow<List<ThreadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThread(thread: ThreadEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThreads(threads: List<ThreadEntity>)

    @Query("UPDATE messages SET deliveryStatus = :status WHERE id = :messageId")
    suspend fun updateDeliveryStatus(messageId: String, status: String)

    @Query("DELETE FROM messages WHERE ttlExpiryTimestamp > 0 AND ttlExpiryTimestamp <= :currentTimeMs")
    suspend fun purgeExpiredPackets(currentTimeMs: Long): Int

    @Query("SELECT * FROM messages WHERE id = :messageId LIMIT 1")
    suspend fun getMessageById(messageId: String): MessageEntity?

    @Query("SELECT * FROM threads WHERE id = :threadId LIMIT 1")
    suspend fun getThreadById(threadId: String): ThreadEntity?

    @Query("DELETE FROM messages WHERE threadId = :threadId")
    suspend fun deleteMessagesForThread(threadId: String)

    @Query("DELETE FROM messages")
    suspend fun clearAllMessages()

    @Query("DELETE FROM threads")
    suspend fun clearAllThreads()
}
