import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SchedulesForm extends JFrame {
    private final String studentId;
    public SchedulesForm(String studentId) {
        this.studentId = studentId;
        setTitle("Schedules");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600,500);
        setLocationRelativeTo(null);
        DefaultTableModel model = new DefaultTableModel(new String[]{"Subject","Day","Time","Room"},0);
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        JLabel empty = new JLabel("No subjects available");
        empty.setHorizontalAlignment(SwingConstants.CENTER);
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT subject,day,time,room FROM schedules WHERE student_id=?")) {
            ps.setString(1,studentId);
            ResultSet rs = ps.executeQuery();
            boolean any=false;
            while (rs.next()) { any=true; model.addRow(new Object[]{rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4)}); }
            if (!any) { getContentPane().removeAll(); add(empty,BorderLayout.CENTER); }
        } catch (SQLException e){ e.printStackTrace(); }
        JButton back = new JButton("Back");
        back.addActionListener(e -> { dispose(); new DashboardFrame().setVisible(true); });
        add(back, BorderLayout.SOUTH);
    }
    public SchedulesForm(){ this(""); }
}
