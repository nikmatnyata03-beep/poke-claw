sed -i 's/appViewModelInstance = getAppViewModelProvider()\[AppViewModel::class.java\]/KVUtils.init(this)\n        appViewModelInstance = getAppViewModelProvider()[AppViewModel::class.java]/g' app/src/main/java/io/agents/pokeclaw/ClawApplication.kt
sed -i 's/KVUtils.init(this)//' app/src/main/java/io/agents/pokeclaw/ClawApplication.kt
