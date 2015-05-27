package com.codingforcookies.mayaclientapi.src;

import org.lwjgl.opengl.GL11;

import com.codingforcookies.mayaclientapi.src.font.MayaFontRenderer;
import com.codingforcookies.mayaui.src.ui.RenderHelper;

public class RenderLoading {
	public static String process = "";
	public static boolean complete = false;
	
	private static final float DEG2RAD = (float)Math.PI / 180F;
	private static float loadingDegree = 0;
	private static float loadingOffset = 0;
	
	public static void draw(String text, float x, float y, float diameter, float delta) {
		float radius = diameter / 2F - 3F;
		GL11.glPushMatrix();
		{
			GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			GL11.glTranslatef(x, MayaAPI.SCREEN_HEIGHT - y, 0F);
			GL11.glColor4f(0F, 0F, 0F, .5F);
			
			RenderHelper.renderBox(-radius - 6F, radius + 6F, diameter + 6F, diameter + 6F);
			GL11.glColor4f(1F, 1F, 1F, .9F);
			
			MayaFontRenderer.draw(text, -MayaFontRenderer.getStringWidth(text) / 2 + 1F, MayaFontRenderer.CHAR_WIDTH_HALF);

			GL11.glLineWidth(diameter / 20F);
			GL11.glBegin(GL11.GL_LINE_STRIP);
			if(loadingDegree < 360F) {
				for(int i = 0; i < loadingDegree; i++) {
					float degInRad = (-i + 90F - loadingDegree - loadingOffset) * DEG2RAD;
					GL11.glVertex2f((float)Math.cos(degInRad) * radius, (float)Math.sin(degInRad) * radius);
				}
			}else{
				loadingOffset += .15F * delta;
				for(float i = loadingOffset; i > loadingDegree - 720 + loadingOffset; i--) {
					float degInRad = (-i + 90F) * DEG2RAD;
					GL11.glVertex2f((float)Math.cos(degInRad) * radius, (float)Math.sin(degInRad) * radius);
				}
			}
			GL11.glEnd();
		}
		GL11.glPopMatrix();
		
		if(loadingDegree > 720F)
			loadingDegree = 0;
		else
			loadingDegree += .3F * delta;
	}
}