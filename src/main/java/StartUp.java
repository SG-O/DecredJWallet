
/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.util.Properties;

/**
 * Decred Util: Created by Joerg Bayer (admin@sg-o.de) on 28.01.2016.
 */
public class StartUp extends JFrame{
    private JProgressBar progressBar1;
    private JPanel rootPanel;
    private JLabel status;
    private JLabel version;

    public StartUp(){
        super("");
        setTitle("DecredJWallet - Loading");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        try {
            setIconImage(ImageIO.read(ClassLoader.getSystemResource("favicon.png")));
        } catch (IOException e) {
        }
        setUndecorated(true);
        setContentPane(rootPanel);
        pack();
        final Properties properties = new Properties();
        try {
            properties.load(this.getClass().getResourceAsStream("project.properties"));
            version.setText(properties.getProperty("version"));
        } catch (IOException e) {
        }
        status.setText("");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void setStatus(String statusText) {
        this.status.setText(statusText);
    }

    public void setProgress(float percent){
        if (percent < 0) percent = 0;
        if (percent > 1) percent = 1;
        progressBar1.setValue((int)(percent * 100));
        progressBar1.setString((int)(percent * 100) + "%");

    }

}
