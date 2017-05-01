package net.mgsx.game.examples.tactics.model;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.badlogic.gdx.files.FileHandle;

public class ModelTest {

	@Test
	public void test(){
		Model model = Model.load(new FileHandle(new File("../demo-android/assets/tactics/model.json")));
	
		Assert.assertEquals(4, model.characters.size);
		Assert.assertEquals(4, model.cards.size);
		Assert.assertEquals(3, model.characters.first().cards.size);
		
		CardDef card = model.getCards(model.characters.first().cards.first());
		Assert.assertEquals("sword", card.id);
	}
}
