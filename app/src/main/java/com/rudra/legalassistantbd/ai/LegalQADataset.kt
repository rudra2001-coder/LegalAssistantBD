package com.rudra.legalassistantbd.ai

data class QAPair(
    val question: String,
    val answer: String,
    val language: String = "bn",
    val relatedSections: List<String> = emptyList()
)

object LegalQADataset {
    val questions = listOf(
        QAPair(
            question = "চুরির শাস্তি কি?",
            answer = "চুরির শাস্তি দণ্ডবিধির ৩৭৯ ধারায় বর্ণিত হয়েছে। চুরির শাস্তি ৩ বছরের কারাদণ্ড বা জরিমানা অথবা উভয়দণ্ড হতে পারে। এটি জামিনযোগ্য অপরাধ।",
            language = "bn",
            relatedSections = listOf("379")
        ),
        QAPair(
            question = "খুনের শাস্তি কি?",
            answer = "খুনের শাস্তি দণ্ডবিধির ৩০২ ধারায় বর্ণিত হয়েছে। খুনের শাস্তি মৃত্যুদণ্ড বা যাবজ্জীবন কারাদণ্ড এবং জরিমানা। এটি অজামিনযোগ্য অপরাধ।",
            language = "bn",
            relatedSections = listOf("302")
        ),
        QAPair(
            question = "জামিন কি?",
            answer = "জামিন হলো আসামির অস্থায়ী মুক্তি। এটি দুই প্রকার: জামিনযোগ্য অপরাধে আসামী অধিকার হিসাবে জামিন পেতে পারে, আর অজামিনযোগ্য অপরাধে আদালতের বিবেচনায় জামিন দেওয়া হয়। ফৌজদারি কার্যবিধির ৪৯৬-৫০২ ধারায় জামিন সংক্রান্ত বিধান রয়েছে।",
            language = "bn",
            relatedSections = listOf("496", "497", "498", "499", "500", "501", "502")
        ),
        QAPair(
            question = "এফআইআর কিভাবে করব?",
            answer = "এফআইআর (FIR) করার জন্য নিকটস্থ থানায় গিয়ে ঘটনার বিবরণ লিখিতভাবে দিতে হবে। পুলিশ বিনামূল্যে এফআইআর রেজিস্টার করবে। ফৌজদারি কার্যবিধির ১৫৪ ধারায় এফআইআর সংক্রান্ত বিধান রয়েছে। এফআইআর করার ২৪ ঘন্টার মধ্যে পুলিশকে একটি কপি দিতে বাধ্য।",
            language = "bn",
            relatedSections = listOf("154")
        ),
        QAPair(
            question = "What is the punishment for theft?",
            answer = "The punishment for theft is described in Section 379 of the Penal Code. Theft is punishable with imprisonment of up to 3 years or fine or both. It is a bailable offense.",
            language = "en",
            relatedSections = listOf("379")
        ),
        QAPair(
            question = "মামলা দায়েরের প্রক্রিয়া কি?",
            answer = "মামলা দায়েরের প্রক্রিয়া: ১) এফআইআর দায়ের, ২) পুলিশ তদন্ত, ৩) চার্জশিট দাখিল, ৪) বিচার শুরু, ৫) সাক্ষ্য গ্রহণ, ৬) যুক্তিতর্ক, ৭) রায়। ফৌজদারি কার্যবিধি ও সাক্ষ্য আইন অনুযায়ী এই প্রক্রিয়া পরিচালিত হয়।",
            language = "bn",
            relatedSections = listOf("154", "155", "156", "157", "158")
        ),
        QAPair(
            question = "সিভিল মামলা ও ক্রিমিনাল মামলার পার্থক্য কি?",
            answer = "সিভিল মামলা ব্যক্তিগত অধিকার সংক্রান্ত (যেমন: সম্পত্তি, চুক্তি) এবং ক্রিমিনাল মামলা অপরাধ সংক্রান্ত। সিভিল মামলায় প্রতিকার হিসেবে ক্ষতিপূরণ দেওয়া হয়, আর ক্রিমিনাল মামলায় শাস্তি দেওয়া হয়। দেউয়ানী কার্যবিধি ও ফৌজদারি কার্যবিধি যথাক্রমে এই দুটি প্রক্রিয়া নিয়ন্ত্রণ করে।",
            language = "bn",
            relatedSections = listOf()
        ),
        QAPair(
            question = "ডিজিটাল নিরাপত্তা আইন কি?",
            answer = "ডিজিটাল নিরাপত্তা আইন, ২০১৮ বাংলাদেশের সাইবার অপরাধ সংক্রান্ত আইন। এই আইনে ডিজিটাল平台上 অপরাধমূলক কাজের শাস্তি নির্ধারণ করা হয়েছে। এতে মানহানি, হ্যাকিং, আইডি চুরি ইত্যাদি অপরাধের বিধান রয়েছে। সর্বোচ্চ শাস্তি যাবজ্জীবন কারাদণ্ড।",
            language = "bn",
            relatedSections = listOf()
        ),
        QAPair(
            question = "তালাকের আইনি প্রক্রিয়া কি?",
            answer = "মুসলিম পারিবারিক আইন অনুযায়ী তালাকের প্রক্রিয়া: ১) তালাক ঘোষণা, ২) সালিশি পরিষদে নোটিশ, ৩) ৯০ দিনের অপেক্ষা সময় (ইদ্দত), ৪) সালিশি বৈঠক, ৫) তালাক কার্যকর। পারিবারিক আদালত অধ্যাদেশ, ১৯৮৫ অনুযায়ী এই প্রক্রিয়া পরিচালিত হয়।",
            language = "bn",
            relatedSections = listOf()
        ),
        QAPair(
            question = "সাক্ষ্য আইন কি?",
            answer = "সাক্ষ্য আইন, ১৮৭২ বাংলাদেশের সাক্ষ্য সংক্রান্ত প্রধান আইন। এতে সাক্ষ্যের প্রকারভেদ, গ্রহণযোগ্যতা, এবং প্রাসঙ্গিকতা সম্পর্কে বিধান রয়েছে। এই আইনে ৩ ভাগ: প্রাসঙ্গিকতা, প্রমাণ, এবং সাক্ষ্য প্রদান। মোট ১৬৭টি ধারা রয়েছে।",
            language = "bn",
            relatedSections = listOf()
        )
    )

    fun findAnswer(query: String): QAPair? {
        val lower = query.lowercase()
        return questions.find { qa ->
            qa.question.lowercase().contains(lower) || lower.contains(qa.question.lowercase())
        }
    }

    fun searchRelated(query: String): List<QAPair> {
        val lower = query.lowercase()
        return questions.filter { qa ->
            qa.question.lowercase().contains(lower) ||
            qa.answer.lowercase().contains(lower) ||
            lower.contains(qa.question.lowercase().take(10))
        }
    }
}
