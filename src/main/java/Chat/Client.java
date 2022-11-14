package Chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client extends JFrame {

    private JTextField textField;
    private JTextArea textArea;
    private Socket socket = null;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public Client() {
        prepareUI();
        try {
            openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Client::new);
    }

    private void openConnection() throws IOException {
        socket = new Socket(Param.SERVER_ADDRESS, Param.PORT);
        textArea.append("Waiting server: " + Param.SERVER_ADDRESS + Param.PORT + "\n");
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        new Thread(() -> {
            while (true) {
                String messageFromServer = null;
                try {
                    messageFromServer = dataInputStream.readUTF();
                } catch (IOException e) {
                    textArea.append("Server Fault\n");
                    try {
                        openConnection();
                    } catch (IOException ex) {
                        e.printStackTrace();
                    }
                }
                if (messageFromServer.equals(Param.STOP_WORD)) break;
                textArea.append(messageFromServer + "\n");

            }
            textArea.append("Connection closed\n");
            textField.setEnabled(false);
            try {
                closeConnection();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void closeConnection() throws IOException {
        try {
            dataInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        if (textField.getText().trim().isEmpty()) return;
        try {
            dataOutputStream.writeUTF(textField.getText());
            textField.setText("");
            textField.grabFocus();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void prepareUI() {
        setBounds(200, 200, 500, 500);
        setTitle("EchoClient");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        add(new JScrollPane(textArea), BorderLayout.CENTER);


        JPanel panel = new JPanel(new BorderLayout());
        JButton button = new JButton("Send");
        panel.add(button, BorderLayout.EAST);
        textField = new JTextField();
        panel.add(textField, BorderLayout.CENTER);

        add(panel, BorderLayout.SOUTH);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        setVisible(true);
    }
}
