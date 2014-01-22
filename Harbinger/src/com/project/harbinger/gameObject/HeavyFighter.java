package com.project.harbinger.gameObject;

import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.project.harbinger.gameObject.Missile.MissileType;
import com.project.harbinger.manager.ResourcesManager;
import com.project.harbinger.scene.GameScene;

/**
 * Klasa reprezentująca ciężki myśliwiec.
 * 
 * @author Łukasz Frącz
 *
 */
public class HeavyFighter extends ActiveEnemy {

	/**Konstruktor ciężkiego myśliwca.
	 * 
	 * @param pX Współrzędna x
	 * @param pY Wspołrzędna y
	 * @param vbo Menadżer obiektów
	 * @param physicsWorld Świat w którym obiekt ma się znajdować
	 * @param type Typ aktywnego przeciwnika (lewy lub prawy)
	 * @param id Numer id obiektu.
	 * @param gameScene Scena gry
	 */
	public HeavyFighter(float pX, float pY, VertexBufferObjectManager vbo, PhysicsWorld physicsWorld,
			ActiveEnemyType type, int id, GameScene gameScene) {
        super(pX, pY, ResourcesManager.getInstance().getHeavyFighterRegion(), vbo, id, gameScene);
        
        score = 5;
        this.type = type;
        
        yVelocity = 5f;
        xVelocity = 0;
        createPhysics(physicsWorld);
    }
	
	/**Tworzy fizykę obiektu.
	 * @param physicsWorld Świat w którym obiekt ma się znajdować.
	 */
	private void createPhysics(PhysicsWorld physicsWorld) {
		body = PhysicsFactory.createBoxBody(physicsWorld, 
				this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));
		
		body.setUserData(ACTIVE_USER_DATA);
		body.setFixedRotation(true);
		body.setLinearVelocity(xVelocity, yVelocity);
		
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
	        @Override
	        public void onUpdate(float pSecondsElapsed) {
	            super.onUpdate(pSecondsElapsed);
	            
	            if (allowToShoot) {
	            	if (updatesCounter == 50) {
	            		gameScene.creteMissile(getX() + 20, getY() + 75, MissileType.ENEMY);
	            		updatesCounter = 0;
	            		return;
	            	}
	            	updatesCounter++;
	            }
	        }
	    });
	}
}
