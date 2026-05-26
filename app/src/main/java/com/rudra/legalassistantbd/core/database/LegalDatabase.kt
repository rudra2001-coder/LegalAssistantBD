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
        PdfImportEntity::class
    ],
    version = 3,
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
    }
}
