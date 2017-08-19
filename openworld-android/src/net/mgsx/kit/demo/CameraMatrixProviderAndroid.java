package net.mgsx.kit.demo;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import net.mgsx.game.examples.openworld.systems.OpenWorldCameraSystem.CameraMatrixProvider;

public class CameraMatrixProviderAndroid implements CameraMatrixProvider
{
	// https://developer.android.com/reference/android/hardware/SensorEvent.html#values
	
	private float [] orientationVals = new float[3];
	
	private float [] mMagnet = new float [3];
	private float [] mAccel = new float [3];
	private float [] mInc = new float [16];
	private float [] mRot = new float [16];
	private float [] mRot2 = new float [16];

	public CameraMatrixProviderAndroid(Activity activity) 
	{
		SensorManager sm = (SensorManager)activity.getSystemService(Context.SENSOR_SERVICE);
		sm.registerListener(new SensorEventListener() {
			
			@Override
			public void onSensorChanged(SensorEvent event) {
				for(int i=0 ; i<event.values.length && i<mMagnet.length ; i++){
					mMagnet[i] = event.values[i];
				}
			}
			
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		}, sm.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).get(0), SensorManager.SENSOR_DELAY_GAME);
		
		sm.registerListener(new SensorEventListener() {
			
			@Override
			public void onSensorChanged(SensorEvent event) {
				for(int i=0 ; i<event.values.length && i<mAccel.length ; i++){
					mAccel[i] = event.values[i];
				}
			}
			
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		}, sm.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0), SensorManager.SENSOR_DELAY_GAME);
	}
	
	@Override
	public void update(){
		SensorManager.getRotationMatrix(mRot, mInc, mAccel, mMagnet);
			
	    SensorManager.remapCoordinateSystem(mRot,
	            SensorManager.AXIS_X, SensorManager.AXIS_Z,
	            mRot2);
			
		SensorManager.getOrientation(mRot2, orientationVals);
	}
	
	@Override
	public float getAzymuth() {
		return orientationVals[0];
	}

	@Override
	public float getPitch() {
		return orientationVals[1];
	}

	@Override
	public float getRoll() {
		return orientationVals[2];
	}

}
