package com.project.harbinger.scene;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import android.content.Context;

import com.project.harbinger.manager.SceneManager;
import com.project.harbinger.manager.SceneManager.SceneType;

public class GameCompletedScene extends BaseScene {

	private Text gameCompletedText;
	private Text[] scoresText;
	private List<Integer> scores;
	
	@Override
	public void createScene() {
		createBackground();
		gameCompletedText = new Text(10, 10, resourcesManager.getFont(),
				"   Game\ncompleted!", new TextOptions(HorizontalAlign.LEFT), vbom);
		gameCompletedText.setPosition(40, 300);
		gameCompletedText.setColor(Color.BLUE);
		attachChild(gameCompletedText);
		
		scoresText = new Text[10];
		for (int i = 0; i < 10; i++) {
			scoresText[i] = new Text(10, 10, resourcesManager.getFont(),
					"1234567890.......1234567890", new TextOptions(HorizontalAlign.LEFT), vbom);
			scoresText[i].setPosition(10, i * 50 + 50);
			scoresText[i].setColor(Color.WHITE);
		}
		
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
	}
	
	public void prepareScene(int score) {
		setScore(score);
		showHighScores();
	}
	
	public void setScore(int score) {
		Debug.e("Twój wynik: " + String.valueOf(score));
		if (score < scores.get(9)) {
			return;
		}
		
		scores.add(score);
		Collections.sort(scores, new Comparator<Integer>() {

			@Override
			public int compare(Integer arg0, Integer arg1) {
				if (arg0 >= arg1) {
					return -1;
				} else {
					return 1;
				}
			}
			
		});
		
		scores.remove(9);
		
		for (int i = 0; i < 10; i++) {
			if (scores.get(i) == score) {
				scores.set(i, score * -1);
				break;
			}
		}
	}
	
	public void showHighScores() {
		detachChild(gameCompletedText);
		for (int i = 0; i < 10; i++) {
			scoresText[i].setColor(Color.WHITE);
			if (scores.get(i) < 0) {
				scores.set(i, scores.get(i) * -1);
				scoresText[i].setColor(Color.RED);
			}
			scoresText[i].setText((i + 1) + "......." + scores.get(i));
			attachChild(scoresText[i]);
		}
	}

	@Override
	public void onBackKeyPressed() {
		try {
			FileOutputStream fos = activity.getApplicationContext().openFileOutput("scores.har", Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(scores);
			oos.close();
		} catch (Exception e) {
			Debug.e(e);
		}
		
		for (int i = 0; i < 10; i++) {
			detachChild(scoresText[i]);
		}
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
