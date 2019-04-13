package utilities;

import java.io.File;
import java.io.FileOutputStream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;


public class FileSystem {
    
    private static FileSystem fs = null;

    public static FileSystem init(int server_id) { 
        fs = new FileSystem(server_id); 
        return fs; 
    } 

    public static FileSystem getInstance() { 
        if(fs == null){
            //throw error
        }
        return fs; 
    } 

    private int server_id;
    String server_path;
    String backup_path;
    String restored_path;

    public FileSystem(int server_id) {
        this.server_id = server_id;
        this.server_path = Utilities.LOCALDISK_DIR + this.server_id + "/";
        this.backup_path = this.server_path + "backup/";
        this.restored_path = this.server_path + "restored/";
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

    public void create_file_dir(String file_id, String path) {

        String new_path = path + file_id;
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


    public boolean contain_chunk(String file_id, int chunk_no){
        Path file = Paths.get(this.backup_path + file_id + "/" + chunk_no);
        return Files.exists(file);        
    }

    public byte[] read_chunk(String file_id, int chunk_no){
        Path file = Paths.get(this.backup_path + file_id + "/" + chunk_no);
        if(Files.exists(file)){
            try {
                byte[] file_content;
                file_content = Files.readAllBytes(file);
                return file_content;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
        
    }


    public void save_chunk_backup(String file_id, int chunk_no, byte[] bytes, int size){
        save_chunk(file_id, chunk_no, bytes, size, this.backup_path);
    }

    public void save_chunk_restore(String file_id, int chunk_no, byte[] bytes, int size){
        save_chunk(file_id, chunk_no, bytes, size, this.restored_path);
    }

    public void save_chunk(String file_id, int chunk_no, byte[] bytes, int size, String path) {
        create_file_dir(file_id, path);

        FileOutputStream fos;
        try {
            String filepath = path + file_id + "/" + chunk_no;
            fos = new FileOutputStream(filepath);
            fos.write(bytes, 0, size);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } 
	}
    
    public void delete_chunk(String file_id, int chunk_no) {
        String file_path = this.backup_path + file_id + "/" + chunk_no;
        
    	Path chunk = Paths.get(file_path);

        if (Files.exists(chunk) && Files.isRegularFile(chunk))
			try {
				Files.delete(chunk);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
    
    public void delete_file(String file_id) {

        String file_path = this.backup_path + file_id;
        
        Path directory = Paths.get(file_path);
        if (Files.exists(directory) && Files.isDirectory(directory)){
            try {
                Files.walk(directory)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


}