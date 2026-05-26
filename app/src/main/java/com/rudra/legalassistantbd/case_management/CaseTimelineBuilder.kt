package com.rudra.legalassistantbd.case_management

import com.rudra.legalassistantbd.core.database.entity.CaseEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

class TimelineEvent(
    val date: Long,
    val title: String,
    val description: String,
    val eventType: EventType
)

enum class EventType {
    FILING, HEARING, DOCUMENT, STATUS_CHANGE, REMINDER
}

@Singleton
class CaseTimelineBuilder @Inject constructor() {

    fun buildTimeline(case: CaseEntity): List<TimelineEvent> {
        val events = mutableListOf<TimelineEvent>()

        events.add(
            TimelineEvent(
                date = case.filingDate,
                title = "Case Filed",
                description = "Case ${case.caseNumber} was filed on ${formatDate(case.filingDate)}",
                eventType = EventType.FILING
            )
        )

        case.nextHearing?.let { hearingDate ->
            events.add(
                TimelineEvent(
                    date = hearingDate,
                    title = "Next Hearing",
                    description = "Next hearing scheduled on ${formatDate(hearingDate)}",
                    eventType = EventType.HEARING
                )
            )
        }

        events.sortBy { it.date }
        return events
    }

    fun addStatusChangeEvent(events: MutableList<TimelineEvent>, timestamp: Long, oldStatus: String, newStatus: String) {
        events.add(
            TimelineEvent(
                date = timestamp,
                title = "Status Changed",
                description = "Status changed from '$oldStatus' to '$newStatus'",
                eventType = EventType.STATUS_CHANGE
            )
        )
        events.sortBy { it.date }
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
