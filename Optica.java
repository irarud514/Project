import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import javax.swing.*;

public class Optica extends JPanel {

    double y = 200;
    double prismX = 200;
    double prismY = 100;
    double prismWidth = 60;
    double prismHeight = 200;
    double startX = 100;
    double startY = 100;
    double refractiveIndexOfAir = 1.0;
    public static final double REFRACTIVE_INDEX_OF_GLASS = 1.5;
    public static final double MARGIN_OF_ERROR = 0.1;

    

    public Optica() {
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                startX = (int)e.getX()/10*10;
                startY = (int)e.getY()/10*10;
                if (startX < 0) {
                    startX = 0;
                }
                if (startX > prismX) {
                    startX = prismX;
                }
                if (startX > getWidth()) {
                    startX = getWidth();
                }
                if (startY < 0) {
                    startY = 0;
                }
                if (startY > getHeight()) {
                    startY = getHeight();
                }
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        drawPrism(g2d);
        drawGrid(g2d);
        drawAxes(g);
        drawRays(g2d);
        drawText(g);
    }

    private void drawText(Graphics graphics) {
        graphics.setColor(Color.darkGray);
        graphics.setFont(new Font("arial", Font.BOLD, 12));
        graphics.drawString("Найдите показатель преломления стела, из которого изготовлена призма.",
                580, 50);
    }

    private void drawPrism(Graphics2D g2d) {
        g2d.setColor(Color.GRAY);
        g2d.fillRect((int) prismX, (int) prismY, (int) prismWidth, (int) prismHeight);
    }

    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(Color.lightGray);
        drawVerticalGridLines(g2d);
        drawHorizontalGridLines(g2d);
    }

    private void drawVerticalGridLines(Graphics2D g2d) {
        for (int i = ((int) startX) / 10 * 10; i <= 460 - ((int) startX) / 10 * 10; i += 10) {
            Line2D gridX = new Line2D.Double(i, Math.min((int) startY / 10 * 10, 90),
                    i, Math.max(400 - (int) startY / 10 * 10,
                            Math.max((int) startY / 10 * 10, 310)));
            g2d.draw(gridX);
        }
    }

    private void drawHorizontalGridLines(Graphics2D g2d) {
        for (int i = Math.min(((int) startY) / 10 * 10, 90);
                i <= Math.max(400 - (int) startY / 10 * 10,
                        Math.max((int) startY / 10 * 10, 310)); i += 10) {
            Line2D gridY = new Line2D.Double((int) startX / 10 * 10, i,
                    460 - (int) startX / 10 * 10, i);
            g2d.draw(gridY);
        }
    }

    private void drawAxes(Graphics g) {
        g.setColor(Color.BLACK);
        int endX = 460 - ((int) startX) / 10 * 10;
        int yShift = Math.max(400 - ((int) startY) / 10 * 10,
                Math.max(((int) startY) / 10 * 10, 310));
        drawXAxis(g, endX, yShift);
        drawYAxis(g, endX, yShift);
    }

    private void drawXAxis(Graphics g, int endX, int yShift) {
        g.drawLine((int) startX, yShift, endX, yShift);
        g.drawLine(endX, yShift, endX - 10, yShift - 5);
        g.drawLine(endX, yShift, endX - 10, yShift + 5);
        g.drawString("x, см", (int) endX + 5, yShift - 5);
        for (int i = 0; i <= (endX - (int) startX) / 50; i++) {
            int x = ((int) startX + i * 50) / 10 * 10;
            g.drawLine(x, yShift - 5, x, yShift + 5);
            g.drawString(String.valueOf(i), x - 5, yShift + 15);
        }
    }

    private void drawYAxis(Graphics g, int endX, int yy) {
        int endY = Math.min(((int) startY) / 10 * 10, 90);
        g.drawLine((int) startX / 10 * 10, yy, (int) startX / 10 * 10, endY);
        g.drawLine((int) startX / 10 * 10, endY, (int) startX / 10 * 10 - 5, endY + 10);
        g.drawLine((int) startX / 10 * 10, endY, (int) startX / 10 * 10 + 5, endY + 10);
        g.drawString("y, см", (int) startX / 10 * 10 - 5, endY - 5);
        for (int i = 1; i <= (-endY + yy) / 50; i++) {
            int y1 = (yy - i * 50) / 10 * 10;
            g.drawLine((int) startX / 10 * 10 - 5, y1, (int) startX / 10 * 10 + 5, y1);
            g.drawString(String.valueOf(i), (int) startX / 10 * 10 - 15, y1 + 5);
        }
    }

    private void drawRays(Graphics2D g2d) {
        g2d.setColor(Color.RED);
        Line2D initialRay = new Line2D.Double(startX, startY, prismX, y);
        g2d.draw(initialRay);
        double startRayAngle = Math.atan((prismX - startX) / (startY - y));
        double refractedRayAngle = Math.asin(refractiveIndexOfAir / REFRACTIVE_INDEX_OF_GLASS * Math.cos(startRayAngle));
        double deltaY = calculateDeltaY(refractedRayAngle);
        Line2D refractedRay = new Line2D.Double(prismX, y, prismX + prismWidth, y + deltaY);
        g2d.draw(refractedRay);
        double deltaX2 = calculateDeltaX(startRayAngle);
        double deltaY2 = calculateDeltaY2(startRayAngle);
        Line2D exitRay = new Line2D.Double(prismX + prismWidth, y + deltaY,
                prismX + prismWidth + deltaX2,
                y + deltaY + deltaY2);
        g2d.draw(exitRay);
    }

    private double calculateDeltaY(double refractedRayAngle) {
        double deltaY = -prismWidth * Math.tan(refractedRayAngle);
        if (startY <= y) {
            deltaY = prismWidth * Math.tan(refractedRayAngle);
        }
        return deltaY;
    }

    private double calculateDeltaX(double startRayAngle) {
        double deltaX2 = prismWidth * Math.sin(startRayAngle);
        if (startY <= y) {
            deltaX2 = -prismWidth * Math.sin(startRayAngle);
        }
        return deltaX2;
    }

    private double calculateDeltaY2(double startRayAngle) {
        double deltaY2 = prismWidth * Math.cos(startRayAngle);
        if (startY >= y) {
            deltaY2 = -prismWidth * Math.cos(startRayAngle);
        }
        return deltaY2;
    }

    public static void start() {
        JFrame frame = new JFrame("Задача");
        Optica panel = new Optica();
        frame.add(panel, BorderLayout.CENTER);
        JPanel southPanel = new JPanel();
        JTextField inputField = new JTextField(10);
        JButton checkButton = new JButton("Проверить");
        southPanel.add(inputField);
        southPanel.add(checkButton);
        frame.add(southPanel, BorderLayout.SOUTH);
        checkButton.addActionListener((ActionEvent e) -> {
            double inputValue = Double.parseDouble(inputField.getText());
            if (inputValue < REFRACTIVE_INDEX_OF_GLASS * (1 + MARGIN_OF_ERROR)
                    && inputValue > REFRACTIVE_INDEX_OF_GLASS * (1 - MARGIN_OF_ERROR)) {
                JOptionPane.showMessageDialog(frame, """
                            Верно!
                            Ваш ответ отличается от правильного не более, чем на """ + (int) (MARGIN_OF_ERROR * 100) + "%");
            } else {
                JOptionPane.showMessageDialog(frame, "Неверно.");
            }
        });
        frame.setSize(1200, 740);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
