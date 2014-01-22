package com.project.harbinger.gameObject;

import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.project.harbinger.scene.GameScene;


/**
 * Klasa reprezentujące aktywnego przeciwnika. Wszyscy aktywni przeciwnicy dziedziczą po tej klasie.
 * 
 * @author Łukasz Frącz
 *
 */
public class ActiveEnemy extends Enemy {
	
	/**Oznaczenie identifikujące aktywnego przeciwnika*/
	public static final String ACTIVE_USER_DATA = "active";
	/**Oznaczenie informujące o tym, że należy aktywować obiekt. Obiekt aktywny strzela i skręca.*/
	public static final String ACTIVE_START_ME = "hmm";
	/**Oznaczenie informujące o tym, że należy zmienić kierunek obiektu (obiekt zderzył się z innym przeciwnikiem).*/
	public static final String ACTIVE_TURN = "turn!";
	
	/**Prędkość w płaszczyźnie x.*/
	float xVelocity;
	/**Prędkość w płaszczyźnie y/*/
	float yVelocity;
	/**Typ aktywnego przeciwnika (lewy lub prawy)*/
	ActiveEnemyType type;
	/**Czy obiekt może strzelać.*/
	boolean allowToShoot;
	/**Licznik uaktualnień obiektu w świecie.*/
	int updatesCounter;
	/**Scena gry.*/
	GameScene gameScene;

	/**Konstruktor obiektu.
	 * @param pX Współrzędna x
	 * @param pY Współrzędna y
	 * @param region Teksura obiektu
	 * @param vbo Menadżer obiektów
	 * @param id Numer id obiektu
	 * @param gameScene Scena gry
	 */
	public ActiveEnemy(float pX, float pY, ITextureRegion region, VertexBufferObjectManager vbo, int id, GameScene gameScene) {
		super(pX, pY, region, vbo, id);
		allowToShoot = false;
		updatesCounter = 0;
		
		this.gameScene = gameScene;
	}
	
	/**Zmienia kierunek obiektu (w poziomie).
	 * 
	 */
	public void changeSide() {
		xVelocity *= -1f;
		body.setLinearVelocity(xVelocity, yVelocity);
		body.setUserData(ACTIVE_USER_DATA);
	}
	
	/**Aktywuje obiekt.
	 * 
	 */
	public void start() {
		if (type == ActiveEnemyType.LEFT) {
        	xVelocity = 5f;
        } else {
        	xVelocity = -5f;
        }
		
		body.setLinearVelocity(xVelocity, yVelocity);
		
		body.setUserData(ACTIVE_USER_DATA);
		
		allowToShoot = true;
	}
	
	/**Typ aktywnego przeciwnika. Potrzebny tylko na początku, by ustalić w którą stronę powinien skręcić obiekt.
	 * @author Łukasz Frącz
	 *
	 */
	public enum ActiveEnemyType { 
		/**Obiekt pojawi się z lewej strony*/
		LEFT, 
		/**Obiekt pojawi się z prawej strony*/
		RIGHT 
	}
}
