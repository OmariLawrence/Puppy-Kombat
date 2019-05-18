import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.applet.Applet;
import java.applet.AudioClip;
/**
 * Write a description of class Fightframe here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Fightframe
{
    //screen dimensions
    protected int pWidth, pHeight;

    //asset variables
    public Prompt prompt;
    protected Fighter player_idle, enemy_idle, player_kick, enemy_kick;
    protected Health player_Health, enemy_Health;
    protected Image bgImage;
    AudioClip playSound = null;
    AudioClip hitSound = null;
    public int level;
    protected int waittime = 0;
    protected long promptstart;

    //used at game termination
    protected boolean finishedoff = false;

    // used for full-screen exclusive mode  
    protected Graphics gScr;

    //fight frame conditions
    protected boolean hit = false;
    protected boolean miss = false;
    protected boolean hitdrawn = true;
    protected boolean changed = true;
    protected long hitTime;
    protected long changeTime;
    
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
        promptstart = System.currentTimeMillis();
        //POSSIBLE ERROR
        //addKeyListener(f);

        loadImages();
        loadClips();
        levelSetter();
    }
    
    public void update (int keyCode) {
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

    public void levelSetter(){
        switch(level){
            case 1:
                waittime = 750;
                break;
            case 2:
                waittime = 500;
                break;
            case 3:
                waittime = 350;
                break;
            case 4:
                waittime = 250;
                break;
            case 5:
                waittime = 150;
                break;
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
        hitTime =  System.currentTimeMillis();
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
        hitTime =  System.currentTimeMillis();
        if(player_Health.isDead()){
            gameover = true;
            win = false;
        }
    }

    public void draw(Graphics gScr){
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
            if(!miss || (System.currentTimeMillis() - prompt.getStartTime()) < waittime){
                enemy_idle.draw((Graphics2D)gScr);
            }else{
                enemy_kick.draw((Graphics2D)gScr);
                hitdrawn = true;
            }
            if((System.currentTimeMillis() - prompt.getStartTime()) >= waittime && (!hit && !miss)){
                miss();
                frameChanger();
                promptstart = System.currentTimeMillis();
            }
            if(hit || miss){
                frameChanger();
            }
        }
    }

    public void frameChanger(){
        long check = System.currentTimeMillis() - promptstart;
        if(check < waittime){
            return;
        }
        if((System.currentTimeMillis()-hitTime)>1000){
            hit = false;
            miss = false;
            changed = true;
            prompt.update();
            promptstart = System.currentTimeMillis();
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
