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

public class Main extends JFrame{

    protected static int width = 1300, height = 732;
    private CardLayout layout = new CardLayout();
    private JPanel mainPanel = new JPanel(layout);
    
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen();
            splash.showSplash();
        });
    }

    public static void startApplication() {
        SwingUtilities.invokeLater(() -> {
            Main app = new Main();
            app.setVisible(true);
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

        mainPanel.add(lobbyPanel, "Lobby");
    }

    private static JButton createStyledButton(String defaultIconPath, String hoverIconPath, String clickIconPath, Dimension size) {
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
}
