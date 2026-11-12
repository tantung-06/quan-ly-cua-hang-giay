import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import gui.Login;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new Login().setVisible(true);
        });
    }
}