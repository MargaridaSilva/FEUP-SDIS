package utilities;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class FileSystem {

    private HashMap<String, String> filepath_id_map;
    private int server_id;
    String server_path;
    String backup_path;
    String restored_path;

    public FileSystem(int server_id) {
        this.server_id = server_id;
        this.server_path = Utilities.LOCALDISK_DIR + this.server_id + "/";
        this.backup_path = this.server_path + "backup/";
        this.restored_path = this.server_path + "restored/";

        this.filepath_id_map = new HashMap<>();
    }

    public void put(String key, String value) {
        filepath_id_map.put(key, value);
    }

    public String get(String key) {
        return filepath_id_map.get(key);
    }

    public void createPeerFileStructure() {

        try {
            Files.createDirectories(Paths.get(this.server_path));
            Files.createDirectories(Paths.get(this.backup_path));
            Files.createDirectories(Paths.get(this.restored_path));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createFileDir(String file_id) {

        String new_path = this.backup_path + file_id;
        if(Files.notExists(Paths.get(new_path))){
            try {
                Files.createDirectories(Paths.get(new_path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public String getBackupPath() {
        return backup_path;
    }

    public String getRestoredPath() {
        return restored_path;
    }

    public void createChunk(String file_id, int chunk_no, byte[] bytes, int size) {
        createFileDir(file_id);

        FileOutputStream fos;
        try {
            String filepath = this.backup_path + file_id + "/" + chunk_no;
            fos = new FileOutputStream(filepath);
            fos.write(bytes, 0, size);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } 
	}


}