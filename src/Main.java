import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // force 1.0 scaling to get consistent gap width
        System.setProperty("sun.java2d.uiScale", "1.0");
        SwingUtilities.invokeLater(() -> new UI(9, 9, 10));
    }
}