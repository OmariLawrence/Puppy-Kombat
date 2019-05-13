import java.util.Random;
import javax.swing.JFrame;
import java.applet.Applet;
import java.applet.AudioClip;
/**
 * Write a description of class Prompt here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Prompt extends Sprite
{
    AudioClip hitWallSound = null;
    Random random;
    private int currprompt;
    private int maxlength = 3000;
    private long startTime = -1000;
    public Prompt(JFrame f,int x, int y, int dx, int dy,int xSize, int ySize,String filename) {
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
        currprompt = random.nextInt(4);
        if(currprompt == 0){
            setImage("images/up.png");
            startTime = System.currentTimeMillis();
        }else if(currprompt == 1){
            setImage("images/down.png");
            startTime = System.currentTimeMillis();
        }else if(currprompt == 2){
            setImage("images/left.png");
            startTime = System.currentTimeMillis();
        }else if(currprompt == 3){
            setImage("images/right.png");
            startTime = System.currentTimeMillis();
        }
    }
    
    public int getCurrprompt(){
        return currprompt;
    }
    
    public long getStartTime(){
        return startTime;
    }
    
    public void loadClips() {

        try {
            hitWallSound = Applet.newAudioClip (
                        getClass().getResource("sounds/hitWall.au"));
        }
        catch (Exception e) {
            System.out.println ("Error loading sound file: " + e);
        }

    }
    
    public void playClip(int index) {

        if (index == 1 && hitWallSound != null)
            hitWallSound.play();
    }
}
