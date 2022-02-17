package server.aidService;


import java.util.HashMap;
import java.util.Iterator;

/**
 * 该类用于管理与客户端通讯的线程
 */
public class manageCToS_socketThread {


    private static HashMap<String, receiveFromClient_socketThread> socketThreads = new HashMap<>();

    public static HashMap<String, receiveFromClient_socketThread> getSocketThreads() {
        return socketThreads;
    }

    //编写方法添加线程
    public static void addThread(String userId, receiveFromClient_socketThread socketThread) {
        socketThreads.put(userId, socketThread);
    }

    //编写得到指定账户的线程的方法
    public static receiveFromClient_socketThread getThread(String userId) {
        return socketThreads.get(userId);
    }

    //编写移除指定账户的线程的方法
    public static void removeThread(String userId) {
        socketThreads.remove(userId);
    }

    //编写方法遍历集合，取出所有的在线userId，即所有的key,userIds的内容形式为“key key key...”
    public static String getUserId() {
        Iterator<String> iterator = socketThreads.keySet().iterator();
        String userIds = "";
        while (iterator.hasNext()) {
            userIds += iterator.next() + " ";
        }
        return userIds;

    }


}
