package server.aidService;

import packet.Message;
import packet.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 创建一个线程，向所有在线用户推送新闻
 * 服务端持有的该线程对应的所有socket不停的向客户端推送message
 */
public class sendToClient_socketThreadWithNews extends Thread {

    @Override
    public void run() {
        while (true) {
            System.out.println("请输入要推送的内容：(输入exit表示退出推送服务)");
            String news = Utility.readString(120);
            if (news.equals("exit")) {
                break;  //退出循环->退出run->退出该线程
            }
            //将news封装到message中传给所有在线用户（获取所有在线用户的socket）
            Message message = new Message();
            HashMap<String, receiveFromClient_socketThread> socketThreads = manageCToS_socketThread.getSocketThreads();
            Iterator<String> iterator = socketThreads.keySet().iterator();
            while (iterator.hasNext()) {
                String userIdOnline = iterator.next();
                Socket socket = manageCToS_socketThread.getThread(userIdOnline).getSocket();
                message.setGetter(userIdOnline);
                message.setSender("Server");
                message.setContent(news);
                message.setSendTime(new SimpleDateFormat(" yyyy-MM-dd hh:mm:ss").format(new Date()));
                message.setMessageType(MessageType.message_public_chat);
                try {
                    ObjectOutputStream oOS = new ObjectOutputStream(socket.getOutputStream());
                    oOS.writeObject(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("退出推送新闻的服务...");
    }
}
