package com.akram.babsharhisab.core.di

import com.akram.babsharhisab.data.repository.AssetRepositoryImpl
import com.akram.babsharhisab.data.repository.BackupSyncRepositoryImpl
import com.akram.babsharhisab.data.repository.BakiRepositoryImpl
import com.akram.babsharhisab.data.repository.LedgerRepositoryImpl
import com.akram.babsharhisab.domain.repository.AssetRepository
import com.akram.babsharhisab.domain.repository.BackupSyncRepository
import com.akram.babsharhisab.domain.repository.BakiRepository
import com.akram.babsharhisab.domain.repository.LedgerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindLedgerRepository(impl: LedgerRepositoryImpl): LedgerRepository

    @Binds
    @Singleton
    abstract fun bindBakiRepository(impl: BakiRepositoryImpl): BakiRepository

    @Binds
    @Singleton
    abstract fun bindAssetRepository(impl: AssetRepositoryImpl): AssetRepository

    @Binds
    @Singleton
    abstract fun bindBackupSyncRepository(impl: BackupSyncRepositoryImpl): BackupSyncRepository
}
