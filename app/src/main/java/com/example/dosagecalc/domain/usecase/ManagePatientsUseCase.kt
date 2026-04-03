package com.example.dosagecalc.domain.usecase

import androidx.paging.PagingData
import com.example.dosagecalc.domain.model.Patient
import com.example.dosagecalc.domain.repository.PatientRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class ManagePatientsUseCase @Inject constructor(
    private val patientRepository: PatientRepository
) {
    fun getPatients(): Flow<List<Patient>> = patientRepository.getAllPatients()

    fun getPatientsPaged(query: String): Flow<PagingData<Patient>> = patientRepository.getPatientsPaged(query)

    suspend fun savePatient(patient: Patient) {
        val toSave = if (patient.id.isBlank()) {
            patient.copy(id = UUID.randomUUID().toString())
        } else {
            patient
        }
        patientRepository.savePatient(toSave)
    }

    suspend fun deletePatient(patientId: String) {
        patientRepository.deletePatient(patientId)
    }
}
