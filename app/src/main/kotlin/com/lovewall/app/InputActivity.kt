package com.lovewall.app

import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lovewall.app.models.Note
import com.lovewall.app.viewmodels.MessagingViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class InputActivity : ComponentActivity() {
    private val viewModel: MessagingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var text by remember { mutableStateOf("") }
            LoveWallTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Send a Note",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextField(
                                value = text,
                                onValueChange = { text = it },
                                placeholder = { Text("Enter your message...") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row {
                                TextButton(onClick = { finish() }) {
                                    Text("Cancel")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (text.isNotBlank()) {
                                            viewModel.sendNote(
                                                Note(
                                                    id = UUID.randomUUID().toString(),
                                                    text = text,
                                                    color = 0xFFFFD700.toInt(), // Gold
                                                    expiry = System.currentTimeMillis() + 86400000, // 24h
                                                    positionX = 0.5f,
                                                    positionY = 0.5f
                                                )
                                            )
                                            finish()
                                        }
                                    }
                                ) {
                                    Text("Send")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
