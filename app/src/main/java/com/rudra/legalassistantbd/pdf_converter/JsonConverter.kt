package com.rudra.legalassistantbd.pdf_converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton

data class LawJsonModel(
    val section: String,
    val title: String,
    val content: String,
    val keywords: List<String> = emptyList(),
    val court_type: String? = null,
    val bail: String? = null,
    val punishment: String? = null
)

@Singleton
class JsonConverter @Inject constructor(
    private val gson: Gson
) {
    fun convertToJson(sections: List<ParsedSection>, lawName: String = ""): String {
        val lawModels = sections.map { section ->
            LawJsonModel(
                section = section.sectionNumber,
                title = section.title,
                content = section.content.trim(),
                keywords = section.keywords
            )
        }
        return gson.toJson(mapOf("law" to lawName, "sections" to lawModels))
    }

    fun parseJson(json: String): List<LawJsonModel> {
        return try {
            val type = object : TypeToken<List<LawJsonModel>>() {}.type
            gson.fromJson<List<LawJsonModel>>(json, type)
        } catch (e: Exception) {
            try {
                val mapType = object : TypeToken<Map<String, Any>>() {}.type
                val map = gson.fromJson<Map<String, Any>>(json, mapType)
                val sectionsJson = gson.toJson(map["sections"])
                val type2 = object : TypeToken<List<LawJsonModel>>() {}.type
                gson.fromJson<List<LawJsonModel>>(sectionsJson, type2)
            } catch (e2: Exception) {
                emptyList()
            }
        }
    }

    fun validateJson(json: String): Boolean {
        return try {
            val sections = parseJson(json)
            sections.isNotEmpty() && sections.all { it.section.isNotBlank() }
        } catch (e: Exception) {
            false
        }
    }
}
