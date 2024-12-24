# 🌐 Peer-to-Peer (P2P) File Sharing System

A lightweight and straightforward P2P file-sharing system for local and same-machine configurations.

---

## ⚙️ Configuration Guide

### 🖥️ Same Machine Setup
Set up peers on the same machine with the following details:

| **Peer** | **Port** | **IP Address**   |
|----------|----------|------------------|
| Peer 1   | 5001     | `127.0.0.1`      |
| Peer 2   | 5002     | `127.0.0.1`      |
| Peer 3   | 5003     | `127.0.0.1`      |

---

### 🏠 Local Network Setup
To configure peers on the same local network:

1. **Find your IP Address**:
   - **Windows**: Open Command Prompt and run:
     ```cmd
     ipconfig
     ```
   - **Linux**: Open Terminal and run:
     ```bash
     ifconfig
     ```

2. **Use the IP Address**:
   Replace `127.0.0.1` with the IP address retrieved from the above step in your peer configuration.

---

## 📌 Notes
- Ensure the ports (5001, 5002, 5003, etc.) are not blocked by your firewall.
- All peers should be connected to the same network for local configurations.
- For cross-network or internet-based setups, consider setting up port forwarding on your router.
