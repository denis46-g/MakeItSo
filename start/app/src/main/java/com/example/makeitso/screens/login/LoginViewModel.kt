/*
Copyright 2022 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.example.makeitso.screens.login

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.makeitso.LOGIN_SCREEN
import com.example.makeitso.R.string as AppText
import com.example.makeitso.SETTINGS_SCREEN
import com.example.makeitso.TASKS_SCREEN
import com.example.makeitso.common.ext.isValidEmail
import com.example.makeitso.common.snackbar.SnackbarManager
import com.example.makeitso.model.User
import com.example.makeitso.model.service.AccountService
import com.example.makeitso.model.service.LogService
import com.example.makeitso.model.service.StorageService
import com.example.makeitso.screens.MakeItSoViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// может, надо поменять import credentials
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID


var email_info = ""
var google_info = ""
var user: MutableState<User> = mutableStateOf(User())

@HiltViewModel
class LoginViewModel @Inject constructor(
  private val accountService: AccountService,
  private val storageService: StorageService,
  logService: LogService
) : MakeItSoViewModel(logService) {
  var uiState = mutableStateOf(LoginUiState())
    private set

  private val email
    get() = uiState.value.email
  private val password
    get() = uiState.value.password

  fun onEmailChange(newValue: String) {
    uiState.value = uiState.value.copy(email = newValue)
  }

  fun onPasswordChange(newValue: String) {
    uiState.value = uiState.value.copy(password = newValue)
  }

  fun onSignInClick(openAndPopUp: (String, String) -> Unit) {
    if (!email.isValidEmail()) {
      SnackbarManager.showMessage(AppText.email_error)
      return
    }

    if (password.isBlank()) {
      SnackbarManager.showMessage(AppText.empty_password_error)
      return
    }

    launchCatching {
      accountService.authenticate(email, password)
      email_info = email
      openAndPopUp(TASKS_SCREEN, LOGIN_SCREEN)
    }
  }

  fun handleGoogleSignIn(context: Context, openAndPopUp: (String, String) -> Unit) {
    viewModelScope.launch {
      googleSignIn(context).collect { result ->
        result.fold(
          onSuccess = {
            launchCatching {
              google_info = accountService.getEmail
              openAndPopUp(TASKS_SCREEN, LOGIN_SCREEN)
            }
          },
          onFailure = { //e ->
            SnackbarManager.showMessage(AppText.google_sign_in_error)
          }
        )
      }
    }
  }

  suspend fun googleSignIn(context: Context): Flow<Result<AuthResult>> {
    val firebaseAuth = FirebaseAuth.getInstance()
    return callbackFlow {
      try {
        // Initialize Credential Manager
        val credentialManager: CredentialManager = CredentialManager.create(context)

        // Generate a nonce (a random number used once)
        val ranNonce: String = UUID.randomUUID().toString()
        val bytes: ByteArray = ranNonce.toByteArray()
        val md: MessageDigest = MessageDigest.getInstance("SHA-1")
        val digest: ByteArray = md.digest(bytes)
        val hashedNonce: String = digest.fold("") { str, it -> str + "%02x".format(it) }
        // Set up Google ID option
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
          .setFilterByAuthorizedAccounts(false)
          .setServerClientId("45271224150-5ctki83u25n2ohuq5c8m4q4vigpvdrdc.apps.googleusercontent.com")
          .setNonce(hashedNonce)
          .build()
        // Request credentials
        val request: GetCredentialRequest = GetCredentialRequest.Builder()
          .addCredentialOption(googleIdOption)
          .build()
        // Get the credential result
        val result = credentialManager.getCredential(context, request)
        val credential = result.credential
        // Check if the received credential is a valid Google ID Token
        if (credential is CustomCredential && credential.type ==
          GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
          val googleIdTokenCredential =
            GoogleIdTokenCredential.createFrom(credential.data)
          val authCredential =
            GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
          val authResult = firebaseAuth.signInWithCredential(authCredential).await()
          trySend(Result.success(authResult))
        } else {
          throw RuntimeException("Received an invalid credential type")
        }
      } catch (e: GetCredentialCancellationException) {
        trySend(Result.failure(Exception("Sign-in was canceled. Please try again.")))
      } catch (e: Exception) {
        trySend(Result.failure(e))
      }
      awaitClose { }
    }
  }

  fun onForgotPasswordClick() {
    if (!email.isValidEmail()) {
      SnackbarManager.showMessage(AppText.email_error)
      return
    }

    launchCatching {
      accountService.sendRecoveryEmail(email)
      SnackbarManager.showMessage(AppText.recovery_email_sent)
    }
  }
}
