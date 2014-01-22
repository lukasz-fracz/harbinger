package com.project.harbinger.scene;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.project.harbinger.MainMenuActivity;
import com.project.harbinger.manager.ResourcesManager;
import com.project.harbinger.manager.SceneManager.SceneType;

/**Klasa bazowa dla wszystkich scen w grze.
 * @author Łukasz Frącz
 *
 */
public abstract class BaseScene extends Scene {

	/**Silnik aplikacji*/
	protected Engine engine;
	/**Activity*/
	protected MainMenuActivity activity;
	/**Menadżer zasobów gry*/
	protected ResourcesManager resourcesManager;
	/**Menadżer objektów*/
	protected VertexBufferObjectManager vbom;
	/**Kamera*/
	protected Camera camera;
	
	/**
	 * Konstruktor klasy.
	 */
	public BaseScene() {
		resourcesManager = ResourcesManager.getInstance();
		engine = resourcesManager.getEngine();
		activity = resourcesManager.getActivity();
		vbom = resourcesManager.getVbom();
		camera = resourcesManager.getCamera();
		createScene();
	}
	
	/**
	 * Metoda wywoływana po naciśnięciu przycisku "home" (lub wtedy, gdy telefon zacznie dzwonić). Tylko jedna scena jej używa, więc
	 * nie ma potrzeby by była abstrakcyjna.
	 */
	public void onHomeKeyPressed() {
		// only one scene will use it, so it doesn't have to be abstract
	}
	
	/**Tworzy scenę*/
	public abstract void createScene();
	/**Metoda wywoływana po naciśnięciu przycisku "back*/
	public abstract void onBackKeyPressed();
	/**
	 * @return Typ sceny
	 */
	public abstract SceneType getSceneType();
	/**Niszczy scenę*/
	public abstract void disposeScene();
}
