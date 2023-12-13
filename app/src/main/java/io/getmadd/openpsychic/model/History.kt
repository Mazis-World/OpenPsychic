package io.getmadd.openpsychic.model

class History(
    val eventId: String,
    val uId: String,
    val timestamp: String,
    val eventType: EventType,
    val description: String,
    val details: String?,
)

enum class EventType{
    Message,
    REQUEST,
    LIVE
}