package com.rudra.legalassistantbd.documents

data class Template(
    val id: String,
    val name: String,
    val type: String,
    val content: String,
    val placeholders: List<String>
)

object DocumentTemplates {
    val templates = listOf(
        Template(
            id = "fir",
            name = "FIR Draft",
            type = "Criminal",
            content = """
FIR (First Information Report) Draft
=====================================

To,
The Officer-in-Charge,
${"${policeStation}"} Police Station
District: ${"${district}"}

Subject: First Information Report regarding ${"${incident}"}

Sir/Madam,

I, ${"${complainantName}"}, son/daughter of ${"${fatherName}"}, residing at ${"${address}"}, do hereby lodge this First Information Report.

1. Date and Time of Incident: ${"${incidentDate}"} at ${"${incidentTime}"}
2. Location of Incident: ${"${incidentLocation}"}
3. Description of Incident: ${"${incidentDescription}"}

4. Accused Person(s):
   ${"${accusedDetails}"}

5. Witnesses:
   ${"${witnesses}"}

6. Property Loss/Damage: ${"${propertyLoss}"}

Therefore, I pray that necessary legal action be taken against the accused person(s) as per law.

Sincerely,

${"${complainantName}"}
Signature: _______________
Date: ${"${currentDate}"}
            """.trimIndent(),
            placeholders = listOf("policeStation", "district", "incident", "complainantName", "fatherName", "address", "incidentDate", "incidentTime", "incidentLocation", "incidentDescription", "accusedDetails", "witnesses", "propertyLoss", "currentDate")
        ),
        Template(
            id = "legal_notice",
            name = "Legal Notice",
            type = "Civil",
            content = """
LEGAL NOTICE
=============

Date: ${"${currentDate}"}

To,
${"${recipientName}"}
${"${recipientAddress}"}

From,
${"${senderName}"}
${"${senderAddress}"}

Subject: Legal Notice regarding ${"${subject}"}

Dear Sir/Madam,

We are writing on behalf of our client, ${"${senderName}"}, to serve you with the following legal notice:

1. ${"${noticeContent}"}

2. Our client has instructed us to demand that you:
   ${"${demand}"}

3. If you fail to comply with the above demand within ${"${deadlineDays}"} days from the receipt of this notice, our client shall be constrained to initiate appropriate legal proceedings against you, and you shall be held liable for all costs and consequences thereof.

This notice is being sent without prejudice to our client's rights and contentions.

Sincerely,

${"${lawyerName}"}
Advocate
${"${lawFirm}"}
            """.trimIndent(),
            placeholders = listOf("currentDate", "recipientName", "recipientAddress", "senderName", "senderAddress", "subject", "noticeContent", "demand", "deadlineDays", "lawyerName", "lawFirm")
        ),
        Template(
            id = "affidavit",
            name = "Affidavit",
            type = "General",
            content = """
AFFIDAVIT
==========

I, ${"${deponentName}"}, son/daughter of ${"${fatherName}"}, aged about ${"${age}"} years, by faith ${"${religion}"}, by nationality Bangladeshi, by occupation ${"${occupation}"}, residing at ${"${address}"}, do hereby solemnly affirm and declare as follows:

1. ${"${declaration1}"}

2. ${"${declaration2}"}

3. ${"${declaration3}"}

4. I make this affidavit knowing the contents thereof to be true and correct.

Deponent

Verified and signed before me on this ${"${currentDate}"}.

Commissioner of Oaths / Notary Public
            """.trimIndent(),
            placeholders = listOf("deponentName", "fatherName", "age", "religion", "occupation", "address", "declaration1", "declaration2", "declaration3", "currentDate")
        ),
        Template(
            id = "bail_petition",
            name = "Bail Petition",
            type = "Criminal",
            content = """
BAIL PETITION
=============

IN THE COURT OF ${"${courtName}"}
CASE NO: ${"${caseNumber}"}

IN THE MATTER OF:
${"${applyingPerson}"} ------------------------------------------- Petitioner
Vs.
The State ------------------------------------------------------- Opposite Party

PETITION FOR BAIL UNDER SECTION ${"${bailSection}"} OF THE CODE OF CRIMINAL PROCEDURE, 1898

Most Respectfully Sheweth:

1. That the petitioner is ${"${relationship}"} of the accused ${"${accusedName}"}, who has been arrested in connection with the above-mentioned case.

2. That the accused has been falsely implicated in the said case and has not committed any offence as alleged.

3. That the accused is a permanent resident of ${"${address}"} and has no previous criminal record.

4. That the accused undertakes to abide by any terms and conditions that this Hon'ble Court may impose.

5. That the accused is ready and willing to furnish adequate surety.

Wherefore, it is prayed that your Honor may be graciously pleased to grant bail to the accused on such terms and conditions as your Honor may deem fit and proper.

And for this act of kindness, the petitioner as in duty bound shall ever pray.

Counsel for the Petitioner
            """.trimIndent(),
            placeholders = listOf("courtName", "caseNumber", "applyingPerson", "bailSection", "relationship", "accusedName", "address")
        )
    )

    fun getTemplateById(id: String): Template? = templates.find { it.id == id }
    fun getTemplatesByType(type: String): List<Template> = templates.filter { it.type == type }
}
