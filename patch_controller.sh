sed -i '86,95c\
    private fun refreshActiveTasks() {\
        _activeTasks.value = if (autoReplyManager.isEnabled) {\
            autoReplyManager.monitoredContacts.toList()\
        } else {\
            emptyList()\
        }\
        _autoReplyTokens.value = io.agents.pokeclaw.utils.KVUtils.getAutoReplyTokens()\
    }\
}' app/src/main/java/io/agents/pokeclaw/ui/chat/ActiveTaskShellController.kt
