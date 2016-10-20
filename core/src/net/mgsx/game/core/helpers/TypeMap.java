package net.mgsx.game.core.helpers;

import com.badlogic.gdx.utils.ObjectMap;

/**
 * Map from one type (or any sub type) to an instance of this type.
 * Useful for type/object registry
 * 
 * @author mgsx
 *
 * @param <T>
 */
public class TypeMap<T> extends ObjectMap<Class<? extends T>, T>
{

}
