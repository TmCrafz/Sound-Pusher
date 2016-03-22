package timsterzel.de.soundpusher;

import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

/**
 * Created by tim on 16.03.16.
 */
public class FileHandler {

    private static final String TAG = FileHandler.class.getSimpleName();

    //private static final String SOUND_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundPusher";
    //private static final String SOUND_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundPusher";

    private static final String SOUND_PATH = "/Sounds";
    private static final String SOUND_PATH_TMP = "/SoundsTmp";


    private final static char[] LEGAL_CHARS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E',
            'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
            'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5' , '6', '7', '8', '9' };


    //public static String getSoundPath() { return SOUND_PATH; }
    public static String getInternalSoundPath(Context context) { return context.getFilesDir() + SOUND_PATH; }

    //public static String getTmpSoundPath() { return  SOUND_PATH_TMP; }
    public static String getInternalTmpSoundPath(Context context) { return  context.getFilesDir() + SOUND_PATH_TMP; }



    public static void createSoundFilePathIfNotExists(Context context) {
        File file = new File(getInternalSoundPath(context));
        if (!file.exists()) {
            file.mkdir();
        }
        File fileTmp = new File(getInternalTmpSoundPath(context));
        if (!fileTmp.exists()) {
            fileTmp.mkdir();
        }
    }

    /*
    public static String moveFileToSoundPath(File file, String name) {
        if (!file.exists()) {
            return null;
        }
        File fileNewPath = new File(SOUND_PATH + "/" + name);
        copyFile(file, fileNewPath);
        return fileNewPath.getAbsolutePath();
    }*/

    public static File moveFileTo(File file, String path) {
        if (!file.exists()) {
            return null;
        }
        File fileNewPath = new File(path);
        copyFile(file, fileNewPath);
        return fileNewPath;
    }

    public static void delete(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File fileTmp : files) {
                delete(fileTmp);
            }
        }
        else {
            file.delete();
        }
    }

    public static void delete(String path) {
        File file = new File(path);
        if (file.exists()) {
            delete(new File(path));
        }
    }

    private static boolean copyFile(File src, File dst)  {
        InputStream in = null;
        OutputStream out = null;
        boolean success = true;
        try {
            in = new FileInputStream(src);
            out = new FileOutputStream(dst);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            success = false;
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    public static String getFileExtension(String fileName) {
        String fileExtension = "";
        // Only split and get file extension if there is one
        if (fileName.lastIndexOf('.') >= 0) {
            // Get file extension
            fileExtension = fileName.substring(fileName.lastIndexOf('.'), fileName.length());
        }
        return fileExtension;
    }

    public static String getWithoutFileExtension(String fileName) {
        // Only remove file extension if there is one
        if (fileName.lastIndexOf('.') >= 0) {
            // remove file extension
            return fileName.substring(0, fileName.lastIndexOf('.'));
        }
        return fileName;
    }

    public static String createLegalFilename(Context context, String str) {
        // Get file extension so we can add it later
        String fileExtension = getFileExtension(str);
        // remove file extension
        str = getWithoutFileExtension(str);

        str = removeIllegalChars(str);
        // If filename is to short add random strings
        if (str.length() < 3) {
            str += getRandomFileNameExtension();
        }
        // Change name as long we have a name that does not already exist
        while(doesFileNameAlreadyExists(getInternalSoundPath(context), str)) {
            // remove last 3 chars (So the file name does not get to large)
            str = str.substring(str.length() - 3, str.length());
            // add 6 new chars
            str += getRandomFileNameExtension();
        }
        // add file extension to filename
        str += fileExtension;
        return str;
    }

    // Generate  a String of 3 random legal chars
    private static String getRandomFileNameExtension() {
        StringBuilder strBuilder = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i != 6; i++) {
            int randNum = rand.nextInt(LEGAL_CHARS.length);
            strBuilder.append(LEGAL_CHARS[randNum]);
        }
        return strBuilder.toString();
    }

    private static String removeIllegalChars(String str) {
        StringBuilder strBuilder = new StringBuilder();
        // Convert legal chars to string so we can easy check if there is a specific char
        String legal = new String(LEGAL_CHARS);
        for (char c : str.toCharArray()) {
            // If index of is bigger or equals 0 the char is in string
            if (legal.indexOf(c) >= 0) {
                strBuilder.append(c);
            }
        }
        return strBuilder.toString();
    }

    private static boolean doesFileNameAlreadyExists(String path, String name) {
        File soundDirectory = new File(path);
        // Remove file extension from file if there is one
        name = name.lastIndexOf('.') >= 0 ? name.substring(0, name.lastIndexOf('.')) : name;

        for (File file : soundDirectory.listFiles()) {
            String fileName = file.getName();
            fileName = fileName.lastIndexOf('.') >= 0 ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
            if (name.equals(fileName)) {
                return true;
            }
        }
        return false;
    }

}
