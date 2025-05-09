# Peer-to-Peer File Sharing Application

## Objective
To create a simple peer-to-peer (P2P) file-sharing application using Java socket programming, enhancing understanding of networking, socket communication, and multi-threading.

## Overview
The application enables two users to connect over a network and share files directly. One user acts as the **Client** and the other as the **Server**, each performing specific roles for file transfer.

## Roles
### Client:
- Sends files to the server.
- Requests files from the server.

### Server:
- Receives files from the client and saves them.
- Serves requested files to the client.

## Features
- **Socket Setup:** TCP sockets for reliable communication with error handling.
- **File Transfer Protocol:** 
  - The client sends and receives files.
  - The server handles file reception and file serving.
- **Multi-threading:** Server supports multi-threading to manage multiple clients.
- **File Integrity:** Basic file size comparison to ensure correct file transfer.

## Technologies Used
- Java (using `java.net` and `java.io` packages)

## Setup Instructions
1. **Clone the repository:**
git clone https://github.com/Sitharahansamali/P2P-File-Sharing.git

2. **Running the Server:**
Navigate to the server directory and run:
javac Server.java
java Server

3. **Running the Client:**
Navigate to the client directory and run:
javac Client.java
java Client



4. **File Transfer:**
- The client can send files to the server and request files via a simple command-line interface.

## Usage
- **Client Side:**
- Send a file to the server: Choose "Send File" and specify the file path.
- Request a file from the server: Choose "Receive File" and pick from available files.

- **Server Side:**
- View available files and serve them to clients.
- Handle multiple client connections using multi-threading.




