import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JWindow {

    public SplashScreen() {
        JPanel panel = new JPanel(new BorderLayout());

        // Load GIF without resizing
        ImageIcon gifIcon = new ImageIcon(CommonConstants.splashScreen);
        if (gifIcon.getIconWidth() == -1) {
            System.err.println("Error loading splash screen: " + CommonConstants.splashScreen);
        }

        // Label to hold the original GIF
        JLabel gifLabel = new JLabel(gifIcon);
        gifLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gifLabel.setVerticalAlignment(SwingConstants.CENTER);

        panel.add(gifLabel, BorderLayout.CENTER);
        setContentPane(panel);
        setSize(1300, 732); // Set fixed size
        setLocationRelativeTo(null);
    }

    public void showSplash() {
        setVisible(true);

        // Use SwingWorker to delay execution without freezing UI
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Thread.sleep(3000); // Show for 3 seconds
                return null;
            }

            @Override
            protected void done() {
                dispose(); // Close splash screen
                Main.startApplication(); // Start the Main window after splash
            }
        };

        worker.execute();
    }

    public static void main(String[] args) {
        SplashScreen splash = new SplashScreen();
        splash.showSplash();
    }
}