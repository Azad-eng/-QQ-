package server.aidService;

import packet.Message;
import packet.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class sendTOClient_socketThreadWithOffContent extends Thread {
    private String userId;
    private ArrayList<Message> messages;
    private String messageType;
    ArrayList<Message> offlineContents;

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public void run() {
        if (messages.get(0).getMessageType().equals(MessageType.message_private_chat)) {
            //将离线消息集合封装到message中传给上线用户（获取上线用户的socket）
            Socket socket = manageCToS_socketThread.getThread(userId).getSocket();
            Message message = new Message();
            offlineContents = new ArrayList<>();
            Iterator<Message> iterator = messages.iterator();
            while (iterator.hasNext()) {
                Message messageOffline = iterator.next();
                if(messageOffline.getGetter().equals(userId)){
                    offlineContents.add(messageOffline);
                }
            }
            message.setOfflineMessages(offlineContents);
            message.setMessageType(MessageType.message_private_chat_offline);
            try {
                ObjectOutputStream oOS = new ObjectOutputStream(socket.getOutputStream());
                oOS.writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (messages.get(0).getMessageType().equals(MessageType.message_file)) {
            //将离线文件消息集合封装到message中传给上线用户（获取上线用户的socket）
            Socket socket = manageCToS_socketThread.getThread(userId).getSocket();
            Message message = new Message();
            offlineContents = new ArrayList<>();
            Iterator<Message> iterator = messages.iterator();
            while (iterator.hasNext()) {
                Message messageOffline = iterator.next();
                if(messageOffline.getGetter().equals(userId)){
                    offlineContents.add(messageOffline);
                }
            }
            message.setOfflineMessages(offlineContents);
            message.setMessageType(MessageType.message_file);
            try {
                ObjectOutputStream oOS = new ObjectOutputStream(socket.getOutputStream());
                oOS.writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
