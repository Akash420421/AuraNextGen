# Aura AI / AntiGem Fork Engineering Master Guide
## Resolving Package Collisions, Shell Permission Denied, & Localhost Port Clashes

---

## 📋 Table of Contents
1. [Overview & Architecture](#1-overview--architecture)
2. [Problem Manifestation & Symptoms](#2-problem-manifestation--symptoms)
3. [Deep Root-Cause Analysis](#3-deep-root-cause-analysis)
   - [A. Android Multi-User UID Sandbox & Shebang Resolution](#a-android-multi-user-uid-sandbox--shebang-resolution)
   - [B. Shared Loopback (127.0.0.1) & Brute-Force Rate Limiting](#b-shared-loopback-127001--brute-force-rate-limiting)
   - [C. Android Package Identity & Keystore Signatures](#c-android-package-identity--keystore-signatures)
4. [Step-by-Step Code Implementations](#4-step-by-step-code-implementations)
   - [Step 1: Dedicated Standalone Flavor (`build.gradle.kts`)](#step-1-dedicated-standalone-flavor-buildgradlekts)
   - [Step 2: Shell Resolution & Banish `login` (`LocalTerminalManager.kt`)](#step-2-shell-resolution--banish-login-localterminalmanagerkt)
   - [Step 3: Force Direct ELF Binary for Server Runner (`LocalServerManager.kt`)](#step-3-force-direct-elf-binary-for-server-runner-localservermanagerkt)
   - [Step 4: Shebang Patching & POSIX File Permissions (`LocalEnvironmentManager.kt`)](#step-4-shebang-patching--posix-file-permissions-localenvironmentmanagerkt)
   - [Step 5: Port Migration & Authenticated Health Checks (`AuthPreferences.kt`)](#step-5-port-migration--authenticated-health-checks-authpreferenceskt)
   - [Step 6: Dynamic Shared Library Preload for MCP (`libtermux-exec.so`)](#step-6-dynamic-shared-library-preload-for-mcp-libtermux-execso)
5. [Understanding Gemini API Quota Limit (Code 429)](#5-understanding-gemini-api-quota-limit-code-429)
6. [Universal AI Agent Prompt (Copy-Paste Ready)](#6-universal-ai-agent-prompt-copy-paste-ready)
7. [Debugging & Verification Protocol](#7-debugging--verification-protocol)

---

## 1. Overview & Architecture

Aura AI (and forks of AntiGem / Termux) operates as a hybrid Android application:
- **Frontend / UI:** Android Jetpack Compose native application.
- **Embedded Linux Subsystem:** An extracted Termux rootfs (`bootstrap-aarch64.zip`) residing in the app's internal storage (`/data/user/0/<applicationId>/files/usr`).
- **Engine / Backend:**
  - `agy_ide_bridge`: A compiled Go daemon acting as an HTTP/WebSocket translation bridge between the app UI and developer tools.
  - `agy --hub`: An Antigravity CLI Python/Node daemon managing agents, tool calls, and model communication.
- **Session Execution:** Terminal and background server sessions are managed by `com.termux.terminal.TerminalSession` executing over local pseudo-terminals (PTYs).

---

## 2. Problem Manifestation & Symptoms

When forking AntiGem into a custom-named application without these adjustments, three critical failures occur:

| Failure Symptom | Error Message / Screen | Root Cause |
|---|---|---|
| **Installation Error** | `INSTALL_FAILED_UPDATE_INCOMPATIBLE` ("App not installed") | Package name remains `com.termux` while keystore differs from the installed Termux app. |
| **Server Crash on Launch** | `exec(".../usr/bin/login"): Permission denied [Process completed (code 1)]` | App executes `/usr/bin/login` which has a hardcoded Termux shebang inaccessible to other UIDs. |
| **Token Rejection & Lockout** | `Bridge: reachable, but the token was rejected` & `HTTP 429: blocked for a while after too many wrong tokens` | App connects to existing AntiGem server running on shared localhost port 1234/1235. |

---

## 3. Deep Root-Cause Analysis

### A. Android Multi-User UID Sandbox & Shebang Resolution
1. **Linux UID Isolation:** Android assigns every installed app a unique Linux UID (e.g., `u0_a334` for Termux, `u0_a335` for Aura AI). The app directory `/data/data/<package>/` is mode `0700` (`rwx------`) owned strictly by that UID.
2. **The Termux Shebang Issue:** Official Termux packages are pre-compiled with hardcoded paths. The file `/usr/bin/login` is a shell script starting with:
   ```bash
   #!/data/data/com.termux/files/usr/bin/sh
   ```
3. **The Kernel Block:** When `com.aura.ai` executes `execve("/data/user/0/com.aura.ai/files/usr/bin/login")`, the Linux kernel parses the shebang and attempts to invoke `/data/data/com.termux/files/usr/bin/sh` using `com.aura.ai`'s UID credentials. Android's kernel blocks access with `EACCES` (**Permission denied**).
4. **Command Execution Limitation:** `/usr/bin/login` is an interactive MOTD script; it does not parse `-c "<command>"` flags.

### B. Shared Loopback (127.0.0.1) & Brute-Force Rate Limiting
1. **Shared Network:** The Android loopback interface (`127.0.0.1`) is shared across all processes and apps on the device.
2. **Port Clashing:** If original AntiGem or Termux is running in the background, it listens on:
   - Bridge: `127.0.0.1:1234`
   - Hub: `127.0.0.1:1235`
3. **Token Mismatch:** If the new app also defaults to `1234` and `1235`, it connects to AntiGem's running bridge. Each app generates its own random 12-character security token at startup. When the new app sends its framed auth header, AntiGem's bridge returns `HTTP 401 Unauthorized` (`token was rejected`).
4. **Brute-Force Lockout:** AntiGem's bridge has built-in brute-force protection. After multiple invalid token attempts, it permanently rejects calls with `HTTP 429 Too Many Requests` (`blocked for a while after too many wrong tokens`).

### C. Android Package Identity & Keystore Signatures
- Android allows updating an existing app ONLY if both APKs share the exact same `applicationId` AND the exact same cryptographic signing key.
- Forked builds compiled with `applicationId = "com.termux"` collide with existing Termux or AntiGem installs, preventing installation unless the original app is deleted.

---

## 4. Step-by-Step Code Implementations

### Step 1: Dedicated Standalone Flavor (`build.gradle.kts`)
File: `app/build.gradle.kts`

Define a dedicated flavor with a unique `applicationId` and distinct port numbers:

```kotlin
flavorDimensions += "mode"
productFlavors {
    // Dedicated standalone flavor with distinct application ID and ports
    create("aura") {
        dimension = "mode"
        applicationId = "com.aura.ai"
        buildConfigField("int", "BRIDGE_PORT", "1244")     // AntiGem is 1234, standard is 1236
        buildConfigField("int", "HUB_PORT", "1245")        // AntiGem is 1235, standard is 1237
        buildConfigField("int", "BROWSER_MCP_PORT", "8769")// AntiGem is 8765
        buildConfigField("boolean", "IS_SHARED_USER_ID", "false")
    }
    create("termux") {
        dimension = "mode"
        applicationId = "com.termux"
        buildConfigField("int", "BRIDGE_PORT", "1234")
        buildConfigField("int", "HUB_PORT", "1235")
        buildConfigField("int", "BROWSER_MCP_PORT", "8765")
        buildConfigField("boolean", "IS_SHARED_USER_ID", "false")
    }
}
```

In `scripts/build_all_apks.sh`, ensure release mode compiles `:app:assembleAuraRelease`:
```bash
"${ROOT_DIR}/gradlew" -p "${ROOT_DIR}" :app:assembleAuraRelease
find "${APKS_SRC}/aura/release" -name "*.apk" -exec cp {} "${OUTPUT_DIR}/AuraAI-v${VERSION_NAME}-release.apk" \;
```

---

### Step 2: Shell Resolution & Banish `login` (`LocalTerminalManager.kt`)
File: `app/src/main/java/com/example/gemini/data/local/LocalTerminalManager.kt`

Never choose `login` if executing a background command (`initialCommand` is present) or if the package is not `com.termux`:

```kotlin
// 1. Filter login shell binaries based on command context
val loginShellBinaries = if (!initialCommand.isNullOrBlank()) {
    arrayOf("bash", "sh", "zsh", "fish") // Exclude login wrapper for commands
} else if (context.packageName == "com.termux") {
    arrayOf("login", "bash", "zsh", "fish", "sh")
} else {
    arrayOf("bash", "sh", "zsh", "fish")
}

// 2. Select shell binary
val shellBinary = when {
    forceShell != null && File(bin, forceShell).exists() && File(bin, forceShell).canExecute() -> File(bin, forceShell).absolutePath
    forceShell != null && File(bin, forceShell).exists() -> File(bin, forceShell).absolutePath
    else -> {
        var found: String? = null
        for (name in loginShellBinaries) {
            val file = File(bin, name)
            if (file.exists() && file.canExecute()) {
                found = file.absolutePath
                break
            }
        }
        found ?: "/system/bin/sh"
    }
}

// 3. Format argv[0] and arguments correctly
val isLoginShell = shellBinary != "/system/bin/sh" && initialCommand.isNullOrBlank()
val processName = (if (isLoginShell) "-" else "") + File(shellBinary).name
val shellArgs = if (!initialCommand.isNullOrBlank()) {
    if (execShellAfterCommand) {
        arrayOf(File(shellBinary).name, "-c", "$initialCommand; exec $shellBinary")
    } else {
        arrayOf(File(shellBinary).name, "-c", initialCommand)
    }
} else {
    arrayOf(processName)
}
```

---

### Step 3: Force Direct ELF Binary for Server Runner (`LocalServerManager.kt`)
File: `app/src/main/java/com/example/gemini/data/local/LocalServerManager.kt`

When spawning the dedicated PTY session for the server runner, explicitly pass `forceShell = "bash"`:

```kotlin
val session = withContext(Dispatchers.Main) {
    LocalPtySession(
        id = "server-runner-pty",
        initialTitle = "Server Runner",
        context = appContext,
        isSsh = false,
        initialCommand = command,
        forceShell = "bash", // Guarantees direct execution of compiled bash binary
        initialCols = 80,
        initialRows = 24
    )
}
```

---

### Step 4: Shebang Patching & POSIX File Permissions (`LocalEnvironmentManager.kt`)
File: `app/src/main/java/com/example/gemini/data/local/LocalEnvironmentManager.kt`

Inside `configureEnvironmentFiles()`:
1. Rewrite hardcoded Termux shebangs in `bin/login` to point to the active app's `bin/sh`.
2. Ensure executable permissions (`0755`) are set on all binaries:

```kotlin
// 1. Rewrite login shebang to active package prefix
val loginFile = File(binDir, "login")
if (loginFile.exists()) {
    try {
        val content = loginFile.readText()
        if (content.contains("/data/data/com.termux/files/usr/bin/sh")) {
            loginFile.writeText(content.replace("/data/data/com.termux/files/usr/bin/sh", "${binDir.absolutePath}/sh"))
        }
        loginFile.setExecutable(true, false)
        try { android.system.Os.chmod(loginFile.absolutePath, 493) } catch (_: Exception) {} // 0755
    } catch (_: Exception) {}
}

// 2. Ensure all binaries in binDir have execute permissions
try {
    binDir.listFiles()?.forEach { f ->
        if (f.isFile && !f.name.endsWith(".txt") && !f.name.endsWith(".conf")) {
            f.setExecutable(true, false)
            f.setReadable(true, false)
            try { android.system.Os.chmod(f.absolutePath, 493) } catch (_: Exception) {} // 0755
        }
    }
} catch (_: Exception) {}
```

Also verify `isTermuxPackage(context: Context)` recognizes custom packages:
```kotlin
fun isTermuxPackage(context: Context): Boolean {
    val pkg = context.packageName
    return pkg == "com.termux" || pkg == "com.aura.ai" || pkg.startsWith("com.aura.")
}
```

---

### Step 5: Port Migration & Authenticated Health Checks (`AuthPreferences.kt`)
File: `app/src/main/java/com/example/gemini/data/preferences/AuthPreferences.kt`

1. Upgrade saved URLs automatically when ports change:
```kotlin
fun migrateHubUrl(url: String): String = 
    if (url.trimEnd('/') == LEGACY_HUB_URL || (BuildConfig.HUB_PORT != 1235 && url.trimEnd('/') == "http://127.0.0.1:1235")) DEFAULT_HUB_URL else url

fun migrateBridgeUrl(url: String): String = 
    if (url.trimEnd('/') == LEGACY_BRIDGE_HTTP_URL || (BuildConfig.BRIDGE_PORT != 1234 && url.trimEnd('/') == "http://127.0.0.1:1234")) DEFAULT_BRIDGE_HTTP_URL else url
```

2. Persist migrated URLs in `init`:
```kotlin
init {
    val savedHub = syncPrefs.getString("agy_hub_url", null)
    val savedBridgeHttp = syncPrefs.getString("agy_bridge_http_url", null)
    if (!savedHub.isNullOrBlank()) {
        val migrated = migrateHubUrl(savedHub)
        currentHubUrl = migrated
        if (migrated != savedHub) syncPrefs.edit().putString("agy_hub_url", migrated).apply()
    }
    if (!savedBridgeHttp.isNullOrBlank()) {
        val migrated = migrateBridgeUrl(savedBridgeHttp)
        currentBridgeHttpUrl = migrated
        if (migrated != savedBridgeHttp) syncPrefs.edit().putString("agy_bridge_http_url", migrated).apply()
    }
}
```

3. In `LocalServerManager.kt`: verify authentication before adopting any running bridge:
```kotlin
val bridgeAuthHeader = AuthPreferences.currentFramedHeader
val isHealthyOurInstance = !forceRestart && withContext(Dispatchers.IO) {
    try {
        val req = okhttp3.Request.Builder()
            .url("http://127.0.0.1:$bridgePort/api/server/info")
            .header(bridgeAuthHeader, "true")
            .get()
            .build()
        AgyBridgeService.instance.fastClient.newCall(req).execute().use { it.isSuccessful }
    } catch (_: Exception) {
        false
    }
}
if (isHealthyOurInstance) {
    _status.value = LocalServerStatus.Running()
    AgyBridgeService.instance.recomputeSystemConnectionState()
    return
}
```

---

### Step 6: Dynamic Shared Library Preload for MCP (`libtermux-exec.so`)
Files: `AgyMcpManager.kt` & `AgyPluginService.kt`

Replace hardcoded `/data/data/com.termux/...` paths with dynamic candidate checks:

```kotlin
val libCandidates = listOf(
    System.getenv("PREFIX")?.let { "$it/lib/libtermux-exec.so" },
    "/data/data/${context.packageName}/files/usr/lib/libtermux-exec.so",
    "/data/data/com.termux/files/usr/lib/libtermux-exec.so"
)
val termuxExec = libCandidates.firstOrNull { it != null && java.io.File(it).exists() }
if (!envMap.containsKey("LD_PRELOAD") && termuxExec != null) {
    envMap["LD_PRELOAD"] = termuxExec
}
if (!envMap.containsKey("PATH")) {
    val homePrefix = System.getenv("HOME") ?: "/data/data/${context.packageName}/files/home"
    val usrPrefix = System.getenv("PREFIX") ?: "/data/data/${context.packageName}/files/usr"
    envMap["PATH"] = "$homePrefix/.local/bin:$usrPrefix/bin:/system/bin"
}
```

---

## 5. Understanding Gemini API Quota Limit (Code 429)

### The Error:
```text
Quota / Usage Limit Exceeded Code: 429
RESOURCE_EXHAUSTED: Individual quota reached. Resets in 144h.
```

### Why it Happens:
- This is **NOT a codebase or server bug**.
- It occurs when the user exhausts Google's weekly free-tier quota for a specific model (e.g., `Gemini 3.8 Flash (High)`) on their signed-in Google account.

### How to Resolve in the App:
1. **Switch Model:** Tap the model selector at the bottom of the chat screen and choose standard `Gemini Flash`, `Claude`, or another available agent model.
2. **Add Personal API Key:** Navigate to **Settings → Servers & API** and supply a Google AI Studio API key (API keys operate on dedicated project quotas).
3. **Switch Google Account:** Sign out and sign in with an alternative Google account.

---

## 6. Universal AI Agent Prompt (Copy-Paste Ready)

When delegating future fork setup or server debugging tasks to an autonomous AI agent, provide the following complete prompt:

```text
TASK: Fix Android Termux/AntiGem Fork Server Auto-Start, Login Permission Denied, and Port Collisions

Please diagnose and fix the local background server startup, shell permissions, and port collisions in this Android project (Termux / AntiGem fork). Follow these exact technical root-cause analyses and implementation steps:

1. Fix "exec(.../usr/bin/login): Permission denied":
   - Root Cause: In Termux rootfs, /usr/bin/login is a shell script with hardcoded shebang #!/data/data/com.termux/files/usr/bin/sh. In any app with a custom package name, Android's UID sandbox blocks reading /data/data/com.termux/, returning EACCES (Permission denied, code 1).
   - In LocalTerminalManager.kt: For non-blank initialCommand, NEVER pick login. Always select compiled ELF binaries: arrayOf("bash", "sh", "zsh", "fish"). Only allow login if initialCommand is null AND packageName == "com.termux".
   - In LocalServerManager.kt: Explicitly pass forceShell = "bash" when spawning LocalPtySession for the server runner.
   - In LocalEnvironmentManager.kt (configureEnvironmentFiles): If bin/login exists, replace hardcoded com.termux paths with ${binDir}/sh, and enforce Os.chmod(path, 0755) and setExecutable(true, false) on all unpacked binaries in binDir.

2. Eliminate Localhost Port Collisions & Token Rejection:
   - Root Cause: If the device runs Termux or AntiGem on default ports 1234 (Bridge) and 1235 (Hub), the forked app collides, connecting to the foreign bridge which rejects the auth token (HTTP 401), followed by brute-force lockout (HTTP 429).
   - In build.gradle.kts: Assign distinct ports in productFlavors (e.g., BRIDGE_PORT = 1244, HUB_PORT = 1245, BROWSER_MCP_PORT = 8769).
   - In AuthPreferences.kt: Automatically migrate any saved 1234/1235 endpoints in SharedPreferences to the new defaults and persist them.
   - In LocalServerManager.kt: Verify server adoption with the app's security token on /api/server/info before adopting any running bridge.

3. Dynamic Library Preload & Package Recognition:
   - In AgyMcpManager.kt and AgyPluginService.kt: Dynamically resolve libtermux-exec.so using candidate paths rather than hardcoding /data/data/com.termux/...
   - In LocalEnvironmentManager.kt: Ensure isTermuxPackage() recognizes the custom package ID and LD_LIBRARY_PATH is supplied.

4. Build and QA:
   - Compile release APK with ./gradlew assembleRelease.
   - Verify server launches automatically on the custom port without permission denied errors.
```

---

## 7. Debugging & Verification Protocol

1. **Verify Binary Metadata:**
   ```bash
   python3 -c "
   import zipfile, re
   with zipfile.ZipFile('AuraAI-release.apk') as z:
       arsc = z.read('resources.arsc')
       print('App Package in ARSC:', set(re.findall(rb'com\.[a-z0-9_.]+', arsc)))
   "
   ```
2. **Inspect Live Device Logs via Logcat:**
   ```bash
   adb logcat -s AntiGem AntiGemTerminal ServerManager
   ```
3. **Verify Open Ports on Device:**
   ```bash
   adb shell "netstat -tuln | grep -E '1234|1235|1244|1245'"
   ```
4. **Inspect Terminal Runner Output:**
   - On the Chat Screen, tap the status dot next to **AURA AI** or click **[Logs]** to open `LocalServerOutputDialog`.
   - Verify that bash initialized cleanly and `agy_ide_bridge` printed:
     `Listening on :1244`
