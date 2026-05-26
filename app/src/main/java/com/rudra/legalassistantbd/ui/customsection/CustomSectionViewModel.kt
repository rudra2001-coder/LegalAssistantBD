package com.rudra.legalassistantbd.ui.customsection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.legalassistantbd.core.database.entity.LawEntity
import com.rudra.legalassistantbd.core.database.entity.LawSectionEntity
import com.rudra.legalassistantbd.core.database.entity.ProcedureEntity
import com.rudra.legalassistantbd.data.repository.LawRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CustomProcedureStep(
    val id: Int = System.currentTimeMillis().toInt(),
    val stepNumber: Int = 1,
    val titleEn: String = "",
    val titleBn: String = "",
    val descriptionEn: String = "",
    val descriptionBn: String = "",
    val requiredDocuments: String = "",
    val duration: String = ""
)

@HiltViewModel
class CustomSectionViewModel @Inject constructor(
    private val lawRepository: LawRepository
) : ViewModel() {

    val customSections: StateFlow<List<LawSectionEntity>> = lawRepository.getCustomSections()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _sectionForm = MutableStateFlow(
        LawSectionEntity(
            id = 0, lawId = 0, sectionNumber = "", titleEn = "", titleBn = "",
            contentEn = "", contentBn = "", isCustom = true
        )
    )
    val sectionForm: StateFlow<LawSectionEntity> = _sectionForm.asStateFlow()

    private val _procedureSteps = MutableStateFlow<List<CustomProcedureStep>>(
        listOf(CustomProcedureStep(stepNumber = 1))
    )
    val procedureSteps: StateFlow<List<CustomProcedureStep>> = _procedureSteps.asStateFlow()

    private val _saveStatus = MutableStateFlow<String?>(null)
    val saveStatus: StateFlow<String?> = _saveStatus.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    fun updateSectionField(
        sectionNumber: String? = null,
        titleEn: String? = null,
        titleBn: String? = null,
        contentEn: String? = null,
        contentBn: String? = null,
        courtType: String? = null,
        bailStatus: String? = null,
        punishment: String? = null,
        isBailable: Boolean? = null,
        isCognizable: Boolean? = null
    ) {
        _sectionForm.update { current ->
            current.copy(
                sectionNumber = sectionNumber ?: current.sectionNumber,
                titleEn = titleEn ?: current.titleEn,
                titleBn = titleBn ?: current.titleBn,
                contentEn = contentEn ?: current.contentEn,
                contentBn = contentBn ?: current.contentBn,
                courtType = courtType ?: current.courtType,
                bailStatus = bailStatus ?: current.bailStatus,
                punishment = punishment ?: current.punishment,
                isBailable = isBailable ?: current.isBailable,
                isCognizable = isCognizable ?: current.isCognizable
            )
        }
    }

    fun updateProcedureStep(index: Int, step: CustomProcedureStep) {
        _procedureSteps.update { steps ->
            steps.toMutableList().apply { this[index] = step }
        }
    }

    fun addProcedureStep() {
        _procedureSteps.update { steps ->
            steps + CustomProcedureStep(
                stepNumber = steps.size + 1
            )
        }
    }

    fun removeProcedureStep(index: Int) {
        _procedureSteps.update { steps ->
            steps.toMutableList().apply {
                removeAt(index)
                forEachIndexed { i, step ->
                    this[i] = step.copy(stepNumber = i + 1)
                }
            }
        }
    }

    fun saveCustomSection(onSuccess: () -> Unit) {
        val form = _sectionForm.value
        if (form.titleEn.isBlank() || form.sectionNumber.isBlank()) {
            _saveStatus.value = "Section number and title are required"
            return
        }

        viewModelScope.launch {
            _isSaving.value = true
            _saveStatus.value = null
            try {
                val customLawId = Int.MAX_VALUE
                val lawExists = lawRepository.getLawById(customLawId) != null
                if (!lawExists) {
                    lawRepository.insertLaws(listOf(
                        LawEntity(
                            id = customLawId,
                            titleEn = "Custom Sections",
                            titleBn = "কাস্টম সেকশন",
                            shortTitle = "Custom",
                            year = 2024,
                            description = "User-defined custom law sections",
                            isActive = true
                        )
                    ))
                }

                val sectionId = form.id.takeIf { it != 0 } ?: System.currentTimeMillis().toInt()
                val section = form.copy(id = sectionId, lawId = customLawId)
                lawRepository.insertSection(section)

                val steps = _procedureSteps.value.filter { it.titleEn.isNotBlank() }
                if (steps.isNotEmpty()) {
                    lawRepository.deleteProceduresForSection(sectionId)
                    steps.forEachIndexed { index, step ->
                        lawRepository.insertProcedure(
                            ProcedureEntity(
                                id = step.id,
                                sectionId = sectionId,
                                stepNumber = index + 1,
                                titleEn = step.titleEn,
                                titleBn = step.titleBn.ifBlank { step.titleEn },
                                descriptionEn = step.descriptionEn,
                                descriptionBn = step.descriptionBn.ifBlank { step.descriptionEn },
                                requiredDocuments = step.requiredDocuments.ifBlank { null },
                                duration = step.duration.ifBlank { null }
                            )
                        )
                    }
                }

                _saveStatus.value = "Section saved successfully!"
                resetForm()
                onSuccess()
            } catch (e: Exception) {
                _saveStatus.value = "Error: ${e.message}"
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun loadCustomSectionForEdit(sectionId: Int) {
        viewModelScope.launch {
            val section = lawRepository.getSectionById(sectionId) ?: return@launch
            _sectionForm.value = section
            val procedures = lawRepository.getProceduresForSectionOnce(sectionId)
            _procedureSteps.value = if (procedures.isEmpty()) {
                listOf(CustomProcedureStep(stepNumber = 1))
            } else {
                procedures.mapIndexed { index, proc ->
                    CustomProcedureStep(
                        id = proc.id,
                        stepNumber = index + 1,
                        titleEn = proc.titleEn,
                        titleBn = proc.titleBn,
                        descriptionEn = proc.descriptionEn,
                        descriptionBn = proc.descriptionBn,
                        requiredDocuments = proc.requiredDocuments ?: "",
                        duration = proc.duration ?: ""
                    )
                }
            }
        }
    }

    fun deleteCustomSection(sectionId: Int) {
        viewModelScope.launch {
            lawRepository.deleteProceduresForSection(sectionId)
            lawRepository.deleteCustomSection(sectionId)
            resetForm()
        }
    }

    fun resetForm() {
        _sectionForm.value = LawSectionEntity(
            id = 0, lawId = 0, sectionNumber = "", titleEn = "", titleBn = "",
            contentEn = "", contentBn = "", isCustom = true
        )
        _procedureSteps.value = listOf(CustomProcedureStep(stepNumber = 1))
    }

    fun clearStatus() {
        _saveStatus.value = null
    }
}
