package com.project.harbinger.multiplayer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.andengine.util.debug.Debug;

import android.bluetooth.BluetoothSocket;

import com.project.harbinger.scene.MultiplayerGameScene;

/**Nadrzędna klasa obsługująca połączenie bluetooth.
 * 
 * Gra wieloosobowa opiera się na komunikatach tekstowych wysyłanych przez dwie gry. Przykładami takich komend są:
 * "move-2;4", co oznacza "przeniś drugiego gracza na współrzędne 2,4"
 * "missile-10;12@-5", co oznacza "stwórz wystrzał o id -5 na współrzędnych 12,12"
 * 
 * @author Łukasz Frącz
 *
 */
public abstract class BluetoothConnection extends Thread {
	
	/**Strumień wyjściowy*/
	ObjectOutputStream oos;
	/**Strumień wejściowy*/
	ObjectInputStream ois;
	/**Socket*/
	BluetoothSocket socket;
	/**Scena gry wieloosobowej*/
	private MultiplayerGameScene gameScene;
	/**Status działania. Gdy false to wątek kończy pracę*/
	private boolean status;
	
	/**Komenda wysyłana przez multiplayer oznaczająca - Zatrzymaj grę*/
	private static final String PAUSE = "pause";
	/**Komenda wysyłana przez multiplayer oznaczająca - Przenieś drugiego gracza*/
	private static final String MOVE = "move";
	/**Komenda wysyłana przez multiplayer oznaczająca - Stwórz wystrzał*/
	private static final String MISSILE = "missile";
	/**Komenda wysyłana przez multiplayer oznaczająca - Zniszcz*/
	private static final String DESTROY = "destroy";
	/**Komenda wysyłana przez multiplayer oznaczająca - Wznów grę*/
	private static final String RESUME = "resume";
	/**Komenda wysyłana przez multiplayer oznaczająca - Dodaj punkty*/
	private static final String SCORE = "score";
	/**Odziela komendę od jej argumentów*/
	private static final String DASH = "-";
	/**Odziela współrzędne*/
	private static final String SEMICOLON = ";";
	/**Odziela współrzędne od numeru id*/
	private static final String AT = "@";
	/**Komenda wysyłana przez multiplayer oznaczająca - Koniec gry, mój wynik:*/
	private static final String MYSCORE = "finished";
	/**Komenda wysyłana przez multiplayer oznaczająca - Wczytałem planszę*/
	private static final String LOADED = "loaded";
	/**Komenda wysyłana przez multiplayer oznaczająca - Nie żyję*/
	private static final String DEAD = "dead";
	/**Komenda wysyłana przez multiplayer oznaczająca - Weź moje życie*/
	private static final String YES = "yes";
	/**Komenda wysyłana przez multiplayer oznaczająca - Nie dostaniesz życia. Koniec gry*/
	private static final String NO = "no";
	
	/**Ustawia pole ze sceną gry
	 * @param gameScene Scena na której toczy się gra wieloosobowa
	 */
	public void setGameScene(MultiplayerGameScene gameScene) {
		this.gameScene = gameScene;
	}
	
	/* Metoda run() wątku
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		status = true;
		
		while (status) {
			String message = "";
			try {
				message = (String) ois.readObject();
			} catch (Exception e) {
				Debug.e(e);
				gameScene.screwYou();
				status = false;
				break;
			}
			
			int dash = message.indexOf(DASH);
			String action = message.substring(0, dash);
			Debug.e(message);
			if (action.equals(MOVE)) {
				int semicolon = message.indexOf(SEMICOLON);
				float x = Float.valueOf(message.substring(dash + 1, semicolon));
				float y = Float.valueOf(message.substring(semicolon + 1));
				
				gameScene.movePlayer2(x, y);
			} else if (action.equals(MISSILE)) {
				int semicolon = message.indexOf(SEMICOLON);
				int at = message.indexOf(AT);
				float x = Float.valueOf(message.substring(dash + 1, semicolon));
				float y = Float.valueOf(message.substring(semicolon + 1, at));
				int id = Integer.valueOf(message.substring(at + 1));
				
				gameScene.addMissile(x, y, id);
			} else if (action.equals(PAUSE)) {
				gameScene.pauseGame();
			} else if (action.equals(RESUME)) {
				gameScene.resumeGame();
			} else if (action.equals(DESTROY)) {
				int id = Integer.valueOf(message.substring(dash + 1));
				gameScene.setToDestroy(id);
			} else if (action.equals(SCORE)) {
				int score = Integer.valueOf(message.substring(dash + 1));
				gameScene.addScore(score);
			} else if (action.equals(MYSCORE)) {
				int score = Integer.valueOf(message.substring(dash + 1));
				gameScene.updateOpponentScore(score);
			} else if (action.equals(DEAD)) {
				gameScene.partnerDead();
			} else if (action.equals(YES)) {
				gameScene.takeLife();
			} else if (action.equals(NO)) {
				gameScene.screwYou();
			} else if (action.equals(LOADED)) {
				gameScene.opponentIsReady();
			}
		}
		
		try {
			ois.close();
			oos.close();
			socket.close();
		} catch (IOException e) {
			Debug.e(e);
		}
	}

	/**
	 * Kończy wątek, a tym samym połączenie bluetooth.
	 */
	public void stopConnection() {
		status = false;
	}
	
	/**Wysyła wiadomość przez bluetooth
	 * @param message Wiadomość do wysłania
	 */
	private synchronized void sendMessage(String message) {
		try {
			oos.writeObject(message);
			oos.flush();
		} catch (IOException e) {
			Debug.e(e);
			gameScene.screwYou();
			status = false;
		}
	}
	
	/**
	 * Wysyła polecenie zatrzymania gry
	 */
	public void sendPause() {
		sendMessage(PAUSE + DASH);
	}
	
	/**
	 * Wysyła polecenie wznowienia gry
	 */
	public void sendResume() {
		sendMessage(RESUME + DASH);
	}
	
	/**Wysyła polecenie przeniesienia drugiego gracz
	 * @param x Nowa współrzędna x
	 * @param y Nowa współrzędna y
	 */
	public void sendMove(float x, float y) {
		sendMessage(MOVE + DASH + x + SEMICOLON + y);
	}
	
	/**Wysyła polecenie stworzenia wystrzału
	 * @param x Współrzędna x
	 * @param y Współrzędna y
	 * @param id Numer id wystrzału
	 */
	public void sendMissile(float x, float y, int id) {
		sendMessage(MISSILE + DASH + x + SEMICOLON + y + AT + id);
	}
	
	/**Wysyła polecenie zniszczenia obiektu
	 * @param id Numer id obiektu do zniszczenia
	 */
	public void sendDestroy(int id) {
		sendMessage(DESTROY + DASH + id);
	}
	
	/**Wysyła polecenie zwiększenia licznika punktów
	 * @param score O ile punktów zwiększyć licznik
	 */
	public void sendScore(int score) {
		sendMessage(SCORE + DASH + score);
	}
	
	/**Wysyła informacje o ilości swoich punktów
	 * @param score Ilość punktów
	 */
	public void sendMyscore(int score) {
		sendMessage(MYSCORE + DASH + score);
	}
	
	/**
	 * Wysyła komunikat o załadowaniu planszy
	 */
	public void sendLoaded() {
		sendMessage(LOADED + DASH);
	}
	
	/**
	 * Wysyła komunikat o śmierci
	 */
	public void sendDead() {
		sendMessage(DEAD + DASH);
	}
	
	/**
	 * Wysyła komunikat o o przekazaniu życia drugiemu graczowi
	 */
	public void sendYes() {
		sendMessage(YES + DASH);
	}
	
	/**
	 * Wysyła komunikat o nieprzekazaniu życia. Kończy grę
	 */
	public void sendNo() {
		sendMessage(NO + DASH);
	}
}
