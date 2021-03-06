package net.mgsx.game.examples.openworld.model;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

import net.mgsx.game.core.helpers.ArrayHelper;
import net.mgsx.game.core.helpers.ColorHelper;
import net.mgsx.game.examples.openworld.model.OpenWorldElement.GeometryType;
import net.mgsx.game.examples.openworld.utils.FreeMindReader;
import net.mgsx.game.examples.openworld.utils.FreeMindReader.FreemindMap;
import net.mgsx.game.examples.openworld.utils.FreeMindReader.FreemindNode;

public class OpenWorldModel {
	
	private static final RandomXS128 inGameRandom = new RandomXS128();
	private static final RandomXS128 rand = new RandomXS128();
	
	private static FreemindMap map;
	
	public static String lang = "fr"; // TODO set it at game startup
	
	public static void load(){
		try {
			map = new FreeMindReader().parse(Gdx.files.internal("openworld/openworld-model.mm"));
		} catch (IOException e) {
			throw new GdxRuntimeException(e);
		}
	}
	
	public static Array<String> getAllTypes() {
		Array<String> l = new Array<String>();
		for(FreemindNode c : map.root().child("items").children()){
			l.add(c.asString());
		}
		return l;
	}
	
	public static String findFusion(Compound compound) 
	{
		for(FreemindNode node : map.root().child("items").children()){
			Compound c = fusion(node);
			if(c != null && compound.equals(c)){
				return node.asString();
			}
		}
		return null;
	}
	
	public static Compound fusion(String uid) {
		return fusion(map.root().child("items").child(uid));
	}
	
	public static Compound fusion(FreemindNode itemNode) {
		FreemindNode buildable = itemNode.child("buildable");
		if(buildable.exists()){
			Compound c = new Compound();
			for(FreemindNode compo : buildable.child("deps").children()){
				c.add(compo.asString(), compo.first().asInt(1));
			}
			return c;
		}
		return null;
	}
	
	/**
	 * use a tool on target type
	 * @param created elements created by interaction (could be empty even if return true)
	 * @param toolName
	 * @param targetName
	 * @return true if action is allowed
	 */
	public static boolean useTool(Array<OpenWorldElement> created, String toolName, String targetName) 
	{
		boolean actionPerformed = false;
		
		// find all cases
		FreemindNode rules = map.root().child("items").child(targetName).child("actions").child(toolName);
		
		if(rules.exists()){
			applyRules(created, rules);
			actionPerformed = true;
		}
		
		return actionPerformed;
	}
	
	private static void applyRules(Array<OpenWorldElement> created, FreemindNode rules)
	{
		// apply rules
		FreemindNode anyRule = rules.child("any");
		FreemindNode allRule = rules.child("all");
		if(anyRule.exists()){
			// pickup random item
			String name = ArrayHelper.any(anyRule.children()).asString();
			created.add(generateNewElement(name));
		}
		else if(allRule.exists()){
			// pickup all items
			for(FreemindNode item : allRule.children()){
				created.add(generateNewElement(item.asString()));
			}
		}
	}
	
	/**
	 * use a weapon on target
	 * @param toolName
	 * @param targetName
	 * @return damage points if tool is a weapon, target has life and weapon is better than target resistence.
	 */
	public static int useWeapon(String toolName, String targetName){
		FreemindNode targetNode = map.root().child("items").child(targetName);
		FreemindNode attackableNode = targetNode.child("attackable");
		FreemindNode weaponNode = map.root().child("items").child(toolName).child("weapon");
		if(attackableNode.exists() && weaponNode.exists()){
			int damage = weaponNode.child("damage").first().asInt(1);
			int shield = attackableNode.child("shield").first().asInt(0);
			if(shield <= damage){
				return damage;
			}
		}
		return 0;
	}
	
	public static void death(Array<OpenWorldElement> created, String type) 
	{
		FreemindNode rules = map.root().child("items").child(type).child("death");
		applyRules(created, rules);
	}

	public static OpenWorldElement generateNewElement(String UID) 
	{
		return generateNewElement(map.root().child("items").child(UID));
	}
	static OpenWorldElement generateNewElement(FreemindNode item) 
	{
		long seed = inGameRandom.nextLong();
		return generateElement(item, seed);
	}
	public static void generateElement(OpenWorldElement element) 
	{
		generateElement(element, map.root().child("items").child(element.type));
	}
	private static void generateElement(OpenWorldElement element, FreemindNode item) 
	{
		// Warning : order is very important here in order to generate same object.
		// TODO maybe use layers
		rand.setSeed(element.seed);

		FreemindNode procedural = item.child("procedural");
		if(procedural.exists()){
			// generates color
			element.color = generateColor(procedural.child("color"));
			
			// generates shape
			final float ratio = .2f;
			String shape = procedural.child("shape").first().asString();
			
			// default is box
			element.geo_x = element.geo_y = 1;
			element.shape = GeometryType.BOX;
			
			if("ball".equals(shape)){
				element.shape = GeometryType.SPHERE;
			}else if("tube".equals(shape)){
				element.geo_x = element.geo_y = ratio;
			}else if("plate".equals(shape)){
				element.geo_x = ratio;
			}
			
			element.size = generateSize(procedural.child("size"));
		}
		
		// TODO damping and density could be derived from typical material :
		// wood like : 700 (floating)
		// stone like : 1500 to 2500
		// default is water density
		element.density = item.child("density").first().asFloat(1000);
		
		element.life = item.child("attackable").child("life").first().asInt(1);
	}
	private static OpenWorldElement generateElement(FreemindNode item, long seed) 
	{
		OpenWorldElement element = new OpenWorldElement();
		element.seed = seed;
		element.type = item.asString();
		element.dynamic = !item.child("static").exists();
		String modelName = item.child("model").first().asString(null);
		if(modelName != null){
			element.modelPath = "openworld/" + modelName + ".g3dj";
		}
		generateElement(element);
		
		return element;
	}

	private static float generateSize(FreemindNode size) {
		return generateFloat(size, 1);
	}

	private static Color generateColor(FreemindNode color) 
	{
		float h = generateFloat(color.child("hue"), 0);
		float s = generateFloat(color.child("sat"), 50) / 100f;
		float l = generateFloat(color.child("lum"), 50) / 100f;
		
		return ColorHelper.hsvToColor(new Color(Color.BLACK), h,s,l);
	}

	private static float generateFloat(FreemindNode node, float defaultValue) {
		float f1 = node.child(0).asFloat(defaultValue);
		float f2 = node.child(1).asFloat(f1);
		return MathUtils.lerp(f1, f2, rand.nextFloat());
	}

	public static String description(String type) {
		return i18n(map.root().child("items").child(type), "hint");
	}

	static String i18n(FreemindNode node, String field) {
		return i18n(node, field, defaultString(field));
	}
	static String i18n(FreemindNode node, String field, String defaultValue) {
		// return defulat value if field doesn't exists.
		FreemindNode fieldNode = node.child(field);
		if(!fieldNode.exists()) return defaultValue;
		// return first translation if theres is not translation for the current lang.
		FreemindNode langNode = fieldNode.child(lang);
		if(!langNode.exists()) return fieldNode.first().first().asString(defaultValue);
		// return the found translation
		return langNode.first().asString(defaultValue);
	}

	public static OpenWorldElement generateNewGarbageElement(Compound compound) 
	{
		// TODO based on compound ?
		return generateNewElement("dirt");
	}

	public static String name(String type) {
		return i18n(map.root().child("items").child(type), "name", missing(type));
	}

	static String missing(String text) {
		return "[" + text + "]";
	}

	private static String defaultString(String name) {
		return i18n(map.root().child("default"), name, missing(name));
	}

	public static Integer space(String type) {
		FreemindNode transportNode = map.root().child("items").child(type).child("transportable");
		if(transportNode.exists()){
			return transportNode.first().asInt(1);
		}
		return null;
	}

	public static Integer energy(String type) {
		FreemindNode eatNode = map.root().child("items").child(type).child("eatable");
		if(eatNode.exists()){
			return eatNode.first().asInt(1);
		}
		return null;
	}

	public static Integer sleepableEnergy(String type) {
		FreemindNode node = map.root().child("items").child(type).child("sleepable");
		if(node.exists()){
			return node.first().asInt(1);
		}
		return null;
	}

	public static Array<OpenWorldElement> destroy(String type) {
		FreemindNode node = map.root().child("items").child(type).child("destructible");
		if(node.exists()){
			Array<OpenWorldElement> result = new Array<OpenWorldElement>();
			for(FreemindNode component : node.child("components").children()){
				for(int i=0 ; i<component.first().asInt(1) ; i++){
					result.add(generateNewElement(component));
				}
			}
			return result;
		}
		return null;
	}

	public static OpenWorldQuestModel quest(String UID) {
		return new OpenWorldQuestModel(map, UID);
	}

	public static Array<String> quests() {
		Array<String> ids = new Array<String>();
		for(FreemindNode node : map.root().child("quests").children()){
			ids.add(node.asString());
		}
		return ids;
	}

	static Array<FreemindNode> items() {
		return map.root().child("items").children();
	}

}
