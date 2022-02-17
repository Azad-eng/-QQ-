package client.view;


import client.aidService.ChatService;
import client.aidService.FunctionService;

import java.io.IOException;

public class menuOne {
    private boolean loop = true;
    private FunctionService fs = new FunctionService(); //获取aidService包里的login_out类，为了调用类中的方法checkUserInfo()
    private ChatService cs = new ChatService();

    //编写方法显示一级菜单
    public void showMenu() throws IOException, ClassNotFoundException, InterruptedException {
        while (loop) {
            System.out.println("========多用户通讯系统启动界面========");
            System.out.println("\t\t1  登录用户");
            System.out.println("\t\t2  注册账号");
            System.out.println("\t\t3  退出系统");
            //让用户输入选择是否进入系统，如果1，就进入用户菜单，如果2，注册账号
            System.out.print("请输入选项：");
            char key = Utility.readMenuOneSelection();
            switch (key) {
                case '1':
                    while (loop) {
                        System.out.print("请输入用户名：");
                        String userId = Utility.readString(8);
                        System.out.print("请输入密  码：");
                        String pwd = Utility.readString(16);
                        //需要通过服务端验证用户信息是否正确，如果正确进入用户菜单界面，如果错误，显示登录失败，并重新回到登录界面
                        if (fs.checkUserInfo(userId, pwd)) {
//                            System.out.println("====用户" + userId + "登录成功====");//进入二级用户菜单TEST
                            new menuTwo(userId);
                        } else {
                            System.out.println("登录失败——原因：用户名或密码输入错误,或者已登录");
                            char res = Utility.readConfirmSelection();
                            if (res == 'N') {
                                break;
                            }
                        }
                    }

                    break;
                case '2':
                    System.out.print("请输入注册的用户名：");
                    String userId = Utility.readString(8);
                    System.out.print("请输入注册的密码：");
                    String pwd = Utility.readString(16);
//                    需要通过服务端验证是否能够注册账号，如果能显示注册成功，然后跳转到登录界面，如果错误，显示登录失败，并重新回到登录界面
                    if (fs.ifRegister(userId, pwd)) {
                        System.out.println("====用户" + userId + "注册成功====");//进入二级用户菜单
                    } else {
                        System.out.println("注册失败: 该账户已经被注册，请重新注册");
                        char res = Utility.readConfirmSelection();
                        if (res == 'N') {
                            break;
                        }
                    }
                    break;
                case '3':
                    loop = false;
                    break;
            }
        }
    }
}
