package client;

import client.view.menuOne;
import java.io.IOException;

public class applicationStart {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        new menuOne().showMenu();
    }

}
