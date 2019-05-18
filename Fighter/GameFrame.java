import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.applet.Applet;
import java.applet.AudioClip;

public class GameFrame extends JFrame implements Runnable, KeyListener {
    private static final int NUM_BUFFERS = 2;   // used for page flipping

    private int pWidth, pHeight;            // dimensions of screen
    private int TXoffset;

    private Thread gameThread = null;               // the thread that controls the game
    private volatile boolean running = false;       // used to stop the animation thread
    
    private Image bgImage;              // background image
    private Fighter p,e1,e2,e3,e4,e5;
    private Fighter p_clone,e1_clone,e2_clone,e3_clone,e4_clone,e5_clone;
    private Fighter p_kick,e1_kick,e2_kick,e3_kick,e4_kick,e5_kick;
    AudioClip playSound = null;         // theme sound
    private Message sp,mp;

    // used at game termination
    private boolean finishedOff = false;

    private volatile boolean isStopped = false;
  
    // used for full-screen exclusive mode  
    private GraphicsDevice device;
    private Graphics gScr;
    private BufferStrategy bufferStrategy;
    
    //game state variables
    private boolean fightstate = false;
    private int level = 0;
    
    //win condition
    private boolean gameover = false;
    private long roundWin = 0;
    
    // Fight frame
    private Fightframe ff = null;

    public GameFrame () {
        super("Bat and Ball Game: Full Screen Exclusive Mode");

        initFullScreen();
        TXoffset = ((pWidth/100)*10);
        
        sp = new Message(this, 0, pHeight-50, "Press Enter to challenge the tower");
        mp = new Message(this, (pWidth/2)+TXoffset, pHeight-50, "Press M to challenge a friend");

        // create game sprites

        addKeyListener(this);           // respond to key events

        loadImages();
        loadClips();
        loadFighters();
        startGame();
    }

    private void initFullScreen() {
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

    // implementation of KeyListener interface

    public void keyPressed (KeyEvent e) {

        int keyCode = e.getKeyCode();
         
        if ((keyCode == KeyEvent.VK_ESCAPE) || (keyCode == KeyEvent.VK_Q) || (keyCode == KeyEvent.VK_END)) {
            running = false;        // user can quit anytime by pressing
            return;             //  one of these keys (ESC, Q, END)
        }
        
        if(keyCode == KeyEvent.VK_ENTER && ff == null){
            adjustFF(1);
        }
        
        if(keyCode == KeyEvent.VK_M && ff == null){
            adjustFF(2);
        }
        
        if(ff != null){
            if (ff.gameover)
                return;
            ff.update(keyCode);
        }

        if(gameover || isStopped)       
            // don't do anything if either condition is true
            return;
    }

    public void keyReleased (KeyEvent e) {

    }

    public void keyTyped (KeyEvent e) {

    }


    // implmentation of MousePressedListener interface

    // implmentation of MouseMotionListener interface

    // The run() method implements the game loop.

    public void run() {

        running = true;
        try {
            while (running) {
                gameUpdate();     
                screenUpdate();
                Thread.sleep(200);
            }
        }
        catch(InterruptedException e) {};

        finishOff();
    }


    // This method updates the game objects (animation and ball)

    private void gameUpdate() { 
        if (ff != null && ff.gameover){
            if(!(ff instanceof FightFrameMP)){
                if(!ff.win){
                    isStopped = true;
                }
                p_clone.setY(p_clone.getY()-(pHeight/5));
            }
            ff = null;
            adjustFF(0);
        }
    }


    // This method updates the screen using double buffering / page flipping

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

    /* This method renders all the game entities to the screen: the
       background image, the buttons, ball, bat, and the animation.
    */

    private void gameRender(Graphics gScr){
        
        gScr.drawImage (bgImage, 0, 0, pWidth, pHeight, null);// draw the background image

        gScr.setColor(Color.black);
        
        if(!gameover){
            if(!fightstate){
                drawTower();
                sp.draw((Graphics2D) gScr);
                mp.draw((Graphics2D) gScr);
            }else if(ff != null){
                ff.draw(gScr);
            }
        }else{
            EndScreen fin = new EndScreen(this, pWidth/2, pHeight/2);
        }
                    
        if (isStopped)              // display game over message
            gameOverMessage(gScr);
    }

    // displays a message to the screen when the user stops the game

    private void gameOverMessage(Graphics g) {
        
        Font font = new Font("SansSerif", Font.BOLD, 24);
        FontMetrics metrics = this.getFontMetrics(font);

        String msg = "Game Over. Thanks for playing!";

        int x = (pWidth - metrics.stringWidth(msg)) / 2; 
        int y = (pHeight - metrics.getHeight()) / 2;

        g.setColor(Color.BLUE);
        g.setFont(font);
        g.drawString(msg, x, y);

    }

    /* This method performs some tasks before closing the game.
       The call to System.exit() should not be necessary; however,
       it prevents hanging when the game terminates.
    */

    private void finishOff() { 
        if (!finishedOff) {
            finishedOff = true;
            restoreScreen();
            System.exit(0);
        }
    }

    /* This method switches off full screen mode. The display
       mode is also reset if it has been changed.
    */

    private void restoreScreen() { 
        Window w = device.getFullScreenWindow();
        
        if (w != null)
            w.dispose();
        
        device.setFullScreenWindow(null);
    }

    // This method provides details about the current display mode.

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
        }
        catch (Exception e) {
            System.out.println ("Error loading sound file: " + e);
        }

    }
    
    public void loadFighters(){
        int x = pHeight/5;
        p = new Fighter(this,TXoffset,pHeight/2,0,0,TXoffset,pHeight/2,"images/player.png");
        p_kick = new Fighter(this,((pWidth/100)*80),pHeight/2,0,0,TXoffset,pHeight/2,"images/player_kick.png");
        p_clone = new Fighter(this,((pWidth/2)-TXoffset),x*4,0,0,TXoffset,x,"images/player.png");
        e1 = new Fighter(this,((pWidth/100)*90),pHeight/2,0,0,TXoffset,pHeight/2,"images/enemy1.png");
        e1_kick = new Fighter(this,((pWidth/100)*20),pHeight/2,0,0,TXoffset,pHeight/2,"images/enemy1_kick.png");
        e1_clone = new Fighter(this,(pWidth/2),x*4,0,0,TXoffset,x,"images/enemy1.png");
        e2 = new Fighter(this,((pWidth/100)*90),pHeight/2,0,0,TXoffset,pHeight/2,"images/enemy2.png");
        e2_kick = new Fighter(this,((pWidth/100)*20),pHeight/2,0,0,TXoffset,pHeight/2,"images/enemy2_kick.png");
        e2_clone = new Fighter(this,(pWidth/2),x*3,0,0,TXoffset,x,"images/enemy2.png");
        e3 = new Fighter(this,((pWidth/100)*90),pHeight/2,0,0,TXoffset,pHeight/2,"images/enemy3.png");
        e3_kick = new Fighter(this,((pWidth/100)*20),pHeight/2,0,0,TXoffset,pHeight/2,"images/enemy3_kick.png");
        e3_clone = new Fighter(this,(pWidth/2),x*2,0,0,TXoffset,x,"images/enemy3.png");
        e4 = new Fighter(this,((pWidth/100)*90),pHeight/2,0,0,TXoffset,pHeight/2,"images/enemy4.png");
        e4_kick = new Fighter(this,((pWidth/100)*20),pHeight/2,0,0,TXoffset,pHeight/2,"images/enemy4_kick.png");
        e4_clone = new Fighter(this,(pWidth/2),x*1,0,0,TXoffset,x,"images/enemy4.png");
        e5 = new Fighter(this,((pWidth/100)*90),pHeight/2,0,0,TXoffset,pHeight/2,"images/enemy5.png");
        e5_kick = new Fighter(this,((pWidth/100)*20),pHeight/2,0,0,TXoffset,pHeight/2,"images/enemy5_kick.png");
        e5_clone = new Fighter(this,(pWidth/2),x*0,0,0,TXoffset,x,"images/enemy5.png");
    }

    public void drawTower(){
        p_clone.draw((Graphics2D) gScr);
        e1_clone.draw((Graphics2D) gScr);
        e2_clone.draw((Graphics2D) gScr);
        e3_clone.draw((Graphics2D) gScr);
        e4_clone.draw((Graphics2D) gScr);
        e5_clone.draw((Graphics2D) gScr);
    }

    public void adjustFF(int type){
        fightstate = !fightstate;
        if(ff == null){
            if(type == 1){
                level += 1;
                switch(level){
                    case 1:
                        ff = new Fightframe(this,p,e1,p_kick,e1_kick,pWidth,pHeight,level);
                        break;
                    case 2:
                        ff = new Fightframe(this,p,e2,p_kick,e2_kick,pWidth,pHeight,level);
                        break;
                    case 3:
                        ff = new Fightframe(this,p,e3,p_kick,e3_kick,pWidth,pHeight,level);
                        break;
                    case 4:
                        ff = new Fightframe(this,p,e4,p_kick,e4_kick,pWidth,pHeight,level);
                        break;
                    case 5:
                        ff = new Fightframe(this,p,e5,p_kick,e5_kick,pWidth,pHeight,level);
                        break;
                }
            }else if(type == 2){
                ff = new FightFrameMP(this,p,e5,p_kick,e5_kick,pWidth,pHeight,level);
            }
        }else{
            ff = null;
        }
    }

    public void playClip (int index) {

        if (index == 1 && playSound != null)
            playSound.play();
    }

}

