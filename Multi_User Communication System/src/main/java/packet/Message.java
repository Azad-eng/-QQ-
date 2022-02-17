package packet;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    private String messageType; //消息类型
    private String content; //消息内容
    private String sender; //消息发送者
    private String getter; //消息接收者
    private String sendTime; //消息发送时间
    private String desPath;//文件抵达路径
    private byte[] sendFile;//消息封装的文件（字节数组）
    private ArrayList<Message> offlineMessages; //把离线消息集合封装到Message中

    public ArrayList<Message> getOfflineMessages() {
        return offlineMessages;
    }

    public void setOfflineMessages(ArrayList<Message> offlineMessages) {
        this.offlineMessages = offlineMessages;
    }

    public String getDesPath() {
        return desPath;
    }

    public void setDesPath(String desPath) {
        this.desPath = desPath;
    }

    public byte[] getSendFile() {
        return sendFile;
    }

    public void setSendFile(byte[] sendFile) {
        this.sendFile = sendFile;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getGetter() {
        return getter;
    }

    public void setGetter(String getter) {
        this.getter = getter;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}


