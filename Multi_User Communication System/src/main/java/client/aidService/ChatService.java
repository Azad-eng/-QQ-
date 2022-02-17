package client.aidService;

import packet.Message;
import packet.MessageType;
import packet.User;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatService {
    //因为我们可能在其他地方用使用user信息, 因此作出成员属性
    private User user = new User();
    //因为Socket在其它地方也可能使用，因此作出属性
    Socket socket;

    //编写方法完成获取在线用户列表
    public void getList(String userId) {
        //构建user和message
        user.setUserId(userId);
        Message message = new Message();
        //设置message type and sender
        message.setMessageType(MessageType.message_get_online_friends);
        message.setSender(userId);
        //得到当前用户的socket，发送message --消息类型：请求返回在线用户
        try {
            //step1 从管理线程的集合中，通过userId, 得到这个线程对象
            SToC_SocketThread sToC_socketThread = ManageSToC_SocketThread.getThread(userId);//在线程集合中得到当前用户的线程（持有socket）
            //step2 通过这个线程对象得到关联的socket
            Socket socket = sToC_socketThread.getSocket();
            //step3 通过得到关联的socket得到对应的 ObjectOutputStream对象
            ObjectOutputStream oOS = new ObjectOutputStream(socket.getOutputStream());
            oOS.writeObject(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //编写方法通知服务端需要发送private message
    public void privateChat(String sender, String getter, String content) {
        Message message = new Message();
        message.setMessageType(MessageType.message_private_chat);
        message.setSender(sender);
        message.setGetter(getter);
        message.setContent(content);
        message.setSendTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        socket = ManageSToC_SocketThread.getThread(sender).getSocket();
        try {
            if (getter.equals(sender)) {
                System.out.println("Cant's messaging yourself!");
                return;
            }
            ObjectOutputStream oOS = new ObjectOutputStream(socket.getOutputStream());
            oOS.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //编写方法通知服务端需要发送private message
    public void publicChat(String sender, String content) {
        Message message = new Message();
        message.setMessageType(MessageType.message_public_chat);
        message.setSender(sender);
        message.setContent(content);
        message.setSendTime(new SimpleDateFormat(" yyyy-MM-dd hh:mm:ss").format(new Date()));
        socket = ManageSToC_SocketThread.getThread(sender).getSocket();
        try {
            ObjectOutputStream oOS = new ObjectOutputStream(socket.getOutputStream());
            oOS.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
