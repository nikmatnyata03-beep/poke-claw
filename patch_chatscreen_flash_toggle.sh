cat app/src/main/java/io/agents/pokeclaw/ui/chat/ChatScreen.kt | awk 'NR<965' > tmp.kt
cat << 'INNER_EOF' >> tmp.kt
                }
            }
        } else {
            // Segmented toggle for Cloud: Multi-Complex vs Flash Task
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                // Multi-Complex button
                Surface(
                    onClick = { onFlashTaskModeChange(false) },
                    shape = RoundedCornerShape(10.dp),
                    color = if (!isFlashTaskMode) colors.aiBubble else Color.Transparent,
                    border = if (!isFlashTaskMode) androidx.compose.foundation.BorderStroke(1.dp, colors.aiBubbleBorder) else null,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        "🧠 Multi-Complex",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (!isFlashTaskMode) colors.textPrimary else colors.textTertiary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 7.dp),
                    )
                }
                // Flash Task button
                Surface(
                    onClick = { onFlashTaskModeChange(true) },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isFlashTaskMode) colors.accent.copy(alpha=0.15f) else Color.Transparent,
                    border = if (isFlashTaskMode) androidx.compose.foundation.BorderStroke(1.dp, colors.accent) else null,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        "⚡ Flash Task",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isFlashTaskMode) colors.accent else colors.textTertiary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 7.dp),
                    )
                }
            }
        }
INNER_EOF
cat app/src/main/java/io/agents/pokeclaw/ui/chat/ChatScreen.kt | awk 'NR>965' >> tmp.kt
mv tmp.kt app/src/main/java/io/agents/pokeclaw/ui/chat/ChatScreen.kt
