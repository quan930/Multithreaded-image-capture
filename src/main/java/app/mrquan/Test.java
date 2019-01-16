package app.mrquan;

import app.mrquan.factory.GitHubAPI;
import app.mrquan.setting.Config;

public class Test {
    public static void main(String[] args) {
//        System.out.println(Config.url(Config.EMOJIS));
        GitHubAPI.getEmojis("/Users/daquan/Desktop/9.png");
    }
}