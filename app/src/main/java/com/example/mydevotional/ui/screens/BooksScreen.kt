package com.example.mydevotional.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mydevotional.model.getLocalizedName
import com.example.mydevotional.navigation.AppDestination
import com.example.mydevotional.viewmodel.AccountViewModel
import com.example.mydevotional.viewmodel.VersesViewModel

@Composable
fun BooksScreen(
    navController: NavController,
    viewModel: VersesViewModel,
    accountViewModel: AccountViewModel
) {
    val books by viewModel.books.collectAsState()
    val selectedTranslation by accountViewModel.selectedTranslation.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn {
            items(books) { book ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            viewModel.selectBook(book)
                            navController.navigate(
                                "${AppDestination.
                                BibleChapters.route}/${book.getLocalizedName(selectedTranslation)}"
                            )
                        },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Text(
                        text = book.getLocalizedName(selectedTranslation),
                        modifier = Modifier.padding(16.dp),
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}