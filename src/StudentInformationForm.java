import javax.swing.*;
import java.awt.*;

public class StudentInformationForm extends JFrame {
    private final boolean editable;
    private final String initialName;
    private final String initialEmail;
    public StudentInformationForm() { this(false, "", ""); }
    public StudentInformationForm(boolean editable) { this(editable, "", ""); }
    public StudentInformationForm(boolean editable, String name) { this(editable, name, ""); }
    public StudentInformationForm(boolean editable, String fullName, String email) {
        this.editable = editable;
        this.initialName = fullName == null ? "" : fullName;
        this.initialEmail = email == null ? "" : email;
        setTitle(editable ? "Student Information (Edit)" : "Student Information");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 700);
        setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        setContentPane(root);

        JPanel card = new JPanel();
        card.setBackground(Theme.CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        JScrollPane scroll = new JScrollPane(card);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Theme.CARD);
        root.add(scroll, BorderLayout.CENTER);

        JLabel title = new JLabel("Student Information");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Theme.CYAN);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(20));

        JTextField id = Theme.field();
        JTextField name = Theme.field();
        name.setText(initialName);
        JTextField course = Theme.field();
        JComboBox<String> year = new JComboBox<>(new String[]{"1","2","3","4"});
        JTextField emailField = Theme.field();
        emailField.setText(initialEmail);
        JTextField contact = Theme.field();

        for (JTextField f : new JTextField[]{id,name,course,emailField,contact}) f.setEditable(editable);
        year.setEnabled(editable);

        addRow(card, "Student ID", id);
        addRow(card, "Full Name", name);
        addRow(card, "Course", course);
        addRow(card, "Year Level", year);
        addRow(card, "Email", emailField);
        addRow(card, "Contact", contact);

        card.add(Box.createVerticalStrut(20));
        JPanel btns = new JPanel(new FlowLayout());
        btns.setOpaque(false);
        JButton save = Theme.button("Save");
        JButton clear = Theme.button("Clear");
        JButton back = Theme.button("Back");
        if (editable) btns.add(save);
        btns.add(clear); btns.add(back);
        card.add(btns);

        save.addActionListener(e -> {
            if (id.getText().trim().isEmpty() || name.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Required fields missing");
                return;
            }
            JOptionPane.showMessageDialog(this, "Saved (UI only)");
        });
        clear.addActionListener(e -> { id.setText(""); name.setText(""); course.setText(""); emailField.setText(""); contact.setText(""); });
        back.addActionListener(e -> { dispose(); new DashboardFrame().setVisible(true); });
    }

    private void addRow(JPanel card, String label, JComponent field) {
        JLabel l = new JLabel(label);
        l.setForeground(Theme.MUTED);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(l);
        card.add(Box.createVerticalStrut(4));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        card.add(field);
        card.add(Box.createVerticalStrut(12));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentInformationForm().setVisible(true));
    }
}
