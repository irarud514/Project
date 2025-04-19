import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

public class Menu extends JFrame {

    private final JButton startGameButton;
    private final JButton chooseGameButton;
    public JLabel currentLevel;
    public int type = 0;
    public String[] lvlType = {"Оптика", "Электричество", "Механика"};
    private BufferedImage backgroundImage;

    public Menu() {
        try {
            backgroundImage = ImageIO.read(new File("C:/Users/Irina/Downloads/images/BG.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setTitle("Game Menu");
        setSize(1200, 740);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(new BackgroundPanel());
        setLayout(new FlowLayout());

        startGameButton = new JButton(new ImageIcon("C:/Users/Irina/Downloads/images/start.png"));
        chooseGameButton = new JButton(new ImageIcon("C:/Users/Irina/Downloads/images/ChooseGame.png"));
        startGameButton.setBorderPainted(false);
        startGameButton.setFocusPainted(false);
        startGameButton.setContentAreaFilled(false);
        chooseGameButton.setBorderPainted(false);
        chooseGameButton.setFocusPainted(false);
        chooseGameButton.setContentAreaFilled(false);
        startGameButton.addActionListener(new MenuActionListener());
        chooseGameButton.addActionListener(new MenuActionListener());

        add(chooseGameButton);
        add(startGameButton);
    }

    private class BackgroundPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    private class MenuActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton source = (JButton) e.getSource();
            if (source == startGameButton) {
                openProgram(type);
            } else if (source == chooseGameButton) {
                chooseGame();
            }
        }
    }

    private void chooseGame() {
        String selectedLevelType = (String) JOptionPane.showInputDialog(
                this,
                "Выберите уровень:",
                "Настройки",
                JOptionPane.QUESTION_MESSAGE,
                null,
                lvlType,
                lvlType[0]
        );

        if (selectedLevelType != null) {
            for (int i = 0; i < lvlType.length; i++) {
                if (lvlType[i].equals(selectedLevelType)) {
                    type = i;
                    break;
                }
            }
        }
    }

    private void openProgram(int topic) {
        switch (topic) {
            case 0:
                Optica.start();
                break;
            case 1:
                PointClicker.start();
                break;
            case 2:
                Mechanica.start();
                break;
            default:
                JOptionPane.showMessageDialog(this, "Неизвестная тема", "Ошибка", JOptionPane.ERROR_MESSAGE);
                break;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Menu().setVisible(true));
    }

    public int get_Type() {
        return type;
    }
}
