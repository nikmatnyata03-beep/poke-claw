sed -i 's/maxIterations: Int,/maxIterations: Int,\n        keepRecentRounds: Int = 3,/g' app/src/main/java/io/agents/pokeclaw/agent/llm/ModelConfigRepository.kt
sed -i 's/maxIterations = maxIterations,/maxIterations = maxIterations,\n                keepRecentRounds = keepRecentRounds,/g' app/src/main/java/io/agents/pokeclaw/agent/llm/ModelConfigRepository.kt
