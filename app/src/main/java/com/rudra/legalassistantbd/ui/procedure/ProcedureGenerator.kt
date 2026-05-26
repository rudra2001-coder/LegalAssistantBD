package com.rudra.legalassistantbd.ui.procedure

import com.rudra.legalassistantbd.core.database.entity.LawSectionEntity
import com.rudra.legalassistantbd.core.database.entity.ProcedureEntity
import com.rudra.legalassistantbd.data.repository.LawRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class ProcedureGenerator @Inject constructor(
    private val lawRepository: LawRepository
) {
    suspend fun generateBasicProcedures(lawId: Int, sections: List<LawSectionEntity>) {
        val procedures = mutableListOf<ProcedureEntity>()
        var idCursor = System.currentTimeMillis().toInt()

        for (section in sections) {
            val sectionNum = section.sectionNumber.trim()

            val filingSteps = createFilingProcedures(idCursor, section.id, sectionNum)
            procedures.addAll(filingSteps)
            idCursor += filingSteps.size

            val hearingSteps = createHearingProcedures(idCursor, section.id)
            procedures.addAll(hearingSteps)
            idCursor += hearingSteps.size

            val postSteps = createPostHearingProcedures(idCursor, section.id)
            procedures.addAll(postSteps)
            idCursor += postSteps.size
        }

        if (procedures.isNotEmpty()) {
            lawRepository.insertProcedures(procedures)
        }
    }

    private fun createFilingProcedures(
        startId: Int,
        sectionId: Int,
        sectionNumber: String
    ): List<ProcedureEntity> {
        return listOf(
            ProcedureEntity(
                id = startId,
                sectionId = sectionId,
                stepNumber = 1,
                titleEn = "Identify the Applicable Court",
                titleBn = "প্রযোজ্য আদালত চিহ্নিত করুন",
                descriptionEn = "Determine whether this case falls under Magistrate Court, Sessions Court, or High Court based on the section and jurisdiction.",
                descriptionBn = "ধারা ও এখতিয়ারের ভিত্তিতে এই মামলাটি ম্যাজিস্ট্রেট আদালত, সেশন আদালত নাকি হাইকোর্টে পড়ে তা নির্ধারণ করুন।"
            ),
            ProcedureEntity(
                id = startId + 1,
                sectionId = sectionId,
                stepNumber = 2,
                titleEn = "File the Complaint / FIR",
                titleBn = "অভিযোগ / এফআইআর দায়ের করুন",
                descriptionEn = "Draft and file the complaint or FIR with the relevant police station or court registry.",
                descriptionBn = "সংশ্লিষ্ট থানা বা আদালত নিবন্ধনে অভিযোগ বা এফআইআর খসড়া তৈরি করে দায়ের করুন।"
            ),
            ProcedureEntity(
                id = startId + 2,
                sectionId = sectionId,
                stepNumber = 3,
                titleEn = "Collect Evidence & Documents",
                titleBn = "প্রমাণ ও নথি সংগ্রহ করুন",
                descriptionEn = "Gather all supporting documents, witness statements, and material evidence relevant to Section $sectionNumber.",
                descriptionBn = "$sectionNumber ধারা সম্পর্কিত সমস্ত সহায়ক নথি, সাক্ষীর বিবৃতি ও বস্তুগত প্রমাণ সংগ্রহ করুন।"
            )
        )
    }

    private fun createHearingProcedures(startId: Int, sectionId: Int): List<ProcedureEntity> {
        return listOf(
            ProcedureEntity(
                id = startId,
                sectionId = sectionId,
                stepNumber = 4,
                titleEn = "Attend First Hearing",
                titleBn = "প্রথম শুনানিতে উপস্থিত হোন",
                descriptionEn = "Appear before the court on the scheduled date. The court will review the complaint and set further dates.",
                descriptionBn = "নির্ধারিত তারিখে আদালতে উপস্থিত হোন। আদালত অভিযোগ পর্যালোচনা করে পরবর্তী তারিখ নির্ধারণ করবেন।"
            ),
            ProcedureEntity(
                id = startId + 1,
                sectionId = sectionId,
                stepNumber = 5,
                titleEn = "Submit Arguments & Evidence",
                titleBn = "যুক্তি ও প্রমাণ পেশ করুন",
                descriptionEn = "Present oral arguments and submit all collected evidence before the court in the prescribed format.",
                descriptionBn = "মৌখিক যুক্তি উপস্থাপন করুন এবং নির্ধারিত ফরম্যাটে আদালতে সমস্ত সংগ্রহ করা প্রমাণ জমা দিন।"
            )
        )
    }

    private fun createPostHearingProcedures(startId: Int, sectionId: Int): List<ProcedureEntity> {
        return listOf(
            ProcedureEntity(
                id = startId,
                sectionId = sectionId,
                stepNumber = 6,
                titleEn = "Obtain Court Order / Judgment",
                titleBn = "আদালতের আদেশ / রায় সংগ্রহ করুন",
                descriptionEn = "Receive the certified copy of the court order or judgment after the final hearing.",
                descriptionBn = "চূড়ান্ত শুনানির পর আদালতের আদেশ বা রায়ের সত্যায়িত কপি গ্রহণ করুন।"
            ),
            ProcedureEntity(
                id = startId + 1,
                sectionId = sectionId,
                stepNumber = 7,
                titleEn = "File Appeal (if needed)",
                titleBn = "আপিল দায়ের করুন (যদি প্রয়োজন হয়)",
                descriptionEn = "If dissatisfied with the judgment, file an appeal with the appropriate appellate court within the limitation period.",
                descriptionBn = "রায়ের সাথে অসন্তুষ্ট হলে, সীমিত সময়ের মধ্যে উপযুক্ত আপিল আদালতে আপিল দায়ের করুন।"
            )
        )
    }
}
