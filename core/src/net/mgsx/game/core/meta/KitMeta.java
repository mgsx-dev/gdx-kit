package net.mgsx.game.core.meta;

import java.lang.annotation.Annotation;

import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.ui.accessors.Accessor;

public interface KitMeta {

	Array<Accessor> accessorsFor(Object object, Class<? extends Annotation> annotatedBy);

}
