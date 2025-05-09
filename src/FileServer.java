import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileServer {
    private static final int PORT = 12345;

    public static void main(String[] args) {
        // Ensure the "server_files" directory exists
        File serverDir = new File("server_files");
        if (!serverDir.exists()) {
            serverDir.mkdir();
        }

        ExecutorService executor = Executors.newFixedThreadPool(5); // Handle up to 5 clients simultaneously
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connected to client.");
                executor.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
             DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream())) {

            while (true) {
                String command = dis.readUTF();

                if ("SEND".equals(command)) {
                    receiveFile(dis);
                } else if ("REQUEST".equals(command)) {
                    sendFile(dis, dos);
                } else if ("VIEW".equals(command)) {
                    listFiles(dos);
                } else if ("EXIT".equals(command)) {
                    System.out.println("Client disconnected.");
                    break;
                } else {
                    System.out.println("Unknown command: " + command);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveFile(DataInputStream dis) throws IOException {
        String fileName = dis.readUTF();
        long fileSize = dis.readLong();

        File file = new File("server_files/" + fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] buffer = new byte[4096];
            int read;
            long totalRead = 0;

            while ((read = dis.read(buffer, 0, (int) Math.min(buffer.length, fileSize - totalRead))) > 0) {
                fos.write(buffer, 0, read);
                totalRead += read;
            }
            System.out.println("Received file: " + fileName);
        }
    }

    private void sendFile(DataInputStream dis, DataOutputStream dos) throws IOException {
        String fileName = dis.readUTF();
        File file = new File("server_files/" + fileName);

        if (file.exists()) {
            dos.writeUTF("FOUND");
            dos.writeLong(file.length());

            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = fis.read(buffer)) > 0) {
                    dos.write(buffer, 0, read);
                }
            }
            System.out.println("Sent file: " + fileName);
        } else {
            dos.writeUTF("NOT_FOUND");
            System.out.println("Requested file not found: " + fileName);
        }
    }

    private void listFiles(DataOutputStream dos) throws IOException {
        File directory = new File("server_files");
        File[] files = directory.listFiles();

        if (files != null) {
            dos.writeInt(files.length); // Send number of files
            for (File file : files) {
                dos.writeUTF(file.getName()); // Send each file name
            }
            System.out.println("Listed " + files.length + " files to client.");
        } else {
            dos.writeInt(0); // No files available
            System.out.println("No files available to list.");
        }
    }
}
