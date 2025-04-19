
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Mechanica extends JPanel {

    private int ballX = 100, ballY = 100;
    private int glassX = 200, glassY = 200;
    private final int scaleX = 300, scaleY = 500, scaleWidth = 200, scaleHeight = 50;
    private final int glassWidth = 50, glassHeight = 100;
    private final int suspensionX = 400, suspensionY = 80;
    private boolean draggingBall = false, draggingGlass = false, ballSuspended = false;
    private double weight = 0.0, offset = 0.0;
    private final double ballVolume = 23.1;
    private static final double ballDensity = 3.14;
    private final double waterDensity = 1.0;
    private final double ballMass;
    private final double glassMass = 100.0;
    private JButton tareButton;
    private int ballRad = 10;
    private static double MARGIN_OF_ERROR = 0.1;
    private final Image backgroundImage = Toolkit.getDefaultToolkit().getImage(
        "C:/Users/Irina/Downloads/images/Background.jpg");
    private final Image Image = Toolkit.getDefaultToolkit().getImage(
        "C:/Users/Irina/Downloads/images/object (11).png");

    public Mechanica() {
        ballMass = ballDensity * ballVolume;
        setLayout(null);

        tareButton = new JButton("Tare");
        tareButton.setBounds(scaleX + 60, scaleY + 10, 100, 30);
        tareButton.addActionListener(e -> {
            offset = weight;
            repaint();
        });
        add(tareButton);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();
                if (mouseX >= ballX && mouseX <= ballX + 20 && mouseY >= ballY && mouseY <= ballY + 20) {
                    draggingBall = true;
                } else if (mouseX >= glassX && mouseX <= glassX + 50 && mouseY >= glassY && mouseY <= glassY + 100) {
                    draggingGlass = true;
                } else if (Math.abs(mouseX - suspensionX) < 10 && Math.abs(mouseY - suspensionY) < 10) {
                    addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent e2) {
                            if (mouseX >= ballX && mouseX <= ballX + 20 && mouseY >= ballY && mouseY <= ballY + 20) {
                                ballSuspended = true;
                                ballX = suspensionX - 10;
                                ballY = suspensionY + 10;
                                updateWeight();
                                repaint();
                            }
                        }
                    });
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (draggingBall) {
                    draggingBall = false;
                    if (Math.abs(ballX + 10 - suspensionX) < 20 && Math.abs(ballY + 10 - suspensionY) < 20) {
                        ballSuspended = true;
                    }
                    updateWeight();
                }
                if (draggingGlass) {
                    draggingGlass = false;
                    updateWeight();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();
                if (draggingBall) {
                    ballX = mouseX - ballRad;
                    ballY = mouseY - ballRad;
                    if (ballSuspended) {
                        ballX = suspensionX - 10;
                    }
                    if (ballY + 20 > scaleY) {
                        ballY = scaleY - 20;
                    }
                    updateWeight();
                    repaint();
                }
                if (draggingGlass) {
                    glassX = mouseX - 25;
                    glassY = mouseY - 50;
                    if (glassY + 100 > scaleY) {
                        glassY = scaleY - 100;
                    }
                    updateWeight();
                    repaint();
                }
            }
        });
    }

    private void drawBackground(Graphics graphics) {
        graphics.drawImage(backgroundImage, 0, 0, this);
        graphics.drawImage(Image, 210, -50, this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBackground(g);
        g.setColor(Color.GRAY);
        g.fillRect(scaleX, scaleY, scaleWidth, scaleHeight);
        g.setColor(Color.BLACK);
        g.drawString(String.format("%.1f", weight - offset) + " г", scaleX + 10, scaleY + 30);
        g.setColor(Color.RED);
        g.fillOval(ballX, ballY, 20, 20);
        g.setColor(new Color(50, 55, 60, 100));
        g.fillRect(glassX, glassY, glassWidth, glassHeight);
        g.setColor(new Color(0, 0, 255, 100));
        g.fillRect(glassX, glassY + 20, glassWidth, glassHeight - 20);
        if (ballSuspended) {
            g.drawLine(suspensionX, suspensionY, ballX + 10, ballY + 10);
        }
        g.setColor(Color.darkGray);
        g.setFont(new Font("arial", Font.BOLD, 12));
        g.drawString("Найдите плотность выданного шарика при помощи электронных весов, стакана с водой", 580, 50);
    }

    private void updateWeight() {
        weight = 0.0;
        if (!ballSuspended && ballX - 2 * ballRad >= scaleX && ballX <= scaleX + scaleWidth
                && ballY + 2 * ballRad >= scaleY && ballY <= scaleY + scaleHeight) {
            weight += ballMass;
        }
        if (glassX + 50 > scaleX && glassX < scaleX + scaleWidth
                && glassY + 100 >= scaleY && glassY <= scaleY + scaleHeight) {
            weight += glassMass;
        }
        if (ballSuspended && glassX + 50 >= scaleX && glassX <= scaleX + scaleWidth
                && glassY + 100 >= scaleY && glassY <= scaleY + scaleHeight
                && ballX + 20 >= glassX && ballX <= glassX + 50
                && ballY + 20 >= glassY && ballY <= glassY + 100) {
            double displacedWaterMass = waterDensity * ballVolume;
            weight += displacedWaterMass;
        }
    }

    static void start() {
        JFrame frame = new JFrame("Задача");
        Mechanica panel = new Mechanica();
        frame.add(panel);
        JPanel southPanel = new JPanel();
        JTextField inputField = new JTextField(10);
        JButton checkButton = new JButton("Проверить");
        southPanel.add(inputField);
        southPanel.add(checkButton);
        frame.add(southPanel, BorderLayout.SOUTH);
        checkButton.addActionListener((ActionEvent e) -> {
            double inputValue = Double.parseDouble(inputField.getText());
            if (inputValue < ballDensity * (1 + MARGIN_OF_ERROR)
                    && inputValue > ballDensity * (1 - MARGIN_OF_ERROR)) {
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
