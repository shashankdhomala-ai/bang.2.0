package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room persistence entity for individual encrypted packet messages.
 * Represents both direct peer packets, multi-hop mesh relays, and cloud messages.
 */
@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey
    val id: String,
    val threadId: String,
    val senderId: String,
    val senderName: String,
    val content: String,
    val timestamp: Long,
    val deliveryStatus: String,
    val transport: String,
    val isFromMe: Boolean,
    val ttlExpiryTimestamp: Long = 0L,
    val payloadHash: String = ""
)

/**
 * Room persistence entity for chat threads/conversations.
 */
@Entity(tableName = "threads")
data class ThreadEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val lastMessage: String,
    val timestamp: Long,
    val unreadCount: Int = 0,
    val preferredTransport: String = "HYBRID_AUTO"
)
