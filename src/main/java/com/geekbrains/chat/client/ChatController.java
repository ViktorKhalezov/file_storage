package com.geekbrains.chat.client;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ChatController implements Initializable {

    public TextField input;
    public ListView<String> clientListView; // список файлов в директории клиента
    public ListView<String> serverListView;
    File[] clientFileList;
    private IoNet net;

    public void sendMsg(ActionEvent actionEvent) throws IOException {
        net.sendMsg(input.getText());
        input.clear();

        //  String item = listView.getSelectionModel().getSelectedItem();
        // отправить выбранный в listView файл на сервер
        // придумать как это сделать
    }

    private void addFileClientList(String msg) {
        Platform.runLater(() -> clientListView.getItems().add(msg));
    }

    private void addFileServerList(String msg) {
        Platform.runLater(() -> serverListView.getItems().add(msg));
    }

    public void sendFile(ActionEvent actionEvent) throws IOException {
        String item = clientListView.getSelectionModel().getSelectedItem();
        System.out.println(item);
        net.sendMsg("fname" + item);
        for(int i = 0; i < clientFileList.length; i++) {
            if(item.equals(clientFileList[i].getName())) {
                long fileSize = clientFileList[i].length();
                net.sendMsg(String.valueOf(fileSize));
                System.out.println(fileSize);
                net.sendFile(clientFileList[i]);
            }
        }
    }

    private void showFileList() {
        File folder = new File("client_files");
        clientFileList = folder.listFiles();

        for(int i = 0; i < clientFileList.length; i++) {
            String fileName = clientFileList[i].getName();
            addFileClientList(fileName);
        }

    }

    private void showServerFiles(String msg) {
            serverListView.getItems().clear();
            String[] files = msg.split("\n");
            for(String f : files) {
                System.out.println(f);
                addFileServerList(f);
            }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showFileList();
        try {
            Socket socket = new Socket("localhost", 8189);
            net = new IoNet(this::showServerFiles, socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}



