cat app/src/main/java/io/agents/pokeclaw/ui/chat/ChatScreen.kt | awk 'NR<2458' > tmp.kt
cat << 'INNER_EOF' >> tmp.kt
                Text(
                    text = if (tasks.size == 1) "Monitoring: ${tasks[0]}" else "${tasks.size} monitoring",
                    color = colors.textPrimary,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                if (tokens > 0) {
                    Text(
                        text = "$tokens tokens",
                        color = colors.textSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                }
                Text(
                    text = if (expanded) "▴" else "▾",
                    color = colors.textSecondary,
                    fontSize = 14.sp
                )
            }
INNER_EOF
cat app/src/main/java/io/agents/pokeclaw/ui/chat/ChatScreen.kt | awk 'NR>2480' >> tmp.kt
mv tmp.kt app/src/main/java/io/agents/pokeclaw/ui/chat/ChatScreen.kt
