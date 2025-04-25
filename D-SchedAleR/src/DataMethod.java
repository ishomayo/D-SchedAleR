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


public class DataMethod extends JPanel {

    private JPanel mainPanel;
    private CardLayout layout;
    private Main main;
    public Image backgroundImage;
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
        userInputButton.addActionListener(e -> showUserInput());
        fileInputButton.addActionListener(e -> showFileInput());
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

    public void showUserInput() {
        // Mutable reference for background image
        backgroundImage = CommonConstants.loadImage(CommonConstants.userDataMethodBG);
    
        JPanel userInputPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        userInputPanel.setOpaque(false);
    
        // Create and configure the "Back" button
        JButton backButton = createStyledButton(CommonConstants.backDefault,
                CommonConstants.backHover, CommonConstants.backClick, new Dimension(220, 56));
        backButton.setBounds(37, 625, 220, 56);
        userInputPanel.add(backButton);
        
        // Create and configure the "Continue" button
        JButton continueButton = createStyledButton(CommonConstants.continueDefault,
                CommonConstants.continueHover, CommonConstants.continueClick, new Dimension(220, 56));
        continueButton.setBounds(992, 565, 220, 70);
        continueButton.setEnabled(false);
        userInputPanel.add(continueButton);
    
        // Action: Back to Lobby + restore original background
        backButton.addActionListener(e -> {
            layout.show(mainPanel, "Lobby");
        });

        continueButton.addActionListener(e -> {
            AlgorithmSelection AlgorithmSelectionPanel = new AlgorithmSelection(main, layout, mainPanel, width, height);
            mainPanel.add(AlgorithmSelectionPanel, "AlgorithmSelection");
            layout.show(mainPanel, "AlgorithmSelection");
        });

        // // Font settings
        Font labelFont = new Font("Arial", Font.BOLD, 22); // Or "Montserrat" if installed
        Font textAreaFont = new Font("Arial", Font.BOLD, 24);
        Font directionFont = new Font("Arial", Font.BOLD, 20);


        // Create and configure a JLabel to display the queue length
        JTextField queueLengthField = new JTextField();
        queueLengthField.setFont(labelFont);
        queueLengthField.setForeground(Color.BLACK);
        queueLengthField.setBounds(327, 205, 110, 55);
        queueLengthField.setBackground(Color.decode("#e7e7e7"));
        queueLengthField.setBorder(BorderFactory.createEmptyBorder());   
        queueLengthField.setFocusTraversalKeysEnabled(false);
        queueLengthField.setCaretColor(Color.BLACK);
        userInputPanel.add(queueLengthField);

        // Add DocumentFilter to limit input to 2 digits
        ((AbstractDocument) queueLengthField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;

                String newText = fb.getDocument().getText(0, fb.getDocument().getLength()) + string;
                if (newText.matches("\\d{0,2}")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;

                String oldText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = oldText.substring(0, offset) + text + oldText.substring(offset + length);
                if (newText.matches("\\d{0,2}")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        // // Create and configure a JLabel to display the head start position
        JTextField headStartLabelField = new JTextField();
        headStartLabelField.setFont(labelFont);
        headStartLabelField.setForeground(Color.BLACK);
        headStartLabelField.setBounds(325, 362, 110, 55);
        headStartLabelField.setBackground(Color.decode("#e7e7e7"));
        headStartLabelField.setBorder(BorderFactory.createEmptyBorder());   
        headStartLabelField.setFocusTraversalKeysEnabled(false);
        headStartLabelField.setCaretColor(Color.BLACK);
        userInputPanel.add(headStartLabelField);

        // Limit input to 3 digits
        ((AbstractDocument) headStartLabelField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;

                String newText = fb.getDocument().getText(0, fb.getDocument().getLength()) + string;
                if (newText.matches("\\d{0,3}")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;

                String oldText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = oldText.substring(0, offset) + text + oldText.substring(offset + length);
                if (newText.matches("\\d{0,3}")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        JComboBox<String> directionBox = new JComboBox<>(new String[] { "LEFT", "RIGHT" });
        directionBox.setFont(directionFont);
        directionBox.setForeground(Color.BLACK);
        directionBox.setBounds(315, 514, 145, 68);
        directionBox.setBackground(Color.decode("#e7e7e7"));
        directionBox.setBorder(BorderFactory.createEmptyBorder());   
        directionBox.setFocusTraversalKeysEnabled(false);
        userInputPanel.add(directionBox);

        JTextArea requestQueueField = new JTextArea();
        requestQueueField.setFont(labelFont);
        requestQueueField.setForeground(Color.BLACK);
        requestQueueField.setBounds(743, 205, 457, 210);
        requestQueueField.setBackground(Color.decode("#e7e7e7"));
        requestQueueField.setCaretColor(Color.BLACK);
        // requestQueueField.setFocusTraversalKeysEnabled(false);
        requestQueueField.setMargin(new Insets(5, 5, 0, 0)); // Top-left padding
        requestQueueField.setLineWrap(true);
        requestQueueField.setWrapStyleWord(true);
        requestQueueField.setAlignmentX(Component.LEFT_ALIGNMENT);
        requestQueueField.setBorder(BorderFactory.createEmptyBorder());
        userInputPanel.add(requestQueueField);

        // Placeholder behavior
        requestQueueField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (requestQueueField.getText().equals("Values are from 0 to 199")) {
                    requestQueueField.setText("");
                    requestQueueField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (requestQueueField.getText().trim().isEmpty()) {
                    requestQueueField.setForeground(Color.GRAY);
                    requestQueueField.setText("Values are from 0 to 199");
                }
            }
        });

        requestQueueField.getDocument().addDocumentListener(new DocumentListener() {
            private void validateInput() {
                String text = requestQueueField.getText().trim();
                if (text.equals("Values are from 0 to 199")) return;
        
                String[] tokens = text.split("\\s+");
                int expectedLength;
                try {
                    expectedLength = Integer.parseInt(queueLengthField.getText().trim());
                } catch (NumberFormatException e) {
                    continueButton.setEnabled(false);
                    return;
                }
        
                if (tokens.length != expectedLength) {
                    continueButton.setEnabled(false);
                    return;
                }
        
                for (String token : tokens) {
                    try {
                        int num = Integer.parseInt(token);
                        if (num < 0 || num > 199) {
                            continueButton.setEnabled(false);
                            return;
                        }
                    } catch (NumberFormatException e) {
                        continueButton.setEnabled(false);
                        return;
                    }
                }
        
                continueButton.setEnabled(true); // All checks passed
            }
        
            @Override public void insertUpdate(DocumentEvent e) { validateInput(); }
            @Override public void removeUpdate(DocumentEvent e) { validateInput(); }
            @Override public void changedUpdate(DocumentEvent e) { validateInput(); }
        });

        setQueueLengthFromField(queueLengthField);
        setHeadStartFromField(headStartLabelField);
        setDirectionFromBox(directionBox);
        setRequestQueueFromField(requestQueueField);
        
        mainPanel.add(userInputPanel, "UserInputScreen");
        layout.show(mainPanel, "UserInputScreen");
    }

    public void showFileInput() {
        // Mutable reference for background image
        final ImageIcon[] backgroundImage = { new ImageIcon(CommonConstants.fileMethodBG) };
    
        JPanel fileInputPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage[0].getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        fileInputPanel.setOpaque(false);
    
        // Create and configure the "Back" button
        JButton backButton = createStyledButton(CommonConstants.backDefault,
                CommonConstants.backHover, CommonConstants.backClick, new Dimension(220, 56));
        backButton.setBounds(37, 625, 220, 56);
        fileInputPanel.add(backButton);
        
        // Create and configure the "Continue" button
        JButton continueButton = createStyledButton(CommonConstants.continueDefault,
                CommonConstants.continueHover, CommonConstants.continueClick, new Dimension(220, 56));
        continueButton.setBounds(992, 565, 220, 70);
        continueButton.setEnabled(false);
        fileInputPanel.add(continueButton);
    
        // Create and configure the "Generate" button
        JButton uploadButton = createStyledButton(CommonConstants.uploadDefault,
                CommonConstants.uploadHover, CommonConstants.uploadClick, new Dimension(220, 56));
                uploadButton.setBounds(743, 565, 220, 70);
        fileInputPanel.add(uploadButton);
    
        // Action: Back to Lobby + restore original background
        backButton.addActionListener(e -> {
            backgroundImage[0] = new ImageIcon(CommonConstants.randomDataMethodBG);
            fileInputPanel.repaint();  // Optional if you're navigating away anyway
            layout.show(mainPanel, "Lobby");
        });

        continueButton.addActionListener(e -> {
            AlgorithmSelection AlgorithmSelectionPanel = new AlgorithmSelection(main, layout, mainPanel, width, height);
            mainPanel.add(AlgorithmSelectionPanel, "AlgorithmSelection");
            layout.show(mainPanel, "AlgorithmSelection");
        });

                // Action: Generate + Change background
        uploadButton.addActionListener(e -> {
            fileUpload(continueButton);
            backgroundImage[0] = new ImageIcon(CommonConstants.fileUploadedMethodBG);
            fileInputPanel.repaint();  // Refresh to apply new background
            continueButton.setEnabled(true);
        });

        // Font settings
        Font labelFont = new Font("Arial", Font.BOLD, 22); // Or "Montserrat" if installed
        Font textAreaFont = new Font("Arial", Font.BOLD, 24);

        // Create and configure a JLabel to display the queue length
        queueLengthLabel = new JLabel();
        queueLengthLabel.setFont(labelFont);
        queueLengthLabel.setForeground(Color.BLACK);
        queueLengthLabel.setBounds(325, 216, 300, 30);
        fileInputPanel.add(queueLengthLabel);

        // Create and configure a JLabel to display the head start position
        headStartLabel = new JLabel();
        headStartLabel.setFont(labelFont);
        headStartLabel.setForeground(Color.BLACK);
        headStartLabel.setBounds(325, 370, 300, 30);
        fileInputPanel.add(headStartLabel);

        // Create and configure a JLabel to display the direction of head movement
        directionLabel = new JLabel();
        directionLabel.setFont(labelFont);
        directionLabel.setForeground(Color.BLACK);
        directionLabel.setBounds(325, 531, 300, 30);
        fileInputPanel.add(directionLabel);

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
        fileInputPanel.add(requestQueueArea);

        mainPanel.add(fileInputPanel, "RandomScreen");
        layout.show(mainPanel, "RandomScreen");
    }

    public void fileUpload(JButton continueButton) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        int returnValue = fileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                String queueLengthStr = reader.readLine(); // line 1
                String requestQueueStr = reader.readLine(); // line 2
                String headStartStr = reader.readLine();    // line 3
                String directionStr = reader.readLine();    // line 4

                if (queueLengthStr != null && requestQueueStr != null && headStartStr != null && directionStr != null) {
                    int queueLength = Integer.parseInt(queueLengthStr.trim());
                    int headStart = Integer.parseInt(headStartStr.trim());
                    String direction = directionStr.trim().toUpperCase(); // "LEFT" or "RIGHT"

                    List<Integer> requestQueue = new ArrayList<>();
                    String[] parts = requestQueueStr.trim().split("\\s+");
                    if (parts.length != queueLength) {
                        JOptionPane.showMessageDialog(null, "Mismatch between queue length and number of requests.");
                        return;
                    }

                    for (String part : parts) {
                        requestQueue.add(Integer.parseInt(part.trim()));
                    }

                    // You can now use these values in your simulation
                    // queueLength, requestQueue, headStart, direction

                    System.out.println("Queue Length: " + queueLength);
                    System.out.println("Request Queue: " + requestQueue);
                    System.out.println("Head Start: " + headStart);
                    System.out.println("Direction: " + direction);

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

                    // You may want to update your GUI or internal variables here
                    continueButton.setEnabled(true);

                } else {
                    JOptionPane.showMessageDialog(null, "Incomplete or invalid file format.");
                }
            } catch (IOException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Error reading or parsing the file.");
            }
        }
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

    // Setter for queue length
    public void setQueueLengthFromField(JTextField queueLengthField) {
        try {
            this.queueLength = Integer.parseInt(queueLengthField.getText().trim());
        } catch (NumberFormatException e) {
            System.err.println("Invalid queue length input");
            this.queueLength = 0; // or some fallback
        }
    }

    // Setter for head start position
    public void setHeadStartFromField(JTextField headStartField) {
        try {
            this.headStart = Integer.parseInt(headStartField.getText().trim());
        } catch (NumberFormatException e) {
            System.err.println("Invalid head start input");
            this.headStart = 0;
        }
    }

    // Setter for direction (from JComboBox)
    public void setDirectionFromBox(JComboBox<String> directionBox) {
        Object selected = directionBox.getSelectedItem();
        this.direction = selected != null ? selected.toString() : "N/A";
    }

    // Setter for request queue (values must be separated by space)
    public void setRequestQueueFromField(JTextArea requestQueueField) {
        this.requestQueue = new ArrayList<>();
        String text = requestQueueField.getText().trim();
        if (!text.isEmpty()) {
            String[] tokens = text.split("\\s+");
            for (String token : tokens) {
                try {
                    int value = Integer.parseInt(token);
                    if (value >= 0 && value <= 199) {
                        this.requestQueue.add(value);
                    } else {
                        System.err.println("Invalid value in request queue: " + value);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Non-numeric value in request queue: " + token);
                }
            }
        }
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
