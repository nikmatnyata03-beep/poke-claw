package io.agents.pokeclaw.tool.impl

import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import io.agents.pokeclaw.ClawApplication
import io.agents.pokeclaw.tool.BaseTool
import io.agents.pokeclaw.tool.ToolParameter
import io.agents.pokeclaw.tool.ToolResult
import java.io.File

class InstallApkTool : BaseTool() {
    override fun getName() = "install_apk"
    override fun getDisplayName() = "Install APK"
    override fun getDescriptionEN() = "Launch APK installation. Provide path relative to external storage (e.g., 'Download/app.apk')."
    override fun getDescriptionCN() = "Launch APK installation. Provide path relative to external storage (e.g., 'Download/app.apk')."

    override fun getParameters(): List<ToolParameter> {
        return listOf(
            ToolParameter("path", "string", "Path to APK file", true)
        )
    }

    override fun execute(params: Map<String, Any>): ToolResult {
        val path = params["path"] as? String ?: return ToolResult.error("path is required")
        
        val baseDir = Environment.getExternalStorageDirectory()
        val file = File(baseDir, path)
        
        if (!file.exists()) return ToolResult.error("APK file not found: $path")
        if (!file.name.endsWith(".apk", ignoreCase = true)) return ToolResult.error("File is not an APK")
        
        return try {
            val context = ClawApplication.instance
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            ToolResult.success("Installation launched for $path")
        } catch (e: Exception) {
            ToolResult.error("Failed to launch APK installation: ${e.message}. Note: you may need to add fileprovider to manifest if it crashes.")
        }
    }
}
