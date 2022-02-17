package server.aidService;

import packet.Message;
import packet.MessageType;
import server.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务端持有的该线程对应的所有socket不停的等待并读取从客户端传送过来的message
 */
public class receiveFromClient_socketThread extends Thread {
    private Socket socket;
    private String userId;
    private static ArrayList<Message> messages = new ArrayList<>();
    private static ConcurrentHashMap<String, ArrayList<Message>> CHM = new ConcurrentHashMap<>();

    public static ArrayList<Message> getMessages() {
        return messages;
    }

    public static ConcurrentHashMap<String, ArrayList<Message>> getCHM() {
        return CHM;
    }

    public receiveFromClient_socketThread(String userId, Socket socket) {
        this.userId = userId;
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        System.out.println("User:" + userId + "上线ing...");
        while (true) {
            try {
                ObjectInputStream oIS = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) oIS.readObject();
                //System.out.println("读取到客户端的message..."); TEST
                //根据客户端发送的不同message类型，做出不同的业务处理
                if (message.getMessageType().equals(MessageType.message_get_online_friends)) {
                    //如果收到的是客户端请求得到在线用户列表的message，那么就返回message（带有在线用户列表信息）
                    //思考：从哪里得到所有的在线userID？
                    System.out.println(message.getSender() + "Request: TO GET ONLINE USERS");
                    //调用方法getUserId()得到所有的userId
                    String userIds = manageCToS_socketThread.getUserId();
                    //构建一个Message 对象，返回给客户端
                    Message reMessage = new Message();
                    reMessage.setContent(userIds);
                    reMessage.setGetter(message.getSender());
                    reMessage.setMessageType(MessageType.message_return_online_friends);
                    //返回给客户端
                    ObjectOutputStream oOS = new ObjectOutputStream(socket.getOutputStream());
                    oOS.writeObject(reMessage);
                } else if (message.getMessageType().equals(MessageType.message_notice_exit)) {
                    //如果收到的是客户端请求退出的message，回复可以退出的message
                    System.out.println("User:" + message.getSender() + "下线ing...");
                    //关闭相关流和移除相关线程
                    manageCToS_socketThread.removeThread(message.getSender());
                    oIS.close();
                    socket.close();
                    break; //退出该线程
                } else if (message.getMessageType().equals(MessageType.message_private_chat)) {
                    /*
                    如果收到的是客户端请求私聊的message
                    */
                    System.out.println("FROM " + message.getSender() + " TO " + message.getGetter() + "\nRequest: " + message.getContent());
                    //分析message的getter是否exist，如果在，就看用户在不在线，如果不在就提示用户不存在
                    if (!server.ifExist(message.getGetter())) {
                        String content = message.getGetter() + " IS NOT EXIST,CAN'T CHAT WITH HIM(HER)!";
                        Message reMessage = new Message();
                        reMessage.setContent(content);
                        reMessage.setGetter(message.getSender());
                        reMessage.setMessageType(MessageType.message_private_chat_return);
                        ObjectOutputStream oOS = new ObjectOutputStream(socket.getOutputStream());
                        oOS.writeObject(reMessage);
                        /*
                        分析message的getter是否online，如果在线就转发，如果不在线，就把offlineMessage放入到ArrayList集合中集中管理
                        把offlineMessage及offlineMessage对应的getter一起放到ConcurrentHashmap集合中,
                        因为一个用户收到的不只一条离线消息，所以一个getter对应的不是一个message,而是一个ArrayList<message>
                        */
                    } else if (manageCToS_socketThread.getThread(message.getGetter()) == null) {
                        //把离线消息放入到ArrayList集合中
                        messages.add(message);
                        //把离线消息接收者-离线消息放入到ConcurrentHashmap集合中
                        CHM.put(message.getGetter(), messages);
                    } else {
                        //如果存在getter，把收到的Message 对象，转发给getter
                        OutputStream outputStream =
                                manageCToS_socketThread.getThread(message.getGetter()).socket.getOutputStream();
                        ObjectOutputStream oOS = new ObjectOutputStream(outputStream);
                        oOS.writeObject(message);
                    }
                } else if (message.getMessageType().equals(MessageType.message_public_chat)) {
                    /*
                    如果收到的是客户端请求群聊的message
                    */
                    System.out.println("FROM " + message.getSender() + " TO ALL USERS" + "\nRequest: " + message.getContent());
                    HashMap<String, receiveFromClient_socketThread> socketThreads = manageCToS_socketThread.getSocketThreads();
                    Iterator<String> iterator = socketThreads.keySet().iterator();
                    while (iterator.hasNext()) {
                        String userId = iterator.next();
                        //排除信息发送者的socket
                        if (!userId.equals(message.getSender())) {
                            ObjectOutputStream oOS =
                                    new ObjectOutputStream(manageCToS_socketThread.getThread(userId).socket.getOutputStream());
                            oOS.writeObject(message);
                        }

                    }
                } else if (message.getMessageType().equals(MessageType.message_file)) {
                    /*
                    如果收到的是客户端请求私发文件的message
                    */
                    System.out.println("FROM " + message.getSender() + " TO " + message.getGetter() + "\nRequest: TO SEND A FILE");
                    //分析message的getter是否exist，如果在，就看用户在不在线，如果不在就提示用户不存在
                    if (!server.ifExist(message.getGetter())) {
                        String content = message.getGetter() + " IS NOT EXIST,CAN'T SEND FILE TO HIM(HER)!";
                        Message reMessage = new Message();
                        reMessage.setContent(content);
                        reMessage.setGetter(message.getSender());
                        reMessage.setMessageType(MessageType.message_private_chat_return);
                        ObjectOutputStream oOS = new ObjectOutputStream(socket.getOutputStream());
                        oOS.writeObject(reMessage);
                        //分析message的getter是否online，如果在线就转发，如果不在就返回message提示用户不在线
                    } else if (manageCToS_socketThread.getThread(message.getGetter()) == null) {
                        //把离线文件消息放入到ArrayList集合中
                        messages.add(message);
                        //把离线文件消息接收者-离线文件消息放入到ConcurrentHashmap集合中
                        CHM.put(message.getGetter(), messages);
                    } else {
                        //如果存在在线的getter，把收到的Message 对象，转发给getter
                        OutputStream outputStream =
                                manageCToS_socketThread.getThread(message.getGetter()).socket.getOutputStream();
                        ObjectOutputStream oOS = new ObjectOutputStream(outputStream);
                        oOS.writeObject(message);
                    }
                } else {
                    System.out.println("接收客户端其它的message类型，暂不处理...");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
