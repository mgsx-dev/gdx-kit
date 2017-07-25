package net.mgsx.game.core.meta;

import org.junit.Assert;
import org.junit.Test;

import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.ui.accessors.Accessor;
import net.mgsx.game.core.ui.accessors.AccessorScanner;

@SuppressWarnings("unused")
public class AccessorScannerTest {
	
	public static class DummyBasic {
		
		private String privateField;
		String privatePAckageField;
		protected String protectedField;
		public String publicField;
		public transient String transientField;
		
		@Editable private String privateFieldEditable;
		@Editable String privatePAckageFieldEditable;
		@Editable protected String protectedFieldEditable;
		@Editable public String publicFieldEditable;
		@Editable public transient String transientFieldEditable;
		
		@Storable private String privateFieldStorable;
		@Storable String privatePAckageFieldStorable;
		@Storable protected String protectedFieldStorable;
		@Storable public String publicFieldStorable;
		@Storable public transient String transientFieldStorable;
		
	}
	
	
	@Test
	public void testDummyBasic_Editable() throws Exception
	{
		DummyBasic dummyBasic = new DummyBasic();
		Array<Accessor> accessors = AccessorScanner.scan(dummyBasic, true, true);
		Assert.assertEquals(2, accessors.size);
	}
	
	@Test
	public void testDummyBasic_EditableAuto() throws Exception
	{
		DummyBasic dummyBasic = new DummyBasic();
		Array<Accessor> accessors = AccessorScanner.scan(dummyBasic, false, true);
		Assert.assertEquals(6, accessors.size);
	}
}
