/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pomodoromobile;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

/**
 * @author timur
 */
public class Pomodoro extends MIDlet implements CommandListener {
    private Display display;
    private Form form;
    private TextField workTimeField;
    private TextField breaksTimeField;
    
    private PomodoroScreen pomodoroScreen;
    
    private Command startCommand;
    private Command backCommand;
    
    public void startApp() {
        display = Display.getDisplay(this);
        
        form = new Form("Pomodoro Mobile");
        workTimeField = new TextField("Work time (min):", "25", 3, TextField.DECIMAL);
        breaksTimeField = new TextField("Breaks time (min):", "5", 3, TextField.DECIMAL);
        StringItem description = new StringItem("The Pomodoro Technique is a time management method, "
                + "uses a timer to break down work into intervals traditionally 25 minutes "
                + "in length, separated by short breaks.", "");

        description.setLayout(Item.LAYOUT_NEWLINE_BEFORE);
        workTimeField.setLayout(Item.LAYOUT_NEWLINE_BEFORE);
        breaksTimeField.setLayout(Item.LAYOUT_NEWLINE_BEFORE);
        
        startCommand = new Command("Start", Command.OK, 0);
        
        form.append(description);
        form.append(workTimeField);
        form.append(breaksTimeField);
        form.addCommand(startCommand);
        form.setCommandListener(this);
        
        display.setCurrent(form);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    public void commandAction(Command c, Displayable d) {
        if (c == startCommand) {
            pomodoroScreen = new PomodoroScreen(
                    Integer.parseInt(workTimeField.getString()), 
                    Integer.parseInt(breaksTimeField.getString()));

            backCommand = new Command("Back", Command.BACK, 0);

            pomodoroScreen.addCommand(backCommand);
            pomodoroScreen.setCommandListener(this);

            display.setCurrent(pomodoroScreen); 
            
        } else if (c == backCommand) {
            display.setCurrent(form);
        }
    }
}

class PomodoroScreen extends Canvas {
    private int screenWidth;
    private int screenHeight;
    
    private Timer timer;
    private int workMinutes = 25;
    private int recreationMinutes = 5;
    
    private int currentWorkMinute = 25;
    private int currentRecreationMinute = 5;
    
    private final int timerDelay = 5; // sec
    
    private float k;
    
    public PomodoroScreen(int work, int recreation) {
        workMinutes = currentWorkMinute = work * 60;
        recreationMinutes = currentRecreationMinute = recreation * 60;
        screenWidth = getWidth();
        screenHeight = getHeight();

        k = (float) screenWidth / (float) (workMinutes + recreationMinutes);

        timer = new Timer();
        timer.schedule(new UpdateScreen(), 1000);
    }
    
    private class UpdateScreen extends TimerTask {
        public void run() {
            repaintPomodoro();

            if (currentWorkMinute <= 0) {
                if (currentRecreationMinute <= 0) {
                    // timer.cancel();
                    currentWorkMinute = workMinutes;
                    currentRecreationMinute = recreationMinutes;
                } else {
                    currentRecreationMinute--;
                }
            } else {
                currentWorkMinute--;
            }
            
            timer.schedule(new UpdateScreen(), 1000);
        }
    }
    
    private void repaintPomodoro() {
        repaint();
    }
    
    protected void paint(Graphics g) {
        try {
            g.setColor(0xFFFFFF);
            g.fillRect(0, 0, screenWidth, screenHeight);
            g.setColor(0xFF1A00);
            
            g.fillRect((int) (recreationMinutes * k), 0, (int) (currentWorkMinute * k), screenHeight);
            
            g.setColor(0xCDEB8B);
            
            g.fillRect(0, 0, (int) (currentRecreationMinute * k), screenHeight);
            
            g.drawImage(Image.createImage(getClass().getResourceAsStream("/images/tomato.png")), 
                    0, 0, Graphics.TOP | Graphics.LEFT);
            
            g.setColor(0x0);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE));
            
            if (currentWorkMinute > 0) {
                g.drawString("WORK HARD!!!", 10, screenHeight / 4, Graphics.TOP | Graphics.LEFT);
            } else {
                g.drawString("PARTY TIME!!!", 10, screenHeight / 4, Graphics.TOP | Graphics.LEFT);
            }
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    
}