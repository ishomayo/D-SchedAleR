import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.Image;

import java.awt.CardLayout;
import java.awt.Dimension;

public class Main extends JFrame {

    protected static int width = 1300, height = 732;
    private CardLayout layout = new CardLayout();
    private JPanel mainPanel = new JPanel(layout);
    private DataMethod dataMethod;
    private Help Help;
    private Credits Credits;
    // dynise lira was here

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen();
            splash.showSplash();
        });
    }

    public static void startApplication() {
        SwingUtilities.invokeLater(() -> {
            Main main = new Main();
            main.setVisible(true);
        });
    }

    public Main() {
        setSize(width, height);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        add(mainPanel);
        Lobby();

        layout.show(mainPanel, "Lobby");
    }

    public void Lobby() {
        BackgroundPanel lobbyPanel = new BackgroundPanel(CommonConstants.lobbyBG);
        lobbyPanel.setLayout(null);

        JButton startButton = createStyledButton(CommonConstants.startDefault,
                CommonConstants.startHover, CommonConstants.startClick, new Dimension(220, 56));
        startButton.setBounds(30, 160, 220, 56);
        lobbyPanel.add(startButton);

        startButton.addActionListener(e -> {
            dataMethod = new DataMethod(this, layout, mainPanel, width, height);
            mainPanel.add(dataMethod, "SelectDataMethod");
            layout.show(mainPanel, "SelectDataMethod");
        });

        JButton helpButton = createStyledButton(CommonConstants.helpDefault,
                CommonConstants.helpHover, CommonConstants.helpClick, new Dimension(220, 56));
        helpButton.setBounds(30, 216, 220, 56);
        lobbyPanel.add(helpButton);

        helpButton.addActionListener(e -> {
            Help = new Help(this, layout, mainPanel, width, height);
            mainPanel.add(Help, "Help");
            layout.show(mainPanel, "Help");
        });

        JButton creditsButton = createStyledButton(CommonConstants.creditsDefault,
                CommonConstants.creditsHover, CommonConstants.creditsClick, new Dimension(220, 56));
        creditsButton.setBounds(30, 272, 220, 56);
        lobbyPanel.add(creditsButton);

        creditsButton.addActionListener(e -> {
            Credits = new Credits(this, layout, mainPanel, width, height);
            mainPanel.add(Credits, "Credits");
            layout.show(mainPanel, "Credits");
        });

        JButton exitButton = createStyledButton(CommonConstants.exitDefault,
                CommonConstants.exitHover, CommonConstants.exitClick, new Dimension(220, 56));
        exitButton.setBounds(30, 328, 220, 56);
        lobbyPanel.add(exitButton);
        exitButton.addActionListener(e -> System.exit(0));

        mainPanel.add(lobbyPanel, "Lobby");
    }

    // Getter for DataMethod
    public DataMethod getDataMethod() {
        return dataMethod;
    }

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
}