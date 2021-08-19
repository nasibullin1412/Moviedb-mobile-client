package com.homework.nasibullin.repo

import com.homework.nasibullin.App
import com.homework.nasibullin.database.AppDatabase
import com.homework.nasibullin.dataclasses.Actor
import com.homework.nasibullin.dataclasses.Movie
import com.homework.nasibullin.dataclasses.MovieDto
import com.homework.nasibullin.dataclasses.MovieWithActor
import com.homework.nasibullin.datasources.Resource
import com.homework.nasibullin.network.ApiService
import com.homework.nasibullin.network.EmulateNetwork
import com.homework.nasibullin.utils.BaseDataSource
import com.homework.nasibullin.utils.Converters
import com.homework.nasibullin.utils.Utility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieListDataRepo @Inject constructor(): BaseDataSource() {

    /**
     * emulation of downloading movie data from the server. A delay of 2 seconds has been simulated
     * @param number is emulation script number
     */
    suspend fun getRemoteData(number: Int): Flow<Resource<List<MovieDto>>> {
        return flow {
            val result = safeApiCall { App.instance.apiService.getPopularMovies()}
            val resultDto = Converters.fromListMovieResponseToListMovieDto(result)
            emit(resultDto)
        }.flowOn(Dispatchers.IO)
    }

    /**
     * downloading movie data from the local database
     */
    suspend fun getLocalData(): Flow<Resource<List<MovieDto>>> {
        return flow {
            val db = AppDatabase.instance
            val result = getSafeLocalMovies { db.movieDao().getAll()}
            emit(result)
        }.flowOn(Dispatchers.IO)
    }


    /**
     * update data base with actual movies
     */
    suspend fun updateDatabase(movieList: List<MovieDto>){
        val db = AppDatabase.instance
        val dbMovieList = movieList.mapIndexed{ index, movieDto ->
            MovieWithActor(
                movie = Movie(
                    id = index.toLong(),
                    title = movieDto.title,
                    description = movieDto.description,
                    rateScore = movieDto.rateScore,
                    ageRestriction = movieDto.ageRestriction,
                    genre = movieDto.genre,
                    imageUrl = movieDto.imageUrl,
                    posterUrl = movieDto.posterUrl,
                    backId = movieDto.id
                ),
                actors = movieDto.actors.map { actor -> Actor(
                    id = actor.id,
                    name = actor.name,
                    avatarUrl = actor.avatarUrl,
                    movieId = movieDto.id
                ) }
            ) }
        if (db.movieDao().check() == null){
            Utility.showToast("insert", context = App.appContext)
            updateDatabase { db.movieDao().insertMovieWithActors(dbMovieList)}
        }
        else{
            Utility.showToast("update", context = App.appContext)
            updateDatabase { db.movieDao().updateMovieWithActors(dbMovieList)}
        }


    }

}