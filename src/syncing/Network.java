package src.syncing;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class Network {
    protected Socket socket;
    protected ServerSocket serverSocket;
    protected String ip;
    protected int port;
    protected ObjectOutputStream oos;
    protected ObjectInputStream ois;
    protected Boolean connect;
    protected Boolean isServer;
    protected String path;
    protected static final int BUFFER_SIZE = 8192; // taille du tampon utilisé pour la lecture et l'écriture des fichiers


    // Getter

    public void resetConnection() {
        connect = false;
        try {
            oos.close();
            ois.close();
            socket.close();
            if (ip == null || ip.length() == 0) {
                System.out.println("Reset server");
                serverSocket.close();
                serverSocket = new ServerSocket(port);
                serverSocket.setSoTimeout(30000);
                socket = serverSocket.accept();
            } else {
                System.out.println("Reset client");
                while(!socket.isConnected()){
                    try{
                        System.out.println("Trying to connect to " + ip + ":" + port);
                        socket = new Socket(ip, port);
                    }
                    catch(Exception e){
                        System.out.println("Connection failed, retrying...");
                    }
                }
                System.out.println("Connected to " + ip + ":" + port);
            }
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            connect = true;
        } catch (IOException e) {
            System.out.println("Error resetting connection: " + e.getMessage());
        }
    }
    

    public void close(){
        try{
            oos.close();
            ois.close();
            if (ip.length()>0){
                socket.close();
            }
            else{
                serverSocket.close();
            }
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
        connect = false;
    };
    // public abstract void firstSync() throws IOException;

    // public List<DateAndName> receiveFilesList() throws IOException, ClassNotFoundException {
    //     return (List<DateAndName>) in.readObject();
    // }

    // public void sendMessage(List<DateAndName> files) throws IOException {
    //     out.writeObject(files);
    //     out.flush();
    // }

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
            fileList.add(new DateAndName(file.getName(), file.lastModified(), type, relativePath.toString()));
        }

        return fileList;
    }

    // public void sendFile(DateAndName file) throws IOException {
    //     System.out.println("Satrting sending file...");
    //     System.out.println("Sending file " + file.getName() + "...");
    //     System.out.println("Relative path: " + file.getPath());
    //     System.out.println("Absolute path: " + path + "/" + file.getPath());

    //     File fileToSend = new File(path + "/" + file.getPath());

    //     FileInputStream fis = new FileInputStream(fileToSend);

    //     // Sending file informations
    //     out.writeObject(file);

    //     // Sending file
    //     byte[] buffer = new byte[BUFFER_SIZE];
    //     int bytesRead;

    //     while ((bytesRead = fis.read(buffer)) != -1) {
    //         out.write(buffer, 0, bytesRead);
    //     }
    //     out.flush();

    //     System.out.println("Sending done.");

    // }

    public void sendFile(DateAndName fileToSend, ObjectOutputStream oos, BufferedOutputStream bos) throws IOException {
        File file = new File(path + "/" + fileToSend.getPath());
        byte[] byteArray = new byte[BUFFER_SIZE];
        FileInputStream fis = new FileInputStream(file);
        oos.writeObject(fileToSend);
    
        int count;
        while ((count = fis.read(byteArray)) > 0) {
            bos.write(byteArray, 0, count);
        }
        bos.flush();
        oos.reset();
        fis.close();
    }
    
    // public void receiveFile() throws IOException, ClassNotFoundException {
    //     ObjectInputStream ois = new ObjectInputStream(is);
    //     DateAndName file = (DateAndName) ois.readObject();

    //     File folder = new File(path + "/" + file.getPath());
    //     folder.mkdirs();

    //     FileOutputStream fos = new FileOutputStream(path + "/" + file.getPath());
    //     byte[] buffer = new byte[BUFFER_SIZE];
    //     int bytesRead;

    //     while ((bytesRead = is.read(buffer)) != -1) {
    //         fos.write(buffer, 0, bytesRead);
    //     }
    //     fos.flush();

    //     File fileToModify = new File(path + "/" + file.getPath());
    //     if (fileToModify.exists() && !fileToModify.isDirectory() && !(fileToModify.lastModified() == file.getDate())) {
    //         fileToModify.setLastModified(file.getDate());
    //     }
    // }

    public void receiveFile(ObjectInputStream ois, BufferedInputStream bis) throws IOException, ClassNotFoundException {
        DateAndName file = (DateAndName) ois.readObject();

        // On crée le dossier dans lequel est le fichier si il n'existe pas
        String folderPath = file.getPath().substring(0, file.getPath().lastIndexOf("/"));
        if (folderPath.length() > 0) {
            File folder = new File(path + "/" + folderPath);
            folder.mkdirs();
        }

        FileOutputStream fos = new FileOutputStream(path + "/" + file.getPath());
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;

        while ((bytesRead = bis.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
        }
        fos.flush();

        File fileToModify = new File(path + "/" + file.getPath());
        if (fileToModify.exists() && !fileToModify.isDirectory() && !(fileToModify.lastModified() == file.getDate())) {
            fileToModify.setLastModified(file.getDate());
        }

        fos.close();
    }
    

}