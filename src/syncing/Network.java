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

public abstract class Network extends Thread{
    protected Socket socket;
    protected ServerSocket serverSocket;
    protected String ip;
    protected int port;
    protected ObjectOutputStream oos;
    protected ObjectInputStream ois;
    protected Boolean connect = false;
    protected Boolean isServer;
    protected Boolean firstSync = true;
    protected Boolean syncCurrent;
    protected Boolean sync;
    protected Boolean active = true;
    protected String path;
    protected static final int BUFFER_SIZE = 8192; // taille du tampon utilisé pour la lecture et l'écriture des fichiers

    protected List <DateAndName> listServer;
    protected List <DateAndName> listClient;
    protected List <DateAndName> lastState;
    protected Boolean isChange;
    protected List<String> messages = new ArrayList<>();

    public void setSync(Boolean sync) {
        this.syncCurrent = sync;
    }

    public void setFirstSync(Boolean firstSync) {
        this.firstSync = firstSync;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setIp(String ip){
        this.ip = ip;
    }

    public void setPort(int port){
        this.port = port;
    }

    public void setConnect (Boolean connect){
        this.connect = connect;
    }


    public Boolean getSync(){
        return this.syncCurrent;
    }

    public Boolean getFirstSync(){
        return this.firstSync;
    }

    public Boolean getActive(){
        return this.active;
    }

    public String getPath(){
        return this.path;
    }

    public String getIp(){
        return this.ip;
    }

    public int getPort(){
        return this.port;
    }

    public void connect(){
        connect = false;
        while(!connect && active){
            try{
                if (!messages.get(7).equals("Connecting") && !messages.get(7).equals("Connecting.") && !messages.get(7).equals("Connecting..") && !messages.get(7).equals("Connecting...") && syncCurrent) {
                    addMessage("Connecting");
                }
                else if (messages.get(7).equals("Connecting")) {
                    messages.set(7, "Connecting.");
                }
                else if (messages.get(7).equals("Connecting.")) {
                    messages.set(7, "Connecting..");
                }
                else if (messages.get(7).equals("Connecting..")) {
                    messages.set(7, "Connecting...");
                }
                else if (messages.get(7).equals("Connecting...")) {
                    messages.set(7, "Connecting");
                }
                if (ip == null || ip.length() == 0) {
                    if(serverSocket == null) {
                        System.out.println("Creation socket");
                        serverSocket = new ServerSocket(port);
                        serverSocket.setSoTimeout(5000);
                    }
                    socket = serverSocket.accept();
                } else {
                    socket = new Socket(ip, port);
                }
                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());
                connect = true;
            }
            catch(Exception e){
                System.out.println("Error while connecting: " + e.getMessage());
                if (ip == null || ip.length() == 0) {
                    try{
                        System.out.println("Reset server socket");
                        serverSocket.close();
                    } catch (Exception ex) {
                        System.out.println("Error while closing socket: " + ex.getMessage());
                    }
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {
                    System.out.println("Error waiting: " + ie.getMessage());
                }
            }
        }
        if (connect){
            System.out.println("Connected.");
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
        }
        catch(Exception e){
            System.out.println("Error while closing connection");
        }
        connect = false;
    };


    public abstract void firstSync() throws IOException;
    public abstract void syncAndDelete() throws IOException;

    public Object receiveMessage() throws IOException, ClassNotFoundException {
        return ois.readObject();
    }

    public void sendMessage(Object message) throws IOException {
        oos.writeObject(message);
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
            fileList.add(new DateAndName(file.getName(), file.lastModified(), type, relativePath.toString().replace('\\', '/')));
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

    public void deleteFile(DateAndName fileToDelete) {
        File file = new File(path + "/" + fileToDelete.getPath());
        file.delete();
    }

    public void createDirectory(DateAndName directoryToCreate) {
        File folder = new File(path + "/" + directoryToCreate.getPath());
        folder.mkdirs();
        folder.setLastModified(directoryToCreate.getDate());
    }

    public void addMessage(String Msg) {
        if (messages.isEmpty()) {
            messages.add("");
            messages.add("");
            messages.add("");
            messages.add("");
            messages.add("");
            messages.add("");
            messages.add("");
            messages.add(Msg);
        }

        else {
            messages.set(0, messages.get(1));
            messages.set(1, messages.get(2));
            messages.set(2, messages.get(3));
            messages.set(3, messages.get(4));
            messages.set(4, messages.get(5));
            messages.set(5, messages.get(6));
            messages.set(6, messages.get(7));
            messages.set(7, Msg);
        }
    }

    public void resetMessages() {
        messages.set(0, "");
            messages.set(1, "");
            messages.set(2, "");
            messages.set(3, "");
            messages.set(4, "");
            messages.set(5, "");
            messages.set(6, "");
            messages.set(7, "");
    }

    public List<String> getMessages() {
        return messages;
    }

}