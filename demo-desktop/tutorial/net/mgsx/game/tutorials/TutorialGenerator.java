package net.mgsx.game.tutorials;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;

/**
 * Generate markdown tutorial from sources.
 * 
 * Choixe to do so is, for now code change a lot and impact on tutorials can be
 * fixed within code changes. This ensure to maintain tutorials/documentation on
 * refactoring and code/API changes.
 * 
 * Usage : run this class at current project root folder.
 * 
 * @author mgsx
 * 
 */
public class TutorialGenerator {

	private static List<List<Class<?>>> tutorials = new ArrayList<List<Class<?>>>();
	private static List<String> names = new ArrayList<String>();
	
	private static void tutorial(String name, Class<?>...types) {
		tutorials.add(Arrays.asList(types));
		names.add(name);
	}
	
	public static void main(String[] args) throws Exception {

		new File("tutorial-gen").mkdirs();
		
		Reflections ref = new Reflections(TutorialGenerator.class.getPackage().getName() + ".chapters", new TypeAnnotationsScanner());
		for(Class<?> type : ref.getTypesAnnotatedWith(Tutorial.class)){
			Tutorial config = type.getAnnotation(Tutorial.class);
			tutorial(config.id() + ".md", type);
		}
		
		generate(Arrays.asList(args));
	}
	
	private static void generate(List<String> parameters) throws Exception
	{
		boolean dry = parameters.contains("-dry");
		String path = "tutorial-gen/";
		if(parameters.contains("-path")){
			path = parameters.get(parameters.indexOf("-path")+1);
		}
		
		for(int i=0 ; i<tutorials.size() ; i++)
		{
			String name = names.get(i);
			List<Class<?>> tutorial = tutorials.get(i);
			Writer writer;
			if(dry)
				writer = new StringWriter();
			else
				writer = new FileWriter(path + "/" + name);
			for(Class<?> type : tutorial){
				generate(new PrintWriter(writer), type, parameters);
			}
			
			System.out.println("File : " + name);
			if(dry){
				System.out.println(writer.toString());
			}else{
				writer.close();
			}
		}
	}
		
	private static void generate(PrintWriter writer, Class<?> type, List<String> parameters) throws Exception{
        FileInputStream in = new FileInputStream("tutorial/" + 
        		type.getName().replaceAll("\\.", "/") + ".java");

        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF8"));         

        Tutorial config = type.getAnnotation(Tutorial.class);
        
        if(parameters.contains("-jekyll")){
        	writer.println("---");
        	writer.println("title: \"" + config.title() + "\"");
        	writer.println("---");
        }
        
        boolean md = false;
        boolean code = false;
        String line;
        while ((line = br.readLine()) != null) {
        	if(md){
        		if(line.trim().equals("md*/") || line.trim().equals("@md*/")){
        			md = false;
        		}else{
        			line = line.replaceAll("\\{@link ([A-Za-z]+)\\}", "[$1](http://todoc.io#$1)");
        			line = line.replaceAll("\\{@link #(.+)\\}", "*$1*");
        			line = line.trim();
        			writer.println(line);
        		}
        	}else if(code){
        		if(line.trim().equals("//@code")){
        			code = false;
        			writer.println();
        			writer.println("```");
        			writer.println();
        		}else{
        			writer.println(line);
        		}
        	} else if(line.trim().equals("/*md") || line.trim().equals("/**@md")){
        		md = true;
        	}else if(line.trim().equals("//@code")){
        		code = true;
        		writer.println();
        		writer.println("```java");
        		writer.println();
        	}
        	
        }
        br.close();
        
		
	}

	

}
