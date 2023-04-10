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

public class Dsync {
    private String path1;
    private String path2;
    private List<DateAndName> lastState = new ArrayList<>();

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
                                System.out.println("Copied from " + path2 + " to " + path1 + " !");
                            }
                            else if(file2.lastModified() < file1.lastModified()) {
                                Files.copy(file1.toPath(), Path.of(path2 + "/" + file1.getName()), StandardCopyOption.REPLACE_EXISTING);
                                System.out.println("Copied from " + path1 + " to " + path2 + " !");
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
                        System.out.println("Created in " + path1 + " from " + path2 + " !");
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

    public static void syncAndDelete(String path1, String path2) throws IOException {
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
                                System.out.println("Copied from " + path2 + " to " + path1 + " !");
                            }
                            else if(file2.lastModified() < file1.lastModified()) {
                                Files.copy(file1.toPath(), Path.of(path2 + "/" + file1.getName()), StandardCopyOption.REPLACE_EXISTING);
                                System.out.println("Copied from " + path1 + " to " + path2 + " !");
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
                        System.out.println("Created in " + path1 + " from " + path2 + " !");
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
                        Dsync.syncAndDelete(path1 + "/" + file1.getName(), path2 + "/" + file2.getName());
                    }
                }
                if (!contains) {
                    Directory.copyDirectory(path2 + "/" + file2.getName(), path1 + "/" + file2.getName());
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
                        System.out.println("Deleted " + file1.getName() + " from " + path1 + " !");
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
                            Dsync.syncAndDelete(path1 + "/" + file1.getName(), path2 + "/" + file2.getName());
                        }
                    }
                }
                if (!contains) {
                    Directory.deleteDirectory(path1 + "/" + file1.getName());
                }
            }
        }
    }

    public static void checkAndSyncSubFolders(String path1, String path2) throws IOException {
        File folder1 = new File(path1);
        File folder2 = new File(path2);

        List<File> list1 = Arrays.asList(folder1.listFiles());
        List<File> list2 = Arrays.asList(folder2.listFiles());

        for (File file2 : list2) {
            for (File file1 : list1) {
                if(file1.getName().equals(file2.getName()) && file1.isDirectory() && file2.isDirectory()) {
                    if (Directory.lastModifiedDate(file2) > Directory.lastModifiedDate(file1)) {
                        Dsync.syncAndDelete(path1 + "/" + file1.getName(), path2 + "/" + file2.getName());
                    }
                    else {
                        Dsync.syncAndDelete(path2 + "/" + file2.getName(), path1 + "/" + file1.getName());
                    }
                }
            }
        }
    }

    public void syncLastState(String path1, String path2) throws IOException {

        System.out.println("Checking...");

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

        // System.out.println(dateAndNameList1);
        // System.out.println(dateAndNameList2);
        // System.out.println(lastState);

        if (!DateAndName.equalLists(dateAndNameList1, lastState) && !DateAndName.equalLists(dateAndNameList2, lastState)) {
            System.out.println("list1 != and list2 !=");
            firstSync(path1, path2);
            firstSync(path2, path1);
            setLastState(path1);
            return;
        }

        if (DateAndName.equalLists(dateAndNameList1, lastState) && !DateAndName.equalLists(dateAndNameList2, lastState)) {
            System.out.println("list1 = and list2 !=");
            syncAndDelete(path1, path2);
            setLastState(path2);
            return;
        }

        if (!DateAndName.equalLists(dateAndNameList1, lastState) && DateAndName.equalLists(dateAndNameList2, lastState)) {
            System.out.println("list1 != and list2 =");
            syncAndDelete(path2, path1);
            setLastState(path1);
            return;
        }

        checkAndSyncSubFolders(path1, path2);
    }

    public void syncDirectories() throws IOException, InterruptedException {
        firstSync(path1, path2);
        firstSync(path2, path1);

        setLastState(path1);

        Thread.sleep(5000);

        while(true) {
            syncLastState(path1, path2);

            Thread.sleep(5000);
        }
    }
        

    public static void main(String[] args) throws InterruptedException, IOException {
        Dsync dsync = new Dsync();
        dsync.setPath1(args[0]);
        dsync.setPath2(args[1]);

        dsync.syncDirectories();
    }
}
