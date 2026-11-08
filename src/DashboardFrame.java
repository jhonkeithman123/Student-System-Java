import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {
    private final String studentId;
    public DashboardFrame() { this(""); }
    public DashboardFrame(String studentId) {
        this.studentId = studentId == null ? "" : studentId;
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
        card.add(Box.createVerticalStrut(20));

        JPanel grid = new JPanel(new GridLayout(3, 3, 10, 10));
        grid.setOpaque(false);
        JButton si = Theme.button("Student Info");
        si.addActionListener(e -> { dispose(); new StudentInformationForm(false, studentId).setVisible(true); });
        JButton edit = Theme.button("Edit Info");
        edit.addActionListener(e -> { dispose(); new StudentInformationForm(true, studentId).setVisible(true); });
        JButton enroll = Theme.button("Enrollment");
        enroll.addActionListener(e -> { dispose(); new EnrollmentForm(studentId).setVisible(true); });
        JButton sched = Theme.button("Schedules");
        sched.addActionListener(e -> { dispose(); new SchedulesForm(studentId).setVisible(true); });
        JButton grades = Theme.button("Grades");
        grades.addActionListener(e -> { dispose(); new GradesForm(studentId).setVisible(true); });
        JButton reset = Theme.button("Reset Password");
        reset.addActionListener(e -> { dispose(); new ResetPasswordForm().setVisible(true); });
        JButton logout = Theme.button("Logout");
        logout.addActionListener(e -> { if (ThemedDialog.confirm("Logout", "Are you sure?")) { dispose(); new LoginForm().setVisible(true); } });
        grid.add(si); grid.add(edit); grid.add(enroll);
        grid.add(sched); grid.add(grades); grid.add(reset);
        grid.add(logout);
        card.add(grid);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DashboardFrame().setVisible(true));
    }
}
