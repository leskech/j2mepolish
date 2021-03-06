//#condition polish.usePolishGui && polish.midp2
/*
 * Created on Apr 25, 2007 at 11:45:01 PM.
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
package de.enough.polish.ui.itemviews;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.AnimationThread;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemView;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.DrawUtil;
import de.enough.polish.util.ImageUtil;

/**
 * <p>Fades out the item.</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Apr 25, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class FadeInItemView extends ItemView {
	
	private int[] rgbData;
	private int currentTransparency;
	private int startTransparency = 0;
	private int endTransparency = 255;
	private int deltaTransparency = 20;
	//#if polish.css.fade-in-next-style
		private Style nextStyle;
	//#endif

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#initContent(de.enough.polish.ui.Item, int, int)
	 */
	protected void initContent(Item parent, int firstLineWidth, int availWidth, int availHeight) {
		//TODO  question: should the view handle focused states and do this stuff or is it more appropriate for the application to do this?
		this.currentTransparency = this.startTransparency;
		initContentByParent(parent, firstLineWidth, availWidth, availHeight);
		int[] itemRgbData = UiAccess.getRgbDataOfContent( parent );
		if (this.currentTransparency != 0) {
			ImageUtil.setTransparencyOnlyForOpaque(this.currentTransparency, itemRgbData);
		}
		this.rgbData = itemRgbData;
		AnimationThread.addAnimationItem(parent);
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#animate()
	 */
	public boolean animate() {
		int transparency = this.currentTransparency + this.deltaTransparency;
		if (transparency >= this.endTransparency) {
			transparency = this.endTransparency;
			AnimationThread.removeAnimationItem(this.parentItem);
			if (transparency == 255) {
				removeViewFromParent();
				//#if polish.css.fade-in-next-style
					if (this.nextStyle != null) {
						this.parentItem.setStyle(this.nextStyle);
					}
				//#endif
				this.rgbData = null;
				this.currentTransparency = transparency;
				return true;
			}
		}
		int[] data = this.rgbData;
		if (data != null) {
			ImageUtil.setTransparencyOnlyForOpaque(transparency, data);
		}
		this.currentTransparency = transparency;
		return true;
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#setStyle(de.enough.polish.ui.Style)
	 */
	protected void setStyle(Style style) {
		super.setStyle(style);
		//#if polish.css.fade-in-transparency-start
			Integer startTransparencyInt = style.getIntProperty("fade-in-transparency-start");
			if (startTransparencyInt != null) {
				this.startTransparency = startTransparencyInt.intValue();
			}
		//#endif
		//#if polish.css.fade-in-transparency-end
			Integer endTransparencyInt = style.getIntProperty("fade-in-transparency-end");
			if (endTransparencyInt != null) {
				this.endTransparency = endTransparencyInt.intValue();
			}
		//#endif
		//#if polish.css.fade-in-transparency-delta
			Integer transparencyDeltaInt = style.getIntProperty("fade-in-transparency-delta");
			if ( transparencyDeltaInt != null) {
				this.deltaTransparency = transparencyDeltaInt.intValue();
			}
		//#endif
		//#if polish.css.fade-in-next-style
			this.nextStyle = (Style) style.getObjectProperty("fade-in-next-style");
		//#endif
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#showNotify()
	 */
	public void showNotify() {
		super.showNotify();
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#paintContent(de.enough.polish.ui.Item, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(Item parent, int x, int y, int leftBorder, int rightBorder, Graphics g) {
		int[] data = this.rgbData;
		if (this.currentTransparency == 0) {
			// do not paint anything
		} else if (this.currentTransparency != 255 && data != null) {
			DrawUtil.drawRgb( data, x, y, this.contentWidth, this.contentHeight, true, g );
		} else {
			super.paintContentByParent(parent, x, y, leftBorder, rightBorder, g);
		}
	}

}
