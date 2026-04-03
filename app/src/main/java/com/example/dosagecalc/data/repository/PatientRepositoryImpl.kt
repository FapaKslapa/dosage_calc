package com.example.dosagecalc.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.dosagecalc.data.datasource.PatientDao
import com.example.dosagecalc.data.model.PatientEntity
import com.example.dosagecalc.domain.model.Patient
import com.example.dosagecalc.domain.repository.PatientRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class PatientRepositoryImpl(
    private val patientDao: PatientDao
) : PatientRepository {

    override fun getAllPatients(): Flow<List<Patient>> {
        return patientDao.getAllPatients().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getPatientsPaged(query: String): Flow<PagingData<Patient>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { patientDao.getPatientsPaged(query) }
        ).flow.map { pagingData -> pagingData.map { it.toDomain() } }
    }

    override suspend fun getPatientById(id: String): Patient? {
        return patientDao.getPatientById(id)?.toDomain()
    }

    override suspend fun savePatient(patient: Patient) {
        patientDao.insertPatient(patient.toEntity())
    }

    override suspend fun deletePatient(patientId: String) {
        patientDao.deletePatient(patientId)
    }

    private fun PatientEntity.toDomain(): Patient {
        return Patient(
            id = id,
            name = name,
            surname = surname,
            birthDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(birthDate), ZoneId.systemDefault()),
            weightKg = weightKg,
            heightCm = heightCm,
            ageYears = ageYears,
            notes = notes
        )
    }

    private fun Patient.toEntity(): PatientEntity {
        return PatientEntity(
            id = id,
            name = name,
            surname = surname,
            birthDate = birthDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            weightKg = weightKg,
            heightCm = heightCm,
            ageYears = ageYears,
            notes = notes
        )
    }
}
