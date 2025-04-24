import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

public class AlgorithmSelection extends JPanel {

    private JPanel mainPanel;
    private CardLayout layout;
    private Main main;
    private Image backgroundImage;
    private int width, height;
    private DataMethod dataMethod; // Add reference to DataMethod

    public AlgorithmSelection(Main main, CardLayout layout, JPanel mainPanel, int width, int height) {
        this.main = main;
        this.layout = layout;
        this.mainPanel = mainPanel;
        this.width = width;
        this.height = height;

        setSize(width, height);
        setLayout(null);
        backgroundImage = CommonConstants.loadImage(CommonConstants.algoSelectBG);

        
    }

    // Set the DataMethod reference
    public void setDataMethod(DataMethod dataMethod) {
        this.dataMethod = dataMethod;
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