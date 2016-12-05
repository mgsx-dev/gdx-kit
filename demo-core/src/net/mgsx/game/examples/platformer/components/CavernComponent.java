package net.mgsx.game.examples.platformer.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("example.platformer.cavern")
@EditableComponent(name="Cavern Logic")
public class CavernComponent implements Component
{
	public float time;
	public TextureAttribute insectPathEffect;
	public boolean ready;
	public Array<Node> bigEyes;
	public Array<Node> smalllEyes;
	public AnimationController ctrl;
}
