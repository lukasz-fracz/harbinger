package com.project.harbinger.gameObject;

import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.project.harbinger.gameObject.Missile.MissileType;
import com.project.harbinger.manager.ResourcesManager;
import com.project.harbinger.scene.GameScene;

/**Klasa reprezentująca gracza
 * @author Łukasz Frącz
 *
 */
public class Player extends GameObject {

	/**Oznaczenie identyfikujące gracza*/
	public static final String PLAYER_USER_DATA = "player";
	/**Oznaczenie identyfikujące nieśmiertelnego gracza*/
	public static final String PLAYER_IMMORTAL_DATA = "haha";
	
	/**Czy chce strzelić*/
	private boolean fire;
	/**Czy został niedawno zabity*/
	private boolean killed;
	/**Czy może strzelać*/
	private boolean canShoot;
	/**Scena gry*/
	private GameScene gameScene;
	
	/**Konstruktor gracza.
	 * 
	 * @param pX Współrzędna x
	 * @param pY Wspołrzędna y
	 * @param vbo Menadżer obiektów
	 * @param physicsWorld Świat w którym obiekt ma się znajdować
	 * @param gameScene Scena gry
	 */
	public Player(float pX, float pY, VertexBufferObjectManager vbo, PhysicsWorld physicsWorld, GameScene gameScene) {
        super(pX, pY, ResourcesManager.getInstance().getPlayerRegion(), vbo, 1);
        createPhysics(physicsWorld);
        
        this.gameScene = gameScene;
        
        fire = killed = false;
        canShoot = true;
    }
	
	/**Tworzy fizykę obiektu.
	 * @param physicsWorld Świat w którym obiekt ma się znajdować.
	 */
	private void createPhysics(PhysicsWorld physicsWorld) {
		body = PhysicsFactory.createBoxBody(physicsWorld, 
				this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));
		
		body.setUserData(PLAYER_USER_DATA);
		body.setFixedRotation(true);
		
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
	        
			float x = 0, y = 0;
			
			@Override
	        public void onUpdate(float pSecondsElapsed) {
	            super.onUpdate(pSecondsElapsed);
	            
	            x += pSecondsElapsed;
	            
	            if (fire) {
	            	if (x >= 0.5) {
	            		x = 0;
	            		if (canShoot) {
	            			gameScene.creteMissile(getX() + 20, getY() - 50, MissileType.PLAYER1);
	            		}
	            		fire = false;
	            	}
	            }
	            if (killed) {
	            	y += pSecondsElapsed;
	            	body.setLinearVelocity(0, 0);
		            if (y >= 1.0) {
		            	setVisible(true);
		            	body.setUserData(PLAYER_USER_DATA);
		            	killed = false;
		            	y = 0;
		            	canShoot = true;
		            }		            
	            }
	        }
	    });
	}
	
	/**Ustawia prędkość gracza
	 * @param dx Prędkość w płaszczyźnie x
	 * @param dy Prędkość w płaszczyźnie y
	 */
	public void setVelocity(float dx, float dy) {
		if (killed) {
			return;
		}
		body.setLinearVelocity(dx, dy);
	}
	
	/**
	 * Ustawia chęc oddania strzału
	 */
	public void fire() {
		fire = true;
	}
	
	/**
	 * Ustawia gracza jako nieśmiertlenego.
	 * Nieśmiertelny gracz nie może się poruszać i strzelać. Nie może też zginąć.
	 */
	public void setToImmortal() {
		body.setUserData(PLAYER_IMMORTAL_DATA);
		setVisible(false);
		killed = true;
		canShoot = false;
		body.setLinearVelocity(0, 0);
	}
}
