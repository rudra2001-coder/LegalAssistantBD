package com.rudra.legalassistantbd.ui.ai

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.rudra.legalassistantbd.ai.AIResponse
import com.rudra.legalassistantbd.ai.OfflineLegalAI
import com.rudra.legalassistantbd.ui.components.*
import com.rudra.legalassistantbd.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatMessage(
    val id: Int,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@HiltViewModel
class AIViewModel @Inject constructor(
    private val offlineLegalAI: OfflineLegalAI
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = 0,
                text = "আসসালামু আলাইকুম! আমি আপনার আইনগত সহায়ক। আপনি আইন সম্পর্কিত যেকোনো প্রশ্ন করতে পারেন।\n\nHello! I'm your legal assistant. Ask me anything about Bangladesh law.",
                isUser = false
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var nextId = 1

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val userMsg = ChatMessage(
            id = nextId++,
            text = text,
            isUser = true
        )
        _messages.update { it + userMsg }
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = offlineLegalAI.processQuery(text)
                val aiMsg = ChatMessage(
                    id = nextId++,
                    text = response.answer,
                    isUser = false
                )
                _messages.update { it + aiMsg }
            } catch (e: Exception) {
                _messages.update {
                    it + ChatMessage(
                        id = nextId++,
                        text = "Sorry, I encountered an error. Please try again.",
                        isUser = false
                    )
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
}

@Composable
fun AIChatScreen(
    navController: NavController,
    viewModel: AIViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopBar(title = "AI Legal Assistant", onBackClick = { navController.popBackStack() })
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { message ->
                    ChatBubble(message = message)
                }
                if (isLoading) {
                    item {
                        ChatBubble(
                            ChatMessage(
                                id = -1,
                                text = "Thinking...",
                                isUser = false
                            )
                        )
                    }
                }
            }

            Surface(
                color = DarkSurface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Ask a legal question...", color = GrayMedium) },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        colors = fieldColors()
                    )
                    Spacer(Modifier.width(8.dp))
                    FilledIconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendMessage(inputText)
                                inputText = ""
                            }
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Gold,
                            contentColor = DarkBackground
                        )
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send")
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    val bgColor = if (message.isUser) Gold.copy(alpha = 0.15f) else DarkCard
    val textColor = if (message.isUser) Gold else WhiteSoft

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            color = bgColor,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 16.dp
            )
        ) {
            Text(
                text = message.text,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
