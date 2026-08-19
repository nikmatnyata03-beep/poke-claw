sed -i '59a\
                if (!KVUtils.hasLlmConfig()) {\
                    io.agents.pokeclaw.agent.llm.ModelConfigRepository.saveCloudDefault(\
                        providerName = "DEEPSEEK",\
                        modelId = "deepseek-v4-flash",\
                        baseUrl = "https://api.deepseek.com/v1",\
                        apiKey = "7dd4ecd4-9576-443c-8a98-63d6f0daea4f",\
                        activateNow = true\
                    )\
                }\
' app/src/main/java/io/agents/pokeclaw/ClawApplication.kt
