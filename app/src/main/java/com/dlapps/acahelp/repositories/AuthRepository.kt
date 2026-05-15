package com.dlapps.acahelp.repositories

import android.util.Log
import com.dlapps.acahelp.api.ApiException
import com.dlapps.acahelp.api.ApiResponse
import com.dlapps.acahelp.api.AuthApi
import com.dlapps.acahelp.api.LoginRequest
import com.dlapps.acahelp.api.LoginResponse
import com.dlapps.acahelp.api.RegisterRequest
import com.dlapps.acahelp.api.RegisterResponse
import com.dlapps.acahelp.api.RetrofitClient
import com.dlapps.acahelp.data.local.TokenManager
import com.dlapps.acahelp.screens.RegisterState
import com.google.gson.Gson
import okio.IOException
import retrofit2.Response

class AuthRepository (
    private val tokenManager: TokenManager
) {

    private val api: AuthApi = RetrofitClient.api

    suspend fun login(email: String, password: String): Result<Unit>/*Response<ApiResponse<LoginResponse>>*/ {
        return try {
            val result = api.login(LoginRequest(email, password))
            if(result.isSuccessful) {
                val body = result.body()
                Log.d("LoginScreen", body.toString())
                Log.d("LoginScreen", "Botón presionado")
                val token = body?.data?.token
                val expiresAt = body?.data?.expiresAt
                if(!token.isNullOrEmpty() && !expiresAt.isNullOrEmpty()){
                    tokenManager.saveToken(token,expiresAt )
                }
                Result.success(Unit)
            }else{
                val errorJson = result.errorBody()?.string()
                val gson = Gson()
                val errorBody = gson.fromJson(errorJson, ApiResponse::class.java)
                val code = errorBody.messageCode
                Log.e("API", "HTTP error: ${errorJson}")

                Result.failure(ApiException(errorBody.messageCode))
            }

        } catch (e: IOException) {
            Result.failure(e)
        }


    }

    suspend fun register(payload: RegisterRequest): Response<ApiResponse<RegisterResponse>> {
        return api.register(payload)
    }
}



