package net.mgsx.box2d.editor.desktop;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.NativeService;

public class DesktopEditor extends Editor
{
	public DesktopEditor() 
	{
		// register native services
		NativeService.instance = new DesktopNativeInterface();
	}
}
