import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class PointClicker extends JPanel {

    private static final Point[] POINTS = new Point[5];
    private static final double RESISTANCE_23 = 73;
    private static final double RESISTANCE_34 = 200;
    private static final double RESISTANCE_42 = 100;
    private static final double MARGIN_OF_ERROR = 0.1;

    private final ArrayList<Integer> clickOrder = new ArrayList<>();
    private final ArrayList<Line> permanentLines = new ArrayList<>();
    private TemporaryLine temporaryLine = null;
    private int lastClickedIndex = -1;
    private double resistance;
    private final int[] nodeArray = new int[5];
    private Point currentMousePosition = null;

    private JTextField inputFieldR23;
    private JTextField inputFieldR34;
    private JTextField inputFieldR42;
    private JButton checkButtonR23;
    private JButton checkButtonR34;
    private JButton checkButtonR42;
    private final Image backgroundImage = Toolkit.getDefaultToolkit().getImage("C:/Users/Irina/Downloads/images/Background.jpg");
    private final Image image1 = Toolkit.getDefaultToolkit().getImage("C:/Users/Irina/Downloads/images/object (1).png");
    private final Image image2 = Toolkit.getDefaultToolkit().getImage("C:/Users/Irina/Downloads/images/object (9).png");

    private static class Line {
        final Point start;
        final Point end;
        Line(Point start, Point end) {
            this.start = start;
            this.end = end;
        }
    }

    private static class TemporaryLine {
        final Point fixedPoint;
        Point movingPoint;
        TemporaryLine(Point fixedPoint) {
            this.fixedPoint = fixedPoint;
            this.movingPoint = fixedPoint;
        }
    }

    public PointClicker() {
        initializePoints();
        setupUI();
    }

    private void initializePoints() {
        POINTS[0] = new Point(185, 380);
        POINTS[1] = new Point(185, 410);
        POINTS[2] = new Point(402, 327);
        POINTS[3] = new Point(465, 347);
        POINTS[4] = new Point(522, 327);
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        JPanel drawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBackground(g);
                drawPoints(g);
                drawLines(g);
                drawResistance(g);
                drawText(g);
            }
        };

        drawingPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                handleMouseClick(event);
                updateWireCheck();
                drawingPanel.repaint();
            }
        });

        drawingPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent event) {
                currentMousePosition = event.getPoint();
                if (temporaryLine != null) {
                    temporaryLine.movingPoint = currentMousePosition;
                    drawingPanel.repaint();
                }
            }
        });

        add(drawingPanel, BorderLayout.CENTER);
        JPanel controlPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        JButton undoButton = new JButton("Удалить последний провод");
        undoButton.addActionListener(e -> {
            removeLastWire();
            drawingPanel.repaint();
        });
        buttonPanel.add(undoButton);
        controlPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));

        inputPanel.add(new JLabel("Введите значение сопртивления 2-3:"));
        inputFieldR23 = new JTextField(10);
        inputFieldR23.setPreferredSize(new Dimension(100, 25));
        inputPanel.add(inputFieldR23);
        checkButtonR23 = new JButton("Проверить");
        checkButtonR23.addActionListener(e -> checkResistance(RESISTANCE_23, inputFieldR23));
        inputPanel.add(checkButtonR23);

        inputPanel.add(new JLabel("Введите значение сопртивления 3-4:"));
        inputFieldR34 = new JTextField(10);
        inputFieldR34.setPreferredSize(new Dimension(100, 25));
        inputPanel.add(inputFieldR34);
        checkButtonR34 = new JButton("Проверить");
        checkButtonR34.addActionListener(e -> checkResistance(RESISTANCE_34, inputFieldR34));
        inputPanel.add(checkButtonR34);

        inputPanel.add(new JLabel("Введите значение сопртивления 4-2:"));
        inputFieldR42 = new JTextField(10);
        inputFieldR42.setPreferredSize(new Dimension(100, 25));
        inputPanel.add(inputFieldR42);
        checkButtonR42 = new JButton("Проверить");
        checkButtonR42.addActionListener(e -> checkResistance(RESISTANCE_42, inputFieldR42));
        inputPanel.add(checkButtonR42);

        controlPanel.add(inputPanel, BorderLayout.NORTH);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private void removeLastWire() {
        if (!permanentLines.isEmpty()) {
            permanentLines.remove(permanentLines.size() - 1);
            if (clickOrder.size() >= 2) {
                clickOrder.remove(clickOrder.size() - 1);
                clickOrder.remove(clickOrder.size() - 1);
            }
            updateWireCheck();
        }
    }

    private void handleMouseClick(MouseEvent event) {
        for (int i = 0; i < POINTS.length; i++) {
            if (POINTS[i].distance(event.getPoint()) < 10) {
                if (clickOrder.size() % 2 == 0) {
                    temporaryLine = new TemporaryLine(POINTS[i]);
                    clickOrder.add(i + 1);
                    lastClickedIndex = i;
                } else {
                    if (lastClickedIndex != i) {
                        permanentLines.add(new Line(POINTS[lastClickedIndex], POINTS[i]));
                        clickOrder.add(i + 1);
                        temporaryLine = null;
                        lastClickedIndex = i;
                    }
                }
                break;
            }
        }
    }

    private void updateWireCheck() {
        StringBuilder selectedPointIndices = new StringBuilder();
        wireCheck(selectedPointIndices);
    }

    private void drawBackground(Graphics graphics) {
        graphics.drawImage(backgroundImage, 0, 0, this);
        graphics.drawImage(image1, 50, 150, this);
        graphics.drawImage(image2, 365, 216, this);
    }

    private void drawPoints(Graphics graphics) {
        graphics.setColor(Color.RED);
        for (Point point : POINTS) {
            int redCircleRad = 8;
            int blackCircleRad = 5;
            graphics.fillOval(point.x - redCircleRad, point.y - redCircleRad, 2 * redCircleRad, 2 * redCircleRad);
            graphics.setColor(Color.BLACK);
            graphics.fillOval(point.x - blackCircleRad, point.y - blackCircleRad, 2 * blackCircleRad, 2 * blackCircleRad);
            graphics.setColor(Color.RED);
        }
    }

    private void drawLines(Graphics graphics) {
        graphics.setColor(Color.BLACK);

        for (Line line : permanentLines) {
            graphics.drawLine(line.start.x, line.start.y, line.end.x, line.end.y);
        }

        if (temporaryLine != null && currentMousePosition != null) {
            graphics.drawLine(temporaryLine.fixedPoint.x, temporaryLine.fixedPoint.y,
                    currentMousePosition.x, currentMousePosition.y);
        }
    }

    private void drawResistance(Graphics graphics) {
        graphics.setColor(Color.black);
        graphics.drawString("" + Math.round(resistance * 100) / 100, 170, 230);
    }

    private void drawText(Graphics graphics){
        graphics.setColor(Color.darkGray);
        graphics.setFont(new Font("arial", Font.BOLD, 12));
        graphics.drawString("Найдите сопротивления резисторов в черном ящике. Известно, что они соединены «треугольником».",
         580, 50);
        }

    public void wireCheck(StringBuilder selectedPointIndices) {
        buildPointIndexString(selectedPointIndices);
        initializeNodeArray();
        processNodeConnections(selectedPointIndices.toString());
        calculateResistance();
    }

    private void buildPointIndexString(StringBuilder selectedPointIndices) {
        for (int i = 0; i < clickOrder.size(); i++) {
            selectedPointIndices.append(clickOrder.get(i));
        }
    }

    private void initializeNodeArray() {
        for (int j = 0; j < nodeArray.length; j++) {
            nodeArray[j] = j;
        }
    }

    private void processNodeConnections(String result) {
        for (int i = 0; i < result.length(); i += 2) {
            if (i + 1 < result.length()) {
                int first = Integer.parseInt("" + result.charAt(i)) - 1;
                int second = Integer.parseInt("" + result.charAt(i + 1)) - 1;

                int minValue = Math.min(nodeArray[first], nodeArray[second]);
                for (int j = 0; j < nodeArray.length; j++) {
                    if (nodeArray[j] == nodeArray[first] || nodeArray[j] == nodeArray[second]) {
                        nodeArray[j] = minValue;
                    }
                }
                nodeArray[first] = minValue;
                nodeArray[second] = minValue;
            }
        }
    }

    private void calculateResistance() {
        int count0 = countNodesWithValue(0);
        int count1 = countNodesWithValue(1);

        if (count0 < 2 || count1 < 2) {
            resistance = 0;
        } else {
            ArrayList<Integer> uniqueNumbers = getUniqueNodeValues();
            determineResistanceBasedOnNodes(uniqueNumbers);
        }
    }

    private int countNodesWithValue(int value) {
        int count = 0;
        for (int num : nodeArray) {
            if (num == value) {
                count++;
            }
        }
        return count;
    }

    private ArrayList<Integer> getUniqueNodeValues() {
        ArrayList<Integer> uniqueNumbers = new ArrayList<>();
        for (int num : nodeArray) {
            if (!uniqueNumbers.contains(num)) {
                uniqueNumbers.add(num);
            }
        }
        return uniqueNumbers;
    }

    private void determineResistanceBasedOnNodes(ArrayList<Integer> uniqueNumbers) {
        switch (uniqueNumbers.size()) {
            case 1 ->
                resistance = 0;
            case 2 ->
                resistance = calculateParallelResistance();
            case 3 ->
                resistance = calculateComplexResistance(uniqueNumbers);
            default ->
                resistance = 0;
        }
    }

    private double calculateParallelResistance() {
        return 1 / ((1 / RESISTANCE_23) * Math.abs(nodeArray[2] - nodeArray[3])
                + (1 / RESISTANCE_34) * Math.abs(nodeArray[3] - nodeArray[4])
                + (1 / RESISTANCE_42) * Math.abs(nodeArray[4] - nodeArray[2]));
    }

    private double calculateComplexResistance(ArrayList<Integer> uniqueNumbers) {
        for (int num : uniqueNumbers) {
            if (num != 0 && num != 1) {
                return switch (num) {
                    case 2 ->
                        RESISTANCE_34 * (RESISTANCE_23 + RESISTANCE_42)
                        / (RESISTANCE_23 + RESISTANCE_34 + RESISTANCE_42);
                    case 3 ->
                        RESISTANCE_42 * (RESISTANCE_23 + RESISTANCE_34)
                        / (RESISTANCE_23 + RESISTANCE_34 + RESISTANCE_42);
                    case 4 ->
                        RESISTANCE_23 * (RESISTANCE_34 + RESISTANCE_42)
                        / (RESISTANCE_23 + RESISTANCE_34 + RESISTANCE_42);
                    default ->
                        0;
                };
            }
        }
        return 0;
    }

    private void checkResistance(double expectedResistance, JTextField inputField) {
            double inputValue = Double.parseDouble(inputField.getText());
            double lowerBound = expectedResistance * (1 - MARGIN_OF_ERROR);
            double upperBound = expectedResistance * (1 + MARGIN_OF_ERROR);

            if (inputValue >= lowerBound && inputValue <= upperBound) {
                JOptionPane.showMessageDialog(this,
                        "Верно!\nВаш ответ отличается от правильного не более, чем на "
                        + (int) (MARGIN_OF_ERROR * 100) + "%");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Неверно.");
            }
    }

    public static void start() {
        JFrame frame = new JFrame("Задача");
        PointClicker pointClicker = new PointClicker();
        frame.add(pointClicker);
        frame.setSize(1200, 740);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}