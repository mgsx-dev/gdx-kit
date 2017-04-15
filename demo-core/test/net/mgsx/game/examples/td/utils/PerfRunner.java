package net.mgsx.game.examples.td.utils;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.AssumptionViolatedException;
import org.junit.Before;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;


public class PerfRunner extends Runner
{
	private Class cls;
	private Map<Method, Description> tests;
	private Description root;
	
	@Retention(RUNTIME)
	@Target({METHOD})
	public static @interface PerfTest 
	{
		int count() default 1;
		
		int memory() default 1<<16;
		
		float timeout() default 10f;
		
		/** name in the editor, default is field/type name */
		String value() default "";
		
	}
	
	
	public PerfRunner(Class cls) {
		super();
		this.cls = cls;
	}
	
	@Override
	public Description getDescription() {
		tests = new HashMap<Method, Description>();
		// TODO scan perf annotation
		root = Description.createSuiteDescription(cls);
		
		for(Method m : cls.getDeclaredMethods()){
			PerfTest a = m.getAnnotation(PerfTest.class);
			if(a == null) continue;
			Description cd = Description.createTestDescription(cls, m.getName());
			tests.put(m, cd);
			root.addChild(cd);
		}
		
		return root;
	}
	
	

	@Override
	public void run(RunNotifier notifier) {
		
		notifier.fireTestStarted(root);
		
		int failures = 0;
		
		Object inst;
		try {
			inst = cls.newInstance();
			
			
			
			for(Entry<Method, Description> entry : tests.entrySet())
			{
				for(Method bm : cls.getDeclaredMethods()){
					Before a = bm.getAnnotation(Before.class);
					if(a == null) continue;
					bm.invoke(inst);
				}
				
				Method m = entry.getKey();
				PerfTest a = m.getAnnotation(PerfTest.class);
				
				Description mDesc = entry.getValue();
				
				notifier.fireTestStarted(mDesc);
				
				System.gc();
				MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
				long pm = memory.getHeapMemoryUsage().getUsed();
				long p = System.nanoTime();
				for(int i=0 ; i<a.count() ; i++){
					
					m.invoke(inst);
					
				}
				long t = System.nanoTime();
				double time = (double)(t - p) / 1e9;
				long am = memory.getHeapMemoryUsage().getUsed();
				long dm = am - pm;
				if(dm > a.memory()){
					notifier.fireTestAssumptionFailed(new Failure(mDesc, new AssumptionViolatedException("memory : " + String.valueOf(dm) + "/" + String.valueOf(a.memory()))));
					failures++;
				}
				else if(time > a.timeout()){
					notifier.fireTestAssumptionFailed(new Failure(mDesc, new AssumptionViolatedException("timeout : " + String.valueOf(time) + "/" + String.valueOf(a.timeout()))));
					failures++;
				}else{
					notifier.fireTestFinished(mDesc);
				}
			}
		} catch (InstantiationException e) {
			throw new Error(e);
		} catch (IllegalAccessException e) {
			throw new Error(e);
		} catch (IllegalArgumentException e) {
			throw new Error(e);
		} catch (InvocationTargetException e) {
			throw new Error(e);
		}
		
		if(failures > 0)
			notifier.fireTestAssumptionFailed(new Failure(root, new AssumptionViolatedException("failed")));
		else 
			notifier.fireTestFinished(root);
		
	}

}
