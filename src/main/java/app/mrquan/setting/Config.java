package app.mrquan.setting;

public class Config {
    private static String token="16a1a4a8a2ee85c693d8adfd63b5cec66976b7ed";
    private static String base="https://api.github.com/";
    //https://api.github.com/emojis?access_token=16a1a4a8a2ee85c693d8adfd63b5cec66976b7ed
    public static String EMOJIS = "emojis";


    public static String url(String fun){
//        return base+fun+"?access_token="+token;
        return base+fun;
    }
}