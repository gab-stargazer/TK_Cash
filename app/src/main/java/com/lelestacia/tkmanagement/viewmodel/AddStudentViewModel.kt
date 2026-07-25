package com.lelestacia.tkmanagement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lelestacia.tkmanagement.data.model.Student
import com.lelestacia.tkmanagement.data.model.UniformStatus
import com.lelestacia.tkmanagement.data.repository.CashRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AddStudentFormState(
    val nis: String = "",
    val name: String = "",
    val guardianName: String = "",
    val whatsappNumber: String = "",
    val uniformShirtSize: String = "",
    val uniformPantsOrSkirtSize: String = "",
    val uniformShoeSize: String = "",
    val error: String? = null,
    val saved: Boolean = false,
    val isSaving: Boolean = false
)

class AddStudentViewModel(private val repository: CashRepository) : ViewModel() {

    private val _formState = MutableStateFlow(AddStudentFormState())
    val formState: StateFlow<AddStudentFormState> = _formState.asStateFlow()

    fun update(transform: (AddStudentFormState) -> AddStudentFormState) {
        _formState.value = transform(_formState.value).copy(error = null, saved = false)
    }

    fun save() {
        val s = _formState.value
        if (s.name.isBlank()) {
            _formState.value = s.copy(error = "Nama murid wajib diisi")
            return
        }
        if (s.guardianName.isBlank()) {
            _formState.value = s.copy(error = "Nama wali wajib diisi")
            return
        }

        _formState.value = s.copy(isSaving = true)
        viewModelScope.launch {
            repository.addStudent(
                Student(
                    nis = s.nis.ifBlank { null },
                    name = s.name.trim(),
                    guardianName = s.guardianName.trim(),
                    whatsappNumber = s.whatsappNumber.trim(),
                    uniformShirtSize = s.uniformShirtSize.ifBlank { null },
                    uniformPantsOrSkirtSize = s.uniformPantsOrSkirtSize.ifBlank { null },
                    uniformShoeSize = s.uniformShoeSize.ifBlank { null },
                    uniformStatus = UniformStatus.BELUM_DIAMBIL
                )
            )
            _formState.value = AddStudentFormState(saved = true)
        }
    }
}
