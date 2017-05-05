package ${package}.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("${plugin}.${name}")
@EditableComponent(autoClone=true)
public class ${classname} implements Component
{
    
    public final static ComponentMapper<${classname}> components = ComponentMapper.getFor(${classname}.class);
    
    // use transient modifier to prevent storage of some fields
    // public transient float runtimeValue;

    // @Editable
    // public float max = 10;
    
}
