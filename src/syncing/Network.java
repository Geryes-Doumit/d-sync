package src.syncing;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public abstract class Network {
    protected Socket socket;
    protected String ip;
    protected int port;
    protected ServerSocket serverSocket;
    protected ObjectOutputStream out;
    protected ObjectInputStream in;
    protected Boolean connect;
    protected Boolean isServer;
    protected String path;

    // Getter
    public Boolean getConnect() {
        return connect;
    }

    public List<DateAndName> receiveFilesList() throws IOException, ClassNotFoundException {
        return (List<DateAndName>) in.readObject();
    }

    public void sendMessage(List<DateAndName> files) throws IOException {
        out.writeObject(files);
        out.flush();
    }

    public abstract void close();
    public abstract void firstSync() throws IOException;

    public List<DateAndName> listFiles(String path){
        File folder = new File(path);
        List<File> filesTemp = new ArrayList<File>(Arrays.asList(folder.listFiles()));
        List<DateAndName> fileList = new ArrayList<DateAndName>();

        for (File file : filesTemp) {
            String type;
            if (file.isFile()) {
                type = "File";
            } else {
                type = "Directory";
                fileList.addAll(listFiles(file.getAbsolutePath())); // appel récursif pour les sous-dossiers
            }
            fileList.add(new DateAndName(file.getName(), file.lastModified(), type));
        }

        return fileList;
    }

}