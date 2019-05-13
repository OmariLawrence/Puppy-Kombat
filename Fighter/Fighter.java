import java.util.Random;
import javax.swing.JFrame;
import java.applet.Applet;
import java.applet.AudioClip;
/**
 * Write a description of class Fighter here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Fighter extends Sprite
{
    AudioClip punchsound = null;
    Random random;
    private int maxlength = 3000;
    private long startTime = -1000;
    public Fighter(JFrame f,int x, int y, int dx, int dy,int xSize, int ySize,String filename) {
        super(f, x, y, dx, dy, xSize, ySize, filename);

        random = new Random();
        //setPosition();
        loadClips();
    }
    
    public void setPosition() {
        int x = random.nextInt (dimension.width - xSize);
        setX(x);
    }
    
    public void update() {
        
    }
    
    public void loadClips() {

        try {
            punchsound = Applet.newAudioClip (
                        getClass().getResource("sounds/PUNCH.wav"));
        }
        catch (Exception e) {
            System.out.println ("Error loading sound file: " + e);
        }

    }
    
    public void playClip(int index) {

        if (index == 1 && punchsound != null)
            punchsound.play();
    }
}
