package com.dlapps.acahelp.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class ApiResponse<T>(
    val success: Boolean,
    val message: String?,
    val messageCode: String,
    val data: T?
)

data class LoginRequest(
    val Email: String,
    val Password: String
)

data class LoginResponse(

    val date: String,
    val token : String,
    val expiresAt: String
)

data class RegisterRequest(
    val name: String,
    val lastName: String,
    val email: String,
    val password: String,
)

data class RegisterResponse(
    val name: String,
    val email : String,
    val id: Int
)


interface AuthApi {

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest) : Response<ApiResponse<LoginResponse>>

    @POST("api/users")
    suspend fun register(@Body body: RegisterRequest) : Response<ApiResponse<RegisterResponse>>

}