package net.mgsx.kit.demo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;

import android.os.Bundle;
import net.mgsx.pd.utils.PdRuntimeException;

public class CameraMatrixSlaveAndroidLauncher extends AndroidApplication {
	
	private OSCPortOut sender;
	private String sendHost = "192.168.0.49";
	private int sendPort = 3100;
	private CameraMatrixProviderAndroid cameraMatrixProvider;
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		cameraMatrixProvider = new CameraMatrixProviderAndroid(this);
		
		try {
			sender = new OSCPortOut(InetAddress.getByName(sendHost), sendPort);
		} catch (SocketException e) {
			throw new PdRuntimeException(e);
		} catch (UnknownHostException e) {
			throw new PdRuntimeException(e);
		}
		
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new Game() {
			@Override
			public void create() {
			}
			@Override
			public void render() {
				cameraMatrixProvider.update();
				
				Collection<Object> args = new ArrayList<Object>();
			    args.add(cameraMatrixProvider.getAzymuth());
			    args.add(cameraMatrixProvider.getPitch());
			    args.add(cameraMatrixProvider.getRoll());
				OSCMessage msg = new OSCMessage("/matrix", args);
				try {
					sender.send(msg);
				} catch (IOException e) {
					throw new PdRuntimeException(e);
				}
			}
		}, config);
	}
}
