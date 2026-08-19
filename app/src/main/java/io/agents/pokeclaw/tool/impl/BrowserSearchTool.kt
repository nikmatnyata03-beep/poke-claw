package io.agents.pokeclaw.tool.impl

import android.content.Intent
import android.net.Uri
import io.agents.pokeclaw.ClawApplication
import io.agents.pokeclaw.tool.BaseTool
import io.agents.pokeclaw.tool.ToolParameter
import io.agents.pokeclaw.tool.ToolResult

class BrowserSearchTool : BaseTool() {
    override fun getName() = "browser_search"
    override fun getDisplayName() = "Browser Search"
    override fun getDescriptionEN() = "Search the web directly using the default browser. Faster than opening the browser and typing."
    override fun getDescriptionCN() = "Search the web directly using the default browser. Faster than opening the browser and typing."

    override fun getParameters(): List<ToolParameter> {
        return listOf(
            ToolParameter("query", "string", "The search query", true)
        )
    }

    override fun execute(params: Map<String, Any>): ToolResult {
        val query = params["query"] as? String ?: return ToolResult.error("query is required")
        
        return try {
            val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                putExtra(android.app.SearchManager.QUERY, query)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val context = ClawApplication.instance
            
            // Try ACTION_WEB_SEARCH first
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                ToolResult.success("Opened browser search for: $query")
            } else {
                // Fallback to google.com
                val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + Uri.encode(query))).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallbackIntent)
                ToolResult.success("Opened browser URL search for: $query")
            }
        } catch (e: Exception) {
            ToolResult.error("Failed to perform browser search: ${e.message}")
        }
    }
}
