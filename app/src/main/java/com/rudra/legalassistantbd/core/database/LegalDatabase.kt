package com.rudra.legalassistantbd.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rudra.legalassistantbd.core.database.dao.*
import com.rudra.legalassistantbd.core.database.entity.*

@Database(
    entities = [
        LawEntity::class,
        LawSectionEntity::class,
        LawSectionFtsEntity::class,
        LawKeywordEntity::class,
        ProcedureEntity::class,
        CaseEntity::class,
        ClientEntity::class,
        EvidenceEntity::class,
        ReminderEntity::class,
        CaseProcedureProgressEntity::class,
        PdfImportEntity::class,
        HearingEntity::class,
        BailEntity::class,
        CaseDocumentEntity::class,
        CaseNoteEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class LegalDatabase : RoomDatabase() {
    abstract fun lawDao(): LawDao
    abstract fun lawSectionDao(): LawSectionDao
    abstract fun caseDao(): CaseDao
    abstract fun clientDao(): ClientDao
    abstract fun procedureDao(): ProcedureDao
    abstract fun reminderDao(): ReminderDao
    abstract fun evidenceDao(): EvidenceDao
    abstract fun caseProcedureProgressDao(): CaseProcedureProgressDao
    abstract fun pdfImportDao(): PdfImportDao
    abstract fun hearingDao(): HearingDao
    abstract fun bailDao(): BailDao
    abstract fun caseDocumentDao(): CaseDocumentDao
    abstract fun caseNoteDao(): CaseNoteDao

    companion object {
        const val DATABASE_NAME = "legal_assistant_bd.db"

        val MIGRATION_1_2 = Migration(1, 2) { db ->
            db.execSQL("ALTER TABLE law_sections ADD COLUMN isCustom INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE cases ADD COLUMN sectionId INTEGER DEFAULT NULL")
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS case_procedure_progress (
                    id INTEGER PRIMARY KEY NOT NULL,
                    caseId INTEGER NOT NULL,
                    procedureId INTEGER NOT NULL,
                    isCompleted INTEGER NOT NULL DEFAULT 0,
                    completedTimestamp INTEGER DEFAULT NULL,
                    notes TEXT DEFAULT NULL,
                    FOREIGN KEY (caseId) REFERENCES cases(id) ON DELETE CASCADE,
                    FOREIGN KEY (procedureId) REFERENCES procedures(id) ON DELETE CASCADE
                )
            """)
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_case_procedure_progress_caseId ON case_procedure_progress(caseId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_case_procedure_progress_procedureId ON case_procedure_progress(procedureId)")
        }

        val MIGRATION_2_3 = Migration(2, 3) { db ->
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS pdf_imports (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    file_name TEXT NOT NULL,
                    law_id INTEGER NOT NULL,
                    sections_count INTEGER NOT NULL DEFAULT 0,
                    imported_at INTEGER NOT NULL,
                    status TEXT NOT NULL DEFAULT 'completed'
                )
            """)
        }

        val MIGRATION_3_4 = Migration(3, 4) { db ->
            db.execSQL("ALTER TABLE cases ADD COLUMN opponentAdvocate TEXT DEFAULT NULL")
            db.execSQL("ALTER TABLE cases ADD COLUMN advocateName TEXT DEFAULT NULL")
            db.execSQL("ALTER TABLE cases ADD COLUMN advocatePhone TEXT DEFAULT NULL")
            db.execSQL("ALTER TABLE cases ADD COLUMN policeStation TEXT DEFAULT NULL")
            db.execSQL("ALTER TABLE cases ADD COLUMN firNumber TEXT DEFAULT NULL")
            db.execSQL("ALTER TABLE cases ADD COLUMN firDate INTEGER DEFAULT NULL")
            db.execSQL("ALTER TABLE cases ADD COLUMN filingNumber TEXT DEFAULT NULL")
            db.execSQL("ALTER TABLE cases ADD COLUMN caseYear TEXT DEFAULT NULL")
            db.execSQL("ALTER TABLE clients ADD COLUMN fatherName TEXT DEFAULT NULL")
            db.execSQL("ALTER TABLE clients ADD COLUMN occupation TEXT DEFAULT NULL")
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS hearings (
                    id INTEGER PRIMARY KEY NOT NULL,
                    caseId INTEGER NOT NULL,
                    hearingDate INTEGER NOT NULL,
                    hearingType TEXT NOT NULL DEFAULT 'Regular Hearing',
                    courtName TEXT DEFAULT NULL,
                    judgeName TEXT DEFAULT NULL,
                    description TEXT DEFAULT NULL,
                    outcome TEXT DEFAULT NULL,
                    nextHearingDate INTEGER DEFAULT NULL,
                    notes TEXT DEFAULT NULL,
                    createdTimestamp INTEGER NOT NULL DEFAULT 0,
                    FOREIGN KEY (caseId) REFERENCES cases(id) ON DELETE CASCADE
                )
            """)
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_hearings_caseId ON hearings(caseId)")
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS bails (
                    id INTEGER PRIMARY KEY NOT NULL,
                    caseId INTEGER NOT NULL,
                    bailType TEXT NOT NULL DEFAULT 'Regular',
                    petitionDate INTEGER NOT NULL,
                    petitionNumber TEXT DEFAULT NULL,
                    bailStatus TEXT NOT NULL DEFAULT 'Pending',
                    courtName TEXT DEFAULT NULL,
                    hearingDate INTEGER DEFAULT NULL,
                    orderDate INTEGER DEFAULT NULL,
                    orderDetails TEXT DEFAULT NULL,
                    suretyDetails TEXT DEFAULT NULL,
                    bailAmount TEXT DEFAULT NULL,
                    createdTimestamp INTEGER NOT NULL DEFAULT 0,
                    updatedTimestamp INTEGER NOT NULL DEFAULT 0,
                    FOREIGN KEY (caseId) REFERENCES cases(id) ON DELETE CASCADE
                )
            """)
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_bails_caseId ON bails(caseId)")
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS case_documents (
                    id INTEGER PRIMARY KEY NOT NULL,
                    caseId INTEGER NOT NULL,
                    title TEXT NOT NULL,
                    description TEXT DEFAULT NULL,
                    filePath TEXT NOT NULL,
                    documentType TEXT NOT NULL DEFAULT 'Document',
                    mimeType TEXT DEFAULT NULL,
                    fileSize INTEGER DEFAULT NULL,
                    addedTimestamp INTEGER NOT NULL DEFAULT 0,
                    FOREIGN KEY (caseId) REFERENCES cases(id) ON DELETE CASCADE
                )
            """)
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_case_documents_caseId ON case_documents(caseId)")
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS case_notes (
                    id INTEGER PRIMARY KEY NOT NULL,
                    caseId INTEGER NOT NULL,
                    content TEXT NOT NULL,
                    addedTimestamp INTEGER NOT NULL DEFAULT 0,
                    FOREIGN KEY (caseId) REFERENCES cases(id) ON DELETE CASCADE
                )
            """)
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_case_notes_caseId ON case_notes(caseId)")
        }
    }
}
