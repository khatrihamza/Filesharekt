# Filesharekt — Quick Start Guide

## What is Filesharekt?

Filesharekt is a **production-grade Android app** for sharing files over Wi‑Fi Direct (P2P) between nearby Android devices. No internet, no cloud, no delay — just peer-to-peer file transfers with a beautiful Material Design 3 interface.

## Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/khatrihamza/Filesharekt.git
   cd Filesharekt
   ```

2. **Open in Android Studio:**
   - File → Open → Select the Filesharekt folder
   - Let Gradle sync (takes 2-5 minutes on first build)

3. **Build & Deploy:**
   - Connect 2+ Android devices via USB (or use emulators)
   - Click **Run** in Android Studio or:
     ```bash
     ./gradlew installDebug
     ```

## How to Use

### Setup (First Time)
1. Launch the app on **Device A** and **Device B**
2. Grant location permissions when prompted (required for Wi‑Fi P2P discovery)
3. Enable Wi‑Fi (not hotspot) on both devices

### Sending a File

**On Device A (Sender):**
1. Tap the **Transfers** tab
2. Tap **"Pick File"** → select a file from your device
3. The file is queued for transfer

**On Device B (Receiver):**
1. Tap the **Discover** tab
2. Tap **"Discover Peers"** button
3. Wait 5-10 seconds to see Device A in the peer list
4. Tap **"Connect"** next to Device A's name
5. Accept the connection prompt (if shown)

**Back to Device A:**
6. Once connected, tap the **Transfers** tab
7. You'll see the transfer start automatically with progress bar
8. Check the **History** tab to see completed transfers

### File Locations

**Sent Files:**
- Tracked in app's **Transfer History** (Transfers tab)

**Received Files:**
- Stored in: `/sdcard/Android/data/com.example.filesharekt/files/Downloads/`
- Access via file manager or Android Studio's Device File Explorer

### Monitoring

**Check Transfers:**
- Tap **Transfers** tab → see active + queued transfers with progress
- Tap **History** tab → see all past transfers with timestamps

**Device Info:**
- Tap **Settings** tab → see your device name, MAC address, Wi‑Fi P2P status, IP address

## Features at a Glance

| Feature | Details |
|---------|---------|
| **P2P Discovery** | Automatically finds nearby devices on the same Wi‑Fi network |
| **Secure Connection** | Direct device-to-device connection, no server/internet required |
| **Progress Tracking** | Real-time progress bar + speed/ETA for each transfer |
| **Transfer History** | Local SQLite database logs all transfers |
| **Material Design 3** | Modern, clean UI with bottom navigation |
| **Concurrent Transfers** | Handle multiple transfers simultaneously |
| **Network Info** | View connection stats (signal, link speed, IP address) |
| **Transfer Logs** | View detailed logs of all operations |
| **Batch Clear** | Clear all transfer history with one tap |

## Troubleshooting

### "No Peers Found"
- Ensure both devices are on the **same Wi‑Fi network**
- Try restarting the Wi‑Fi or opening Settings → turn off/on Wi‑Fi P2P
- Emulators: Use physical devices instead (emulator Wi‑Fi P2P is unreliable)

### Transfer Fails
- Check that devices are still connected (tap **Discover** again)
- Ensure sufficient storage on receiver device
- Check transfer logs in **History** for error details

### Permissions Denied
- Go to Settings → Apps → Filesharekt → Permissions
- Grant **Location** (required for P2P), **Storage** (for file access)

### Can't Find App in Play Store
- This is a **development/custom build** — build it yourself from source using Android Studio

## Development

### Project Structure
```
Filesharekt/
├── app/src/main/
│   ├── java/com/example/filesharekt/
│   │   ├── MainActivity.kt              # Main UI activity
│   │   ├── data/                        # Room DB entities, DAO, repository
│   │   ├── ui/                          # Fragments, ViewModels, Adapters
│   │   ├── transfer/                    # Custom transfer protocol
│   │   └── utils/                       # Network & file utilities
│   └── res/
│       ├── layout/                      # Material Design 3 layouts
│       ├── menu/                        # Bottom navigation menu
│       └── values/                      # Resources
├── build.gradle                         # Project-level Gradle config
├── app/build.gradle                     # App-level Gradle config
└── README.md                            # Full documentation
```

### Key Technologies
- **Language:** Kotlin
- **UI:** Material Design 3, AndroidX
- **Database:** Room (SQLite)
- **Networking:** Wi‑Fi P2P Manager, Sockets
- **State Management:** ViewModel, LiveData, Flow
- **Concurrency:** Coroutines

### Building from Command Line
```bash
# Debug build
./gradlew assembleDebug

# Release build (unsigned)
./gradlew assembleRelease

# Install on connected device
./gradlew installDebug

# Run tests
./gradlew test
```

## Known Limitations

- ⚠️ **Emulator:** Wi‑Fi P2P doesn't work on most emulators; use physical devices
- ⚠️ **File Size:** Limited by available storage on receiver
- ⚠️ **Single Connection:** One connection per peer (can be enhanced)
- ⚠️ **No Encryption:** Files sent in plain text (consider adding TLS in production)

## Future Roadmap

- [ ] End-to-end encryption (AES-256)
- [ ] Resumable transfers (pause/resume)
- [ ] Batch operations (send folders)
- [ ] Background service mode
- [ ] Cloud backup integration
- [ ] Desktop client (Windows/Mac)
- [ ] Bandwidth throttling
- [ ] Share via QR code

## Contributing

Found a bug or have an idea? Open an issue or submit a pull request!

## License

MIT License — See LICENSE file in repository

## Support

**Having issues?**
1. Check Troubleshooting section above
2. Open an issue on GitHub with logs/screenshots
3. Ensure you're using Android 5.0+ (API 21) or higher

---

**Happy sharing! 📱📁**
