package com.codingforcookies.mayaclientapi.src.texture;

import org.lwjgl.opengl.GL11;

/**
 * Class for a loaded texture
 * @author Stumblinbear
 */
public class MTexture {
	private int texture = 0;
	
	public MTexture(int textureid) {
		texture = textureid;
	}
	
	/**
	 * Bind the texture
	 */
	public void bind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
	}
}