package cn.sh.client;

import cn.sh.classload.core.Driver;
import cn.sh.classload.core.DriverManager;

/**
 * @author sh
 */
public class ClientTest {

    public static void main(String[] args) throws ClassNotFoundException {
        System.setProperty("java.ext.dirs", "/Users/sh/workspace/ClassLoader/classloader-core/build/libs");
        Driver driver = DriverManager.getDriver();
        System.out.println(driver.getDriverDesc());
    }
}
