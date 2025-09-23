package com.example.login.ui.screens

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.login.BuildConfig
import com.example.login.login.LoginState
import com.example.login.login.LoginUiState
import com.example.login.login.LoginViewModel
import com.example.login.ui.components.EmailInput
import com.example.login.ui.components.GoogleSignInButton
import com.example.login.ui.components.LoadingButton
import com.example.login.ui.components.PasswordInput
import com.example.mylogin.ui.theme.MyLoginTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val loginState by viewModel.loginState.collectAsState()
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.handleGoogleSignInResult(result)
    }
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Login") }) }
    ) { innerPadding ->
        LoginContent(
            uiState = uiState,
            loginState = loginState,
            onEmailChange = viewModel::onEmailChange,
            onPasswordChange = viewModel::onPasswordChange,
            onLoginClick = viewModel::login,
            onGoogleSignInClick = { launchGoogleSignIn(context, viewModel, launcher) },
            onRegisterClick = { navController.navigate("basicForm") },
            onLoginSuccess = onLoginSuccess,
            onLogout = onLogout,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        )
    }
}

@Composable
fun LoginContent(
    uiState: LoginUiState,
    loginState: LoginState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmailInput(email = uiState.email, onEmailChange = onEmailChange)
        Spacer(modifier = Modifier.height(16.dp))
        PasswordInput(password = uiState.password, onPasswordChange = onPasswordChange)
        Spacer(modifier = Modifier.height(24.dp))

        uiState.errorMessage?.let { errorMessage ->
            Text(text = errorMessage, color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
        }

        when (loginState) {
            is LoginState.Logged -> onLoginSuccess()
            is LoginState.Error -> {
                Text(text = loginState.message, color = Color.Red)
                Spacer(modifier = Modifier.height(8.dp))
            }
            is LoginState.Logout -> onLogout()
            else -> {}
        }

        LoadingButton(
            onClick = onLoginClick,
            isLoading = uiState.isLoading,
            text = "Login"
        )

        Spacer(modifier = Modifier.height(18.dp))

        GoogleSignInButton(onClick = onGoogleSignInClick)

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Não tem uma conta? Cadastre-se",
            modifier = Modifier
                .clickable { onRegisterClick() }
                .padding(top = 8.dp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

fun launchGoogleSignIn(context: Context, viewModel: LoginViewModel, launcher: ActivityResultLauncher<Intent>) {

    val googleClientId = BuildConfig.GOOGLE_CLIENT_ID
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(googleClientId)
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)
    launcher.launch(googleSignInClient.signInIntent)
}



@Composable
fun LoginNavigation(navController: NavHostController, routeSuccess: String, onLoginSuccess: @Composable () -> Unit) {
    MyLoginTheme {
        NavHost(navController = navController, startDestination = "login") {
            composable(routeSuccess) {
                onLoginSuccess()
            }
            composable("login") {
                LoginScreen(
                    navController = navController,
                    onLoginSuccess = {
                        navController.navigate(routeSuccess) {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onLogout = {
                        navController.navigate("login")
                    }
                )
            }
            composable("basicForm") { RegistrationBasicScreen(navController) }
            composable(
                "choiseForm/{nome}/{dataNascimento}",
                arguments = listOf(
                    navArgument("nome") { type = NavType.StringType },
                    navArgument("dataNascimento") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                RegistrationChoiseScreen(
                    navController,
                    backStackEntry.arguments?.getString("nome") ?: "",
                    backStackEntry.arguments?.getString("dataNascimento") ?: ""
                )

            }
            composable("confirmationScreen/{verificationType}/{id}/{verificationId}") { backStackEntry ->
                val verificationType = backStackEntry.arguments?.getString("verificationType") ?: "email"
                val id = backStackEntry.arguments?.getString("id") ?: ""
                val verificationId = backStackEntry.arguments?.getString("verificationId") ?: ""
                ConfirmationScreen(
                    navController,
                    verificationType = verificationType,
                    id = id,
                    verificationId = verificationId
                )
            }
        }
    }
}

