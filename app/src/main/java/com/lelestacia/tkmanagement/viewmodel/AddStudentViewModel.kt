package com.lelestacia.tkmanagement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lelestacia.tkmanagement.data.model.Student
import com.lelestacia.tkmanagement.data.model.UniformStatus
import com.lelestacia.tkmanagement.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AddStudentUiState(
    val nis: String = "",
    val name: String = "",
    val guardianName: String = "",
    val whatsappNumber: String = "",
    val uniformShirtSize: String = "",
    val uniformPantsOrSkirtSize: String = "",
    val isAlumniFamily: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false,
    val isSaving: Boolean = false
) {
    val isValid: Boolean
        get() = name.isNotBlank() &&
            guardianName.isNotBlank() &&
            whatsappNumber.isNotBlank() &&
            uniformShirtSize.isNotBlank() &&
            uniformPantsOrSkirtSize.isNotBlank()
}

sealed interface AddStudentUiEvent {
    data class UpdateNis(val value: String) : AddStudentUiEvent
    data class UpdateName(val value: String) : AddStudentUiEvent
    data class UpdateGuardianName(val value: String) : AddStudentUiEvent
    data class UpdateWhatsappNumber(val value: String) : AddStudentUiEvent
    data class UpdateUniformShirtSize(val value: String) : AddStudentUiEvent
    data class UpdateUniformPantsOrSkirtSize(val value: String) : AddStudentUiEvent
    data class UpdateAlumniStatus(val isAlumni: Boolean) : AddStudentUiEvent
    object Save : AddStudentUiEvent
}

class AddStudentViewModel(private val repository: StudentRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AddStudentUiState())
    val uiState: StateFlow<AddStudentUiState> = _uiState.asStateFlow()

    fun onEvent(event: AddStudentUiEvent) {
        when (event) {
            is AddStudentUiEvent.UpdateNis -> _uiState.value = _uiState.value.copy(nis = event.value)
            is AddStudentUiEvent.UpdateName -> _uiState.value = _uiState.value.copy(name = event.value)
            is AddStudentUiEvent.UpdateGuardianName -> _uiState.value = _uiState.value.copy(guardianName = event.value)
            is AddStudentUiEvent.UpdateWhatsappNumber -> _uiState.value = _uiState.value.copy(whatsappNumber = event.value)
            is AddStudentUiEvent.UpdateUniformShirtSize -> _uiState.value = _uiState.value.copy(uniformShirtSize = event.value)
            is AddStudentUiEvent.UpdateUniformPantsOrSkirtSize -> _uiState.value = _uiState.value.copy(uniformPantsOrSkirtSize = event.value)
            is AddStudentUiEvent.UpdateAlumniStatus -> _uiState.value = _uiState.value.copy(isAlumniFamily = event.isAlumni)
            AddStudentUiEvent.Save -> save()
        }
    }

    private fun save() {
        val state = _uiState.value
        val firstError = when {
            state.name.isBlank() -> "Nama murid wajib diisi"
            state.guardianName.isBlank() -> "Nama wali wajib diisi"
            state.whatsappNumber.isBlank() -> "Nomor WhatsApp wajib diisi"
            state.uniformShirtSize.isBlank() -> "Ukuran baju wajib diisi"
            state.uniformPantsOrSkirtSize.isBlank() -> "Ukuran celana/rok wajib diisi"
            else -> null
        }
        if (firstError != null) {
            _uiState.value = state.copy(error = firstError)
            return
        }

        _uiState.value = state.copy(isSaving = true, error = null)
        viewModelScope.launch {
            repository.addStudent(
                Student(
                    nis = state.nis.ifBlank { null },
                    name = state.name.trim(),
                    guardianName = state.guardianName.trim(),
                    whatsappNumber = state.whatsappNumber.trim(),
                    uniformShirtSize = state.uniformShirtSize.trim(),
                    uniformPantsOrSkirtSize = state.uniformPantsOrSkirtSize.trim(),
                    uniformStatus = UniformStatus.BELUM_DIAMBIL,
                ),
                isAlumniFamily = state.isAlumniFamily
            )
            _uiState.value = _uiState.value.copy(saved = true, isSaving = false)
        }
    }
}