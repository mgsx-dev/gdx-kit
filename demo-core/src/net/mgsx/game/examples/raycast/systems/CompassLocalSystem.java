package net.mgsx.game.examples.raycast.systems;

import java.net.SocketException;
import java.util.Date;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.MathUtils;
import com.illposed.osc.AddressSelector;
import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.pd.utils.PdRuntimeException;

@EditableSystem
public class CompassLocalSystem extends EntitySystem
{
	private OSCPortIn receiver;
	private int recvPort = 3100;
	
	@Editable(realtime=true)
	public float x;
	@Editable(realtime=true)
	public float y;
	
	@Editable(realtime=true)
	public float azymuth;
	@Editable(realtime=true)
	public float dx;
	@Editable(realtime=true)
	public float dy;
	
	@Editable
	public float speed = 5;
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		try {
			receiver = new OSCPortIn(recvPort);
			receiver.addListener(new AddressSelector() {
				@Override
				public boolean matches(String messageAddress) {
					return true;
				}
			}, new OSCListener() {
				@Override
				public void acceptMessage(Date time, OSCMessage message) 
				{
					try{
						onRemoteMessage(message);
					}catch(Throwable e){
						e.printStackTrace();
					}
				}
			});
			receiver.startListening();
		} catch (SocketException e) {
			throw new PdRuntimeException(e);
		}
	}
	
	private void onRemoteMessage(OSCMessage message) 
	{
		if(message.getAddress().equals("/azimuth")){
			this.azymuth = MathUtils.PI/2 - (Float)message.getArguments().get(0);
			this.dx = (Float)message.getArguments().get(1);
			this.dy = -(Float)message.getArguments().get(2);
		}
	}
	
	@Override
	public void removedFromEngine(Engine engine) {
		
		receiver.stopListening();
		receiver.close();
		
		super.removedFromEngine(engine);
	}
	
	@Override
	public void update(float deltaTime) 
	{
		float dx = MathUtils.cos(azymuth);
		float dy = MathUtils.sin(azymuth);

		x += dx * deltaTime * speed * this.dy;
		y += dy * deltaTime * speed * this.dy;
		
	}
}
