package com.project.harbinger.manager;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.project.harbinger.MainMenuActivity;

public class ResourcesManager {

	private static final ResourcesManager INSTANCE = new ResourcesManager();
	
	public static ResourcesManager getInstance() {
		return INSTANCE;
	}
	
	public static void prepareManager(Engine engine, MainMenuActivity activity, 
			Camera camera, VertexBufferObjectManager vbom) {
        getInstance().engine = engine;
        getInstance().activity = activity;
        getInstance().camera = camera;
        getInstance().vbom = vbom;
    }
	
	// --------
	
	private Engine engine;
	private MainMenuActivity activity;
	private Camera camera;
	private VertexBufferObjectManager vbom;
	
	private void loadMenuGraphics() {
		
	}
	
	private void loadGameGraphics() {
		
	}
	
	public void loadSplashScreen() {
		
	}
	
	public void unloadSplashScreen() {
		
	}
	
	public void loadMenuResources() {
		loadMenuGraphics();
	}
	
	public void loadGameResources() {
		loadGameGraphics();
	}
	
	public Engine getEngine() {
		return engine;
	}
	
	public MainMenuActivity getActivity() {
		return activity;
	}
	
	public Camera getCamera() {
		return camera;
	}
	
	public VertexBufferObjectManager getVbom() {
		return vbom;
	}
}
