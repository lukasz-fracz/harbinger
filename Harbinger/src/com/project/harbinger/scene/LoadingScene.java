package com.project.harbinger.scene;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.util.color.Color;

import com.project.harbinger.manager.SceneManager.SceneType;

public class LoadingScene extends BaseScene {

	@Override
	public void createScene() {
		setBackground(new Background(Color.WHITE));
		attachChild(new Text(0, 400, resourcesManager.getFont(), "Loading...", vbom));
	}

	@Override
	public void onBackKeyPressed() {
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_LOADING;
	}

	@Override
	public void disposeScene() {
		// TODO Auto-generated method stub
		
	}

}
