package client.aidService;

import packet.MessageType;
import packet.Message;
import packet.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * 该类完成用户登录验证/用户注册/获取在线用户列表等功能
 */

public class FunctionService {
    //因为我们可能在其他地方用使用user信息, 因此作出成员属性
    private User user = new User();
    //因为Socket在其它地方也可能使用，因此作出属性
    Socket socket;

    //编写方法完成用户登录验证
    public boolean checkUserInfo(String userId, String pwd) {
        //构建user
        user.setUserId(userId);
        user.setPwd(pwd);
        user.setType("login");
        boolean b = false;
        //把user对象传到服务端
        try {
            socket = new Socket(InetAddress.getLocalHost(), 9999);
            ObjectOutputStream oOS = new ObjectOutputStream(socket.getOutputStream());
            oOS.writeObject(user);
            //接收服务端传过来的message对象
            ObjectInputStream oIS = new ObjectInputStream(socket.getInputStream());
            Message response = (Message) oIS.readObject();
            if (response.getMessageType().equals(MessageType.message_login_succeed)) {
                //如果成功，就创建并启动该socket的线程类(接收服务端传送的message)，重写run方法让该线程一直run，保持同服务端之间的消息传递
                SToC_SocketThread sToC_socketThread = new SToC_SocketThread(socket);
                sToC_socketThread.start();
                //把该线程放入到线程集合中管理
                ManageSToC_SocketThread.addThread(userId, sToC_socketThread);
                b = true;
            } else {
                //如果失败，就关闭数据通道，返回false，表示登录失败
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    //编写方法完成用户注册验证
    public boolean ifRegister(String userId, String pwd) {
        boolean b = false;
        //构建user
        user.setUserId(userId);
        user.setPwd(pwd);
        user.setType("register");
        //把user对象传到服务端
        try {
            socket = new Socket(InetAddress.getLocalHost(), 9999);
            ObjectOutputStream oOS = new ObjectOutputStream(socket.getOutputStream());
            oOS.writeObject(user);
            //接收服务端传过来的message对象 , 获取message类型--注册succeed or failed
            ObjectInputStream oIS = new ObjectInputStream(socket.getInputStream());
            Message response = (Message) oIS.readObject();
            if (response.getMessageType().equals(MessageType.message_register_succeed)) {
                b = true;
            }
            //关闭相关流
            oIS.close();
            oOS.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    //编写方法通知服务端退出本用户端的进程
    public void exitNotice(String userId) {
        //构建user和message
        user.setUserId(userId);
        Message message = new Message();
        //设置message type and sender
        message.setMessageType(MessageType.message_notice_exit);
        message.setSender(userId);
        //得到当前用户的socket，发送message --消息类型：通知退出持有当前用户socket的线程
        //step1 从管理线程的集合中，通过userId, 得到这个线程对象
        SToC_SocketThread thread = ManageSToC_SocketThread.getThread(userId);
        //step2 通过这个线程对象得到关联的socket
        Socket socket = thread.getSocket();
        //step3 通过得到关联的socket得到对应的 ObjectOutputStream对象
        try {
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream oOS = new ObjectOutputStream(outputStream);
            oOS.writeObject(message);
            //通过语句来强制正常结束进程，那么线程自然而然就停止run了，不需要接收服务端message后再去结束
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
