package net.mgsx.game.core.helpers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ReflectionHelper {

	@SuppressWarnings("serial")
	public static class ReflectionError extends Error
	{
		public ReflectionError(Throwable e) {
			super(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T get(Object object, Field field, Class<T> type)
	{
		return (T)get(object, field);
	}
	public static Object get(Object object, Field field)
	{
		try {
			return field.get(object);
		} catch (IllegalArgumentException e) {
			throw new ReflectionError(e);
		} catch (IllegalAccessException e) {
			throw new ReflectionError(e);
		}
	}
	public static <T> void set(Object object, Field field, T value)
	{
		try {
			field.set(object, value);
		} catch (IllegalArgumentException e) {
			throw new ReflectionError(e);
		} catch (IllegalAccessException e) {
			throw new ReflectionError(e);
		}
	}
	public static Field field(Class type, String field) {
		try {
			return type.getField(field);
		} catch (NoSuchFieldException e) {
			throw new ReflectionError(e);
		} catch (SecurityException e) {
			throw new ReflectionError(e);
		}
	}
	public static <T> T newInstance(Class<? extends T> type) {
		try {
			return (T)type.newInstance();
		} catch (InstantiationException e) {
			throw new ReflectionError(e);
		} catch (IllegalAccessException e) {
			throw new ReflectionError(e);
		}
	}
	public static <T> T newInstance(String className) {
		try {
			return (T)Class.forName(className).newInstance();
		} catch (InstantiationException e) {
			throw new ReflectionError(e);
		} catch (IllegalAccessException e) {
			throw new ReflectionError(e);
		} catch (ClassNotFoundException e) {
			throw new ReflectionError(e);
		}
	}
	/**
	 * Type check version
	 * @param className
	 * @param type
	 * @return null if instance doesn't match type
	 */
	public static <T> T newInstance(String className, Class<T> type) {
		try {
			Object object = Class.forName(className).newInstance();
			if(type.isInstance(object)){
				return (T)object;
			}
			return null;
		} catch (InstantiationException e) {
			throw new ReflectionError(e);
		} catch (IllegalAccessException e) {
			throw new ReflectionError(e);
		} catch (ClassNotFoundException e) {
			throw new ReflectionError(e);
		}
	}
	public static Class forName(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new ReflectionError(e);
		}
	}
	public static boolean hasName(String className) {
		try {
			Class.forName(className);
		} catch (ClassNotFoundException e) {
			return false;
		}
		return true;
	}
	public static Method method(Class type, String name, Class ...parameterTypes) {
		try {
			return type.getMethod(name, parameterTypes);
		} catch (NoSuchMethodException e) {
			return null; // not found
		} catch (SecurityException e) {
			throw new ReflectionError(e);
		}
	}
	/** note : return null if return type is void */
	public static Object invoke(Object obj, Method method, Object...args) {
		try {
			return method.invoke(obj, args);
		} catch (IllegalAccessException e) {
			throw new ReflectionError(e);
		} catch (IllegalArgumentException e) {
			throw new ReflectionError(e);
		} catch (InvocationTargetException e) {
			throw new ReflectionError(e);
		}
	}
	public static <T> T copy(T out, T in) 
	{
		for(Field field : out.getClass().getFields()){
			if(Modifier.isPublic(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())){
				set(out, field, get(in, field));
			}
		}
		return out;
	}
	/**
	 * Check if left type is same type or subtype of right type using same conventions
	 * as java instanceof expression but apply to type.
	 * 
	 * @param left
	 * @param right
	 * @return
	 */
	public static boolean instanceOf(Class left, Class right) {
		return left.isAssignableFrom(right);
	}
}
