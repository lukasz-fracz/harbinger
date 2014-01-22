package com.project.harbinger.gameObject;

import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.project.harbinger.manager.ResourcesManager;

/**
 * Klasa reprezentująca wystrzał (statku gracza lub przeciwnika)
 * 
 * @author Łukasz Frącz
 *
 */
public class Missile extends GameObject {
	
	/**Oznaczenie identyfikujące wystrzał*/
	public static final String MISSILE_USER_DATA = "missile";
	/**Oznaczenie identyfikujące wystrzał wystrzelony przez drugiego gracza*/
	public static final String MISSILE_PLAYER2_USER_DATA = "missile2";
	/**Typ wystzrału*/
	private MissileType type;
	
	/**Konstruktor wystrzału.
	 * 
	 * @param pX Współrzędna x
	 * @param pY Wspołrzędna y
	 * @param vbo Menadżer obiektów
	 * @param physicsWorld Świat w którym obiekt ma się znajdować
	 * @param type Typ wystrzału
	 * @param id Numer id obiektu.
	 */
	public Missile(float pX, float pY, VertexBufferObjectManager vbo, PhysicsWorld physicsWorld,
			MissileType type, int id) {
        super(pX, pY, ResourcesManager.getInstance().getMissileRegion(), vbo, id);
        
        this.type = type;
        createPhysics(physicsWorld);
    }
	
	/**Tworzy fizykę obiektu.
	 * @param physicsWorld Świat w którym obiekt ma się znajdować.
	 */
	private void createPhysics(PhysicsWorld physicsWorld) {
		body = PhysicsFactory.createBoxBody(physicsWorld, 
				this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));
		
		if (type == MissileType.PLAYER2) {
			body.setUserData(MISSILE_PLAYER2_USER_DATA);
		} else {
			body.setUserData(MISSILE_USER_DATA);
		}
		body.setFixedRotation(true);
		if (type == MissileType.PLAYER1 || type == MissileType.PLAYER2) {
			body.setLinearVelocity(0, -15);
		} else {
			body.setLinearVelocity(0, 15);
		}
		
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
	        @Override
	        public void onUpdate(float pSecondsElapsed) {
	            super.onUpdate(pSecondsElapsed);
	        }
	    });
	}
	
	/**Typ wystrzału. Konieczny by odpowiednio ustawić oznacznie i prędkość.
	 * @author lukaszSA
	 *
	 */
	public enum MissileType { 
		/**Wystrzelony przez pierwszego gracza*/
		PLAYER1, 
		/**Wystrzelony przez drugiego gracza*/
		PLAYER2, 
		/**Wystrzelony przez przeciwnika*/
		ENEMY
	}
}
