package server;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import state.ChunkInfo;

public class Algorithm{

    public static List<ChunkInfo> chunks_to_remove(PriorityQueue<ChunkInfo> min_priorityQueue, int max_space){

        int size = max_space;

        if(min_priorityQueue == null || min_priorityQueue.isEmpty()){
            return new LinkedList<ChunkInfo>();
        }

        while(!min_priorityQueue.isEmpty() && (size - min_priorityQueue.peek().getSize() >= 0)){
            size -= min_priorityQueue.poll().getSize();
        }       

        List<ChunkInfo> list = new LinkedList<>();
        while(!min_priorityQueue.isEmpty()){
            list.add(min_priorityQueue.poll());
        }

        return list;
    }


}