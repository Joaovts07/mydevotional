package com.example.mydevotional.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mydevotional.viewmodel.AccountViewModel

@Composable
fun AccountScreen(viewModel: AccountViewModel = hiltViewModel()) {
    //val readingPercentage by viewModel.readingPercentage.collectAsState(initial = 0f)
    //val userName by viewModel.userName.collectAsState(initial = "")
    //val userEmail by viewModel.userEmail.collectAsState(initial = "")

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

        // Porcentagem de Dias Lidos
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Progresso de Leitura",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                CircularProgressIndicator(
                    //progress = readingPercentage,
                    modifier = Modifier.size(80.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 8.dp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "", // "${(readingPercentage * 100).toInt()}% Lido",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Informações do Usuário
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
            onClick = { /* Lógica para logout */ },
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