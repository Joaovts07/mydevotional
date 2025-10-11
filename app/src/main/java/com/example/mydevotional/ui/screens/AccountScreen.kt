package com.example.mydevotional.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.login.login.LoginState
import com.example.login.login.LoginViewModel
import com.example.mydevotional.model.BibleTranslation
import com.example.mydevotional.viewmodel.AccountViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    accountViewModel: AccountViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel(),
    onLogout: () -> Unit = { }
) {
    val user by accountViewModel.localUser.collectAsState()
    val selectedTranslation by accountViewModel.selectedTranslation.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    val loginState by loginViewModel.loginState.collectAsState()

    when (loginState) {
        is LoginState.Logged -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Minha Conta", fontSize = 24.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    TextField(
                        readOnly = true,
                        value = selectedTranslation.displayName,
                        onValueChange = { },
                        label = { Text("Tradução da Bíblia") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
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

                Spacer(modifier = Modifier.height(16.dp))

                if(user != null) {
                    OutlinedTextField(
                        value = user!!.name,
                        onValueChange = {},
                        label = { Text("Nome") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = user!!.email,
                        onValueChange = {},
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true
                    )
                }


                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        loginViewModel.logout()
                        onLogout()
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Sair", color = Color.White)
                }
            }
        }

        is LoginState.Logout, LoginState.Idle -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Você ainda não está logado", fontSize = 20.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { /* abre tela de login */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Fazer login")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { /* continua sem login */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continuar sem login")
                }
            }
        }

        is LoginState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is LoginState.Error -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Erro ao logar: ${(loginState as LoginState.Error).message}")
                Spacer(Modifier.height(16.dp))
                OutlinedButton(onClick = { loginViewModel.logout() }) {
                    Text("Tentar novamente")
                }
            }
        }
    }
}
