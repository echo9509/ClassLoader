# 微信公众号
搜索：Different Java

或者扫码关注
![qrcode_for_gh_1706f00849c9_344.jpg](http://ww1.sinaimg.cn/large/69ad3470gy1gmd7g8h298j209k09kt8n.jpg)

# 1. 打破双亲委派
Java类加载器的实现采用双亲委派原则，通过双亲委派原则，我们可以很好的保护Java程序在运行时的安全，越基础的类越会被上层的类加载器（例如启动类加载器）加载，
但是这种双亲委派在有些时候也会成为约束。 比如当我的基础类需要调用用户自实现的类的将会发生问题，由于委派是向父类委派，因此用户自实现的类无法加载（没有类加载器可以加载），
因此调用会失败。此时我们就需要打破这种约束，让父类加载器可以出发子类记载器去加载特定的类。

其实上述情况在Java中已经存在，典型的例子便是JDNI服务，JDNI服务的代码由启动类加载器去加载(rt.jar)，但在具体的实现时则需要调用各个厂商的具体实现，这些具体实现
（SPI，Service Provider Interface）则是保存在用户的类路径下。

为了解决这个问题，Java引入了一个不太优雅的设计：线程上下文类加载器，这个类加载器可以通过Thread.setContextClassLoader()来设置，如果创建线程时
没有设置，将会从父线程进行继承，如果全局都没有进行设置，那么这个类加载器默认则是应用程序类加载器。

# 2. 实现加载器
为了更好的巩固类加载器，我们特地自实现一个自己的类加载器，该类加载模拟的是ServiceLoader，ServiceLoader通常用在SPI机制里面，它打破了双亲委派机制，
使得父类加载器可以访问子类加载器加载的类。

实现大致分为三个模块：

- classloader-core：类加载器的核心实现
- wechat-classloader：模拟厂商提供的具体实现
- client：模拟我们的客户端，使用厂商提供的具体实现


## 2.1 classloader-core
classloader-core包含了三个关键类：
- Driver：接口，具体的实现由wechat-classloader实现
- DriverManager：Driver管理类，client可使用它获取具体的Driver，并且执行相关方法
- ServiceLoader：自定义的类加载器

classloader-core模块在运行时我会设置使用ExtClassLoader进行加载，将他当做Java提出来的规范。

## 2.1.1 ServiceLoader
ServiceLoader作为核心的核心，我单独介绍一下，ServiceLoader主要加载META-INF/sh-services/路径下的所有接口的具体实现类，如下图：
![WX20210220-155211@2x.png](http://ww1.sinaimg.cn/large/69ad3470gy1gnu1vuu8i7j20ka0b8dgq.jpg)

该文件的内容为该接口的具体实现类，由于我们只是做演示了解整个类加载的过程，因此ServiceLoader的实现相对简单，没有使用懒加载也没有使用缓存。

核心：

- final Class<T> services属性：接口 
- final ClassLoader loader：具体的类加载器，使用Thread.getContextClassLoader()，这里得到的是默认的AppClassLoader

## 2.2 wechat-classloader
Driver的具体实现，例如JDBC的jar包

## 2.3 client
具体的应用程序，我们的应用程序会使用到classloader-core，wechat-classloader，触发对应的类加载器

client的时序图如下：
![WX20210221-100532@2x.png](http://ww1.sinaimg.cn/large/69ad3470gy1gnuxid79izj21ki12mwkd.jpg)

# 运行程序
```shell
./gradlew build
# /Users/sh/workspace/ClassLoader/classloader-core/build/libs/这个目录替换成classloader-core的jar包所在的目录
java -Djava.ext.dirs=$JAVA_HOME/jre/lib/ext:/Users/sh/workspace/ClassLoader/classloader-core/build/libs/ -jar client/build/libs/client-1.0-SNAPSHOT.jar
```
结果如下：

![WX20210220-165803@2x.png](http://ww1.sinaimg.cn/large/69ad3470gy1gnu3sgvrlnj20sm03idgi.jpg)