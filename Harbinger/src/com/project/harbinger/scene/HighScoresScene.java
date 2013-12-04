package com.project.harbinger.scene;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import com.project.harbinger.manager.SceneManager;
import com.project.harbinger.manager.SceneManager.SceneType;

public class HighScoresScene extends BaseScene {

	private Text[] scoresText;
	
	@Override
	public void createScene() {
		createBackground();
		
		scoresText = new Text[10];
		for (int i = 0; i < 10; i++) {
			scoresText[i] = new Text(10, 10, resourcesManager.getFont(),
					"1234567890.......1234567890", new TextOptions(HorizontalAlign.LEFT), vbom);
			scoresText[i].setPosition(10, i * 50 + 50);
			scoresText[i].setColor(Color.WHITE);
			attachChild(scoresText[i]);
		}
	}
	
	public void refresh() {
		Debug.e("Rereshuje");
		List<Integer> scores;
		
		try {
			FileInputStream fis = activity.getApplicationContext().openFileInput("scores.har");
			ObjectInputStream ois = new ObjectInputStream(fis);
			scores = (ArrayList<Integer>) ois.readObject();
			fis.close();
		} catch (Exception e) {
			scores = new ArrayList<Integer>();
			for (int i = 0; i < 10; i++) {
				scores.add(0);
			}
		}
		
		for (int i = 0; i < 10; i++) {
			scoresText[i].setText((i + 1) + "......." + scores.get(i));
		}
	}

	@Override
	public void onBackKeyPressed() {
		SceneManager.getInstance().backToMenu();
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_HIGH_SCORES;
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
