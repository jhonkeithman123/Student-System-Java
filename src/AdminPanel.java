import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminPanel extends JFrame {
    private final JTextField searchField = new JTextField(20);
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"ID","Name","Email","Role"}, 0);
    private final JTable table = new JTable(model);

    public AdminPanel() {
        setTitle("Admin Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout(8,8));
        setContentPane(root);

        JPanel top = new JPanel();
        top.add(new JLabel("Search by ID:"));
        top.add(searchField);
        JButton searchBtn = new JButton("Search");
        top.add(searchBtn);
        root.add(top, BorderLayout.NORTH);

        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btns = new JPanel();
        JButton add = new JButton("Add");
        JButton update = new JButton("Update");
        JButton delete = new JButton("Delete");
        JButton refresh = new JButton("Refresh");
        btns.add(add); btns.add(update); btns.add(delete); btns.add(refresh);
        root.add(btns, BorderLayout.SOUTH);

        searchBtn.addActionListener(e -> search());
        refresh.addActionListener(e -> loadAll());
        delete.addActionListener(e -> deleteSelected());
        add.addActionListener(e -> JOptionPane.showMessageDialog(this, "Add via Signup or extend"));
        update.addActionListener(e -> JOptionPane.showMessageDialog(this, "Update not yet wired"));

        loadAll();
    }

    private void loadAll() {
        model.setRowCount(0);
        try (Connection c = DB.getConnection(); Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT id,name,email,role FROM users")) {
            while (rs.next()) model.addRow(new Object[]{rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4)});
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void search() {
        String id = searchField.getText().trim();
        model.setRowCount(0);
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id,name,email,role FROM users WHERE id=?")) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) model.addRow(new Object[]{rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4)});
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        String id = (String) model.getValueAt(row, 0);
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM users WHERE id=?")) {
            ps.setString(1, id);
            ps.executeUpdate();
            loadAll();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
