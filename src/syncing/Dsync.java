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

/**
 * The Dsync class used for the local synchronisation.
 * It extends the {@link java.lang.Thread} class to be able to run in parallel with the GUI.
 * <br/>
 * Author: Geryes Doumit
 */
public class Dsync extends Thread {
    // The two paths of the folders to synchronise
    private String path1;
    private String path2;

    // List of Files' dates and names that represents the last synchronised state that is shared by the two folders
    private List<DateAndName> lastState = new ArrayList<>();

    // To sync or not to sync, that is the question
    private Boolean sync = false;

    // Boolean that specifies if it is the first time syncing or not
    private Boolean firstSync = true;

    // List to store the log messages that will be shown on the GUI
    private List<String> messages = new ArrayList<>();

    // --------------- setters --------------- //
    /**
     * Sets the sync boolean to true or false.
     * @param bool
     */
    public void setSync(Boolean bool) {
        this.sync = bool;
    }

    /**
     * Sets the firstSync boolean to true or false.
     * @param bool
     */
    public void setFirstSync(Boolean bool) {
        this.firstSync = bool;
    }
    
    /**
     * Sets the path1 variable to the given path.
     * @param path
     */
    public void setPath1(String path) {
        this.path1 = path;
    }

    /**
     * Sets the path2 variable to the given path.
     * @param path
     */
    public void setPath2(String path) {
        this.path2 = path;
    }

    /**
     * Sets the lastState variable using the .
     * @param list
     */
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

    // --------------- Adding to the messages list, resetting it and getting the list --------------- //
    /**
     * Adds a message at the end of the list and shifts the other messages.
     * @param Msg
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
     * Resets the messages list.
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
     * Get the messages list.
     * @return the messages list.
     */
    public List<String> getMessages() {
        return messages;
    }

    // --------------- Syncing functions --------------- //

    /** The first synchronisation between two folders. The goal here is to combine the folders so that no file is lost.
     * @param path1 The path of the first folder.
     * @param path2 The path of the second folder.
     */
    public static void firstSync(String path1, String path2) throws IOException {

        // Generating the list of files to compare them later on
        File folder1 = new File(path1);
        File folder2 = new File(path2);

        List<File> list1 = Arrays.asList(folder1.listFiles());
        List<File> list2 = Arrays.asList(folder2.listFiles());

        // Double for loop that will go through every element in the two lists
        for (File file2 : list2) {
            if (file2.isFile()) {

                // For each file in list2, it checks if a file of list1 has the same name.
                // If it does, then we keep the file with the latest modified date.
                // If it doesn't, then we copy it in folder1.

                // This function will copy everything from folder2 that isn't in folder1, but not vice-versa.
                // That's why we call it twice later on to merge the two folders.

                Boolean contains = false;
                for (File file1 : list1) {
                    if(file2.getName().equals(file1.getName()) && file1.isFile()) {
                        contains = true;
                        try{
                            if(file2.lastModified() > file1.lastModified()) {
                                Files.copy(file2.toPath(), Path.of(path1 + "/" + file2.getName()), StandardCopyOption.REPLACE_EXISTING);
                                new File(path1 + "/" + file2.getName()).setLastModified(file2.lastModified());
                                // System.out.println("Copied from " + path2 + " to " + path1 + ".");
                            }
                            else if(file2.lastModified() < file1.lastModified()) {
                                Files.copy(file1.toPath(), Path.of(path2 + "/" + file1.getName()), StandardCopyOption.REPLACE_EXISTING);
                                new File(path2 + "/" + file1.getName()).setLastModified(file1.lastModified());
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
                        new File(path1 + "/" + file2.getName()).setLastModified(file2.lastModified());
                        // System.out.println("Created in " + path1 + " from " + path2 + ".");
                    }
                    catch(IOException e) {
                        // e.getCause();
                    }
                }
            }
            else if (file2.isDirectory()) {

                // For each directory in list2, it checks if a directory of list1 has the same name.
                // If it does, then we do a firstSync between the two directories that have the same name.
                // If it doesn't, then we copy it in folder1.

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

    /**
     * The normal synchronisation between two folders.
     * <br/>
     * The difference between this and firstSync is that if a file is deleted from a directory, we want to delete it from the other.
     * <br/>
     * Here, path2 takes priority over path1. If a file isn't in path2, it is deleted from path1. If a file isn't in path1, it is copied from path2.
     * @param path1 The path of the first folder.
     * @param path2 The path of the second folder.
     * @return A Boolean that tells us if anything has been modified.
     * @throws IOException
     */
    public Boolean syncAndDelete(String path1, String path2) throws IOException {

        // Generating the list of files to compare them later on
        File folder1 = new File(path1);
        File folder2 = new File(path2);

        List<File> list1 = Arrays.asList(folder1.listFiles());
        List<File> list2 = Arrays.asList(folder2.listFiles());

        // The Boolean to return
        Boolean modified = false;

        for (File file2 : list2) {
            if (file2.isFile()) {

                // for each file in list2, it checks if a file of list1 has the same name.
                // If it does, then we keep the file with the latest modified date.
                // If it doesn't, then we copy it in folder1.
                // if a modification is made, modified is set to true.

                Boolean contains = false;
                for (File file1 : list1) {
                    if(file2.getName().equals(file1.getName()) && file1.isFile()) {
                        contains = true;
                        try{
                            if(file2.lastModified() > file1.lastModified()) {
                                Files.copy(file2.toPath(), Path.of(path1 + "/" + file2.getName()), StandardCopyOption.REPLACE_EXISTING);
                                new File(path1 + "/" + file2.getName()).setLastModified(file2.lastModified());
                                addMessage("Copied the modified file.");
                                modified = true;
                            }
                            else if(file2.lastModified() < file1.lastModified()) {
                                Files.copy(file1.toPath(), Path.of(path2 + "/" + file1.getName()), StandardCopyOption.REPLACE_EXISTING);
                                new File(path2 + "/" + file1.getName()).setLastModified(file1.lastModified());
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
                        new File(path1 + "/" + file2.getName()).setLastModified(file2.lastModified());
                        addMessage("Copied " + file2.getName() + ".");
                        modified = true;
                    }
                    catch(IOException e) {
                        // e.getCause();
                    }
                }
            }

            else if (file2.isDirectory()) {

                // for each directory in list2, it checks if a directory of list1 has the same name.
                // If it does, then we call syncAndDelete between the two directories that have the same name.
                // If it doesn't, then we copy it in folder1.
                // if a modification is made, modified is set to true.

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

        // We do another for loop here to check if the files/directories in folder1 are contained in folder2.
        // If a file isn't in folder2, it is deleted from folder1.
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

        // This function prioritizes folder2 over folder1, which is why we call it with different parameters,
        // depending on the folder that was modified when compared to the last synchronised state.
        // I like to call the folder it prioritizes "base folder".

        return modified;
    }

    /**
     * This function uses the {@link src.syncing.Dsync} functions to check the sub-directories contained inside the two directories to sync.
     * <br/>
     * This function is necessary because we do not compare the modified dates of folders between them directly, because they do not work the same way files do.
     * <br/>
     * It uses the lastModifiedDate function of the {@link src.syncing.Directory} class, which returns the date of the last modified file inside the folder.
     * <br/>
     * It uses the most recent folder as the base folder for syncAndDelete.
     * @param path1 The path of the first folder.
     * @param path2 The path of the second folder.
     * @return A Boolean that tells us if anything has been modified.
     * @throws IOException
     */
    public Boolean checkAndSyncSubFolders(String path1, String path2) throws IOException {

        // Generating the list of files to compare them later on
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

    /**
     * This function compares the names and dates of files with the last synchronized state.
     * @param path1 The path of the first folder.
     * @param path2 The path of the second folder.
     * @throws IOException
     */
    public void syncLastState(String path1, String path2) throws IOException {

        // Generating the list of files to generate the dates and names
        File folder1 = new File(path1);
        File folder2 = new File(path2);

        List<File> list1 = Arrays.asList(folder1.listFiles());
        List<File> list2 = Arrays.asList(folder2.listFiles());

        // Sorting the lists so that I can use the equalLists function that compares each element in a list with the corresponding element in another.
        Collections.sort(list1);
        Collections.sort(list2);

        // Generating the list of dates and names to compare them with the last state, using the DateAndName class
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

        // The messages added describe what each condition does. We correctly call the previous functions to do what is needed.
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

        if (!checkAndSyncSubFolders(path1, path2)) // checking the subdirectories.
        // If no modification was made, then we put a message:
        {
            if (!messages.get(7).equals("No changes detected") && !messages.get(7).equals("No changes detected.") && !messages.get(7).equals("No changes detected..") && !messages.get(7).equals("No changes detected...") ) {
                addMessage("No changes detected");
            }
            else if (messages.get(7).equals("No changes detected")) {
                messages.set(7, "No changes detected.");
            }
            else if (messages.get(7).equals("No changes detected.")) {
                messages.set(7, "No changes detected..");
            }
            else if (messages.get(7).equals("No changes detected..")) {
                messages.set(7, "No changes detected...");
            }
            else if (messages.get(7).equals("No changes detected...")) {
                messages.set(7, "No changes detected");
            }
            // This creates a small animation with the dots appearing and disappearing.
        }
    }

    /** 
     * The main function. A simple while loop with some conditions.
     * @throws IOException
     * @throws InterruptedException
     */
    public void syncDirectories() throws IOException, InterruptedException {
        while(true) {
            if (firstSync && sync) { // Only true when it is the first synchronisation.
                firstSync(path1, path2);
                firstSync(path2, path1);

                addMessage("Combined the two folders.");

                setLastState(path1);

                Thread.sleep(1500); // Waits 1.5 seconds

                firstSync = false;
            }

            else if (sync) {
                // Quick check to see if the folders haven't been moved/modified
                if (new File(path1).exists() && new File(path1).isDirectory()
                && new File(path2).exists() && new File(path2).isDirectory()) 
                {
                    syncLastState(path1, path2);
                }
                else {
                    String Msg = "The paths aren't valid anymore. Please reset and retry.";

                    if (!messages.get(7).equals(Msg)) addMessage(Msg);
                }
                Thread.sleep(2000); // Waits 2 seconds between each sync
            }
            else {
                Thread.sleep(500); // Waits only half a second if syncing is paused
            }
        }
    }

    /** 
     * Overriding the run function of the Thread class.
     * <br/>
     * This is the function that will be called when we start the thread.
     */
    @Override
    public void run() {
        try {
            this.syncDirectories();
        } catch (IOException e) {
            // e.printStackTrace();
        } catch (InterruptedException e) {
            // e.printStackTrace();
        }
    }
}
