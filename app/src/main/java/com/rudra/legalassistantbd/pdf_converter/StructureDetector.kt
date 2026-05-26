package com.rudra.legalassistantbd.pdf_converter

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StructureDetector @Inject constructor() {

    private val patterns = listOf(
        Regex("""(?:Section|ধারা|সেকশন)\s*[#:.．\s]*(\d+[A-Za-z]?)\s*[:\-–—]?\s*(.*?)(?=(?:Section|ধারা|সেকশন|\n\n|$))""", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)),
        Regex("""(?:ধারা|Section)\s+(\d+[A-Za-z]?)""", RegexOption.IGNORE_CASE),
        Regex("""^(\d+[A-Za-z]?)\.\s+(.*)""", RegexOption.MULTILINE),
        Regex("""^(\d+[A-Za-z]?)[)．]\s*(.*)""", RegexOption.MULTILINE),
        Regex("""CHAPTER\s+(\d+[A-Za-z]?)\s*[:\-–—]?\s*(.*?)(?=(?:CHAPTER|\n\n|$))""", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    )

    fun detectSections(text: String): List<ParsedSection> {
        val sections = mutableListOf<ParsedSection>()
        val lines = text.lines()
        var currentSection: ParsedSection? = null
        var buffer = StringBuilder()

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isBlank()) continue

            val sectionMatch = findSectionStart(trimmed)

            if (sectionMatch != null) {
                if (currentSection != null) {
                    sections.add(currentSection!!.copy(content = currentSection!!.content.trim()))
                }
                currentSection = ParsedSection(
                    sectionNumber = sectionMatch.first,
                    title = sectionMatch.second,
                    content = ""
                )
                buffer = StringBuilder()
            } else {
                if (currentSection != null) {
                    if (currentSection!!.title.isEmpty() && !trimmed.startsWith("(") && !trimmed.startsWith("[")) {
                        currentSection = currentSection!!.copy(title = trimmed)
                    } else {
                        if (buffer.isNotEmpty()) buffer.append("\n")
                        buffer.append(trimmed)
                    }
                }
            }
        }

        if (currentSection != null) {
            sections.add(currentSection!!.copy(content = buffer.toString().trim()))
        }

        return sections
    }

    private fun findSectionStart(line: String): Pair<String, String>? {
        for (pattern in patterns) {
            val match = pattern.find(line)
            if (match != null) {
                if (match.groupValues.size >= 3) {
                    val title = match.groupValues[2].trim().take(100)
                    return Pair(match.groupValues[1], title)
                }
                if (match.groupValues.size >= 2) {
                    return Pair(match.groupValues[1], "")
                }
            }
        }
        return null
    }

    fun extractKeywords(section: ParsedSection): List<String> {
        val keywords = mutableSetOf<String>()
        val stopWords = setOf("the", "a", "an", "in", "of", "to", "and", "or", "is", "are", "was", "were", "be", "been", "shall", "may", "any", "such", "said", "under", "with", "within", "without", "after", "before", "upon", "whoever", "which", "that", "this", "these", "those", "his", "her", "its", "their", "him", "them")

        val words = section.title.split("\\s+".toRegex()) +
                section.content.split("\\s+".toRegex())

        words
            .map { it.replace(Regex("[^a-zA-Zবাংলা]"), "").lowercase() }
            .filter { it.length > 3 && it !in stopWords }
            .distinct()
            .take(15)
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
