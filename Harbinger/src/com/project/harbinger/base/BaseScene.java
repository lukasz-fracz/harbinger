package com.project.harbinger.base;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.project.harbinger.MainMenuActivity;
import com.project.harbinger.manager.ResourcesManager;
import com.project.harbinger.manager.SceneManager.SceneType;

public abstract class BaseScene extends Scene {

	protected Engine engine;
	protected MainMenuActivity activity;
	protected ResourcesManager resourcesManager;
	protected VertexBufferObjectManager vbom;
	protected Camera camera;
	
	public BaseScene() {
		resourcesManager = ResourcesManager.getInstance();
		engine = resourcesManager.getEngine();
		activity = resourcesManager.getActivity();
		vbom = resourcesManager.getVbom();
		camera = resourcesManager.getCamera();
		createScene();
	}
	
	public abstract void createScene();
	public abstract void onBackKeyPressed();
	public abstract SceneType getSceneType();
	public abstract void disposeScene();
}
