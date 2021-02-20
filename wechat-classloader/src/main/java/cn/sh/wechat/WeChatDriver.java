package cn.sh.wechat;


import cn.sh.classload.core.Driver;
import cn.sh.classload.core.DriverManager;

/**
 * @author sh
 */
public class WeChatDriver implements Driver {

    static {
        Driver driver = new WeChatDriver();
        DriverManager.registerDriver(driver);
        System.out.println("WeChatDriver Class Loader By " + WeChatDriver.class.getClassLoader());
    }

    @Override
    public String getDriverDesc() {
        return "公众号：Different Java，或者扫描README.md中的二维码关注";
    }
}
