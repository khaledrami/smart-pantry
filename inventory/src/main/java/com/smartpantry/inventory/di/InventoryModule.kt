package com.smartpantry.inventory.di

import android.content.Context
import androidx.room.Room
import com.smartpantry.inventory.data.AppDatabase
import com.smartpantry.inventory.data.dao.MovementDao
import com.smartpantry.inventory.data.dao.ProductDao
import com.smartpantry.inventory.data.repository.BarcodeScannerRepositoryImpl
import com.smartpantry.inventory.data.repository.MockProductLookupRepository
import com.smartpantry.inventory.data.repository.ProductRepositoryImpl
import com.smartpantry.inventory.domain.repository.BarcodeScannerRepository
import com.smartpantry.inventory.domain.repository.ProductLookupRepository
import com.smartpantry.inventory.domain.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InventoryModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@dagger.hilt.android.qualifiers.ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "smart_pantry.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideProductDao(database: AppDatabase): ProductDao = database.productDao()

    @Provides
    @Singleton
    fun provideMovementDao(database: AppDatabase): MovementDao = database.movementDao()

    @Provides
    @Singleton
    fun provideProductRepository(impl: ProductRepositoryImpl): ProductRepository = impl

    @Provides
    @Singleton
    fun provideBarcodeScannerRepository(impl: BarcodeScannerRepositoryImpl): BarcodeScannerRepository = impl

    @Provides
    @Singleton
    fun provideProductLookupRepository(impl: MockProductLookupRepository): ProductLookupRepository = impl
}