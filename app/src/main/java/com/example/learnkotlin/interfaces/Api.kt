package com.example.learnkotlin.interfaces

import com.example.learnkotlin.model.RandomUserDemo
import kotlinx.coroutines.Deferred
import retrofit2.http.GET

interface Api {

    @GET("api")
    fun Api():Deferred<RandomUserDemo>
}