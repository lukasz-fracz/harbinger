package com.project.harbinger.scene;

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
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        // When discovery finds a device
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            // Get the BluetoothDevice object from the Intent
	        	Debug.e("Mam");
	        	BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	        	mBluetoothAdapter.cancelDiscovery();
	            BluetoothClient client = new BluetoothClient(device, activity.getEngine());
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
		
		IMenuItem singleItem = new ScaleMenuItemDecorator(new SpriteMenuItem(
				0, resourcesManager.getSingleButtonRegion(), vbom),
				1.2f, 1);
		IMenuItem multiItem = new ScaleMenuItemDecorator(new SpriteMenuItem(
				1, resourcesManager.getMultiButtonRegion(), vbom),
				1.2f, 1);
		
		menuChildScene.addMenuItem(singleItem);
		menuChildScene.addMenuItem(multiItem);
		
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);
		
		singleItem.setPosition(singleItem.getX(), singleItem.getY() + 10);
		multiItem.setPosition(multiItem.getX(), multiItem.getY() + 10);
		
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
		case 0:
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
					BluetoothServer server = new BluetoothServer(mBluetoothAdapter);
					server.start();
				}
				
			});
			return true;
		case 1:
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
		default:
			return false;
		}
	} 
}
