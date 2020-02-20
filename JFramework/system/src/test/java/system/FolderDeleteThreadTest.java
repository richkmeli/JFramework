package system;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertFalse;

public class FolderDeleteThreadTest {

    @Test
    public void run() {
        File testfolder = new File("testfolder");
        File testfile = new File("testfolder/testfile.txt");

        testfolder.mkdir();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(testfile);

            fos.write('o');
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FolderDeleteThread folderDeleteThread = new FolderDeleteThread(testfolder);
        folderDeleteThread.start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertFalse(testfolder.exists());
    }
}