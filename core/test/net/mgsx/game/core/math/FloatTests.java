package net.mgsx.game.core.math;

import java.io.IOException;

import com.badlogic.gdx.math.MathUtils;

public class FloatTests {

	private static void limit(){
		// 8388609 // (1<<23)
		for(int i=0 ; i<Integer.MAX_VALUE ; i++){
			if(floor(i+.5f) != i){
				break;
			}
		}
		
		// -8388610 // -(1<<23)-1
		for(int i=0 ; i>Integer.MIN_VALUE ; i--){
			if(floor(i+.5f) != i){
				break;
			}
		}
	}
	
	public static void main(String[] args) throws InterruptedException, IOException {
		
		System.in.read();
		
		long MAX = 1L<<32;
		long STEP = 1L<<26;
		for(long i=0L ; i<MAX ; i++){
			float value = Float.intBitsToFloat((int)i);
			int truth = (int)Math.floor(value);
			int test = floor(value);
			if(test != truth){
				System.err.println("error for : " + value + " expected " + truth + " actual " + test);
			}
			if(i % STEP == 0){
				float progress = (float)i / (float)MAX;
				System.out.println((int)(progress * 100) + " %");
			}
			// Thread.sleep(1);
		}
	}
				
	public static void main2(String[] args) throws InterruptedException, IOException {
		
		System.in.read();
		
		
		for(;;){
				
			float value = MathUtils.random(-516000, 416000) + MathUtils.random();
			
			int floor32 = floor32(value);
			int floor24 = floor24(value);
			int floor15 = floor15(value);
			
			if(floor32 != floor24){
				System.out.println("floor24 error for : " + floor32 + " gives " + floor24);
			}
			if(floor32 != floor15){
				// System.out.println("floor15 error for : " + floor32 + " gives " + floor15);
			}
			
			Thread.sleep(1);
		}
		
	}
	
	/**
	 * Fast floor range from {@code -1<<23-1} = -8388609 to {@code 1<<23} = 8388608
	 * TODO doesn't work with xxx.000 values !
	 * @param v
	 * @return
	 */
	public static int floor(float v){
		// return (int)v - (Float.floatToIntBits(v)>>>31); // v<0 ? (int)v-1 : (int)v;
		return v<0 && (v%1f != 0f) ? (int)v-1 : (int)v;
		
	}
	public static int floor24(float v){
		return floor(v);
	}
	public static int floor15(float v){
		return MathUtils.floor(v);
	}
	public static int floor32(float v){
		return (int)Math.floor(v);
	}
}
