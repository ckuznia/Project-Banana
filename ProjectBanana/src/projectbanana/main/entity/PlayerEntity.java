package projectbanana.main.entity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import projectbanana.main.CollisionEvent;
import projectbanana.main.Game;
import projectbanana.main.World;
import projectbanana.main.util.HUD;
import projectbanana.main.util.Sound;
import projectbanana.main.values.GeometryId;
import projectbanana.main.values.RotationId;

public abstract class PlayerEntity extends BufferedEntity {
	
	protected double speed = 10.0;
	protected double thrust = 0.25;
	protected double rotSpeed = 0.075;
	protected double rotThrust = rotSpeed * 0.05;
	
	private final HUD HUD = new HUD();
	
	private static final String PATH = "/spaceships/";
	private final String imagePath;
	private final String thrustImagePath;
	
	public ArrayList<Point> points = new ArrayList<Point>();
	
	public PlayerEntity(int x, int y, String imageName, String thrustImageName) {
		super(x, y, PATH + imageName, GeometryId.CIRCLE.getId());
		imagePath = PATH + imageName;
		thrustImagePath = PATH + thrustImageName;
		this.rotateImage();
		this.loadImage(imagePath);
		
		/*int dir = ((int) (Math.random() * 2) == 0 ? 1 : -1);
		velX = (Math.random() * 15) * dir;
		dir = ((int) (Math.random() * 2) == 0 ? 1 : -1);
		velY = (Math.random() * 15) * dir;*/
		//velDamping = .8;
	}
	
	@Override
	public void tick() {
		//System.out.println(x + " " + y);
		try {
			for(Entity entity : World.enemies) {
				CollisionEvent event = this.isCollidingWith(entity);
				if(event.isColliding()) {
					accX = accY = 0;
					velX = -velX;
					velY = -velY;
					x = lastValidX;
					y = lastValidY;
					Sound.BUMP.play();
					points.add(new Point((int)entity.getCenterX(), (int)entity.getCenterY()));
				}
			}
			
			
			
			
			
			// Respawn the spaceship
			if(Game.inputHandler.isEnter()) this.respawn();
			
			// Calculate movement
			else {
				// If turning
				if(Game.inputHandler.isLeft() || Game.inputHandler.isRight()) {
					if(Game.inputHandler.isLeft()) this.turn(RotationId.COUNTER_CLOCKWISE.getId(), rotSpeed, rotThrust);
					if(Game.inputHandler.isRight()) this.turn(RotationId.CLOCKWISE.getId(), rotSpeed, rotThrust);
				}
				
				// If not turning
				else if(rotVel != 0) this.applyRotVelDamping();
				
				// If accelerating
				if(Game.inputHandler.isDown()) {
					this.moveForward(thrust, speed);
					loadImage(thrustImagePath);
				}
				
				// If not accelerating
				else {
					if(vel != 0) this.applyVelDamping();
					loadImage(imagePath);
				}
				
				this.applyForces();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void render(Graphics g) {
		g.drawImage(op.filter(image, null), (int) x, (int) y, null);
		
		g.setColor(Color.GREEN);
		int boundingRad = (int)this.boundingRad;
		//if(this.getGeometryId() == GeometryId.CIRCLE.getId()) g.drawOval((int) this.x + (width / boundingRad), (int) this.y + (width / boundingRad), (int) boundingRad, (int) boundingRad);
		//else g.drawRect((int) this.x + boundingWidth / 2, (int) this.y + boundingHeight / 2, (int) boundingWidth, (int) boundingHeight);
	}
	
	public HUD getHUD() {
		return HUD;
	}
}
