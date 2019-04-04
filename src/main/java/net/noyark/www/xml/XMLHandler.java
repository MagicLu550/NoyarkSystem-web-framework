package net.noyark.www.xml;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.DocumentException;
import org.dom4j.Element;


import cn.gulesberry.www.exception.IllegalMappingException;
import cn.gulesberry.www.exception.IndexLengthException;
import cn.gulesberry.www.helper.InstanceQueryer;
import net.noyark.www.annotations.Param;
import net.noyark.www.core.Assert;
import net.noyark.www.core.LazyInstance;
import net.noyark.www.core.NoyarkApplicationContext;
import net.noyark.www.core.ProtoTypeInstance;
import net.noyark.www.exception.AliasException;
import net.noyark.www.exception.AttributeException;
import net.noyark.www.exception.NoSuchBeanException;
import net.noyark.www.exception.NoneStaticException;
import net.noyark.www.interf.XMLDomFile;

public class XMLHandler {
	private List<String> scanPackage = new ArrayList<>();
	private Map<String, Object> objPool = new HashMap<String, Object>();
	private Map<Object, Method> obj_despory = new HashMap<Object, Method>();
	private Map<Object,Map<Object,Object>> properties_pool = new HashMap<>();
	private String file;
	public XMLHandler(String file,boolean... isStream) throws Exception{
		this.file = file;
		readBeanXML(isStream);
	}
	void readBeanXML(boolean... isStream) throws Exception {
		XMLDomFile xdf = InstanceQueryer.getDefaultXml(file,this,isStream);
		xdf.clearMapping();
		parsePropertiesFile(xdf);//properties file
		parseCommonBean(xdf);//解析只有id和name的bean
		parseFactoryBean(xdf);//解析有工厂方法的
		ParseOtherBeanFactoryBean(xdf);//对象工厂
		alias(xdf);//别名
		parseScope(xdf);//scope
		initAndDesporyBean(xdf);//despory
		setProperty(xdf);//property
		lazyInstance(xdf);//lazy
		addSet(xdf);//set
		addMap(xdf);//map
		setListArray(xdf);//list and array
		setProps(xdf);//props
		parseConstructor(xdf);//constructor
		parsePackageScan(xdf);
	}
	//普通的bean
	private void parseCommonBean(XMLDomFile xdf) throws IllegalMappingException, IndexLengthException, DocumentException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		List<Element> list = xdf.getElementsByAttributesAndNameOnlyThese("bean","id","class");
		list.addAll(xdf.getElementsByAttributesAndNameOnlyThese("bean","name","class"));
		for(Element e:list) {
			Attribute id = e.attribute("id")==null?e.attribute("name"):e.attribute("id");
			String idValue = id.getValue();
			Attribute clazz = e.attribute("class");
			String clazzValue = clazz.getValue();
			Object instance = Class.forName(clazzValue).newInstance();
			Assert.notRepeat(objPool, idValue);
			objPool.put(idValue, instance);
		}
	}
	//factory-method
	private void parseFactoryBean(XMLDomFile xdf) throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		List<Element> list = xdf.getElementsByAttributesAndNameOnlyThese("bean","id","class","factory-method");
		list.addAll(xdf.getElementsByAttributesAndNameOnlyThese("bean","name","class","factory-method"));
		for(Element e:list) {
			Attribute id = e.attribute("id")==null?e.attribute("name"):e.attribute("id");
			Attribute clazzAttr = e.attribute("class");
			String clazzValue = clazzAttr.getValue();
			Attribute factory = e.attribute("factory-method");
			String factoryValue = factory.getValue();
			Class<?> clz = Class.forName(clazzValue);
			Method factoryMethod = clz.getDeclaredMethod(factoryValue);
			try {
				Assert.notRepeat(objPool, id.getValue());
				Object o = factoryMethod.invoke(null);
				objPool.put(id.getValue(),o);
			}catch(Exception ex){
				throw new NoneStaticException("the factory method is not static");
			}
		}
	}
	//factory-class method
	private void ParseOtherBeanFactoryBean(XMLDomFile xdf) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		List<Element> list = xdf.getElementsByAttributesAndNameOnlyThese("bean","id","factory-bean","factory-method");
		list.addAll(xdf.getElementsByAttributesAndNameOnlyThese("bean","name","factory-bean","factory-method"));
		for(Element e:list) {
			Attribute id = e.attribute("id")==null?e.attribute("name"):e.attribute("id");
			String factoryBean = e.attribute("factory-bean").getValue();
			Attribute factoryMethod = e.attribute("factory-method");
			Object factory_bean = objPool.get(factoryBean);
			if(factory_bean==null) {
				throw new NoSuchBeanException("no "+factoryBean+" bean");
			}
			Assert.notRepeat(objPool, id.getValue());
			Method factory_method = factory_bean.getClass().getDeclaredMethod(factoryMethod.getValue());
			Object bean = factory_method.invoke(factory_bean);
			objPool.put(id.getValue(),bean);
		}
	}
	private void alias(XMLDomFile xdf) {
		List<Element> aliases = xdf.getElementsByAttributesAndNameOnlyThese("alias");
		for(Element alias:aliases) {
			Attribute id = alias.attribute("id")==null?alias.attribute("name"):alias.attribute("id");
			if(id==null) {
				throw new AliasException("the alias don't have the default id");
			}
			Attribute aliasAttr = alias.attribute("alias");
			if(aliasAttr==null) {
				throw new AliasException("the alias don't have the alias mapping");
			}
			objPool.put(aliasAttr.getValue(),objPool.get(id.getValue()));
		}
	}
	//作用域
	private void parseScope(XMLDomFile xdf) throws ClassNotFoundException {
		List<Element> elements = xdf.getElementsByAttributesAndNameOnlyThese("bean","id","class","scope");
		elements.addAll(xdf.getElementsByAttributesAndNameOnlyThese("bean","name","class","scope"));
		for(Element e:elements) {
			Attribute id = e.attribute("id");
			if(id==null) {
				id = e.attribute("name");
			}
			String clazz = e.attribute("class").getValue();
			String scope = e.attribute("scope").getValue();
			Assert.notRepeat(objPool, id.getValue());
			if(scope.equals("prototype")) {
				objPool.put(id.getValue(),new ProtoTypeInstance(Class.forName(clazz)));
			}
		}
	}
	private void initAndDesporyBean(XMLDomFile xdf) throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		List<Element> list = xdf.getElementsByAttributesAndNameOnlyThese("bean","id","class","init-method","despory-method");
		for(Element e:list) {
			Attribute id = e.attribute("id")==null?e.attribute("name"):e.attribute("id");
			String clazz = e.attributeValue("class");
			String init_method = e.attributeValue("init-method");
			String despory_method = e.attributeValue("despory-method");
			Class<?> objClazz = Class.forName(clazz);
			Object o = objClazz.newInstance();
			Method m = objClazz.getDeclaredMethod(init_method);
			m.invoke(o);
			Assert.notRepeat(objPool, id.getValue());
			objPool.put(id.getValue(),o);
			obj_despory.put(o,objClazz.getDeclaredMethod(despory_method));
		}
	}
	@SuppressWarnings("unchecked")
	private void setProperty(XMLDomFile xdf) throws Exception {
		List<Element> allElements = xdf.getElementsByAttributesAndName("bean","id");
		allElements.addAll(xdf.getElementsByAttributesAndName("bean","name"));
		for(Element e:allElements) {
			String id = e.attributeValue("id");
			if(id==null) {
				id = e.attributeValue("name");
			}
			Object instance = objPool.get(id);
			List<Element> propertys = e.elements("property");
			if(instance!=null) {
				Class<?> clz = instance.getClass();
				for(Element p:propertys) {
					String name = p.attributeValue("name");
					Field field = clz.getDeclaredField(name);
					field.setAccessible(true);
					Object value = p.attributeValue("value");
					if(value==null) {
						value = objPool.get(p.attributeValue("ref"));
					}
					if(value!=null) {
						if(value.toString().startsWith("#{")) {
							Object result = parseExpressions(value+"", xdf);
							if(result!= value+"") {
								value = result;
							}
						}
					}
					field.set(instance,value);
					addInPool(name, value, instance);
				}
			}	
		}
	}
	public void addInPool(Object name,Object value,Object instance) {
		Map<Object, Object> property = properties_pool.get(instance)==null?new HashMap<>():properties_pool.get(instance);
		property.put(name, value);
		properties_pool.put(instance,property);
	}
	public void setListArray(XMLDomFile xdf) throws Exception{
		addCollection(xdf, "list","array",List.class);
	}
	public void lazyInstance(XMLDomFile xdf) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		List<Element> list = xdf.getElementsByAttributesAndName("bean","id","class","lazy-init");
		list.addAll(xdf.getElementsByAttributesAndName("bean","name","class","lazy-init"));
		for(Element e:list) {
			String id = e.attributeValue("id")==null?e.attributeValue("name"):e.attributeValue("id");
			Class<?> clz = Class.forName(e.attributeValue("class"));
			boolean isLazy = Boolean.parseBoolean(e.attributeValue("lazy-init"));
			Object o = objPool.get(id);
			if(o==null) {
				objPool.put(id,null);
			}
			if(isLazy) {
				objPool.put(id,new LazyInstance(clz));
			}else {
				objPool.put(id, clz.newInstance());
			}
		}
	}
	private void addSet(XMLDomFile xdf) throws Exception {
		addCollection(xdf,"set","set",Set.class);
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void addMap(XMLDomFile xdf) throws Exception {
		List<Element> allElements = xdf.getElementsByAttributesAndName("bean","id");
		allElements.addAll(xdf.getElementsByAttributesAndName("bean","name"));
		Map map;
		for(Element e:allElements) {
			String id = e.attributeValue("id")==null?e.attributeValue("name"):e.attributeValue("id");
			String classname = e.attributeValue("class");
			Class<?> cls;
			if(classname!=null) {
				cls = Class.forName(classname);
			}else {
				cls = objPool.get(id).getClass();
			}
			List<Element> properties = e.elements("property");
			for(Element property:properties) {
				if(property.attributeValue("value")==null||property.attributeValue("ref")==null) {
					String listName = property.attributeValue("name");
					Field field = cls.getDeclaredField(listName);
					field.setAccessible(true);
					Element list = property.element("map");
					if(list!=null) {
						String keyType = property.attributeValue("key-type");
						String valType = property.attributeValue("value-type");
						List<Element> values = list.elements("entry");//entry节点
						//不指定类型就是默认String-String类型
						if(keyType==null) {
							map = new LinkedHashMap<String,String>();
							for(Element entry:values) {
								String key = entry.attributeValue("key");
								String value = entry.attributeValue("value");
								if(value==null) {
									value = entry.element("value").getText();
								}
								map.put(key, value);
							}
						}else {
							map = getMap(Class.forName(keyType),Class.forName(valType));
							for(Element entry:values) {
								Object key = entry.attributeValue("key");
								Object value = entry.attributeValue("ref");
								if(value!=null) {
									Object[] vals = {key,value};
									for(Object val:vals) {
										if(val.toString().startsWith("#{")) {
											Object result = parseExpressions(val+"", xdf);
											if(result!= val+"") {
												val = result;
											}
										}
									}
								}
								if(keyType.equals("java.lang.String")) {
									map.put(key, objPool.get(value));
								}else {
									map.put(objPool.get(key),objPool.get(value));
								}
							}
						}
						field.set(objPool.get(id),map);
					}
				}
			}
		}
	}
	//动态map泛型
	private <K,V> Map<K, V> getMap(Class<K> key,Class<V> value) {
		return new LinkedHashMap<K,V>();
	}
	//动态list泛型
	public <T> List<T> getList(Class<T> cls){
		return new ArrayList<T>();
	}
	//动态Set泛型
	private <S> Set<S> getSet(Class<S> cls){
		return new LinkedHashSet<S>();
	}
	//泛型类型为Object
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void addCollection(XMLDomFile xdf,String type1,String type2,Class<?> type) throws Exception {
		List<Element> allElements = xdf.getElementsByAttributesAndName("bean","id");
		allElements.addAll(xdf.getElementsByAttributesAndName("bean","name"));
		for(Element e:allElements) {
			List<Element> properties = e.elements("property");
			String classname = e.attributeValue("class");
			Class<?> clazz;
			String id = e.attributeValue("id")==null?e.attributeValue("name"):e.attributeValue("id");
			if(classname!=null) {
				clazz = Class.forName(classname);
			}else {
				clazz = objPool.get(id).getClass();
			}
			for(Element property:properties) {
				if(property.attributeValue("value")==null||property.attributeValue("ref")==null) {
					String listName = property.attributeValue("name");
					Element ele = property.element(type1);
					if(ele==null) {
						ele = property.element(type2);
					}
					if(ele!=null) {
						List<Element> values = ele.elements("value");//有value子节点
						//获取values且每个value都有ref属性,type='type'
						String valueType = property.attributeValue("type");//有ref
						if(valueType!=null) {
							Collection listObject;
							if(type.equals(List.class)) {
								listObject = getList(Class.forName(valueType));
							}else {
								listObject = getSet(Class.forName(valueType));
							}
							Field f = clazz.getDeclaredField(listName);
							f.setAccessible(true);
							for(Element val:values) {
								Object value = val.attributeValue("ref");
								if(value.toString().startsWith("#{")) {
									Object result = parseExpressions(value+"", xdf);
									if(result!= value+"") {
										value = result;
									}
								}
								Object o = objPool.get(value);
								listObject.add(o);
							}
							f.set(objPool.get(id),listObject);
							addInPool(listName, listObject, objPool.get(id));
						}else {
							setValue(type, clazz, listName,values,id);//直接找
						}
					}
				}
			}
		}
	}
	//设XMLDomFile xdf,String type1,String type2,Class<?> type置value
	private void setValue(Class<?> type,Class<?> clazz,String listName,List<Element> values,String id) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoSuchFieldException, SecurityException {
		//当value==null
		Collection<String> listObject = null;
		if(type.equals(List.class)) {
			listObject = new ArrayList<>();
		}else {
			listObject = new LinkedHashSet<>();
		}
		for(Element value:values) {
			listObject.add(value.getText());
		}
		Field f = clazz.getDeclaredField(listName);
		if(f.getType().isArray()) {
			f.setAccessible(true);
			f.set(objPool.get(id),listObject.toArray());
			addInPool(listName,listObject.toArray(), objPool.get(id));
		}else {
			f.set(objPool.get(id),listObject);
			addInPool(listName,listObject, objPool.get(id));
		}
	}
	//Properties
	@SuppressWarnings("unchecked")
	private void setProps(XMLDomFile xdf) throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		List<Element> beans = xdf.getElementsByAttributesAndName("bean","id");
		beans.addAll(xdf.getElementsByAttributesAndName("bean","name"));
		for(Element bean:beans) {
			String classname = bean.attributeValue("class");
			String id = bean.attributeValue("id");
			if(id==null) {
				id = bean.attributeValue("name");
			}
			Class<?> clz;
			if(classname!=null) {
				clz= Class.forName(classname);
			}else {
				clz = objPool.get(id).getClass();
			}
			
			List<Element> properties = bean.elements("property");
			for(Element property:properties) {
				String propertyName = property.attributeValue("name");
				Field field = clz.getDeclaredField(propertyName);
				field.setAccessible(true);
				Element property_props = property.element("props");
				if(property_props!=null) {
					Properties propObject = new Properties();
					List<Element> props_prop = property_props.elements("prop");
					for(Element prop:props_prop) {
						String value = prop.getText();
						String key = prop.attributeValue("key");
						propObject.put(key, value);
					}
					field.set(objPool.get(id), propObject);
					addInPool(propertyName,propObject, objPool.get(id));
				}
			}
		}
	}
	private void parsePropertiesFile(XMLDomFile xdf) throws Exception {
		List<Element> properties = xdf.getElementsByNameSpace("http://www.noyark.net:8081/util",true);
		for(Element e:properties) {
			if(e.getName().equals("properties")) {
				String location = e.attributeValue("location");
				String filename = location.substring("classpath:".length());
				if(location.startsWith("classpath:")) {
					location = (this.getClass().getResource("/").getPath()+location.substring("classpath:".length())).replaceAll("test-classes", "classes");
				}
				Assert.notNull(location,new AttributeException("No location attribute->util:properties"));
				String id = e.attributeValue("name");
				if(id ==null) {
					id = filename;
				}
				Properties props = new Properties();
				props.load(new FileInputStream(location));
				objPool.put(id,props);
			}
		}
	}
	@SuppressWarnings({ "unchecked"})
	private Object parseExpressions(String value,XMLDomFile xdf) throws Exception {
		if(value.startsWith("#{")&&value.endsWith("}")) {
			String string = value.substring(value.indexOf("{")+1,value.indexOf("}"));
			String propertyPoint = null;
			if(string.indexOf("!")!=-1) {
				String before = string;
				string = string.substring(0,string.indexOf("!")-1);
				propertyPoint = before.substring(before.indexOf("!")+1,before.lastIndexOf("!")); 
			}
			String[] expressions = string.split("\\.");
			String id = expressions[0];
			List<Element> elements = xdf.getElementsByAttribut("id",id);
			if(elements.size()==0) {
				elements = xdf.getElementsByAttribut("name",id);
			}
			//属性
			Element theBeanXML = null;
			for(Element e:elements) {
				if(e.getName().equals("bean")&&(id.equals(e.attributeValue("id"))||id.equals(e.attributeValue("name")))) {
					theBeanXML = e;
				}
			}
			String property = null;
			if(expressions.length>1) {
				property = expressions[1];
			}
			List<Element> es = xdf.getElementsByNameSpace("http://www.noyark.net:8081/util",true);
			for(Element e:es) {
				String propId = e.attributeValue("name")==null?e.attributeValue("id"):e.attributeValue("name");
				if(e.getName().equals("properties")&&id.equals(propId)) {
					Properties object = (Properties)objPool.get(propId);
					if(propertyPoint!=null) {
						return object.getProperty(propertyPoint);
					}
					return object.getProperty(property);
				}
			}
			Assert.notNull(theBeanXML, new NoSuchBeanException("no bean named "+id));
			List<Element> properties = theBeanXML.elements("property");
			for(Element prop:properties) {
				if(property.equals(prop.attributeValue("name"))) {
					String val = prop.attributeValue("value");
					if(val!=null) {
						value = val;
						return value;
					}else {
						String ref = prop.attributeValue("ref");
						if(ref!=null) {
							Object o = objPool.get(ref);//获取ref指向的
							return o;
						}else {
							String entryName = expressions[2];
							//不指向ref也不指向value,但没有[],就是map或者props
							Object informationMap = getMapInformation(prop, entryName, "map", "entry");
							if(informationMap==null) {
								Object informationProperties = getMapInformation(prop, entryName, "props", "prop");
								if(informationProperties!=null) {
									return informationProperties;
								}
							}else{
								return informationMap;
							}
					}						
				}
			}else if(property.indexOf("[")!=-1&&property.substring(0,property.indexOf("[")).equals(prop.attributeValue("name"))) {
					Integer index = Integer.parseInt(property.substring(property.indexOf("[")+1,property.indexOf("]")));
					Element element = prop.element("list");
					if(element == null) {
						element = prop.element("array");
					}
					if(element == null) {
						element = prop.element("set");
					}
					Object object = getListOrSet(element, index);
					if(object!=null) {
						return object;
					}
				}
			}
		}
		return value;
	}
	@SuppressWarnings("null")
	private Object getMapInformation(Element prop,String entryName,String parent,String child) throws Exception {
		Element map = prop.element(parent);//map
		if(map!=null) {
			@SuppressWarnings("unchecked")
			List<Element> entrys = map.elements(child);//entry
			for(Element entry:entrys) {//entry节点
				String entryNameTruly =entry.attributeValue("name");
				if(entryNameTruly==null) {
					entryNameTruly = entry.attributeValue("key");
				}
				if(entryName.equals(entryNameTruly)) {
					String entryText = entry.getTextTrim();
					if(entryText!=null||!entryText.equals("")) {
						return entryText;
					}
					String entryValue = entry.attributeValue("value");
					if(entryValue==null) {
						String entryRef = entry.attributeValue("ref");
						if(entryRef!=null) {//entryRef
							Object bean = objPool.get(entryRef);
							Assert.notNull(bean, new NoSuchBeanException("no such bean named "+entryRef));
							return bean;
						}else {//那就是在下一个标签里
							String bean = entry.element("value").getText();
							return bean;
						}
					}else {
						return entryValue;
				}
			}
		}
	}
		return null;
	}
	@SuppressWarnings("unchecked")
	private Object getListOrSet(Element element,int index) throws Exception {
		List<Element> values = element.elements("value");
		int i = 0;
		for(Element val:values) {
			if(i == index) {
				if(val.attribute("ref")==null) {
					return val.getText();
				}else {
					String ref = val.attributeValue("ref");
					Object bean = objPool.get(ref);
					Assert.notNull(bean, new NoSuchBeanException("the bean named "+ref+" is not found"));
					return bean;
				}
			}
			i++;
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	private void parseConstructor(XMLDomFile xdf) throws Exception {
		List<Element> elements = xdf.EPathSelector("bean [name]");
		for(Element e:elements) {
			String id = e.attributeValue("id")==null?e.attributeValue("name"):e.attributeValue("id");
			Object object = objPool.get(id);
			Class<?> objCls = object.getClass();
			List<Element> values = e.elements("constructor-value");
			List<Object> keys = new ArrayList<>();
			Map<Object,Class<?>> vals = new HashMap<>();
			for(Element val:values) {
				Object value = val.attributeValue("value");
				if(value==null) {
					value = objPool.get(val.attributeValue("ref"));
				}
				if(value!=null) {
					if(value.toString().startsWith("#{")) {
						Object result = parseExpressions(value+"", xdf);
						if(result!= value+"") {
							value = result;
						}
					}
				}
				vals.put(value,Class.forName(val.attributeValue("type")));
				keys.add(value);
			}
			Class<?>[] clzs = vals.values().toArray(new Class<?>[vals.size()]);
			Constructor<?> constructor = objCls.getConstructor(clzs);
			Parameter[] parameter = constructor.getParameters();
			int i = 0;
			for(Parameter p:parameter) {
				String fieldName = p.getAnnotation(Param.class).value();
				Field f = objCls.getDeclaredField(fieldName);
				f.setAccessible(true);
				f.set(object,keys.get(i));
				i++;
			}
		}
	}
	public void autowiredBean(XMLDomFile xdf) {
		List<Element> elements = xdf.EPathSelector("bean autowired [name,key]");
		for(Element e:elements) {
			String id = e.attributeValue("id")==null?e.attributeValue("name"):e.attributeValue("id");
			String autowired = e.attributeValue("autowried");
			if(autowired.equals("getByname")) {
				Map<String, XMLHandler> all = NoyarkApplicationContext.getHandlers();
				Collection<XMLHandler> coll = all.values();
				for(XMLHandler xh:coll) {
					Object obj = xh.getBeans().get(id);
					objPool.put(id,obj);
				}
			}else if(autowired.equals("getByType")){
				Collection<Object> collection = objPool.values();
				for(Object o:collection) {
					if(o.getClass().getName().equals(e.attributeValue("class"))) {
						objPool.put(id,o);
					}
				}
			}
		}
	}
	private void parsePackageScan(XMLDomFile xdf) {
		List<Element> list = xdf.getElementsByNameSpace("http://www.noyark.net:8081/context",true);
		for(Element e:list) {
			if(e.getName().equals("component-scan")) {
				scanPackage.add(e.attributeValue("base-package"));
			}
		}
	}
	public Map<String, Object> getBeans(){
		return objPool;
	}
	public Map<Object, Method> getDesporyMethod(){
		return obj_despory;
	}
	public List<String> getPackage() {
		return scanPackage;
	}
}
