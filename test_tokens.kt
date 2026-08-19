import dev.langchain4j.model.output.ChatResponse
fun foo(res: ChatResponse) = res.tokenUsage()?.totalTokenCount()
