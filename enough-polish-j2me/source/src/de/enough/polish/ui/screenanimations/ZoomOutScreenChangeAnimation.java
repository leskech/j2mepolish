//#condition polish.usePolishGui && polish.midp2

/*
 * Created on 27-May-2005 at 18:54:36.
 * 
 * Copyright (c) 2010 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.ui.screenanimations;

import de.enough.polish.ui.CssAnimation;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.ui.ScreenChangeAnimation;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ImageUtil;

/**
 * <p>Magnifies the new screen.</p>
 * <p>Activate this animation by specifying it in the corresponding screen's style:
 * <pre>
 * .myAlert {
 * 		//#if polish.midp2
 * 			screen-change-animation: zoomOut;
 * 		//#endif
 * }
 * </pre>
 * </p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2011</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ZoomOutScreenChangeAnimation extends ScreenChangeAnimation {
	private int scaleFactor = 800;
	private int[] scaledScreenRgb;

	/**
	 * Creates a new animation 
	 */
	public ZoomOutScreenChangeAnimation() {
		super();
		this.animationForwardFunction = CssAnimation.FUNCTION_EXPONENTIAL_OUT;
		this.animationBackwardFunction = CssAnimation.FUNCTION_EXPONENTIAL_IN;
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#show(de.enough.polish.ui.Style, javax.microedition.lcdui.Display, int, int, javax.microedition.lcdui.Image, javax.microedition.lcdui.Image, de.enough.polish.ui.Screen)
	 */
	protected void onShow(Style style, Display dsplay, int width, int height,
			Displayable lstDisplayable, Displayable nxtDisplayable, boolean isForward  ) 
	{
		if (isForward) {
			this.useLastCanvasRgb = false;
			this.useNextCanvasRgb = true;
		} else {
			this.useLastCanvasRgb = true;
			this.useNextCanvasRgb = false;			
		}
		this.scaledScreenRgb = new int[ width * height ];
		super.onShow(style, dsplay, width, height, lstDisplayable, nxtDisplayable, isForward );
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#animate(long, long)
	 */
	protected boolean animate(long passedTime, long duration) {
		if (passedTime > duration) {
			this.scaledScreenRgb = null;
			return false;
		}
		int[] rgb;
		int startFactor, endFactor;
		int startOpacity, endOpacity;
		if (this.isForwardAnimation) {
			startFactor = this.scaleFactor;
			endFactor = 100;
			startOpacity = 10;
			endOpacity = 255;
			rgb = this.nextCanvasRgb;
		} else {
			startFactor = 100;
			endFactor = this.scaleFactor;
			startOpacity = 255;
			endOpacity = 10;
			rgb = this.lastCanvasRgb;
		}
		int promille = calculateAnimationPoint(0, 1000, passedTime, duration);
		int factor = startFactor + ((endFactor - startFactor) * promille) / 1000;
		int opacity = startOpacity + ((endOpacity - startOpacity) * promille) / 1000;
		ImageUtil.scale( opacity, factor, this.screenWidth, this.screenHeight, rgb, this.scaledScreenRgb);
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
	 */
	public void paintAnimation(Graphics g) {
		Image canvasImage;
		if (this.isForwardAnimation) {
			canvasImage = this.lastCanvasImage;
		} else {
			canvasImage = this.nextCanvasImage;
		}
		g.drawImage( canvasImage, 0, 0, Graphics.TOP | Graphics.LEFT );
		g.drawRGB(this.scaledScreenRgb, 0, this.screenWidth, 0, 0, this.screenWidth, this.screenHeight, true );
	}

}
