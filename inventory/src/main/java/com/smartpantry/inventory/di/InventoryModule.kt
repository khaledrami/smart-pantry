package com.smartpantry.inventory.di

import android.content.Context
import androidx.room.Room
import com.smartpantry.inventory.data.AppDatabase
import com.smartpantry.inventory.data.dao.MovementDao
import com.smartpantry.inventory.data.dao.ProductDao
import com.smartpantry.inventory.data.remote.openfoodfacts.CompositeProductLookupRepository
import com.smartpantry.inventory.data.remote.openfoodfacts.OpenFoodFactsApi
import com.smartpantry.inventory.data.remote.openfoodfacts.OpenFoodFactsProductLookupRepository
import com.smartpantry.inventory.data.repository.BarcodeScannerRepositoryImpl
import com.smartpantry.inventory.data.repository.ProductRepositoryImpl
import com.smartpantry.inventory.domain.repository.BarcodeScannerRepository
import com.smartpantry.inventory.domain.repository.ProductLookupRepository
import com.smartpantry.inventory.domain.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
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
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                }
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideOpenFoodFactsApi(retrofit: Retrofit): OpenFoodFactsApi {
        return retrofit.create(OpenFoodFactsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideProductLookupRepository(impl: CompositeProductLookupRepository): ProductLookupRepository = impl
}
