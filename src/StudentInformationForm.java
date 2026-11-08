import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StudentInformationForm extends JFrame {
    private final boolean editable;
    private final String studentId;
    public StudentInformationForm() { this(false, ""); }
    public StudentInformationForm(boolean editable) { this(editable, ""); }
    public StudentInformationForm(boolean editable, String studentId) { this(editable, studentId, "", ""); }
    public StudentInformationForm(boolean editable, String fullName, String email) { this(editable, "", fullName, email); }
    public StudentInformationForm(boolean editable, String studentId, String fullName, String email) {
        this.editable = editable;
        this.studentId = studentId;
        setTitle(editable ? "Student Information (Edit)" : "Student Information");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(500, 500));
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        setContentPane(root);

        JPanel card = new JPanel();
        card.setBackground(Theme.CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JScrollPane scroll = new JScrollPane(card);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.getHorizontalScrollBar().setUnitIncrement(20);
        scroll.getVerticalScrollBar().setUI(new ThinScrollBarUI2());
        scroll.getHorizontalScrollBar().setUI(new ThinScrollBarUI2());
        root.add(scroll, BorderLayout.CENTER);

        JLabel title = new JLabel("Student Information");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Theme.CYAN);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(16));

        // name row linearly grouped
        JPanel nameRow = new JPanel(new GridLayout(2, 2, 8, 8));
        nameRow.setOpaque(false);
        nameRow.setMaximumSize(new Dimension(640, 120));
        JTextField lastName = Theme.field();
        JTextField firstName = Theme.field();
        JTextField middleName = Theme.field();
        JTextField nameExt = Theme.field();
        nameRow.add(wrap("Last Name", lastName));
        nameRow.add(wrap("First Name", firstName));
        nameRow.add(wrap("Middle Name", middleName));
        nameRow.add(wrap("Name Ext (opt)", nameExt));
        card.add(nameRow);
        card.add(Box.createVerticalStrut(12));

        // grid fields
        JPanel grid = new JPanel(new GridLayout(0, 2, 8, 8));
        grid.setOpaque(false);
        JTextField id = Theme.field();
        id.setText(studentId == null ? "" : studentId);
        JTextField course = Theme.field();
        JComboBox<String> year = new JComboBox<>(new String[]{"1","2","3","4"});
        JTextField emailField = Theme.field();
        emailField.setText(email == null ? "" : email);
        JTextField contact = Theme.field();
        grid.add(wrap("Student ID", id));
        grid.add(wrap("Course", course));
        grid.add(wrap("Year Level", year));
        grid.add(wrap("Email", emailField));
        grid.add(wrap("Contact", contact));
        card.add(grid);
        card.add(Box.createVerticalStrut(16));

        // bottom table
        DefaultTableModel model = new DefaultTableModel(new String[]{"Field","Value"},0);
        JTable table = new JTable(model);
        card.add(new JScrollPane(table));
        card.add(Box.createVerticalStrut(12));

        JPanel btns = new JPanel(new FlowLayout());
        btns.setOpaque(false);
        JButton save = Theme.button("Save");
        JButton clear = Theme.button("Clear");
        JButton back = Theme.button("Back");
        if (editable) btns.add(save);
        btns.add(clear); btns.add(back);
        card.add(btns);

        for (JTextField f : new JTextField[]{id,lastName,firstName,middleName,nameExt,course,emailField,contact}) f.setEditable(editable);
        year.setEnabled(editable);

        // load from DB
        if (studentId != null && !studentId.trim().isEmpty()) {
            try (java.sql.Connection c = DB.getConnection();
                 java.sql.PreparedStatement ps = c.prepareStatement("SELECT name,course,year,email,contact FROM students WHERE student_id=?")) {
                ps.setString(1, studentId.trim());
                java.sql.ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String nm = rs.getString("name");
                    if (nm != null) { /* leave name fields as-is or could split */ }
                    course.setText(rs.getString("course")==null?"":rs.getString("course"));
                    String y = rs.getString("year");
                    if (y != null) year.setSelectedItem(y);
                    emailField.setText(rs.getString("email")==null?"":rs.getString("email"));
                    contact.setText(rs.getString("contact")==null?"":rs.getString("contact"));
                }
            } catch (java.sql.SQLException ex) { ex.printStackTrace(); }
        }

        save.addActionListener(e -> {
            String full = (lastName.getText()+" "+firstName.getText()+" "+middleName.getText()+" "+nameExt.getText()).trim();
            try (java.sql.Connection c = DB.getConnection();
                 java.sql.PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO students(student_id,name,course,year,email,contact) VALUES(?,?,?,?,?,?) " +
                     "ON CONFLICT(student_id) DO UPDATE SET name=excluded.name,course=excluded.course,year=excluded.year,email=excluded.email,contact=excluded.contact")) {
                ps.setString(1, id.getText().trim());
                ps.setString(2, full);
                ps.setString(3, course.getText().trim());
                ps.setString(4, year.getSelectedItem().toString());
                ps.setString(5, emailField.getText().trim());
                ps.setString(6, contact.getText().trim());
                ps.executeUpdate();
                // update table
                model.setRowCount(0);
                model.addRow(new Object[]{"Student ID", id.getText().trim()});
                model.addRow(new Object[]{"Name", full});
                model.addRow(new Object[]{"Course", course.getText().trim()});
                model.addRow(new Object[]{"Year", year.getSelectedItem().toString()});
                model.addRow(new Object[]{"Email", emailField.getText().trim()});
                model.addRow(new Object[]{"Contact", contact.getText().trim()});
                JOptionPane.showMessageDialog(this, "Saved");
            } catch (java.sql.SQLException ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "DB error"); }
        });
        clear.addActionListener(e -> { id.setText(""); lastName.setText(""); firstName.setText(""); middleName.setText(""); nameExt.setText(""); course.setText(""); emailField.setText(""); contact.setText(""); });
        back.addActionListener(e -> { dispose(); new DashboardFrame().setVisible(true); });
    }
    private JPanel wrap(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setForeground(Theme.MUTED);
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentInformationForm().setVisible(true));
    }
}

/* ---------- Thin rounded scrollbar UI ---------- */
class ThinScrollBarUI2 extends javax.swing.plaf.basic.BasicScrollBarUI {
    @Override
    protected void configureScrollBarColors() {
        thumbColor = new Color(0x22, 0xd3, 0xee);
        trackColor = new Color(0x1e, 0x29, 0x3b);
    }
    @Override
    protected JButton createDecreaseButton(int orientation) { return zeroButton(); }
    @Override
    protected JButton createIncreaseButton(int orientation) { return zeroButton(); }
    private JButton zeroButton() {
        JButton b = new JButton();
        b.setPreferredSize(new Dimension(0, 0));
        b.setMinimumSize(new Dimension(0, 0));
        b.setMaximumSize(new Dimension(0, 0));
        return b;
    }
    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(thumbColor);
        int arc = 10;
        g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4, arc, arc);
        g2.dispose();
    }
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(trackColor);
        g2.fillRoundRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height, 10, 10);
        g2.dispose();
    }
}
