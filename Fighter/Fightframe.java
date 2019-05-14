import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.applet.Applet;
import java.applet.AudioClip;
/**
 * Write a description of class Fightframe here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Fightframe implements KeyListener
{
    //screen dimensions
    private int pWidth, pHeight;

    //asset variables
    private Prompt prompt;
    private Fighter player_idle, enemy_idle, player_kick, enemy_kick;
    private Health player_Health, enemy_Health;
    private Image bgImage;
    AudioClip playSound = null;
    AudioClip hitSound = null;
    public int level;
    private int waittime = 0;

    //used at game termination
    private boolean finishedoff = false;

    // used for full-screen exclusive mode  
    private Graphics gScr;

    //fight frame conditions
    private boolean hit = false;
    private boolean miss = false;
    private boolean hitdrawn = true;
    private boolean changed = true;
    
    //win condition
    public boolean gameover = false;
    public boolean win;

    public Fightframe(JFrame f,Fighter pIdle,Fighter eIdle,Fighter pKick,Fighter eKick,int pWidth,int pHeight,int lvl)
    {
        level = lvl;

        this.pWidth = pWidth;
        this.pHeight = pHeight;

        //Create sprites
        prompt = new Prompt(f, pWidth/2, ((pHeight/100)*10), 0, 0,  100, 100, "images/arrowKeys.png");
        player_idle = pIdle;
        enemy_idle = eIdle;
        player_kick = pKick;
        enemy_kick = eKick;
        player_Health = new Health(5,10, ((pWidth/100)*25), ((pHeight/100)*5));
        enemy_Health = new Health(((pWidth/100)*75),10, ((pWidth/100)*25), ((pHeight/100)*5));

        prompt.update();
        
        //POSSIBLE ERROR
        //addKeyListener(f);

        loadImages();
        loadClips();
    }

    public void keyPressed (KeyEvent e) {

        int keyCode = e.getKeyCode();

        if (gameover)       
            // don't do anything if either condition is true
            return;
        
        if (keyCode == KeyEvent.VK_UP) {
            if(prompt.getCurrprompt() == 0){
                hit();
            }else{
                miss();
            }
        }
        if (keyCode == KeyEvent.VK_DOWN) {
            if(prompt.getCurrprompt() == 1){
                hit();
            }else{
                miss();
            }
        }
        if (keyCode == KeyEvent.VK_LEFT) {
            if(prompt.getCurrprompt() == 2){
                hit();
            }else{
                miss();
            }
        }
        if (keyCode == KeyEvent.VK_RIGHT) {
            if(prompt.getCurrprompt() == 3){
                hit();
            }else{
                miss();
            }
        }
    }

    public void hit(){
        if(!changed) return;
        System.out.println("Hit");
        enemy_Health.redden();
        hit = true;
        hitdrawn = false;
        playClip(2);
        changed = false;
        if(enemy_Health.isDead()){
            gameover = true;
            win = true;
        }
    }
    
    public void miss(){
        if(!changed) return;
        System.out.println("Miss");
        player_Health.redden();
        miss = true;
        hitdrawn = false;
        playClip(2);
        changed = false;
        if(player_Health.isDead()){
            gameover = true;
            win = false;
        }
    }

    public void keyReleased (KeyEvent e) {

    }

    public void keyTyped (KeyEvent e) {

    }

    private void gameUpdate() { 

    }

    private void gameRender(Graphics gScr){
        if(!gameover){
            prompt.draw((Graphics2D)gScr);
            player_Health.draw((Graphics2D)gScr);
            enemy_Health.draw((Graphics2D)gScr);
            if(!hit){
                player_idle.draw((Graphics2D)gScr);
            }else{
                player_kick.draw((Graphics2D)gScr);
                hitdrawn = true;
            }
            if(!miss || (System.currentTimeMillis() - prompt.getStartTime()) >= waittime){
                enemy_idle.draw((Graphics2D)gScr);
            }else{
                enemy_kick.draw((Graphics2D)gScr);
                hitdrawn = true;
            }
            if((System.currentTimeMillis() - prompt.getStartTime()) >= waittime){
                miss();
                prompt.update();
            }
        }
    }
    
    public void loadImages() {

        bgImage = loadImage("images/arena.png");
    }

    public Image loadImage (String fileName) {
        return new ImageIcon(fileName).getImage();
    }

    public void loadClips() {

        try {
            playSound = Applet.newAudioClip (getClass().getResource("sounds/fightmusic.wav"));
            hitSound = Applet.newAudioClip (getClass().getResource("sounds/PUNCH.wav"));
        }
        catch (Exception e) {
            System.out.println ("Error loading sound file: " + e);
        }

    }

    public void playClip (int index) {

        if (index == 1 && playSound != null)
            playSound.play();
        if(index == 2 && hitSound != null)
            hitSound.play();
    }
}
