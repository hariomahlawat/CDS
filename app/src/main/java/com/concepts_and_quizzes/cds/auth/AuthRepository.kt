package com.concepts_and_quizzes.cds.auth

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.concepts_and_quizzes.cds.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthRepository(private val context: Context) {
    private val credentialManager = CredentialManager.create(context)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val googleRequest: GetCredentialRequest by lazy {
        GetCredentialRequest.Builder()
            .addCredentialOption(
                GetGoogleIdOption.Builder()
                    .setServerClientId(context.getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            .build()
    }

    val currentUser: FirebaseUser? get() = auth.currentUser

    suspend fun startGoogleSignIn(): FirebaseUser? {
        return try {
            val result = credentialManager.getCredential(context, googleRequest)
            (result.credential as? GoogleIdTokenCredential)?.let { firebaseAuthWithGoogle(it.idToken) }
        } catch (e: GetCredentialException) {
            null
        }
    }

    suspend fun trySilentSignIn(): FirebaseUser? {
        return try {
            val result = credentialManager.getCredential(context, googleRequest)
            (result.credential as? GoogleIdTokenCredential)?.let { firebaseAuthWithGoogle(it.idToken) }
        } catch (_: Exception) {
            null
        }
    }

    private suspend fun firebaseAuthWithGoogle(idToken: String): FirebaseUser? =
        suspendCancellableCoroutine { cont ->
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
                .addOnSuccessListener { cont.resume(auth.currentUser) }
                .addOnFailureListener { cont.resumeWithException(it) }
        }

    suspend fun signInWithEmail(email: String, password: String): FirebaseUser? =
        suspendCancellableCoroutine { cont ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { cont.resume(it.user) }
                .addOnFailureListener { cont.resumeWithException(it) }
        }

    suspend fun registerWithEmail(name: String, email: String, password: String): FirebaseUser? =
        suspendCancellableCoroutine { cont ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                    result.user?.updateProfile(profileUpdates)
                        ?.addOnSuccessListener { cont.resume(result.user) }
                        ?.addOnFailureListener { cont.resumeWithException(it) }
                }
                .addOnFailureListener { cont.resumeWithException(it) }
        }

    suspend fun signOut() {
        auth.signOut()
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }
}

