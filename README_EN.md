[![LICENSE](https://img.shields.io/badge/license-Noyark-green.svg)](NoyarkLICENSE.md)
![VERSION](https://img.shields.io/badge/version-0.3.0NEW-blue.svg)
![Build](https://img.shields.io/badge/build-passing-green.svg)
[![zh](https://img.shields.io/badge/readme-chinese-orange.svg)](README.md)
# Noyark-System Web Lightweight Framework
* Beta, there are currently no optimized code and standard comments*
Drawing on the `spring` framework style, the author's daily works
Original code

> How to configure the framework?
* First, configure the following in web.xml
```xml
<servlet>
    <description></description>
    <display-name>MainServlet</display-name>
    <servlet-name>MainServlet</servlet-name>
    <servlet-class>net.noyark.www.servlet.MainServlet</servlet-class>
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
The url-pattern of the servlet is a generic path. When accessing, any xxx.no will be processed by the MainServlet.
The param-value in the servlet is the noyarksystem configuration file path, and the class path: prefix can get the resource's classpath path.
The second interceptor is to set the default encoding, here is configured utf-8
* Configure noyark.xml
In github, download the configuration file and download it in full.
There are 3 folders and an xml file, all need to be downloaded
* Configure pom.xml
Configure the following jar package coordinates in `denpendencies` of pom.xml
```xml
<dependency>
  <groupId>net.noyark</groupId>
  <artifactId>equery-framework</artifactId>
  <version>0.3.0</version>
</dependency>
<dependency>
  <groupId>net.noyark</groupId>
  <artifactId>noyark-system</artifactId>
  <version>0.0.2</version>
</dependency>
<dependency>
      <groupId>dom4j</groupId>
      <artifactId>dom4j</artifactId>
      <version>1.6.1</version>
    </dependency>
    <!- ​​[https://mvnrepository.com/artifact/mysql/mysql-connector-java](https://mvnrepository.com/artifact/mysql/mysql-connector-java) —>
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
Then configure the following nodes
```xml
<repositories>
 <repository>
    <id>nexus</id>
    <name>Team Neux Repository</name><url> http://www.noyark.net:8081/nexus/content/groups/public/ </url>
</repository>
</repositories>
<pluginRepositories>
    <pluginRepository>
      <id>nexus</id>
      <name>Team Neux Repository</name>
      <url> http://www.noyark.net:8081/nexus/content/groups/public/ </url>
    </pluginRepository>
 </pluginRepositories>
```
Noyark-system environment configuration completed
> How to configure a bean in xml

Noyark-system supports the following xml-bean configuration
`Ordinary configuration method`
`Own factory mode configuration`
`Object factory mode configuration`
Lazy initialization
`Automatic assembly`
`scope assembly`
`Specify initialization method and destruction method`
* Normal configuration mode:
```xml
<bean id="test" class="test.User"></bean>
```
Also, allow the use of name instead of id
```xml
<bean name="test" class="test.User"></bean>
```
* Its own factory mode configuration
If a class has a static factory method, used to initialize the object, you can configure the following
```xml
<bean id="factory" class="java.util.Calendar" factory-method="getInstance">
```
* Initialize the object by methods of other objects
If you have previously created a bean and the bean owns a method to initialize an object, you can use
```xml
<bean id="test" class="test.User">
<bean id="factory" factory-bean="test" factory-method="getInstance">
```
* lazy initialization
All objects are initialized when the container is opened. 
