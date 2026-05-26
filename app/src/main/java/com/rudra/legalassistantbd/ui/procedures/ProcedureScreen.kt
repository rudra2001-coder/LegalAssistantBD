package com.rudra.legalassistantbd.ui.procedures

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.rudra.legalassistantbd.core.database.entity.LawSectionEntity
import com.rudra.legalassistantbd.core.database.entity.ProcedureEntity
import com.rudra.legalassistantbd.data.repository.LawRepository
import com.rudra.legalassistantbd.data.repository.ProcedureRepository
import com.rudra.legalassistantbd.ui.components.*
import com.rudra.legalassistantbd.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProcedureViewModel @Inject constructor(
    private val procedureRepository: ProcedureRepository,
    private val lawRepository: LawRepository
) : ViewModel() {
    val procedures = MutableStateFlow<List<ProcedureEntity>>(emptyList())
    val sectionInfo = MutableStateFlow<LawSectionEntity?>(null)
    val isLoading = MutableStateFlow(false)

    fun loadProcedures(sectionId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            sectionInfo.value = lawRepository.getSectionById(sectionId)
            procedureRepository.getProceduresForSection(sectionId).collect { list ->
                procedures.value = list
                isLoading.value = false
            }
        }
    }
}

@Composable
fun ProcedureScreen(
    sectionId: Int,
    navController: NavController,
    viewModel: ProcedureViewModel = hiltViewModel()
) {
    LaunchedEffect(sectionId) {
        viewModel.loadProcedures(sectionId)
    }

    val procedures by viewModel.procedures.collectAsState()
    val section by viewModel.sectionInfo.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopBar(
                title = "Legal Procedure",
                onBackClick = { navController.popBackStack() }
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        if (isLoading) {
            LoadingIndicator(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    section?.let { sec ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkCard),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Section ${sec.sectionNumber}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Gold
                                )
                                Text(
                                    text = sec.titleEn,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = WhiteSoft,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Step-by-Step Procedure",
                            style = MaterialTheme.typography.titleLarge,
                            color = WhiteSoft,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                }

                if (procedures.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkCard),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Outlined.Info,
                                    contentDescription = null,
                                    tint = GrayLight,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = "Procedure information not yet available for this section.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = GrayLight
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Sample procedure shown below",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GrayMedium
                                )
                            }
                        }
                    }
                    item { SampleProcedure() }
                } else {
                    itemsIndexed(procedures) { index, procedure ->
                        ProcedureStepCard(
                            stepNumber = index + 1,
                            title = procedure.titleEn,
                            titleBn = procedure.titleBn,
                            description = procedure.descriptionEn,
                            requiredDocs = procedure.requiredDocuments,
                            duration = procedure.duration
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProcedureStepCard(
    stepNumber: Int,
    title: String,
    titleBn: String,
    description: String,
    requiredDocs: String?,
    duration: String?
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Surface(
                    color = Gold.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "$stepNumber",
                        style = MaterialTheme.typography.titleMedium,
                        color = Gold,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = WhiteSoft,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = titleBn,
                    style = MaterialTheme.typography.bodySmall,
                    color = GrayLight
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrayLight
                )
                if (requiredDocs != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Required: $requiredDocs",
                        style = MaterialTheme.typography.labelSmall,
                        color = WarningOrange
                    )
                }
                if (duration != null) {
                    Text(
                        text = "Duration: $duration",
                        style = MaterialTheme.typography.labelSmall,
                        color = InfoBlue
                    )
                }
            }
        }
    }
}

@Composable
fun SampleProcedure() {
    val sampleSteps = listOf(
        "File First Information Report (FIR)" to "নিকটস্থ থানায় এফআইআর দায়ের করুন",
        "Police Investigation" to "পুলিশ তদন্ত শুরু করবে এবং প্রমাণ সংগ্রহ করবে",
        "Evidence Collection" to "সাক্ষ্য ও অন্যান্য প্রমাণ সংগ্রহ করা হয়",
        "Witness Statements" to "সাক্ষীদের বিবৃতি রেকর্ড করা হয়",
        "Charge Sheet Submission" to "পুলিশ চার্জশিট দাখিল করে",
        "Trial in Magistrate Court" to "ম্যাজিস্ট্রেট আদালতে বিচার শুরু হয়"
    )
    sampleSteps.forEachIndexed { index, (title, bn) ->
        ProcedureStepCard(
            stepNumber = index + 1,
            title = title,
            titleBn = bn,
            description = "Step ${index + 1} in the legal procedure for this section.",
            requiredDocs = null,
            duration = null
        )
        Spacer(Modifier.height(8.dp))
    }
}
