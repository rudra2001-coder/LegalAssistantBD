package com.rudra.legalassistantbd.laws

import com.rudra.legalassistantbd.core.database.entity.LawEntity
import com.rudra.legalassistantbd.core.database.entity.LawSectionEntity
import com.rudra.legalassistantbd.core.database.entity.ProcedureEntity

object LawDataProvider {

    fun getDefaultLaws(): List<LawEntity> {
        return listOf(
            LawEntity(1, "Penal Code, 1860", "দণ্ডবিধি, ১৮৬০", "Penal Code", 1860, "The Penal Code of Bangladesh is the main criminal code covering substantive criminal law.", true),
            LawEntity(2, "Code of Criminal Procedure, 1898", "ফৌজদারি কার্যবিধি, ১৮৯৮", "CrPC", 1898, "The Code of Criminal Procedure regulates the procedure for criminal trials in Bangladesh.", true),
            LawEntity(3, "Code of Civil Procedure, 1908", "দেওয়ানী কার্যবিধি, ১৯০৮", "CPC", 1908, "The Code of Civil Procedure regulates civil court proceedings in Bangladesh.", true),
            LawEntity(4, "Evidence Act, 1872", "সাক্ষ্য আইন, ১৮৭২", "Evidence Act", 1872, "The Evidence Act defines the rules of evidence applicable in Bangladeshi courts.", true),
            LawEntity(5, "Digital Security Act, 2018", "ডিজিটাল নিরাপত্তা আইন, ২০১৮", "Digital Security Act", 2018, "The Digital Security Act addresses cyber crimes and digital security in Bangladesh.", true),
            LawEntity(6, "Contract Act, 1872", "চুক্তি আইন, ১৮৭২", "Contract Act", 1872, "The Contract Act governs contract law in Bangladesh.", true),
            LawEntity(7, "Family Courts Ordinance, 1985", "পারিবারিক আদালত অধ্যাদেশ, ১৯৮৫", "Family Courts Ordinance", 1985, "The Family Courts Ordinance establishes family courts for resolving family disputes.", true),
            LawEntity(8, "Labour Act, 2006", "শ্রম আইন, ২০০৬", "Labour Act", 2006, "The Labour Act governs employment and labor rights in Bangladesh.", true)
        )
    }

    fun getDefaultSections(): List<LawSectionEntity> {
        return listOf(
            // Penal Code sections
            LawSectionEntity(101, 1, "302", "Punishment for murder", "খুনের শাস্তি", "Whoever commits murder shall be punished with death, or imprisonment for life, and shall also be liable to fine.", "যে ব্যক্তি খুন করে, সে মৃত্যুদণ্ড বা যাবজ্জীবন কারাদণ্ডে দণ্ডিত হবে এবং জরিমানাও দেবে।", "High Court / Sessions Court", "Non-bailable", "Death or Life Imprisonment + Fine", false, false, "Non-compoundable", 1),
            LawSectionEntity(102, 1, "379", "Punishment for theft", "চুরির শাস্তি", "Whoever commits theft shall be punished with imprisonment of either description for a term which may extend to three years, or with fine, or with both.", "যে ব্যক্তি চুরি করে, সে তিন বছর পর্যন্ত কারাদণ্ড বা জরিমানা বা উভয়দণ্ডে দণ্ডিত হবে।", "Magistrate Court", "Bailable", "Up to 3 years imprisonment + Fine", true, true, "Compoundable", 2),
            LawSectionEntity(103, 1, "307", "Attempt to murder", "খুনের চেষ্টা", "Whoever does any act with such intention or knowledge, and under such circumstances that, if he by that act caused death, he would be guilty of murder, shall be punished with imprisonment of either description for a term which may extend to ten years, and shall also be liable to fine.", "যে ব্যক্তি এমন উদ্দেশ্যে বা জ্ঞাতসারে কোনো কাজ করে যে, যদি সেই কাজের ফলে মৃত্যু ঘটে তবে তা খুন হবে, সে দশ বছর পর্যন্ত কারাদণ্ডে দণ্ডিত হবে এবং জরিমানাও দেবে।", "Sessions Court", "Non-bailable", "Up to 10 years + Fine", false, false, "Non-compoundable", 3),
            LawSectionEntity(104, 1, "323", "Punishment for voluntarily causing hurt", "আঘাতের শাস্তি", "Whoever, except in the case provided for by section 334, voluntarily causes hurt, shall be punished with imprisonment of either description for a term which may extend to one year, or with fine which may extend to one thousand taka, or with both.", "যে ব্যক্তি স্বেচ্ছায় আঘাত করে, সে এক বছর পর্যন্ত কারাদণ্ড বা এক হাজার টাকা পর্যন্ত জরিমানা বা উভয়দণ্ডে দণ্ডিত হবে।", "Magistrate Court", "Bailable", "Up to 1 year + Fine", true, true, "Compoundable", 4),
            LawSectionEntity(105, 1, "420", "Cheating and dishonestly inducing delivery of property", "প্রতারণা", "Whoever cheats and thereby dishonestly induces the person deceived to deliver any property to any person, or to make, alter or destroy the whole or any part of a valuable security, shall be punished with imprisonment of either description for a term which may extend to seven years, and shall also be liable to fine.", "যে ব্যক্তি প্রতারণা করে এবং এর মাধ্যমে প্রতারিত ব্যক্তিকে কোনো সম্পত্তি প্রদান করতে বা কোনো মূল্যবান দলিল তৈরি, পরিবর্তন বা ধ্বংস করতে প্ররোচিত করে, সে সাত বছর পর্যন্ত কারাদণ্ডে দণ্ডিত হবে এবং জরিমানাও দেবে।", "Magistrate Court", "Bailable", "Up to 7 years + Fine", true, true, "Compoundable", 5),
            LawSectionEntity(106, 1, "326", "Voluntarily causing grievous hurt by dangerous weapons", "মারাত্মক অস্ত্র দ্বারা আঘাত", "Whoever, except in the case provided for by section 335, voluntarily causes grievous hurt by means of any instrument for shooting, stabbing or cutting, or any instrument which, used as a weapon of offence, is likely to cause death, shall be punished with imprisonment for life, or with imprisonment of either description for a term which may extend to ten years, and shall also be liable to fine.", "যে ব্যক্তি গুলি, ছুরিকাঘাত বা কাটার যন্ত্র বা আক্রমণের অস্ত্র হিসেবে ব্যবহৃত কোনো যন্ত্র দ্বারা স্বেচ্ছায় গুরুতর আঘাত করে, সে যাবজ্জীবন বা দশ বছর পর্যন্ত কারাদণ্ডে দণ্ডিত হবে এবং জরিমানাও দেবে।", "Sessions Court", "Non-bailable", "Up to Life Imprisonment + Fine", false, false, "Non-compoundable", 6),

            // CrPC sections
            LawSectionEntity(201, 2, "154", "Information in cognizable cases", "জ্ঞাতব্য মামলায় তথ্য", "Every information relating to the commission of a cognizable offence, if given orally to an officer in charge of a police station, shall be reduced to writing by him or under his direction, and be read over to the informant.", "জ্ঞাতব্য অপরাধ সম্পর্কে কোনো তথ্য যদি কোনো থানার ভারপ্রাপ্ত কর্মকর্তাকে মৌখিকভাবে দেওয়া হয়, তবে তিনি তা লিখিত আকারে নেবেন এবং তথ্যদাতাকে শুনিয়ে দেবেন।", "Police Station", null, null, false, false, null, 1),
            LawSectionEntity(202, 2, "496", "Grant of bail in bailable offences", "জামিনযোগ্য অপরাধে জামিন", "When any person other than a person accused of a non-bailable offence is arrested or detained without warrant by an officer in charge of a police station, or appears or is brought before a Court, and is prepared at any time while in the custody of such officer or at any stage of the proceeding before such Court to give bail, such person shall be released on bail.", "যখন কোনো ব্যক্তি, যে অজামিনযোগ্য অপরাধের অভিযুক্ত নয়, গ্রেপ্তার হয় বা আদালতে হাজির হয় এবং জামিন দিতে প্রস্তুত থাকে, তখন তাকে জামিনে মুক্তি দিতে হবে।", "Magistrate Court", "Bailable", "Bail as right", true, true, null, 2),

            // Evidence Act sections
            LawSectionEntity(301, 4, "3", "Interpretation clause", "ব্যাখ্যা ধারা", "'Evidence' means and includes all statements which the Court permits or requires to be made before it by witnesses, in relation to matters of fact under inquiry; and all documents produced for the inspection of the Court.", "'সাক্ষ্য' বলতে বোঝায় এবং অন্তর্ভুক্ত করে সেই সকল বিবৃতি যা আদালত সাক্ষীদের দ্বারা তার সামনে পেশ করার অনুমতি বা নির্দেশ দেয়, এবং আদালতের পরিদর্শনের জন্য পেশ করা সকল দলিল।", null, null, null, false, false, null, 1),
            LawSectionEntity(302, 4, "9", "Facts necessary to explain or introduce relevant facts", "প্রাসঙ্গিক事实 ব্যাখ্যার জন্য প্রয়োজনীয়事实", "Facts necessary to explain or introduce a fact in issue or relevant fact, or which support or rebut an inference suggested by a fact in issue or relevant fact, or which establish the identity of any thing or person, or which fix the time or place at which any fact in issue or relevant fact happened, or which show the relation of parties by whom any such fact was transacted, are relevant.", "বিবাদ্যমান事实 বা প্রাসঙ্গিক事实 ব্যাখ্যা বা উপস্থাপনের জন্য প্রয়োজনীয়事实, বা যা বিবাদ্যমান事实 বা প্রাসঙ্গিক事实 দ্বারা প্রস্তাবিত অনুমান সমর্থন বা খণ্ডন করে, প্রাসঙ্গিক।", null, null, null, false, false, null, 2)
        )
    }

    fun getDefaultProcedures(): List<ProcedureEntity> {
        return listOf(
            // Procedure for Section 379 (Theft)
            ProcedureEntity(1, 102, 1, "File FIR", "এফআইআর দায়ের", "Go to the nearest police station and file a First Information Report (FIR) under Section 154 CrPC.", "NID card, incident details", "1 day", "Police Station"),
            ProcedureEntity(2, 102, 2, "Police Investigation", "পুলিশ তদন্ত", "Police will investigate the case, collect evidence, and record witness statements.", "FIR copy", "7-60 days", "Police Station"),
            ProcedureEntity(3, 102, 3, "Arrest / Summons", "গ্রেপ্তার / সমন", "Based on evidence, police may arrest the accused or issue summons.", "Warrant/Summons", "1-7 days", null),
            ProcedureEntity(4, 102, 4, "Charge Sheet", "চার্জশিট দাখিল", "Police submits charge sheet to the Magistrate Court if sufficient evidence found.", "Charge sheet, evidence list", "7-30 days", "Magistrate Court"),
            ProcedureEntity(5, 102, 5, "Trial", "বিচার", "Magistrate Court conducts trial. Accused can apply for bail as it is a bailable offence.", "Case file, witnesses", "30-180 days", "Magistrate Court"),
            ProcedureEntity(6, 102, 6, "Judgment", "রায়", "Court delivers judgment. If found guilty, punishment as per Section 379.", "আদালত রায় প্রদান করে। দোষী সাব্যস্ত হলে ধারা ৩৭৯ অনুযায়ী শাস্তি নির্ধারিত হয়।", null, null, "Magistrate Court"),

            // Procedure for Section 302 (Murder)
            ProcedureEntity(7, 101, 1, "File FIR", "এফআইআর দায়ের", "Immediately go to the nearest police station and file FIR.", "NID card, incident details", "Same day", "Police Station"),
            ProcedureEntity(8, 101, 2, "Police Investigation", "পুলিশ তদন্ত", "Police investigate, collect forensic evidence, and record witness statements.", "FIR copy, death certificate", "7-90 days", "Police Station"),
            ProcedureEntity(9, 101, 3, "Arrest", "গ্রেপ্তার", "Accused is arrested. Bail is not applicable as it is a non-bailable offence.", "Arrest warrant", "Immediate", null),
            ProcedureEntity(10, 101, 4, "Charge Sheet", "চার্জশিট", "Police submits charge sheet to Sessions Court.", "Charge sheet, forensic reports", "30-90 days", "Sessions Court"),
            ProcedureEntity(11, 101, 5, "Trial in Sessions Court", "সেশনস আদালতে বিচার", "Trial conducted in Sessions Court. Includes examination of witnesses and arguments.", "Case file, witnesses, exhibits", "90-365 days", "Sessions Court"),
            ProcedureEntity(12, 101, 6, "Judgment", "রায়", "Court delivers judgment. Conviction can lead to death sentence or life imprisonment.", "আদালত রায় প্রদান করে। দোষী সাব্যস্ত হলে মৃত্যুদণ্ড বা যাবজ্জীবন কারাদণ্ড হতে পারে।", null, null, "Sessions Court"),

            // Procedure for Section 420 (Cheating)
            ProcedureEntity(13, 105, 1, "File Complaint/FIR", "অভিযোগ/এফআইআর দায়ের", "File a complaint or FIR at the police station or directly before the Magistrate.", "Complaint, evidence of cheating", "1-3 days", "Police/Magistrate Court"),
            ProcedureEntity(14, 105, 2, "Investigation", "তদন্ত", "Police investigates the fraud/cheating allegations.", "Documents, evidence", "7-60 days", "Police Station"),
            ProcedureEntity(15, 105, 3, "Charge Sheet", "চার্জশিট", "Charge sheet filed if evidence supports the allegations.", "Charge sheet", "15-30 days", "Magistrate Court"),
            ProcedureEntity(16, 105, 4, "Trial", "বিচার", "Trial in Magistrate Court. Bailable offence.", "Case file", "30-180 days", "Magistrate Court")
        )
    }
}
