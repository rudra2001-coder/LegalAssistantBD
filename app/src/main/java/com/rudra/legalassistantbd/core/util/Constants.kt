package com.rudra.legalassistantbd.core.util

object Constants {
    const val APP_NAME = "Legal Assistant BD"
    const val DATABASE_NAME = "legal_assistant_bd.db"

    // Case Status
    const val CASE_STATUS_ACTIVE = "Active"
    const val CASE_STATUS_PENDING = "Pending"
    const val CASE_STATUS_CLOSED = "Closed"
    const val CASE_STATUS_DRAFT = "Draft"
    val CASE_STATUSES = listOf(CASE_STATUS_ACTIVE, CASE_STATUS_PENDING, CASE_STATUS_CLOSED, CASE_STATUS_DRAFT)

    // Case Types
    const val CASE_TYPE_CRIMINAL = "Criminal"
    const val CASE_TYPE_CIVIL = "Civil"
    const val CASE_TYPE_FAMILY = "Family"
    const val CASE_TYPE_LABOUR = "Labour"
    const val CASE_TYPE_OTHER = "Other"
    val CASE_TYPES = listOf(CASE_TYPE_CRIMINAL, CASE_TYPE_CIVIL, CASE_TYPE_FAMILY, CASE_TYPE_LABOUR, CASE_TYPE_OTHER)

    // Reminder Types
    const val REMINDER_HEARING = "Hearing"
    const val REMINDER_DEADLINE = "Deadline"
    const val REMINDER_DOCUMENT = "Document Submission"
    const val REMINDER_OTHER = "Other"

    // Hearing Types
    const val HEARING_FIRST = "First Hearing"
    const val HEARING_REGULAR = "Regular Hearing"
    const val HEARING_FINAL = "Final Hearing"
    const val HEARING_ARGUMENT = "Argument"
    const val HEARING_BAIL = "Bail Hearing"
    const val HEARING_OTHER = "Other"
    val HEARING_TYPES = listOf(HEARING_FIRST, HEARING_REGULAR, HEARING_FINAL, HEARING_ARGUMENT, HEARING_BAIL, HEARING_OTHER)

    // Hearing Outcomes
    const val HEARING_OUTCOME_ADJOURNED = "Adjourned"
    const val HEARING_OUTCOME_COMPLETED = "Completed"
    const val HEARING_OUTCOME_PARTIAL = "Partially Heard"
    const val HEARING_OUTCOME_RESERVED = "Order Reserved"
    const val HEARING_OUTCOME_OTHER = "Other"
    val HEARING_OUTCOMES = listOf(HEARING_OUTCOME_COMPLETED, HEARING_OUTCOME_ADJOURNED, HEARING_OUTCOME_PARTIAL, HEARING_OUTCOME_RESERVED, HEARING_OUTCOME_OTHER)

    // Bail Types
    const val BAIL_REGULAR = "Regular"
    const val BAIL_ANTICIPATORY = "Anticipatory"
    const val BAIL_INTERIM = "Interim"
    const val BAIL_DEFAULT = "Default"
    const val BAIL_OTHER = "Other"
    val BAIL_TYPES = listOf(BAIL_REGULAR, BAIL_ANTICIPATORY, BAIL_INTERIM, BAIL_DEFAULT, BAIL_OTHER)

    // Bail Statuses
    const val BAIL_STATUS_FILED = "Filed"
    const val BAIL_STATUS_PENDING = "Pending"
    const val BAIL_STATUS_GRANTED = "Granted"
    const val BAIL_STATUS_REJECTED = "Rejected"
    const val BAIL_STATUS_CANCELLED = "Cancelled"
    val BAIL_STATUSES = listOf(BAIL_STATUS_FILED, BAIL_STATUS_PENDING, BAIL_STATUS_GRANTED, BAIL_STATUS_REJECTED, BAIL_STATUS_CANCELLED)

    // Document Types
    const val DOC_TYPE_IMAGE = "Image"
    const val DOC_TYPE_PDF = "PDF"
    const val DOC_TYPE_DOCUMENT = "Document"
    const val DOC_TYPE_OTHER = "Other"

    // PDF Converter
    const val PDF_MIME_TYPE = "application/pdf"
    const val JSON_MIME_TYPE = "application/json"

    // AI
    const val AI_MODE_OFFLINE = "offline"
    const val AI_MODE_ONLINE = "online"

    // Navigation Routes
    const val ROUTE_ONBOARDING = "onboarding"
    const val ROUTE_DASHBOARD = "dashboard"
    const val ROUTE_LAW_EXPLORER = "law_explorer"
    const val ROUTE_LAW_DETAIL = "law_detail/{lawId}"
    const val ROUTE_SECTION_DETAIL = "section_detail/{sectionId}"
    const val ROUTE_SEARCH = "search"
    const val ROUTE_CASES = "cases"
    const val ROUTE_CASE_DETAIL = "case_detail/{caseId}"
    const val ROUTE_CREATE_CASE = "create_case"
    const val ROUTE_PROCEDURES = "procedures/{sectionId}"
    const val ROUTE_AI_CHAT = "ai_chat"
    const val ROUTE_DOCUMENTS = "documents"
    const val ROUTE_REMINDERS = "reminders"
    const val ROUTE_PDF_CONVERTER = "pdf_converter"
    const val ROUTE_SECURITY = "security"
    const val ROUTE_CUSTOM_SECTION = "custom_section"
    const val ROUTE_ALL_FEATURES = "all_features"
    const val ROUTE_SETTINGS = "settings"
    const val ROUTE_PROCEDURE_GUIDANCE = "procedure_guidance/{caseId}"

    const val ROUTE_LAW_DETAIL_WITH_LAW_ID = "law_detail"
    const val ROUTE_SECTION_DETAIL_WITH_SECTION_ID = "section_detail"
    const val ROUTE_CASE_DETAIL_WITH_CASE_ID = "case_detail"
    const val ROUTE_PROCEDURES_WITH_SECTION_ID = "procedures"
    const val ROUTE_PROCEDURE_GUIDANCE_WITH_CASE_ID = "procedure_guidance"
}
