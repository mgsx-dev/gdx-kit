package net.mgsx.examples.platformer;

import java.net.SocketException;
import java.util.Date;

import com.illposed.osc.AddressSelector;
import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;

import net.mgsx.game.examples.openworld.systems.OpenWorldCameraSystem.CameraMatrixProvider;
import net.mgsx.pd.utils.PdRuntimeException;

public class CameraMatrixProviderRemote implements CameraMatrixProvider
{
	private OSCPortIn receiver;
	private int recvPort = 3100;
	
	private volatile float azymuth, pitch, roll;

	public CameraMatrixProviderRemote() {
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
		if(message.getAddress().equals("/matrix")){
			azymuth = (Float)message.getArguments().get(0);
			pitch = (Float)message.getArguments().get(1);
			roll = (Float)message.getArguments().get(2);
		}
	}


	
	@Override
	public float getAzymuth() {
		return azymuth;
	}

	@Override
	public float getPitch() {
		return pitch;
	}

	@Override
	public float getRoll() {
		return roll;
	}

	@Override
	public void update() {
		// NOOP
	}

}
