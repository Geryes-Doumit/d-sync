package src.Syncing;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;

public class Dsync {
    private String path1;
    private String path2;

    public void setPath1(String path) {
        this.path1 = path;
    }

    public void setPath2(String path) {
        this.path2 = path;
    }

    public void syncDirectories() {
        File folder1 = new File(path1);
        File folder2 = new File(path2);

        List<File> list1 = Arrays.asList(folder1.listFiles());
        List<File> list2 = Arrays.asList(folder2.listFiles());

        for (File file : list1) {
            if (list2.contains(file)) {
                
            }
        }
    }
}
