package com.anizmocreations.bmstask.service

import com.anizmocreations.bmstask.model.*
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface IMoviesService {

    @GET("movie/now_playing")
    @Headers("Accept: application/json", "Content-Type: application/json")
    fun getNowPlayingMovies(@Query("page") page: Int): Observable<NowPlayingResponse>

    @GET("movie/{movie_id}")
    @Headers("Accept: application/json", "Content-Type: application/json")
    fun getDetails(@Path("movie_id") movieId: Int): Observable<MovieDetailsResponse>

    @GET("movie/{movie_id}/reviews")
    @Headers("Accept: application/json", "Content-Type: application/json")
    fun getReviews(@Path("movie_id") movieId: Int, @Query("page") page: Int): Observable<ReviewsResponse>

    @GET("movie/{movie_id}/similar")
    @Headers("Accept: application/json", "Content-Type: application/json")
    fun getSimilar(@Path("movie_id") movieId: Int, @Query("page") page: Int): Observable<SimilarMovieResponse>

    @GET("movie/{movie_id}/credits")
    @Headers("Accept: application/json", "Content-Type: application/json")
    fun getCredits(@Path("movie_id") movieId: Int): Observable<CastAndCrewResponse>

    @GET("search/movie")
    @Headers("Accept: application/json", "Content-Type: application/json")
    fun searchMovie(@Query("query") query: String, @Query("page") page: Int): Observable<SimilarMovieResponse>

}