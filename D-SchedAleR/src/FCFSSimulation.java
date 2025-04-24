import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

public class FCFSSimulation extends JPanel {
    private JPanel mainPanel;
    private CardLayout layout;
    private Main main;
    private Image backgroundImage;
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
    private List<Point> animationPoints = new ArrayList<>();

    public FCFSSimulation(Main main, CardLayout layout, JPanel mainPanel, int width, int height, 
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
        backgroundImage = CommonConstants.loadImage(CommonConstants.simulation_screen_FCFS);

        createUI();
        calculateFCFS();
    }

    private void createUI() {
        // Title panel at the top
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBounds(0, 0, width, 50);
        titlePanel.setOpaque(false);
        
        
        JLabel titleLabel = new JLabel("Disk SCHEDuling ALgorithms SimulatoR");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        add(titlePanel);

        // Algorithm name panel (top right)
        JPanel algoPanel = new JPanel(new BorderLayout());
        algoPanel.setBounds(width - 400, 0, 400, 50);
        algoPanel.setOpaque(false);
        
        JLabel algoLabel = new JLabel("First Come, First Serve (FCFS)");
        algoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        algoLabel.setHorizontalAlignment(JLabel.CENTER);
        algoPanel.add(algoLabel, BorderLayout.CENTER);
        
        add(algoPanel);

        // Back button
        JButton backButton = createStyledButton(CommonConstants.backDefault,
                CommonConstants.backHover, CommonConstants.backClick, new Dimension(50, 50));
        backButton.setBounds(width - 120, 0, 50, 50);
        add(backButton);
        backButton.addActionListener(e -> layout.show(mainPanel, "AlgorithmSelection"));

    

        // Main content panel with red border
        JPanel contentPanel = new JPanel(null);
        contentPanel.setBounds(60, 60, width - 120, 480);
        contentPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        add(contentPanel);

        // Order of Sequence
        JLabel orderLabel = new JLabel("Order of Sequence");
        orderLabel.setBounds(20, 20, 150, 25);
        contentPanel.add(orderLabel);

        orderSequenceField = new JTextField();
        orderSequenceField.setBounds(170, 20, 550, 25);
        orderSequenceField.setEditable(false);
        contentPanel.add(orderSequenceField);

        // Starting Position
        JLabel startPosLabel = new JLabel("Starting Position");
        startPosLabel.setBounds(740, 20, 120, 25);
        contentPanel.add(startPosLabel);

        startingPositionField = new JTextField();
        startingPositionField.setBounds(870, 20, 70, 25);
        startingPositionField.setEditable(false);
        startingPositionField.setText(String.valueOf(headStart));
        contentPanel.add(startingPositionField);

        // Total Seek Time
        JLabel seekTimeLabel = new JLabel("Total Seek Time:");
        seekTimeLabel.setBounds(20, 70, 150, 25);
        contentPanel.add(seekTimeLabel);

        totalSeekTimeField = new JTextField();
        totalSeekTimeField.setBounds(20, 100, 250, 25);
        totalSeekTimeField.setEditable(false);
        contentPanel.add(totalSeekTimeField);

        // Total Head Movements
        JLabel headMovLabel = new JLabel("Total Head Movements:");
        headMovLabel.setBounds(20, 150, 180, 25);
        contentPanel.add(headMovLabel);

        totalHeadMovementsField = new JTextField();
        totalHeadMovementsField.setBounds(20, 180, 250, 25);
        totalHeadMovementsField.setEditable(false);
        contentPanel.add(totalHeadMovementsField);

        // Graph Panel
        graphPanel = new GraphPanel();
        graphPanel.setBounds(320, 60, 600, 400);
        graphPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        contentPanel.add(graphPanel);

        // Speed slider
        JLabel speedLabel = new JLabel("SPEED");
        speedLabel.setBounds(100, 560, 60, 25);
        add(speedLabel);

        speedSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        speedSlider.setBounds(190, 560, 250, 50);
        speedSlider.setMajorTickSpacing(50);
        speedSlider.setPaintTicks(true);
        add(speedSlider);

        JLabel slowLabel = new JLabel("Slow");
        slowLabel.setBounds(180, 590, 40, 20);
        add(slowLabel);

        JLabel fastLabel = new JLabel("Fast");
        fastLabel.setBounds(440, 590, 40, 20);
        add(fastLabel);

        // Control buttons
        JButton startButton = new JButton("START");
        startButton.setBounds(520, 560, 150, 40);
        add(startButton);
        startButton.addActionListener(e -> startAnimation());

        JButton restartButton = new JButton("RESTART");
        restartButton.setBounds(690, 560, 150, 40);
        add(restartButton);
        restartButton.addActionListener(e -> restartAnimation());

        JButton pauseButton = new JButton("PAUSE");
        pauseButton.setBounds(860, 560, 150, 40);
        add(pauseButton);
        pauseButton.addActionListener(e -> pauseAnimation());
    }

    private void calculateFCFS() {
        StringBuilder sequence = new StringBuilder();
        totalSeekTime = 0;
        totalHeadMovements = requestQueue.size();
        
        int currentPosition = headStart;
        
        // Create a list of positions in FCFS order
        List<Integer> positions = new ArrayList<>();
        positions.add(currentPosition); // Add starting position
        
        for (Integer request : requestQueue) {
            positions.add(request);
            sequence.append(request).append(", ");
            totalSeekTime += Math.abs(currentPosition - request);
            currentPosition = request;
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
        
        // Scale positions to fit the graph
        int graphWidth = graphPanel.getWidth();
        int graphHeight = graphPanel.getHeight();
        
        for (int i = 0; i < positions.size(); i++) {
            int x = (positions.get(i) * graphWidth) / 200; // Scale to graph width (assuming 0-199 range)
            int y = (i * graphHeight) / (positions.size() - 1); // Distribute evenly vertically
            
            animationPoints.add(new Point(x, y));
        }
    }
    
    private void startAnimation() {
        if (isAnimating) return;
        
        currentStep = 0;
        isAnimating = true;
        
        int delay = 505 - (speedSlider.getValue() * 5); // Convert 0-100 to 500-0 (faster at higher values)
        
        animationTimer = new Timer(delay, e -> {
            if (currentStep < animationPoints.size() - 1) {
                currentStep++;
                graphPanel.setCurrentStep(currentStep);
                graphPanel.repaint();
            } else {
                pauseAnimation();
            }
        });
        
        animationTimer.start();
    }
    
    private void pauseAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        isAnimating = false;
    }
    
    private void restartAnimation() {
        pauseAnimation();
        currentStep = 0;
        graphPanel.setCurrentStep(currentStep);
        graphPanel.repaint();
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    // Inner class for drawing the graph
    class GraphPanel extends JPanel {
        private List<Integer> positions = new ArrayList<>();
        private int currentStep = 0;

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

            // Draw the scale at the top
            g2d.setColor(Color.BLACK);
            g2d.drawLine(0, 20, width, 20);
            
            // Draw scale marks and labels
            g2d.drawString("0", 10, 15);
            g2d.drawLine(10, 17, 10, 23);
            
            g2d.drawString("50", width/4, 15);
            g2d.drawLine(width/4, 17, width/4, 23);
            
            g2d.drawString("199", width-30, 15);
            g2d.drawLine(width-20, 17, width-20, 23);

            if (positions.isEmpty()) return;

            // Scale positions to fit the graph
            int[] xPoints = new int[positions.size()];
            int[] yPoints = new int[positions.size()];

            for (int i = 0; i < positions.size(); i++) {
                xPoints[i] = (positions.get(i) * width) / 200; // Scale to graph width (assuming 0-199 range)
                yPoints[i] = 30 + (i * (height - 50)) / (positions.size() - 1); // Distribute evenly vertically
            }

            // Draw the path up to current step
            if (currentStep > 0) {
                g2d.setStroke(new BasicStroke(2));
                for (int i = 0; i < Math.min(currentStep, positions.size() - 1); i++) {
                    g2d.drawLine(xPoints[i], yPoints[i], xPoints[i+1], yPoints[i+1]);
                    
                    // Draw arrowhead
                    drawArrow(g2d, xPoints[i], yPoints[i], xPoints[i+1], yPoints[i+1]);
                }
            }

            // Draw circles at each point
            g2d.setColor(Color.BLACK);
            for (int i = 0; i <= Math.min(currentStep, positions.size() - 1); i++) {
                g2d.fill(new Ellipse2D.Double(xPoints[i] - 4, yPoints[i] - 4, 8, 8));
            }
        }
        
        private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2) {
            double dx = x2 - x1;
            double dy = y2 - y1;
            double angle = Math.atan2(dy, dx);
            int len = 10; // Arrow length
            
            // Calculate the arrow head points
            int x3 = (int)(x2 - len * Math.cos(angle - Math.PI/6));
            int y3 = (int)(y2 - len * Math.sin(angle - Math.PI/6));
            int x4 = (int)(x2 - len * Math.cos(angle + Math.PI/6));
            int y4 = (int)(y2 - len * Math.sin(angle + Math.PI/6));
            
            // Draw the arrow head
            Path2D.Double path = new Path2D.Double();
            path.moveTo(x2, y2);
            path.lineTo(x3, y3);
            path.lineTo(x4, y4);
            path.closePath();
            
            g2d.fill(path);
        }
    }
}