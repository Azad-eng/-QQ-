package client.aidService;

import packet.Message;
import packet.MessageType;

import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * 客户端持有的所有线程的对应socket不停的等待并读取从服务端传送过来的message
 */
public class SToC_SocketThread extends Thread {
    private Socket socket;

    public SToC_SocketThread(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {  //user's aidService 可能会用到当前用户的socket
        return socket;
    }

    @Override
    public void run() {
        //因为Thread需要在后台和服务器通信，因此我们while循环
        while (true) {

            try {
                //System.out.println("客户端线程，等待读取从服务端传过来的message...");TEST
                ObjectInputStream oIS = new ObjectInputStream(socket.getInputStream());
                //如果没有收到，会阻塞 read是阻塞函数
                Message message = (Message) oIS.readObject();
                //根据服务端不同的消息类型，做出不同的业务处理
                if (message.getMessageType().equals(MessageType.message_return_online_friends)) {
                    //如果读取到的消息是服务端返回的在线列表，那么就取出想要的信息并显示
                    //规定在线用户列表形式： "xxxxxxxxx"  -->  "xxx xxx xxx" --> xxx
                    //                                                       xxx
                    //                                                       xxx
                    String[] friends = message.getContent().split(" ");
                    System.out.println("\n========在线用户列表========");
                    for (int i = 0; i < friends.length; i++) {
                        System.out.println("用户：" + friends[i] + "\t\t状态：在线中...");
                    }
                } else if (message.getMessageType().equals(MessageType.message_private_chat)) {
                    //接收服务端传过来的message对象,把消息内容显示出来
                    String privateChatContent = "";
                    System.out.println("\n--------聊----天----窗----口----栏--------");
                    privateChatContent = message.getSender() + "在" + message.getSendTime() + " 对你说: \n——" + message.getContent();
                    System.out.println(privateChatContent);
                    System.out.println("----------------------------------------");
                } else if (message.getMessageType().equals(MessageType.message_private_chat_return)) {
                    //接收服务端传过来的message对象,把消息内容显示出来
                    System.out.println("\n============FROM SERVER============");
                    System.out.println(message.getContent());
                } else if (message.getMessageType().equals(MessageType.message_public_chat)) {
                    //接收服务端传过来的message对象,把消息内容显示出来
                    String publicChatContent = "";
                    System.out.println("\n--------聊----天----窗----口----栏--------");
                    publicChatContent = message.getSender() + "在" + message.getSendTime() + " 对大家说: \n——" + message.getContent();
                    System.out.println(publicChatContent);
                    System.out.println("-----------------------------------------");
                } else if (message.getMessageType().equals(MessageType.message_file)) {
                    //接收服务端传过来的message对象,把消息内的文件存储到指定位置
                    System.out.println("\n============FROM SERVER============");
                    //检查message中的offlineMessages是否为空，如果不为空，就显示离线消息
                    if (!message.getOfflineMessages().isEmpty() && message.getOfflineMessages() != null) {
                        ArrayList<Message> offlineMessages = message.getOfflineMessages();
                        Iterator<Message> iterator = offlineMessages.iterator();
                        String privateChatContent_off = "";
                        while (iterator.hasNext()) {
                            Message offlineMessage = iterator.next();
                            privateChatContent_off = offlineMessage.getSender() + " send a file to your local file path:" + offlineMessage.getDesPath();
                            System.out.println(privateChatContent_off);
                            FileOutputStream fOS = new FileOutputStream(offlineMessage.getDesPath());
                            fOS.write(offlineMessage.getSendFile());
                            fOS.close();
                            System.out.println("(File to store in the specified location,please check.)");
                        }
                    } else {
                        System.out.println(message.getSender() + " send a file to your local file path: " + message.getDesPath());
                        FileOutputStream fOS = new FileOutputStream(message.getDesPath());
                        fOS.write(message.getSendFile());
                        fOS.close();
                        System.out.println("(File to store in the specified location,please check.)");
                    }
                    System.out.println("----------------------------------------");

                } else if (message.getMessageType().equals(MessageType.message_private_chat_offline)) {
                    //检查message中的offlineMessages是否为空，如果不为空，就显示离线消息
                    if (!message.getOfflineMessages().isEmpty() && message.getOfflineMessages() != null) {
                        ArrayList<Message> offlineMessages = message.getOfflineMessages();
                        Iterator<Message> iterator = offlineMessages.iterator();
                        String privateChatContent_off = "";
                        System.out.println("\n--------留---------言----------箱--------");
                        while (iterator.hasNext()) {
                            Message offlineMessage = iterator.next();
                            privateChatContent_off = offlineMessage.getSender() + "在" + offlineMessage.getSendTime() + " 对你说: \n——" + offlineMessage.getContent();
                            System.out.println(privateChatContent_off);
                        }
                        System.out.println("----------------------------------------");
                    }
                } else {
                    System.out.println("接收服务端其它的消息类型，暂不处理...");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
