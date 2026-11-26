package com.gtinnox.edutrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtinnox.edutrack.data.local.entity.ExaminationEntity
import com.gtinnox.edutrack.data.local.entity.SubjectEntity
import com.gtinnox.edutrack.data.repository.EduTrackRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.UUID

data class ExamWithCountdown(
    val exam: ExaminationEntity,
    val subject: SubjectEntity?,
    val daysRemaining: Long
)

class ExamViewModel(
    private val repository: EduTrackRepository
) : ViewModel() {

    val subjects: StateFlow<List<SubjectEntity>> = repository.allSubjects.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allExamsWithCountdown: StateFlow<List<ExamWithCountdown>> = kotlinx.coroutines.flow.combine(
        repository.allExaminations,
        repository.allSubjects
    ) { exams, subjectsList ->
        val subjectMap = subjectsList.associateBy { it.id }
        val today = LocalDate.now()
        exams.map { exam ->
            val examDate = try {
                LocalDate.parse(exam.examDate)
            } catch (e: Exception) {
                today.plusDays(14)
            }
            val days = ChronoUnit.DAYS.between(today, examDate)
            ExamWithCountdown(exam, subjectMap[exam.subjectId], days)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow: SharedFlow<String> = _eventFlow.asSharedFlow()

    fun saveExam(
        id: String?,
        title: String,
        subjectId: String,
        examDate: String,
        examTime: String = "09:00",
        location: String = "Exam Hall",
        examType: String = "midterm",
        preparationStatus: String = "in_progress",
        targetGrade: String = "A",
        notes: String = ""
    ) {
        viewModelScope.launch {
            if (title.isBlank() || subjectId.isBlank()) {
                _eventFlow.emit("Please enter exam title and select subject")
                return@launch
            }
            val examId = id ?: "exam-${UUID.randomUUID().toString().take(8)}"
            val entity = ExaminationEntity(
                id = examId,
                title = title.trim(),
                subjectId = subjectId,
                examDate = examDate,
                examTime = examTime,
                location = location.trim(),
                examType = examType,
                preparationStatus = preparationStatus,
                targetGrade = targetGrade.trim(),
                notes = notes.trim(),
                createdAt = Instant.now().toString()
            )
            if (id == null) {
                repository.insertExamination(entity)
                _eventFlow.emit("Examination scheduled")
            } else {
                repository.updateExamination(entity)
                _eventFlow.emit("Examination updated")
            }
        }
    }

    fun deleteExam(exam: ExaminationEntity) {
        viewModelScope.launch {
            repository.deleteExamination(exam)
            _eventFlow.emit("Examination removed")
        }
    }
}
