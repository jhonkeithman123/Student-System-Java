import javax.swing.*;
import java.awt.*;

public class ResetPasswordForm extends JFrame {
    public ResetPasswordForm() {
        setTitle("Reset Password");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(420, 420);
        setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        setContentPane(root);

        JPanel card = new JPanel();
        card.setBackground(Theme.CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        root.add(card, BorderLayout.CENTER);

        JLabel title = new JLabel("Reset Password");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Theme.CYAN);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(20));

        JPasswordField cur = new JPasswordField();
        JPasswordField nw = new JPasswordField();
        JPasswordField conf = new JPasswordField();
        for (JPasswordField f : new JPasswordField[]{cur,nw,conf}) {
            f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            f.setAlignmentX(Component.CENTER_ALIGNMENT);
            f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            f.setBackground(new Color(0x0f,0x17,0x2a));
            f.setForeground(Color.WHITE);
            f.setCaretColor(Theme.CYAN);
            f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0x33,0x41,0x55)),
                    BorderFactory.createEmptyBorder(8,12,8,12)));
        }

        JLabel l1 = new JLabel("Current Password"); l1.setForeground(Theme.MUTED); l1.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(l1); card.add(Box.createVerticalStrut(4)); card.add(cur); card.add(Box.createVerticalStrut(12));
        JLabel l2 = new JLabel("New Password"); l2.setForeground(Theme.MUTED); l2.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(l2); card.add(Box.createVerticalStrut(4)); card.add(nw); card.add(Box.createVerticalStrut(12));
        JLabel l3 = new JLabel("Confirm Password"); l3.setForeground(Theme.MUTED); l3.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(l3); card.add(Box.createVerticalStrut(4)); card.add(conf); card.add(Box.createVerticalStrut(20));

        JButton btn = Theme.button("Reset");
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(e -> {
            if (!new String(nw.getPassword()).equals(new String(conf.getPassword()))) {
                JOptionPane.showMessageDialog(this, "Passwords do not match");
                return;
            }
            JOptionPane.showMessageDialog(this, "Password reset (UI only)");
        });
        card.add(btn);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ResetPasswordForm().setVisible(true));
    }
}
