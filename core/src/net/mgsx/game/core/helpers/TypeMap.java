package net.mgsx.game.core.helpers;

import com.badlogic.gdx.utils.OrderedMap;

/**
 * Map from one type (or any sub type) to an instance of this type.
 * Useful for type/object registry
 * 
 * @author mgsx
 *
 * @param <T>
 */
public class TypeMap<T> extends OrderedMap<Class<? extends T>, T>
{

}
