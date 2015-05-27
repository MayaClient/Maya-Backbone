package com.codingforcookies.mayabackbone.src;

import java.util.Random;

import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import com.codingforcookies.mayaclientapi.src.MayaAPI;
import com.codingforcookies.mayaclientapi.src.RenderLoading;
import com.codingforcookies.mayaclientapi.src.texture.MayaTextureLoader;
import com.codingforcookies.mayaui.src.MayaUI;
import com.codingforcookies.mayaui.src.notification.MNotification;
import com.codingforcookies.mayaui.src.notification.MNotificationType;
import com.codingforcookies.mayaui.src.ui.theme.MAlign;
import com.codingforcookies.mayaui.src.ui.theme.MayaColor;
import com.codingforcookies.mayaui.src.ui.theme.components.UIButton;
import com.codingforcookies.mayaui.src.ui.theme.components.UIProgressBar;
import com.codingforcookies.mayaui.src.ui.window.MWindow;
import com.codingforcookies.mayaui.src.ui.window.MWindowBase;
import com.codingforcookies.mayaui.src.ui.window.preset.PerformanceMonitor;

/**
 * Simple testing program
 * @author Stumblinbear
 */
public class Test {
	long lastFrame;
    int fps;
    long lastFPS;
	
	public Test() {
		init();
		
		getDelta();
        lastFPS = getTime();
        
		boolean skipEvent = false;
		while(!Display.isCloseRequested()) {
			MayaTextureLoader.loadQueuedTexture();
			
			if(Display.wasResized()) {
				GL11.glMatrixMode(GL11.GL_PROJECTION);
				GL11.glLoadIdentity();
				GL11.glOrtho(0, Display.getWidth(), 0, Display.getHeight(), 1, -1);
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
				
				int changewidth = Display.getWidth() - MayaAPI.SCREEN_WIDTH;
				int changeheight = Display.getHeight() - MayaAPI.SCREEN_HEIGHT;
				
				MayaAPI.SCREEN_WIDTH = Display.getWidth();
				MayaAPI.SCREEN_HEIGHT = Display.getHeight();
				
				MayaUI.getUIManager().onWindowResized(skipEvent, changewidth, changeheight);
				
				if(!skipEvent)
					skipEvent = true;
			}
			
			float delta = getDelta();
			update(delta);
			updateFPS();
			
			render(delta);
			
			if(!RenderLoading.complete)
				RenderLoading.draw(RenderLoading.process, MayaAPI.SCREEN_WIDTH / 2, MayaAPI.SCREEN_HEIGHT / 2, 120, delta);
			
		    Display.update();
		}
		
		Display.destroy();
	}
	
	public float getDelta() {
        long time = getTime();
        float delta = (int)(time - lastFrame);
        lastFrame = time;
      
        return delta;
    }
	
	public long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }
	
	public void updateFPS() {
        if (getTime() - lastFPS > 1000) {
            Display.setTitle("Maya UI  v." + MayaUI.version + " - " + fps + "fps");
            fps = 0;
            lastFPS += 1000;
        }
        
        fps++;
    }
	
	public void init() {
		MayaUI.getUIManager().createWindow(new MWindowBase("prgbar", 0, 0, Display.getWidth(), 5) {
			UIProgressBar prgBar;
			public void init() {
				super.init();
				anchor = MAlign.TOPLEFT;
				
				prgBar = new UIProgressBar().setBounds(0, 0, Display.getWidth(), 5);
				addComponent(prgBar);
			}
			
			public void update(float delta) {
				prgBar.setProgress(prgBar.getProgress() + .0005F);
				
				if(prgBar.getProgress() >= 1F)
					prgBar.setProgress(0);
			}
			
			public void onWindowResized(int changewidth, int changeheight) {
				prgBar.width += changewidth;
			}
		});
		
		MayaUI.getUIManager().createWindow(new MWindow("Window 1", 10, 15, 300, 200) {
			public void init() {
				super.init();
				this.anchor = MAlign.TOPLEFT;
				
				addComponent(new UIButton("Thuper").setBounds(0, height - 40, 100, 30));
				addComponent(new UIButton("Duper").setBounds(width - 110, height - 70, 100, 60));
			}
		});
		
		MayaUI.getUIManager().createWindow(new MWindow("Window 2", -480, 15, 470, 200) {
			public void init() {
				super.init();
				this.anchor = MAlign.TOPRIGHT;
			}
		});
		
		new Thread() {
			public void run() {
				try {
					Thread.sleep(1000L);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
				
				MayaUI.addNotification(MNotificationType.INFO, "Loading complete.");
				try {
					Thread.sleep(1000L);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
				
				MNotification[] testNotifications = new MNotification[] {
						new MNotification(MNotificationType.WARNING, "Initializing to tha moon thrusters."),
						new MNotification(MNotificationType.HELP, "To tha moon thrusters are functioning."),
						new MNotification(MNotificationType.WARNING, "BLAST OFF!!"),
						new MNotification(MNotificationType.ERROR, "Out of fuel!"),
						new MNotification(MNotificationType.INFO, "Initiating order #374!"),
						new MNotification(MNotificationType.ERROR, "Self destruct activated.")
					};
				
				for(int i = 0; i < testNotifications.length; i++) {
					testNotifications[i].push();
					
					try {
						Thread.sleep(new Random().nextInt(2000));
					} catch(InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
		
		MayaUI.getUIManager().createWindow(new PerformanceMonitor());
		
		PerformanceMonitor.addSection("total", "update", new MayaColor("#DDAF08"));
		PerformanceMonitor.addSection("total", "render", new MayaColor("#2676AB"));
	}
	
	public void update(float delta) {
		PerformanceMonitor.startUpdateSection("update");
		PerformanceMonitor.startUpdateSection("update_screen");
		
		MayaUI.getUIManager().doUpdateUI(delta);

		PerformanceMonitor.endSection("update_screen");
		PerformanceMonitor.endSection("update");
	}
	
	public void render(float delta) {
		PerformanceMonitor.startRenderSection("render");
		PerformanceMonitor.startRenderSection("render_screen");
		
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
	    GL11.glColor3f(.1F, .1F, .1F);
	    
	    GL11.glBegin(GL11.GL_QUADS);
	    {
	        GL11.glVertex2f(0, 0);
			GL11.glVertex2f(Display.getWidth(), 0);
			GL11.glVertex2f(Display.getWidth(), Display.getHeight());
			GL11.glVertex2f(0, Display.getHeight());
	    }
	    GL11.glEnd();
	    
		MayaUI.getUIManager().doRenderUI();
		
		PerformanceMonitor.endSection("render_screen");
		PerformanceMonitor.endSection("render");
	}
}