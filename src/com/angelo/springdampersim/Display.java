package com.angelo.springdampersim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.text.DecimalFormat;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.Vector2d;

import com.angelo.springdampersim.physics.Physics;
import com.angelo.springdampersim.physics.PhysicsObject;
import com.angelo.springdampersim.physics.Spring;

public class Display {

	public static boolean VECTORS_DISPLAYED = false;
	public static boolean RENDER_SPRING = true;
	
	public static final int WIDTH = 800;
	public static final int HEIGHT = 800;

	public static final double PIXELS_PER_METER = 100;

	public static double valuesTimer;
	
	public static DecimalFormat df = new DecimalFormat("0.00");
	
	private static final long serialVersionUID = 1L;
	
	private JFrame frame;
	private JPanel panel;

	private static int framesCount;
	private static int timePast;
	private static long framesTimer;
	private static int framesCountAvg;
	
	private String oldPosStr;
	private String oldVelStr;
	private String oldAccStr;
	private Vector2d oldVel = new Vector2d();
	private Vector2d oldAcc = new Vector2d();

	public Display() {
		frame = new JFrame("Spring Damper Simulation");

		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));

		panel = new JPanel() {
			private static final long serialVersionUID = 1L;

			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				for(PhysicsObject physicsObject : Physics.physicsObjects) {
					physicsObject.render(g2);
					
					if(VECTORS_DISPLAYED) {		
						
						if(physicsObject.state.position.length() >= 0.01) {
							//drawArrow(g2, Color.GREEN, pointMass.velocity, pointMass.rectangle.getCenterX(), pointMass.rectangle.getCenterY());	
						}
						if(physicsObject.acceleration.length() >= 0.01) {
							double scale = 20;
							
							Vector2d newVec = new Vector2d(physicsObject.acceleration);
							newVec.normalize();
							newVec.scale(scale);
							
							drawArrow(g2, Color.BLUE, newVec, physicsObject.shape.getBounds2D().getCenterX(), physicsObject.shape.getBounds2D().getCenterY());
						}
						
						Vector2d newVec2 = new Vector2d(physicsObject.vector);
						newVec2.scale(50);
						
						System.out.println(newVec2.toString());
						
						drawArrow(g2, Color.PINK, newVec2, physicsObject.shape.getBounds2D().getCenterX(), physicsObject.shape.getBounds2D().getCenterY());
					}
				}	
				
				if(RENDER_SPRING) {
					for(Spring spring : Physics.springs) {
						spring.render(g2);
					}
				}
				
				g2.setColor(Color.BLACK);
				g2.drawString("FPS: " + framesCountAvg, 5, 15);
				g2.drawString("UPS: " + Main.getTicksAvg(), 5, 35);
				g2.drawString("Simulation Speed: x"+df.format(Main.simulationSpeed), 5, 55);
				
				//drawStringWithOutline(g2, "FPS: " + framesCountAvg, 5, 15, 12);
				//drawStringWithOutline(g2, "UPS: " + Main.getTicksAvg(), 5, 35, 12);
				//drawStringWithOutline(g2, "Simulation Speed: x"+df.format(Main.simulationSpeed), 5, 55, 12);
				
				if(Main.isPaused) {	
					drawStringWithOutline(g2, "Simulation Paused...", 190, 400, 48);
				}

				/*
				if(valuesTimer >= 0.1) {
					
					oldPosStr = "(" + df.format(Physics.pointMass.position.getX()) + ", "
							+ df.format(Physics.pointMass.position.getY()) + ")";
					oldVelStr = "(" + df.format(Physics.pointMass.velocity.getX()) + ", "
							+ df.format(Physics.pointMass.velocity.getY()) + ")";
					oldAccStr = "(" + df.format(Physics.pointMass.acceleration.getX() / 9.81) + ", "
							+ df.format(Physics.pointMass.acceleration.getY() / 9.81) + ")";
	
					
					
					valuesTimer = 0;
				}
				
				g2.drawString("Position (m):         " + oldPosStr, 520, 50);
				g2.drawString("Velocity (m/s):      " + oldVelStr, 520, 90);
				g2.drawString("Acceleration (g):  " + oldAccStr, 520, 130);
				
				if(Physics.pointMass.velocity.length() >= 0.1) {
					drawArrow(g2, oldVel, 750, 90);	
				}
				if(Physics.pointMass.acceleration.length() >= 0.1) {
					drawArrow(g2, oldAcc, 750, 130);
				}
				*/
				g2.dispose();
			}
		};

		panel.setPreferredSize(new Dimension(Display.WIDTH, Display.HEIGHT));

		Input input = new Input();
		
		frame.addKeyListener(input);
		panel.addMouseListener(input);
		panel.addMouseMotionListener(input);
		frame.add(panel);		
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	private void drawStringWithOutline(Graphics2D g2, String text, double x, double y, float size) {
		AffineTransform saveAT = g2.getTransform();
		
		AffineTransform transform = new AffineTransform();
		transform.translate(x, y);
		g2.transform(transform);
		g2.setColor(Color.black);
		FontRenderContext frc = g2.getFontRenderContext();
		TextLayout tl = new TextLayout(text, g2.getFont().deriveFont(size), frc);
		Shape shape = tl.getOutline(null);
		g2.setStroke(new BasicStroke(3f));
		g2.setTransform(transform);
		g2.draw(shape);
		g2.setColor(Color.GREEN);
		g2.fill(shape);
		
		
		g2.setTransform(saveAT);
	}

	private Path2D arrowHead = new ArrowHead();

	private void drawArrow(Graphics2D g2, Color color, Vector2d vec, double x, double y) {
		Color oldColor = new Color(g2.getColor().getRed(), g2.getColor().getGreen(), g2.getColor().getBlue());
		
		g2.setColor(color);
		AffineTransform saveAT = g2.getTransform();
		
		double x2 = vec.x;
		double y2 = -vec.y;
		
		g2.setStroke(new BasicStroke(2));
		g2.drawLine((int)x, (int)y, (int)(x2 + x), (int)(y2 + y));
		
		AffineTransform at = new AffineTransform();
		at.translate(x + x2, y + y2);
		at.rotate(2 * Math.PI - Math.atan2(vec.y, vec.x), 0, 0);
		g2.setTransform(at);

		g2.draw(arrowHead);
		
		g2.setColor(oldColor);
		g2.setTransform(saveAT);
		g2.setStroke(new BasicStroke(1));
		
	}

	public void requestFocus() {
		frame.requestFocus();
	}

	public void updateDisplay() {
		calculateFPS();
		
		panel.paintImmediately(0, 0, Display.WIDTH, Display.HEIGHT);
	}
	
	public static void calculateFPS() {
		long now = System.currentTimeMillis();

		framesCount++;

		if (now - framesTimer > 1000) {
			framesTimer = now;
			framesCountAvg = framesCount;
			framesCount = 0;
		}
	}

	public class ArrowHead extends Path2D.Double {

		public ArrowHead() {
			moveTo(-4, -4);
			lineTo(4, 0);
			moveTo(-4, 4);
			lineTo(4, 0);
		}

	}
}
