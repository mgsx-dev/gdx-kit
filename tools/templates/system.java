package ${package}.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Storable;

@Storable("${plugin}.${name}")
@EditableSystem
public class ${classname} extends IteratingSystem
{
    // @Inject
    // public AnotherSystem dependency;
    
    // @Editable
    // public int max = 360;
    
    // public transient ...
    
    public ${classname}() 
    {
        super(Family.all().get(), GamePipeline.DEFAULT);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) 
    {
        
    }
    
}
