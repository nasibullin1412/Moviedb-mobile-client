package com.homework.nasibullin.repo

import com.homework.nasibullin.App
import com.homework.nasibullin.database.AppDatabase
import com.homework.nasibullin.dataclasses.GenreDto
import com.homework.nasibullin.dataclasses.UserDto
import com.homework.nasibullin.dataclasses.UserWithGenres
import com.homework.nasibullin.datasources.Resource
import com.homework.nasibullin.fragments.MainFragment.Companion.ALL_GENRE
import com.homework.nasibullin.security.SharedPreferenceUtils
import com.homework.nasibullin.utils.BaseDataSource
import com.homework.nasibullin.utils.Converters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.lang.IllegalArgumentException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataRepo @Inject constructor(): BaseDataSource() {
    /**
     * emulate get remote user
     */
        suspend fun getRemoteUser(sessionId: String): Flow<Resource<UserDto>> {
            return flow {
                val result = safeApiCall { App.instance.apiService.getUserDetails(sessionId) }
                val resultDto = Converters.fromAccountDetailToUserDto(result)
                emit(resultDto)
            }.flowOn(Dispatchers.IO)
        }

    /**
     * get user data from database
     */
    suspend fun getLocalUser(): Flow<Resource<UserDto>> {
        return flow {
            val result = getSafeLocalData{ AppDatabase.instance.userDao().getUserData() }
            val resultDto = Converters.fromUserWithGenresToUserDto(result)
            emit(resultDto)
        }.flowOn(Dispatchers.IO)
    }

    /**
     * insert user data to database
     */
    suspend fun insertUser(userDto: UserDto){
        updateDatabase {
            AppDatabase.instance.userDao().insertUserWithGenres(
                UserWithGenres(
                    user = userDto,
                    genres = listOf(
                        GenreDto(
                            title = ALL_GENRE,
                            genreId = 0,
                            userId = userDto.id
                        )
                    )
                )
            )
        }
        updateDatabase {
            SharedPreferenceUtils.setEncryptedValue(
                key = SharedPreferenceUtils.PASSWORD_KEY,
                value = userDto.password)
        }
    }

}