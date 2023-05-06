package src.syncing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

public class Directory {
    // Copies a directory and all its content to the destination
    public static String copyDirectory(String src, String dest) throws IOException {
        Files.createDirectory(Path.of(dest));
        List<File> subFolderList = Arrays.asList(new File(src).listFiles());
        for (File file : subFolderList) {
            if (file.isFile()) {
                Files.copy(file.toPath(), Path.of(dest + "/" + file.getName()), StandardCopyOption.REPLACE_EXISTING);
                new File(dest + "/" + file.getName()).setLastModified(file.lastModified());
                // System.out.println("Created in " + src + " from " + dest + " !");
            }
            else if(file.isDirectory()) {
                copyDirectory(src + "/" + file.getName(), dest + "/" + file.getName());
            }
        }
        // System.out.println("Copied the " + src + " directory in" + dest);

        return "Synced the subdirectories.";
    }

    // Deletes a directory and all its content
    public static String deleteDirectory(String src) throws IOException {
        List<File> subFolderList = Arrays.asList(new File(src).listFiles());
        for (File file : subFolderList) {
            if (file.isFile()) {
                // System.out.println("Deleting" + file.getName() + "in " + src + " ...");
                file.delete();
            }
            else if(file.isDirectory()) {
                deleteDirectory(src + "/" + file.getName());
            }
        }
        new File(src).delete();
        // System.out.println("Deleted the " + src + " directory and all its content.");

        return "Synced the subdirectories.";
    }

    // returns the date of the latest file in a directory
    public static Long lastModifiedDate(File file) {
        if (file.isFile()) {
            return file.lastModified();
        }
        else if (file.isDirectory()) {
            Long lastModified = file.lastModified();

            List<File> list = Arrays.asList(file.listFiles());
            for (File doc : list) {
                if (doc.isFile()) {
                    if (doc.lastModified() > lastModified) {
                        lastModified = doc.lastModified();
                    }
                }
                else if (doc.isDirectory()) {
                    Long temp = lastModifiedDate(doc);
                    if (temp > lastModified) {
                        lastModified = temp;
                    }
                }
            }
            return lastModified;
        }

        return file.lastModified();
    }
}
