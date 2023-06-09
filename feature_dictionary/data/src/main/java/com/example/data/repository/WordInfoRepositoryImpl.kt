package com.example.data.repository

import com.example.core.util.Resource
import com.example.data.local.WordInfoDao
import com.example.data.mapper.toWordInfo
import com.example.data.mapper.toWordInfoEntity
import com.example.data.remote.DictionaryApi
import com.example.domain.model.WordInfo
import com.example.domain.repository.WordInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class WordInfoRepositoryImpl(
    private val api: DictionaryApi,
    private val dao: WordInfoDao
): WordInfoRepository {

    override fun getWordInfo(word: String): Flow<Resource<List<WordInfo>>> = flow {
        emit(Resource.Loading())

        val wordInfo = dao.getWordInfo(word).map { it.toWordInfo() }
        emit(Resource.Loading(data = wordInfo))

        try {
            val remoteWordInfo = api.getWordInfo(word)
            dao.deleteWordInfo(remoteWordInfo.map { it.word })
            dao.insertWordInfo(remoteWordInfo.map { it.toWordInfoEntity() })
        } catch(e: HttpException) {
            emit(
                Resource.Error(
                message = "Oops, something went wrong!",
                data = wordInfo
            ))
        } catch(e: IOException) {
            emit(
                Resource.Error(
                message = "Couldn't reach server, check you internet connection.",
                data = wordInfo
            ))
        }

        val newWordInfo = dao.getWordInfo(word).map { it.toWordInfo() }
        emit(Resource.Success(newWordInfo))
    }
}