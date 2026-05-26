# Legal Assistant BD - Architecture & Reference

## Project Overview
AI-powered, offline-first legal operating system for Bangladesh. Built with Kotlin, Jetpack Compose, MVVM + Clean Architecture.

## Tech Stack
- **Kotlin** 2.2.10 | **Jetpack Compose** (BOM 2026.02.01) | **Material 3**
- **MVVM + Clean Architecture** (data/domain/ui/core)
- **Room DB** + SQLite FTS5 (unicode61 tokenizer)
- **Hilt DI** | **Coroutines + Flow** | **WorkManager**
- **Navigation Compose** | **Material 3** (Black+Gold theme)

## Module Structure

### 1. Law Library Engine (`laws/`, `ui/laws/`)
- Section-wise navigation
- Bengali + English dual support
- Full-text offline search (FTS5)
- Smart keyword tagging via synonym engine

### 2. Advanced Search Engine (`search/`, `ui/search/`)
- Bengali NLP search with synonym mapping
- FTS5 virtual tables with unicode61 tokenizer
- Fuzzy matching through expanded synonyms
- 300ms debounced search

### 3. Case Management (`case_management/`, `ui/cases/`)
- Case creation (multi-type: Criminal/Civil/Family/Labour)
- Client CRM and opponent tracking
- Evidence management (PDF/image)
- Timeline UI (case events)
- Case status tracking (Active/Pending/Closed)

### 4. Legal Procedure Engine (`ui/procedures/`)
- Input: Law section number
- Output: Step-by-step legal workflow
- Shows required documents, court type, bail status, punishment

### 5. AI Legal Assistant (`ai/`, `ui/ai/`)
- **Offline AI**: Rule-based system + pre-trained Q&A dataset
- Capabilities: Explain laws, summarize sections, draft documents, answer queries
- Bengali + English support

### 6. Document Generator (`documents/`, `ui/documents/`)
- Templates: FIR Draft, Legal Notice, Affidavit, Bail Petition
- Dynamic placeholders
- Export to text file

### 7. Reminder System (`reminders/`, `ui/reminders/`)
- WorkManager-based scheduling
- Hearing alerts, Deadline alerts, Document submission alerts
- Notification channel for legal reminders

### 8. Security System (`core/security/`, `ui/security/`)
- App lock with PIN
- Biometric authentication
- Encrypted SharedPreferences

### 9. PDF to JSON Converter (`pdf_converter/`, `ui/pdf/`)
- PDF text extraction via PDFBox-Android
- Structure detection (section number, title, content)
- JSON conversion and import to Room DB

## Database Tables
- `laws` - Law metadata
- `law_sections` - Section content (dual language)
- `law_sections_fts` - FTS4 virtual table for full-text search
- `law_keywords` - Keywords and synonyms per section
- `procedures` - Step-by-step legal procedures
- `cases` - Case management
- `clients` - Client CRM
- `evidence` - Evidence tracking per case
- `reminders` - Scheduled reminders

## Folder Structure
```
app/src/main/java/com/rudra/legalassistantbd/
├── LegalAssistantApp.kt          # Hilt Application
├── MainActivity.kt               # Single Activity entry
├── core/
│   ├── database/                 # Room DB, entities, DAOs
│   ├── di/                       # Hilt DI modules
│   ├── security/                 # PIN, Biometric
│   └── util/                     # Constants, Extensions
├── data/
│   ├── datastore/                # User preferences
│   └── repository/               # Data repositories
├── domain/
│   └── model/                    # Domain models
├── ui/
│   ├── theme/                    # Black+Gold Material 3 theme
│   ├── navigation/               # NavGraph
│   ├── components/               # Shared composables
│   ├── dashboard/                # Dashboard screen
│   ├── laws/                     # Law explorer & detail
│   ├── search/                   # Search screen
│   ├── cases/                    # Case management
│   ├── procedures/               # Legal procedures
│   ├── ai/                       # AI chat assistant
│   ├── documents/                # Document generator
│   ├── reminders/                # Reminder system
│   ├── pdf/                      # PDF converter
│   ├── security/                 # Security settings
│   └── customsection/            # Custom sections with procedures
├── laws/                         # Law data provider
├── ai/                           # Offline AI engine
├── search/                       # Bengali search engine
├── reminders/                    # WorkManager workers
├── documents/                    # Document templates
├── case_management/              # Case timeline builder
└── pdf_converter/                # PDF extraction & parsing
```

## Navigation Routes
| Route | Screen |
|-------|--------|
| `dashboard` | Main dashboard |
| `law_explorer` | Law library |
| `law_detail/{lawId}` | Law detail with sections |
| `section_detail/{sectionId}` | Section detail |
| `search` | Full-text search |
| `cases` | Case list |
| `case_detail/{caseId}` | Case detail |
| `create_case` | Create new case |
| `procedures/{sectionId}` | Legal procedure steps |
| `ai_chat` | AI legal assistant |
| `documents` | Document generator |
| `reminders` | Reminder list |
| `pdf_converter` | PDF import |
| `security` | Security settings |
| `custom_section` | Create/manage custom sections with procedures |

## Custom Sections & Procedure Guidance

### Creating Custom Sections
Users can define their own law sections with step-by-step procedures via **Custom Sections** screen (accessible from dashboard).

**Fields per section:**
- Section Number, Title (En/Bn), Content (En/Bn), Court Type, Bail Status, Punishment, Bailable/Cognizable flags

**Fields per procedure step:**
- Title (En/Bn), Description (En/Bn), Required Documents, Duration

### Linking to Cases
When creating a case, users can link it to any section (built-in or custom). The section selector shows all available sections with a "Custom Section" label.

### Procedure Guidance in Case Detail
When viewing a case linked to a section:
- The linked section info and procedure progress (e.g. "3/5 completed") are shown
- Each procedure step appears as a checklist card
- Steps can be toggled completed/incomplete by tapping
- Progress is persisted in `case_procedure_progress` table
- Completed steps show green checkmarks with "Completed" label

### Database Changes (v2 migration)
- `law_sections.isCustom` column added (boolean, default false)
- `cases.sectionId` column added (nullable FK reference)
- New table `case_procedure_progress` tracks per-case step completion

## Key Implementation Details

### Color Scheme (Black + Gold)
```kotlin
val Gold = Color(0xFFD4A017)
val DarkBackground = Color(0xFF0D0D0D)
val DarkSurface = Color(0xFF1A1A1A)
```

### FTS5 Search
- Tokenizer: `unicode61` for Bengali + English
- Columns indexed: titleEn, titleBn, contentEn, contentBn
- Synonym engine expands Bengali/English terms

### Synonym Engine
Maps Bengali terms to English keywords for intelligent search:
- "চুরি" → "theft", "stealing"
- "খুন" → "murder", "homicide"
- etc.

### Law Data Initialization
`DataInitializer` auto-loads 8 Bangladesh laws with sections and procedures on first launch.

## Build Configuration
- **minSdk**: 28
- **targetSdk**: 36
- **compileSdk**: 36
- **KSP**: for Room and Hilt annotation processing

## Dependencies Summary
- `androidx.compose.material3` - Material 3 UI
- `androidx.room:room-ktx` + `room-compiler` (KSP) - Database
- `com.google.dagger:hilt-android` + `hilt-compiler` (KSP) - DI
- `androidx.hilt:hilt-navigation-compose` - Hilt + Navigation
- `androidx.hilt:hilt-work` - Hilt + WorkManager
- `androidx.work:work-runtime-ktx` - Background scheduling
- `androidx.navigation:navigation-compose` - Navigation
- `com.tom-roush:pdfbox-android` - PDF text extraction
- `com.google.code.gson:gson` - JSON parsing
- `androidx.datastore:datastore-preferences` - Preferences
- `androidx.biometric:biometric` - Biometric auth

## Development Phases
1. ✅ **PHASE 1**: Law DB + JSON import + FTS5 search + Law viewer
2. ✅ **PHASE 2**: Case management + Timeline UI + Reminder system
3. ✅ **PHASE 3**: Procedure engine + Offline AI
4. 🔲 **PHASE 4**: Online AI integration (OpenAI/Gemini)
5. 🔲 **PHASE 5**: OCR + Voice search
