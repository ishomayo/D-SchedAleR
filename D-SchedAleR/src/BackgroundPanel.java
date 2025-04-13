import javax.swing.JPanel;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Graphics;

public class BackgroundPanel extends JPanel {
    
    private Image backgroundImage;

    // Constructor to load the background image
    public BackgroundPanel(String imagePath) {
        try {
            backgroundImage = Toolkit.getDefaultToolkit().getImage(imagePath);
        } catch (Exception e) {
            System.err.println("Failed to load background image: " + imagePath);
            e.printStackTrace();
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
