package com.example.dictcacheapp.feature_dictionary.presentation.di

import android.app.Application
import androidx.room.Room
import com.example.dictcacheapp.common.Constants
import com.example.dictcacheapp.feature_dictionary.data.local.Converters
import com.example.dictcacheapp.feature_dictionary.data.local.WordInfoDao
import com.example.dictcacheapp.feature_dictionary.data.local.WordInfoDatabase
import com.example.dictcacheapp.feature_dictionary.data.remote.DictionaryApi
import com.example.dictcacheapp.feature_dictionary.data.repository.WordInfoRepositoryImpl
import com.example.dictcacheapp.feature_dictionary.data.util.GsonParser
import com.example.dictcacheapp.feature_dictionary.domain.repository.WordInfoRepository
import com.example.dictcacheapp.feature_dictionary.domain.use_case.GetWordInfoUseCase
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WordInfoModule {

    @Provides
    @Singleton
    fun provideGetWordInfoUseCase(
        repository: WordInfoRepository
    ): GetWordInfoUseCase {
        return GetWordInfoUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideWordInfoRepository(
        db: WordInfoDatabase,
        api: DictionaryApi
    ): WordInfoRepository {
        return WordInfoRepositoryImpl(api, db.dao)
    }

    @Provides
    @Singleton
    fun provideWordInfoDatabase(
        app: Application
    ): WordInfoDatabase {
        return Room.databaseBuilder(
            app, WordInfoDatabase::class.java, "word_db"
        ).addTypeConverter(Converters(GsonParser(Gson())))
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideDictionaryApi(): DictionaryApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DictionaryApi::class.java)
    }
}