package com.example.skillcinema.sources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.skillcinema.data.Movie
import com.example.skillcinema.domain.apiUseCases.GetMovieListByGenreUseCase
import javax.inject.Inject

class MovieByGenrePagingSource @Inject constructor(private val getMovieListByGenreUseCase: GetMovieListByGenreUseCase) :
    PagingSource<Int, Movie>() {
    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? = INITIAL_PAGE

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: 1
        val countries: Array<Int> = arrayOf((1..3).random())
        val genres: Array<Int> = arrayOf((1..3).random())
        return kotlin.runCatching {
            if (page <= (INITIAL_PAGE + PAGE_SIZE)) {
                getMovieListByGenreUseCase.getMoviesByGenre(countries, genres, page)
            } else {
                emptyList()
            }
        }.fold(
            onSuccess = {
                LoadResult.Page(
                    data = it,
                    prevKey = if (page > INITIAL_PAGE) page - 1 else null,
                    nextKey = if (it.size == PAGE_SIZE) page + 1 else null
                )
            },
            onFailure = { LoadResult.Error(it) }
        )
    }

    companion object {
        private const val INITIAL_PAGE = 1
        private const val PAGE_SIZE = 10
    }
}