package com.badlogic.gdx.ai.tests.btree.dog;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.math.MathUtils;

public class Dog implements Component
{
	public String name;
	public String brainLog;

	public void bark () {
		if (MathUtils.randomBoolean())
			log("Arf arf");
		else
			log("Woof");
	}

	public void startWalking () {
		log("Let's find a nice tree");
	}

	public void randomlyWalk () {
		log("SNIFF SNIFF - Dog walks randomly around!");
	}

	public void stopWalking () {
		log("This tree smells good :)");
	}

	public Boolean markATree (int i) {
		if (i == 0) {
			log("Swoosh....");
			return null;
		}
		if (MathUtils.randomBoolean()) {
			log("MUMBLE MUMBLE - Still leaking out");
			return Boolean.FALSE;
		}
		log("I'm ok now :)");
		return Boolean.TRUE;
	}

	public void log (String msg) {
		GdxAI.getLogger().info(name, msg);
	}

	public void brainLog (String msg) {
		GdxAI.getLogger().info(brainLog, msg);
	}
}
