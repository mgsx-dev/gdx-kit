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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;

import com.badlogic.gdx.utils.ObjectMap;

/**
 * Generate markdown tutorial from sources.
 * 
 * This helps to maintain tutorials/documentation on
 * refactoring and code/API changes.
 * 
 * Usage : run this class with arguments :
 * <ul>
 * <li>-jekyll to generate Jekyll markdown header.</li>
 * <li>-path [output folder] to generate in a specific folder.</li>
 * </ul>
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
		List<String> parameters = Arrays.asList(args);
		Reflections ref = new Reflections(TutorialGenerator.class.getPackage().getName() + ".chapters", new TypeAnnotationsScanner());
		for(Class<?> type : ref.getTypesAnnotatedWith(Tutorial.class)){
			Tutorial config = type.getAnnotation(Tutorial.class);
			String fileName = config.id() + ".md";
			tutorial(fileName, type);
		}
		
		generate(parameters);
	}
	
	private static void generate(List<String> parameters) throws Exception
	{
		boolean dry = parameters.contains("-dry");
		String path = "tutorial-gen/";
		if(parameters.contains("-path")){
			path = parameters.get(parameters.indexOf("-path")+1);
		}
		new File(path).mkdirs();
		
		for(int i=0 ; i<tutorials.size() ; i++)
		{
			String name = names.get(i);
			
			List<Class<?>> tutorial = tutorials.get(i);
			Writer writer;
			if(dry){
				System.out.println("File would be generated at : " + path + "/" + name);
				writer = new StringWriter();
			}else{
				System.out.println("Generate file : " + path + "/" + name);
				writer = new FileWriter(path + "/" + name);
			}
			
			for(Class<?> type : tutorial){
				generate(new PrintWriter(writer), type, parameters);
			}
			
			if(dry){
				System.out.println(writer.toString());
			}else{
				writer.close();
			}
		}
	}
	
	private static String resolveLinks(String line, ObjectMap<String, String> imports){
		String rx = "\\{@link ([A-Za-z\\.]+)#?([A-Za-z]+)?(\\(.*\\))?\\}";

		StringBuffer sb = new StringBuffer();
		Pattern p = Pattern.compile(rx);
		Matcher m = p.matcher(line);

		while (m.find())
		{
			String fqName = m.group(1);
			String typeName;
			if(!fqName.contains(".")){
				typeName = fqName;
				fqName = imports.get(typeName);
			}else{
				typeName = fqName.substring(fqName.lastIndexOf('.')+1);
			}
			String methodName = m.group(2);
			String url = null;
			if(fqName.startsWith("com.badlogic")){
    			url = "http://libgdx.badlogicgames.com/nightlies/docs/api/";
    		}else if(fqName.startsWith("net.mgsx")){
    			url = "http://kit.mgsx.net/docs/api/";
    		}
			url += fqName.replaceAll("\\.", "/");
			url += ".html";
			if(methodName != null){
				url += "#" + methodName;
				url += "-";
				// TODO arguments types
				url += "-";
			}
			String mdString = "[" + typeName;
			if(methodName != null){
				mdString += "." + methodName;
			}
			mdString += "](" + url + ")";
		    m.appendReplacement(sb, mdString);
		}
		m.appendTail(sb);
		return sb.toString();
	}
	
	private static void generate(PrintWriter writer, Class<?> type, List<String> parameters) throws Exception{
        FileInputStream in = new FileInputStream("tutorial/" + 
        		type.getName().replaceAll("\\.", "/") + ".java");

        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF8"));         

        Tutorial config = type.getAnnotation(Tutorial.class);
        
        if(parameters.contains("-jekyll")){
        	writer.println("---");
        	writer.println("title: \"" + config.title() + "\"");
        	writer.println("category: \"" + config.group() + "\"");
        	writer.println("key: \"" + String.format("%06d", config.order()) + "\"");
        	writer.println("---");
        }
        
        String offset = null;
        boolean md = false;
        boolean code = false;
        String line;
        int lineCount = 1;
        ObjectMap<String, String> imports = new ObjectMap<String, String>();
        while ((line = br.readLine()) != null) {
        	if(md){
        		if(line.trim().equals("md*/") || line.trim().equals("@md*/")){
        			md = false;
        		}else{
        			line = resolveLinks(line, imports);
        			
        			String stripLine = line;
        			if(offset != null && !line.trim().isEmpty()){
        				if(stripLine.startsWith(offset)){
        					stripLine = stripLine.substring(offset.length());
        				}else{
        					System.out.println("warning : indentation mismatch in " + type.toString() + " line " + lineCount);
        				}
        			}
        			writer.println(stripLine);
        		}
        	}else if(code){
        		if(line.trim().equals("//@code")){
        			code = false;
        			writer.println();
        			writer.println("```");
        			writer.println();
        		}else{
        			String stripLine = line;
        			if(offset != null && !line.trim().isEmpty()){
        				if(stripLine.startsWith(offset)){
        					stripLine = stripLine.substring(offset.length());
        				}else{
        					System.out.println("warning : indentation mismatch in " + type.toString() + " line " + lineCount);
        				}
        			}
        			writer.println(stripLine);
        		}
        	} else if(line.trim().equals("/**@md")){
        		String [] splitted = line.split("/\\*\\*@md");
        		offset = splitted.length > 0 ? splitted[0] : null;
        		md = true;
        	} else if(line.trim().equals("//@code")){
        		code = true;
        		String [] splitted = line.split("//@code");
        		offset = splitted.length > 0 ? splitted[0] : null;
        		writer.println();
        		writer.println("```java");
        		writer.println();
        	} else if(line.startsWith("import ")) {
        		String fqName = line.replaceAll("import (.+);", "$1");
        		String [] packageNames = fqName.split("\\.");
        		String typeName = packageNames[packageNames.length-1];
        		imports.put(typeName, fqName);
        	}
        	lineCount++;
        }
        br.close();
        
		
	}

	

}
