package mystuff;



import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;

import mystuff.checkers.ui.ugolki.MainVisualUgolki;

public class UgolkiApplet extends JApplet {
    //Called when this applet is loaded into the browser.
    public void init() {
        //Execute a job on the event-dispatching thread; creating this applet's GUI.
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                	MainVisualUgolki frame = new MainVisualUgolki();
                    add(frame);
                }
            });
        } catch (Exception e) {
            System.err.println("createGUI didn't complete successfully");
        }
    }
}
