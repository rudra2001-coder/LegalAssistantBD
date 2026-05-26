# Legal Assistant BD - Architecture & Reference

## Project Overview
AI-powered, offline-first legal operating system for Bangladesh. Built with Kotlin, Jetpack Compose, MVVM + Clean Architecture.

**Source files**: 85 Kotlin files across 9 functional modules.

## Tech Stack
- **Kotlin** 2.2.10 | **Jetpack Compose** (BOM 2026.02.01) | **Material 3**
- **MVVM + Clean Architecture** (data/domain/ui/core)
- **Room DB** (v3) + SQLite FTS5 (unicode61 tokenizer)
- **Hilt DI** (2.48) | **Coroutines + Flow** | **WorkManager**
- **Navigation Compose** | **PDFBox-Android** (2.0.29)
- **Gson** | **Biometric** | **Datastore Preferences**

## Module Structure

### 1. Law Library Engine (`laws/`, `ui/laws/`)
- Section-wise navigation with Bengali + English dual support
- Full-text offline search via FTS5 (unicode61 tokenizer)
- Smart keyword tagging via synonym engine
- Custom section creation with procedure steps

### 2. Advanced Search Engine (`search/`, `ui/search/`)
- Bengali NLP search with synonym mapping (চুরি→theft, খুন→murder, etc.)
- FTS5 virtual tables with auto-sync (contentEntity backed)
- Fuzzy matching through expanded synonyms
- 300ms debounced search across all imported laws

### 3. Case Management (`case_management/`, `ui/cases/`)
- Case creation (multi-type: Criminal/Civil/Family/Labour)
- Client CRM and opponent tracking
- Evidence management (PDF/image)
- Timeline UI (case events)
- Case status tracking (Active/Pending/Closed)
- Section-linked procedure guidance checklist

### 4. Legal Procedure Engine (`ui/procedures/`, `ui/procedure/`)
- Input: Law section number → Output: Step-by-step legal workflow
- Shows required documents, court type, bail status, punishment
- **ProcedureGenerator**: Auto-generates 7-step procedures (filing→hearings→post-hearing) for imported PDF sections

### 5. AI Legal Assistant (`ai/`, `ui/ai/`)
- **Offline AI**: Rule-based system + pre-trained Q&A dataset (10 pairs)
- Capabilities: Explain laws, summarize sections, draft documents, answer queries
- Bengali + English support

### 6. Document Generator (`documents/`, `ui/documents/`)
- Templates: FIR Draft, Legal Notice, Affidavit, Bail Petition
- Dynamic placeholders with text file export
- Share via `ExportShareUtil`

### 7. Reminder System (`reminders/`, `ui/reminders/`)
- WorkManager-based scheduling via HiltWorker
- Hearing alerts, Deadline alerts, Document submission alerts
- Notification channel for legal reminders

### 8. Security System (`core/security/`, `ui/security/`)
- App lock with PIN (EncryptedSharedPreferences)
- Biometric authentication (BiometricManager)
- Per-session authentication state

### 9. PDF to Database Pipeline (`pdf_converter/`, `ui/pdf/`)
- **4-stage pipeline**: Select → Extract → Detect → Import
- PDF text extraction via PDFBox-Android (page-by-page with progress callback)
- Structure detection: 5 regex patterns (Section/ধারা/সেকশন, CHAPTER, numbered lists)
- Auto-import to Room DB with PdfImportEntity tracking
- Auto-generates basic legal procedures for imported sections
- Post-import: success screen with "View in Search", "Share Summary", "Import Another"
- Share/export any section or import summary as plain text

## Database Tables (v3)
| Table | Purpose |
|-------|---------|
| `laws` | Law metadata (title, year, description) |
| `law_sections` | Section content (dual language, isCustom flag) |
| `law_sections_fts` | FTS4 virtual table (auto-sync, unicode61) |
| `law_keywords` | Keywords and synonyms per section |
| `procedures` | Step-by-step legal procedures |
| `cases` | Case management |
| `clients` | Client CRM |
| `evidence` | Evidence tracking per case |
| `reminders` | Scheduled reminders |
| `case_procedure_progress` | Per-case step completion tracking |
| `pdf_imports` | PDF import history (filename, lawId, count, timestamp) |

### Database Migrations
- **v1→v2**: Added `law_sections.isCustom`, `cases.sectionId`, `case_procedure_progress` table
- **v2→v3**: Added `pdf_imports` table
- **Fallback**: `fallbackToDestructiveMigration()` during development (data is re-seedable)

## Folder Structure
```
app/src/main/java/com/rudra/legalassistantbd/
├── LegalAssistantApp.kt              # Hilt Application
├── MainActivity.kt                   # Single Activity + onboarding check
├── core/
│   ├── database/                     # Room DB (v3), entities (11), DAOs (9)
│   ├── di/                           # Hilt DI modules (App, Database, Worker)
│   ├── security/                     # PIN + Biometric
│   └── util/                         # Constants, Extensions
├── data/
│   ├── datastore/                    # User preferences (DataStore)
│   └── repository/                   # LawRepository, CaseRepository, etc.
├── domain/
│   └── model/                        # Domain models
├── laws/                             # LawDataProvider (8 Bangladesh laws)
├── ai/                               # OfflineLegalAI engine
├── search/                           # BengaliSearchEngine + SearchIndexer
├── pdf_converter/                    # PdfTextExtractor, StructureDetector, JsonConverter
├── documents/                        # DocumentTemplate models
├── reminders/                        # ReminderWorker (HiltWorker)
├── case_management/                  # CaseTimelineBuilder
├── ui/
│   ├── theme/                        # Black+Gold Material 3 theme (Color/Theme/Type)
│   ├── navigation/                   # NavGraph (16 routes)
│   ├── components/                   # GoldButton, TopBar, SectionCard, etc.
│   ├── onboarding/                   # 3-page first-launch wizard
│   ├── dashboard/                    # Main dashboard
│   ├── laws/                         # Law explorer, detail, section detail
│   ├── search/                       # FTS5 search screen
│   ├── cases/                        # Case list, detail, create
│   ├── procedures/                   # Legal procedure display
│   ├── procedure/                    # ProcedureGenerator utility
│   ├── ai/                           # AI chat assistant
│   ├── documents/                    # Document generator
│   ├── reminders/                    # Reminder list
│   ├── pdf/                          # PDF converter (PdfViewModel + PdfConverterScreen)
│   ├── export/                       # ExportShareUtil (share/export sections)
│   ├── security/                     # Security settings
│   └── customsection/                # Custom sections with procedures
```

## Navigation Routes (16)
| Route | Screen | Arguments |
|-------|--------|-----------|
| `onboarding` | First-launch wizard | none |
| `dashboard` | Main dashboard | none |
| `law_explorer` | Law library | none |
| `law_detail/{lawId}` | Law detail with sections | lawId: Int |
| `section_detail/{sectionId}` | Section detail | sectionId: Int |
| `search` | Full-text search | none |
| `cases` | Case list | none |
| `case_detail/{caseId}` | Case detail with procedure guidance | caseId: Int |
| `create_case` | Create new case | none |
| `procedures/{sectionId}` | Legal procedure steps | sectionId: Int |
| `ai_chat` | AI legal assistant | none |
| `documents` | Document generator | none |
| `reminders` | Reminder list | none |
| `pdf_converter` | PDF import (4-stage pipeline) | none |
| `security` | Security settings | none |
| `custom_section` | Create/manage custom sections | none |

## PDF Pipeline (4-Stage)

### Stage 1 - Select
- File picker for PDF (application/pdf MIME)
- Settings hint card (dismissible)
- Shows selected filename with file info

### Stage 2 - Extract
- Page-by-page PDF text extraction via PdfTextExtractor
- Progress: page count + LinearProgressIndicator
- PDFBoxResourceLoader.init(context) called once in extractor

### Stage 3 - Detect
- StructureDetector parses text with 5 regex patterns:
  - `Section|ধারা|সেকশন` with optional separators
  - `ধারা|Section \d+[A-Za-z]?` (inline)
  - `^\d+[A-Za-z]?\.\s+` (numbered list)
  - `^\d+[A-Za-z]?[)．]\s*` (parenthetical)
  - `CHAPTER \d+[A-Za-z]?` (chapter headers)
- Preview list: section number badge + title + content preview
- "Import N Sections to Database" button

### Stage 4 - Import & Post-Import
- Creates LawEntity from filename
- Maps ParsedSections → LawSectionEntity (auto-id via hash)
- Bulk insert via LawRepository
- **ProcedureGenerator** auto-creates 7-step procedures:
  1. Identify Applicable Court
  2. File Complaint/FIR
  3. Collect Evidence
  4. Attend First Hearing
  5. Submit Arguments
  6. Obtain Court Order
  7. File Appeal (if needed)
- Records PdfImportEntity for history
- Success screen: "View in Search", "Share Summary", "Import Another"

## Onboarding Flow
- First launch → SharedPreferences check ("onboarding" key, "completed" boolean)
- Not completed → OnboardingScreen (3 HorizontalPager pages)
  - Page 1: Welcome (gavel icon)
  - Page 2: PDF Import (PDF icon)
  - Page 3: Search (search icon)
- On complete → navigate to Dashboard, pop onboarding from backstack
- Subsequent launches → skip onboarding, go directly to Dashboard

## Custom Sections & Procedure Guidance

### Creating Custom Sections
- Accessible from Dashboard → "Custom Sections"
- Fields: Section Number, Title (En/Bn), Content (En/Bn), Court Type, Bail Status, Punishment, Bailable/Cognizable
- Stored in `law_sections` with `isCustom = 1`
- Each custom section can have N procedure steps (add/edit/delete/reorder)

### Procedure Guidance in Case Detail
- When creating a case, link it to any section (built-in or custom)
- Case detail shows linked section info + procedure progress ("3/5 completed")
- Each procedure step appears as a checklist card (tap to toggle complete)
- Progress persisted in `case_procedure_progress` table
- Completed steps show green checkmark + "Completed" label

## Key Implementation Details

### Color Scheme (Black + Gold)
```kotlin
val Gold = Color(0xFFD4A017)
val GoldDark = Color(0xFFB8860B)
val GoldLight = Color(0xFFF0D060)
val DarkBackground = Color(0xFF0D0D0D)
val DarkSurface = Color(0xFF1A1A1A)
val DarkCard = Color(0xFF1E1E1E)
val WhiteSoft = Color(0xFFF5F5F5)
val GrayLight = Color(0xFFB0B0B0)
val GrayMedium = Color(0xFF808080)
val SuccessGreen = Color(0xFF4CAF50)
val ErrorRed = Color(0xFFF44336)
```

### FTS5 Search
- Tokenizer: `unicode61` for Bengali + English
- Backed by `contentEntity = LawSectionEntity::class` (auto-sync on insert)
- Columns indexed: titleEn, titleBn, contentEn, contentBn
- Synonym engine expands Bengali/English terms

### Synonym Engine (`BengaliSearchEngine.kt`)
Maps Bengali terms to English keywords for cross-language search:
- "চুরি" → "theft", "stealing"
- "খুন" → "murder", "homicide"
- "জমি" → "land", "property"
- "বিবাহ" → "marriage", "divorce"
- "চুক্তি" → "contract", "agreement"
- Builds FTS5 query with expanded OR terms

### Law Data Initialization
`DataInitializer` auto-loads 8 Bangladesh laws (Penal Code, CrPC, CPC, Evidence Act, Digital Security Act, Contract Act, Family Courts Ordinance, Labour Act) with sections and procedures on first launch.

### FileProvider Configuration
- Authority: `${applicationId}.fileprovider`
- Path: `cache-path name="documents" path="documents/"` (in res/xml/file_paths.xml)
- Used by ExportShareUtil for sharing exported section files
- Declared in AndroidManifest.xml

### Key Dependency Versions
- Room 2.6.1 | Hilt 2.48 | Compose BOM 2026.02.01
- PDFBox-Android 2.0.29 | Navigation Compose 2.7.5
- WorkManager 2.9.0 | DataStore 1.1.3 | Biometric 1.1.0

## Build Configuration
- **minSdk**: 28 | **targetSdk**: 36 | **compileSdk**: 36
- **KSP**: Room + Hilt annotation processing
- **AGP**: 9.2.1 | **Kotlin**: 2.2.10

## Development Phases
1. ✅ **PHASE 1**: Law DB + JSON import + FTS5 search + Law viewer
2. ✅ **PHASE 2**: Case management + Timeline UI + Reminder system
3. ✅ **PHASE 3**: Procedure engine + Offline AI + PDF pipeline (4-stage + auto-procedures + share/export)
4. 🔲 **PHASE 4**: Online AI integration (OpenAI/Gemini API + streaming chat)
5. 🔲 **PHASE 5**: OCR (ML Kit) + Voice search (Bengali voice queries)
