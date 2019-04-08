package utilities;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utilities {
    public static String mc_addr = "224.0.0.1";
    public static int mc_port = 8000;
    public static String mdb_addr = "224.0.0.2";
    public static int mdb_port = 8000;
    public static String mdr_addr = "224.0.0.3";
    public static int mdr_port = 8000;
    public static int CHUNK_SIZE = 64 * 1000;
    public static final String FILES_DIR = "../files/";
    public static final String LOCALDISK_DIR = "../localdisk/";
    public static int UDP_MAX = 64 * 1024 - 20 - 8;


    private static String bytesToHex(byte[] hashInBytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


    public static String generateIdentifier(String path) throws IOException, NoSuchAlgorithmException {
        Path file = Paths.get(path);
        BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
       
        String text = file.getFileName().toString() +  attr.lastModifiedTime();

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));

        return bytesToHex(hash);
    }

    public static int indexOf(byte[] array, byte valueToFind) {
        if (array == null) {
            return -1;
        }
        for (int i = 0; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

}
