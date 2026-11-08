import javax.swing.*;
import java.awt.*;

public class Theme {
    public static final Color BG = new Color(0x0f, 0x17, 0x2a);
    public static final Color CARD = new Color(0x1e, 0x29, 0x3b);
    public static final Color CYAN = new Color(0x22, 0xd3, 0xee);
    public static final Color MUTED = new Color(0x94, 0xa3, 0xb8);

    public static JTextField field() {
        JTextField f = new JTextField();
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBackground(new Color(0x0f, 0x17, 0x2a));
        f.setForeground(Color.WHITE);
        f.setCaretColor(CYAN);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x33, 0x41, 0x55)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        return f;
    }

    public static JButton button(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBackground(new Color(0x06, 0xb6, 0xd4));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(260, 44));
        return b;
    }
}
