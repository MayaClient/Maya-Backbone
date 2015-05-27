package com.codingforcookies.mayaclientapi.src.texture;

import java.awt.image.BufferedImage;

public class MQueueTexture {
	public String name = "";
	public BufferedImage texture;
	public Runnable loadedCallback;
	
	public MQueueTexture(String name, BufferedImage texture, Runnable loadedCallback) {
		this.name = name;
		this.texture = texture;
		this.loadedCallback = loadedCallback;
	}
}
