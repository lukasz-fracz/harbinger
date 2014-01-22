package com.project.harbinger.scene;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;
import org.andengine.engine.camera.Camera;

import com.project.harbinger.manager.SceneManager.SceneType;

/**Splash screen widoczny zaraz po uruchomieniu gry.
 * @author Łukasz Frącz
 *
 */
public class SplashScene extends BaseScene {
	
	/**Tło*/
	private Sprite splash;

	/**Tworzy scenę
	 * @see com.project.harbinger.scene.BaseScene#createScene()
	 */
	@Override
	public void createScene() {
		splash = new Sprite(0, 0, resourcesManager.getSplashRegion(), vbom) {
			
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		
		splash.setScale(1.5f);
		splash.setPosition(0, 0);
		attachChild(splash);
	}

	/**Metoda wywoływana po naciśnięciu przycisku "back". W tym przypadku nic nie robi
	 * @see com.project.harbinger.scene.BaseScene#onBackKeyPressed()
	 */
	@Override
	public void onBackKeyPressed() {
	}

	/**
	 * @see com.project.harbinger.scene.BaseScene#getSceneType()
	 * 
	 * @return Typ sceny (splash)
	 */
	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_SPLASH;
	}

	/**Metoda wywoływana przy niszczeniu sceny
	 * @see com.project.harbinger.scene.BaseScene#disposeScene()
	 */
	@Override
	public void disposeScene() {
		splash.detachSelf();
		splash.dispose();
		detachSelf();
		dispose();
	}

}
