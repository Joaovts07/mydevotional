package com.example.mydevotional.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.login.presentation.login.LoginState
import com.example.login.presentation.login.LoginViewModel
import com.example.login.ui.screens.LoginContent
import com.example.login.ui.screens.launchGoogleSignIn
import com.example.mydevotional.model.BibleTranslation
import com.example.mydevotional.ui.theme.MyDevotionalTheme
import com.example.mydevotional.viewmodel.AccountViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    accountViewModel: AccountViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel(),
    onLogout: () -> Unit = { }
) {
    val selectedTranslation by accountViewModel.selectedTranslation.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    val loginState by loginViewModel.loginState.collectAsState()
    val uiState by loginViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        loginViewModel.handleGoogleSignInResult(result)
    }

    when(loginState) {
        is LoginState.Logout -> onLogout
        is LoginState.Logged -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Minha Conta",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { newExpandedState -> expanded = newExpandedState }
                        ) {
                            TextField(
                                readOnly = true,
                                value = selectedTranslation.displayName,
                                onValueChange = { },
                                label = { Text("Tradução da Bíblia") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                BibleTranslation.entries.forEach { translation ->
                                    DropdownMenuItem(
                                        text = { Text(translation.displayName) },
                                        onClick = {
                                            accountViewModel.setTranslation(translation)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                Text(
                    text = "Informações do Usuário",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Start
                )

                OutlinedTextField(
                    value = "",//userName,
                    onValueChange = { /* Lógica para alterar o nome (se necessário) */ },
                    label = { Text("Nome") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true // Por enquanto, apenas exibição
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = "",//userEmail,
                    onValueChange = { /* Lógica para alterar o email (se necessário) */ },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true // Por enquanto, apenas exibição
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = "***", // Exibindo senha de forma segura
                    onValueChange = { /* Lógica para alterar a senha (se necessário) */ },
                    label = { Text("Senha") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { loginViewModel.logout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    //shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Sair", color = Color.White)
                }


            }
        }
        else -> {
            LoginContent(
                uiState = uiState,
                loginState = loginState,
                onEmailChange = loginViewModel::onEmailChange,
                onPasswordChange = loginViewModel::onPasswordChange,
                onLoginClick = loginViewModel::login,
                onGoogleSignInClick = { launchGoogleSignIn(context, loginViewModel, launcher) },
                onRegisterClick = { /* Lógica para navegação de cadastro */ },
                onLoginSuccess = { /* Lógica de navegação após login */ },
                onLogout = { /* Lógica de navegação após logout */ },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
fun AccountScreenPreview() {
    MyDevotionalTheme {
        AccountScreen(

        )
    }
}