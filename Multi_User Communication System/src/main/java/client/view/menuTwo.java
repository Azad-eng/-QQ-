package client.view;

import client.aidService.ChatService;
import client.aidService.FileService;
import client.aidService.FunctionService;

public class menuTwo {
    private String userId;
    private String pwd;
    private boolean loop = true;
    private FunctionService fs = new FunctionService();
    private ChatService cs = new ChatService();
    private FileService fi = new FileService();
    private String content;
    private String getter;

    //编写方法显示二级菜单
    public menuTwo(String userId) {
        this.userId = userId;
        while (loop) {
            System.out.println("========" + userId + "的聊天窗口栏========");
            System.out.println("\t\t1.显示在线用户列表");
            System.out.println("\t\t2.群发消息");
            System.out.println("\t\t3.私聊消息");
            System.out.println("\t\t4.发送文件");
            System.out.println("\t\t5.退出登录");
            System.out.print("请输入选项：");
            char key = Utility.readMenuTwoSelection();
            switch (key) {
                case '1':
                    //调用户辅助服务类之一class FunctionService 的方法getList(),获得在线用户列表
                    cs.getList(userId);
                    break;
                case '2':
                    System.out.println("Please enter public chat content：");
                    content = Utility.readString(100);
                    cs.publicChat(userId, content);
                    break;
                case '3':
                    System.out.println("Please enter private chat friend(online)：");
                    getter = Utility.readString(10);
                    System.out.println("Please enter private chat content：");
                    content = Utility.readString(100);
                    cs.privateChat(userId, getter, content);
                    break;
                case '4':
                    System.out.println("Please enter the path of file you want to sent from：(x(c,d,e...):\\xxx.jpg(txt,wav...))");
                    String srcPath = Utility.readString(50);
                    System.out.println("Please enter the file getter：");
                    getter = Utility.readString(10);
                    System.out.println("Please enter the path of file you want to sent to：(x(c,d,e...):\\xxx.jpg(txt,wav...))");
                    String desPath = Utility.readString(50);
                    fi.sendFile(userId, getter, srcPath, desPath);
                    break;
                case '5':
                    System.out.println("============您已退出登录=============");
                    fs.exitNotice(userId);
                    loop = false;
                    break;
            }

        }

    }
}
