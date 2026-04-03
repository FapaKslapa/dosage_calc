package com.example.dosagecalc.domain.repository

import androidx.paging.PagingData
import com.example.dosagecalc.domain.model.Patient
import kotlinx.coroutines.flow.Flow

interface PatientRepository {
    fun getAllPatients(): Flow<List<Patient>>
    fun getPatientsPaged(query: String): Flow<PagingData<Patient>>
    suspend fun getPatientById(id: String): Patient?
    suspend fun savePatient(patient: Patient)
    suspend fun deletePatient(patientId: String)
}
