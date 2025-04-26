import javax.swing.JPanel;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;

public class BackgroundPanel extends JPanel {
    
    private Image backgroundImage;

    /**
     * Constructor to load the background image from resources
     * @param imagePath The resource path to the image
     */
    public BackgroundPanel(String imagePath) {
        setLayout(new BorderLayout());
        
        // Load the image using CommonConstants resource loader
        backgroundImage = CommonConstants.loadImage(imagePath);
        
        // Make the panel transparent so the background shows
        setOpaque(false);
        
        if (backgroundImage == null) {
            // Fallback for missing background image
            setOpaque(true);
            setBackground(Color.LIGHT_GRAY);
            JLabel errorLabel = new JLabel("Background image not found: " + imagePath);
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            errorLabel.setForeground(Color.RED);
            add(errorLabel, BorderLayout.CENTER);
            System.err.println("Failed to load background image: " + imagePath);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}