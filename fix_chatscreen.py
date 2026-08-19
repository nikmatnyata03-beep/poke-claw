import re

with open('app/src/main/java/io/agents/pokeclaw/ui/chat/ChatScreen.kt', 'r') as f:
    content = f.read()

bad_block = """                )
                Text(
                    text = "$tokens tokens",
                    color = colors.textSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(end = 8.dp),
                )"""

# Remove the bad block
content = content.replace(bad_block, "")

with open('app/src/main/java/io/agents/pokeclaw/ui/chat/ChatScreen.kt', 'w') as f:
    f.write(content)
