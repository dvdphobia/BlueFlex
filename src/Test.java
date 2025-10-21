import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class Test {
    private final JLabel positionLabel;

    public Test(JFrame frame) {
        positionLabel = new JLabel("X: 0, Y: 0");
        positionLabel.setForeground(Color.WHITE);
        positionLabel.setOpaque(true);
        positionLabel.setBackground(new Color(0, 0, 0, 120)); // semi-transparent black
        positionLabel.setFont(new Font("Monospaced", Font.BOLD, 12));

        // Use absolute positioning
        frame.setLayout(null);
        frame.add(positionLabel);
        positionLabel.setBounds(frame.getWidth() - 100, 10, 90, 20);

        // Adjust label position on resize
        frame.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                positionLabel.setBounds(frame.getWidth() - 100, 10, 90, 20);
            }
        });

        // Track mouse movement anywhere inside the frame
        frame.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                updatePosition(frame, e.getX(), e.getY());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                updatePosition(frame, e.getX(), e.getY());
            }
        });
    }

    private void updatePosition(JFrame frame, int x, int y) {
        positionLabel.setText("X: " + x + ", Y: " + y);
        // Keep it at top-right
        positionLabel.setBounds(frame.getWidth() - 100, 10, 90, 20);
    }

    // Static helper to easily attach to any frame
    public static void attach(JFrame frame) {
        new Test(frame);
    }

    // Test demo
    public static void main(String[] args) {
        JFrame frame = new JFrame("UI Debug Tool");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        Test.attach(frame);
    }
}
