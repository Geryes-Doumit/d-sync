package src.syncing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

public class Dsync {
    private String path1;
    private String path2;

    public void setPath1(String path) {
        this.path1 = path;
    }

    public void setPath2(String path) {
        this.path2 = path;
    }

    public void syncDirectories() throws IOException {
        File folder1 = new File(path1);
        File folder2 = new File(path2);

        List<File> list1 = Arrays.asList(folder1.listFiles());
        List<File> list2 = Arrays.asList(folder2.listFiles());

        for (File file2 : list2) {
            Boolean contains = false;
            for (File file1 : list1) {
                if(file2.getName().equals(file1.getName())) {
                    contains = true;
                    try{
                        if(file2.lastModified() > file1.lastModified()) {
                            Files.copy(file2.toPath(), Path.of(path1 + "/" + file2.getName()), StandardCopyOption.REPLACE_EXISTING);
                            System.out.println("Copied from path2 to path1 !");
                        }
                        else if(file2.lastModified() < file1.lastModified()) {
                            Files.copy(file1.toPath(), Path.of(path2 + "/" + file1.getName()), StandardCopyOption.REPLACE_EXISTING);
                            System.out.println("Copied from path1 to path2 !");
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
                    System.out.println("Created in path1 from path2 !");
                }
                catch(IOException e) {
                    // e.getCause();
                }
            }
        }

        for (File file1 : list1) {
            Boolean contains = false;
            for (File file2 : list2) {
                if(file1.getName().equals(file2.getName())) {
                    contains = true;
                    try{
                        if(file1.lastModified() > file2.lastModified()) {
                            Files.copy(file1.toPath(), Path.of(path2 + "/" + file1.getName()), StandardCopyOption.REPLACE_EXISTING);
                            System.out.println("Copied from path1 to path2 !");
                        }
                        else if (file1.lastModified() < file2.lastModified()) {
                            Files.copy(file2.toPath(), Path.of(path1 + "/" + file2.getName()), StandardCopyOption.REPLACE_EXISTING);
                            System.out.println("Copied from path2 to path1 !");
                        }
                    }
                    catch(IOException e) {
                        // e.getCause();
                    }
                }
            }
            if (!contains) {
                try{
                    Files.copy(file1.toPath(), Path.of(path2 + "/" + file1.getName()), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Created in path2 from path1 !");
                }
                catch(IOException e) {
                    // e.getCause();
                }
            }
        }
    }

    public static void main(String[] args) {
        Dsync dsync = new Dsync();
        dsync.setPath1(args[0]);
        dsync.setPath2(args[1]);

        while(true) {
            try {
                dsync.syncDirectories();
            } catch (IOException e) {
                // e.printStackTrace();
            }
            try {
                Thread.sleep(5000);
            }
            catch(InterruptedException e) {
                // e.getCause();
            }
        }
    }
}
