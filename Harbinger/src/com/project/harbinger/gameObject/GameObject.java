package com.project.harbinger.gameObject;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;

/**
 * A class that represent all game objects. 
 * All specific game objects classes extends this class.
 * 
 * @author Łukasz Frącz
 *
 */
public class GameObject extends Sprite {

	/**Object was detroyed by player.*/
	public static final String DESTROY_USER_DATA = "destroy";
	/**Object was destroyed, but player should not receive points. Use when object collides with bottom wall, or with player.*/
	public static final String DESTROY_BY_WALL_USER_DATA = "destroy1";
	/**Object was destroyed by second player (use only in multiplayer game).*/
	public static final String DESTROY_BY_SECOND_PLAYER = "destroy2";
	
	/**Object's body*/
	Body body;
	/**How many points player gets for destroying that object*/
	int score;
	/**Object's id. Use in multiplayer*/
	int id;
	
	/**Object's contructor. 
	 * 
	 * @param pX 
	 * @param pY
	 * @param region
	 * @param vbo
	 * @param id Object's id
	 */
	public GameObject(float pX, float pY, ITextureRegion region, VertexBufferObjectManager vbo, int id) {
		super(pX, pY, region, vbo);
		
		this.id = id;
	}
	
	/**
	 * @return Object's body
	 */
	public Body getBody() {
		return body;
	}
	
	/**
	 * @return How many points player gets for destroying this object
	 */
	public int getScore() {
		return score;
	}
	
	/**
	 * @return Object's id.
	 */
	public int getId() {
		return id;
	}
}
