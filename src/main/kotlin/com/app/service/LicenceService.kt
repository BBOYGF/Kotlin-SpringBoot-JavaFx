package com.app.service

import com.app.pojo.Obj
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface LicenceService {
    /**
     * 根据 id 请求 Licence
     */
    @GET("/todos/{id}")
    fun requestLicenceById(@Path("id") id: Int): Call<Obj>

    @GET("/todos/{id}")
    suspend fun requestLicenceByIdSuspend(@Path("id") id: Int): Obj

}