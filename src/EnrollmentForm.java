import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class EnrollmentForm extends JFrame {
    private final String studentId;
    private static final String[] SUBJECTS = {
        "Data Structures and Algorithms","Data Communication and Networking","Computer Programming 3",
        "Social and Professional Issues in Computing","Physical Fitness and Health 3",
        "Integrative Programming 1","Operating Systems"
    };
    public EnrollmentForm(String studentId) {
        this.studentId = studentId;
        setTitle("Enrollment");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500,600);
        setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout());
        setContentPane(root);
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card,BoxLayout.Y_AXIS));
        root.add(new JScrollPane(card),BorderLayout.CENTER);
        JLabel title = new JLabel("Enroll Subjects");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);
        java.util.List<JCheckBox> boxes = new java.util.ArrayList<>();
        for (String s : SUBJECTS) {
            JCheckBox cb = new JCheckBox(s);
            cb.setAlignmentX(Component.LEFT_ALIGNMENT);
            boxes.add(cb);
            card.add(cb);
        }
        // load selected from DB
        if (studentId != null && !studentId.trim().isEmpty()) {
            try (Connection c = DB.getConnection();
                 PreparedStatement ps = c.prepareStatement("SELECT subject FROM enrollments WHERE student_id=?")) {
                ps.setString(1, studentId);
                ResultSet rs = ps.executeQuery();
                java.util.Set<String> sel = new java.util.HashSet<>();
                while (rs.next()) sel.add(rs.getString(1));
                for (JCheckBox cb : boxes) cb.setSelected(sel.contains(cb.getText()));
            } catch (SQLException ex) { ex.printStackTrace(); }
        }
        JButton save = new JButton("Save Enrollment");
        save.addActionListener(e -> {
            try (Connection c = DB.getConnection()) {
                c.createStatement().execute("DELETE FROM enrollments WHERE student_id='"+studentId+"'");
                for (JCheckBox cb : boxes) {
                    if (cb.isSelected()) {
                        PreparedStatement ps = c.prepareStatement("INSERT INTO enrollments(student_id,subject) VALUES(?,?)");
                        ps.setString(1,studentId); ps.setString(2,cb.getText()); ps.executeUpdate(); ps.close();
                    }
                }
                JOptionPane.showMessageDialog(this,"Saved");
            } catch (SQLException ex){ ex.printStackTrace(); }
        });
        card.add(save);
        JButton back = new JButton("Back");
        back.addActionListener(e -> { dispose(); new DashboardFrame().setVisible(true); });
        card.add(back);
    }
    public EnrollmentForm(){ this(""); }
}
