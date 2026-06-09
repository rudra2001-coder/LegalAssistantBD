# Legal Assistant BD — Design System

## Color Palette

All colors defined in `ui/theme/Color.kt`. Import with `import com.rudra.legalassistantbd.ui.theme.*`.

### Core
| Token | Value | Usage |
|-------|-------|-------|
| `DarkBackground` | `#0D0D0D` | `Scaffold.containerColor` |
| `DarkSurface` | `#1A1A1A` | `TopBar` background, alert dialog container |
| `DarkSurfaceVariant` | `#242424` | `HorizontalDivider`, unfocused field border |
| `DarkCard` | `#1E1E1E` | `Card(containerColor = DarkCard)` |

### Text
| Token | Value | Usage |
|-------|-------|-------|
| `WhiteSoft` | `#F5F5F5` | Primary text, titles |
| `GrayLight` | `#B0B0B0` | Secondary/subtitle text |
| `GrayMedium` | `#808080` | Placeholder text, disabled content |
| `GrayDark` | `#505050` | Outlines, borders |

### Accent
| Token | Value | Usage |
|-------|-------|-------|
| `Gold` | `#D4A017` | Primary accent, buttons, active state, highlight |
| `GoldDark` | `#B8860B` | Darker gold |
| `GoldLight` | `#F0D060` | Lighter gold |
| `InfoBlue` | `#2196F3` | Blue accent |
| `SuccessGreen` | `#4CAF50` | Success/completed state |
| `WarningOrange` | `#FF9800` | Warning state |
| `ErrorRed` | `#F44336` | Error/destructive state |

---

## Scaffold Pattern

Every screen **must** use `Scaffold`:

```kotlin
Scaffold(
    topBar = {
        TopBar(title = "Screen Title", onBackClick = { navController.popBackStack() })
    },
    containerColor = DarkBackground
) { padding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)   // ← screen horizontal padding
    ) {
        // content
    }
}
```

Exceptions:
- **OnboardingScreen** — no TopBar, uses custom skip + pager layout
- **DashboardScreen** — custom header replaces TopBar
- **Tab destinations** — `TopBar(onBackClick = null)` (no back button)

---

## Components

### TopBar (`ui/components/CommonComponents.kt`)
```kotlin
TopBar(
    title = "Screen Title",
    onBackClick = { navController.popBackStack() },  // null for tab destinations
    actions = { /* optional RowScope action icons */ }
)
```

### Card
```kotlin
Card(
    colors = CardDefaults.cardColors(containerColor = DarkCard),
    shape = RoundedCornerShape(16.dp)   // ← standard corner radius
) {
    Column(modifier = Modifier.padding(20.dp)) { /* content */ }
}
```

### SectionCard (shared component)
Use for law section list items:
```kotlin
SectionCard(
    sectionNumber = "302",
    title = "Punishment for murder",
    onClick = { /* navigate */ }
)
```
Container: `DarkCard`, shape: `RoundedCornerShape(16.dp)`.

### OutlinedTextField
```kotlin
OutlinedTextField(
    value = ...,
    onValueChange = { ... },
    label = { Text("Field Name") },
    modifier = Modifier.fillMaxWidth(),
    singleLine = true,
    shape = RoundedCornerShape(12.dp),     // ← standard field shape
    colors = fieldColors()                 // ← must use shared helper
)
```

`fieldColors()` is defined in `CommonComponents.kt` — never inline it.

### GoldButton (primary action)
```kotlin
GoldButton(
    text = "Submit",
    onClick = { ... },
    modifier = Modifier.fillMaxWidth(),
    icon = Icons.Default.Save              // optional
)
```

### DetailRow (label-value pair)
```kotlin
DetailRow("Court Type", "Session Court")
// Renders: "Court Type" (GrayLight) ⇄ "Session Court" (WhiteSoft)
```

### LoadingIndicator
```kotlin
if (isLoading) {
    LoadingIndicator()
}
```

### EmptyState
```kotlin
if (items.isEmpty()) {
    EmptyState(
        icon = Icons.Outlined.SearchOff,
        title = "No results found",
        subtitle = "Try different keywords"
    )
}
```

---

## Spacing Rules

| Context | Value |
|---------|-------|
| Screen horizontal padding | `16.dp` |
| Between sections | `Spacer(Modifier.height(16.dp))` |
| Card content padding | `20.dp` |
| Card inner element spacing | `12.dp` |
| Between related fields | `16.dp` |
| List item padding | `16.dp` |
| Between list items | `8.dp` |

---

## Card Corner Radius

| Element | Radius |
|---------|--------|
| Standard card | `RoundedCornerShape(16.dp)` |
| OutlinedTextField | `RoundedCornerShape(12.dp)` |
| Section card | `RoundedCornerShape(16.dp)` |
| Button | `RoundedCornerShape(12.dp)` |
| AI chat bubble | `RoundedCornerShape(16.dp)` with asymmetric corners |
| Alert dialog | `RoundedCornerShape(12.dp)` (default) |

---

## Dividers
```kotlin
HorizontalDivider(color = DarkSurfaceVariant)
```

---

## Alert Dialogs
```kotlin
AlertDialog(
    onDismissRequest = { ... },
    containerColor = DarkSurface,
    title = { Text("Title", color = WhiteSoft, fontWeight = FontWeight.Bold) },
    text = { Text("Message", color = GrayLight) },
    confirmButton = {
        TextButton(onClick = { ... }) { Text("Confirm", color = Gold) }
    },
    dismissButton = {
        TextButton(onClick = { ... }) { Text("Cancel", color = GrayLight) }
    }
)
```

---

## Loading & Empty States

- **Loading**: `LoadingIndicator()` — centered `CircularProgressIndicator(color = Gold)`
- **Empty**: `EmptyState(icon, title, subtitle)` — centered icon + text

---

## Navigation

### Bottom Nav (5 tabs)
| Tab | Route | Icon |
|-----|-------|------|
| Home | `dashboard` | `Home` |
| Laws | `law_explorer` | `LibraryBooks` |
| Cases | `cases` | `Gavel` |
| AI | `ai_chat` | `SmartToy` |
| All | `all_features` | `Dashboard` |

- Hidden on detail/onboarding/settings screens
- State preserved via `saveState`/`restoreState`
- Selected tab: `Gold` tint, unselected: `GrayLight`

### Navigation Arguments
- Use `NavController.navigate(route)` for push
- Use `navController.popBackStack()` for back
- Pass Int IDs as path segments: `"section_detail/${section.id}"`

---

## Tab Patterns

### Case Detail Tabs (`CaseDetailScreen`)
Use `TabRow` for switching between content sections:
```kotlin
TabRow(
    selectedTabIndex = selectedTab,
    containerColor = scheme.surface,
    contentColor = scheme.primary
) {
    tabs.forEachIndexed { index, title ->
        Tab(
            selected = selectedTab == index,
            onClick = { selectedTab = index },
            text = { Text(title, fontWeight = ...) }
        )
    }
}
```
All case detail functionality (Hearings, Documents, Bail, Notes) is handled via internal tabs, no separate navigation routes.

### Filter Tabs (`CaseListScreen`)
Use `ScrollableTabRow` with `edgePadding = 16.dp` and no divider for status filter chips:
```kotlin
ScrollableTabRow(
    selectedTabIndex = ...,
    containerColor = scheme.background,
    contentColor = scheme.primary,
    edgePadding = 16.dp,
    divider = {}
) {
    filters.forEach { filter ->
        Tab(selected = ..., onClick = { ... }, text = { Text(filter) })
    }
}
```

---

## Code Conventions

1. **Always use shared `fieldColors()`** — never inline `OutlinedTextFieldDefaults.colors()`
2. **Always use shared `TopBar`** — never build custom top bars (except DashboardScreen)
3. **Always use theme color tokens** — never define local `Color(0xFF...)` values
4. **Always use `Scaffold`** — never use raw `.background(DarkBackground)` on Column
5. **Always use `DarkCard` for Card backgrounds** — never `DarkSurface` unless intentional for hierarchy
6. **Always use `RoundedCornerShape(16.dp)` for Cards** — never non-standard radii
7. **Always use `HorizontalDivider(color = DarkSurfaceVariant)`** for horizontal rules
8. **Do not duplicate shared components** — `fieldColors()`, `DetailRow`, `StatCard` are in `CommonComponents.kt`
