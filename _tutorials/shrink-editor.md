---
title: "Shrink the KIT Editor"
category: ""
key: "000000"
---

You will learn in this tutorial how to narrow KIT plugin configuration
to have a specific editor.

For illustration, we will just include what we need to preview our 3D models.
This can be helpful to analyse key frames in animations, mesh density and so on.

	
First we create a new plugin and configure its dependencies.
	
It just depends on {@link G3DEditorPlugin} which include all tools, components and systems
we need.
	

```java

@PluginDef(dependencies={
	G3DEditorPlugin.class
})
public class ModelViewerPlugin extends EditorPlugin
{
	@Override
	public void initialize(EditorScreen editor) {
		// nothing more
	}
}

```

	
For sake of simplicity we don't create a cross platform game with separated launcher
but merge all this in a desktop launcher.
	
Here we just have one plugin.
	

```java

public class ModelViewerDesktopLauncher
{
	public static void main(String[] args) {
		EditorConfiguration editorConfig = new EditorConfiguration();
		editorConfig.registry.registerPlugin(new ModelViewerPlugin());
		new LwjglApplication(new EditorApplication(editorConfig), new LwjglApplicationConfiguration());
	}
}

```

