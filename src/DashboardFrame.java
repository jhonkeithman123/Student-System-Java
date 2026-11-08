import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {
    public DashboardFrame() {
        setTitle("Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 700);
        setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        setContentPane(root);

        JPanel card = new JPanel();
        card.setBackground(Theme.CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        root.add(card, BorderLayout.CENTER);

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Theme.CYAN);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(8));
        JLabel sub = new JLabel("Student Information System");
        sub.setForeground(Theme.MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(sub);
        card.add(Box.createVerticalStrut(40));

        JButton si = Theme.button("Student Information");
        si.setAlignmentX(Component.CENTER_ALIGNMENT);
        si.addActionListener(e -> new StudentInformationForm().setVisible(true));
        card.add(si);
        card.add(Box.createVerticalStrut(16));

        JButton edit = Theme.button("Edit Student Information");
        edit.setAlignmentX(Component.CENTER_ALIGNMENT);
        edit.addActionListener(e -> new StudentInformationForm(true).setVisible(true));
        card.add(edit);
        card.add(Box.createVerticalStrut(16));

        JButton reset = Theme.button("Reset Password");
        reset.setAlignmentX(Component.CENTER_ALIGNMENT);
        reset.addActionListener(e -> new ResetPasswordForm().setVisible(true));
        card.add(reset);
        card.add(Box.createVerticalStrut(16));
        JButton logout = Theme.button("Logout");
        logout.setAlignmentX(Component.CENTER_ALIGNMENT);
        logout.addActionListener(e -> { dispose(); new LoginForm().setVisible(true); });
        card.add(logout);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DashboardFrame().setVisible(true));
    }
}
