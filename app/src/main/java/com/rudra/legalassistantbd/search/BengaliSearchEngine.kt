package com.rudra.legalassistantbd.search

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BengaliSearchEngine @Inject constructor() {

    private val synonymMap = mapOf(
        "চুরি" to listOf("theft", "stealing", "চুরি", "চৌর্য"),
        "খুন" to listOf("murder", "homicide", "kill", "খুন", "হত্যা"),
        "ধর্ষণ" to listOf("rape", "sexual assault", "ধর্ষণ", "যৌন নির্যাতন"),
        "জালিয়াতি" to listOf("forgery", "fraud", "জালিয়াতি", "প্রতারণা"),
        "ডাকাতি" to listOf("robbery", "dacoity", "ডাকাতি", "দস্যুতা"),
        "মারামারি" to listOf("assault", "fight", "grievous hurt", "মারামারি", "আঘাত"),
        "চুক্তি" to listOf("contract", "agreement", "চুক্তি", "ঠিকা"),
        "বিবাহ" to listOf("marriage", "divorce", "বিবাহ", "তালাক"),
        "সম্পত্তি" to listOf("property", "land", "ownership", "সম্পত্তি", "জমি"),
        "সাক্ষ্য" to listOf("evidence", "witness", "testimony", "সাক্ষ্য", "প্রমাণ")
    )

    fun getSynonyms(word: String): List<String> {
        val lower = word.lowercase().trim()
        val directMatch = synonymMap.entries.find { (key, _) ->
            key == lower || key.contains(lower) || lower.contains(key)
        }?.value
        return directMatch ?: listOf(lower)
    }

    fun buildFtsQuery(query: String): String {
        val parts = query.trim().split("\\s+".toRegex())
        val expanded = parts.flatMap { part ->
            val synonyms = getSynonyms(part)
            if (synonyms.size > 1) {
                synonyms.map { "\"$it\"" }
            } else {
                listOf("\"${synonyms.first()}\"")
            }
        }
        return expanded.joinToString(" OR ")
    }

    fun isBengali(text: String): Boolean {
        return text.any { it in '\u0980'..'\u09FF' }
    }
}
