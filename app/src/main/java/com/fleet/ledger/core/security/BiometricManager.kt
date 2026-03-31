package com.fleet.ledger.core.security

import android.content.Context
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * Biyometrik kimlik doğrulama yöneticisi
 * Parmak izi ve yüz tanıma desteği
 */
class BiometricManager(private val context: Context) {
    
    fun isBiometricAvailable(): Boolean {
        // TODO: BiometricManager.from(context).canAuthenticate() kontrolü
        return true
    }
    
    fun authenticate(
        activity: FragmentActivity,
        title: String = "Kimlik Doğrulama",
        subtitle: String = "Devam etmek için kimliğinizi doğrulayın",
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(context)
        
        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }
                
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errString.toString())
                }
                
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onError("Kimlik doğrulama başarısız")
                }
            }
        )
        
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText("İptal")
            .build()
        
        biometricPrompt.authenticate(promptInfo)
    }
}
