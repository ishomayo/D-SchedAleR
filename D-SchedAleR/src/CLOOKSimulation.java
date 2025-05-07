import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

// Add these imports at the top of your file
import javax.imageio.ImageIO;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.text.SimpleDateFormat;

// Add iText PDF library imports (you'll need to add the dependency to your project)
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

public class CLOOKSimulation extends JPanel {
    private JPanel mainPanel;
    private CardLayout layout;
    private Main main;
    private Image backgroundImage;
    private JButton startButton, restartButton, pauseButton, saveButton;
    private int width, height;
    private JTextField totalSeekTimeField, totalHeadMovementsField;
    private JTextField orderSequenceField, startingPositionField;
    private GraphPanel graphPanel;
    private List<Integer> requestQueue;
    private int headStart;
    private String direction;
    private int totalSeekTime;
    private int totalHeadMovements;
    private JSlider speedSlider;
    private Timer animationTimer;
    private boolean isAnimating = false;
    private int currentStep = 0;
    private JLabel timerLabel;
    private long startTime;
    private long elapsedTime = 0;
    private Timer displayTimer;
    private List<Point> animationPoints = new ArrayList<>();
    private final int MAX_DISK_SIZE = 199; // Maximum disk position (0-199)

    public CLOOKSimulation(Main main, CardLayout layout, JPanel mainPanel, int width, int height,
            List<Integer> requestQueue, int headStart, String direction) {
        this.main = main;
        this.layout = layout;
        this.mainPanel = mainPanel;
        this.width = width;
        this.height = height;
        this.requestQueue = requestQueue;
        this.headStart = headStart;
        this.direction = direction;

        setSize(width, height);
        setLayout(null);
        backgroundImage = CommonConstants.loadImage(CommonConstants.simulation_screen_CLOOK); // You might want to
                                                                                              // update this constant

        createUI();
        calculateCLOOK();
    }

    /**
     * Creates a custom styled slider with transparent background
     */
    private JSlider createCustomSlider(int min, int max, int value) {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, value) {
            @Override
            public void updateUI() {
                setUI(new CustomSliderUI(this));
                updateLabelUIs();
            }

            // Override to ensure the component is fully transparent
            @Override
            protected void paintComponent(Graphics g) {
                // Don't call super.paintComponent to avoid background painting
                // Only call UI's paint methods that we've customized
                if (getUI() != null) {
                    // Let the UI delegate paint
                    getUI().paint(g, this);
                }
            }
        };

        // Remove ticks and labels that come by default
        slider.setPaintTicks(false);
        slider.setPaintLabels(false);

        // Make the slider transparent
        slider.setOpaque(false);
        slider.setBackground(new Color(0, 0, 0, 0)); // Fully transparent

        return slider;
    }

    /**
     * Custom UI for the slider component with transparent background
     */
    class CustomSliderUI extends javax.swing.plaf.basic.BasicSliderUI {

        public CustomSliderUI(JSlider slider) {
            super(slider);
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            // Only paint the specific parts we want (track and thumb)
            paintTrack(g);
            paintThumb(g);
            // Deliberately omit painting other parts
        }

        @Override
        public void paintTrack(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = slider.getWidth();
            int height = slider.getHeight();
            int trackHeight = 4;

            // Draw the track - black line
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(trackHeight));
            g2d.drawLine(0, height / 2, width, height / 2);
        }

        @Override
        public void paintThumb(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Rectangle thumbBounds = thumbRect;

            // Create a red filled circle for the thumb
            g2d.setColor(Color.RED);
            int diameter = 16; // Size of the circular thumb
            int x = thumbBounds.x + (thumbBounds.width - diameter) / 2;
            int y = thumbBounds.y + (thumbBounds.height - diameter) / 2;
            g2d.fillOval(x, y, diameter, diameter);
        }

        @Override
        protected Dimension getThumbSize() {
            // Make the thumb area larger to make it easier to grab
            return new Dimension(20, 20);
        }
    }

    private JButton createOblongButton(String text, int width, int height) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // Choose background color based on button state
                if (!isEnabled()) {
                    // Distinctive gray for disabled state
                    g2.setColor(new Color(220, 220, 220));
                } else if (getModel().isPressed()) {
                    // Darker background when pressed
                    g2.setColor(new Color(200, 200, 200));
                } else if (getModel().isRollover()) {
                    // Slightly lighter background when hovered
                    g2.setColor(new Color(240, 240, 240));
                } else {
                    // Default background
                    g2.setColor(Color.WHITE);
                }

                // Draw rounded rectangle background
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, height, height);

                // Draw border (light gray for disabled)
                if (!isEnabled()) {
                    g2.setColor(new Color(180, 180, 180)); // Lighter border for disabled
                } else {
                    g2.setColor(Color.BLACK);
                }
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, height, height);

                // Text settings - ensure perfect centering
                Font buttonFont = new Font("Arial", Font.BOLD, 14);
                g2.setFont(buttonFont);

                FontMetrics metrics = g2.getFontMetrics(buttonFont);
                int textWidth = metrics.stringWidth(getText());
                int textHeight = metrics.getHeight();

                // Calculate exact center position
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() - textHeight) / 2 + metrics.getAscent();

                // Draw text (gray for disabled)
                if (!isEnabled()) {
                    g2.setColor(new Color(150, 150, 150)); // Gray text for disabled
                } else {
                    g2.setColor(Color.BLACK);
                }
                g2.drawString(getText(), x, y);
                g2.dispose();
            }

            // Override getPreferredSize to ensure the button has the right dimensions
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(width, height);
            }
        };

        // Set button properties
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);

        return button;
    }

    private void createUI() {
        // Title panel at the top
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBounds(0, 0, width, 50);
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        add(titlePanel);

        // Algorithm name panel (top right)
        JPanel algoPanel = new JPanel(new BorderLayout());
        algoPanel.setBounds(width - 400, 150, 400, 50);
        algoPanel.setOpaque(false);

        JLabel algoLabel = new JLabel("");
        algoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        algoLabel.setHorizontalAlignment(JLabel.CENTER);
        algoPanel.add(algoLabel, BorderLayout.CENTER);

        add(algoPanel);

        // Back button
        JButton backButton = createStyledButton(CommonConstants.backDefaultSim,
                CommonConstants.backHoverSim, CommonConstants.backHoverSim, new Dimension(50, 50));
        backButton.setBounds(width - 120, 41, 50, 50);
        add(backButton);
        backButton.addActionListener(e -> layout.show(mainPanel, "AlgorithmSelection"));

        // Main content panel with red border
        JPanel contentPanel = new JPanel(null);
        contentPanel.setBounds(60, 150, width - 120, 480); // Moved up, increased height
        contentPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel);

        // Order of Sequence - top row
        JLabel orderLabel = new JLabel("Order of Sequence");
        orderLabel.setBounds(25, 20, 150, 25);
        orderLabel.setFont(new Font("Arial", Font.BOLD, 14));
        contentPanel.add(orderLabel);

        orderSequenceField = new JTextField();
        orderSequenceField.setBounds(175, 20, 750, 30);
        orderSequenceField.setEditable(false);
        orderSequenceField.setBackground(new Color(230, 230, 230));
        orderSequenceField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 0));
        contentPanel.add(orderSequenceField);

        // Starting Position - top row, right
        JLabel startPosLabel = new JLabel("Starting Position");
        startPosLabel.setBounds(940, 20, 120, 25);
        startPosLabel.setFont(new Font("Arial", Font.BOLD, 14));
        contentPanel.add(startPosLabel);

        startingPositionField = new JTextField();
        startingPositionField.setBounds(1080, 20, 70, 30);
        startingPositionField.setEditable(false);
        startingPositionField.setBackground(new Color(230, 230, 230));
        startingPositionField.setText(" " + String.valueOf(headStart));
        startingPositionField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 0));
        contentPanel.add(startingPositionField);

        // Create a panel to contain the graph and metrics
        JPanel graphContainerPanel = new JPanel(null);
        graphContainerPanel.setBounds(25, 60, 1130, 400);
        graphContainerPanel.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 0));
        graphContainerPanel.setBackground(Color.WHITE);
        contentPanel.add(graphContainerPanel);

        JPanel metricsPanel = new JPanel(new GridBagLayout()); // GridBagLayout will center components
        metricsPanel.setBounds(0, 0, 1130, 40); // Full width of the container, 40px height
        metricsPanel.setOpaque(false); // Make it transparent

        // Create a panel to hold the Total Seek Time label and field
        JPanel seekTimePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        seekTimePanel.setOpaque(false);
        JLabel seekTimeLabel = new JLabel("Total Seek Time:");
        seekTimeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        seekTimePanel.add(seekTimeLabel);

        totalSeekTimeField = new JTextField(8); // Control width by columns
        totalSeekTimeField.setEditable(false);
        totalSeekTimeField.setBackground(new Color(230, 230, 230));
        totalSeekTimeField.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 0));
        totalSeekTimeField.setHorizontalAlignment(JTextField.CENTER);
        seekTimePanel.add(totalSeekTimeField);

        // Create a panel to hold the Total Head Movements label and field
        JPanel headMovPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        headMovPanel.setOpaque(false);
        JLabel headMovLabel = new JLabel("Total Head Movements:");
        headMovLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headMovPanel.add(headMovLabel);

        totalHeadMovementsField = new JTextField(5); // Control width by columns
        totalHeadMovementsField.setEditable(false);
        totalHeadMovementsField.setBackground(new Color(230, 230, 230));
        totalHeadMovementsField.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 0));
        totalHeadMovementsField.setHorizontalAlignment(JTextField.CENTER);
        headMovPanel.add(totalHeadMovementsField);

        // Add them to the metrics panel with proper spacing
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 0, 20); // Add spacing
        gbc.gridx = 0;
        gbc.gridy = 0;
        metricsPanel.add(seekTimePanel, gbc);

        gbc.gridx = 1;
        metricsPanel.add(headMovPanel, gbc);

        // Add the metrics panel to the graph container
        graphContainerPanel.add(metricsPanel);

        // Graph Panel - inside the container, below the metrics
        graphPanel = new GraphPanel();
        graphPanel.setBounds(35, 45, 1050, 350);
        graphPanel.setBorder(null); // Remove border since the container has one
        graphContainerPanel.add(graphPanel);

        JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        timerPanel.setOpaque(false);
        timerPanel.setBounds(80, 650, 90, 50);

        JLabel timerTitleLabel = new JLabel("Timer");
        timerTitleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        timerPanel.add(timerTitleLabel);

        timerLabel = new JLabel("00:00");
        timerLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        timerPanel.add(timerLabel);

        add(timerPanel);

        // Speed slider - using custom styled slider
        JLabel speedLabel = new JLabel("SPEED");
        speedLabel.setBounds(160, 650, 60, 25);
        add(speedLabel);

        // Create custom slider
        speedSlider = createCustomSlider(0, 100, 50);
        speedSlider.setBounds(230, 650, 300, 30);
        add(speedSlider);

        // Label for minimum value (Slow)
        JLabel slowLabel = new JLabel("Slow");
        slowLabel.setBounds(230, 680, 40, 20);
        add(slowLabel);

        // Label for maximum value (Fast)
        JLabel fastLabel = new JLabel("Fast");
        fastLabel.setBounds(530, 680, 40, 20);
        add(fastLabel);

        startButton = createOblongButton("START", 150, 40);
        startButton.setBounds(580, 650, 150, 40);
        add(startButton);
        startButton.addActionListener(e -> startAnimation());

        restartButton = createOblongButton("RESTART", 150, 40);
        restartButton.setBounds(750, 650, 150, 40);
        add(restartButton);
        restartButton.addActionListener(e -> restartAnimation());

        pauseButton = createOblongButton("PAUSE", 150, 40);
        pauseButton.setBounds(920, 650, 150, 40);
        pauseButton.setEnabled(false); // Initially disabled until animation starts
        add(pauseButton);
        pauseButton.addActionListener(e -> pauseAnimation());

        saveButton = createOblongButton("SAVE", 150, 40);
        saveButton.setBounds(1085, 650, 150, 40);
        saveButton.setEnabled(false); // Initially disabled
        add(saveButton);
        saveButton.addActionListener(e -> showSaveDialog());
    }

    private void showSaveDialog() {
        // Create a dialog to let user choose between PDF and PNG
        String[] options = { "Save as PDF", "Save as PNG", "Cancel" };
        int choice = JOptionPane.showOptionDialog(
                this,
                "Choose export format:",
                "Save Simulation",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == 0) {
            saveAsPDF();
        } else if (choice == 1) {
            saveAsPNG();
        }
        // If choice is 2 (Cancel) or dialog is closed, do nothing
    }

    private String generateTimestampFilename() {
        // Create a date formatter for the pattern "MMddyy_HHmmss"
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyy_HHmmss");

        // Get current date/time and format it
        String timestamp = dateFormat.format(new Date());

        // Return the formatted string with _DS suffix
        return timestamp + "_DS";
    }

    private void saveAsPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save PDF");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF files (*.pdf)", "pdf"));

        // Set default file name
        String baseFilename = generateTimestampFilename();
        fileChooser.setSelectedFile(new File(baseFilename + ".pdf"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // Ensure filename ends with .pdf
            if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
            }

            try {
                // Capture the simulation screen
                BufferedImage screenCapture = captureScreen();

                // Create standard A4 PDF document
                Document document = new Document();
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
                document.open();

                // Convert BufferedImage to bytes for iText
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(screenCapture, "png", baos);
                byte[] imageBytes = baos.toByteArray();

                // Create iText image
                com.itextpdf.text.Image pdfImage = com.itextpdf.text.Image.getInstance(imageBytes);

                // Scale image to fit the page
                pdfImage.scaleToFit(document.getPageSize().getWidth(), document.getPageSize().getHeight());

                // Center the image on the page
                pdfImage.setAlignment(com.itextpdf.text.Image.ALIGN_CENTER);

                // Add image to document
                document.add(pdfImage);
                document.close();
                writer.close();

                JOptionPane.showMessageDialog(this,
                        "Simulation saved as PDF: " + fileToSave.getAbsolutePath(),
                        "Save Successful", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error saving PDF: " + ex.getMessage(),
                        "Save Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void saveAsPNG() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save PNG");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG images (*.png)", "png"));

        // Set default file name
        String baseFilename = generateTimestampFilename();
        fileChooser.setSelectedFile(new File(baseFilename + ".png"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // Ensure filename ends with .png
            if (!fileToSave.getName().toLowerCase().endsWith(".png")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".png");
            }

            try {
                // Capture and save the screen
                BufferedImage screenCapture = captureScreen();
                ImageIO.write(screenCapture, "png", fileToSave);

                JOptionPane.showMessageDialog(this,
                        "Simulation saved as PNG: " + fileToSave.getAbsolutePath(),
                        "Save Successful", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error saving PNG: " + ex.getMessage(),
                        "Save Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private BufferedImage captureScreen() {
        // Get the parent window (the entire application window)
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

        // Create image of the entire screen
        BufferedImage image = new BufferedImage(
                frame.getWidth(),
                frame.getHeight(),
                BufferedImage.TYPE_INT_ARGB);

        // Paint the entire frame onto the image
        frame.paint(image.getGraphics());

        return image;
    }

    private void calculateCLOOK() {
        // Implementation of C-LOOK algorithm (Circular LOOK)
        StringBuilder sequence = new StringBuilder();
        totalSeekTime = 0;
        totalHeadMovements = 0;

        int currentPosition = headStart;

        // Create a list to store the sequence of positions
        List<Integer> positions = new ArrayList<>();
        positions.add(currentPosition); // Add starting position

        // Create a copy of the request queue
        List<Integer> sortedRequests = new ArrayList<>(requestQueue);

        // Sort all requests in ascending order
        Collections.sort(sortedRequests);

        // Find the index where requests are greater than or equal to current position
        int index = 0;
        while (index < sortedRequests.size() && sortedRequests.get(index) < currentPosition) {
            index++;
        }

        // Determine the initial direction (default to moving toward larger cylinder
        // numbers)
        boolean movingTowardsLarger = "right".equalsIgnoreCase(direction) ||
                (!"left".equalsIgnoreCase(direction));

        if (movingTowardsLarger) {
            // Moving towards larger cylinder numbers first

            // First service all requests >= current position
            for (int i = index; i < sortedRequests.size(); i++) {
                int request = sortedRequests.get(i);
                positions.add(request);
                sequence.append(request).append(", ");
                totalSeekTime += Math.abs(currentPosition - request);
                currentPosition = request;
                totalHeadMovements++;
            }

            // If there are requests before the starting position, we need to "jump" to the
            // smallest request
            if (index > 0) {
                // Visualize the jump from the largest request to the smallest
                // In a real system this would be a seek without servicing requests along the
                // way
                int smallestRequest = sortedRequests.get(0);

                // Add the jump to the visualization
                positions.add(smallestRequest);
                sequence.append(smallestRequest).append(", ");
                totalSeekTime += Math.abs(currentPosition - smallestRequest);
                currentPosition = smallestRequest;
                totalHeadMovements++;

                // Service all remaining requests (those < starting position, excluding the
                // smallest which we just did)
                for (int i = 1; i < index; i++) {
                    int request = sortedRequests.get(i);
                    positions.add(request);
                    sequence.append(request).append(", ");
                    totalSeekTime += Math.abs(currentPosition - request);
                    currentPosition = request;
                    totalHeadMovements++;
                }
            }
        } else {
            // Moving towards smaller cylinder numbers first

            // First service all requests < current position (in reverse order)
            for (int i = index - 1; i >= 0; i--) {
                int request = sortedRequests.get(i);
                positions.add(request);
                sequence.append(request).append(", ");
                totalSeekTime += Math.abs(currentPosition - request);
                currentPosition = request;
                totalHeadMovements++;
            }

            // If there are requests after the starting position, we need to "jump" to the
            // largest request
            if (index < sortedRequests.size()) {
                // Visualize the jump from the smallest request to the largest
                int largestRequest = sortedRequests.get(sortedRequests.size() - 1);

                // Add the jump to the visualization
                positions.add(largestRequest);
                sequence.append(largestRequest).append(", ");
                totalSeekTime += Math.abs(currentPosition - largestRequest);
                currentPosition = largestRequest;
                totalHeadMovements++;

                // Service all remaining requests (those >= starting position, excluding the
                // largest which we just did)
                for (int i = sortedRequests.size() - 2; i >= index; i--) {
                    int request = sortedRequests.get(i);
                    positions.add(request);
                    sequence.append(request).append(", ");
                    totalSeekTime += Math.abs(currentPosition - request);
                    currentPosition = request;
                    totalHeadMovements++;
                }
            }
        }

        // Update fields
        if (sequence.length() > 2) {
            sequence.delete(sequence.length() - 2, sequence.length()); // Remove trailing comma
        }
        orderSequenceField.setText(sequence.toString());
        totalSeekTimeField.setText(String.valueOf(totalSeekTime));
        totalHeadMovementsField.setText(String.valueOf(totalHeadMovements));

        // Prepare animation points
        prepareAnimationPoints(positions);

        // Draw the graph
        graphPanel.setPositions(positions);
        graphPanel.repaint();
    }

    private void prepareAnimationPoints(List<Integer> positions) {
        animationPoints.clear();

        int graphWidth = graphPanel.getWidth();
        int graphHeight = graphPanel.getHeight();

        for (int i = 0; i < positions.size(); i++) {
            int x = (positions.get(i) * graphWidth) / 200; // Scale to graph width (assuming 0-199 range)
            int y = (i * graphHeight) / (positions.size() - 1);

            animationPoints.add(new Point(x, y));
        }
    }

    private void startAnimation() {
        // If already animating, don't do anything
        if (isAnimating)
            return;

        // If we're at the end, don't allow starting (force restart)
        if (currentStep >= animationPoints.size() - 1) {
            // Highlight the restart button to guide the user
            restartButton.setEnabled(true);
            startButton.setEnabled(false);
            return;
        }

        // Now we can start/resume the animation from the current step
        isAnimating = true;

        // Start or resume the timer
        if (currentStep == 0) {
            // Reset elapsed time for fresh start
            elapsedTime = 0;
            startTime = System.currentTimeMillis();
        } else {
            // For resuming after pause, capture current time
            startTime = System.currentTimeMillis();
        }

        // Create and start display timer for updating the timer label
        displayTimer = new Timer(100, e -> {
            if (isAnimating) {
                long currentTime = System.currentTimeMillis();
                long newElapsed = elapsedTime + (currentTime - startTime);
                updateTimerDisplay(newElapsed);
            }
        });
        displayTimer.start();

        // Update button states
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        restartButton.setEnabled(true);

        int delay = 505 - (speedSlider.getValue() * 5); // Convert 0-100 to 500-0 (faster at higher values)

        animationTimer = new Timer(delay, e -> {
            if (currentStep < animationPoints.size() - 1) {
                currentStep++;
                graphPanel.setCurrentStep(currentStep);
                graphPanel.repaint();
            } else {
                // Reached the end
                pauseAnimation();

                // Stop and finalize the timer display
                if (displayTimer != null) {
                    displayTimer.stop();
                }

                // Disable start button, keep restart enabled
                startButton.setEnabled(false);
                restartButton.setEnabled(true);
            }
        });

        animationTimer.start();
    }

    // Modified pauseAnimation method to handle the timer
    private void pauseAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        isAnimating = false;

        // Update elapsed time and stop the display timer
        if (displayTimer != null) {
            displayTimer.stop();
            long currentTime = System.currentTimeMillis();
            elapsedTime += (currentTime - startTime);
            updateTimerDisplay(elapsedTime);
        }

        // Update button states
        if (currentStep < animationPoints.size() - 1) {
            startButton.setEnabled(true); // Only enable start if not at the end
        } else {
            startButton.setEnabled(false);
        }
        pauseButton.setEnabled(false);
        saveButton.setEnabled(true);
        restartButton.setEnabled(true);
    }

    // Modified restartAnimation method to handle the timer
    private void restartAnimation() {
        // First stop any ongoing animation
        pauseAnimation();

        // Reset to beginning
        currentStep = 0;
        graphPanel.setCurrentStep(currentStep);
        graphPanel.repaint();

        // Reset timer
        elapsedTime = 0;
        updateTimerDisplay(0);

        // Reset button states
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        restartButton.setEnabled(true);
        saveButton.setEnabled(false);

        // Don't automatically start - user needs to press start
        isAnimating = false;
    }

    // Helper method to format and update the timer display
    private void updateTimerDisplay(long timeInMillis) {
        // Convert milliseconds to minutes and seconds
        long totalSeconds = timeInMillis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        // Format as MM:SS
        String timeString = String.format("%02d:%02d", minutes, seconds);
        timerLabel.setText(timeString);
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    class GraphPanel extends JPanel {
        private List<Integer> positions = new ArrayList<>();
        private int currentStep = 0;
        private final int MAX_DISK_SIZE = 199; // Maximum disk position
        private final int TOP_PADDING = 30; // Increased padding for ruler
        private final int RULER_POSITION = 25; // Position of ruler line from top

        public GraphPanel() {
            setOpaque(true);
            setBackground(Color.WHITE);
        }

        public void setPositions(List<Integer> positions) {
            this.positions = positions;
        }

        public void setCurrentStep(int step) {
            this.currentStep = step;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            // Find min and max positions to determine the scale
            int minPos = Integer.MAX_VALUE;
            int maxPos = Integer.MIN_VALUE;

            for (Integer pos : positions) {
                minPos = Math.min(minPos, pos);
                maxPos = Math.max(maxPos, pos);
            }

            // Ensure we have some padding
            minPos = Math.max(0, minPos - 10);
            maxPos = Math.min(MAX_DISK_SIZE, maxPos + 10);

            // Draw the dynamic scale at the top with increased padding
            g2d.setColor(Color.BLACK);
            g2d.drawLine(0, RULER_POSITION, width, RULER_POSITION);

            // Draw scale with more tick marks for better readability
            int numTicks = 10; // Number of tick marks to display
            int step = (maxPos - minPos) / numTicks;

            // Ensure step is at least 1
            step = Math.max(1, step);

            // Font for ruler labels
            Font rulerFont = new Font("Arial", Font.PLAIN, 11);
            g2d.setFont(rulerFont);

            for (int i = 0; i <= numTicks; i++) {
                int pos = minPos + (i * step);
                if (pos > maxPos)
                    break;

                int x = scaleX(pos, minPos, maxPos, width);
                g2d.drawLine(x, RULER_POSITION - 3, x, RULER_POSITION + 3);

                // Position text above the line with more space
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(String.valueOf(pos));
                g2d.drawString(String.valueOf(pos), x - textWidth / 2, RULER_POSITION - 8);
            }

            // Always draw min, max and important values
            drawTickMark(g2d, 0, minPos, maxPos, width, rulerFont);
            drawTickMark(g2d, MAX_DISK_SIZE, minPos, maxPos, width, rulerFont);

            if (positions.isEmpty())
                return;

            // Scale positions to fit the graph based on the dynamic scale
            int[] xPoints = new int[positions.size()];
            int[] yPoints = new int[positions.size()];

            for (int i = 0; i < positions.size(); i++) {
                xPoints[i] = scaleX(positions.get(i), minPos, maxPos, width);
                // Start drawing paths below the ruler with additional padding
                yPoints[i] = TOP_PADDING + 10 + (i * (height - TOP_PADDING - 30)) / (positions.size() - 1);
            }

            // Draw the path up to current step
            if (currentStep > 0) {
                g2d.setStroke(new BasicStroke(2));
                for (int i = 0; i < Math.min(currentStep, positions.size() - 1); i++) {
                    g2d.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);

                    // Draw arrowhead in the middle of the line
                    int midX = (xPoints[i] + xPoints[i + 1]) / 2;
                    int midY = (yPoints[i] + yPoints[i + 1]) / 2;

                    double angle = Math.atan2(yPoints[i + 1] - yPoints[i],
                            xPoints[i + 1] - xPoints[i]);

                    drawArrow(g2d, midX, midY, angle);
                }
            }

            // Draw circles at each point
            g2d.setColor(Color.BLACK);
            for (int i = 0; i <= Math.min(currentStep, positions.size() - 1); i++) {
                g2d.fill(new Ellipse2D.Double(xPoints[i] - 4, yPoints[i] - 4, 8, 8));
            }
        }

        private int scaleX(int pos, int minPos, int maxPos, int width) {
            // Scale the position to fit within the panel width with horizontal padding
            return (int) (((double) (pos - minPos) / (maxPos - minPos)) * (width - 40)) + 20;
        }

        private void drawTickMark(Graphics2D g2d, int pos, int minPos, int maxPos, int width, Font font) {
            // Only draw if the position is within the visible range
            if (pos >= minPos && pos <= maxPos) {
                int x = scaleX(pos, minPos, maxPos, width);
                g2d.drawLine(x, RULER_POSITION - 3, x, RULER_POSITION + 3);

                // Position text centered above the tick mark
                FontMetrics fm = g2d.getFontMetrics(font);
                int textWidth = fm.stringWidth(String.valueOf(pos));
                g2d.drawString(String.valueOf(pos), x - textWidth / 2, RULER_POSITION - 8);
            }
        }

        // Draw an arrow at a specific point with a given angle
        private void drawArrow(Graphics2D g2d, int x, int y, double angle) {
            int len = 8; // Arrow length

            // Calculate the arrow head points
            int x1 = (int) (x - len * Math.cos(angle - Math.PI / 6));
            int y1 = (int) (y - len * Math.sin(angle - Math.PI / 6));
            int x2 = (int) (x - len * Math.cos(angle + Math.PI / 6));
            int y2 = (int) (y - len * Math.sin(angle + Math.PI / 6));

            // Calculate the tip of the arrow
            int tipX = (int) (x + len * Math.cos(angle));
            int tipY = (int) (y + len * Math.sin(angle));

            // Save the current color
            Color originalColor = g2d.getColor();

            // Set the arrow color to #FF0000 (bright red)
            g2d.setColor(new Color(255, 0, 0));

            // Draw the arrow head
            Path2D.Double path = new Path2D.Double();
            path.moveTo(tipX, tipY);
            path.lineTo(x1, y1);
            path.lineTo(x2, y2);
            path.closePath();

            g2d.fill(path);

            // Restore the original color
            g2d.setColor(originalColor);
        }
    }
}