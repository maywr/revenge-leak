/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.BufferBuilder
 *  net.minecraft.client.renderer.GLAllocation
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.texture.TextureUtil
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.client.renderer.vertex.VertexFormat
 *  net.minecraft.client.resources.IResource
 *  net.minecraft.client.resources.IResourceManager
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.util.glu.GLU
 */
package com.lukflug.panelstudio.mc12;

import com.lukflug.panelstudio.Interface;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Stack;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public abstract class GLInterface
implements Interface {
    private static final FloatBuffer MODELVIEW = GLAllocation.func_74529_h((int)16);
    private static final FloatBuffer PROJECTION = GLAllocation.func_74529_h((int)16);
    private static final IntBuffer VIEWPORT = GLAllocation.func_74527_f((int)16);
    private static final FloatBuffer COORDS = GLAllocation.func_74529_h((int)3);
    private Stack<Rectangle> clipRect = new Stack();
    protected boolean clipX;

    public GLInterface(boolean clipX) {
        this.clipX = clipX;
    }

    @Override
    public void fillTriangle(Point pos1, Point pos2, Point pos3, Color c1, Color c2, Color c3) {
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder bufferbuilder = tessellator.func_178180_c();
        bufferbuilder.func_181668_a(4, DefaultVertexFormats.field_181706_f);
        bufferbuilder.func_181662_b((double)pos1.x, (double)pos1.y, (double)this.getZLevel()).func_181666_a((float)c1.getRed() / 255.0f, (float)c1.getGreen() / 255.0f, (float)c1.getBlue() / 255.0f, (float)c1.getAlpha() / 255.0f).func_181675_d();
        bufferbuilder.func_181662_b((double)pos2.x, (double)pos2.y, (double)this.getZLevel()).func_181666_a((float)c2.getRed() / 255.0f, (float)c2.getGreen() / 255.0f, (float)c2.getBlue() / 255.0f, (float)c2.getAlpha() / 255.0f).func_181675_d();
        bufferbuilder.func_181662_b((double)pos3.x, (double)pos3.y, (double)this.getZLevel()).func_181666_a((float)c3.getRed() / 255.0f, (float)c3.getGreen() / 255.0f, (float)c3.getBlue() / 255.0f, (float)c3.getAlpha() / 255.0f).func_181675_d();
        tessellator.func_78381_a();
    }

    @Override
    public void drawLine(Point a, Point b, Color c1, Color c2) {
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder bufferbuilder = tessellator.func_178180_c();
        bufferbuilder.func_181668_a(1, DefaultVertexFormats.field_181706_f);
        bufferbuilder.func_181662_b((double)a.x, (double)a.y, (double)this.getZLevel()).func_181666_a((float)c1.getRed() / 255.0f, (float)c1.getGreen() / 255.0f, (float)c1.getBlue() / 255.0f, (float)c1.getAlpha() / 255.0f).func_181675_d();
        bufferbuilder.func_181662_b((double)b.x, (double)b.y, (double)this.getZLevel()).func_181666_a((float)c2.getRed() / 255.0f, (float)c2.getGreen() / 255.0f, (float)c2.getBlue() / 255.0f, (float)c2.getAlpha() / 255.0f).func_181675_d();
        tessellator.func_78381_a();
    }

    @Override
    public void fillRect(Rectangle r, Color c1, Color c2, Color c3, Color c4) {
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder bufferbuilder = tessellator.func_178180_c();
        bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181706_f);
        bufferbuilder.func_181662_b((double)r.x, (double)(r.y + r.height), (double)this.getZLevel()).func_181666_a((float)c4.getRed() / 255.0f, (float)c4.getGreen() / 255.0f, (float)c4.getBlue() / 255.0f, (float)c4.getAlpha() / 255.0f).func_181675_d();
        bufferbuilder.func_181662_b((double)(r.x + r.width), (double)(r.y + r.height), (double)this.getZLevel()).func_181666_a((float)c3.getRed() / 255.0f, (float)c3.getGreen() / 255.0f, (float)c3.getBlue() / 255.0f, (float)c3.getAlpha() / 255.0f).func_181675_d();
        bufferbuilder.func_181662_b((double)(r.x + r.width), (double)r.y, (double)this.getZLevel()).func_181666_a((float)c2.getRed() / 255.0f, (float)c2.getGreen() / 255.0f, (float)c2.getBlue() / 255.0f, (float)c2.getAlpha() / 255.0f).func_181675_d();
        bufferbuilder.func_181662_b((double)r.x, (double)r.y, (double)this.getZLevel()).func_181666_a((float)c1.getRed() / 255.0f, (float)c1.getGreen() / 255.0f, (float)c1.getBlue() / 255.0f, (float)c1.getAlpha() / 255.0f).func_181675_d();
        tessellator.func_78381_a();
    }

    @Override
    public void drawRect(Rectangle r, Color c1, Color c2, Color c3, Color c4) {
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder bufferbuilder = tessellator.func_178180_c();
        bufferbuilder.func_181668_a(2, DefaultVertexFormats.field_181706_f);
        bufferbuilder.func_181662_b((double)r.x, (double)(r.y + r.height), (double)this.getZLevel()).func_181666_a((float)c4.getRed() / 255.0f, (float)c4.getGreen() / 255.0f, (float)c4.getBlue() / 255.0f, (float)c4.getAlpha() / 255.0f).func_181675_d();
        bufferbuilder.func_181662_b((double)(r.x + r.width), (double)(r.y + r.height), (double)this.getZLevel()).func_181666_a((float)c3.getRed() / 255.0f, (float)c3.getGreen() / 255.0f, (float)c3.getBlue() / 255.0f, (float)c3.getAlpha() / 255.0f).func_181675_d();
        bufferbuilder.func_181662_b((double)(r.x + r.width), (double)r.y, (double)this.getZLevel()).func_181666_a((float)c2.getRed() / 255.0f, (float)c2.getGreen() / 255.0f, (float)c2.getBlue() / 255.0f, (float)c2.getAlpha() / 255.0f).func_181675_d();
        bufferbuilder.func_181662_b((double)r.x, (double)r.y, (double)this.getZLevel()).func_181666_a((float)c1.getRed() / 255.0f, (float)c1.getGreen() / 255.0f, (float)c1.getBlue() / 255.0f, (float)c1.getAlpha() / 255.0f).func_181675_d();
        tessellator.func_78381_a();
    }

    @Override
    public synchronized int loadImage(String name) {
        try {
            ResourceLocation rl = new ResourceLocation(this.getResourcePrefix() + name);
            InputStream stream = Minecraft.func_71410_x().func_110442_L().func_110536_a(rl).func_110527_b();
            BufferedImage image = ImageIO.read(stream);
            int texture = TextureUtil.func_110996_a();
            TextureUtil.func_110987_a((int)texture, (BufferedImage)image);
            return texture;
        }
        catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void drawImage(Rectangle r, int rotation, boolean parity, int image) {
        if (image == 0) {
            return;
        }
        int[][] texCoords = new int[][]{{0, 1}, {1, 1}, {1, 0}, {0, 0}};
        for (int i = 0; i < rotation % 4; ++i) {
            int temp1 = texCoords[3][0];
            int temp2 = texCoords[3][1];
            texCoords[3][0] = texCoords[2][0];
            texCoords[3][1] = texCoords[2][1];
            texCoords[2][0] = texCoords[1][0];
            texCoords[2][1] = texCoords[1][1];
            texCoords[1][0] = texCoords[0][0];
            texCoords[1][1] = texCoords[0][1];
            texCoords[0][0] = temp1;
            texCoords[0][1] = temp2;
        }
        if (parity) {
            int temp1 = texCoords[3][0];
            int temp2 = texCoords[3][1];
            texCoords[3][0] = texCoords[0][0];
            texCoords[3][1] = texCoords[0][1];
            texCoords[0][0] = temp1;
            texCoords[0][1] = temp2;
            temp1 = texCoords[2][0];
            temp2 = texCoords[2][1];
            texCoords[2][0] = texCoords[1][0];
            texCoords[2][1] = texCoords[1][1];
            texCoords[1][0] = temp1;
            texCoords[1][1] = temp2;
        }
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder bufferbuilder = tessellator.func_178180_c();
        GlStateManager.func_179144_i((int)image);
        GlStateManager.func_179098_w();
        bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181707_g);
        bufferbuilder.func_181662_b((double)r.x, (double)(r.y + r.height), (double)this.getZLevel()).func_187315_a((double)texCoords[0][0], (double)texCoords[0][1]).func_181675_d();
        bufferbuilder.func_181662_b((double)(r.x + r.width), (double)(r.y + r.height), (double)this.getZLevel()).func_187315_a((double)texCoords[1][0], (double)texCoords[1][1]).func_181675_d();
        bufferbuilder.func_181662_b((double)(r.x + r.width), (double)r.y, (double)this.getZLevel()).func_187315_a((double)texCoords[2][0], (double)texCoords[2][1]).func_181675_d();
        bufferbuilder.func_181662_b((double)r.x, (double)r.y, (double)this.getZLevel()).func_187315_a((double)texCoords[3][0], (double)texCoords[3][1]).func_181675_d();
        tessellator.func_78381_a();
        GlStateManager.func_179090_x();
    }

    protected void scissor(Rectangle r) {
        if (r == null) {
            GL11.glScissor((int)0, (int)0, (int)0, (int)0);
            GL11.glEnable((int)3089);
            return;
        }
        GLU.gluProject((float)r.x, (float)r.y, (float)this.getZLevel(), (FloatBuffer)MODELVIEW, (FloatBuffer)PROJECTION, (IntBuffer)VIEWPORT, (FloatBuffer)COORDS);
        float x1 = COORDS.get(0);
        float y1 = COORDS.get(1);
        GLU.gluProject((float)(r.x + r.width), (float)(r.y + r.height), (float)this.getZLevel(), (FloatBuffer)MODELVIEW, (FloatBuffer)PROJECTION, (IntBuffer)VIEWPORT, (FloatBuffer)COORDS);
        float x2 = COORDS.get(0);
        float y2 = COORDS.get(1);
        if (!this.clipX) {
            x1 = VIEWPORT.get(0);
            x2 = x1 + (float)VIEWPORT.get(2);
        }
        GL11.glScissor((int)Math.round(Math.min(x1, x2)), (int)Math.round(Math.min(y1, y2)), (int)Math.round(Math.abs(x2 - x1)), (int)Math.round(Math.abs(y2 - y1)));
        GL11.glEnable((int)3089);
    }

    @Override
    public void window(Rectangle r) {
        if (this.clipRect.isEmpty()) {
            this.scissor(r);
            this.clipRect.push(r);
            return;
        }
        Rectangle top = this.clipRect.peek();
        if (top == null) {
            this.scissor(null);
            this.clipRect.push(null);
            return;
        }
        int x1 = Math.max(r.x, top.x);
        int y1 = Math.max(r.y, top.y);
        int x2 = Math.min(r.x + r.width, top.x + top.width);
        int y2 = Math.min(r.y + r.height, top.y + top.height);
        if (x2 > x1 && y2 > y1) {
            Rectangle rect = new Rectangle(x1, y1, x2 - x1, y2 - y1);
            this.scissor(rect);
            this.clipRect.push(rect);
            return;
        }
        this.scissor(null);
        this.clipRect.push(null);
    }

    @Override
    public void restore() {
        if (this.clipRect.isEmpty()) return;
        this.clipRect.pop();
        if (this.clipRect.isEmpty()) {
            GL11.glDisable((int)3089);
            return;
        }
        this.scissor(this.clipRect.peek());
    }

    public void getMatrices() {
        GlStateManager.func_179111_a((int)2982, (FloatBuffer)MODELVIEW);
        GlStateManager.func_179111_a((int)2983, (FloatBuffer)PROJECTION);
        GlStateManager.func_187445_a((int)2978, (IntBuffer)VIEWPORT);
    }

    public static void begin() {
        GlStateManager.func_179147_l();
        GlStateManager.func_179090_x();
        GlStateManager.func_187428_a((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GlStateManager.func_179103_j((int)7425);
        GlStateManager.func_187441_d((float)2.0f);
    }

    public static void end() {
        GlStateManager.func_179103_j((int)7424);
        GlStateManager.func_179098_w();
        GlStateManager.func_179084_k();
    }

    protected abstract float getZLevel();

    protected abstract String getResourcePrefix();
}

