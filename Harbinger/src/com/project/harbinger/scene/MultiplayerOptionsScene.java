package com.project.harbinger.scene;

import java.io.IOException;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;

import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.project.harbinger.manager.ResourcesManager;
import com.project.harbinger.manager.SceneManager;
import com.project.harbinger.manager.SceneManager.SceneType;
import com.project.harbinger.multiplayer.BluetoothClient;
import com.project.harbinger.multiplayer.BluetoothServer;

/**Menu trybu wieloosobowego.
 * @author Łukasz Frącz
 *
 */
public class MultiplayerOptionsScene extends BaseScene implements IOnMenuItemClickListener {

	/**Adapter bluetooth*/
	private BluetoothAdapter mBluetoothAdapter;
	/**Urządzenie bluetooth*/
	private BluetoothDevice device;
	/**Scena dziecko zawierająca przyciski*/
	private MenuScene menuChildScene;
	/**Przycisk "host game"*/
	private IMenuItem hostItem;
	/**Przycisk "join game"*/
	private IMenuItem joinItem; 
	/**Przycisk "back"*/
	private IMenuItem backItem;
	/**Ikona statusu*/
	private Sprite statusIcon;
	/**Tekst informujący o tym, co aktualnie robi połączenie bluetooth*/
	private Text actionText;
	/***/
	private Intent discoverableIntent;
	
	/**Identyfikacja przycisku "host game"*/
	private static final short HOST = 0;
	/**Identyfikacja przycisku "join game"*/
	private static final short JOIN = 1;
	/**Identyfikacja przycisku "back"*/
	private static final short BACK = 2;
	/**Akcja - czekaj*/
	public static final short WAIT = 3;
	/**Akcja - coś znalazłem*/
	public static final short FOUND_SOMETHING = 4;
	/**Akcja - znalazłem grę*/
	public static final short FOUND = 5;
	
	/**
	 * Obiekt wyszukujący urządzenia bluetooth
	 */
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        // When discovery finds a device
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            // Get the BluetoothDevice object from the Intent
	        	setStatus(FOUND_SOMETHING);
	        	device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE); 	
	            BluetoothClient client;
	            try {
	            	client = new BluetoothClient(device);
	            } catch (IOException e) {
	            	setStatus(WAIT);
	            	return;
	            }
	            mBluetoothAdapter.cancelDiscovery();
	            try {
	            	activity.unregisterReceiver(mReceiver);
	            } catch (Exception e) {}
	        }
	    }
	};

	/** Tworzy scenę
	 * @see com.project.harbinger.scene.BaseScene#createScene()
	 */
	@Override
	public void createScene() {
		createBackground();
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(0, 0);
		
		actionText = new Text(10, 10, resourcesManager.getFont(),
				"Looking for player game...", new TextOptions(HorizontalAlign.LEFT), vbom);
		actionText.setPosition(-30, 80);
		actionText.setScale(0.7f);
		actionText.setColor(Color.WHITE);
		
		hostItem = new ScaleMenuItemDecorator(new SpriteMenuItem(
				HOST, resourcesManager.getHostButtonRegion(), vbom),
				1.2f, 1);
		joinItem = new ScaleMenuItemDecorator(new SpriteMenuItem(
				JOIN, resourcesManager.getJoinButtonRegion(), vbom),
				1.2f, 1);
		backItem = new ScaleMenuItemDecorator(new SpriteMenuItem(
				BACK, resourcesManager.getBackMenuButtonRegion(), vbom),
				1.2f, 1);
		
		menuChildScene.addMenuItem(hostItem);
		menuChildScene.addMenuItem(joinItem);
		menuChildScene.addMenuItem(backItem);
		
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);
		
		hostItem.setPosition(hostItem.getX(), hostItem.getY() + 10);
		joinItem.setPosition(joinItem.getX(), joinItem.getY() + 10);
		backItem.setPosition(backItem.getX(), backItem.getY() + 30);
		
		menuChildScene.setOnMenuItemClickListener(this);
		
		setChildScene(menuChildScene);
		
		statusIcon = new Sprite(0, 0, ResourcesManager.getInstance().getWaitIconRegion(), vbom);
	}

	/**Metoda wywoływana po naciśnięciu przycisku "back". Wyłącza bluetooth, restartuje menu i powraca do menu głównego.
	 * @see com.project.harbinger.scene.BaseScene#onBackKeyPressed()
	 */
	@Override
	public void onBackKeyPressed() {
		activity.runOnUpdateThread(new Runnable() {

			@Override
			public void run() {
				try {
					detachChild(statusIcon);
					menuChildScene.attachChild(hostItem);
					menuChildScene.attachChild(joinItem);
					menuChildScene.registerTouchArea(hostItem);
					menuChildScene.registerTouchArea(joinItem);
					detachChild(actionText);
					activity.stopService(discoverableIntent);
				} catch (Exception e) {}
			}
			
		});		
		
		try {
			mBluetoothAdapter.cancelDiscovery();
        	activity.unregisterReceiver(mReceiver);
		} catch (Exception e) {}
		
		mBluetoothAdapter.disable();
		SceneManager.getInstance().backToMenu();
	}

	/**
	 * @see com.project.harbinger.scene.BaseScene#getSceneType()
	 * @return Typ sceny (menu trybu wieloosobowego)
	 */
	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_MULTIPLAYER_OPTIONS;
	}

	/**Niszczy scenę
	 * @see com.project.harbinger.scene.BaseScene#disposeScene()
	 */
	@Override
	public void disposeScene() {
		detachSelf();
		dispose();
	}
	
	/**
	 * Tworzy tło
	 */
	private void createBackground() {
		setBackground(new Background(Color.BLACK));
	}

	/**Interfejs uruchamiany po naciśnięciu przycisku w menu
	 * @see org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener#onMenuItemClicked(org.andengine.entity.scene.menu.MenuScene, org.andengine.entity.scene.menu.item.IMenuItem, float, float)
	 */
	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
		case HOST:			
			(new Thread() {

				@Override
				public void run() {
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							actionText.setText("Looking for player...");
							attachChild(actionText);
							menuChildScene.detachChild(hostItem);
							menuChildScene.detachChild(joinItem);
							menuChildScene.unregisterTouchArea(hostItem);
							menuChildScene.unregisterTouchArea(joinItem);
						}				
					});
					
					try {
						BluetoothServer server = new BluetoothServer(mBluetoothAdapter);
						activity.stopService(discoverableIntent);
					} catch (IOException e) {
						try {
							detachChild(statusIcon);
							menuChildScene.attachChild(hostItem);
							menuChildScene.attachChild(joinItem);
							menuChildScene.registerTouchArea(hostItem);
							menuChildScene.registerTouchArea(joinItem);
							detachChild(actionText);
						} catch (Exception ex) {}
						
						try {
							mBluetoothAdapter.cancelDiscovery();
			            	activity.unregisterReceiver(mReceiver);
						} catch (Exception ex) {}
						
						try {
							mBluetoothAdapter.disable();
						} catch (Exception exx) {}
						SceneManager.getInstance().backToMenu();
					}
				}
				
			}).start();
			return true;
		case JOIN:
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					actionText.setText("Looking for game...");
					attachChild(actionText);
					menuChildScene.detachChild(hostItem);
					menuChildScene.detachChild(joinItem);
					menuChildScene.unregisterTouchArea(hostItem);
					menuChildScene.unregisterTouchArea(joinItem);
					
					setStatus(WAIT);
					mBluetoothAdapter.startDiscovery();
				}				
			});
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			activity.registerReceiver(mReceiver, filter);
			return true;
		case BACK:
			onBackKeyPressed();
			return true;
		default:
			return false;
		}
	} 
	
	/**Zmienia ikonkę statusu poszukiwań gry.
	 * @param status Aktualny status (odpowiednie wartości są polami statycznymi)
	 */
	public void setStatus(short status) {
		switch (status) {
		case WAIT:
			activity.runOnUpdateThread(new Runnable() {

				@Override
				public void run() {
					try {
						detachChild(statusIcon);
					} catch (Exception e) {}
					statusIcon = new Sprite(0, 0, ResourcesManager.getInstance().getWaitIconRegion(), vbom);
					attachChild(statusIcon);
				}
				
			});
			break;
		case FOUND_SOMETHING:
			activity.runOnUpdateThread(new Runnable() {

				@Override
				public void run() {
					try {
						detachChild(statusIcon);
					} catch (Exception e) {}
					statusIcon = new Sprite(0, 0, ResourcesManager.getInstance().getHaveSomethingIconRegion(), vbom);
					attachChild(statusIcon);
				}
				
			});
			break;
		case FOUND:
			activity.runOnUpdateThread(new Runnable() {

				@Override
				public void run() {
					try {
						detachChild(statusIcon);
					} catch (Exception e) {}
					statusIcon = new Sprite(0, 0, ResourcesManager.getInstance().getGoIconRegion(), vbom);
					attachChild(statusIcon);
				}				
			});
			
			break;
		default:
			break;
		}
	}
	
	/**
	 * Metoda wywoływana przy przełączaniu sceny. Uruchamia bluetooth i prosi o pozwolenie na stanie
	 * się widzialnym dla innych urządzeń.
	 */
	public void onStart() {
		discoverableIntent = new
				Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		activity.startActivity(discoverableIntent);
		
		mBluetoothAdapter.enable();
	}
	
	/**
	 * Metoda wywoływana przy opuszczaniu sceny. Restartuje menu i zaprzestaje poszukiwania urządzeń bluetooth
	 * (jeśli były poszukiwane)
	 */
	public void onStop() {
		activity.runOnUpdateThread(new Runnable() {

			@Override
			public void run() {
				try {
					detachChild(statusIcon);
					menuChildScene.attachChild(hostItem);
					menuChildScene.attachChild(joinItem);
					menuChildScene.registerTouchArea(hostItem);
					menuChildScene.registerTouchArea(joinItem);
					detachChild(actionText);
					activity.stopService(discoverableIntent);
				} catch (Exception e) {}
			}
			
		});
	}
	
	/**
	 * Wyłącza bluetooth
	 */
	public void disableBluetooth() {
		try {
			mBluetoothAdapter.disable();
		} catch (Exception e) {
			Debug.e(e);
		}
	}
}
