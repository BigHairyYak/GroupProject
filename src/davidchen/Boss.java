package davidchen;

import java.awt.Graphics;

public class Boss extends Mob
{
	int x, y, w, h, health;
	int phase = 1, explodeTime;
	float velocity = 5;
	Room room;
	
	public Boss(int x, int y, int width, int height, int health, Room bossRoom) 
	{
		super(x, y, width, height, health);
		health = 30; room = bossRoom;
		this.x = x; this.y = y; w = width; h = height; this.health = health;
		explodeTime = 100;
	}
	/*
	 * MOVE
	 * Should technically be act()
	 * but I'm lazy and running out
	 * of time. 
	 */
	@Override
	public void move(Entity other)
	{		
		if (phase == 1) //spawning enemies
		{
			if (Driver.RNG.nextInt(151) == 150)
				spawn(Driver.RNG.nextInt(3) + 1);
		}
		if (phase == 2) //teleporting and spawning
		{
			if (Driver.RNG.nextInt(176) == 175)
			{
				x = Driver.RNG.nextInt(800) + 200;
				y = Driver.RNG.nextInt(400) + 200;
			}
			if (Driver.RNG.nextInt(151) == 150)
				spawn(Driver.RNG.nextInt(5) + 1);
		}
		if (phase == 3) //No more spawning, just charging at the player
		{
			angle = Math.atan2(Driver.view.dungeon.player.y - y, Driver.view.dungeon.player.x - x);
			x += velocity * Math.cos(angle);
			y += velocity * Math.sin(angle);
			//super.move(Driver.view.dungeon.player);
		}
		if (health > 0)
		{
			YakEngine.createSystem(x + 45, y + 90, .5f * 30/health, 4);
			YakEngine.createSystem(x + 120, y + 90, .5f * 30/health, 4);
		}
		if (health <= 0)
		{

			phase = 4; //unused phase, allows for proper death 'spasms'
			x += Driver.RNG.nextInt(6)-5;
			y += Driver.RNG.nextInt(6)-5;

			if (Driver.RNG.nextInt(10) < 5)
				YakEngine.createSystem(x + w/2 + (Driver.RNG.nextInt(61)-60), y + h/2+(Driver.RNG.nextInt(61)-60), 2.5f, 2);
			
			explodeTime--;
		}
		if (explodeTime <= 0)
		{
			dead = true;
			YakEngine.createSystem(x + w/2, y + h/2, 8.5f, 2);
		}
		System.out.println("BOSS ACTING");
	}
	public void draw(Graphics g)
	{
		if (phase == 1)
			g.drawImage(Driver.view.dungeon.boss1, x, y, w, h, null);
		if (phase == 2)
			g.drawImage(Driver.view.dungeon.boss2, x, y, w, h, null);
		if (phase > 2)
			g.drawImage(Driver.view.dungeon.boss2, x, y, w, h, null);
		g.drawRect(x, y, w, h);
		g.drawString("BOSS HEALTH: " + health, 50, 100);
	}
	public void spawn(int amount)
	{
		Mob[] newMobs = new Mob[amount];
		for (int q = 0; q < amount; q++)
		{
			int dx = x + w/2 + (Driver.RNG.nextInt(201)-200);
			int dy = y + h/2 + (Driver.RNG.nextInt(201)-200);
			
			/*
			 * Creates new mobs that are twice as fast
			 * as normal, should be extra spooky
			 */
			newMobs[q] = new Mob(dx, dy, 100, 100, 4);
			newMobs[q].setXMultiplier(3);
			newMobs[q].setYMultiplier(3);
			newMobs[q].bossSpawn = true;
			YakEngine.createSystem(dx, dy, 1.5f, 2);
		}
		room.addMobs(newMobs);
	}
	public void lowerHealth(int increment)
	{
		health -= increment;
		if (health >= 20)
			phase = 1;
		if (health >= 10 && health < 20)
			phase = 2;
		if (health < 10)
			phase = 3;
		if (health < 0)
			phase = 4;
	}
}
