package com.rudra.legalassistantbd.ai

import com.rudra.legalassistantbd.core.database.dao.LawSectionDao
import com.rudra.legalassistantbd.data.repository.LawRepository
import javax.inject.Inject
import javax.inject.Singleton

data class AIResponse(
    val answer: String,
    val source: String = "offline",
    val confidence: Float = 0.5f,
    val relatedSections: List<String> = emptyList(),
    val isLawExplanation: Boolean = false
)

@Singleton
class OfflineLegalAI @Inject constructor(
    private val lawRepository: LawRepository,
    private val sectionDao: LawSectionDao
) {
    suspend fun processQuery(query: String): AIResponse {
        val lower = query.lowercase().trim()

        if (lower.contains("ব্যাখ্যা") || lower.contains("explain") || lower.contains("বলে")) {
            val sectionMatch = Regex("(\\d{3})").find(query)
            if (sectionMatch != null) {
                val section = sectionDao.getSectionByNumber(sectionMatch.value)
                if (section != null) {
                    val answer = buildString {
                        appendLine("**${section.titleEn} / ${section.titleBn}**")
                        appendLine()
                        appendLine("**Section ${section.sectionNumber}**")
                        appendLine()
                        appendLine("English: ${section.contentEn}")
                        appendLine()
                        appendLine("বাংলা: ${section.contentBn}")
                        if (section.punishment != null) {
                            appendLine()
                            appendLine("শাস্তি: ${section.punishment}")
                        }
                    }
                    return AIResponse(
                        answer = answer,
                        confidence = 0.9f,
                        relatedSections = listOf(section.sectionNumber),
                        isLawExplanation = true
                    )
                }
            }
        }

        val qaMatch = LegalQADataset.findAnswer(query)
        if (qaMatch != null) {
            return AIResponse(
                answer = qaMatch.answer,
                confidence = 0.8f,
                relatedSections = qaMatch.relatedSections
            )
        }

        val relatedQA = LegalQADataset.searchRelated(query)
        if (relatedQA.isNotEmpty()) {
            val answer = relatedQA.joinToString("\n\n") { "${it.question}\n${it.answer}" }
            return AIResponse(
                answer = answer,
                confidence = 0.6f,
                relatedSections = relatedQA.flatMap { it.relatedSections }
            )
        }

        val sectionMatch = Regex("(\\d{3})").find(query)
        if (sectionMatch != null) {
            val section = sectionDao.getSectionByNumber(sectionMatch.value)
            if (section != null) {
                return AIResponse(
                    answer = "Section ${section.sectionNumber}: ${section.titleEn}\n\n${section.contentEn}\n\n${section.contentBn}",
                    confidence = 0.85f,
                    relatedSections = listOf(section.sectionNumber),
                    isLawExplanation = true
                )
            }
        }

        return AIResponse(
            answer = "আমি এই বিষয়ে এখনও পর্যাপ্ত তথ্য দিতে পারছি না। অনুগ্রহ করে নির্দিষ্ট আইনের ধারা বা বিষয় উল্লেখ করুন।\n\nI don't have enough information on this topic yet. Please mention a specific law section or topic.",
            confidence = 0.3f
        )
    }

    suspend fun summarizeSection(sectionNumber: String): String {
        val section = sectionDao.getSectionByNumber(sectionNumber) ?: return "Section $sectionNumber not found."
        return buildString {
            appendLine("Summary of Section $sectionNumber:")
            appendLine("Title: ${section.titleEn} / ${section.titleBn}")
            appendLine()
            appendLine("Key Points:")
            val content = section.contentEn
            val sentences = content.split(". ")
            sentences.take(3).forEach { sentence ->
                appendLine("- $sentence.")
            }
        }
    }

    suspend fun draftDocument(documentType: String, details: Map<String, String>): String {
        val template = when (documentType.lowercase()) {
            "fir" -> """
                FIR DRAFT
                ==========
                Date: ${details["date"] ?: "[Date]"}
                Complainant Name: ${details["name"] ?: "[Name]"}
                Incident Date: ${details["incidentDate"] ?: "[Date]"}
                Incident Location: ${details["location"] ?: "[Location]"}

                Description of Incident:
                ${details["description"] ?: "[Description]"}

                Suspect Information:
                ${details["suspect"] ?: "[Suspect Information]"}

                Witnesses:
                ${details["witnesses"] ?: "[Witness Details]"}
            """.trimIndent()
            "legal notice" -> """
                LEGAL NOTICE
                =============
                Date: ${details["date"] ?: "[Date]"}
                From: ${details["from"] ?: "[Sender]"}
                To: ${details["to"] ?: "[Recipient]"}

                Subject: ${details["subject"] ?: "[Subject]"}

                Notice Content:
                ${details["content"] ?: "[Content]"}

                You are hereby requested to comply within ${details["deadline"] ?: "15"} days.
            """.trimIndent()
            "affidavit" -> """
                AFFIDAVIT
                ==========
                I, ${details["name"] ?: "[Name]"}, son/daughter of ${details["fatherName"] ?: "[Father's Name]"}, 
                of ${details["address"] ?: "[Address]"}, do hereby solemnly affirm and declare as follows:

                ${details["content"] ?: "[Content]"}

                Deponent's Signature: _______________
                Date: ${details["date"] ?: "[Date]"}
            """.trimIndent()
            else -> "Document type '$documentType' is not supported yet."
        }
        return template
    }
}
