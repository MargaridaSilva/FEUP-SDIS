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

    public static final int TCP_PORT = 8000;
    
    public static int UDP_MAX = 64 * 1024 - 20 - 8;
    public static int CHUNK_SIZE = 64 * 1000;

    public static final String STOCK_VERSION = "1.0";
    public static final String ENH_VERSION = "2.0";

    public static final String FILES_DIR = "../files/";
    public static final String LOCALDISK_DIR = "../localdisk/";
    public static final String SERVER_BACKUP_DIR = "../server_backup/";


    public static int byte_to_kbyte(int size_in_bytes){
        return size_in_bytes / 1000;
    }

    public static int kbyte_to_byte(int size_in_kbytes){
        return size_in_kbytes * 1000;
    }

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

    public static int indexSeq(byte[] array, byte[] sequence) {
        if (array == null) {
            return -1;
        }
        int i = 0;
        int j = 0;

        for (i = 0; i < array.length; i++) {

            if(j == sequence.length){
                return i - sequence.length;
            }

            if (sequence[j] == array[i]) {
                j++;
            }
            else{
                j = 0;
            }
        }

        if(j == sequence.length){
            return i - sequence.length;
        }else{
            return -1;
        }
    }

}
