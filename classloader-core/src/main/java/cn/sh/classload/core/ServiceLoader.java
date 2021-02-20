package cn.sh.classload.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author sh
 */
public class ServiceLoader<T> {

    private static final String PREFIX = "META-INF/sh-services/";

    /**
     * SPI Interface
     */
    private final Class<T> services;

    /**
     * ClassLoader
     */
    private final ClassLoader loader;

    private ServiceLoader(Class<T> services, ClassLoader classLoader) {
        this.services = Objects.requireNonNull(services, "Service interface can not be null");
        this.loader = classLoader == null ? ClassLoader.getSystemClassLoader() : classLoader;
        loadServices();
    }

    private void loadServices() {
        String fullName = PREFIX + services.getName();
        Enumeration<URL> serviceConfigs = getServiceConfigs(fullName);
        if (serviceConfigs == null) {
            return;
        }
        Set<String> serviceImplClassNames = null;
        while (serviceConfigs.hasMoreElements()) {
            serviceImplClassNames = parseLine(services, serviceConfigs.nextElement());
        }
        loadServiceImplClasses(serviceImplClassNames);
    }

    private void loadServiceImplClasses(Set<String> serviceImplClassNames) {
        if (serviceImplClassNames == null || serviceImplClassNames.isEmpty()) {
            return;
        }
        for (String className : serviceImplClassNames) {
            loadServiceImplClass(className);
        }
    }

    private void loadServiceImplClass(String className) {
        Class<?> c = null;
        try {
            c = Class.forName(className, false, loader);
        } catch (ClassNotFoundException e) {
            fail(services, "Provider " + className + " not found", e);
        }
        if (!services.isAssignableFrom(c)) {
            fail(services, "Provider " + className + " not a subtype");
        }
        try {
            services.cast(c.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            fail(services, "Provider " + className + "can not instantiation");
        }
    }

    private Set<String> parseLine(Class<T> services, URL url) {
        Set<String> result = new LinkedHashSet<>();
        try (InputStream in = url.openStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
        } catch (IOException e) {
            fail(services, "read service config file error", e);
        }
        return result;
    }

    private Enumeration<URL> getServiceConfigs(String serviceResourceName) {
        Enumeration<URL> serviceConfigs = null;
        try {
            if (loader == null) {
                serviceConfigs = ClassLoader.getSystemResources(serviceResourceName);
            } else {
                serviceConfigs = loader.getResources(serviceResourceName);
            }
        } catch (IOException e) {
            fail(services, "Error loading config file", e);
        }
        return serviceConfigs;
    }

    private void fail(Class<T> services, String error) {
        throw new ServiceConfigurationError(services.getName() + ":" + error);
    }

    private void fail(Class<T> services, String error, Exception e) {
        throw new ServiceConfigurationError(services.getName() + ":" + error, e);
    }


    public static <T> ServiceLoader<T> load(Class<T> services) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return ServiceLoader.load(services, classLoader);
    }

    private static <T> ServiceLoader<T> load(Class<T> services, ClassLoader classLoader) {
        return new ServiceLoader<>(services, classLoader);
    }


}
