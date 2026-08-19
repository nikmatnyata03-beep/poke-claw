sed -i 's/kotlinx.coroutines.flow.asStateFlow(_isFlashTaskMode)/_isFlashTaskMode.asStateFlow()/g' app/src/main/java/io/agents/pokeclaw/AppViewModel.kt
sed -i 's/import kotlinx.coroutines.flow.MutableStateFlow/import kotlinx.coroutines.flow.MutableStateFlow\nimport kotlinx.coroutines.flow.asStateFlow/g' app/src/main/java/io/agents/pokeclaw/AppViewModel.kt
