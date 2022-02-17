package server;

import packet.Message;
import packet.User;
import packet.MessageType;
import server.aidService.receiveFromClient_socketThread;
import server.aidService.manageCToS_socketThread;
import server.aidService.sendTOClient_socketThreadWithOffContent;
import server.aidService.sendToClient_socketThreadWithNews;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/*
 * 在创建对象时，系统会自动的调用该类的构造器完成对象的初始化。
 */
@SuppressWarnings({"all"})
public class server {
    private static ServerSocket ss = null;
    /*
      这里我们也可以使用 ConcurrentHashMap, 可以处理并发的集合，没有线程安全问题
      HashMap 没有处理线程安全，因此在多线程情况下是不安全
      ConcurrentHashMap 处理的线程安全,即线程同步处理, 在多线程情况下是安全
    */
    private static ConcurrentHashMap<String, User> validUsers = new ConcurrentHashMap<>();

    /*
     * 静态代码块：与普通代码块一样，在类被加载时会执行方法体内代码，与普通代码块不同的是，静态代码块只会执行一次
     * 思考：类什么时候会被加载？
     * 答：1.实例化类对象时(new serverStart())
     *    2.实例化子类对象时，父类也会被加载
     *    3.使用类的静态成员时(validUsers)
     */
    static {
        //把离线消息接收者-离线消息放入到ConcurrentHashmap集合中
        validUsers.put("罗辑", new User("罗辑", "001"));
        validUsers.put("史强", new User("史强", "002"));
        validUsers.put("叶文洁", new User("叶文洁", "003"));
        validUsers.put("云天明", new User("云天明", "004"));
        validUsers.put("章北海", new User("章北海", "005"));
    }

    //编写方法遍历集合，取出所有的userId,判断指定userId是否存在，返回一个布尔值
    public static boolean ifExist(String userId) {
        Iterator<String> iterator = validUsers.keySet().iterator();
        boolean b = false;
        while (iterator.hasNext()) {
            String validUser = iterator.next();
            if (validUser.equals(userId)) {
                b = true;
            }
        }
        return b;
    }

    //编写验证用户是否能够登陆成功的方法
    public static boolean checkUserLogIn_Out(String userId, String pwd) {
        User user = validUsers.get(userId);
        if (user == null) { //说明集合中不存在userId对应的user
            return false;
        }
        //如果线程集合里有对应的线程，说明该用户已经在登录中了，就不能再次登录了
        if (manageCToS_socketThread.getThread(userId) != null) {
            return false;
        }
        //如果存在，那么就去验证密码是否正确
        return user.getPwd().equals(pwd);
    }

    //编写方法验证用户登录成功后ConcurrentHashmap集合中是否有对应的getter，如果有就返回该getter对应的ArrayList<message>
    public boolean checkIsGetter(String userId, ConcurrentHashMap<String, ArrayList<Message>> chm) {
        boolean b = false;
        Iterator<String> iterator = chm.keySet().iterator();
        while (iterator.hasNext()) {
            String getter = iterator.next();
            if (userId.equals(getter)) {
                b = true;
            }
        }
        return b;
    }

    //
//    //编写验证用户是否能够注册成功的方法
    public boolean checkUserRegister(String userId) {
        //通过userId去获得user，如果有，就代表无法再注册了
        User user = validUsers.get(userId);
        return user == null;
    }

    public server() {  //构造器，完成对类对象的初始化
        try {
            System.out.println("服务端在9999端口监听ing...");
            ss = new ServerSocket(9999);
            //实例化推送新闻的服务类，启动该服务类线程
            new sendToClient_socketThreadWithNews().start();
            while (true) {
                //监听是否有接收到客户端传来的对象(user/message),保持持续监听状态，收到一个后也要继续重新监听，因此while
                Socket socket = ss.accept(); //如果没有客户端连接，会堵塞
                //得到socket关联的对象输入流
                ObjectInputStream oIS =
                        new ObjectInputStream(socket.getInputStream());
                //得到socket关联的对象输出流
                ObjectOutputStream oOS =
                        new ObjectOutputStream(socket.getOutputStream());
                //读取客户端发送的User对象，得到userId和pwd和type
                User user = (User) oIS.readObject();
                //核查对象信息，返回相应message
                Message message = new Message();
                switch (user.getType()) {
                    case "login":
                        //如果数据库中有对应的user，就返回带有登录成功属性的message，反之，返回带有登录失败的属性的message
                        if (checkUserLogIn_Out(user.getUserId(), user.getPwd())) {
                            //构建带有登录成功属性的message
                            message.setMessageType(MessageType.message_login_succeed);
                            //创建一个并启动该socket的线程类(接收服务端传送的message)，重写run方法让该线程一直run，保持同客户端之间的消息传递
                            receiveFromClient_socketThread receiveFromClient_socketThread = new receiveFromClient_socketThread(user.getUserId(), socket);
                            //启动该线程
                            receiveFromClient_socketThread.start();
                            //将message对象回复客户端
                            oOS.writeObject(message);
                            ///把该线程对象放入到线程集合中管理
                            manageCToS_socketThread.addThread(user.getUserId(), receiveFromClient_socketThread);
                            if (checkIsGetter(user.getUserId(), receiveFromClient_socketThread.getCHM())) {
                                //实例化推送留言或离线文件的服务类，启动该服务类线程
                                sendTOClient_socketThreadWithOffContent s = new sendTOClient_socketThreadWithOffContent();
                                s.setUserId(user.getUserId());
                                s.setMessages(receiveFromClient_socketThread.getMessages());
                                s.start();
                            }
                        } else {
                            System.out.println("id=" + user.getUserId() + " pwd=" + user.getPwd() + " 用户验证失败");
                            message.setMessageType(MessageType.message_login_fail);
                            //将message对象回复客户端
                            oOS.writeObject(message);
                            //关闭socket
                            socket.close();
                        }
                        break;
                    case "register":
                        //如果数据库中有对应的user，就返回带有注册失败属性的message，反之，返回带有注册成功的属性的message
                        if (checkUserRegister(user.getUserId())) {
                            //构建message
                            message.setMessageType(MessageType.message_register_succeed);
                            //将message对象回复客户端
                            oOS.writeObject(message);
                            //把user加入到validUsers集合中
                            validUsers.put(user.getUserId(), new User(user.getUserId(), user.getPwd()));
                        } else {
                            message.setMessageType(MessageType.message_register_fail);
                            oOS.writeObject(message);
                            //关闭socket
                            socket.close();
                        }
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


}
