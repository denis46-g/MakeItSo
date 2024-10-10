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

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.makeitso.R.string as AppText
import com.example.makeitso.common.composable.*
import com.example.makeitso.common.ext.basicButton
import com.example.makeitso.common.ext.fieldModifier
import com.example.makeitso.common.ext.textButton
import com.example.makeitso.theme.MakeItSoTheme

// добавлено
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.Text
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.background
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material.Surface
import androidx.compose.ui.graphics.Color
import com.example.makeitso.R.drawable.google_icon as google_icon

var login = false
var google = false

@Composable
fun LoginScreen(
  openAndPopUp: (String, String) -> Unit,
  viewModel: LoginViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState
  val context = LocalContext.current as Activity

  LoginScreenContent(
    uiState = uiState,
    onEmailChange = viewModel::onEmailChange,
    onPasswordChange = viewModel::onPasswordChange,
    onSignInClick = { viewModel.onSignInClick(openAndPopUp) },
    googleSignIn = { viewModel.handleGoogleSignIn(context, openAndPopUp) },
    onForgotPasswordClick = viewModel::onForgotPasswordClick
  )
}

@Composable
fun LoginScreenContent(
  modifier: Modifier = Modifier,
  uiState: LoginUiState,
  onEmailChange: (String) -> Unit,
  onPasswordChange: (String) -> Unit,
  onSignInClick: () -> Unit,
  googleSignIn: () -> Unit,
  onForgotPasswordClick: () -> Unit
) {
  BasicToolbar(AppText.login_details)

  if(login)
    login = false
  if(google)
    google = false

  Column(
    modifier = modifier
      .fillMaxWidth()
      .fillMaxHeight()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    EmailField(uiState.email, onEmailChange, Modifier.fieldModifier())
    PasswordField(uiState.password, onPasswordChange, Modifier.fieldModifier())

    BasicButton(AppText.sign_in, Modifier.basicButton()) {
      login = true
      onSignInClick() }

    BasicButton(AppText.sign_in_google, Modifier.basicButton()) {
      google = true
      googleSignIn() }

    BasicTextButton(AppText.forgot_password, Modifier.textButton()) {
      onForgotPasswordClick()
    }
  }
}

/*@Composable
fun LoginScreen(
  viewModel: LoginViewModel = LoginViewModel(),
) {
  val isDarkTheme = isSystemInDarkTheme()
  val context = LocalContext.current as Activity
  val onClick = { viewModel.handleGoogleSignIn(context) }
  Box(
    contentAlignment = Alignment.Center
  ) {
    Surface(
      shape = CircleShape,
      color = when (isDarkTheme) {
        true -> Color(0xFF131314)
        false -> Color(0xFFFFFFFF)
      },
      modifier = Modifier
        .height(50.dp)
        .width(260.dp)
        .clip(CircleShape)
        .border(
          BorderStroke(
            width = 1.dp,
            color = when (isDarkTheme) {
              true -> Color(0xFF8E918F)
              false -> Color.Transparent
            }
          ),
          shape = CircleShape
        )
        .clickable { onClick() }
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 5.dp)
      ) {
        Spacer(modifier = Modifier.width(14.dp))
        Image(
          painterResource(id = googleIcon),
          contentDescription = null,
          modifier = Modifier.padding(vertical = 5.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "Continue with Google")
        Spacer(modifier = Modifier.weight(1f))
      }
    }
  }
}*/

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
  val uiState = LoginUiState(
    email = "email@test.com"
  )

  MakeItSoTheme {
    LoginScreenContent(
      uiState = uiState,
      onEmailChange = { },
      onPasswordChange = { },
      onSignInClick = { },
      googleSignIn = { },
      onForgotPasswordClick = { }
    )
  }
}
