package net.mgsx.core.helpers;

import java.lang.reflect.Field;

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
	public static Field field(Object entity, String field) {
		try {
			return entity.getClass().getField(field);
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
}
