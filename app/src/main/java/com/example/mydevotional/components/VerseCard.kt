package com.example.mydevotional.components

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mydevotional.ReadVerseWithTTS
import com.example.mydevotional.model.Verses

@Composable
fun VerseCard(
    verse: Verses,
    onFavoriteClick: (Verses) -> Unit = {}
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = verse.text,
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "- ${verse.book_name} ${verse.chapter}:${verse.verse}",
                    fontStyle = FontStyle.Italic
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onFavoriteClick(verse) }
                    ) {
                        Icon(
                            imageVector =
                                if (verse.isFavorite) Icons.Filled.Favorite else
                                    Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favoritar verso",
                            tint = if (verse.isFavorite) Color.Red else Color.Gray
                        )
                    }

                    ReadVerseWithTTS(verse.text)

                    IconButton(
                        onClick = {
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "${verse.text}\n- ${verse.book_name} ${verse.chapter}:${verse.verse}")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Compartilhar Versículo"))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Compartilhar Versículo",
                            tint = MaterialTheme.colorScheme.onSurface // Cor do ícone
                        )
                    }
                }
            }
        }
    }
}