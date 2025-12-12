# Filesharekt

**Advanced Wi‑Fi Direct File Sharing App** — A production-ready Android application for peer-to-peer file transfers using Wi‑Fi Direct (P2P).

## Features

- **Wi‑Fi Direct Discovery & Connection** — Discover nearby peers and establish P2P connections
- **File Transfer** — Send and receive files with progress tracking and speed monitoring
- **Transfer History** — SQLite database logs all transfers with status and timestamps
- **Advanced Protocol** — Custom binary protocol with metadata support for reliable transfers
- **Concurrent Transfers** — Queue-based transfer management with multiple simultaneous transfers
- **Material Design 3 UI** — Modern, responsive interface with bottom navigation
- **Device Information** — Display MAC address, IP, and Wi‑Fi P2P status
- **Network Monitoring** — Real-time network stats (signal strength, link speed)
- **Transfer Logs** — File-based logging for debugging and auditing
- **Batch Operations** — Clear history, cancel transfers in bulk
- **Settings** — Auto-accept transfers, notification preferences

## Architecture

```
app/
├── src/main/java/com/example/filesharekt/
│   ├── MainActivity.kt                  # Activity with fragment navigation
│   ├── FileTransferService.kt           # Transfer server & logging
│   ├── WiFiP2pBroadcastReceiver.kt      # P2P event handling
│   ├── data/
│   │   ├── AppDatabase.kt               # Room database
│   │   ├── TransferEntity.kt            # DB entity
│   │   ├── TransferDao.kt               # DB access
│   │   └── TransferRepository.kt        # Data repository
│   ├── domain/
│   │   └── Models.kt                    # Domain models (PeerInfo, TransferInfo, etc.)
│   ├── transfer/
│   │   └── TransferProtocol.kt          # Custom binary protocol & transfer queue
│   ├── ui/
│   │   ├── viewmodel/
│   │   │   └── FileShareViewModel.kt    # Shared UI state
│   │   ├── adapter/
│   │   │   ├── PeerAdapter.kt           # Peer list RecyclerView
│   │   │   └── TransferAdapter.kt       # Transfer list RecyclerView
│   │   └── fragments/
│   │       ├── DiscoveryFragment.kt     # Peer discovery UI
│   │       ├── TransfersFragment.kt     # Active transfers & file picker
│   │       ├── HistoryFragment.kt       # Transfer history
│   │       └── SettingsFragment.kt      # Settings & device info
│   └── utils/
│       ├── NetworkMonitor.kt            # Network utilities
│       └── FileBrowser.kt               # File system utilities
└── src/main/res/
    ├── layout/
    │   ├── activity_main.xml            # Main activity with navigation
    │   ├── fragment_*.xml               # Fragment layouts
    │   ├── item_transfer.xml            # Transfer item card
    │   ├── item_peer.xml                # Peer item card
    │   └── ...
    └── menu/
        └── bottom_nav_menu.xml          # Bottom navigation menu
```

## Technologies

- **Kotlin** — Language
- **AndroidX** — UI components & lifecycle management
- **Room** — Local database
- **Material Design 3** — UI framework
- **LiveData & Flow** — Reactive state management
- **Coroutines** — Async operations
- **Wi‑Fi P2P Manager** — Direct peer networking
- **Sockets** — TCP transfer protocol

## Build & Run

### Prerequisites
- Android Studio 2023.1+
- Android SDK 34 (API level)
- Min SDK 21 (Android 5.0+)

### Steps
1. **Clone & Open** in Android Studio
2. **Sync Gradle** — Let Android Studio download dependencies
3. **Build** — Build > Make Project or `./gradlew assembleDebug`
4. **Run** — Run on device or emulator (min. 2 devices for P2P testing)

```bash
# From command line (with SDK installed):
./gradlew assembleDebug
./gradlew installDebug
```

### Testing
- Deploy to **two physical Android devices** (or two emulator instances with network bridge)
- On Device A: Tap **Discover** tab → "Discover Peers"
- On Device B: Tap **Transfers** → "Pick File" → select a file
- Observe transfer on Device A's **Transfers** tab
- Check **History** for completed transfers

## Configuration

### Permissions
- `ACCESS_FINE_LOCATION` — Required for Wi‑Fi P2P discovery
- `NEARBY_WIFI_DEVICES` — Android 12+ for P2P access
- `READ_EXTERNAL_STORAGE` — File access
- `WRITE_EXTERNAL_STORAGE` — Save received files

### Transfer Port
Default: **8988** (configurable in `FileTransferService.PORT`)

### Database
- **Name:** `filesharekt_db`
- **Location:** App's private directory
- **Migrations:** Version 1 (initial)

## API Endpoints (Internal)

### ViewModel Methods
```kotlin
viewModel.setPeers(deviceList)
viewModel.selectPeer(peer)
viewModel.createTransfer(fileName, fileSize, direction, remotePeer)
viewModel.updateTransferProgress(transferId, progress)
viewModel.markTransferCompleted(transferId)
viewModel.clearAllTransfers()
```

### Transfer Protocol
- **Magic:** `0x46534854` ("FSHT")
- **Types:** INIT (1), DATA (2), ACK (3), ERROR (4), CANCEL (5)
- **Chunk Size:** 64 KB
- **Metadata:** JSON-encoded file info in INIT message

## Known Limitations

- Emulator Wi‑Fi P2P may not work reliably; use physical devices
- File size limit depends on available storage
- One transfer per peer at a time (can be enhanced with queuing)

## Future Enhancements

- [ ] Resumable transfers
- [ ] Directory sync
- [ ] Bandwidth throttling
- [ ] Encryption (TLS/AES)
- [ ] Group transfer orchestration
- [ ] Web UI for desktop clients
- [ ] Firebase Cloud Messaging integration

## License

MIT — See LICENSE file

## Support

For issues or feature requests, open an issue in the repository.

---

**Built with ❤️ using Kotlin & Android**
