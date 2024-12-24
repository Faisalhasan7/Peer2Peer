import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Peer {

    static ArrayList<MyFile> receivedFiles = new ArrayList<>();
    static int fileId = 0;

    public static void main(String[] args) {
        // Configure peer's settings
        String peerName = JOptionPane.showInputDialog(null, "Enter your name (Peer 1, Peer 2, or Peer 3):");
        int listeningPort = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter your listening port:"));

        // Initialize GUI
        JFrame frame = new JFrame(peerName + " - File Sharing");
        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel filePanel = new JPanel();
        filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(filePanel);
        frame.add(scrollPane, BorderLayout.CENTER);

        JButton sendFileButton = new JButton("Send File");
        frame.add(sendFileButton, BorderLayout.SOUTH);

        frame.setVisible(true);

        // Start server thread to listen for incoming files
        new Thread(() -> startServer(listeningPort, filePanel, frame)).start();

        // Add action listener for sending files
        sendFileButton.addActionListener(e -> openSendFileDialog());

    }

    private static void startServer(int port, JPanel filePanel, JFrame frame) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();

                DataInputStream dis = new DataInputStream(socket.getInputStream());
                int fileNameLength = dis.readInt();
                if (fileNameLength > 0) {
                    byte[] fileNameBytes = new byte[fileNameLength];
                    dis.readFully(fileNameBytes);
                    String fileName = new String(fileNameBytes);

                    int fileContentLength = dis.readInt();
                    if (fileContentLength > 0) {
                        byte[] fileContentBytes = new byte[fileContentLength];
                        dis.readFully(fileContentBytes);

                        MyFile file = new MyFile(fileId++, fileName, fileContentBytes, getFileExtension(fileName));
                        receivedFiles.add(file);

                        JPanel fileRow = new JPanel();
                        fileRow.setLayout(new BoxLayout(fileRow, BoxLayout.X_AXIS));
                        JLabel fileLabel = new JLabel(fileName);
                        fileLabel.setFont(new Font("Arial", Font.BOLD, 20));
                        fileRow.add(fileLabel);

                        fileRow.setName(String.valueOf(file.getId()));
                        fileRow.addMouseListener(new MouseListener(file));
                        filePanel.add(fileRow);
                        frame.validate();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void openSendFileDialog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a file to send");
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File fileToSend = fileChooser.getSelectedFile();
            String peerIP = JOptionPane.showInputDialog(null, "Enter peer's IP:");
            int peerPort = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter peer's port:"));

            sendFile(fileToSend, peerIP, peerPort);
        }
    }

    private static void sendFile(File fileToSend, String peerIP, int peerPort) {
        try (Socket socket = new Socket(peerIP, peerPort);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             FileInputStream fis = new FileInputStream(fileToSend)) {

            String fileName = fileToSend.getName();
            byte[] fileNameBytes = fileName.getBytes();
            byte[] fileContentBytes = new byte[(int) fileToSend.length()];
            fis.read(fileContentBytes);

            dos.writeInt(fileNameBytes.length);
            dos.write(fileNameBytes);
            dos.writeInt(fileContentBytes.length);
            dos.write(fileContentBytes);

            JOptionPane.showMessageDialog(null, "File sent successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to send the file!");
            e.printStackTrace();
        }
    }

    private static String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        return index > 0 ? fileName.substring(index + 1) : "No extension";
    }

    static class MouseListener implements java.awt.event.MouseListener {
        private final MyFile file;

        MouseListener(MyFile file) {
            this.file = file;
        }

        @Override
        public void mouseClicked(java.awt.event.MouseEvent e) {
            JFrame previewFrame = new JFrame("Preview - " + file.getName());
            previewFrame.setSize(400, 300);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            previewFrame.add(panel);

            JLabel label = new JLabel("Preview: " + file.getName());
            panel.add(label);

            JButton saveButton = new JButton("Save File");
            panel.add(saveButton);

            saveButton.addActionListener(ae -> saveFile(file));

            previewFrame.setVisible(true);
        }

        private void saveFile(MyFile file) {
            try (FileOutputStream fos = new FileOutputStream(file.getName())) {
                fos.write(file.getData());
                JOptionPane.showMessageDialog(null, "File saved successfully!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Failed to save the file!");
                e.printStackTrace();
            }
        }

        @Override
        public void mousePressed(java.awt.event.MouseEvent e) {}
        @Override
        public void mouseReleased(java.awt.event.MouseEvent e) {}
        @Override
        public void mouseEntered(java.awt.event.MouseEvent e) {}
        @Override
        public void mouseExited(java.awt.event.MouseEvent e) {}
    }
}
