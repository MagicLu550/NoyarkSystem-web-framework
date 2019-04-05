[![LICENSE](https://img.shields.io/badge/license-Noyark-green.svg)](NoyarkLICENSE.md)
![VERSION](https://img.shields.io/badge/version-0.3.0NEW-blue.svg)
![Build](https://img.shields.io/badge/build-passing-green.svg)
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
All objects are initialized when the container is opened. If some objects are not used immediately, you can use a lazy initialization solution.
```xml
<bean id="test" class="test.User" lazy-init="true">
```
* Automatic assembly
Automated assembly looks for objects in the entire container environment, there are two search methods
getByType getByName , getByName will look for the same id bean in the whole environment, getByType will find the same type of bean
```xml
<bean id="test" class="test.User" autowired="getByName">
```
*scope assembly
Currently supports protoType, after this property is added, the instantiation will not be a single object, each time getBean will get a new object
```xml
<bean id="test" class="test.User" scope="protoType">
```
* You can specify initialization and destruction methods during assembly
```xml
<bean id="test" class=“test.User” init-method="init" despory-method="despory">
```
> How to create a bean with annotations
This article supports the creation of bean objects using the following annotations.
@Component General Annotation
@Controller Controller Annotation
@Service business layer annotation
@Repository DAO and other persistence layer annotations
They are all equivalent in function, but for the sake of semantics, it is recommended to use
* How to configure the package scanner
Before using annotations, you must configure a node for scanning and identifying annotations, configured in noyark.xml
```xml
<context:component-scan base-package=”net.noyark.www”/>
```
`base-package` is the basic package path. All sub-packages under the package path and the classes with bean annotations in the package will be scanned.
* How to use in a class
```java
Package net.noyark.www;

@Component
Public class User{
}
```
With this annotation, the default beanid is user, which is the first letter of the class name. If you want to make a beanid, you can add a value to the annotation.
```java
@Component("id")
```
That is, beanid is formulated, and other annotations are equally equivalent.
> How to use a bean
To use a bean, you need to start the container to resolve the bean, using the following classes:
```java
NoyarkAbstractApplicationContext nac_ = new NoyarkApplicationContext("noyark.xml", true);
```
Get the bean: (here the previous user bean as an example)
Get the bean two ways, recommend the second, the type of the bean is indicated later, but to ensure that the bean is an object of this type, otherwise a type conversion exception will be thrown.
```java
User user = (User)(nac.getBean("user"));//Need to cast
User user = nac.getBean("user",User.class);//No conversion required
```
> Need to pay attention: ensure that each class has a struct constructor, otherwise it cannot be instantiated

> How to inject fields in a bean
* Normal injection
Noyark-system can inject values ​​without the need for constructors and set methods
Injection using property node: instance omits content in bean
```xml
<bean>
<property name="" value=""/>
</bean>
```
It is necessary to ensure that the field specified by `name` is present, otherwise an exception will be thrown.
`value` can only specify basic types and strings. If you want to import beans, you can use `ref`
```xml
<bean>
<property name="" ref="beanid">
</bean>
```
There is no requirement for the order of the beans.
* Inject collection
This supports the injection of collection tags.
Need to be done in the property keyword
```xml
<bean>
<property name="">
<list>
<value>value</value>
</list>
</property>
</bean>
```
Noyarksystem supports collection injection beans
```xml
<bean>
<property name="" type="generic type">
<list>
<value ref="beanid"/>
</list>
</property>
</bean>
```
Array and list are injected in the same way, and both array and list nodes are shared. They are equivalent. Injecting arrays can be used in this way, but the type is guaranteed to be consistent.
Noyarksystem supports set collection, only replaces list with set, and also supports injection of beans.

Noyark-system supports map collection injection
```xml
<bean>
<property name="">
<map>
<entry key="">
<value></value>
</entry>
</map>
</property>
</bean>
```
```xml
<bean>
<property name="">
<map>
<entry key="" value=""/>
</map>
</property>
</bean>
```
Also supports injecting beans
```xml
<bean>
<property name="" key-type="" value-type="">
<map>
<entry key="" ref="">
</entry>
</map>
</property>
</bean>
```
At the same time, support the injection of Property objects
```xml
<bean>
<property name="">
<props>
<prop key="" value="">
</props>
</property>
</bean>
```
Constructor injection is also supported:
```xml
<bean>
<constructor-value value="" type=""></constructor-value>
</bean>
```
Value is the value of the constructor property, type is the type of the property, the container will find the corresponding constructor according to the order and type of the constructor, and assign the value to the corresponding type of the parameter.
* Import the Properties file and inject it into the object
```xml
<util:properties id=”” location=“classpath:xxxx”>
```
How to use in the bean, please see the injection expression below
> Note when injecting, it should be determined that this attribute exists, and the type corresponds
* Injection expression:
Noyark-system supports injection expressions, syntax
`#{bean.property.propertyInside}`
Specify by value=‘’ of property, etc.

* property object: name.propertyKey of beanID.property
* Normal value: beanID.fieldname
* set, array, list: beanID.name[index]
* map value: beanID.mapName.key
* Imported properties:propertiesID.key, if there are multiple points, they can be separated by an exclamation mark
```xml
#{config.!system.bean.let!}
```
* Alias ​​mechanism
You can alias an bean
```xml
<alias id="beanid" alias="alias">
```
But then there can't be a bean with a duplicate alias name, and the bean property with no previous duplicate name is determined.
# NoyarkSystem MVC
Noyarksystem supports the mvc function, based on the forwarding function of the bean container and servlet introduced earlier:
The following web.xml configuration patterns have been mentioned before:
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
`Specify package path:`
Specify the package to be scanned for noyark.xml
```xml
<context:component-scan base-package=”net.noyark.www”/>
```
Then you can start
* Know the annotations
@RequestMapping
@AutoWired
@ExceptionHandle
- The role of the RequestMapping annotation
The annotation can be placed in two places, on the class and on the method
```java
@Controller
@RequestMapping("start")
Public class Controller{
@RequestMapping("test.no")
Public String test(HttpServletRequest req){
System.out.println("Enter test.no");
Return null;
}
}
```
Then the path defaults to start/test.no
If we visit http://localhost:8080/project name /start/test.no
The console will output `Enter test.no` and then 404, indicating that the framework configuration is successful.
About the method under RequestMapping:
The current version only supports returning a String, although it returns no error, but `will not work.' The returned value is the path to which it is forwarded. If you add `redirect:` before the return value, it is redirected.
Add to the class will use this as the parent path
- About AutoWired annotations
Its role is on the field, it can automatically inject the value on this field
```java
@AutoWired
Public User user;
```
- About ExceptionHandle annotations
The ExceptionHandle annotation will jump to the ExceptionHandle method when the method in MethodMapping throws an exception. You need to specify the exception type or the exception parent class type.
Must have Exception parameter
```java
@ExceptionHandle(Exception.class)
Public String handleException(Exception e){
//...
Return null;
}
```
