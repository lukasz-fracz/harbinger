package com.project.harbinger.scene;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import com.project.harbinger.manager.SceneManager;
import com.project.harbinger.manager.SceneManager.SceneType;

/**Ekran zakończenia gry wieloosobowej. Pokazuje który z graczy zdobył więcej punktów.
 * @author Łukasz Frącz
 *
 */
public class MultiplayerGameCompletedScene extends BaseScene {

	/**Tekst wyświetlający wynik gracza*/
	private Text myScoreText;
	/**Tekst wyświetlający wynik drugiego gracza*/
	private Text opponentScoreText;
	/**Tekst wyświetlający zwycięzce*/
	private Text resultText;
	
	/**Tworzy scenę
	 * @see com.project.harbinger.scene.BaseScene#createScene()
	 */
	@Override
	public void createScene() {
		createBackground();
		
		myScoreText = new Text(10, 10, resourcesManager.getFont(),
				"Your score: \n0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
		myScoreText.setPosition(40, 300);
		myScoreText.setColor(Color.BLUE);
		
		opponentScoreText = new Text(10, 10, resourcesManager.getFont(),
				"Opponent's\nscore: \n0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
		opponentScoreText.setPosition(40, 400);
		opponentScoreText.setColor(Color.YELLOW);
		
		resultText =  new Text(10, 10, resourcesManager.getFont(),
				"You won loose Draw", new TextOptions(HorizontalAlign.LEFT), vbom);
		resultText.setPosition(40,  600);
	}
	
	/**Metoda wywoływana po naciśnięciu przycisku "back". Powraca do menu głównego.
	 * @see com.project.harbinger.scene.BaseScene#onBackKeyPressed()
	 */
	@Override
	public void onBackKeyPressed() {
		SceneManager.getInstance().loadMenuSceneFromMultiplayer();
	}

	/**
	 * @see com.project.harbinger.scene.BaseScene#getSceneType()
	 * @return Typ sceny (ekran zakończenia gry wieloosobowej)
	 */
	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_MULTIPLAYER_GAME_COMPLETED;
	}

	/**Niszczy scenę
	 * @see com.project.harbinger.scene.BaseScene#disposeScene()
	 */
	@Override
	public void disposeScene() {
		detachSelf();
		dispose();
	}
	
	/**Przygotowywuje scenę
	 * @param myScore Wynik gracza
	 * @param opponentScore Wynik drugiego gracza
	 */
	public void prepareScene(int myScore, int opponentScore) {
		myScoreText.setText("Your score: " + myScore);
		opponentScoreText.setText("Opponent's\nscore: " + opponentScore);
		
		try {
			attachChild(myScoreText);
			attachChild(opponentScoreText);
		} catch (Exception e) {
			return;
		}
		
		if (myScore < opponentScore) {
			resultText.setText("You loose");
			resultText.setColor(Color.RED);
		} else if (myScore > opponentScore) {
			resultText.setText("You won");
			resultText.setColor(Color.GREEN);
		} else {
			resultText.setText("Draw");
			resultText.setColor(Color.WHITE);
		}
		
		attachChild(resultText);
	}
	
	/**
	 * Tworzy tło
	 */
	private void createBackground() {
		setBackground(new Background(Color.BLACK));
	}
}
