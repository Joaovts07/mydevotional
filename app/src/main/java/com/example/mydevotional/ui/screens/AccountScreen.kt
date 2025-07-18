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
import com.example.mydevotional.model.BibleTranslation
import com.example.mydevotional.viewmodel.AccountViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(accountViewModel: AccountViewModel = hiltViewModel()) {
    val selectedTranslation by accountViewModel.selectedTranslation.collectAsState()
    var expanded by remember { mutableStateOf(false) } // Para o DropdownMenu

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
                    .padding(horizontal = 16.dp, vertical = 16.dp)
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
                /*CircularProgressIndicator(
                    progress = readingPercentage,
                    modifier = Modifier.size(80.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 8.dp
                )*/
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "", //"${(readingPercentage * 100).toInt()}% Lido",
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