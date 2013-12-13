package com.project.harbinger.scene;

import java.io.IOException;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;

import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.project.harbinger.manager.SceneManager;
import com.project.harbinger.manager.SceneManager.SceneType;
import com.project.harbinger.multiplayer.BluetoothClient;
import com.project.harbinger.multiplayer.BluetoothServer;

public class MultiPlayerOptionsScene extends BaseScene implements IOnMenuItemClickListener {

	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothDevice device;
	private MenuScene menuChildScene;
	
	private static final int HOST = 0, JOIN = 1, BACK = 2;
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        // When discovery finds a device
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            // Get the BluetoothDevice object from the Intent
	        	Debug.e("Mam");
	        	BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE); 	
	            BluetoothClient client;
	            try {
	            	client = new BluetoothClient(device, activity.getEngine());
	            } catch (IOException e) {
	            	return;
	            }
	            mBluetoothAdapter.cancelDiscovery();
	            client.start();
	            activity.unregisterReceiver(mReceiver);
	            
	        }
	    }
	};

	
	@Override
	public void createScene() {
		createBackground();
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(0, 0);
		
		IMenuItem hostItem = new ScaleMenuItemDecorator(new SpriteMenuItem(
				HOST, resourcesManager.getHostButtonRegion(), vbom),
				1.2f, 1);
		IMenuItem joinItem = new ScaleMenuItemDecorator(new SpriteMenuItem(
				JOIN, resourcesManager.getJoinButtonRegion(), vbom),
				1.2f, 1);
		IMenuItem backItem = new ScaleMenuItemDecorator(new SpriteMenuItem(
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
		Intent discoverableIntent = new
				Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
		activity.startActivity(discoverableIntent);
		/*IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		activity.registerReceiver(mReceiver, filter);
		Intent discoverableIntent = new
				Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
		activity.startActivity(discoverableIntent);*/
	}

	@Override
	public void onBackKeyPressed() {
		/*activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				mBluetoothAdapter.startDiscovery();
				BluetoothServer server = new BluetoothServer(mBluetoothAdapter);
				server.start();
			}
			
		});*/		
		
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
		setBackground(new Background(Color.YELLOW));
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
		case HOST:
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
					BluetoothServer server = new BluetoothServer(mBluetoothAdapter, activity.getEngine());
					server.start();
				}
				
			});
			return true;
		case JOIN:
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
					Debug.e(String.valueOf(mBluetoothAdapter.startDiscovery()));
					mBluetoothAdapter.startDiscovery();
					/*BluetoothServer server = new BluetoothServer(mBluetoothAdapter);
					server.start();*/
				}
				
			});
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			activity.registerReceiver(mReceiver, filter);
			return true;
		case BACK:
			SceneManager.getInstance().backToMenu();
			return true;
		default:
			return false;
		}
	} 
}
