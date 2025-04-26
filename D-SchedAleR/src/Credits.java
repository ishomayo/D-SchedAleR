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

public class Credits extends JPanel {

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

    public Credits(Main main, CardLayout layout, JPanel mainPanel, int width, int height) {
        this.main = main;
        this.layout = layout;
        this.mainPanel = mainPanel;
        this.width = width;
        this.height = height;

        setSize(width, height);
        setLayout(null);
        backgroundImage = CommonConstants.loadImage(CommonConstants.credits);

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

    public static JButton createStyledButton(String defaultIconPath, String hoverIconPath, String clickIconPath,
            Dimension size) {
        JButton button = new JButton();
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(size);

        // Use CommonConstants to load the images
        ImageIcon defaultIcon = scaleImage(defaultIconPath, size);
        ImageIcon hoverIcon = scaleImage(hoverIconPath, size);
        ImageIcon clickIcon = scaleImage(clickIconPath, size);

        // Set the default icon if available, or add a fallback text
        if (defaultIcon != null) {
            button.setIcon(defaultIcon);
        } else {
            button.setText("Button");
            System.err.println("Failed to load button images for: " + defaultIconPath);
        }

        // Only add mouse listeners if we have the hover/click icons
        if (hoverIcon != null && clickIcon != null) {
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (button.isEnabled()) {
                        button.setIcon(hoverIcon);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (button.isEnabled()) {
                        button.setIcon(defaultIcon);
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if (button.isEnabled()) {
                        button.setIcon(clickIcon);
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (button.isEnabled()) {
                        if (button.contains(e.getPoint())) {
                            button.setIcon(hoverIcon);
                        } else {
                            button.setIcon(defaultIcon);
                        }
                    }
                }
            });
        }

        return button;
    }

    /**
     * Scales an image loaded from resources
     */
    private static ImageIcon scaleImage(String path, Dimension size) {
        // Use CommonConstants to load the image from resources
        ImageIcon icon = CommonConstants.createImageIcon(path);

        // If the image couldn't be loaded, return null
        if (icon == null) {
            return null;
        }

        // Scale the image
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