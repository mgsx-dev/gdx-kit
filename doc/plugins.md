
# Boot your plugin

TODOC : packages scan and default plugins interface

# What is a plugin

Plugins are entry for sub configuration. It's split in 2 parts : Runtime and Editor

Runtime plugin adds systems to current engine and register some serializers.

Editor plugin adds systems to current engine (mostly debug systems), tools and editors.

# Reflection limitations

Automatic package scanning is only available on desktop because of use of google reflection API and because
classpath contains all classes.

On Android, Dex file is created upon dependency graph. Then all classes may not be available if not directly or
indirectly referenced from Main activity class.

To overcome this problem, Kit provide a tool to generate direct references (see Meta tool)

# Plugin dependencies

Sometime systems depends on other systems. Order of system creation (plugin call) can be controlled by
specifying dependencies (see @PluginDef#dependencies annotation). This ensure dependent plugin to be initialized
after all dependencies and then safely get system from engine.
