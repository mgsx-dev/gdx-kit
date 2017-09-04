package net.mgsx.game.core.helpers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

public class ColorHelper {

	// TODO pullup to LibGDX
	
	/** Converts HSV color sytem to RGB
	 * https://en.wikipedia.org/wiki/HSL_and_HSV#From_HSL
	 * @return RGB values in Libgdx Color class */
	public static Color hsvToColor (Color color, float h, float s, float v) {
		float x = (h/60f + 6) % 6;
		int i = MathUtils.floorPositive(x);
		float f = x - i;
		float p = v * (1 - s);
		float q = v * (1 - s * f);
		float t = v * (1 - s * (1 - f));
		switch (i) {
		case 0:
			color.r = v;
			color.g = t;
			color.b = p;
			break;
		case 1:
			color.r = q;
			color.g = v;
			color.b = p;
			break;
		case 2:
			color.r = p;
			color.g = v;
			color.b = t;
			break;
		case 3:
			color.r = p;
			color.g = q;
			color.b = v;
			break;
		case 4:
			color.r = t;
			color.g = p;
			color.b = v;
			break;
		default:
			color.r = v;
			color.g = p;
			color.b = q;
		}

		return color;
	}

	public static Color hsvToColor(Color c, float[] hsv) {
		return hsvToColor(c, hsv[0], hsv[1], hsv[2]);
	}

	// https://fr.wikipedia.org/wiki/Teinte_Saturation_Valeur
	public static void colorToHsv(float[] hsv, Color color) 
	{
		float max = Math.max(Math.max(color.r, color.g), color.b);
		float min = Math.min(Math.min(color.r, color.g), color.b);
		float range = max - min;
		if(range == 0){
			hsv[0] = 0;
		}else if(max == color.r){
			hsv[0] = (60 * (color.g - color.b) / range + 360) % 360;
		}else if(max == color.g){
			hsv[0] = 60 * (color.b - color.r) / range + 120;
		}else{
			hsv[0] = 60 * (color.r - color.g) / range + 240;
		}

		if(max > 0){
			hsv[1] = 1 - min/max;
		}else{
			hsv[1] = 0;
		}
		
		hsv[2] = max;
	}
}
