package com.example.dosagecalc.data.di

import com.example.dosagecalc.data.repository.DrugRepositoryImpl
import com.example.dosagecalc.domain.repository.DrugRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Modulo Hilt per il layer Data.
 *
 * Collega le interfacce del Domain (astrazioni) alle implementazioni concrete
 * del layer Data. Hilt usa questo modulo per sapere quale classe concreta
 * iniettare quando qualcuno richiede un [DrugRepository].
 *
 * @Binds è preferito a @Provides quando si tratta solo di legare un'interfaccia
 * a un'implementazione: genera meno codice e non richiede una funzione con corpo.
 *
 * InstallIn(SingletonComponent::class) → l'istanza vive per tutto il ciclo
 * di vita dell'Application (scoped singleton). Corretto per un repository
 * che legge dati in memoria.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    /**
     * Dice a Hilt: "quando qualcuno chiede un [DrugRepository], inietta un [DrugRepositoryImpl]".
     * [DrugRepositoryImpl] è già @Singleton grazie alla sua annotazione,
     * quindi Hilt creerà una sola istanza.
     */
    @Binds
    @Singleton
    abstract fun bindDrugRepository(
        impl: DrugRepositoryImpl
    ): DrugRepository
}
