import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DataMethod extends JPanel {

    private JPanel mainPanel;
    private CardLayout layout;
    private Main main;
    public final Image backgroundImage;
    private int width, height, queueLength, headStart;
    private java.util.List<Integer> requestQueue;
    private String direction;
    private JLabel queueLengthLabel, headStartLabel, directionLabel;
    private JTextArea requestQueueArea;

    public DataMethod(Main main, CardLayout layout, JPanel mainPanel, int width, int height) {
        this.main = main;
        this.layout = layout;
        this.mainPanel = mainPanel;
        this.width = width;
        this.height = height;

        setSize(width, height);
        setLayout(null);
        backgroundImage = CommonConstants.loadImage(CommonConstants.dataMethodBG);

        JButton randomButton = createStyledButton(CommonConstants.randomDefault,
        CommonConstants.randomHover, CommonConstants.randomClick, new Dimension(220, 56));
        randomButton.setBounds(37, 457, 220, 56);
        add(randomButton);

        JButton userInputButton = createStyledButton(CommonConstants.userDefault,
        CommonConstants.userHover, CommonConstants.userClick, new Dimension(220, 56));
        userInputButton.setBounds(37, 513, 220, 56);
        add(userInputButton);

        JButton fileInputButton = createStyledButton(CommonConstants.fileDefault,
        CommonConstants.fileHover, CommonConstants.fileClick, new Dimension(220, 56));
        fileInputButton.setBounds(37, 569, 220, 56);
        add(fileInputButton);

        JButton backButton = createStyledButton(CommonConstants.backDefault,
        CommonConstants.backHover, CommonConstants.backClick, new Dimension(220, 56));
        backButton.setBounds(37, 625, 220, 56);
        add(backButton);

        randomButton.addActionListener(e -> showRandom());
        backButton.addActionListener(e -> layout.show(mainPanel, "Lobby"));
    }

    public void showRandom() {
        // Mutable reference for background image
        final ImageIcon[] backgroundImage = { new ImageIcon(CommonConstants.randomDataMethodBG) };
    
        JPanel randomPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage[0].getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        randomPanel.setOpaque(false);
    
        // Create and configure the "Back" button
        JButton backButton = createStyledButton(CommonConstants.backDefault,
                CommonConstants.backHover, CommonConstants.backClick, new Dimension(220, 56));
        backButton.setBounds(37, 625, 220, 56);
        randomPanel.add(backButton);
        
        // Create and configure the "Continue" button
        JButton continueButton = createStyledButton(CommonConstants.continueDefault,
                CommonConstants.continueHover, CommonConstants.continueClick, new Dimension(220, 56));
        continueButton.setBounds(992, 565, 220, 70);
        continueButton.setEnabled(false);
        randomPanel.add(continueButton);
    
        // Create and configure the "Generate" button
        JButton generateButton = createStyledButton(CommonConstants.generateDefault,
                CommonConstants.generateHover, CommonConstants.generateClick, new Dimension(220, 56));
        generateButton.setBounds(743, 565, 220, 70);
        randomPanel.add(generateButton);
    
        // Action: Generate + Change background
        generateButton.addActionListener(e -> {
            generateRandom();
            backgroundImage[0] = new ImageIcon(CommonConstants.randomGeneratedMethodBG);
            randomPanel.repaint();  // Refresh to apply new background
            continueButton.setEnabled(true);
        });
    
        // Action: Back to Lobby + restore original background
        backButton.addActionListener(e -> {
            backgroundImage[0] = new ImageIcon(CommonConstants.randomDataMethodBG);
            randomPanel.repaint();  // Optional if you're navigating away anyway
            layout.show(mainPanel, "Lobby");
        });

        continueButton.addActionListener(e -> {
            AlgorithmSelection AlgorithmSelectionPanel = new AlgorithmSelection(main, layout, mainPanel, width, height);
            mainPanel.add(AlgorithmSelectionPanel, "AlgorithmSelection");
            layout.show(mainPanel, "AlgorithmSelection");
        });

        // Font settings
        Font labelFont = new Font("Arial", Font.BOLD, 22); // Or "Montserrat" if installed
        Font textAreaFont = new Font("Arial", Font.BOLD, 24);

        // Create and configure a JLabel to display the queue length
        queueLengthLabel = new JLabel();
        queueLengthLabel.setFont(labelFont);
        queueLengthLabel.setForeground(Color.BLACK);
        queueLengthLabel.setBounds(325, 216, 300, 30);
        randomPanel.add(queueLengthLabel);

        // Create and configure a JLabel to display the head start position
        headStartLabel = new JLabel();
        headStartLabel.setFont(labelFont);
        headStartLabel.setForeground(Color.BLACK);
        headStartLabel.setBounds(325, 370, 300, 30);
        randomPanel.add(headStartLabel);

        // Create and configure a JLabel to display the direction of head movement
        directionLabel = new JLabel();
        directionLabel.setFont(labelFont);
        directionLabel.setForeground(Color.BLACK);
        directionLabel.setBounds(325, 531, 300, 30);
        randomPanel.add(directionLabel);

        // Create and configure a JTextArea to display the request queue numbers
        requestQueueArea = new JTextArea();
        requestQueueArea.setFont(textAreaFont);
        requestQueueArea.setForeground(Color.BLACK);
        requestQueueArea.setOpaque(false);
        requestQueueArea.setEditable(false);
        requestQueueArea.setLineWrap(true);
        requestQueueArea.setWrapStyleWord(true);
        requestQueueArea.setBounds(740, 218, 477, 182);
        requestQueueArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        requestQueueArea.setMargin(new Insets(0, 5, 0, 5));
        randomPanel.add(requestQueueArea);

        mainPanel.add(randomPanel, "RandomScreen");
        layout.show(mainPanel, "RandomScreen");
    }
    
    private void generateRandom() {
        // Random queue length between 1 and 40
        queueLength = (int)(Math.random() * 40) + 1;
    
        // Generate queue values from 0 to 199
        requestQueue = new java.util.ArrayList<>();
        for (int i = 0; i < queueLength; i++) {
            requestQueue.add((int)(Math.random() * 200));
        }
    
        // Head's starting position from 0 to 199
        headStart = (int)(Math.random() * 200);
    
        // Random direction
        direction = Math.random() < 0.5 ? "LEFT" : "RIGHT";
    
        // Update labels and text area
        queueLengthLabel.setText(String.valueOf(queueLength));
        headStartLabel.setText(String.valueOf(headStart));
        directionLabel.setText(direction);
    
        // Format the queue list without brackets
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < requestQueue.size(); i++) {
            sb.append(requestQueue.get(i));
            if (i != requestQueue.size() - 1) sb.append(" ");
        }
        requestQueueArea.setText(sb.toString());
    }
    
    // Getter for queue length
    public int getQueueLength() {
        return queueLength;
    }

    // Getter for request queue
    public java.util.List<Integer> getRequestQueue() {
        return requestQueue;
    }

    // Getter for head start position
    public int getHeadStart() {
        return headStart;
    }

    // Getter for direction
    public String getDirection() {
        return direction;
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
