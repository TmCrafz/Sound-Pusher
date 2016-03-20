package timsterzel.de.soundpusher;

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

    private static final String SOUND_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RecordSounds";


    private final static char[] LEGAL_CHARS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E',
            'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
            'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5' , '6', '7', '8', '9' };


    public static void createSoundFilePathIfNotExists() {
        File file = new File(SOUND_PATH);
        if (!file.exists()) {
            file.mkdir();
        }
    }


    public static String moveFileToSoundPath(File file, String name) {
        if (!file.exists()) {
            return null;
        }
        File fileNewPath = new File(SOUND_PATH + "/" + name);
        Log.d(TAG, "SoundPath fileNewPath:" + fileNewPath.getAbsolutePath());

        boolean copied = copyFile(file, fileNewPath);

        Log.d(TAG, "SoundPath copied: " + copied);

        return fileNewPath.getAbsolutePath();
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
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    public static String createLegalFilename(String str) {
        String fileExtension = "";
        // handle file extension if there is one
        if (str.lastIndexOf('.') >= 0) {
            // Get file extension so we can add it later
            fileExtension = str.substring(str.lastIndexOf('.'), str.length());
            // remove file extension
            str = str.substring(0, str.lastIndexOf('.'));
        }

        str = removeIllegalChars(str);
        // If filename is to short add random strings
        if (str.length() < 3) {
            str += getRandomFileNameExtension();
        }
        // Change name as long we have a name that does not already exist
        while(doesFileNameAlreadyExists(str)) {
            // remove last 3 chars
            str = str.substring(str.length() - 3, str.length());
            // add 3 new chars
            str += getRandomFileNameExtension();
        }
        // add file extension to filename
        str += fileExtension;
        Log.d(TAG, "Creates legal filename: " + str);
        return str;
    }

    // Generate  a String of 3 random legal chars
    private static String getRandomFileNameExtension() {
        StringBuilder strBuilder = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i != 3; i++) {
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

    private static boolean doesFileNameAlreadyExists(String name) {
        File soundDirectory = new File(SOUND_PATH);
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
