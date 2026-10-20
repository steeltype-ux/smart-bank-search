#!/usr/bin/env python3
"""Conservative security regression checks for the Plugin Hub release.

This is source scanning, not a proof of security. It runs only during CI and
does not ship in the RuneLite plugin. Production Java is deliberately local,
without network/file access or client automation.
"""
from pathlib import Path
import re
import subprocess
import sys

ROOT = Path(__file__).resolve().parent.parent
MAIN = ROOT / "src/main/java"
RESOURCES = ROOT / "src/main/resources"

# Sensitive capabilities aren't required for a read-only bank-search filter.
FORBIDDEN = {
    "network, filesystem, or reflection": r"\bjava\.(?:net|io|nio\.file|lang\.reflect|lang\.invoke|awt\.Robot)\b",
    "process execution or dynamic loading": r"\b(?:ProcessBuilder|Runtime\.getRuntime|Class\.forName|ClassLoader|System\.load(?:Library)?|MethodHandles)\b",
    "network clients or sockets": r"\b(?:HttpClient|HttpURLConnection|OkHttpClient|WebSocket|Socket|DatagramSocket|URLConnection)\b",
    "filesystem and credential access": r"\b(?:FileInputStream|FileOutputStream|RandomAccessFile|FileReader|FileWriter|Files\.\w+|getPassword|credentials\.properties|getCredentials|accessToken|sessionToken)\b",
    "game actions or packet sending": r"\b(?:client\.(?:runScript|menuAction|hopToWorld)|ScriptEventBuilder\.setCanSendPackets|PacketWriter|sendPacket|invokeMenuAction)\b",
    "unsafe memory and native code": r"\b(?:sun\.misc\.Unsafe|jdk\.internal|javax\.native|JNI|JNA)\b",
}
BLOCKED_TRACKED_PATHS = (
    re.compile(r"(^|/)\.runelite(/|$)", re.I),
    re.compile(r"(^|/)credentials\.properties$", re.I),
    re.compile(r"(^|/)\.env(?:\..+)?$", re.I),
    re.compile(r"\.(?:p12|pfx|jks|keystore|pem|key)$", re.I),
)
SECRET_MARKERS = [
    re.compile(r"-----BEGIN (?:RSA |EC |OPENSSH )?PRIVATE KEY-----"),
    re.compile(r"\bgithub_pat_[A-Za-z0-9_]{20,}\b"),
    re.compile(r"\bgh[pousr]_[A-Za-z0-9]{30,}\b"),
]
errors = []

sources = sorted(MAIN.rglob("*.java"))
if not sources:
    errors.append("No production Java source discovered")
for source in sources:
    content = source.read_text(encoding="utf-8")
    for kind, pattern in FORBIDDEN.items():
        for match in re.finditer(pattern, content, flags=re.IGNORECASE):
            line = content.count("\n", 0, match.start()) + 1
            errors.append(f"{source.relative_to(ROOT)}:{line}: forbidden {kind}: {match.group(0)}")

# Only the bank-search callback may consume a client event.
subscriptions = []
for source in sources:
    subscriptions.extend((source.relative_to(ROOT).as_posix(), m.start())
                         for m in re.finditer(r"@Subscribe\b", source.read_text(encoding="utf-8")))
if len(subscriptions) != 1 or subscriptions[0][0] != "src/main/java/com/smartbanksearch/SmartBankSearchPlugin.java":
    errors.append(f"Unexpected plugin event subscriptions: {subscriptions}")
main_plugin = ROOT / "src/main/java/com/smartbanksearch/SmartBankSearchPlugin.java"
if main_plugin.exists() and '"bankSearchFilter".equals(event.getEventName())' not in main_plugin.read_text(encoding="utf-8"):
    errors.append("Bank-search-only callback guard missing")

# No unreviewed binary payloads should be packaged with the plugin.
for file in RESOURCES.rglob("*"):
    if file.is_file() and file.suffix.lower() != ".png":
        errors.append(f"Unexpected production resource: {file.relative_to(ROOT)}")

# Check tracked files without reading the user's local private RuneLite profile.
tracked = subprocess.check_output(["git", "ls-files", "-z"], cwd=ROOT).decode().split("\0")
for name in filter(None, tracked):
    if any(p.search(name) for p in BLOCKED_TRACKED_PATHS):
        errors.append(f"Sensitive file tracked: {name}")
    p = ROOT / name
    if p.is_file() and p.suffix.lower() in {".java", ".gradle", ".properties", ".yml", ".md", ".py", ".txt"}:
        data = p.read_text(encoding="utf-8")
        for marker in SECRET_MARKERS:
            if marker.search(data):
                errors.append(f"Possible secret in tracked file: {name}")

# The Plugin Hub should build with its standard, dependency-limited process.
manifest = (ROOT / "runelite-plugin.properties").read_text(encoding="utf-8")
if "build=standard" not in manifest or "plugins=com.smartbanksearch.SmartBankSearchPlugin" not in manifest:
    errors.append("Unexpected Plugin Hub build mode or plugin entrypoint")
gradle = (ROOT / "build.gradle").read_text(encoding="utf-8")
if "mavenLocal()" in gradle:
    errors.append("Avoid mavenLocal() to prevent build dependency substitution")
if re.search(r"(?m)^\s*(?:implementation|runtimeOnly|api)\s+(?!//)", gradle):
    errors.append("Unexpected production dependency requires explicit supply-chain review")

if errors:
    print("SECURITY SOURCE GUARD FAILED")
    for issue in errors:
        print("-", issue)
    sys.exit(1)

print(f"SECURITY SOURCE GUARD PASSED: {len(sources)} production Java files; "
      "one bank-search callback; no forbidden APIs or tracked secrets detected.")
