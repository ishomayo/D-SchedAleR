

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

    public AlgorithmSelection(Main main, CardLayout layout, JPanel mainPanel, int width, int height) {
        this.main = main;
        this.layout = layout;
        this.mainPanel = mainPanel;
        this.width = width;
        this.height = height;

        setSize(width, height);
        setLayout(null);
        backgroundImage = CommonConstants.loadImage(CommonConstants.algoSelectBG);

        JButton backButton = createStyledButton(CommonConstants.backDefault,
        CommonConstants.backHover, CommonConstants.backClick, new Dimension(220, 56));
        backButton.setBounds(37, 625, 220, 56);
        add(backButton);
        backButton.addActionListener(e -> layout.show(mainPanel, "Lobby"));

        JButton fcfsButton = createStyledButton(CommonConstants.fcfsDefault,
        CommonConstants.fcfsHover, CommonConstants.fcfsClick, new Dimension(283, 207));
        fcfsButton.setBounds(312, 170, 283, 207);
        add(fcfsButton);

        JButton sstfButton = createStyledButton(CommonConstants.sstfDefault,
        CommonConstants.sstfHover, CommonConstants.sstfClick, new Dimension(283, 207));
        sstfButton.setBounds(628, 170, 283, 207);
        add(sstfButton);

        JButton scanButton = createStyledButton(CommonConstants.scanDefault,
        CommonConstants.scanHover, CommonConstants.scanClick, new Dimension(283, 207));
        scanButton.setBounds(944, 170, 283, 207);
        add(scanButton);

        JButton cscanButton = createStyledButton(CommonConstants.cscanDefault,
        CommonConstants.cscanHover, CommonConstants.cscanClick, new Dimension(283, 207));
        cscanButton.setBounds(312, 405, 283, 207);
        add(cscanButton);

        JButton lookButton = createStyledButton(CommonConstants.lookDefault,
        CommonConstants.lookHover, CommonConstants.lookClick, new Dimension(283, 207));
        lookButton.setBounds(628, 405, 283, 207);
        add(lookButton);

        JButton clookButton = createStyledButton(CommonConstants.clookDefault,
        CommonConstants.clookHover, CommonConstants.clookClick, new Dimension(283, 207));
        clookButton.setBounds(944, 405, 283, 207);
        add(clookButton);

        JButton simulateALLButton = createStyledButton(CommonConstants.simulateALLDefault,
        CommonConstants.simulateALLHover, CommonConstants.simulateALLClick, new Dimension(220, 56));
        simulateALLButton.setBounds(660, 610, 220, 70);
        add(simulateALLButton);
    }


    
        //*******************************************************
    //
    //                  HELPER METHODS
    //
    //*******************************************************

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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}