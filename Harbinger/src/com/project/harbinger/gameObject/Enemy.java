package com.project.harbinger.gameObject;

import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**Klasa reprezentująca przeciwników. Dziedziczą po niej wszyscy przeciwnicy.
 * @author Łukasz Frącz
 *
 */
public class Enemy extends GameObject {
	
	/**Konstruktor obiektu.
	 * 
	 * @param pX Współrzędna x
	 * @param pY Współrzędna y
	 * @param region Tekstura obiektu
	 * @param vbo Menadżer obiektów
	 * @param id Numer id obiektu
	 */
	public Enemy(float pX, float pY, ITextureRegion region, VertexBufferObjectManager vbo, int id) {
		super(pX, pY, region, vbo, id);
	}
}
