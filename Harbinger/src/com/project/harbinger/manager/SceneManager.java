package com.project.harbinger.manager;

import org.andengine.engine.Engine;

import com.project.harbinger.base.BaseScene;

public class SceneManager {
	
	private static final SceneManager INSTANCE = new SceneManager();
	
	public SceneManager getInstance() {
		return INSTANCE;
	}
	
	// ---------------
	
	private BaseScene splashScene;
	private BaseScene menuScene;
	private BaseScene gameScene;
	private BaseScene loadingScene;
	
	public enum SceneType {
		SCENE_SPLASH,
		SCENE_MENU,
		SCENE_GAME,
		SCENE_LOADING,
	}
	
	private SceneType currentSceneType = SceneType.SCENE_SPLASH;
	private BaseScene currentScene;
	private Engine engine = ResourcesManager.getInstance().getEngine();
	
	public void setScene(BaseScene scene) {
		engine.setScene(scene);
		currentScene = scene;
		currentSceneType = scene.getSceneType();
	}
	
	public void setScene(SceneType sceneType) {
		switch (sceneType) {
		case SCENE_MENU:
			setScene(menuScene);
			break;
		case SCENE_SPLASH:
			setScene(splashScene);
			break;
		case SCENE_GAME:
			setScene(gameScene);
			break;
		case SCENE_LOADING:
			setScene(loadingScene);
			break;
		default:
			break;
		}
	}

	public BaseScene getCurrentScene() {
		return currentScene;
	}
	
	public SceneType getCurrentSceneType() {
		return currentSceneType;
	}
	
}
