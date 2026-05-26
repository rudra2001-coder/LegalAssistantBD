package com.rudra.legalassistantbd.core.util

object Constants {
    const val APP_NAME = "Legal Assistant BD"
    const val DATABASE_NAME = "legal_assistant_bd.db"

    // Case Status
    const val CASE_STATUS_ACTIVE = "Active"
    const val CASE_STATUS_PENDING = "Pending"
    const val CASE_STATUS_CLOSED = "Closed"
    const val CASE_STATUS_DRAFT = "Draft"

    // Case Types
    const val CASE_TYPE_CRIMINAL = "Criminal"
    const val CASE_TYPE_CIVIL = "Civil"
    const val CASE_TYPE_FAMILY = "Family"
    const val CASE_TYPE_LABOUR = "Labour"
    const val CASE_TYPE_OTHER = "Other"

    // Reminder Types
    const val REMINDER_HEARING = "Hearing"
    const val REMINDER_DEADLINE = "Deadline"
    const val REMINDER_DOCUMENT = "Document Submission"
    const val REMINDER_OTHER = "Other"

    // PDF Converter
    const val PDF_MIME_TYPE = "application/pdf"
    const val JSON_MIME_TYPE = "application/json"

    // AI
    const val AI_MODE_OFFLINE = "offline"
    const val AI_MODE_ONLINE = "online"

    // Navigation Routes
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
    const val ROUTE_PROCEDURE_GUIDANCE = "procedure_guidance/{caseId}"
}
