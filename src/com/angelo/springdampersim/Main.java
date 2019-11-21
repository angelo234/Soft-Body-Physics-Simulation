package com.angelo.springdampersim;

import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;

import com.angelo.springdampersim.physics.Map;
import com.angelo.springdampersim.physics.Physics;

public class Main {
	
	//Updates per Second
	private static final int UPS = 10000;
	private static final int FPS = 144;
	
	private static final double TIME_U = 1000000000 / UPS;
	private static final double TIME_F = 1000000000 / FPS;
	
	public static Display display;
	public static Physics physics;
	public static Map map;

	public static boolean isPaused = true;
	public static double simulationSpeed = 1;
	
	private static long timeElapsed;
	private static long lastFrameTime;
	private static long lastTime;
	private static double deltaF = 0;
	private static double deltaU = 0;
	private static int ticks;
	private static int ticksAvg;
	private static long ticksTimer;
	
	private static void init(){	
		physics = new Physics();
		map = new Map();
		display = new Display();
		
		lastFrameTime = System.nanoTime();
		lastTime = System.nanoTime();	
		timeElapsed = System.nanoTime();
	}
	
	private static void update(){
		double delta = getDeltaTime();
		timeElapsed = System.nanoTime();

		input();

		if(!isPaused) {
			Display.valuesTimer += delta;
			
			physics.update(delta * simulationSpeed, TimeUnit.NANOSECONDS.toSeconds(timeElapsed));
			
			calculateUPS();
		}
		
		
	}
	
	private static void render(){
		display.requestFocus();
		display.updateDisplay();
	}
	
	static boolean pauseHeld = false;
	static boolean upButtonHeld = false;
	static boolean downButtonHeld = false;
	
	private static void input() {	
		if(Input.keys[KeyEvent.VK_P]) {
			if(!pauseHeld) {
				isPaused = !isPaused;
				pauseHeld = true;
			}
		}
		else {
			if(pauseHeld) {
				pauseHeld = false;
			}		
		}
		if(Input.keys[KeyEvent.VK_EQUALS]) {
			if(!upButtonHeld) {
				
				simulationSpeed /= 0.5;
				
				upButtonHeld = true;
			}
		}
		else {
			if(upButtonHeld) {
				upButtonHeld = false;
			}		
		}
		if(Input.keys[KeyEvent.VK_MINUS]) {
			if(!downButtonHeld) {
				
				simulationSpeed *= 0.5;
				
				downButtonHeld = true;
			}
		}
		else {
			if(downButtonHeld) {
				downButtonHeld = false;
			}		
		}
	}
	
	private static double getDeltaTime(){
		long currentFrameTime = System.nanoTime();
		double delta = (currentFrameTime - lastFrameTime) / 1000000000.0;
		lastFrameTime = currentFrameTime;	
		
		return delta;
	}
	
	public static void calculateUPS() {
		long now = System.currentTimeMillis();

		ticks++;

		if (now - ticksTimer > 1000) {
			ticksTimer = now;
			ticksAvg = ticks;
			ticks = 0;
		}
	}
	
	public static int getTicksAvg() {
		return ticksAvg;
	}

	public static void main(String[] args) throws InterruptedException {
		init();
		
		while(true){
			long now = System.nanoTime();
			deltaF += (now - lastTime) / TIME_F;
			deltaU += (now - lastTime) / TIME_U;
			lastTime = now;
			
			if(deltaU >= 1) {
				deltaU--;
				
				update();		
			}
			
			if(deltaF >= 1) {
				deltaF--;
				
				render();			
			}			
		}
	}
}
