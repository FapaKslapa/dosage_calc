package com.example.dosagecalc.data.datasource

import android.content.Context
import com.example.dosagecalc.data.model.DrugDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDrugDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val DRUGS_FILE_NAME = "drugs.json"
    }

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    val drugs: List<DrugDto> by lazy {
        readAndParseDrugsJson()
    }

    private fun readAndParseDrugsJson(): List<DrugDto> {

        val jsonString = context.assets.open(DRUGS_FILE_NAME).use { inputStream ->
            inputStream.bufferedReader().readText()
        }

        return json.decodeFromString<List<DrugDto>>(jsonString)
    }
}
