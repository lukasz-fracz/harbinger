package com.project.harbinger.scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.project.harbinger.gameObject.ActiveEnemy;
import com.project.harbinger.gameObject.Enemy;
import com.project.harbinger.gameObject.GameObject;
import com.project.harbinger.gameObject.Missile;
import com.project.harbinger.gameObject.Player;
import com.project.harbinger.gameObject.StaticEnemy;
import com.project.harbinger.gameObject.Missile.MissileType;
import com.project.harbinger.manager.ResourcesManager;
import com.project.harbinger.manager.SceneManager;
import com.project.harbinger.manager.SceneManager.SceneType;
import com.project.harbinger.multiplayer.BluetoothConnection;

/**Scena na której rozgrywa się gra w trybie wieloosobowym.<br/>
 * Obsługuje całą logikę gry wieloosobowej (reszte obsłguje scena gry, po której ta scena dziedziczy).
 * <br/>
 * Scena zachowuje się w pewnych miejscach inaczej w zależności od tego, czy obsługuje grę hosta czy klienta.
 * @author Łukasz Frącz
 *
 */
public class MultiplayerGameScene extends GameScene {

	/**Obiekt obsługujący połączenie bluetooth*/
	private BluetoothConnection bluetoothConnection;
	/**Określa czy scena obsługuje grę klienta czy hosta*/
	private boolean isClient;
	/**Określa czy drugi gracz załadował planszę*/
	private boolean opponentReady;
	/**Określa czy plansza została załadowana*/
	private boolean iAmReady;
	/**Określa czy gra została zatrzymana przez drugiego gracza*/
	private boolean pausedBySecondPlayer;
	/**Statek drugiego gracza*/
	private Sprite player2;
	/**Wynik drugiego gracza*/
	private int opponentScore;
	/**Przycisk "no"*/
	private Sprite noButton;
	/**Przycisk "yesy"*/
	private Sprite yesButton;
	/**Tekst informujący o śmierci partnera*/
	private Text partnerDeadText;
	/**Tekst z pytaniem o pożyczenie życia partnerowi*/
	private Text questionText;

	/**Konstruktor sceny
	 * @param bluetoothConnection Obiekt obsługujący połączenie bluetooth
	 * @param isClient True jeśli scenę tworzy klient, False gdy host
	 */
	public MultiplayerGameScene(BluetoothConnection bluetoothConnection, boolean isClient) {
		super();
		
		this.bluetoothConnection = bluetoothConnection;
		bluetoothConnection.setGameScene(this);
		bluetoothConnection.start();
		this.isClient = isClient;
		opponentReady = iAmReady = pausedBySecondPlayer = false;
		
		opponentScore = 0;
		createPhysics();
		try {
			loadLevel(0);
		} catch (IOException e) {}
		
		missilesToAdd = Collections.synchronizedList(new ArrayList<PreMissile>());
	}
	
	/**Metoda wywoływana przy aktualizacji sceny
	 * @see com.project.harbinger.scene.GameScene#onManagedUpdate(float)
	 */
	public void onManagedUpdate(float pSecondsElapsed) {
		if (isPaused) {
			super.onManagedUpdate(0);
		} else {
			super.onManagedUpdate(pSecondsElapsed);
			activity.runOnUpdateThread(new Runnable() {

				@Override
				public void run() {
					addMissiles();
					deleteObjectsForDestroy();
					updateActiveEnemies();
					if (enemies == 0) {
						deleteEverything();
						loadNextLevel(30);
					}
				}
				
			});
		}
	}
	
	/**Tworzy scenę
	 * @see com.project.harbinger.scene.GameScene#createScene()
	 */
	@Override
	public void createScene() {
		score = 0;
		currentLevel = 0;
		live = 5;
		isPaused = false;
		
		createBackground();
		createHUD();
		createPartnerDeadMenu();
	}
	
	/**
	 * @see com.project.harbinger.scene.GameScene#getSceneType()
	 * @return Typ sceny (gra wieloosobowa)
	 */
	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_MULTIPLAYER_GAME;
	}
	
	/**Tworzy menu pauzy
	 * @see com.project.harbinger.scene.GameScene#createPauseMenu()
	 */
	void createPauseMenu() {
		gamePausedText = new Text(10, 10, resourcesManager.getFont(),
				"Game paused", new TextOptions(HorizontalAlign.LEFT), vbom);
		gamePausedText.setPosition(60, 200);
		gamePausedText.setColor(Color.GREEN);
		

		backButton = new Sprite(90, 370, ResourcesManager.getInstance().getBackButtonRegion(), vbom) {
	        public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
	        	bluetoothConnection.sendNo();
	            gameFinished();
	        	
	            return true;
	        };
	    };
	    resumeButton = new Sprite(90, 300, ResourcesManager.getInstance().getResumeButtonRegion(), vbom) {
	        public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
	            gameHUD.detachChild(gamePausedText);
	            gameHUD.detachChild(backButton);
	            gameHUD.detachChild(this);
	            gameHUD.unregisterTouchArea(this);
	            gameHUD.unregisterTouchArea(backButton);
	            isPaused = false;
	            bluetoothConnection.sendResume();
	            return true;
	        };
	    };
	}
	
	/**
	 * Tworzy menu z pytaniem o pożyczenie życia partnerowi.
	 */
	private void createPartnerDeadMenu() {
		partnerDeadText = new Text(10, 10, resourcesManager.getFont(),
				"Partner's dead", new TextOptions(HorizontalAlign.LEFT), vbom);
		partnerDeadText.setPosition(30, 150);
		partnerDeadText.setColor(Color.GREEN);
		
		questionText = new Text(10, 10, resourcesManager.getFont(),
				"Give him one life?", new TextOptions(HorizontalAlign.LEFT), vbom);
		questionText.setPosition(10, 200);
		questionText.setScale(0.7f);
		questionText.setColor(Color.WHITE);
		
		yesButton = new Sprite(80, 300, ResourcesManager.getInstance().getYesButtonRegion(), vbom) {
	        public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
	        	activity.runOnUpdateThread(new Runnable() {

					@Override
					public void run() {
						gameHUD.detachChild(partnerDeadText);
						gameHUD.detachChild(questionText);
			        	gameHUD.detachChild(yesButton);
			        	gameHUD.detachChild(noButton);
			        	gameHUD.unregisterTouchArea(yesButton);
			    		gameHUD.unregisterTouchArea(noButton);
					}
	        		
	        	});
	            bluetoothConnection.sendYes();
	            live--;
	            updateScore();
	            isPaused = false;
	            pausedBySecondPlayer = false;
	            return true;
	        };
	    };
	    noButton = new Sprite(80, 400, ResourcesManager.getInstance().getNoButtonRegion(), vbom) {
	        public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
	            bluetoothConnection.sendNo();
	            gameFinished();
	            return true;
	        };
	    };
	}
	
	/**
	 * Wyświetla menu z pytaniem o pożyczenie życia partnerowi
	 */
	private void showPartnerDeadMenu() {
		isPaused = true;
		pausedBySecondPlayer = true;
		activity.runOnUpdateThread(new Runnable() {

			@Override
			public void run() {
				gameHUD.attachChild(partnerDeadText);
				gameHUD.attachChild(questionText);
				gameHUD.attachChild(yesButton);
				gameHUD.attachChild(noButton);
				gameHUD.registerTouchArea(yesButton);
				gameHUD.registerTouchArea(noButton);
			}
			
		});
	}
	
	/**Wyświetla napis "level completed"
	 * @see com.project.harbinger.scene.GameScene#showLevelCompleted()
	 */
	void showLevelCompleted() {
		gameHUD.attachChild(levelCompletedText);
		setOnSceneTouchListener(new IOnSceneTouchListener() {

			@Override
			public boolean onSceneTouchEvent(Scene pScene,
					TouchEvent pSceneTouchEvent) {
				//bluetoothConnection.sendLoaded();
				gameHUD.detachChild(levelCompletedText);
				isPaused = false;
				
				setOnSceneTouchListener(null);
				
				return false;
			}
			
		});
	}
	
	/**Metoda wywoływana po naciśnięciu przycisku "back". Zatrzymuje/wznawia grę (o ile jest to możliwe)
	 * @see com.project.harbinger.scene.GameScene#onBackKeyPressed()
	 */
	@Override
	public void onBackKeyPressed() {
		if (pausedBySecondPlayer) {
			return;
		}
		
		super.onBackKeyPressed();
		
		if (live == 0) {
			return;
		}
		
		if (isPaused) {
			bluetoothConnection.sendPause();
		} else {
			bluetoothConnection.sendResume();
		}
	}
	
	/**
	 * Tworzy fizykę świata gry
	 */
	void createPhysics() {
		super.createPhysics(30);
		
		registerUpdateHandler(createMultiplayerUpdateHandler());
	}
	
	/**
	 * @return Interfejs obsługjący zmianę położenia gracza. Gdy gracz zmieni położenie, drugi gracz otrzymuje o tym infromacje
	 */
	IUpdateHandler createMultiplayerUpdateHandler() {
		IUpdateHandler iUpdateHandler = new IUpdateHandler() {

			float x = 0;
			float a = 1f / 25f;
			
			float oldX = -1;
			float oldY = -1;
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				
				x += pSecondsElapsed;
				if (x >= a) {
					if (!(player.getX() == oldX && player.getY() == oldY)) {
						bluetoothConnection.sendMove(player.getX(), player.getY());
						oldX = player.getX();
						oldY = player.getY();
					}
					x = 0;
				}
			}

			@Override
			public void reset() {
			}
			
		};
		
		return iUpdateHandler;
	}
	
	/**
	 * @see com.project.harbinger.scene.GameScene#createContactListener()
	 * @return Interfejs obsługujący kolizje obiektów
	 */
	ContactListener createContactListener() {
		ContactListener contactListener;
		if (!isClient) {
			contactListener = super.createContactListener();
		} else {
			contactListener = new ContactListener() {

				@Override
				public void beginContact(Contact contact) {
					final Fixture first = contact.getFixtureA();
					final Fixture second = contact.getFixtureB();
					
					String firstUD = (String) first.getBody().getUserData();
					String secondUD = (String) second.getBody().getUserData();
					
					
					// activating active enemies
					if ((firstUD.equals(WALL_TOP_USER_DATA) && secondUD.equals(ActiveEnemy.ACTIVE_USER_DATA)) || 
							(secondUD.equals(WALL_TOP_USER_DATA) && firstUD.equals(ActiveEnemy.ACTIVE_USER_DATA))) {
						if (firstUD.equals(ActiveEnemy.ACTIVE_USER_DATA)) {
							first.getBody().setUserData(ActiveEnemy.ACTIVE_START_ME);
						} else {
							second.getBody().setUserData(ActiveEnemy.ACTIVE_START_ME);
							return;
						}
					}
					
					// bouncing active enemies
					
					// from walls
					if ((firstUD.equals(ActiveEnemy.ACTIVE_USER_DATA) && secondUD.equals(WALL_VERTICAL_USER_DATA)) ||
							(secondUD.equals(ActiveEnemy.ACTIVE_USER_DATA) && firstUD.equals(WALL_VERTICAL_USER_DATA))) {
						if (firstUD.equals(ActiveEnemy.ACTIVE_USER_DATA)) {
							first.getBody().setUserData(ActiveEnemy.ACTIVE_TURN);
						} else {
							second.getBody().setUserData(ActiveEnemy.ACTIVE_TURN);
						}
						return;
					}
					
					// from another active enemy
					if (firstUD.equals(ActiveEnemy.ACTIVE_USER_DATA) && secondUD.equals(ActiveEnemy.ACTIVE_USER_DATA)) {
						first.getBody().setUserData(ActiveEnemy.ACTIVE_TURN);
						second.getBody().setUserData(ActiveEnemy.ACTIVE_TURN);
						return;
					}
					
					// handling player collision with enemies, the player has to respawn, and the client has to inform server
					
					if (firstUD.equals(Player.PLAYER_USER_DATA)) {
						if (secondUD.equals(StaticEnemy.STATIC_USER_DATA) || secondUD.equals(ActiveEnemy.ACTIVE_USER_DATA)) {
							second.getBody().setUserData("COLLISION");
							first.getBody().setUserData(GameObject.DESTROY_USER_DATA);
							synchronized (gameObjects) {
								for (GameObject gobj : gameObjects) {
									if (gobj.getBody().getUserData().equals("COLLISION")) {
										gobj.getBody().setUserData(GameObject.DESTROY_BY_WALL_USER_DATA);
										bluetoothConnection.sendDestroy(gobj.getId());
										return;
									}
								}
							}
						}
					}
					if (secondUD.equals(Player.PLAYER_USER_DATA)) {
						if (firstUD.equals(StaticEnemy.STATIC_USER_DATA) || firstUD.equals(ActiveEnemy.ACTIVE_USER_DATA)) {
							first.getBody().setUserData("COLLISION");
							second.getBody().setUserData(GameObject.DESTROY_USER_DATA);
							synchronized (gameObjects) {
								for (GameObject gobj : gameObjects) {
									if (gobj.getBody().getUserData().equals("COLLISION")) {
										gobj.getBody().setUserData(GameObject.DESTROY_BY_WALL_USER_DATA);
										bluetoothConnection.sendDestroy(gobj.getId());
										return;
									}
								}
							}
						}
					}
				}
	
				@Override
				public void endContact(Contact contact) {}
	
				@Override
				public void preSolve(Contact contact, Manifold oldManifold) {
					final Fixture first = contact.getFixtureA();
					final Fixture second = contact.getFixtureB();
					String firstUD = (String) first.getBody().getUserData();
					String secondUD = (String) second.getBody().getUserData();
					
					// if there is a collision with missile
					
					if (firstUD.equals(Missile.MISSILE_USER_DATA) || firstUD.equals(Missile.MISSILE_PLAYER2_USER_DATA) || 
							secondUD.equals(Missile.MISSILE_USER_DATA) || secondUD.equalsIgnoreCase(Missile.MISSILE_PLAYER2_USER_DATA)) {
						// set missile to destroy
						if (firstUD.equals(Missile.MISSILE_USER_DATA) || firstUD.equals(Missile.MISSILE_PLAYER2_USER_DATA)) {
							first.getBody().setUserData(GameObject.DESTROY_BY_WALL_USER_DATA);
							if (secondUD.equals(Player.PLAYER_USER_DATA)) {
								second.getBody().setUserData(GameObject.DESTROY_USER_DATA);
							}
						}
						if (secondUD.equals(Missile.MISSILE_USER_DATA) || secondUD.equals(Missile.MISSILE_PLAYER2_USER_DATA)) {
							second.getBody().setUserData(GameObject.DESTROY_BY_WALL_USER_DATA);
							if (firstUD.equals(Player.PLAYER_USER_DATA)) {
								first.getBody().setUserData(GameObject.DESTROY_USER_DATA);
							}
						}
						contact.setEnabled(false);
					}
					
					if (firstUD.equals(Player.PLAYER_USER_DATA) || secondUD.equals(Player.PLAYER_USER_DATA)) {
						return;
					}			
					
					if (firstUD.equals(Player.PLAYER_IMMORTAL_DATA) && (secondUD.equals(StaticEnemy.STATIC_USER_DATA) || 
							secondUD.equals(ActiveEnemy.ACTIVE_USER_DATA) || secondUD.equals(Missile.MISSILE_USER_DATA) ||
							secondUD.equals(Missile.MISSILE_PLAYER2_USER_DATA))) {
						contact.setEnabled(false);
						return;
					}
					
					if (secondUD.equals(Player.PLAYER_IMMORTAL_DATA) && (firstUD.equals(StaticEnemy.STATIC_USER_DATA) || 
							firstUD.equals(ActiveEnemy.ACTIVE_USER_DATA) || firstUD.equals(Missile.MISSILE_USER_DATA) ||
							firstUD.equals(Missile.MISSILE_PLAYER2_USER_DATA))) {
						contact.setEnabled(false);
						return;
					}
					
					if (first.getBody().getUserData().equals(WALL_BOTTOM_USER_DATA) || 
							second.getBody().getUserData().equals(WALL_BOTTOM_USER_DATA) ||
							first.getBody().getUserData().equals(WALL_TOP_USER_DATA) || 
							second.getBody().getUserData().equals(WALL_TOP_USER_DATA)) {
						contact.setEnabled(false);
					}
				}
	
				@Override
				public void postSolve(Contact contact, ContactImpulse impulse) {}
				
			};
		}
		return contactListener;		
	}
	
	/**Usuwa obiekty, które mają być usunięte. W przypadku hosta wysyła też infromacje o usunięciu obiektu drugiemu graczowi.
	 * @see com.project.harbinger.scene.GameScene#deleteObjectsForDestroy()
	 */
	void deleteObjectsForDestroy() {
		if (isClient) {
			super.deleteObjectsForDestroy();
			return;
		}
		
		if (physicsWorld != null) {
			synchronized (gameObjects) {
				Iterator<GameObject> objects = gameObjects.iterator();
				
				while (objects.hasNext()) {
					final GameObject next = objects.next();
					if (next.getBody() != null && 
							next.getBody().getUserData().equals(GameObject.DESTROY_USER_DATA) || 
							next.getBody().getUserData().equals(GameObject.DESTROY_BY_WALL_USER_DATA) ||
							next.getBody().getUserData().equals(GameObject.DESTROY_BY_SECOND_PLAYER)) {
						PhysicsConnector physicsConnector = physicsWorld.getPhysicsConnectorManager().
								findPhysicsConnectorByShape(next);
						if (physicsConnector != null) {
							if (next instanceof Player) {
								next.getBody().setUserData(Player.PLAYER_USER_DATA);
								respawnPlayer();
								return;
							}
							
							bluetoothConnection.sendDestroy(next.getId());
							
							if (next instanceof Enemy) {
								enemies--;
							}
							if (next.getBody().getUserData().equals(GameObject.DESTROY_USER_DATA)) {
								score += next.getScore();
								updateScore();
							} else if (next.getBody().getUserData().equals(GameObject.DESTROY_BY_SECOND_PLAYER)) {
								bluetoothConnection.sendScore(next.getScore());
								opponentScore += next.getScore();
							}
							
							physicsWorld.unregisterPhysicsConnector(physicsConnector);
							next.getBody().setActive(false);
							physicsWorld.destroyBody(next.getBody());
							detachChild(next);
							objects.remove();
						}
					}
				}
			}
		}
		if (!isClient) {
			bluetoothConnection.sendMyscore(score);
		}
	}
	
	/**Pokazuje tekst "you are dead"
	 * @see com.project.harbinger.scene.GameScene#showGameOverText()
	 */
	void showGameOverText() {
		isPaused = true;
		gameHUD.attachChild(gameOverText);
		bluetoothConnection.sendDead();
	}
	
	/**
	 * Zatrzmuje grę. Metoda wywoływana gdy grę zatrzyma drugi gracz.
	 */
	public void pauseGame() {
		isPaused = true;
		pausedBySecondPlayer = true;
		activity.runOnUpdateThread(new Runnable() {

			@Override
			public void run() {
				attachChild(gamePausedText);
			}
			
		});
	}
	
	/**
	 * Wznawia grę zatrzymaną przez drugiego gracza
	 */
	public void resumeGame() {
		isPaused = false;
		pausedBySecondPlayer = false;
		activity.runOnUpdateThread(new Runnable() {

			@Override
			public void run() {
				detachChild(gamePausedText);
			}
			
		});

	}
	
	/**Przemieszcza na ekranie statek drugiego gracza
	 * @param x Nowa współrzędna x
	 * @param y Nowa współrzędna y
	 */
	public void movePlayer2(float x, float y) {
		player2.setX(x);
		player2.setY(y);
	}

	/**Lista wystrzałów do dodania*/
	private List<PreMissile> missilesToAdd;
	
	/**
	 * Dodaje do gry wystrzały. Obiekty nie mogą być dodawane w dowolnej chwili, więc trzeba je kolejkować.
	 */
	private void addMissiles() {
		synchronized (missilesToAdd) {
			for (PreMissile pm : missilesToAdd) {
				Missile m = new Missile(pm.x, pm.y, vbom, physicsWorld, MissileType.PLAYER2, pm.id);
				m.setCullingEnabled(true);
				attachChild(m);
				synchronized (gameObjects) {
					gameObjects.add(m);
				}
			}
			missilesToAdd.clear();
		}
	}
	
	/**Dodaje nowy wystrzał do kolejki.
	 * @param x Współrzędna x
	 * @param y Współrzędna y
	 * @param id Numer id wystrzału
	 */
	public void addMissile(float x, float y, int id) {
		synchronized (missilesToAdd) {
			missilesToAdd.add(new PreMissile(x, y, id));
		}
	}
	
	/**Ustawia odpowiedni obiekt jako przeznaczony do zniszczenia.
	 * @param id Numer id obiektu do zniszczenia
	 */
	public void setToDestroy(int id) {
		synchronized (gameObjects) {
			for (GameObject gobj : gameObjects) {
				if (gobj.getId() == id) {
					gobj.getBody().setUserData(GameObject.DESTROY_BY_WALL_USER_DATA);
					return;
				}
			}
		}
	}
	
	/**Uaktualnia wynik gracza
	 * @param score O ile należy zwiększyć licznik punktów
	 */
	public void addScore(int score) {
		this.score += score;
		updateScore();
	}
	
	/**Uaktualnia wynik przeciwnika
	 * @param opponentScore Wynik przeciwnika
	 */
	public void updateOpponentScore(int opponentScore) {
		this.opponentScore = opponentScore;
	}
	
	/**
	 * Metoda wywoływana po śmierci drugiego gracza. Gdy gracz ma więcej niż 1 życie pojawia się pytanie o pożyczenie życia partnerowi.
	 * W przeciwnym wypadku gra kończy się.
	 */
	public void partnerDead() {
		if (live > 1) {
			showPartnerDeadMenu();
		} else {
			bluetoothConnection.sendNo();
			gameFinished();
		}
	}
	
	/**
	 * Metoda wywoływana gdy drugi gracz zdecyduje się na oddanie swojego życia.
	 */
	public void takeLife() {
		gameHUD.detachChild(gameOverText);
		live++;
		float width = player.getWidth() / 2;
        float heigh = player.getHeight() / 2;
        float angle = player.getBody().getAngle();
        player.getBody().setTransform((startX + width) / 32, (startY + heigh) / 32, angle);
		player.setToImmortal();
        player.setVelocity(0, 0);
		updateScore();
		isPaused = false;
	}
	
	/**
	 * Metoda wywoływana gdy drugi gracz zakończył grę (nie pożyczył życia)
	 */
	public void screwYou() {
		gameFinished();
	}
	
	/**Tworzy nowy wystrzał wystrzelony przez gracza lub przeciwnika
	 * @see com.project.harbinger.scene.GameScene#creteMissile(float, float, com.project.harbinger.gameObject.Missile.MissileType)
	 */
	public void creteMissile(float x, float y, MissileType type) {
		synchronized (gameObjects) {
			super.creteMissile(x, y, type);
		}
		
		if (isPaused) {
			return;
		}

		if (type == MissileType.PLAYER1 || type == MissileType.PLAYER2) {
			bluetoothConnection.sendMissile(x, y, missileCounter + 1);
		}
	}
	
	/**
	 * Metoda wywoływana gdy drugi gracz wczyta planszę
	 */
	public void opponentIsReady() {
		opponentReady = true;
		if (iAmReady) {
			isPaused = false;
		}
	}
	
	/**Metoda ładująca kolejną planszę
	 * @see com.project.harbinger.scene.GameScene#loadLevel(int)
	 */
	void loadLevel(int levelID) throws IOException {
		super.loadLevel(levelID);
		
		setOnSceneTouchListener(null);
		
		bluetoothConnection.sendLoaded();
		iAmReady = true;
		isPaused = true;
		
		if (opponentReady) {
			opponentReady = false;
			isPaused = false;
			iAmReady = false;
		}

		try {
			gameHUD.detachChild(levelCompletedText);
		} catch (Exception e) {}
		
		try {
			detachChild(player2);
		} catch (Exception e) {}
		
		player2 = new Sprite(player.getX(), player.getY(), ResourcesManager.getInstance().getPlayer2Region(), vbom);
		attachChild(player2);
	}
	
	/**Metoda zakańczająca grę
	 * @see com.project.harbinger.scene.GameScene#gameFinished()
	 */
	void gameFinished() {
		bluetoothConnection.stopConnection();
		SceneManager.getInstance().loadMultiplayerGameCompletedScene(score, opponentScore);
	}
	
	/**Robocza klasa przeznaczona do pamiętania podstawowych informacji o wystrzale (obiekty tej klasy dodawane są do kolejki)
	 * @author Łukasz Frącz
	 *
	 */
	private class PreMissile {
		private float x, y;
		private int id;
		
		private PreMissile(float x, float y, int id) {
			this.x = x;
			this.y = y;
			this.id = id;
		}
	}
}
