package com.example.mydevotional.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun ScrollableLazyColumn(
    listState: LazyListState,
    content: LazyListScope.() -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            content()
        }

        // Barra de rolagem na lateral direita
        VerticalScrollbar(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .padding(end = 8.dp),
            scrollState = listState
        )
    }
}
@Composable
fun VerticalScrollbar(
    scrollState: LazyListState,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope() // Mantém o escopo correto para animações
    val totalItems = scrollState.layoutInfo.totalItemsCount
    val viewportHeight = scrollState.layoutInfo.viewportSize.height
    val firstVisibleItem by remember { derivedStateOf { scrollState.firstVisibleItemIndex } }

    val scrollFraction by remember {
        derivedStateOf {
            if (totalItems > 0) {
                firstVisibleItem.toFloat() / (totalItems - 1)
            } else 0f
        }
    }

    val scrollbarHeight = (viewportHeight * 0.2f).coerceAtLeast(20f)

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(12.dp)
            .background(Color.LightGray.copy(alpha = 0.5f), shape = RoundedCornerShape(6.dp))
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->
                    change.consume()

                    val targetItem = (scrollFraction * totalItems) +
                            (dragAmount / viewportHeight) * totalItems

                    coroutineScope.launch {
                        scrollState.animateScrollToItem(targetItem.toInt().coerceIn(0, totalItems - 1))
                    }
                }
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(scrollbarHeight.dp)
                .offset(y = (scrollFraction * (viewportHeight - scrollbarHeight)).dp)
                .background(Color.DarkGray, shape = RoundedCornerShape(6.dp))
        )
    }
}