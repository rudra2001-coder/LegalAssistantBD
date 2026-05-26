package com.rudra.legalassistantbd.pdf_converter

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StructureDetector @Inject constructor() {

    private val sectionPattern = Regex(
        """(?:Section|ধারা|সেকশন)\s*[#:.．\s]*(\d+[A-Za-z]?)\s*[:\-–—]?\s*(.*?)(?=(?:Section|ধারা|সেকশन|\n\n|$))""",
        setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
    )

    private val titlePattern = Regex(
        """(?:Title|শিরোনাম|Heading|হেডিং)\s*[:\-–—]?\s*(.+)""",
        RegexOption.IGNORE_CASE
    )

    fun detectSections(text: String): List<ParsedSection> {
        val sections = mutableListOf<ParsedSection>()
        var currentSection: ParsedSection? = null
        val lines = text.lines()

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isBlank()) continue

            val sectionMatch = Regex(
                """(?:Section|ধারা)\s+(\d+[A-Za-z]?)""",
                RegexOption.IGNORE_CASE
            ).find(trimmed)

            if (sectionMatch != null) {
                currentSection?.let { sections.add(it) }
                currentSection = ParsedSection(
                    sectionNumber = sectionMatch.groupValues[1],
                    title = "",
                    content = ""
                )
                val afterSection = trimmed.substring(sectionMatch.range.last + 1).trim()
                if (afterSection.isNotEmpty()) {
                    if (currentSection!!.title.isEmpty()) {
                        currentSection = currentSection!!.copy(title = afterSection)
                    } else {
                        currentSection = currentSection!!.copy(
                            content = currentSection!!.content + "\n" + afterSection
                        )
                    }
                }
            } else {
                if (currentSection != null) {
                    if (currentSection!!.title.isEmpty() && !trimmed.startsWith("(")) {
                        currentSection = currentSection!!.copy(title = trimmed)
                    } else {
                        currentSection = currentSection!!.copy(
                            content = currentSection!!.content + "\n" + trimmed
                        )
                    }
                }
            }
        }
        currentSection?.let { sections.add(it) }
        return sections
    }

    fun extractKeywords(section: ParsedSection): List<String> {
        val keywords = mutableSetOf<String>()
        val stopWords = setOf("the", "a", "an", "in", "of", "to", "and", "or", "is", "are", "was", "were", "be", "been", "shall", "may", "any", "such", "said", "under", "with", "within", "without", "after", "before", "upon", "whoever", "which", "that", "this", "these", "those", "his", "her", "its", "their", "him", "them")

        val words = section.title.split("\\s+".toRegex()) +
                section.content.split("\\s+".toRegex())

        words
            .map { it.replace(Regex("[^a-zA-Zবাংলা]"), "").lowercase() }
            .filter { it.length > 3 && it !in stopWords }
            .take(10)
            .let { keywords.addAll(it) }

        return keywords.toList()
    }
}

data class ParsedSection(
    val sectionNumber: String,
    val title: String,
    val content: String,
    val keywords: List<String> = emptyList()
)
