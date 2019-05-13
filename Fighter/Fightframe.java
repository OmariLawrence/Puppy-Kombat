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
public class Fightframe extends JFrame implements Runnable, KeyListener
{
    //buffer number
    private static final int NUM_BUFFERS = 2;

    //screen dimensions
    private int pWidth, pHeight;

    //gamethread variables
    private Thread gameThread = null;
    private volatile boolean running = false;

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
    private GraphicsDevice device;
    private Graphics gScr;
    private BufferStrategy bufferStrategy;

    //fight frame conditions
    private boolean hit = false;
    private boolean miss = false;
    private boolean hitdrawn = true;
    
    //win condition
    private boolean gameover = false;
    private boolean changed = true;

    public Fightframe(Fighter player_idle,Fighter enemy_idle,Fighter player_kick,Fighter enemy_kick,int lvl)
    {
        super("Bunny Fighter");
        level = lvl;

        initFullScreen();

        //Create sprites
        prompt = new Prompt(this, pWidth/2, ((pHeight/100)*10), 0, 0,  100, 100, "images/arrowKeys.png");
        player_Health = new Health(5,10, ((pWidth/100)*25), ((pHeight/100)*5));
        enemy_Health = new Health(((pWidth/100)*75),10, ((pWidth/100)*25), ((pHeight/100)*5));

        prompt.update();

        addKeyListener(this);

        loadImages();
        loadClips();
        startGame();
    }

    private void initFullScreen(){
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        device = ge.getDefaultScreenDevice();

        setUndecorated(true);   // no menu bar, borders, etc.
        setIgnoreRepaint(true); // turn off all paint events since doing active rendering
        setResizable(false);    // screen cannot be resized
        
        if (!device.isFullScreenSupported()) {
            System.out.println("Full-screen exclusive mode not supported");
            System.exit(0);
        }

        device.setFullScreenWindow(this); // switch on full-screen exclusive mode

        // we can now adjust the display modes, if we wish

        showCurrentMode();

        pWidth = getBounds().width;
        pHeight = getBounds().height;

        try {
            createBufferStrategy(NUM_BUFFERS);
        }
        catch (Exception e) {
            System.out.println("Error while creating buffer strategy " + e); 
            System.exit(0);
        }

        bufferStrategy = getBufferStrategy();
    }

    // this method creates and starts the game thread

    private void startGame() { 
        if (gameThread == null || !running) {
            gameThread = new Thread(this);
            gameThread.start();
            playSound.loop();
        }
    }

    public void keyPressed (KeyEvent e) {

        int keyCode = e.getKeyCode();
         
        if ((keyCode == KeyEvent.VK_ESCAPE) || (keyCode == KeyEvent.VK_Q) ||
                   (keyCode == KeyEvent.VK_END)) {
                running = false;        // user can quit anytime by pressing
            return;             //  one of these keys (ESC, Q, END)
            }   

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
        }
    }

    public void keyReleased (KeyEvent e) {

    }

    public void keyTyped (KeyEvent e) {

    }

    public void run() {

        running = true;
        try {
            while (running) {
                gameUpdate();     
                screenUpdate();
                Thread.sleep(200);
                if((hit && hitdrawn) || (miss && hitdrawn)){
                    Thread.sleep(1000);
                    prompt.update();
                    hit = false;
                    miss = false;
                    changed = true;
                }
            }
        }
        catch(InterruptedException e) {};

        finishOff();
    }

    private void gameUpdate() { 

    }

    private void screenUpdate() { 

        try {
            gScr = bufferStrategy.getDrawGraphics();
            gameRender(gScr);
            gScr.dispose();
            if (!bufferStrategy.contentsLost())
                bufferStrategy.show();
            else
                System.out.println("Contents of buffer lost.");
      
            // Sync the display on some systems.
            // (on Linux, this fixes event queue problems)

            Toolkit.getDefaultToolkit().sync();
        }
        catch (Exception e) { 
            e.printStackTrace();  
            running = false; 
        } 
    }

    private void gameRender(Graphics gScr){
 
        gScr.drawImage (bgImage, 0, 0, pWidth, pHeight, null);

        gScr.setColor(Color.black);

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
        }else{
            finishOff();
        }
    }

    private void finishOff() { 
        if (!finishedoff) {
            finishedoff = true;
            restoreScreen();
            gameThread.interrupt();
        }
    }

    private void restoreScreen() { 
        Window w = device.getFullScreenWindow();
        
        if (w != null)
            w.dispose();
        
        device.setFullScreenWindow(null);
    }

    private void showCurrentMode() {
        DisplayMode dm = device.getDisplayMode();
        System.out.println("Current Display Mode: (" + 
                           dm.getWidth() + "," + dm.getHeight() + "," +
                           dm.getBitDepth() + "," + dm.getRefreshRate() + ")  " );
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
