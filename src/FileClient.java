import java.io.*;
import java.net.*;
import java.util.Scanner;

public class FileClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             DataInputStream dis = new DataInputStream(socket.getInputStream());
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to the server.");

            boolean running = true;

            while (running) {
                System.out.println("\nSelect an option:");
                System.out.println("1. View files on server");
                System.out.println("2. Request a file from the server");
                System.out.println("3. Send a file to the server");
                System.out.println("4. Exit");
                System.out.print("Enter your choice: ");

                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        viewFilesOnServer(dos, dis);
                        break;
                    case 2:
                        requestFile(dos, dis, scanner);
                        break;
                    case 3:
                        sendFile(dos, scanner);
                        break;
                    case 4:
                        running = false;
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void viewFilesOnServer(DataOutputStream dos, DataInputStream dis) throws IOException {
        dos.writeUTF("VIEW");
        dos.flush();

        int fileCount = dis.readInt();
        if (fileCount == 0) {
            System.out.println("No files available on the server.");
        } else {
            System.out.println("Files available on the server:");
            for (int i = 0; i < fileCount; i++) {
                System.out.println("- " + dis.readUTF());
            }
        }
    }

    private static void requestFile(DataOutputStream dos, DataInputStream dis, Scanner scanner) throws IOException {
        System.out.print("Enter the name of the file to request: ");
        String fileName = scanner.nextLine();

        dos.writeUTF("REQUEST");
        dos.writeUTF(fileName);
        dos.flush();

        String response = dis.readUTF();
        if ("FOUND".equals(response)) {
            long fileSize = dis.readLong();
            try (FileOutputStream fos = new FileOutputStream("client_files/" + fileName)) {
                byte[] buffer = new byte[4096];
                int read;
                long totalRead = 0;

                while ((read = dis.read(buffer, 0, (int) Math.min(buffer.length, fileSize - totalRead))) > 0) {
                    fos.write(buffer, 0, read);
                    totalRead += read;
                }
                System.out.println("File received successfully: " + fileName);
            }
        } else {
            System.out.println("File not found on the server.");
        }
    }

    private static void sendFile(DataOutputStream dos, Scanner scanner) throws IOException {
        System.out.print("Enter the file path to send: ");
        String filePath = scanner.nextLine();
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("File does not exist. Please check the path and try again.");
            return;
        }

        dos.writeUTF("SEND");
        dos.writeUTF(file.getName());
        dos.writeLong(file.length());
        dos.flush();

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = fis.read(buffer)) > 0) {
                dos.write(buffer, 0, read);
            }
            System.out.println("File sent successfully: " + file.getName());
        }
    }
}
