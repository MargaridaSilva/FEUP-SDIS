package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import server.ServerInfo;
import state.ServerBackup;
import state.ServerState;

public class FileSystem {

    private static FileSystem fs = null;

    public static FileSystem init(int server_id) {
        fs = new FileSystem(server_id);
        return fs;
    }

    public static FileSystem getInstance() {
        if (fs == null) {
            // throw error
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
        if (Files.notExists(Paths.get(new_path))) {
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

    public boolean contain_chunk(String file_id, int chunk_no) {
        Path file = Paths.get(this.backup_path + file_id + "/" + chunk_no);
        return Files.exists(file);
    }


    public byte[] read_chunk_backup(String file_id, int chunk_no){
        return read_chunk(file_id, chunk_no, this.backup_path);
    }

    public byte[] read_chunk_restore(String file_id, int chunk_no){
        return read_chunk(file_id, chunk_no, this.restored_path);
    }

    public byte[] read_chunk(String file_id, int chunk_no, String path) {
        Path file = Paths.get(path + file_id + "/" + chunk_no);
        if (Files.exists(file)) {
            try {
                byte[] file_content;
                file_content = Files.readAllBytes(file);
                return file_content;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else System.out.println("WARNING: Access to illegal chunk no." + chunk_no + " of file " + file_id);
        return null;
        
    }

    public void join_chunk(String file_id, String filename) {
        Path directory = Paths.get(this.restored_path + file_id);
        String output_file = this.restored_path + filename;

        try {
            FileOutputStream fos = new FileOutputStream(output_file);

            if (Files.exists(directory) && Files.isDirectory(directory)){
                Files.walk(directory)
                    .filter(Files::isRegularFile)
                    .sorted(Comparator.comparingInt(this::pathToInt))
                    .forEach(p -> {
                            try {
                                fos.write(Files.readAllBytes(p));
                                Files.delete(p);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    });
                Files.delete(directory);
                fos.close();
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    private int pathToInt(final Path path) {
        return Integer.parseInt(path.getFileName()
                .toString()
        );
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
                e.printStackTrace();
            }
        }
    }
    
    public void save_server_state(ServerState state) {
    
	   try {
           FileOutputStream fos = new FileOutputStream(Utilities.SERVER_BACKUP_DIR+ServerInfo.getInstance().server_id+"/log");
           ObjectOutputStream oos = new ObjectOutputStream(fos);
           state.writeExternal(oos);
           oos.close();
           fos.close();
       } catch (IOException e) {
           e.printStackTrace();
       } 
    }
    

	public void createServerBackupStructure() {
		Path path =  Paths.get(ServerBackup.path);
		if (Files.exists(path) && Files.isDirectory(path)) {
			load_server_state();
		} else
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public void load_server_state() {
    	ServerState state = new ServerState();
    	System.out.println(ServerBackup.path);
        
 	   try {
            FileInputStream fis = new FileInputStream(ServerBackup.path+"/log");
            ObjectInputStream ois = new ObjectInputStream(fis);
            state.readExternal(ois);
            ois.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 
     }
}