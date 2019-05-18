import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.applet.Applet;
import java.applet.AudioClip;
/**
 * Write a description of class FightFrameMP here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class FightFrameMP extends Fightframe
{


    /**
     * Constructor for objects of class FightFrameMP
     */
    public FightFrameMP(JFrame f,Fighter pIdle,Fighter eIdle,Fighter pKick,Fighter eKick,int pWidth,int pHeight,int lvl)
    {
        super(f, pIdle, eIdle, pKick, eKick, pWidth, pHeight,lvl);
        // initialise instance variables

    }

    public void update (int keyCode) {
        if (keyCode == KeyEvent.VK_UP) {
            if(prompt.getCurrprompt() == 0){
                miss();
            }
        }
        if (keyCode == KeyEvent.VK_DOWN) {
            if(prompt.getCurrprompt() == 1){
                miss();
            }
        }
        if (keyCode == KeyEvent.VK_LEFT) {
            if(prompt.getCurrprompt() == 2){
                miss();
            }
        }
        if (keyCode == KeyEvent.VK_RIGHT) {
            if(prompt.getCurrprompt() == 3){
                miss();
            }
        }
        if (keyCode == KeyEvent.VK_W) {
            if(prompt.getCurrprompt() == 0){
                hit();
            }
        }
        if (keyCode == KeyEvent.VK_S) {
            if(prompt.getCurrprompt() == 1){
                hit();
            }
        }
        if (keyCode == KeyEvent.VK_A) {
            if(prompt.getCurrprompt() == 2){
                hit();
            }
        }
        if (keyCode == KeyEvent.VK_D) {
            if(prompt.getCurrprompt() == 3){
                hit();
            }
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
            if(!miss){
                enemy_idle.draw((Graphics2D)gScr);
            }else{
                enemy_kick.draw((Graphics2D)gScr);
                hitdrawn = true;
            }
            if(hit || miss){
                frameChanger();
            }
        }
    }
}
