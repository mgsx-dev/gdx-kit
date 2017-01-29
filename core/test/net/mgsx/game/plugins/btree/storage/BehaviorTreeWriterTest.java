package net.mgsx.game.plugins.btree.storage;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.junit.Assert;
import org.junit.Test;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.decorator.Repeat;
import com.badlogic.gdx.ai.btree.leaf.Wait;
import com.badlogic.gdx.ai.utils.random.ConstantFloatDistribution;
import com.badlogic.gdx.ai.utils.random.ConstantIntegerDistribution;
import com.badlogic.gdx.ai.utils.random.TriangularIntegerDistribution;
import com.badlogic.gdx.ai.utils.random.UniformIntegerDistribution;

public class BehaviorTreeWriterTest {

	private void assertWrite(String expected, Task task) 
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(stream);
		
		BehaviorTree tree = new BehaviorTree();
		tree.addChild(task);
		new BehaviorTreeWriter().writeTree(tree, writer);
		writer.close();
		
		Assert.assertEquals(expected, stream.toString().trim());
	}
	
	@Test
	public void testWriteTreeConstantIntegerDistribution()
	{
		assertWrite("repeat times:2", new Repeat(new ConstantIntegerDistribution(2), null));
	}
	
	@Test
	public void testWriteTreeUniformIntegerDistribution()
	{
		assertWrite("repeat times:\"uniform,3,7\"", new Repeat(new UniformIntegerDistribution(3, 7), null));
	}
	
	@Test
	public void testWriteTreeTriangularIntegerDistribution()
	{
		assertWrite("repeat times:\"triangular,1,5,4.23\"", new Repeat(new TriangularIntegerDistribution(1, 5, 4.23f), null));
	}
	
	@Test
	public void testWriteTreeConstantFloatDistribution()
	{
		assertWrite("wait seconds:5.69", new Wait(new ConstantFloatDistribution(5.69f)));
	}
	


}
