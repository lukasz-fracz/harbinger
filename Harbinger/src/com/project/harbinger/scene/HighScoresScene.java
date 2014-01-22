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

/**Scena z tabelą wyników.
 * @author Łukasz Frącz
 *
 */
public class HighScoresScene extends BaseScene {

	/**Wyniki wyświetlane na ekranie*/
	private Text[] scoresText;
	/**Napis "hisgh scores"*/
	private Text highScoresText;
	
	/**Tworzy scenę
	 * @see com.project.harbinger.scene.BaseScene#createScene()
	 */
	@Override
	public void createScene() {
		createBackground();
		
		highScoresText = new Text(10, 10, resourcesManager.getFont(),
				"High scores:", new TextOptions(HorizontalAlign.LEFT), vbom);
		highScoresText.setPosition(80, 80);
		highScoresText.setColor(Color.BLUE);
		attachChild(highScoresText);
		
		scoresText = new Text[10];
		for (int i = 0; i < 10; i++) {
			scoresText[i] = new Text(10, 10, resourcesManager.getFont(),
					"1234567890.......1234567890", new TextOptions(HorizontalAlign.LEFT), vbom);
			scoresText[i].setPosition(140, i * 50 + 200);
			scoresText[i].setColor(Color.WHITE);
			attachChild(scoresText[i]);
		}
	}
	
	/**
	 * Uaktualnia tabelę wyników
	 */
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

	/**Metoda wywoływana po naciśnięciu przycisku "back". Powraca do menu głównego
	 * @see com.project.harbinger.scene.BaseScene#onBackKeyPressed()
	 */
	@Override
	public void onBackKeyPressed() {
		SceneManager.getInstance().backToMenu();
	}

	/**
	 * @see com.project.harbinger.scene.BaseScene#getSceneType()
	 * @return Typ sceny (ekran wyników)
	 */
	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_HIGH_SCORES;
	}

	/**Niszczy scenę
	 * @see com.project.harbinger.scene.BaseScene#disposeScene()
	 */
	@Override
	public void disposeScene() {
		detachSelf();
		dispose();
	}

	/**
	 * Tworzy tło
	 */
	private void createBackground() {
		setBackground(new Background(Color.BLACK));
	}
	
}
