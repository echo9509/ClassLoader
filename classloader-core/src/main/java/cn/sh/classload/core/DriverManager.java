package cn.sh.classload.core;

/**
 * @author sh
 */
public class DriverManager {

    private static Driver registeredDriver = null;
    private static final Object LOCK = new Object();

    static {
        System.out.println("DriverManager Class Loader By " + DriverManager.class.getClassLoader());
        ServiceLoader.load(Driver.class);
    }

    public static Driver getDriver() {
        if (registeredDriver == null) {
            throw new NullPointerException("no register driver");
        }
        return registeredDriver;
    }

    public static void registerDriver(Driver driver) {
        if (registeredDriver == null) {
            synchronized (LOCK) {
                if (registeredDriver == null) {
                    registeredDriver = driver;
                }
            }
        }
    }
}
