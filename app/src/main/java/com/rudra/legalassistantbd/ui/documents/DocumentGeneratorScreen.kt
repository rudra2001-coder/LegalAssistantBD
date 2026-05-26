package com.rudra.legalassistantbd.ui.documents

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.rudra.legalassistantbd.documents.DocumentExporter
import com.rudra.legalassistantbd.documents.DocumentTemplates
import com.rudra.legalassistantbd.documents.Template
import com.rudra.legalassistantbd.ui.components.*
import com.rudra.legalassistantbd.ui.theme.*
import com.rudra.legalassistantbd.ui.theme.LocalAppColors
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DocumentViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val documentExporter: DocumentExporter
) : ViewModel() {

    private val _selectedTemplate = MutableStateFlow<Template?>(null)
    val selectedTemplate: StateFlow<Template?> = _selectedTemplate.asStateFlow()

    private val _fieldValues = MutableStateFlow<Map<String, String>>(emptyMap())
    val fieldValues: StateFlow<Map<String, String>> = _fieldValues.asStateFlow()

    private val _generatedContent = MutableStateFlow<String?>(null)
    val generatedContent: StateFlow<String?> = _generatedContent.asStateFlow()

    fun selectTemplate(template: Template) {
        _selectedTemplate.value = template
        _fieldValues.value = template.placeholders.associateWith { "" }
        _generatedContent.value = null
    }

    fun updateField(key: String, value: String) {
        _fieldValues.update { it + (key to value) }
    }

    fun generateDocument() {
        val template = _selectedTemplate.value ?: return
        var content = template.content
        _fieldValues.value.forEach { (key, value) ->
            content = content.replace("\${${key}}", value.ifBlank { "[$key]" })
        }
        _generatedContent.value = content
    }

    fun exportDocument(fileName: String): Boolean {
        val content = _generatedContent.value ?: return false
        return documentExporter.exportToText(context, content, fileName) != null
    }

    fun reset() {
        _selectedTemplate.value = null
        _fieldValues.value = emptyMap()
        _generatedContent.value = null
    }
}

@Composable
fun DocumentGeneratorScreen(
    navController: NavController,
    viewModel: DocumentViewModel = hiltViewModel()
) {
    val selectedTemplate by viewModel.selectedTemplate.collectAsState()
    val fieldValues by viewModel.fieldValues.collectAsState()
    val generatedContent by viewModel.generatedContent.collectAsState()
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current

    Scaffold(
        topBar = {
            TopBar(
                title = "Document Generator",
                onBackClick = { navController.popBackStack() }
            )
        },
        containerColor = scheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (selectedTemplate == null) {
                Text(
                    text = "Select Document Type",
                    style = MaterialTheme.typography.titleLarge,
                    color = scheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(16.dp))

                DocumentTemplates.templates.forEach { template ->
                    Card(
                        onClick = { viewModel.selectTemplate(template) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = c.darkCard),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.Description,
                                contentDescription = null,
                                tint = scheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = template.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = scheme.onSurface,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = template.type,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = scheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            } else if (generatedContent == null) {
                Text(
                    text = selectedTemplate!!.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = scheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Fill in the details below",
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))

                fieldValues.forEach { (key, value) ->
                    OutlinedTextField(
                        value = value,
                        onValueChange = { viewModel.updateField(key, it) },
                        label = { Text(key.replaceFirstChar { it.uppercase() }.replace("_", " ")) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors()
                    )
                }

                Spacer(Modifier.height(24.dp))
                scheme.primaryButton(
                    text = "Generate Document",
                    onClick = { viewModel.generateDocument() },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.Download
                )

                Spacer(Modifier.height(8.dp))
                scheme.primaryButton(
                    text = "Back to Templates",
                    onClick = { viewModel.reset() },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = selectedTemplate!!.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = scheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = c.darkCard),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = generatedContent!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = scheme.onSurface,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))
                scheme.primaryButton(
                    text = "Export to Text",
                    onClick = {
                        viewModel.exportDocument(selectedTemplate!!.name)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.Share
                )

                Spacer(Modifier.height(8.dp))
                scheme.primaryButton(
                    text = "Edit Again",
                    onClick = { viewModel.generateDocument() },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
