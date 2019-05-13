import java.util.Random;
import javax.swing.JFrame;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Color;
/**
 * Write a description of class Health here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Health
{
    private int x, y, r, g, b, xsize, ysize;
    private boolean dead = false;
    public Health(int x, int y,int xs,int ys)
    {
        this.x = x;
        this.y = y;
        this.xsize = xs;
        this.ysize = ys;
        this.r = 255;
        this.g = 255;
        this.b = 255;
    }

    public void redden(){
        g -= 25;
        b -= 25;
        if(g < 0){
            g = 0;
        }
        if(b < 0){
            b = 0;
        }
        if(g <= 0 || b <= 0){
            dead = true;
        }
    }
    
    public boolean isDead(){
        return dead;
    }
    
    public void draw(Graphics g){
        Color c = new Color(this.r,this.g,this.b);
        g.setColor(c);
        g.fillRect(x,y,xsize,ysize);
    }
}
