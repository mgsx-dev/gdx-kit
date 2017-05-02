package net.mgsx.game.examples.tactics.ui;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeBitmapFontData;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class IconsHelper {

	private static BitmapFont font12;
	private static ObjectMap<String, Character> nameToUnicode = new ObjectMap<String, Character>();
	
	private static void load()
	{
		nameToUnicode.clear();
		
		String basePath = "skins/rpg-awesome/rpgawesome-webfont";
		
		String allChars = " ";
		XmlReader xml = new XmlReader();
		try {
			Element root = xml.parse(Gdx.files.internal(basePath + ".svg"));
			for(Element e : root.getChildrenByNameRecursively("glyph")){
				String unicode = e.getAttribute("unicode");
				String name = e.getAttribute("glyph-name", "");
				// &#xe901;
				Pattern p = Pattern.compile("^&#x(\\p{XDigit}{4});$");
				Matcher m = p.matcher(unicode);
				if(m.matches()){
					String xdigits = m.group(1);
						
					int code = Integer.valueOf(xdigits, 16);
					allChars += (char)code;
					nameToUnicode.put("ra-" + name, (char)code);
				}
			}
			
		} catch (IOException e) {
			throw new GdxRuntimeException(e);
		}
		
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(basePath + ".ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.characters = allChars;
		parameter.size = 24;
		FreeTypeBitmapFontData data = new FreeTypeBitmapFontData();
		data.xChars = new char[]{};
		data.xHeight = 16;
		font12 = generator.generateFont(parameter, data); // font size 12 pixels
		generator.dispose(); // don't forget to dispose to avoid memory leaks!
	}
	
	public static Drawable find(String id){
		// font12.getData().
		return null;
	}

	public static Image image(String id) 
	{
		if(font12 == null) load();
		Character c = nameToUnicode.get(id);
		if(c == null) c = nameToUnicode.get("ra-help");
		Glyph glyph = font12.getData().getGlyph(c); // '\ue946'
		TextureRegion region = font12.getRegion(glyph.page);
		region = new TextureRegion(region.getTexture(), glyph.u, glyph.v2, glyph.u2, glyph.v);
		Image img = new Image(region);
		img.setScaling(Scaling.fit);
		return img;
	}
}
