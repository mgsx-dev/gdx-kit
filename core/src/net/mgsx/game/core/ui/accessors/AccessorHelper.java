package net.mgsx.game.core.ui.accessors;

public class AccessorHelper {

	public static long asLong(Accessor accessor){
		if(accessor.getType() == long.class || accessor.getType() == Long.class){
			return (Long)accessor.get();
		}
		if(accessor.getType() == int.class || accessor.getType() == Integer.class){
			return (Integer)accessor.get();
		}
		if(accessor.getType() == short.class || accessor.getType() == Short.class){
			return (Short)accessor.get();
		}
		if(accessor.getType() == byte.class || accessor.getType() == Byte.class){
			return (Byte)accessor.get();
		}
		return 0;
	}

	public static void fromLong(Accessor accessor, long value) {
		if(accessor.getType() == long.class || accessor.getType() == Long.class){
			accessor.set(value);
		}
		if(accessor.getType() == int.class || accessor.getType() == Integer.class){
			accessor.set((int)value);
		}
		if(accessor.getType() == short.class || accessor.getType() == Short.class){
			accessor.set((short)value);
		}
		if(accessor.getType() == byte.class || accessor.getType() == Byte.class){
			accessor.set((byte)value);
		}
	}
}
