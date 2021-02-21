package cn.sh.client;

import cn.sh.classload.core.Driver;
import cn.sh.classload.core.DriverManager;

/**
 * @author sh
 */
public class ClientTest {

    public static void main(String[] args) throws ClassNotFoundException {
        Driver driver = DriverManager.getDriver();
        System.out.println(driver.getDriverDesc());
    }
}
