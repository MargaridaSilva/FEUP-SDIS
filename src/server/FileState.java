package server;

import java.io.Serializable;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

class FileState implements Serializable{
    private static final long serialVersionUID = 1L;

    public static ConcurrentHashMap<ChunkId, HashSet<Integer>> concurrentHashMap = new ConcurrentHashMap<>();
}