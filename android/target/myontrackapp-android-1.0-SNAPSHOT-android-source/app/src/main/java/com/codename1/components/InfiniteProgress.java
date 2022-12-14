/*
 * Copyright (c) 2012, Codename One and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Codename One designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Codename One through http://www.codenameone.com/ if you
 * need additional information or have any questions.
 */
package com.codename1.components;

import com.codename1.ui.Component;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.Graphics;
import com.codename1.ui.Image;
import com.codename1.ui.Stroke;
import com.codename1.ui.animations.CommonTransitions;
import com.codename1.ui.animations.Motion;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.geom.GeneralPath;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.WeakHashMap;

/**
 * <p>Shows a "Washing Machine" infinite progress indication animation, to customize the image you can either
 * use the infiniteImage theme constant or the <code>setAnimation</code> method. The image is rotated
 * automatically so don't use an animated image or anything like that as it would fail with the rotation logic.</p>
 *
 * <p>This class can be used in one of two ways either by embedding the component into the UI thru something
 * like this:
 * </p>
 * <script src="https://gist.github.com/codenameone/bddead645fcd8ee33e9c.js"></script>
 *
 * <p>
 * Notice that this can be used within a custom dialog too.<br>
 * A second approach allows showing the infinite progress over the entire screen which blocks all input. This tints
 * the background while the infinite progress rotates:
 * </p>
 *<script src="https://gist.github.com/codenameone/a0a6abca781cd86e4f5e.js"></script>
 * <img src="https://www.codenameone.com/img/developer-guide/infinite-progress.png" alt="InfiniteProgress">
 *
 * @author Shai Almog
 */
public class InfiniteProgress extends Component {
    /**
     * Indicates whether infinite progress and pull to refresh work in the material
     * design mode by default
     */
    private static boolean defaultMaterialDesignMode;

    /**
     * The default color of the current material design progress spinner
     */
    private static int defaultMaterialDesignColor = 0x6200ee;

    /**
     * Indicates whether infinite progress and pull to refresh work in the material
     * design mode by default
     * @return the defaultMaterialDesignMode
     */
    public static boolean isDefaultMaterialDesignMode() {
        return defaultMaterialDesignMode;
    }

    /**
     * Indicates whether infinite progress and pull to refresh work in the material
     * design mode by default
     * @param aDefaultMaterialDesignMode the defaultMaterialDesignMode to set
     */
    public static void setDefaultMaterialDesignMode(
        boolean aDefaultMaterialDesignMode) {
        defaultMaterialDesignMode = aDefaultMaterialDesignMode;
    }

    /**
     * The default color of the current material design progress spinner
     * @return the defaultMaterialDesignColor
     */
    public static int getDefaultMaterialDesignColor() {
        return defaultMaterialDesignColor;
    }

    /**
     * The default color of the current material design progress spinner
     * @param aDefaultMaterialDesignColor the defaultMaterialDesignColor to set
     */
    public static void setDefaultMaterialDesignColor(
        int aDefaultMaterialDesignColor) {
        defaultMaterialDesignColor = aDefaultMaterialDesignColor;
    }
    private Image animation;
    private int angle = 0;
    private int tick;
    private WeakHashMap<Integer, Image> cache = new WeakHashMap<Integer, Image>();
    private int tintColor = 0x90000000;

    /**
     * Indicates whether this instance of infinite progress works in the material
     * design mode by default
     */
    private boolean materialDesignMode = defaultMaterialDesignMode;

    /**
     * The color of the current material design progress spinner
     */
    private int materialDesignColor = defaultMaterialDesignColor;
    private Motion materialLengthAngle;
    private boolean materialLengthDirection;

    /**
     * The animation rotates with EDT ticks, but not for every tick. To slow down the animation increase this
     * number and to speed it up reduce it to 1. It can't be 0 or lower.
     */
    private int tickCount = 3;

    /**
     * The angle to increase (in degrees naturally) in every tick count, reduce to 1 to make the animation perfectly
     * slow and smooth, increase to 45 to make it fast and jumpy. Its probably best to use a number that divides well
     * with 360 but that isn't a requirement. Valid numbers are anything between 1 and 359.
     */
    private int angleIncrease = 16;

    /**
     * Default constructor to define the UIID
     */
    public InfiniteProgress() {
        setUIID("InfiniteProgress");
    }

    /**
     * Shows the infinite progress over the whole screen, the blocking can be competed by calling <code>dispose()</code>
     * on the returned <code>Dialog</code>.
     *<script src="https://gist.github.com/codenameone/a0a6abca781cd86e4f5e.js"></script>
     * @return the dialog created for the blocking effect, disposing it will return to the previous form and remove the input block.
     * @deprecated typo in method name please use {@link #showInfiniteBlocking()} instead
     */
    public Dialog showInifiniteBlocking() {
        return showInfiniteBlocking();
    }

    /**
     * Shows the infinite progress over the whole screen, the blocking can be competed by calling <code>dispose()</code>
     * on the returned <code>Dialog</code>.
     *<script src="https://gist.github.com/codenameone/a0a6abca781cd86e4f5e.js"></script>
     * @return the dialog created for the blocking effect, disposing it will return to the previous form and remove the input block.
     */
    public Dialog showInfiniteBlocking() {
        Form f = Display.getInstance().getCurrent();
        if(f == null) {
            f = new Form();
            f.show();
        }
        if (f.getClientProperty("isInfiniteProgress") == null) {
            f.setTintColor(tintColor);
        }
        Dialog d = new Dialog();
        d.putClientProperty("isInfiniteProgress", true);
        d.setTintColor(0x0);
        d.setDialogUIID("Container");
        d.setLayout(new BorderLayout());
        d.addComponent(BorderLayout.CENTER, this);
        d.setTransitionInAnimator(CommonTransitions.createEmpty());
        d.setTransitionOutAnimator(CommonTransitions.createEmpty());
        d.showPacked(BorderLayout.CENTER, false);
        return d;
    }

    /**
     * {@inheritDoc}
     */
    protected void initComponent() {
        super.initComponent();
        if(animation == null) {
            animation = UIManager.getInstance().getThemeImageConstant("infiniteImage");
        }
        Form f = getComponentForm();
        if(f != null) {
            f.registerAnimated(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void deinitialize() {
        Form f = getComponentForm();
        if(f == null) {
            f = Display.getInstance().getCurrent();
        }
        f.deregisterAnimated(this);
        super.deinitialize();
    }

    /**
     * Updates the progress animation.  This only updates if the InfiniteProgress is on the
     * currently displayed form and is visible.  If you need to update the progress animation
     * in another context, use {@link #animate(boolean) }.
     * @return true if it animated and should be repainted.
     */
    public boolean animate() {
        return animate(false);
    }

    /**
     * Updates the progress animation.
     * @param force If false, then the animation is only updated if the progress is visible and on
     * the current form.  True will force the update.
     * @return True if it animated and should be repainted.
     * @since 7.0
     */
    public boolean animate(boolean force) {
        if (!force) {
            if (!isVisible() || Display.getInstance().getCurrent() != this.getComponentForm()) {
                return false;
            }
        }
        // reduce repaint thrushing of the UI from the infinite progress
        boolean val = super.animate() || tick % tickCount == 0;
        tick++;
        if(val) {
            angle += angleIncrease;
        }
        return val;
    }

    private int getMaterialDesignSize() {
        float dipCount = Float.parseFloat(getUIManager().
                getThemeConstant("infiniteMaterialDesignSize", "6.667f"));

        return Display.getInstance().convertToPixels(dipCount);
    }

    private int getMaterialImageSize() {
        float dipCount = Float.parseFloat(getUIManager().
                getThemeConstant("infiniteMaterialImageSize", "7"));

        return Display.getInstance().convertToPixels(dipCount);
    }

    /**
     * {@inheritDoc}
     */
    protected Dimension calcPreferredSize() {
        if(materialDesignMode) {
            int size = getMaterialDesignSize();
            return new Dimension(getStyle().getHorizontalPadding() + size,
                getStyle().getVerticalPadding() + size);
        }
        if(animation == null) {
            animation = UIManager.getInstance().getThemeImageConstant("infiniteImage");
            if(animation == null) {
                int size = getMaterialImageSize();
                String f = getUIManager().getThemeConstant("infiniteDefaultColor", null);
                int color = 0x777777;
                if(f != null) {
                    color = Integer.parseInt(f, 16);
                }
                FontImage fi = FontImage.createFixed("" + FontImage.MATERIAL_AUTORENEW,
                        FontImage.getMaterialDesignFont(),
                        color, size, size, 0);

                animation = fi.toImage();
            }
        }
        if(animation == null) {
            return new Dimension(100, 100);
        }
        Style s = getStyle();
        return new Dimension(s.getHorizontalPadding() + animation.getWidth(),
                s.getVerticalPadding() + animation.getHeight());
    }

    /**
     * {@inheritDoc}
     */
    public void paint(Graphics g) {
        if (this.getComponentForm() != null && Display.getInstance().getCurrent() != this.getComponentForm()) {
            return;
        }
        super.paint(g);
        if(materialDesignMode) {
            int size = getMaterialDesignSize();
            int strokeWidth = Display.getInstance().convertToPixels(0.635f);
            int oldColor = g.getColor();
            g.setColor(materialDesignColor);
            int oldAlpha = g.setAndGetAlpha(255);

            Style s = getStyle();
            GeneralPath gp = new GeneralPath();
            if(materialLengthAngle == null || materialLengthAngle.isFinished()) {
                materialLengthAngle = Motion.createEaseInOutMotion(
                        10, 300, 1000);
                materialLengthAngle.start();
                materialLengthDirection = !materialLengthDirection;
            }
            int angleLength = materialLengthAngle.getValue();
            double dr;
            if(!materialLengthDirection) {
                angleLength = 300 - angleLength;
                dr = Math.toRadians((angle - angleLength) % 360);
            } else {
                dr = Math.toRadians(angle % 360);
            }
            double x = getX() + s.getPaddingLeft(isRTL());
            double y = getY() + s.getPaddingTop();
            //System.out.println("Arc x: " + x + " y: " + y + " width/height: " + size + " angle: " + angle + " endAngle: " + (angle % 180 + 45));
            gp.arc(x, y, size, size, dr, Math.toRadians(angleLength));
            Stroke st = new Stroke(strokeWidth, Stroke.CAP_ROUND, Stroke.JOIN_MITER, 1);
            g.setAntiAliased(true);
            g.drawShape(gp, st);
            g.setColor(oldColor);
            g.setAlpha(oldAlpha);
            return;
        }
        if(animation == null) {
            return;
        }
        int v = angle % 360;
        Style s = getStyle();
        /*if(g.isAffineSupported()) {
            g.rotate(((float)v) / 57.2957795f, getAbsoluteX() + s.getPadding(LEFT) + getWidth() / 2, getAbsoluteY() + s.getPadding(TOP) + getHeight() / 2);
            g.drawImage(getAnimation(), getX() + s.getPadding(LEFT), getY() + s.getPadding(TOP));
            g.resetAffine();
        } else {*/

        Image rotated;
        if(animation instanceof FontImage) {
            rotated = animation.rotate(v);
        } else {
            Integer angle = new Integer(v);
            rotated = cache.get(angle);
            if(rotated == null) {
                rotated = animation.rotate(v);
                cache.put(v, rotated);
            }
        }
        g.drawImage(rotated, getX() + s.getPaddingLeftNoRTL(), getY() + s.getPaddingTop());
        //}
    }

    /**
     * @return the animation
     */
    public Image getAnimation() {
        return animation;
    }

    /**
     * Allows setting the image that will be rotated as part of this effect
     * @param animation the animation to set
     */
    public void setAnimation(Image animation) {
        this.animation = animation;
        cache.clear();
    }

    /**
     * {@inheritDoc}
     */
    public String[] getPropertyNames() {
        return new String[] {"animation"};
    }

    /**
     * {@inheritDoc}
     */
    public Class[] getPropertyTypes() {
       return new Class[] {Image.class};
    }

    /**
     * {@inheritDoc}
     */
    public Object getPropertyValue(String name) {
        if(name.equals("animation")) {
            return animation;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String setPropertyValue(String name, Object value) {
        if(name.equals("animation")) {
            this.animation = (Image)value;
            cache.clear();
            return null;
        }
        return super.setPropertyValue(name, value);
    }

    /**
     * The tinting color of the screen when the showInfiniteBlocking method is invoked
     * @return the tintColor
     */
    public int getTintColor() {
        return tintColor;
    }

    /**
     * The tinting color of the screen when the showInfiniteBlocking method is invoked
     * @param tintColor the tintColor to set
     */
    public void setTintColor(int tintColor) {
        this.tintColor = tintColor;
    }

    /**
     * The animation rotates with EDT ticks, but not for every tick. To slow down the animation increase this
     * number and to speed it up reduce it to 1. It can't be 0 or lower.
     * @return the tickCount
     */
    public int getTickCount() {
        return tickCount;
    }

    /**
     * The animation rotates with EDT ticks, but not for every tick. To slow down the animation increase this
     * number and to speed it up reduce it to 1. It can't be 0 or lower.
     * @param tickCount the tickCount to set
     */
    public void setTickCount(int tickCount) {
        this.tickCount = tickCount;
    }

    /**
     * The angle to increase (in degrees naturally) in every tick count, reduce to 1 to make the animation perfectly
     * slow and smooth, increase to 45 to make it fast and jumpy. Its probably best to use a number that divides well
     * with 360 but that isn't a requirement. Valid numbers are anything between 1 and 359.
     * @return the angleIncrease
     */
    public int getAngleIncrease() {
        return angleIncrease;
    }

    /**
     * The angle to increase (in degrees naturally) in every tick count, reduce to 1 to make the animation perfectly
     * slow and smooth, increase to 45 to make it fast and jumpy. Its probably best to use a number that divides well
     * with 360 but that isn't a requirement. Valid numbers are anything between 1 and 359.
     * @param angleIncrease the angleIncrease to set
     */
    public void setAngleIncrease(int angleIncrease) {
        this.angleIncrease = angleIncrease;
    }

    /**
     * Indicates whether this instance of infinite progress works in the material
     * design mode by default
     * @return the materialDesignMode
     */
    public boolean isMaterialDesignMode() {
        return materialDesignMode;
    }

    /**
     * Indicates whether this instance of infinite progress works in the material
     * design mode by default
     * @param materialDesignMode the materialDesignMode to set
     */
    public void setMaterialDesignMode(boolean materialDesignMode) {
        this.materialDesignMode = materialDesignMode;
    }

    /**
     * The color of the current material design progress spinner
     * @return the materialDesignColor
     */
    public int getMaterialDesignColor() {
        return materialDesignColor;
    }

    /**
     * The color of the current material design progress spinner
     * @param materialDesignColor the materialDesignColor to set
     */
    public void setMaterialDesignColor(int materialDesignColor) {
        this.materialDesignColor = materialDesignColor;
    }
}
