package net.mgsx.game.examples.platformer.inputs;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.helpers.MathHelper;

@Storable(value="example.platformer.player-control", auto=true)
@EditableComponent(autoClone=false)
public class PlayerController implements Component
{
	public static ComponentMapper<PlayerController> components = ComponentMapper.getFor(PlayerController.class);
	
	public boolean left, right, up, down, jump, grab, justJump, justGrab;

	public float jumpMax, jumpOrigin;
	
	public Vector2 groundRay;

	public boolean onGround, onGroundLeft, onGroundRight;

	public boolean onWallLeft, onWallRight;

	public boolean isHurt;
	
	public Fixture lastGroundFixture;
	
	public boolean canJump;
	
	/**
	 * retrieve normalized direction accoring to left/right and up/down (right and up are positive)
	 * @param direction the result
	 * @return direction for chaining
	 */
	public Vector2 getDirection(Vector2 direction) 
	{
		if(left && !right) direction.x = -1;
		else if(right && !left) direction.x = 1;
		else direction.x = 0;
		if(down && !up) direction.y = -1;
		else if(up && !down) direction.y = 1;
		else direction.y = 0;
		if(direction.x != 0 && direction.y != 0){
			direction.scl(MathHelper.SQRT2);
		}
		return direction;
	}
}
