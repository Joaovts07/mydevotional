package com.example.mydevotional.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ScrollableList() {
    val scrollState = rememberScrollState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        items(items = List(100) { "Item $it" }) { item ->
            Text(text = item, modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
fun Scrollbar(
    scrollState: ScrollState,
    maxScroll: Int,
    height: Dp,
    modifier: Modifier = Modifier
) {
    val thumbHeight = remember(maxScroll, scrollState.value) {
        if (maxScroll <= 0) return@remember 0.dp
        val visibleRatio = (scrollState.value.toFloat() / maxScroll).coerceIn(0f, 1f)
        (height * visibleRatio).value.dp
    }

    Canvas(modifier = modifier.height(height)) {
        if (maxScroll <= 0) return@Canvas
        val y = (scrollState.value.toFloat() / maxScroll) * size.height
        drawRect(
            color = Color.Gray,
            topLeft = Offset(0f, y),
            size = Size(4.dp.toPx(), thumbHeight.toPx())
        )
    }
}

@Composable
fun ScrollableListWithScrollbar() {
    val scrollState = rememberScrollState()
    var scrollbarHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            items(items = List(100) { "Item $it" }) { item ->
                Text(text = item, modifier = Modifier.padding(16.dp))
            }
        }

        Layout(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterEnd),
            content = {}
        ) { _, constraints ->
            val height = constraints.maxHeight.toDp(density)
            scrollbarHeight = height
            layout(0, constraints.maxHeight) {}
        }

        Scrollbar(
            scrollState = scrollState,
            maxScroll = scrollState.maxValue,
            height = scrollbarHeight,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

private fun Int.toDp(density: androidx.compose.ui.unit.Density): Dp {
    return with(density) { this@toDp.toDp() }
}