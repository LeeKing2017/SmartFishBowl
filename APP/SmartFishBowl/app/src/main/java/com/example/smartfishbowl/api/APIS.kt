package com.example.smartfishbowl.api

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface APIS {
    @POST("login") // 파이어베이스 디바이스 토큰값 전송
    @Headers("accept: application/json",
        "content-type: application/json")
    fun sendToken(
        @Body jsonparams: Token
    ): Call<String>

    @POST("update/deviceid")
    @Headers("accept: application/json",
        "content-type: application/json")
    fun currentDevice(
        @Header("Authorization") jwtToken: String,
        @Body jsonparams: CurrentDevice
    ): Call<String>

    @POST("setUserSet")
    @Headers("accept: application/json",
        "content-type: application/json")
    fun settingValue(
        @Header("Authorization") jwtToken: String,
        @Body jsonparams: Setting
    ): Call<String>

    @POST("getSensingData")
    @Headers("accept: application/json",
        "content-type: application/json")
    fun getValues(
        @Header("Authorization") jwtToken: String,
        @Body jsonparams: CurrentDevice
    ): Call<Getting>

    @POST("setFeedTime")
    @Headers("accept: application/json",
        "content-type: application/json")
    fun setTime(
        @Header("Authorization") jwtToken: String,
        @Body jsonparams: FoodSetting
    ): Call<String>

    @GET("signout")
    fun signOut(
        @Header("Authorization") jwtToken: String
    ): Call<String>

    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "http://172.30.3.68:8080/" // 주소

        fun create(): APIS {
            val gson : Gson = GsonBuilder().setLenient().create()
            val ret :APIS= Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(APIS::class.java)
            Log.d("ret=", ret.toString())
            return ret
        }
    }
}