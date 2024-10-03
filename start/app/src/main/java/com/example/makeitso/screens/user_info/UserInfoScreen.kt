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

package com.example.makeitso.screens.user_info

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.makeitso.R.drawable as AppIcon
import com.example.makeitso.R.string as AppText
import com.example.makeitso.common.composable.*
import com.example.makeitso.common.ext.card
import com.example.makeitso.common.ext.fieldModifier
import com.example.makeitso.common.ext.spacer
import com.example.makeitso.common.ext.toolbarActions
import com.example.makeitso.model.Priority
import com.example.makeitso.model.User
import com.example.makeitso.screens.edit_task.EditFlagOption
import com.example.makeitso.screens.edit_task.EditTaskViewModel
import com.example.makeitso.theme.MakeItSoTheme
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

@Composable
@ExperimentalMaterialApi
fun UserInfoScreen(
    popUpScreen: () -> Unit,
    viewModel: UserInfoViewModel = hiltViewModel()
) {
    val user by viewModel.user
    val activity = LocalContext.current as AppCompatActivity

    UserInfoScreenContent(
        user = user,
        onDoneClick = { viewModel.onDoneClick(popUpScreen) },
        onNameChange = viewModel::onNameChange,
        onDateBirthChange = viewModel::onDateBirthChange,
        activity = activity
    )
}

@Composable
@ExperimentalMaterialApi
fun UserInfoScreenContent(
    modifier: Modifier = Modifier,
    user: User,
    onDoneClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onDateBirthChange: (Long) -> Unit,
    activity: AppCompatActivity?
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ActionToolbar(
            title = AppText.user_info,
            modifier = Modifier.toolbarActions(),
            endActionIcon = AppIcon.ic_settings,
            endAction = { onDoneClick() }
        )

        Spacer(modifier = Modifier.spacer())

        val fieldModifier = Modifier.fieldModifier()
        BasicField(AppText.name, user.name, onNameChange, fieldModifier)
        CardEditors(user, onDateBirthChange, activity)
        UnmutableBasicField("Логин: ", fieldModifier)
        UnmutableBasicField("Метод аутентификации: ", fieldModifier)
        //BasicField(AppText.login, task.url, onUrlChange, fieldModifier)
        //BasicField(AppText.auth_method, task.url, onUrlChange, fieldModifier)
    }
}

@Composable
fun UnmutableBasicField(
    text: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        singleLine = true,
        value = text,
        modifier = modifier,
        onValueChange = { }
    )
}

@ExperimentalMaterialApi
@Composable
private fun CardEditors(
    user: User,
    onDateChange: (Long) -> Unit,
    activity: AppCompatActivity?
) {
    RegularCardEditor(AppText.birth_date, AppIcon.ic_calendar, user.birthDate, Modifier.card()) {
        showDatePicker(activity, onDateChange)
    }
}

private fun showDatePicker(activity: AppCompatActivity?, onDateChange: (Long) -> Unit) {
    val picker = MaterialDatePicker.Builder.datePicker().build()

    activity?.let {
        picker.show(it.supportFragmentManager, picker.toString())
        picker.addOnPositiveButtonClickListener { timeInMillis -> onDateChange(timeInMillis) }
    }
}

@Preview(showBackground = true)
@ExperimentalMaterialApi
@Composable
fun UserInfoScreenPreview() {
    val user = User(
        name = "User name"
    )

    MakeItSoTheme {
        UserInfoScreenContent(
            user = user,
            onDoneClick = { },
            onNameChange = { },
            onDateBirthChange = { },
            activity = null
        )
    }
}