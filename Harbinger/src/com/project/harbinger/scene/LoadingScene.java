package com.project.harbinger.scene;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.util.color.Color;

import com.project.harbinger.manager.SceneManager.SceneType;

/**Ekran ładowania. Wyświetla się, gdy ładowana jest duża ilość danych.
 * @author Łukasz Frącz
 *
 */
public class LoadingScene extends BaseScene {

	/**Tworzy scenę
	 * @see com.project.harbinger.scene.BaseScene#createScene()
	 */
	@Override
	public void createScene() {
		setBackground(new Background(Color.WHITE));
		attachChild(new Text(0, 400, resourcesManager.getFont(), "Loading...", vbom));
	}

	/**Metoda wywoływana po naciśnięciu przycisku "back". Nic nie robi
	 * @see com.project.harbinger.scene.BaseScene#onBackKeyPressed()
	 */
	@Override
	public void onBackKeyPressed() {
	}

	/**
	 * @see com.project.harbinger.scene.BaseScene#getSceneType()
	 * @return Typ sceny (ekran ładowania)
	 */
	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_LOADING;
	}

	/**Niszczy scenę. W tym przypadku nic nie robi.
	 * @see com.project.harbinger.scene.BaseScene#disposeScene()
	 */
	@Override
	public void disposeScene() {
	}

}
