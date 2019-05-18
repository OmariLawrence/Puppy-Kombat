import java.util.Random;
import java.applet.Applet;
import java.applet.AudioClip;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
/**
 * Write a description of class Message here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Message
{
    // instance variables - replace the example below with your own
    private String message;
    private int x, y;
    private JFrame window;

    /**
     * Constructor for objects of class Score
     */
    public Message(JFrame f,int x, int y, String m)
    {
        this.x = x;
        this.y = y;
        this.message = m;
        this.window = f;
    }
    
    public void update()
    {
        
    }
    
    public void draw(Graphics g){
        Font font = new Font("SansSerif", Font.BOLD, 24);
        FontMetrics metrics = window.getFontMetrics(font);

        String msg = message;

        g.setColor(Color.WHITE);
        g.setFont(font);
        g.drawString(msg, x, y);
    }
}
