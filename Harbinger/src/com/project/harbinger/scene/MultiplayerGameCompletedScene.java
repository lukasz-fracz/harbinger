package com.project.harbinger.scene;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import com.project.harbinger.manager.SceneManager;
import com.project.harbinger.manager.SceneManager.SceneType;

public class MultiplayerGameCompletedScene extends BaseScene {

	private Text myScoreText, opponentScoreText, resultText;
	
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
	
	@Override
	public void onBackKeyPressed() {
		SceneManager.getInstance().loadMenuSceneFromMultiplayer(engine);
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_MULTIPLAYER_GAME_COMPLETED;
	}

	@Override
	public void disposeScene() {
		detachSelf();
		dispose();
	}
	
	public void prepareScene(int myScore, int opponentScore) {
		myScoreText.setText("Your score: " + myScore);
		opponentScoreText.setText("Opponent's\nscore: " + opponentScore);
		attachChild(myScoreText);
		attachChild(opponentScoreText);
		
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
	
	private void createBackground() {
		setBackground(new Background(Color.BLACK));
	}
}
