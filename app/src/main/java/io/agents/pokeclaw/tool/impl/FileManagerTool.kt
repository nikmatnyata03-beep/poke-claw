package io.agents.pokeclaw.tool.impl

import android.os.Environment
import io.agents.pokeclaw.tool.BaseTool
import io.agents.pokeclaw.tool.ToolParameter
import io.agents.pokeclaw.tool.ToolResult
import java.io.File

class FileManagerTool : BaseTool() {
    override fun getName() = "file_manager"
    override fun getDisplayName() = "File Manager"
    override fun getDescriptionEN() = "List files in a directory or read a file. Pass 'path' as argument."
    override fun getDescriptionCN() = "List files in a directory or read a file. Pass 'path' as argument."

    override fun getParameters(): List<ToolParameter> {
        return listOf(
            ToolParameter("action", "string", "Action to perform: 'list' or 'read'", true),
            ToolParameter("path", "string", "Path relative to external storage (e.g., 'Download')", true)
        )
    }

    override fun execute(params: Map<String, Any>): ToolResult {
        val action = params["action"] as? String ?: return ToolResult.error("action is required")
        val path = params["path"] as? String ?: return ToolResult.error("path is required")
        
        val baseDir = Environment.getExternalStorageDirectory()
        val target = File(baseDir, path)
        
        if (!target.exists()) return ToolResult.error("File or directory not found")
        
        return try {
            when (action.lowercase()) {
                "list" -> {
                    if (target.isDirectory) {
                        val files = target.listFiles()?.joinToString("\n") { 
                            (if (it.isDirectory) "[DIR] " else "[FILE] ") + it.name 
                        } ?: "Empty directory"
                        ToolResult.success(files)
                    } else {
                        ToolResult.error("Path is not a directory")
                    }
                }
                "read" -> {
                    if (target.isFile) {
                        if (target.length() > 50000) return ToolResult.error("File is too large to read (max 50KB)")
                        ToolResult.success(target.readText().take(5000))
                    } else {
                        ToolResult.error("Path is not a file")
                    }
                }
                else -> ToolResult.error("Unknown action: $action")
            }
        } catch (e: Exception) {
            ToolResult.error("Failed to access file system: ${e.message}")
        }
    }
}
