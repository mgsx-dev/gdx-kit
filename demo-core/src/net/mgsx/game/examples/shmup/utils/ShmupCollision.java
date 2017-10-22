package net.mgsx.game.examples.shmup.utils;

public class ShmupCollision {

	public static final short background = 1 << 0;
	public static final short player = 1 << 1;
	public static final short playerBullet = 1 << 2;
	public static final short enemy = 1 << 3;
	public static final short enemyBullet = 1 << 4;
	
	public static final short playerMask = background | enemy | enemyBullet;
	
	public static final short enemyMask = player | playerBullet;
	
	public static final short enemyBulletMask = background | player;
	public static final short playerBulletMask = background | enemy;
	
}
