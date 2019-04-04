# Noyark-System Web轻量级框架
*测试版，目前没有优化代码和标准注释*
借鉴了`spring`框架风格,作者日常作品
代码原创

>  如何配置该框架？
* 首先，在web.xml如下配置
```xml
<servlet>
    <description></description>
    <display-name>MainServlet</display-name>
    <servlet-name>MainServlet</servlet-name>
    <servlet-class>net.noyark.www.core.MainServlet</servlet-class>
    <init-param>
      <param-name>contextConfigLocation</param-name>
      <param-value>classpath:noyark.xml</param-value>
    </init-param>
  </servlet>
  <servlet-mapping>
    <servlet-name>MainServlet</servlet-name>
    <url-pattern>*.no</url-pattern>
  </servlet-mapping>
  <filter>
    <display-name>RequestEncodingFilter</display-name>
    <filter-name>RequestEncodingFilter</filter-name>
    <filter-class>net.noyark.www.filter.RequestEncodingFilter</filter-class>
   <init-param>
     <param-name>encoding</param-name>
     <param-value>UTF-8</param-value>
   </init-param>
  </filter>
  <filter-mapping>
    <filter-name>RequestEncodingFilter</filter-name>
    <url-pattern>/*</url-pattern>
  </filter-mapping>
```
servlet的url-pattern是通用路径，访问时，凡是xxx.no都会经过MainServlet进行处理
servlet中的param-value是noyarksystem配置文件路径,class path:前缀可以获取到resource的classpath路径
第二个拦截器是设置默认编码，这里配置了utf-8
* 配置noyark.xml
在github中，下载配置文件，全套下载即可
有3个文件夹和一个xml文件，都需要下载
* 配置pom.xml
在pom.xml的`denpendencies`配置以下jar包坐标
```xml
<dependency>
  <groupId>net.noyark</groupId>
  <artifactId>equery-framework</artifactId>
  <version>0.3.0</version>
</dependency>
<dependency>
  <groupId>net.noyark</groupId>
  <artifactId>noyark-system</artifactId>
  <version>0.0.1</version>
</dependency>
<dependency> 
      <groupId>dom4j</groupId>  
      <artifactId>dom4j</artifactId>  
      <version>1.6.1</version> 
    </dependency>  
    <!—  [https://mvnrepository.com/artifact/mysql/mysql-connector-java](https://mvnrepository.com/artifact/mysql/mysql-connector-java)  —>  
    <dependency> 
      <groupId>mysql</groupId>  
      <artifactId>mysql-connector-java</artifactId>  
      <version>8.0.15</version> 
    </dependency>  
    <dependency> 
      <groupId>commons-dbcp</groupId>  
      <artifactId>commons-dbcp</artifactId>  
      <version>1.4</version> 
    </dependency>
```
之后配置以下节点
```xml
<repositories>
 <repository>
    <id>nexus</id>
    <name>Team Neux Repository</name><url> [http://www.noyark.net:8081/nexus/content/groups/public/](http://www.noyark.net:8081/nexus/content/groups/public/) </url>
</repository>
</repositories>
<pluginRepositories>
    <pluginRepository>
      <id>nexus</id>
      <name>Team Neux Repository</name>
      <url> [http://www.noyark.net:8081/nexus/content/groups/public/](http://www.noyark.net:8081/nexus/content/groups/public/) </url>
    </pluginRepository>
 </pluginRepositories>
```
noyark-system环境配置完成
> 如何在xml中配置bean
noyark-system支持以下xml-bean配置方式
`普通配置方式` `自身的工厂模式配置` `对象工厂模式配置` 
`懒初始化` `自动装配` `scope装配` `指定初始化方法和销毁方法`
* 普通的配置模式：
```xml 
<bean id="test" class="test.User"></bean>
```
同时，允许使用name代替id
```xml
<bean name="test" class="test.User"></bean>
```
* 自身的工厂模式配置
如果一个类有静态工厂方法，用来初始化对象，可以以下配置方式
```xml
<bean id="factory" class="java.util.Calendar" factory-method="getInstance">
```
* 通过其他对象的方法初始化该对象
如果之前创建了bean且bean拥有方法初始化一个对象，可以使用
```xml
<bean id="test" class="test.User">
<bean id="factory" factory-bean="test" factory-method="getInstance">
```
* 懒初始化
所有对象都是在容器开启时就会初始化,如果有些对象不会立即使用，可以使用懒初始化的解决方案
```xml
<bean id="test" class="test.User" lazy-init="true">
```
* 自动装配
自动装配会在整个容器环境中寻找对象，有两种搜索方法
getByFile getByName
