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
  <version>0.0.2</version>
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
noyark-system环境配置完成
> 如何在xml中配置bean

noyark-system支持以下xml-bean配置方式
`普通配置方式`
`自身的工厂模式配置` 
`对象工厂模式配置` 
`懒初始化` 
`自动装配` 
`scope装配` 
`指定初始化方法和销毁方法`
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
getByType getByName ,getByName会在整个环境寻找相同id的bean，getByType会寻找同一个类型的bean
```xml
<bean id="test" class="test.User" autowired="getByName">
```
* scope装配
目前支持protoType，加上该属性后,实例化将不是单一对象，每次getBean都会获得一个新对象
```xml
<bean id="test" class="test.User" scope="protoType">
```
* 在装配时可以指定初始化和销毁方法
```xml
<bean id="test" class=“test.User” init-method="init" despory-method="despory">
```
> 如何利用注解创建bean
这里支持使用以下注解创建bean对象
@Component 通用注解
@Controller 控制器注解
@Service 业务层注解
@Repository DAO等持久层注解
他们在作用上都是等价的，但是为了语意明显，建议分化使用
* 如何配置包扫描器
在使用注解前，必须配置好一个节点，用于扫描和识别注解，在noyark.xml配置
```xml
<context:component-scan base-package=“net.noyark.www”/>
```
`base-package`是基本包路径，该包路径下的全部子包里和该包里带bean注解的类都会被扫描到
* 如何在类中使用
```java
package net.noyark.www;

@Component
public class User{
}
```
使用该注解，默认beanid是user，即类名的首字母小写，如果要制定beanid，可以在注解加入一个value值
```java
@Component("id")
```
即制定了beanid，其他注解同样等价
> 如何使用bean
使用bean需要启动容器，来解析bean，使用以下类：
```java
NoyarkAbstractApplicationContext nac_ = new NoyarkApplicationContext(“noyark.xml”,true);
```
获取bean：(这里以之前的user bean为例子)
获取bean两种方式，推荐第二种，在后面表明bean的类型，但是要保证这个bean是这个类型的对象，否则会抛出类型转换异常
```java
User user = (User)(nac.getBean("user"));//需要强制转换
User user = nac.getBean("user",User.class);//不需要转换
```
> 需要注意：保证每个类有无参构造器，否则无法被实例化

> 如何注入bean中的字段
* 平常的注入
Noyark-system可以在不需要构造方法和set方法情况下注入值
使用property节点注入：实例省略bean中内容
```xml
<bean>
	<property name="" value=""/>
</bean>
```
其中要保证`name`指定的字段是存在的，否则也会抛出异常
`value`只能指定基本类型和字符串，如果要导入bean，可以使用`ref`
```xml
<bean>
	<property name="" ref="beanid">
</bean>
```
其中对于bean的先后顺序没要求
* 注入集合
这里支持集合标签的注入
需要在property关键字中进行
```xml
<bean>
	<property name="">
		<list>
			<value>value</value>
		</list>
	</property>
</bean>
```
noyarksystem支持集合注入bean
```xml
<bean>
	<property name="" type="泛型类型">
		<list>
			<value ref="beanid"/>
		</list>
	</property>
</bean>
```
数组和list的注入方式相同，同时共用array和list节点，他们是等价的，注入数组可以使用这种方式，但是保证类型一致
noyarksystem支持set集合，仅将list换成set即可，也支持注入bean

noyark-system支持map集合注入
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
也支持注入bean
```xml
<bean>
	<property name="" key-type=“” value-type=“”>
		<map>
			<entry key="" ref=“”>
			</entry>
		</map>
	</property>
</bean>
```
同时，支持Property对象的注入
```xml
<bean>
<property name="">
	<props>
		<prop key="" value="">
	</props>
</property>
</bean>
```
也支持构造方法注入：
```xml
<bean>
	<constructor-value value="" type=""></constructor-value>
</bean>
```
Value是构造方法属性的值，type是该属性的类型，容器会根据constructor先后顺序和type找到对应的构造方法，并对参数对应类型进行赋值
* 导入Properties文件，注入到对象
```xml
<util:properties id=“” location=“classpath:xxxx”>
```
如何在bean中使用，请看下面的注入表达式
> 注入时注意，应当确定存在这个属性，并且类型对应
* 注入表达式：
noyark-system支持注入表达式，语法
`#{bean.property.propertyInside}`
通过property等的value=‘’“来指定即可
property对象：beanID.property的name.propertyKey
普通值：beanID.fieldname
set，数组，list: beanID.name[index]
map值：beanID.mapName.key
导入的properties:propertiesID.key
* 别名机制
你可以为一个bean起别名
```xml
<alias id="beanid" alias="alias">
```
但是之后不能有重复别名名字的bean，且确定之前没有重名的bean属性
# NoyarkSystem MVC
noyarksystem支持mvc功能，基于之前介绍的bean容器和servlet的转发功能实现：
之前已经提到的以下web.xml配置模式：
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
`指定包路径：`
将被扫描的包指定好noyark.xml
```xml
<context:component-scan base-package=“net.noyark.www”/>
```
之后即可开始
* 认识注解
@RequestMapping
@AutoWired
@ExceptionHandle
- RequestMapping注解的作用
该注解可以放在两个位置，类上和方法上
```java
@Controller
@RequestMapping("start")
public class Controller{
	@RequestMapping("test.no")
	public String test(HttpServletRequest req){
		System.out.println("进入test.no");
		return null;
	}
}
```
那么路径默认为start/test.no
如果我们访问http://localhost:8080/项目名/start/test.no
控制台会输出`进入test.no` 然后404，说明框架配置成功
关于RequestMapping下的方法:
目前版本只支持返回String，虽然返回其他对象也不报错，但是`不会起作用` 返回的值就是转发到的路径，如果在返回值之前加`redirect:`，则就是重定向
加在类上就会以此作为父路径
- 关于AutoWired注解
它的作用位置在字段上，可以自动注入这个字段上的值
```java
@AutoWired
public User user;
```
- 关于ExceptionHandle注解
ExceptionHandle注解会在RequestMapping下方法抛出异常时，跳转到ExceptionHandle下方法进行处理，需要指定异常类型或者异常父类类型
必须要带Exception参数
```java
@ExceptionHandle(Exception.class)
public String handleException(Exception e){
	//...
	return null;
}
```
