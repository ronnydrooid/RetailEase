package com.example.retailease.ui.components

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class BiometricPromptManager(
    private val activity: AppCompatActivity,
) {
    private val resultChannel = Channel<BiometricResult?>()
    val result = resultChannel.receiveAsFlow()
    fun showBiometricPrompt(
        title: String,
        description: String,
    ) {
        val manager = BiometricManager.from(activity)
        val authenticators = if (Build.VERSION.SDK_INT >= 30) {
            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
        } else BIOMETRIC_STRONG

        val promptInfo = PromptInfo.Builder()
            .setTitle(title)
            .setDescription(description)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            promptInfo.setAllowedAuthenticators(authenticators)
        }else{
            promptInfo.setDeviceCredentialAllowed(true)
        }


        when (manager.canAuthenticate(authenticators)) {

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                resultChannel.trySend(BiometricResult.BiometricUnavailable)
                return
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                resultChannel.trySend(BiometricResult.AuthenticationNotSetInSettings)
                return
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                resultChannel.trySend(BiometricResult.FeatureUnavailable)
                return
            }

            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                resultChannel.trySend(BiometricResult.BiometricUnavailable)
                return
            }

            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                resultChannel.trySend(BiometricResult.BiometricUnavailable)
                return
            }

            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                resultChannel.trySend(BiometricResult.BiometricUnavailable)
                return
            }

            else -> Unit
        }

        val prompt = BiometricPrompt(
            activity,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    when (errorCode) {

                        BiometricPrompt.ERROR_CANCELED,
                        BiometricPrompt.ERROR_USER_CANCELED -> {
                            resultChannel.trySend(BiometricResult.AuthenticationCancelled)
                        }

                        else -> resultChannel.trySend(BiometricResult.AuthenticationError(errString.toString()))


                    }

                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    resultChannel.trySend(BiometricResult.AuthenticationFailed)
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    resultChannel.trySend(BiometricResult.AuthenticationSucceeded)
                }
            }
        )
        prompt.authenticate(promptInfo.build())

    }
    fun resetResult() {
        resultChannel.trySend(null)
    }


    sealed class BiometricResult {
        data object BiometricUnavailable : BiometricResult()
        data object FeatureUnavailable : BiometricResult()
        data class AuthenticationError(val errorMessage: String) : BiometricResult()
        data object AuthenticationSucceeded : BiometricResult()
        data object AuthenticationCancelled : BiometricResult()
        data object AuthenticationFailed : BiometricResult()
        data object AuthenticationNotSetInSettings : BiometricResult()
    }
}