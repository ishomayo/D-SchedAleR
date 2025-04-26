import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * A splash screen that properly displays animated GIFs
 */
public class SplashScreen extends JWindow {

    public SplashScreen() {
        // Set up the panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLACK);
        
        try {
            // Get the resource URL directly
            String resourcePath = "resources/splash.gif";
            System.out.println("Attempting to load splash screen: " + resourcePath);
            
            URL resourceUrl = null;
            
            // Try multiple ways to get the resource URL
            resourceUrl = ClassLoader.getSystemResource(resourcePath);
            if (resourceUrl != null) {
                System.out.println("Found splash via System ClassLoader");
            }
            
            if (resourceUrl == null) {
                resourceUrl = SplashScreen.class.getClassLoader().getResource(resourcePath);
                if (resourceUrl != null) {
                    System.out.println("Found splash via Class ClassLoader");
                }
            }
            
            if (resourceUrl == null) {
                resourceUrl = SplashScreen.class.getResource("/" + resourcePath);
                if (resourceUrl != null) {
                    System.out.println("Found splash via Class getResource");
                }
            }
            
            // If we found the URL, create ImageIcon directly from URL (preserves animation)
            if (resourceUrl != null) {
                // IMPORTANT: This preserves animation in GIFs
                ImageIcon icon = new ImageIcon(resourceUrl);
                System.out.println("Created ImageIcon: " + icon.getIconWidth() + "x" + icon.getIconHeight());
                
                JLabel imageLabel = new JLabel(icon);
                imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                panel.add(imageLabel, BorderLayout.CENTER);
            } else {
                System.out.println("Could not find splash.gif resource URL");
                throw new Exception("Resource URL not found");
            }
        } catch (Exception e) {
            System.err.println("Error loading splash screen: " + e.getMessage());
            e.printStackTrace();
            
            // Create a fallback splash screen
            JLabel errorLabel = new JLabel("D-SchedAleR");
            errorLabel.setFont(new Font("Arial", Font.BOLD, 48));
            errorLabel.setForeground(Color.WHITE);
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(errorLabel, BorderLayout.CENTER);
            
            JLabel subLabel = new JLabel("Loading...");
            subLabel.setFont(new Font("Arial", Font.PLAIN, 24));
            subLabel.setForeground(Color.LIGHT_GRAY);
            subLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(subLabel, BorderLayout.SOUTH);
        }
        
        setContentPane(panel);
        setSize(1300, 732);
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
                Main.startApplication(); // Start the main application
            }
        };

        worker.execute();
    }

    public static void main(String[] args) {
        // Print current working directory for debugging
        System.out.println("Working Directory: " + System.getProperty("user.dir"));
        
        SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen();
            splash.showSplash();
        });
    }
}