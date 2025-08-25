package com.example.agritech.data

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.agritech.MainActivity
import com.example.agritech.remote.ForgotPasswordRequest
import com.example.agritech.remote.LoginRequest
import com.example.agritech.remote.LoginResponse
import com.example.agritech.remote.ResetPasswordRequest
import com.example.agritech.remote.SignUpRequest
import com.example.agritech.remote.authApi
import com.example.agritech.remote.extractErrorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext

sealed class Error : Exception() {
    data class ValidationException(val field: String, override val message: String) : Error()
    data class BadResponse(override val message: String) : Error()
}


class AuthViewModel : ViewModel() {
    companion object {
        const val MIN_USERNAME_LEN: Int = 4
        const val MIN_PASSWORD_LEN: Int = 6
        const val MIN_OTP_LEN: Int = 6
    }

    var username by mutableStateOf<String>("")
    var email by mutableStateOf<String>("")
    var password by mutableStateOf<String>("")
    var confirmPassword by mutableStateOf<String>("")
    var otp by mutableStateOf<String>("")

    val errors = MutableSharedFlow<Error>()

    suspend fun createAccount(): Boolean = withContext((Dispatchers.IO)) {
        try {
            validateUsername()
            validateEmail()
            validatePassword(password)
            matchPasswordAndConfirmPassword()

            Log.d(MainActivity.TAG, "createAccount()")
            val request = SignUpRequest(
                username = username,
                email = email,
                password = password,
            )
            val response = authApi.createAccount(request)
            val isSuccessful = response.isSuccessful
            if (!isSuccessful) {
                val errorMessage: String? = response.extractErrorMessage()
                errorMessage?.let {
                    errors.emit(Error.BadResponse(it))
                }
            }
            return@withContext isSuccessful
        } catch (e: Error.ValidationException) {
            errors.emit(e)
            return@withContext false
        } catch (e: Exception) {
            Log.e(MainActivity.TAG, "createAccount() => ${e.message}")
            return@withContext false
        }
    }

    suspend fun loginUser(): Boolean = withContext((Dispatchers.IO)) {
        try {
            validateEmail()
            validatePassword(password)

            Log.d(MainActivity.TAG, "loginUser()")
            val request = LoginRequest(
                email = email,
                password = password,
            )
            val response = authApi.login(request)
            if (response.isSuccessful) {
                val loginResponse: LoginResponse = response.body() ?: return@withContext false

                // Store access token in sharedPreferences
                val context = MainActivity.instance.applicationContext
                val sharedPreferences = context.getSharedPreferences(
                    MainActivity.SHARED_PREFERENCES,
                    Context.MODE_PRIVATE
                )
                sharedPreferences.edit()
                    .putString(MainActivity.ACCESS_TOKEN, loginResponse.accessToken).apply()

                return@withContext true
            } else {
                // Parse error message from response body
                val errorMessage: String? = response.extractErrorMessage()
                errorMessage?.let {
                    errors.emit(Error.BadResponse(it))
                }
                return@withContext false
            }

        } catch (e: Error.ValidationException) {
            errors.emit(e)
            return@withContext false
        } catch (e: Exception) {
            Log.e(MainActivity.TAG, "loginUser() => ${e.message}")
            return@withContext false
        }
    }

    suspend fun forgotPassword(): Boolean = withContext((Dispatchers.IO)) {
        try {
            validateEmail()

            Log.d(MainActivity.TAG, "forgotPassword()")
            val request = ForgotPasswordRequest(
                email = email
            )
            val response = authApi.forgotPassword(request)
            if (response.isSuccessful) {
                return@withContext true
            } else {
                val errorMessage: String? = response.extractErrorMessage()
                errorMessage?.let {
                    errors.emit(Error.BadResponse(it))
                }
                return@withContext false
            }
        } catch (e: Error.ValidationException) {
            errors.emit(e)
            return@withContext false
        } catch (e: Exception) {
            Log.e(MainActivity.TAG, "forgotPassword() => ${e.message}")
            return@withContext false
        }
    }

    suspend fun resetPassword(): Boolean = withContext((Dispatchers.IO)) {
        try {
            validateEmail()
            validatePassword(password)
            matchPasswordAndConfirmPassword()
            validateOTP()

            Log.d(MainActivity.TAG, "resetPassword()")
            val request = ResetPasswordRequest(
                email = email,
                otp = otp,
                newPassword = password,
                confirmNewPassword = confirmPassword,
            )
            val response = authApi.resetPassword(request)
            if (response.isSuccessful) {
                return@withContext true
            } else {
                val errorMessage: String? = response.extractErrorMessage()
                errorMessage?.let {
                    errors.emit(Error.BadResponse(it))
                }
                return@withContext false
            }
        } catch (e: Error.ValidationException) {
            errors.emit(e)
            return@withContext false
        } catch (e: Exception) {
            Log.e(MainActivity.TAG, "forgotPassword() => ${e.message}")
            return@withContext false
        }
    }

    fun logout(): Boolean {
        // Remove access token from sharedPreferences
        val context = MainActivity.instance.applicationContext
        val sharedPreferences =
            context.getSharedPreferences(MainActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(MainActivity.ACCESS_TOKEN, "")
        return editor.commit()
    }

    private fun validateUsername() {
        if (username.length < MIN_USERNAME_LEN) {
            throw Error.ValidationException(field = "username", message = "Username too short")
        }
    }

    private fun validateEmail() {
        if (email.isEmpty()) {
            throw Error.ValidationException(field = "email", message = "Email cannot be empty")
        }
        if (!email.contains("@") || !email.contains(".com")) {
            throw Error.ValidationException(field = "email", message = "Invalid email")
        }
    }

    private fun validatePassword(password: String) {
        if (password.length < MIN_PASSWORD_LEN) {
            throw Error.ValidationException(field = "password", message = "Password too short")
        }
    }

    private fun validateOTP() {
        if (otp.length < MIN_OTP_LEN) {
            throw Error.ValidationException(field = "otp", message = "OTP too short")
        }
    }

    private fun matchPasswordAndConfirmPassword() {
        if (password != confirmPassword) {
            throw Error.ValidationException(
                field = "confirmPassword",
                message = "Password and confirmPassword does NOT match"
            )
        }
    }
}