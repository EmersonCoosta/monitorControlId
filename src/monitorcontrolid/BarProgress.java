/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package monitorcontrolid;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

/**
 *
 * @author Emerson
 */
public final class BarProgress extends JFrame {

    private static final long serialVersionUID = 1L;

    private final JProgressBar bar;
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize(); //Pega a resolução da Tela do computador.

    public BarProgress(int total) {
        setIcon();
        this.setSize(680, 45);
        this.setUndecorated(true);
        this.setAlwaysOnTop(true);
        this.setLocation(dim.getSize().width / 2 - this.getSize().width / 2, //Posiciona o frame no centro da tela do computador
                dim.getSize().height / 2 - this.getSize().height / 2);

        bar = new JProgressBar(0, total);

        bar().setStringPainted(true);
        this.add(bar);

    }
    
    public JProgressBar bar(){
        return bar;
    }

    public void iterate(int total) {
        int i = 0;
        while (i <= total) {
            bar().setValue(i);
            i = i + 1;
            try {
                Thread.sleep(100);

            } catch (InterruptedException e) {}
        }
    }

    public static void main(String args[]) {
        BarProgress bar = new BarProgress(10);
        bar.setVisible(true);
        bar.iterate(10);
        bar.setVisible(false);
        System.exit(0);
    }
    
    //COLOCA O ICONE NO APP
    private void setIcon() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/DownloadDados.png")));
    }
}
