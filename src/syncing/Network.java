package src.syncing;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class Network {
    protected Socket socket;
    protected String ip;
    protected int port;
    protected ServerSocket serverSocket;
    protected ObjectOutputStream out;
    protected ObjectInputStream in;
    protected FileInputStream fis;
    protected Boolean connect;
    protected Boolean isServer;
    protected String path;
    protected static final int BUFFER_SIZE = 8192; // taille du tampon utilisé pour la lecture et l'écriture des fichiers


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

    public List<DateAndName> listFiles(String path, String root) {
        File folder = new File(path);
        List<File> filesTemp = new ArrayList<File>(Arrays.asList(folder.listFiles()));
        List<DateAndName> fileList = new ArrayList<DateAndName>();
        Path basePath = Paths.get(root);

        for (File file : filesTemp) {
            String type;
            if (file.isFile()) {
                type = "File";
            } else {
                type = "Directory";
                fileList.addAll(listFiles(file.getAbsolutePath(), root)); // appel récursif pour les sous-dossiers
            }
            Path absolutePath = Paths.get(file.getAbsolutePath());
            Path relativePath = basePath.relativize(absolutePath);
            fileList.add(new DateAndName(file.getName(), file.lastModified(), type, relativePath.toString()));
        }

        return fileList;
    }

    public void sendFile(DateAndName file) throws IOException {
        System.out.println("Satrting sending file...");
        System.out.println("Sending file " + file.getName() + "...");
        System.out.println("Relative path: " + file.getPath());
        System.out.println("Absolute path: " + path + "/" + file.getPath());

        File fileToSend = new File(path + "/" + file.getPath());

        FileInputStream fis = new FileInputStream(fileToSend);

        // Sending file informations
        out.writeObject(file);

        // Sending file
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;

        while ((bytesRead = fis.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        out.flush();

        System.out.println("Sending done.");

    }
    
    public void receiveFile() throws IOException, ClassNotFoundException {
        InputStream is = socket.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(is);

        DateAndName file = (DateAndName) ois.readObject();

        File folder = new File(path + "/" + file.getPath());
        folder.mkdirs();

        FileOutputStream fos = new FileOutputStream(path + "/" + file.getPath());
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;

        while ((bytesRead = is.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
        }
        fos.flush();

        File fileToModify = new File(path + "/" + file.getPath());
        if (fileToModify.exists() && !fileToModify.isDirectory() && !(fileToModify.lastModified() == file.getDate())) {
            fileToModify.setLastModified(file.getDate());
        }
    }
    

}