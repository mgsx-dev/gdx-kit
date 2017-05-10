package net.mgsx.game.examples.raycast.systems;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.math.MathUtils;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;

import net.mgsx.pd.utils.PdRuntimeException;

public class CompassRemoteSystem extends EntitySystem
{
	
	private OSCPortOut sender;
	private String sendHost = "192.168.0.49";
	private int sendPort = 3100;

	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		try {
			sender = new OSCPortOut(InetAddress.getByName(sendHost), sendPort);
		} catch (SocketException e) {
			throw new PdRuntimeException(e);
		} catch (UnknownHostException e) {
			throw new PdRuntimeException(e);
		}
	}
	
	@Override
	public void removedFromEngine(Engine engine) {
		sender.close();
		super.removedFromEngine(engine);
	}
	
	@Override
	public void update(float deltaTime) 
	{
		if(Gdx.input.isPeripheralAvailable(Peripheral.Compass))
		{
			float angle = Gdx.input.getAzimuth() * MathUtils.degreesToRadians;
			// Pd.audio.sendFloat("azimuth", angle);
			
			float px = 0, py = 0;
			if(Gdx.input.isTouched()){
				px = Gdx.input.getX() / (float)Gdx.graphics.getWidth() - .5f;
				py = Gdx.input.getY() / (float)Gdx.graphics.getHeight() - .5f;
			}
			
			Collection<Object> args = new ArrayList<Object>();
			args.add(angle);
			args.add(px);
			args.add(py);
			OSCMessage msg = new OSCMessage("/azimuth", args);
			try {
				sender.send(msg);
			} catch (IOException e) {
				throw new PdRuntimeException(e);
			}
		}
	}
}
