package src.syncing;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.net.ConnectException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.lang.Thread;

public abstract class Network {
    protected Socket socket;
    protected ServerSocket serverSocket;
    protected String ip;
    protected int port;
    protected ObjectOutputStream oos;
    protected ObjectInputStream ois;
    protected Boolean connect = false;
    protected Boolean isServer;
    protected String path;
    protected static final int BUFFER_SIZE = 8192; // taille du tampon utilisé pour la lecture et l'écriture des fichiers

    protected List <DateAndName> listServer;
    protected List <DateAndName> listClient;
    protected List <DateAndName> lastState;


    // Getter

    public void connect(){
        try{
            if (ip == null || ip.length() == 0) {
                serverSocket = new ServerSocket(port);
                serverSocket.setSoTimeout(30000);
                socket = serverSocket.accept();
            } else {
                socket = new Socket(ip, port);
            }
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            connect = true;
        }
        catch(Exception e){
            System.out.println("Error while connecting");
        }
    }

    public void resetConnection() {
        connect = false;
        try {
            oos.close();
            ois.close();
            socket.close();
            if (ip == null || ip.length() == 0) {
                serverSocket.setSoTimeout(30000);
                socket = serverSocket.accept();
            } else {
                try{
                    Thread.sleep(100);
                }
                catch(InterruptedException ie){
                    System.out.println("Error while waiting");
                }
                while(socket.isClosed()){
                    try{
                        socket = new Socket(ip, port);
                    }
                    catch(UnknownHostException e){
                        System.out.println("Unknown host: " + ip);
                    }
                    catch(ConnectException e){
                        System.out.println("Connection refused: " + ip + ":" + port);
                    }
                    catch(Exception e){
                        System.out.println("Connection failed, retrying...");
                    }
                }
            }
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            connect = true;
        } catch (IOException e) {
            System.out.println("Error resetting connection: " + e.getMessage());
        }
    }
    

    public void close(){
        try{
            oos.close();
            ois.close();
            socket.close();
            if (ip == null){
                serverSocket.close();
            }
        }
        catch(Exception e){
            System.out.println("Error while closing connection");
        }
        connect = false;
    };


    public abstract void firstSync() throws IOException;
    public abstract void syncAndDelete() throws IOException;

    public List<DateAndName> receiveFilesList() throws IOException, ClassNotFoundException {
        return (List<DateAndName>) ois.readObject();
    }

    public void sendMessage(List<DateAndName> files) throws IOException {
        oos.writeObject(files);
        oos.flush();
    }

    public List<DateAndName> listFiles(String path, String root) {
        File folder = new File(path);
        List<File> filesTemp = new ArrayList<File>(Arrays.asList(folder.listFiles()));
        List<DateAndName> fileList = new ArrayList<DateAndName>();
        Path basePath = Paths.get(root);

        for (File file : filesTemp) {
            if (file.isHidden()){
                continue;
            }
            String type;
            if (file.isFile()) {
                type = "File";
            } else {
                type = "Directory";
                fileList.addAll(listFiles(file.getAbsolutePath(), root)); // appel récursif pour les sous-dossiers
            }
            Path absolutePath = Paths.get(file.getAbsolutePath());
            Path relativePath = basePath.relativize(absolutePath);
            if (file.isDirectory()){
                continue;
            }
            fileList.add(new DateAndName(file.getName(), file.lastModified(), type, relativePath.toString()));
        }

        return fileList;
    }

    public void sendFile(DateAndName fileToSend) throws IOException {
        File file = new File(path + "/" + fileToSend.getPath());

        byte[] byteArray = new byte[BUFFER_SIZE];
        int count;
        FileInputStream fis = new FileInputStream(file);

        while ((count = fis.read(byteArray)) > 0) {
            oos.write(byteArray, 0, count);
        }

        oos.flush();
        fis.close();
        resetConnection();
    }

    public void receiveFile(DateAndName fileToReceive) throws IOException, ClassNotFoundException {

        // On crée le dossier dans lequel est le fichier si il n'existe pas
        String folderPath = fileToReceive.getPath().lastIndexOf("/") != -1 ? fileToReceive.getPath().substring(0, fileToReceive.getPath().lastIndexOf("/")) : "";
        if (folderPath.length() > 0) {
            File folder = new File(path + "/" + folderPath);
            folder.mkdirs();
        }

        FileOutputStream fos = new FileOutputStream(path + "/" + fileToReceive.getPath());
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;

        while ((bytesRead = ois.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
        }
        fos.flush();

        File fileToModify = new File(path + "/" + fileToReceive.getPath());
        if (fileToModify.exists() && !fileToModify.isDirectory() && !(fileToModify.lastModified() == fileToReceive.getDate())) {
            fileToModify.setLastModified(fileToReceive.getDate());
        }

        fos.close();
        resetConnection();
    }
    

}