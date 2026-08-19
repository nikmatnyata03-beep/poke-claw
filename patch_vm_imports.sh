sed -i 's/import android.os.PowerManager/import android.os.PowerManager\nimport kotlinx.coroutines.flow.asStateFlow/g' app/src/main/java/io/agents/pokeclaw/AppViewModel.kt
