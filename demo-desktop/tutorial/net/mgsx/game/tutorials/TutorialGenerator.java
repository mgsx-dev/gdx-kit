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

import net.mgsx.game.tutorials.tutorial1.DesktopLauncher;
import net.mgsx.game.tutorials.tutorial1.TutorialGame;

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
		
		tutorial("tutorial-01.md", DesktopLauncher.class, TutorialGame.class);
		
		generate(args.length > 0 && args[0].equals("-dry"));
	}
	
	private static void generate(boolean dry) throws Exception
	{
		for(int i=0 ; i<tutorials.size() ; i++)
		{
			String name = names.get(i);
			List<Class<?>> tutorial = tutorials.get(i);
			Writer writer;
			if(dry)
				writer = new StringWriter();
			else
				writer = new FileWriter("tutorial-gen/" + name);
			for(Class<?> type : tutorial){
				generate(new PrintWriter(writer), type);
			}
			
			if(dry){
				System.out.println("File : " + name);
				System.out.println(writer.toString());
			}else{
				writer.close();
			}
		}
	}
		
	private static void generate(PrintWriter writer, Class<?> type) throws Exception{
        FileInputStream in = new FileInputStream("tutorial/" + 
        		type.getName().replaceAll("\\.", "/") + ".java");

        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF8"));         

        boolean md = false;
        boolean code = false;
        String line;
        while ((line = br.readLine()) != null) {
        	if(md){
        		if(line.trim().equals("md*/") || line.trim().equals("@md*/")){
        			md = false;
        		}else{
        			writer.println(line);
        		}
        	}else if(code){
        		if(line.trim().equals("//code")){
        			code = false;
        			writer.println("```");
        			writer.println();
        		}else{
        			writer.println(line);
        		}
        	} else if(line.trim().equals("/*md") || line.trim().equals("/**@md")){
        		md = true;
        	}else if(line.trim().equals("//code")){
        		code = true;
        		writer.println();
        		writer.println("```java");
        	}
        	
        }
        br.close();
        
		
	}

	

}
