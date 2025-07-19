// Entry point for the application
public class App {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new ui.MainFrame().setVisible(true);
        });
    }
}
