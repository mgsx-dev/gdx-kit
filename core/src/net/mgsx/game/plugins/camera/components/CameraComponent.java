package net.mgsx.game.plugins.camera.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Camera;

public class CameraComponent implements Component
{
	
	public static ComponentMapper<CameraComponent> components = ComponentMapper.getFor(CameraComponent.class);
	
	public Camera camera;
	public boolean frustumDirty;
}
