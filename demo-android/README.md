
# Credentials

Google play service credentials are stored in **assets/openworld/client_secrets.json** file and is
ignored by GIT. This file is only included in desktop distribution.
In order to work, the local file should be created are respond to the official format :

```
{
  "installed": {
    "client_id": "xxxxxxxxxxxxxxxxxxxxxxxxx.apps.googleusercontent.com",
    "client_secret": "yyyyyyyyyyyyyyyyyyyyyyyyyyyy"
  }
}
```
# Google Play Service Setup

Since libraries are AAR, this project is not fully compatible with Eclipse.

Workaround require several manual steps : 

* run `gradlew generateEclipseDependencies` on this project. It will create some jars in aarDependencies folder and copy them to libs directory.
* remove all non aar jars (libgdx, kit ...)

You should have this in the libs folder : 

```
com.android.support-support-v4-23.0.0.jar
com.google.android.gms-play-services-base-8.4.0.jar
com.google.android.gms-play-services-basement-8.4.0.jar
com.google.android.gms-play-services-games-8.4.0.jar
de.golfgl.gdxgamesvcs-gdx-gamesvcs-android-0.0.1-SNAPSHOT.jar
de.golfgl.gdxgamesvcs-gdx-gamesvcs-android-gpgs-0.0.1.jar
```

Note that for issues with DEX method 64K limit, following jars have to be omitted as well. This
could disable some features (game save) to properly work.
Note also that new libraries are changes in dependencies may raise this issue again ...

```
com.android.support-appcompat-v7-20.0.0.jar
com.google.android.gms-play-services-drive-8.4.0.jar
com.google.android.gms-play-services-plus-8.4.0.jar
```

# Google play update

Due to Eclipse setup, GPGS version should be set manually in **res/values/integers.xml** and can be found in GPGS dependency : **<SDK_DIR>/extras/google/m2repository/com/google/android/gms/play-services-basement/8.4.0/play-services-basement-8.4.0.aar**

Version ID can be found in **res/values/version.xml**

# Google Play Service Test

In order to debug/test GPGS, it is required to :

* generate a signed APK with android keystore :

    * keystore path on linux : <USER_HOME>/.android/debug.keystore
    * alias : androiddebugkey
    * keystore password : android
    
* Then uninstall previous unsigned APK : `adb uninstall net.mgsx.openworld`
* Then install signed APK : `adb install gdx-kit-demo-android.apk`
* Finally start the APK : `adb shell am start -n net.mgsx.openworld/net.mgsx.kit.demo.AndroidLauncher`

