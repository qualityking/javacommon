package org.mockingbird;

import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.control.CompilationFailedException;

public class TestRunner {
	static GroovyClassLoader classLoader;
	
	static int TestCaseCount = 0; 
	static int failedCount = 0; 
	static PrintWriter writer;
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String TestCasePath = args[0];// "test.MyTest";

		try {
			writer = new PrintWriter("Reporting/tmp/tmpreport.txt");
			writer.println("{id:1, pId:0, name:'Results',type:'group'}");
			writer.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		System.out.println();
		System.out.println("Execution Started*********");
		System.out.println();

		classLoader = new GroovyClassLoader();
		loadClasses(TestCasePath);
		runTestCase();

		
		System.out.println("--------------------------------------------------");
		System.out.println("TOTAL TEST CASE EXECUTED : " + TestCaseCount);
		System.out.println("PASS : " + (TestCaseCount - failedCount) + " | FAIL : " + failedCount);
		System.out.println();
		System.out.println("********Test Execution Completed*********");

	}

	private static void loadClasses(String path) {
		try {
			if (new File(path).exists()) {
				if (new File(path).isFile()) {
					classLoader.parseClass(new File(path));
				} else {
					List<String> fileList = new ArrayList<String>();
					walk(path, fileList);
					for (String string : fileList) {
						classLoader.parseClass(new File(string));
					}
				}
			}

		} catch (CompilationFailedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}

	private static void walk(String path, List<String> fileList) {
		File root = new File(path);
		File[] list = root.listFiles();
		if (list == null)
			return;
		for (File f : list) {
			if (f.isDirectory()) {
				walk(f.getAbsolutePath(), fileList);
			} else {
				if(f.getAbsoluteFile().toString().split("\\.")[1].toLowerCase().equals("groovy")){
					fileList.add(f.getAbsoluteFile().toString());	
				}
			}
		}
	}

	private static void runTestCase() {
		Class[] classes = classLoader.getLoadedClasses();

		for (Class aClass : classes) {
			System.out.println("--------------------------------------------------");
			System.out.println("Test Case Started : " + aClass.getName());
			//writer.println("The first line");
			
			TestCaseCount++;
			boolean isFailed = false; 
			for (Method method : aClass.getDeclaredMethods()) {

				if (method.isAnnotationPresent(Step.class)) {

					Annotation annot = method.getAnnotation(Step.class);
					Step annotTest = (Step) annot;
					try {
						System.out.println("--------------------------------------------------");
						System.out.println("Running Test Step :  " + method.getName());
						System.out.println("desc :" + annotTest.value());

						method.invoke(aClass.newInstance());

					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
						// TODO Auto-generated catch block
						System.out.flush();
						System.err.println(e.getCause().toString());
						System.err.flush();
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						isFailed=true; 
					}
				}
			}
			
			if(isFailed){
				failedCount++;
			}
			
			
		}
		System.out.println("--------------------------------------------------");

	}
	
	private void addTestCase(){
		
	}

}
