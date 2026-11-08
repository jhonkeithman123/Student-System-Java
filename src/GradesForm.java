import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class GradesForm extends JFrame {
    private final String studentId;
    public GradesForm(String studentId) {
        this.studentId = studentId;
        setTitle("Grades");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600,500);
        setLocationRelativeTo(null);
        DefaultTableModel model = new DefaultTableModel(new String[]{"Subject","Grade","Term"},0);
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        JLabel empty = new JLabel("No subjects available");
        empty.setHorizontalAlignment(SwingConstants.CENTER);
        boolean any=false;
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT subject,grade,term FROM grades WHERE student_id=?")) {
            ps.setString(1,studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) { any=true; model.addRow(new Object[]{rs.getString(1),rs.getDouble(2),rs.getString(3)}); }
            if (!any) { getContentPane().removeAll(); add(empty,BorderLayout.CENTER); }
        } catch (SQLException e){ e.printStackTrace(); }
        JButton back = new JButton("Back");
        back.addActionListener(e -> { dispose(); new DashboardFrame().setVisible(true); });
        add(back, BorderLayout.SOUTH);
    }
    public GradesForm(){ this(""); }
}
