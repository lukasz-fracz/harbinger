package com.project.harbinger.gameObject;

import org.andengine.engine.camera.Camera;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.project.harbinger.gameObject.Missile.MissileType;
import com.project.harbinger.manager.ResourcesManager;
import com.project.harbinger.manager.SceneManager;
import com.project.harbinger.scene.GameScene;

public class Cruiser extends ActiveEnemy {

	public Cruiser(float pX, float pY, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld,
			ActiveEnemyType type) {
        super(pX, pY, ResourcesManager.getInstance().getCruiserRegion(), vbo);
        
        score = 7;
        this.type = type;
        
        yVelocity = 5f;
        xVelocity = 0;
        createPhysics(camera, physicsWorld);
    }
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
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
	            	if (updatesCounter == 30) {
	            		((GameScene) SceneManager.getInstance().getCurrentScene()).
	            			creteMissile(getX() + 10, getY() + 90, MissileType.ENEMY);
	            		updatesCounter = 0;
	            		return;
	            	}
	            	updatesCounter++;
	            }
	        }
	    });
	}
}
