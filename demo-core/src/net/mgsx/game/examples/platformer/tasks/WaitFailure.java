package net.mgsx.game.examples.platformer.tasks;

import com.badlogic.gdx.ai.btree.Decorator;
import com.badlogic.gdx.ai.btree.Task;

import net.mgsx.game.plugins.btree.annotations.TaskAlias;

@TaskAlias("waitFailure")
public class WaitFailure<E> extends Decorator<E>
{
	@Override
	public void childFail(Task<E> runningTask) {
		running();
	}
}
