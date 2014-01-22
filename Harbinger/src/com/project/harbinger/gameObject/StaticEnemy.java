package com.project.harbinger.gameObject;

import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**Klasa reprezentujące statycznego przeciwnika. Wszyscy statyczni przeciwnicy dziedziczą po tej klasie.
 * 
 * @author Łukasz Frącz
 *
 */
public class StaticEnemy extends Enemy {
	
	/**Oznaczenie identifikujące statycznego przeciwnika*/
	public static final String STATIC_USER_DATA = "static";

	/**Konstruktor obiektu.
	 * @param pX Współrzędna x
	 * @param pY Wspołrzędna y
	 * @param region Tekstura obiektu
	 * @param vbo Menadżer obiektów
	 * @param id Numer id obiektu
	 */
	public StaticEnemy(float pX, float pY, ITextureRegion region, VertexBufferObjectManager vbo, int id) {
		super(pX, pY, region, vbo, id);
	}	
}
