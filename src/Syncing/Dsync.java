package src.syncing;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.io.IOException;

public class Dsync extends Thread {
    private String path1;
    private String path2;
    private List<DateAndName> lastState = new ArrayList<>();

    private Boolean sync = false; // To sync or not to sync, that is the question
    private Boolean firstSync = true;

    private List<String> messages = new ArrayList<>(); // List to store the log messages that will be shown on the GUI

    public void setSync(Boolean bool) {
        this.sync = bool;
    }

    public void setFirstSync(Boolean bool) {
        this.firstSync = bool;
    }
    
    public void setPath1(String path) {
        this.path1 = path;
    }

    public void setPath2(String path) {
        this.path2 = path;
    }

    public void setLastState(String path) {
        lastState.clear();

        List<File> fileList = Arrays.asList(new File(path1).listFiles());
        Collections.sort(fileList);

        for(File file : fileList) {
            String type;
            if (file.isFile()) {type = "File";}
            else {type = "Directory";}
            lastState.add(new DateAndName(file.getName(), file.lastModified(), type));
        }
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

    public static void firstSync(String path1, String path2) throws IOException {
        File folder1 = new File(path1);
        File folder2 = new File(path2);

        List<File> list1 = Arrays.asList(folder1.listFiles());
        List<File> list2 = Arrays.asList(folder2.listFiles());

        for (File file2 : list2) {
            if (file2.isFile()) {
                Boolean contains = false;
                for (File file1 : list1) {
                    if(file2.getName().equals(file1.getName()) && file1.isFile()) {
                        contains = true;
                        try{
                            if(file2.lastModified() > file1.lastModified()) {
                                Files.copy(file2.toPath(), Path.of(path1 + "/" + file2.getName()), StandardCopyOption.REPLACE_EXISTING);
                                // System.out.println("Copied from " + path2 + " to " + path1 + ".");
                            }
                            else if(file2.lastModified() < file1.lastModified()) {
                                Files.copy(file1.toPath(), Path.of(path2 + "/" + file1.getName()), StandardCopyOption.REPLACE_EXISTING);
                                // System.out.println("Copied from " + path1 + " to " + path2 + ".");
                            }
                        }
                        catch(IOException e) {
                            // e.getCause();
                        }
                    }
                }
                if (!contains) {
                    try{
                        Files.copy(file2.toPath(), Path.of(path1 + "/" + file2.getName()), StandardCopyOption.REPLACE_EXISTING);
                        // System.out.println("Created in " + path1 + " from " + path2 + ".");
                    }
                    catch(IOException e) {
                        // e.getCause();
                    }
                }
            }
            else if (file2.isDirectory()) {
                Boolean contains = false;
                for (File file1 : list1) {
                    if(file2.getName().equals(file1.getName()) && file1.isDirectory()) {
                        contains = true;
                        Dsync.firstSync(path1 + "/" + file1.getName(), path2 + "/" + file2.getName());
                    }
                }
                if (!contains) {
                    Directory.copyDirectory(path2 + "/" + file2.getName(), path1 + "/" + file2.getName());
                }
            }
        }
    }

    public Boolean syncAndDelete(String path1, String path2) throws IOException {
        File folder1 = new File(path1);
        File folder2 = new File(path2);

        List<File> list1 = Arrays.asList(folder1.listFiles());
        List<File> list2 = Arrays.asList(folder2.listFiles());

        Boolean modified = false;

        for (File file2 : list2) {
            if (file2.isFile()) {
                Boolean contains = false;
                for (File file1 : list1) {
                    if(file2.getName().equals(file1.getName()) && file1.isFile()) {
                        contains = true;
                        try{
                            if(file2.lastModified() > file1.lastModified()) {
                                Files.copy(file2.toPath(), Path.of(path1 + "/" + file2.getName()), StandardCopyOption.REPLACE_EXISTING);
                                addMessage("Copied the modified file.");
                                modified = true;
                            }
                            else if(file2.lastModified() < file1.lastModified()) {
                                Files.copy(file1.toPath(), Path.of(path2 + "/" + file1.getName()), StandardCopyOption.REPLACE_EXISTING);
                                addMessage("Copied the modified file.");
                                modified = true;
                            }
                        }
                        catch(IOException e) {
                            // e.getCause();
                        }
                    }
                }
                if (!contains) {
                    try{
                        Files.copy(file2.toPath(), Path.of(path1 + "/" + file2.getName()), StandardCopyOption.REPLACE_EXISTING);
                        addMessage("Copied the missing file.");
                        modified = true;
                    }
                    catch(IOException e) {
                        // e.getCause();
                    }
                }
            }

            else if (file2.isDirectory()) {
                Boolean contains = false;
                for (File file1 : list1) {
                    if(file2.getName().equals(file1.getName()) && file1.isDirectory()) {
                        contains = true;
                        syncAndDelete(path1 + "/" + file1.getName(), path2 + "/" + file2.getName());
                    }
                }
                if (!contains) {
                    addMessage(Directory.copyDirectory(path2 + "/" + file2.getName(), path1 + "/" + file2.getName()));
                    modified = true;
                }
            }

        }

        for (File file1 : list1) {
            if (file1.isFile()) {
                Boolean contains = false;
                for (File file2 : list2) {
                    if(file1.getName().equals(file2.getName()) && file1.isFile()) {
                        contains = true;
                    }
                }
                if (!contains) {
                    try{
                        file1.delete();
                        addMessage("Deleted " + file1.getName() + ".");
                        modified = true;
                    }
                    catch(Exception e) {
                        // e.getCause();
                    }
                }
            }
            else if (file1.isDirectory()) {
                Boolean contains = false;
                for (File file2 : list2) {
                    if (file2.isDirectory()) {
                        if(file1.getName().equals(file2.getName())) {
                            contains = true;
                            syncAndDelete(path1 + "/" + file1.getName(), path2 + "/" + file2.getName());
                        }
                    }
                }
                if (!contains) {
                    addMessage(Directory.deleteDirectory(path1 + "/" + file1.getName()));
                    modified = true;
                }
            }
        }

        return modified;
    }

    public Boolean checkAndSyncSubFolders(String path1, String path2) throws IOException {
        File folder1 = new File(path1);
        File folder2 = new File(path2);

        List<File> list1 = Arrays.asList(folder1.listFiles());
        List<File> list2 = Arrays.asList(folder2.listFiles());

        Boolean modified = false;

        for (File file2 : list2) {
            for (File file1 : list1) {
                if(file1.getName().equals(file2.getName()) && file1.isDirectory() && file2.isDirectory()) {
                    if (Directory.lastModifiedDate(file2) > Directory.lastModifiedDate(file1)) {
                        modified = syncAndDelete(path1 + "/" + file1.getName(), path2 + "/" + file2.getName());
                    }
                    else {
                        modified = syncAndDelete(path2 + "/" + file2.getName(), path1 + "/" + file1.getName());
                    }
                }
            }
        }

        return modified;
    }

    public void syncLastState(String path1, String path2) throws IOException {

        File folder1 = new File(path1);
        File folder2 = new File(path2);

        List<File> list1 = Arrays.asList(folder1.listFiles());
        List<File> list2 = Arrays.asList(folder2.listFiles());

        Collections.sort(list1);
        Collections.sort(list2);

        List<DateAndName> dateAndNameList1 = new ArrayList<>();
        List<DateAndName> dateAndNameList2 = new ArrayList<>();

        for(File file : list1) {
            String type;
            if (file.isFile()) {type = "File";}
            else {type = "Directory";}
            dateAndNameList1.add(new DateAndName(file.getName(), file.lastModified(), type));
        }

        for(File file : list2) {
            String type;
            if (file.isFile()) {type = "File";}
            else {type = "Directory";}
            dateAndNameList2.add(new DateAndName(file.getName(), file.lastModified(), type));
        }

        if (!DateAndName.equalLists(dateAndNameList1, lastState) && !DateAndName.equalLists(dateAndNameList2, lastState)) {
            addMessage("Both folders modfied, combining their contents...");
            firstSync(path1, path2);
            firstSync(path2, path1);
            setLastState(path1);
            addMessage("Folders combined.");
            return;
        }

        if (DateAndName.equalLists(dateAndNameList1, lastState) && !DateAndName.equalLists(dateAndNameList2, lastState)) {
            addMessage("Second folder modified, updating the first...");
            syncAndDelete(path1, path2);
            setLastState(path2);
            addMessage("Folders synced.");
            return;
        }

        if (!DateAndName.equalLists(dateAndNameList1, lastState) && DateAndName.equalLists(dateAndNameList2, lastState)) {
            addMessage("First folder modified, updating the second...");
            syncAndDelete(path2, path1);
            setLastState(path1);
            addMessage("Folders synced.");
            return;
        }

        if (!checkAndSyncSubFolders(path1, path2)) addMessage("No changes detected.");
    }

    public void syncDirectories() throws IOException, InterruptedException {
        while(true) {
            if (this.firstSync && sync) {
                firstSync(path1, path2);
                firstSync(path2, path1);

                addMessage("Combined the two folders.");

                setLastState(path1);

                Thread.sleep(5000);

                this.firstSync = false;
            }

            else if (sync) {
                syncLastState(path1, path2);
                Thread.sleep(5000);
            }
            else {
                Thread.sleep(500);
            }

            // System.out.println(messages.get(2));
        }
    }

    public void run() {
        try {
            this.syncDirectories();
        } catch (IOException e) {
            // e.printStackTrace();
        } catch (InterruptedException e) {
            // e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        Dsync dsync = new Dsync();
        dsync.setPath1(args[0]);
        dsync.setPath2(args[1]);
        dsync.setSync(true);

        dsync.syncDirectories();
    }
}
