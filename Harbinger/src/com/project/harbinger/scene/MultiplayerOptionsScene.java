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

public class MultiplayerOptionsScene extends BaseScene implements IOnMenuItemClickListener {

	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothDevice device;
	private MenuScene menuChildScene;
	private IMenuItem hostItem, joinItem, backItem;
	private Sprite statusIcon;
	private Text actionText;
	private Intent discoverableIntent;
	
	private static final short HOST = 0, JOIN = 1, BACK = 2;
	public static final short WAIT = 3, FOUND_SOMETHING = 4, FOUND = 5;
	
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
	            	client = new BluetoothClient(device, activity.getEngine());
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

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_MULTIPLAYER_OPTIONS;
	}

	@Override
	public void disposeScene() {
		detachSelf();
		dispose();
	}
	
	private void createBackground() {
		setBackground(new Background(Color.BLACK));
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
		case HOST:
			actionText.setText("Looking for player...");
			attachChild(actionText);
			(new Thread() {

				@Override
				public void run() {
					menuChildScene.detachChild(hostItem);
					menuChildScene.detachChild(joinItem);
					menuChildScene.unregisterTouchArea(hostItem);
					menuChildScene.unregisterTouchArea(joinItem);
					
					try {
						BluetoothServer server = new BluetoothServer(mBluetoothAdapter, activity.getEngine());
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
			actionText.setText("Looking for game...");
			attachChild(actionText);
			
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
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
	
	public void onStart() {
		discoverableIntent = new
				Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		//discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
		activity.startActivity(discoverableIntent);
		
		mBluetoothAdapter.enable();
	}
	
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
		
		/*try {
			mBluetoothAdapter.cancelDiscovery();
        	activity.unregisterReceiver(mReceiver);
		} catch (Exception e) {}*/
	}
	
	public void disableBluetooth() {
		mBluetoothAdapter.disable();
	}
}
