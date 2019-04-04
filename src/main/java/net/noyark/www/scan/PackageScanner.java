package net.noyark.www.scan;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import cn.gulesberry.www.io.XMLDocument;
import net.noyark.www.annotations.Component;
import net.noyark.www.annotations.Controller;
import net.noyark.www.annotations.Repository;
import net.noyark.www.annotations.Service;
import net.noyark.www.xml.XMLHandler;

public class PackageScanner {
	private XMLHandler handler;
	public PackageScanner(XMLHandler handler) {
		this.handler = handler;
	}
	public void scanPackage() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		List<String> allPackages = handler.getPackage();
		for(String pack:allPackages) {
			String classPath = this.getClass().getResource("/").getPath();
			String otherPath;
			if(classPath.indexOf("test-classes")!=-1) {
				otherPath = classPath.replace("test-classes","classes");
			}else {
				otherPath = classPath.replace("classes","test-classes");
			}
			String[] allPath = {classPath,otherPath};
			for(String path:allPath) {
				String packagePath = path+pack.replaceAll(XMLDocument.POINT,"/");
				File file = new File(packagePath);
				List<File> fileName = loadClass(file, packagePath);
				for(File f:fileName) {
					File[] classFiles = f.listFiles();
					if(classFiles!=null) {
						for(File f1:classFiles) {
							if(f1.getName().endsWith(".class")) {
								String classpath = f1.getPath();
								classpath = classpath.substring(classpath.indexOf("classes")+"classes".length()+1,classpath.indexOf(".class")).replaceAll("/",".");
								Class<?> clz = Class.forName(classpath);
								Annotation[] annotations = clz.getDeclaredAnnotations();
								for(Annotation a:annotations) {
									String name;
									if(a instanceof Component) {
										name = ((Component)a).value();
									}else if(a instanceof Controller){
										name = ((Controller)a).value(); 
									}else if(a instanceof Service) {
										name = ((Service)a).value(); 
									}else if(a instanceof Repository) {
										name = ((Repository)a).value(); 
									}else {
										name = null;
									}
									if(name!=null) {
										if(name.equals("")) {
											name = defaultBeanName(name, f1);
										}
										handler.getBeans().put(name,clz.newInstance());
									}	
								}	
							}
						}
					}
				}
			}
		}
	}
	private String defaultBeanName(String name,File f1) {
		name = f1.getName().substring(0,f1.getName().lastIndexOf("."));
		String s = (name.charAt(0)+"").toLowerCase();
		name = s+name.substring(1);
		return name;
	}
	private List<File> loadClass(File file,String pack) {
		File[] files = file.listFiles();
		List<File> allFiles = new ArrayList<File>();
		allFiles.add(file);
		searchFile(files, allFiles);
		return allFiles;
	}
	private void searchFile(File[] files,List<File> allFiles) {
		if(files!=null) {
			for(File f:files) {
				if(f.isDirectory()){
					allFiles.add(f);
					File[] files2 = f.listFiles();
					searchFile(files2, allFiles);
					}
				}
			}
	}
}
