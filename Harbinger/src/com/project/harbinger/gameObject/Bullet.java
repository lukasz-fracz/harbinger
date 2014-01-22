package com.project.harbinger.gameObject;

import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.project.harbinger.manager.ResourcesManager;

/**
 * Klasa reprezentująca pocisk.
 * 
 * @author Łukasz Frącz
 *
 */
public class Bullet extends StaticEnemy {

	/**Konstruktor pocisku.
	 * 
	 * @param pX Współrzędna x
	 * @param pY Wspołrzędna y
	 * @param vbo Menadżer obiektów
	 * @param physicsWorld Świat w którym obiekt ma się znajdować
	 * @param id Numer id obiektu.
	 */
	public Bullet(float pX, float pY, VertexBufferObjectManager vbo, PhysicsWorld physicsWorld,
			int id) {
        super(pX, pY, ResourcesManager.getInstance().getBulletRegion(), vbo, id);
        
        score = 2;
        createPhysics(physicsWorld);
    }
	
	/**Tworzy fizykę obiektu.
	 * @param physicsWorld Świat w którym obiekt ma się znajdować.
	 */
	private void createPhysics(PhysicsWorld physicsWorld) {
		body = PhysicsFactory.createBoxBody(physicsWorld, 
				this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));
		
		body.setUserData(STATIC_USER_DATA);
		body.setFixedRotation(true);
		body.setLinearVelocity(0, 10);
		
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
	        @Override
	        public void onUpdate(float pSecondsElapsed) {
	            super.onUpdate(pSecondsElapsed);
	        }
	    });
	}
}
