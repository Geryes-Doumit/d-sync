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


/**
 * Network class that contains all the methods to synchronize the two devices.
 * Extends {@link java.lang.Thread} to be able to run the synchronization in a separate thread.
* <br/>
 * Author: Marc Proux
 */
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
    protected Boolean sync = true;
    protected Boolean active = true;
    protected String path;
    protected static final int BUFFER_SIZE = 8192;

    protected List <DateAndName> listServer;
    protected List <DateAndName> listClient;
    protected List <DateAndName> lastState;
    protected Boolean isChange;
    protected List<String> messages = new ArrayList<>();

    /**
     * Set the sync boolean.
     * @param sync
     */
    public void setSync(Boolean sync) {
        this.syncCurrent = sync;
    }

    /**
     * Set the firstSync boolean.
     * @param firstSync
     */
    public void setFirstSync(Boolean firstSync) {
        this.firstSync = firstSync;
    }

    /**
     * Set the active boolean.
     * @param active
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * Set the path.
     * @param path of the folder to synchronize.
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Set the ip.
     * @param ip of the server.
     */
    public void setIp(String ip){
        this.ip = ip;
    }

    /**
     * Set the port.
     * @param port of the server.
     */
    public void setPort(int port){
        this.port = port;
    }

    /**
     * Set the connect boolean.
     * @param connect
     */
    public void setConnect (Boolean connect){
        this.connect = connect;
    }


    /**
     * Get the sync boolean.
     * @return sync boolean
     */
    public Boolean getSync(){
        return this.syncCurrent;
    }

    /**
     * Get the firstSync boolean.
     * @return firstSync boolean
     */
    public Boolean getFirstSync(){
        return this.firstSync;
    }

    /**
     * Get the active boolean.
     * @return active boolean
     */
    public Boolean getActive(){
        return this.active;
    }

    /**
     * Get the path.
     * @return path of the folder to synchronize.
     */
    public String getPath(){
        return this.path;
    }

    /**
     * Get the ip.
     * @return ip of the server.
     */
    public String getIp(){
        return this.ip;
    }

    /**
     * Get the port.
     * @return port of the server.
     */
    public int getPort(){
        return this.port;
    }

    /**
     * Create a new socket and connect to the other device.
     * Loop until the connection is established.
     * Add a message to the message list to show the connection status.
     * 
     * @throws IOException
     */
    public void connect(){
        connect = false;
        while(!connect && active){
            try{
                if (!messages.get(7).equals("Connecting") && !messages.get(7).equals("Connecting.") && !messages.get(7).equals("Connecting..") && !messages.get(7).equals("Connecting...")) {
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
                        serverSocket = new ServerSocket(port);
                        serverSocket.setSoTimeout(2000);
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
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    System.out.println("Error waiting: " + ie.getMessage());
                }
            }
        }
    }

    /**
     * Reset the connection by closing the socket and the streams.
     * 
     * @throws IOException
     */
    public void resetConnection() {
        connect = false;
        try {
            oos.close();
            ois.close();
            socket.close();
            if (ip == null || ip.length() == 0) {
                serverSocket.setSoTimeout(2000);
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
    

    /**
     * Close the socket and the streams.
     * Set the connect boolean to false.
     * 
     * @throws IOException
     */
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


    /**
     * Do the first synchronization between the two devices.
     * During this synchronization, no file is deleted everything is merged.
     * 
     * @throws IOException
     */
    public abstract void firstSync() throws IOException;


    /**
     * Do the synchronization between the two devices.
     * During this synchronization, the files deleted on one device are deleted on the other.
     * 
     * @throws IOException
     */
    public abstract void syncAndDelete() throws IOException;

    /**
     * Run the synchronization in a separate thread.
     * Loop until the synchronization is stopped and not execute the synchronization if the sync is false or if the folder to synchronize doesn't exist.
     */
    @Override
    public abstract void run();

    /**
     * Receive a message from the other device by using the ObjectInputStream.
     * 
     * @return  the message received as an Object.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Object receiveMessage() throws IOException, ClassNotFoundException {
        return ois.readObject();
    }

    /**
     * Send a message to the other device by using the ObjectOutputStream.
     * @param message
     * @throws IOException
     */
    public void sendMessage(Object message) throws IOException {
        oos.writeObject(message);
        oos.flush();
    }

    /**
     * List all the files and directories in the directory to synchronize.
     * 
     * @param path the path of the directory to get the files from.
     * @param root the path of the directory to synchronize.
     * @return the list of the files and directories in the directory to synchronize.
     */
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
                fileList.addAll(listFiles(file.getAbsolutePath(), root));
            }
            Path absolutePath = Paths.get(file.getAbsolutePath());
            Path relativePath = basePath.relativize(absolutePath);
            fileList.add(new DateAndName(file.getName(), file.lastModified(), type, relativePath.toString().replace('\\', '/')));
        }

        return fileList;
    }

    /**
     * Send a file to the other device.
     * 
     * @param fileToSend the file to send.
     * @throws IOException
     */
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

    /**
     * Receive a file from the other device and create the directory if it doesn't exist.
     * 
     * @param fileToReceive the file to receive.
     * @throws IOException
     * @throws ClassNotFoundException
     */
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

    /**
     * Delete a file.
     * 
     * @param fileToDelete the file to delete.
     */
    public void deleteFile(DateAndName fileToDelete) {
        File file = new File(path + "/" + fileToDelete.getPath());
        file.delete();
    }

    /**
     * Create a directory.
     * 
     * @param directoryToCreate the directory to create.
     */
    public void createDirectory(DateAndName directoryToCreate) {
        File folder = new File(path + "/" + directoryToCreate.getPath());
        folder.mkdirs();
        folder.setLastModified(directoryToCreate.getDate());
    }

    /**
     * Add a message to the message list display in the GUI.
     * 
     * @param Msg the message to add.
     */
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

    /**
     * Reset the message list.
     */
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

    /**
     * Get the message list.
     * 
     * @return the message list.
     */
    public List<String> getMessages() {
        return messages;
    }

}