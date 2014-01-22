package com.project.harbinger.multiplayer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

import org.andengine.util.debug.Debug;

import com.project.harbinger.manager.SceneManager;
import com.project.harbinger.scene.MultiplayerOptionsScene;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;

/**Klasa obsługująca połączenie bluetooth od strony hostującego grę. Potrzebna jest tylko dlatego, że 
 * protokół TCP opiera się na modelu serwer-klient.
 * 
 * @author Łukasz Frącz
 *
 */
public class BluetoothServer extends BluetoothConnection {

	/**Socket serwera, czekający na połączenie*/
	private BluetoothServerSocket serverSocket;
	
	/**Konstruktor obiektu.
	 * @param bluetoothAdapter obiekt BluetoothAdapter
	 * @throws IOException Gdy pojawi się problem z połączeniem
	 */
	public BluetoothServer(BluetoothAdapter bluetoothAdapter) throws IOException {

		serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(getName(), UUID.fromString("6D2DF50E-06EF-C21C-7DB0-345099A5F64E"));
		
		Debug.e("Czekam");
		((MultiplayerOptionsScene) SceneManager.getInstance().getCurrentScene()).setStatus(MultiplayerOptionsScene.WAIT);
		socket = serverSocket.accept();
		
		Debug.e("Serwer połączony");
		((MultiplayerOptionsScene) SceneManager.getInstance().getCurrentScene()).setStatus(MultiplayerOptionsScene.FOUND);
		
		BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
		ois = new ObjectInputStream(bis);
		BufferedOutputStream obs = new BufferedOutputStream(socket.getOutputStream());
		oos = new ObjectOutputStream(obs);
		oos.flush();
		
		SceneManager.getInstance().loadMultiplayerGameScene(this, false);
	}
	
	/** Metoda run() wątku
	 * @see com.project.harbinger.multiplayer.BluetoothConnection#run()
	 */
	public void run() {
		super.run();
		try {
			serverSocket.close();
		} catch (Exception e) {
			Debug.e("Dupa");
		}
	}
}
