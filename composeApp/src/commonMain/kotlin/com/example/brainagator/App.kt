package com.example.brainagator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.button_name
import brainagator.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.stringResource

@Composable
@Preview
fun App() {
    MaterialTheme {
        val viewModel: Test1ViewModel = viewModel { Test1ViewModel() }
        var showContent by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = { showContent = !showContent }) {
                Text(stringResource(Res.string.button_name))
            }
            DraggableScreen(
                modifier = Modifier.fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))) {
                Test1Screen(viewModel)
            }
            AnimatedVisibility(showContent) {
                val greeting = remember { Greeting().greet() }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text("Compose: $greeting")
                }
            }
        }
    }
}

internal val LocalDragTargetInfo = compositionLocalOf { DragTargetInto() }

class DragTargetInto {
    var isDragging by mutableStateOf(false)
    var dragPosition by mutableStateOf(Offset.Zero)
    var dragOffset by mutableStateOf(Offset.Zero)
    var draggableComposeable by mutableStateOf<(@Composable () -> Unit)?>(null)
    var dataToDrop by mutableStateOf<Any?>(null)
}

@Composable
fun <T> DragTarget(
    modifier: Modifier = Modifier,
    dataToDrop: T,
    viewModel: Test1ViewModel,
    content: @Composable (() -> Unit)
) {
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    val currentState = LocalDragTargetInfo.current

    Box(
        modifier = modifier
            .onGloballyPositioned {
                currentPosition = it.localToWindow(Offset.Zero)
            }
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        viewModel.startDragging()
                        currentState.dataToDrop = dataToDrop
                        currentState.isDragging = true
                        currentState.dragPosition = currentPosition + offset
                        currentState.draggableComposeable = content
                    }, onDrag = { change, dragAmount ->
                        change.consumeAllChanges()
                        currentState.dragOffset += Offset(dragAmount.x, dragAmount.y)
                    }, onDragEnd = {
                        viewModel.stopDragging()
                        currentState.dragOffset = Offset.Zero
                        currentState.isDragging = false
                    }, onDragCancel = {
                        viewModel.stopDragging()
                        currentState.dragOffset = Offset.Zero
                        currentState.isDragging = false
                    })
            }
    ) {
        content()
    }
}

@Composable
fun <T> DropItem(
    modifier: Modifier,
    content: @Composable (BoxScope.(isInBound: Boolean, data:T?) -> Unit)
) {
    val dragInfo = LocalDragTargetInfo.current
    val dragPosition = dragInfo.dragPosition
    val dragOffset = dragInfo.dragOffset
    var isCurrentDrapTarget by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .onGloballyPositioned {
                it.boundsInWindow().let { rect ->
                    isCurrentDrapTarget = rect.contains(dragPosition + dragOffset)
                }
            }) {
        val data = if (isCurrentDrapTarget && !dragInfo.isDragging) dragInfo.dataToDrop as? T? else null
        content(isCurrentDrapTarget, data)
    }
}


@Composable
fun DraggableScreen(
    modifier: Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val state = remember { DragTargetInto() }
    CompositionLocalProvider(LocalDragTargetInfo provides state) {
        Box(modifier = modifier.fillMaxSize()){
            content()
            if (state.isDragging) {
                var targetSize by remember {
                    mutableStateOf(IntSize.Zero)
                }
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            val offset = (state.dragPosition + state.dragOffset)
                            scaleX = 1.3f
                            scaleY = 1.3f
                            alpha = if (targetSize == IntSize.Zero) 0f else .9f
                            translationX = offset.x.minus(targetSize.width/2)
                            translationY = offset.y.minus(targetSize.height/2)
                        }
                        .onGloballyPositioned {
                            targetSize = it.size
                        }
                ){
                    state.draggableComposeable?.invoke()
                }
            }
        }
    }
}

@Composable
fun Test1Screen(
    test1ViewModel: Test1ViewModel
) {
    //val screenWirth = LocalConfiguration.current.screenWidth

    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            test1ViewModel.items.forEach { person ->
                DragTarget(
                    dataToDrop = person,
                    viewModel = test1ViewModel,
                ) {
                    Box(modifier = Modifier.size(80.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .shadow(5.dp, RoundedCornerShape(15.dp))
                        .background(person.backgroundColor, RoundedCornerShape(15.dp)),
                        contentAlignment = Alignment.Center
                        ) {
                        Text(
                            text = person.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            AnimatedVisibility(
                test1ViewModel.isCurrentlyDragging,
                enter = slideInHorizontally(initialOffsetX = {it})
            ) {
                DropItem<Person>(
                    modifier = Modifier.size(80.dp)
                ) { isInBound, personItem ->
                    if (personItem != null) {
                        LaunchedEffect(key1 = personItem) {
                            test1ViewModel.addPerson(personItem)
                        }
                    }
                    if (isInBound) {
                        Box(modifier = Modifier.size(80.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .shadow(5.dp, RoundedCornerShape(15.dp))
                            .border(1.dp, color = Color.Red, RoundedCornerShape(15.dp))
                            .background(Color.Gray.copy(0.5f), RoundedCornerShape(15.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Add person",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    } else {
                        Box(modifier = Modifier.size(80.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .shadow(5.dp, RoundedCornerShape(15.dp))
                            .border(1.dp, color = Color.White, RoundedCornerShape(15.dp))
                            .background(Color.Black.copy(0.6f), RoundedCornerShape(15.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Add person",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }
    }
}


class Test1ViewModel: ViewModel() {
    var isCurrentlyDragging by mutableStateOf(false)
    private set

    var items by mutableStateOf(emptyList<Person>())

    var addedPersons = mutableStateListOf<Person>()

    init {
        items = listOf(
            Person("Stanislaw", "1", Color.Red),
            Person("Wladek", "2", Color.Blue),
            Person("Omar", "3", Color.Black)
        )
    }

    fun startDragging() {
        isCurrentlyDragging = true
    }

    fun stopDragging() {
        isCurrentlyDragging = false
    }

    fun addPerson(person: Person) {
        addedPersons.add(person)
    }
}

data class Person(
    val name: String,
    val id: String,
    val backgroundColor: Color
)