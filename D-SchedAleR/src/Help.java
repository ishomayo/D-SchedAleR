import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.event.DocumentEvent;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import javax.swing.text.*;
import java.util.List;

public class Help extends JPanel {

    private JPanel mainPanel;
    private CardLayout layout;
    private Main main;
    public Image backgroundImage;
    private int width, height, queueLength, headStart;
    private java.util.List<Integer> requestQueue;
    private String direction;
    private JLabel queueLengthLabel, headStartLabel, directionLabel;
    private JTextArea requestQueueArea;
    private DataMethod dataMethod;

    public Help(Main main, CardLayout layout, JPanel mainPanel, int width, int height) {
        this.main = main;
        this.layout = layout;
        this.mainPanel = mainPanel;
        this.width = width;
        this.height = height;

        setSize(width, height);
        setLayout(null);
        backgroundImage = CommonConstants.loadImage(CommonConstants.help);

        BackgroundPanel lobbyPanel = new BackgroundPanel(CommonConstants.lobbyBG);
        lobbyPanel.setLayout(null);

        JButton startButton = createStyledButton(CommonConstants.startDefault,
                CommonConstants.startHover, CommonConstants.startClick, new Dimension(220, 56));
        startButton.setBounds(30, 160, 220, 56);
        lobbyPanel.add(startButton);

     

        JButton helpButton = createStyledButton(CommonConstants.helpDefault,
                CommonConstants.helpHover, CommonConstants.helpClick, new Dimension(220, 56));
        helpButton.setBounds(30, 216, 220, 56);
        lobbyPanel.add(helpButton);

        JButton creditsButton = createStyledButton(CommonConstants.creditsDefault,
                CommonConstants.creditsHover, CommonConstants.creditsClick, new Dimension(220, 56));
        creditsButton.setBounds(30, 272, 220, 56);
        lobbyPanel.add(creditsButton);

        JButton exitButton = createStyledButton(CommonConstants.exitDefault,
                CommonConstants.exitHover, CommonConstants.exitClick, new Dimension(220, 56));
        exitButton.setBounds(30, 328, 220, 56);
        lobbyPanel.add(exitButton);
        exitButton.addActionListener(e -> System.exit(0));

        JButton backButton = createStyledButton(CommonConstants.backDefault,
        CommonConstants.backHover, CommonConstants.backClick, new Dimension(220, 56));
        backButton.setBounds(37, 625, 220, 56);
        add(backButton);
        backButton.addActionListener(e -> layout.show(mainPanel, "Lobby"));
    }

    // *******************************************************
    //
    // HELPER METHODS
    //
    // *******************************************************

    private static JButton createStyledButton(String defaultIconPath, String hoverIconPath, String clickIconPath,
            Dimension size) {
        JButton button = new JButton();
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(size);

        ImageIcon defaultIcon = scaleImage(defaultIconPath, size);
        ImageIcon hoverIcon = scaleImage(hoverIconPath, size);
        ImageIcon clickIcon = scaleImage(clickIconPath, size);

        button.setIcon(defaultIcon);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setIcon(hoverIcon);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(defaultIcon);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setIcon(clickIcon);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setIcon(hoverIcon);
            }
        });

        return button;
    }

    private static ImageIcon scaleImage(String path, Dimension size) {
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}