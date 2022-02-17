package client.aidService;

import packet.Message;
import packet.MessageType;

import java.io.*;
import java.net.Socket;

public class FileService {
    //编写方法发送文件到服务端
    public void sendFile(String sender, String getter, String srcPath, String desPath) {
        /*
        IO流，从本地文件（字节数组）路径读取文件
        使用 read(byte[] b) 读取文件，提高效率
        new byte[8]---一次读取8个字节
        new byte[(int)new File(srcPath).length()]一次读取文件所有的字节
        */
        byte[] bytes = new byte[(int) new File(srcPath).length()];
        FileInputStream fIS = null;
        try {
            fIS = new FileInputStream(srcPath);
            fIS.read(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fIS != null) {
                try {
                    fIS.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /*
        socket，将文件（字节数组）传入到服务端
        获取到发送者的socket
        将字节数组封装到message中
        通过创建对象流传输Message类的对象
        */
        Socket socket = ManageSToC_SocketThread.getThread(sender).getSocket();
        Message message = new Message();
        message.setMessageType(MessageType.message_file);
        message.setSender(sender);
        message.setGetter(getter);
        message.setSendFile(bytes);
        message.setDesPath(desPath);
        try {
            if (getter.equals(sender)) {
                System.out.println("Cant's sending file to yourself!");
                return;
            }
            ObjectOutputStream oOS = new ObjectOutputStream(socket.getOutputStream());
            oOS.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
