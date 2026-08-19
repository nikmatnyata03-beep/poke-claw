// Copyright 2026 PokeClaw (agents.io). All rights reserved.
// Licensed under the Apache License, Version 2.0.

package io.agents.pokeclaw

import io.agents.pokeclaw.agent.DefaultAgentService
import io.agents.pokeclaw.agent.llm.LocalBackendHealth
import io.agents.pokeclaw.base.BaseApp
import io.agents.pokeclaw.channel.ChannelManager
import io.agents.pokeclaw.tool.ToolRegistry
import io.agents.pokeclaw.utils.AppLogStore
import io.agents.pokeclaw.utils.KVUtils
import io.agents.pokeclaw.utils.XLog
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.content.Context
import android.os.Handler
import android.os.Looper

/**
 * Application entry point
 */

val appViewModel: AppViewModel by lazy { ClawApplication.appViewModelInstance }
class ClawApplication : BaseApp() {

    companion object {
        private const val TAG = "ClawApplication"
        lateinit var instance: ClawApplication
            private set
        lateinit var appViewModelInstance: AppViewModel
    }

    override fun onCreate() {
        super.onCreate()
        AppCapabilityCoordinator.markProcessStart()
        instance = this
        AppLogStore.init(this)
        XLog.setDEBUG(BuildConfig.DEBUG)
        registerNetworkCallback()
        
        KVUtils.init(this)
        appViewModelInstance = getAppViewModelProvider()[AppViewModel::class.java]
        
        LocalBackendHealth.recoverPendingGpuCrashIfNeeded()
        ToolRegistry.getInstance().registerAllTools(ToolRegistry.DeviceType.MOBILE)
        io.agents.pokeclaw.agent.skill.SkillRegistry.loadBuiltInSkills()
        io.agents.pokeclaw.agent.PlaybookManager.loadAll(this)
        XLog.i(TAG, "ClawApplication initialized, tools registered: ${ToolRegistry.getInstance().getAllTools().size}")

        // Write network logs to file (set to true when debugging)
        DefaultAgentService.FILE_LOGGING_ENABLED = BuildConfig.DEBUG
        DefaultAgentService.FILE_LOGGING_CACHE_DIR = cacheDir

        // Lightweight initialization (main thread)
        appViewModelInstance.initCommon()
        Thread({
            try {
                android.util.Log.i("POKECLAW_INIT", "app-async-init thread STARTED")
                if (!KVUtils.hasLlmConfig()) {
                    io.agents.pokeclaw.agent.llm.ModelConfigRepository.saveCloudDefault(
                        providerName = "DEEPSEEK",
                        modelId = "deepseek-v4-flash",
                        baseUrl = "https://api.deepseek.com/v1",
                        apiKey = "sk-1a81a6c43d05436f8a053bcb700423e1",
                        activateNow = true
                    )
                }

                val hasConfig = KVUtils.hasLlmConfig()
                android.util.Log.i("POKECLAW_INIT", "app-async-init: hasLlmConfig=$hasConfig, canDrawOverlays=${android.provider.Settings.canDrawOverlays(instance)}")
                if (hasConfig) {
                    appViewModelInstance.initAgent()
                    appViewModelInstance.afterInit()
                }
            } catch (e: Exception) {
                android.util.Log.e("POKECLAW_INIT", "app-async-init CRASHED: ${e.message}", e)
            }
        }, "app-async-init").start()
    }

    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    /**
     * Listen for network recovery and automatically re-initialize channels.
     * Fixes channel initialization failures when booting with no network, and reconnects channels after network outages.
     */
    private fun registerNetworkCallback() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (KVUtils.hasLlmConfig()) {
                        XLog.i(TAG, "Network recovered, checking and reconnecting dropped channels")
                        ChannelManager.reconnectIfNeeded()
                    }
                }, 2000)
            }

            override fun onLost(network: Network) {
                XLog.w(TAG, "Network disconnected")
            }
        }
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback!!)
    }

}
