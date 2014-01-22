package com.project.harbinger.gameObject;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;

/**
 * Klasa reprezentująca obiekty występujące w grze.
 * Każda specjalistyczna klasa (przeciwnicy, wystrzały, statek gracza) dziedziczy po tej klasie.
 * 
 * @author Łukasz Frącz
 *
 */
public class GameObject extends Sprite {

	/**Oznaczenie informujące o tym, że obiekt został zniszczony, a gracz powinien dostać punkty*/
	public static final String DESTROY_USER_DATA = "destroy";
	/**Oznaczenie informujące o tym, że obiekt został zniszczony nie przez gracza, więc gracz nie powinien dostać punktów.*/
	public static final String DESTROY_BY_WALL_USER_DATA = "destroy1";
	/**Oznaczenie informujące o tym, że obiekt został zniszczony przez drugiego gracza (w trybie wieloosobowym).
	 * Hostujący grę powinien wysłać punkt drugiemu graczowi/*/
	public static final String DESTROY_BY_SECOND_PLAYER = "destroy2";
	
	/**Ciało reprezentujące obiekt w świecie.*/
	Body body;
	/**Ilość punktów, jakie gracz otrzymuje za zestrzelenie obiektu*/
	int score;
	/**Numer identyfikujący obiekt na planszy. Używane w trybie wieloosobowym.*/
	int id;
	
	/**Konstruktor obiektu
	 * 
	 * @param pX Współżędna x
	 * @param pY Współżędna y
	 * @param region Tekstura obiektu
	 * @param vbo Menadżer obiektów
	 * @param id Numer id obiektu
	 */
	public GameObject(float pX, float pY, ITextureRegion region, VertexBufferObjectManager vbo, int id) {
		super(pX, pY, region, vbo);
		
		this.id = id;
	}
	
	/**
	 * @return Ciało reprezentujące obiekt.
	 */
	public Body getBody() {
		return body;
	}
	
	/**
	 * @return Ilość punktów przysługujących graczowi za zestrzelenie obiektu.
	 */
	public int getScore() {
		return score;
	}
	
	/**
	 * @return Numer id obiektu.
	 */
	public int getId() {
		return id;
	}
}
