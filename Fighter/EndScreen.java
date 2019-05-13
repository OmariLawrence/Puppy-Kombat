import java.util.Random;
import java.applet.Applet;
import java.applet.AudioClip;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
/**
 * Write a description of class EndScreen here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class EndScreen
{
    private int x,y;
    private JFrame window;
    /**
     * Constructor for objects of class EndScreen
     */
    public EndScreen(JFrame f,int x, int y)
    {
        this.x = x;
        this.y = y;
        this.window = f;
    }
    
    public void draw(Graphics g, String msg){
        Font font = new Font("SansSerif", Font.BOLD, 24);
        FontMetrics metrics = window.getFontMetrics(font);
        
        g.setColor(Color.RED);
        g.setFont(font);
        g.drawString(msg, x, y);
    }
}
