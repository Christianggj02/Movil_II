package com.CL.sicenet.network

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface siceApiService {
    @Headers(
        "Content-Type: text/xml",
        "SOAPAction: http://tempuri.org/accesoLogin"
    )
    @POST("ws/wsalumnos.asmx")
    //fun getAccess(@Body request: String): Call<ResponseBody>
    fun getAccess(@Body recuest: RequestBody): Call<ResponseBody>

    @GET("ws/wsalumnos.asmx")
    fun getCookie(): Call<ResponseBody>

    @Headers(
        "Content-Type: text/xml",
        "SOAPAction: http://tempuri.org/getAlumnoAcademicoWithLineamiento"
    )
    @POST("ws/wsalumnos.asmx")
    fun getInfo(@Body recuest: RequestBody): Call<ResponseBody>
}