package net.mgsx.game.core.helpers;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;

public class AssetHelper {

	public static <T> T loadAssetNow(AssetManager assets, String fileName, Class<T> type) {
		assets.load(fileName, type);
		assets.finishLoadingAsset(fileName);
		return assets.get(fileName, type);
	}
	public static <T> T loadAssetNow(AssetManager assets, String fileName, Class<T> type, AssetLoaderParameters<T> parameters) {
		assets.load(fileName, type, parameters);
		assets.finishLoadingAsset(fileName);
		return assets.get(fileName, type);
	}

}
