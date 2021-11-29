package com.geekbrains.chat.server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Handler implements Runnable {

    private boolean running;
    private final byte[] buf;
    private final InputStream is;
    private final OutputStream os;
    private final Socket socket;
    private File[] fileList;

    public Handler(Socket socket) throws IOException {
        running = true;
        buf = new byte[8192];
        this.socket = socket;
        is = socket.getInputStream();
        os = socket.getOutputStream();
        sendFilesList();
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        try {
            while (running) {
                // вкрутить логику с получением файла от клиента

                int read = is.read(buf);
                String message = new String(buf, 0, read).trim();
                if(message.startsWith("fname")) {
                    String fileName = message.substring(5);
                    System.out.println(fileName);
                    byte[] buffer = new byte[8192];
                    int size = is.read(buffer);
                    String sizeString = new String(buffer, 0, size).trim();
                    long fileSize = Long.parseLong(sizeString);
                    saveFile(fileName, fileSize);
                    sendFilesList();
                }

           /*     if (message.equals("quit")) {
                    os.write("Client disconnected\n".getBytes(StandardCharsets.UTF_8));
                    close();
                    break;
                } */
               // System.out.println("Received: " + message);
             //   os.write((message + "\n").getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void close() throws IOException {
        os.close();
        is.close();
        socket.close();
    }

    public void saveFile(String fileName, long fileSize) {
        long read = 0L;
        try(FileOutputStream fos = new FileOutputStream("server_files/" + fileName)){
            while (read < fileSize) {
                int bytes = is.read(buf);
                fos.write(buf);
                read += bytes;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendFilesList() {
        File folder = new File("server_files");
        fileList = folder.listFiles();

        try {
           for(int i = 0; i < fileList.length; i++) {
               String fileName = fileList[i].getName();
                os.write((fileName + "\n").getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
