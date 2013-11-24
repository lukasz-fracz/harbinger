package com.project.harbinger.scene;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import com.project.harbinger.manager.SceneManager;
import com.project.harbinger.manager.SceneManager.SceneType;

public class GameCompletedScene extends BaseScene {

	private Text gameCompletedText;
	
	@Override
	public void createScene() {
		createBackground();
		gameCompletedText = new Text(10, 10, resourcesManager.getFont(),
				"   Game\ncompleted!", new TextOptions(HorizontalAlign.LEFT), vbom);
		gameCompletedText.setPosition(40, 300);
		gameCompletedText.setColor(Color.BLUE);
		attachChild(gameCompletedText);
	}

	@Override
	public void onBackKeyPressed() {
		SceneManager.getInstance().loadMenuScene(engine);
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_GAME_COMPLETED;
	}

	@Override
	public void disposeScene() {
		detachSelf();
		dispose();
	}
	
	private void createBackground() {
		setBackground(new Background(Color.BLACK));
	}

}
