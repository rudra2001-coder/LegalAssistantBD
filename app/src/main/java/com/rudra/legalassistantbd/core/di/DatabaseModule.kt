package com.rudra.legalassistantbd.core.di

import android.content.Context
import androidx.room.Room
import com.rudra.legalassistantbd.core.database.LegalDatabase
import com.rudra.legalassistantbd.core.database.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LegalDatabase {
        return Room.databaseBuilder(
            context,
            LegalDatabase::class.java,
            LegalDatabase.DATABASE_NAME
        ).addMigrations(LegalDatabase.MIGRATION_1_2)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides fun provideLawDao(db: LegalDatabase): LawDao = db.lawDao()
    @Provides fun provideLawSectionDao(db: LegalDatabase): LawSectionDao = db.lawSectionDao()
    @Provides fun provideCaseDao(db: LegalDatabase): CaseDao = db.caseDao()
    @Provides fun provideClientDao(db: LegalDatabase): ClientDao = db.clientDao()
    @Provides fun provideProcedureDao(db: LegalDatabase): ProcedureDao = db.procedureDao()
    @Provides fun provideReminderDao(db: LegalDatabase): ReminderDao = db.reminderDao()
    @Provides fun provideEvidenceDao(db: LegalDatabase): EvidenceDao = db.evidenceDao()
    @Provides fun provideCaseProcedureProgressDao(db: LegalDatabase): CaseProcedureProgressDao = db.caseProcedureProgressDao()
    @Provides fun providePdfImportDao(db: LegalDatabase): PdfImportDao = db.pdfImportDao()
    @Provides fun provideHearingDao(db: LegalDatabase): HearingDao = db.hearingDao()
    @Provides fun provideBailDao(db: LegalDatabase): BailDao = db.bailDao()
    @Provides fun provideCaseDocumentDao(db: LegalDatabase): CaseDocumentDao = db.caseDocumentDao()
    @Provides fun provideCaseNoteDao(db: LegalDatabase): CaseNoteDao = db.caseNoteDao()
}
