package packet;

public interface MessageType {
    String message_login_succeed = "1"; //登录成功(from server to client)
    String message_login_fail = "2"; //登录失败(from server to client)

    String message_register_succeed = "3"; //注册成功(from server to client)
    String message_register_fail = "4"; //注册失败(from server to client)

    String message_return_online_friends = "5"; //返回在线用户列表(from server to client)
    String message_private_chat_return = "6"; //返回在线用户列表(from server to client)
    String message_private_chat_offline = "7";
    String message_get_online_friends = "a"; //请求get在线用户列表(from client to server)
    String message_notice_exit = "b"; //退出进程通知(from client to server)

    String message_private_chat = "c"; //私聊(from client to server)
    String message_public_chat = "d"; //群聊(from client to server)

    String message_file = "e"; //私发文件(from client to server)
}
