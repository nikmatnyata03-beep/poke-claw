sed -i 's/private val KEEP_RECENT_ROUNDS = 3//g' app/src/main/java/io/agents/pokeclaw/agent/DefaultAgentService.kt
sed -i 's/KEEP_RECENT_ROUNDS/config.keepRecentRounds/g' app/src/main/java/io/agents/pokeclaw/agent/DefaultAgentService.kt
