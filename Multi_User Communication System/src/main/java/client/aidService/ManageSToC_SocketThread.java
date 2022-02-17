package client.aidService;


import java.util.HashMap;

/**
 * 该类用于管理与服务端通讯的线程
 */
public class ManageSToC_SocketThread {

    private static HashMap<String, SToC_SocketThread> socketThreads =new HashMap<String, SToC_SocketThread>();
    //编写添加线程的方法
    public static void addThread(String userId, SToC_SocketThread socketThread){
        socketThreads.put(userId,socketThread);
    }
    //编写得到指定账户的线程的方法
    public static SToC_SocketThread getThread(String userId){
        return socketThreads.get(userId);
    }
}
