
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class LoginForm extends JFrame {

    public LoginForm() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setMinimumSize(new Dimension(440, 620));

        TransitionHost host = new TransitionHost();
        setContentPane(host);

        pack();
        setSize(500, 700);
        setMinimumSize(new Dimension(500, 700));
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.formdev.flatlaf.FlatDarkLaf");
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException t) {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException ex) {
                /* ignore */ }
        }
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}

/* ---------- Shared matrix + electric glow background ---------- */
class MatrixBackgroundPanel extends JPanel {

    private final Timer timer;
    private int[] dropsArr;
    private final Random rnd = new Random();
    private float wavePhase = 0f;

    MatrixBackgroundPanel() {
        setOpaque(true);
        setBackground(new Color(0x0f, 0x17, 0x2a));
        timer = new Timer(33, e -> {
            advance();
            repaint();
        });
        timer.start();
    }

    private void advance() {
        wavePhase += 0.05f;
    }

    private int[] ensureDrops(int w, int h) {
        int c = Math.max(1, w / 16);
        if (dropsArr == null || dropsArr.length != c) {
            dropsArr = new int[c];
            for (int i = 0; i < c; i++) {
                dropsArr[i] = rnd.nextInt(Math.max(1, h));
            }
        }
        return dropsArr;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth(), h = getHeight();
        if (w <= 0 || h <= 0) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setColor(getBackground());
        g2.fillRect(0, 0, w, h);

        int[] d = ensureDrops(w, h);
        int colW = 16;
        g2.setFont(new Font("Monospaced", Font.PLAIN, 14));
        for (int i = 0; i < d.length; i++) {
            int x = i * colW;
            for (int t = 0; t < 12; t++) {
                int y = d[i] - t * 14;
                if (y < 0 || y > h) {
                    continue;
                }
                int alpha = Math.max(0, 220 - t * 18);
                g2.setColor(new Color(0x22, 0xd3, 0xee, alpha));
                char ch = (char) (0x30A0 + rnd.nextInt(96));
                g2.drawString(String.valueOf(ch), x, y);
            }
            d[i] += 14;
            if (d[i] > h + 100 || rnd.nextInt(40) == 0) {
                d[i] = -rnd.nextInt(60);
            }
        }

        // (electric glow lines removed per user request)
        g2.dispose();
    }
}

/* ---------- Reusable eye password field ---------- */
final class PasswordFieldWithEye extends JPanel {

    private final JPasswordField field = new JPasswordField();

    public char[] getPassword() {
        return field.getPassword();
    }
    private final JToggleButton eyeBtn = new JToggleButton("\uD83D\uDC41");

    PasswordFieldWithEye() {
        setLayout(null);
        setOpaque(false);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        setPreferredSize(new Dimension(300, 40));
        setMinimumSize(new Dimension(0, 40));
        setAlignmentX(Component.CENTER_ALIGNMENT);

        field.setBackground(new Color(0x0f, 0x17, 0x2a));
        field.setForeground(Color.WHITE);
        field.setCaretColor(new Color(0x22, 0xd3, 0xee));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x33, 0x41, 0x55)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        add(field);

        eyeBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        eyeBtn.setForeground(new Color(0x94, 0xa3, 0xb8));
        eyeBtn.setBackground(new Color(0x0f, 0x17, 0x2a));
        eyeBtn.setFocusPainted(false);
        eyeBtn.setBorderPainted(true);
        eyeBtn.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, new Color(0x33, 0x41, 0x55)));
        eyeBtn.setContentAreaFilled(true);
        eyeBtn.setOpaque(true);
        add(eyeBtn);
        setComponentZOrder(eyeBtn, 0);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                doLayout();
            }
        });
        doLayout();

        eyeBtn.addActionListener(e -> {
            boolean show = eyeBtn.isSelected();
            field.setEchoChar(show ? (char) 0 : '\u2022');
            final String openIcon = "\uD83D\uDC41";
            final String closedIcon = "\uD83D\uDE48";
            final int[] sizes = {16, 12, 8, 12, 16};
            Timer t = new Timer(40, null);
            int[] step = {0};
            t.addActionListener(ev -> {
                if (step[0] < sizes.length) {
                    eyeBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, sizes[step[0]]));
                    if (step[0] == 2) {
                        eyeBtn.setText(show ? closedIcon : openIcon);
                    }
                    step[0]++;
                } else {
                    eyeBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
                    eyeBtn.setText(show ? openIcon : closedIcon);
                    t.stop();
                }
            });
            t.start();
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (eyeBtn.getWidth() == 0 && getWidth() > 0) {
            doLayout();
        }
        super.paintComponent(g);
    }

    @Override
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        doLayout();
    }

    @Override
    public void doLayout() {
        int w = getWidth(), h = getHeight();
        field.setBounds(0, 0, w, h);
        int bw = h, bh = h;
        eyeBtn.setBounds(w - bw, 0, bw, bh);
    }

    public JPasswordField getField() {
        return field;
    }
}

/* ---------- Login panel ---------- */
class LoginPanel extends JPanel {

    LoginPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("WELCOME BACK");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(0x22, 0xd3, 0xee));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Sign in to continue");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(0x94, 0xa3, 0xb8));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JComboBox<String> typeBox = new JComboBox<>(new String[]{"ID Number", "Email"});
        typeBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        typeBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField user = new JTextField();
        user.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        user.setAlignmentX(Component.CENTER_ALIGNMENT);
        user.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        user.setBackground(new Color(0x0f, 0x17, 0x2a));
        user.setForeground(Color.WHITE);
        user.setCaretColor(new Color(0x22, 0xd3, 0xee));
        user.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x33, 0x41, 0x55)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        // apply IdFormat filter initially (ID Number selected)
        javax.swing.text.DocumentFilter idFilter = new javax.swing.text.DocumentFilter() {
            public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
                String cur = fb.getDocument().getText(0, fb.getDocument().getLength());
                String next = cur.substring(0, offset) + (text == null ? "" : text) + cur.substring(offset + length);
                String formatted = IdFormat.format(next);
                fb.replace(0, fb.getDocument().getLength(), formatted, attrs);
            }
            public void insertString(FilterBypass fb, int offset, String text, javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException { replace(fb, offset, 0, text, attrs); }
            public void remove(FilterBypass fb, int offset, int length) throws javax.swing.text.BadLocationException { replace(fb, offset, length, "", null); }
        };
        ((javax.swing.text.AbstractDocument) user.getDocument()).setDocumentFilter(idFilter);

        typeBox.addActionListener(e -> {
            if ("ID Number".equals(typeBox.getSelectedItem())) {
                ((javax.swing.text.AbstractDocument) user.getDocument()).setDocumentFilter(idFilter);
            } else {
                ((javax.swing.text.AbstractDocument) user.getDocument()).setDocumentFilter(null);
            }
        });

        PasswordFieldWithEye pass = new PasswordFieldWithEye();

        JLabel userL = new JLabel("ID Number");
        userL.setForeground(new Color(0xcb, 0xd5, 0xe1));
        userL.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userL.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel passL = new JLabel("Password");
        passL.setForeground(new Color(0xcb, 0xd5, 0xe1));
        passL.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        passL.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btn = new JButton("LOGIN");
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setPreferredSize(new Dimension(300, 44));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(new Color(0x06, 0xb6, 0xd4));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            String id = user.getText().trim();
            String pw = new String(pass.getPassword());
            boolean byEmail = "Email".equals(typeBox.getSelectedItem());
            String col = byEmail ? "email" : "id";
            try (java.sql.Connection c = DB.getConnection(); java.sql.PreparedStatement ps = c.prepareStatement("SELECT role,name,email,id FROM users WHERE " + col + "=? AND password=?")) {
                ps.setString(1, id);
                ps.setString(2, pw);
                java.sql.ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String role = rs.getString("role");
                    String name = rs.getString("name");
                    String email = rs.getString("email");
                    String userId = rs.getString("id");
                    Window w = SwingUtilities.getWindowAncestor(this);
                    if (w != null) {
                        w.dispose();
                    }
                    if ("admin".equalsIgnoreCase(role)) {
                        new AdminPanel().setVisible(true);
                    } else {
                        new DashboardFrame(userId).setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials");
                }
            } catch (java.sql.SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "DB error");
            }
        });
        add(Box.createVerticalGlue());
        add(title);
        add(Box.createVerticalStrut(6));
        add(sub);
        add(Box.createVerticalStrut(28));
        add(typeBox);
        add(Box.createVerticalStrut(8));
        add(userL);
        add(Box.createVerticalStrut(4));
        add(user);
        add(Box.createVerticalStrut(16));
        add(passL);
        add(Box.createVerticalStrut(4));
        add(pass);
        add(Box.createVerticalStrut(24));
        add(btn);
        add(Box.createVerticalGlue());
    }
}

/* ---------- Signup panel ---------- */
class SignupPanel extends JPanel {

    SignupPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setMinimumSize(new Dimension(320, 480));
        setPreferredSize(new Dimension(360, 620));

        JLabel title = new JLabel("CREATE ACCOUNT");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(0x22, 0xd3, 0xee));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Sign up to get started");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(0x94, 0xa3, 0xb8));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField idField = field();
        // ID format lock
        ((javax.swing.text.AbstractDocument) idField.getDocument()).setDocumentFilter(new javax.swing.text.DocumentFilter() {
            public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
                String cur = fb.getDocument().getText(0, fb.getDocument().getLength());
                String next = cur.substring(0, offset) + (text == null ? "" : text) + cur.substring(offset + length);
                String formatted = IdFormat.format(next);
                fb.replace(0, fb.getDocument().getLength(), formatted, attrs);
            }
            public void insertString(FilterBypass fb, int offset, String text, javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException { replace(fb, offset, 0, text, attrs); }
            public void remove(FilterBypass fb, int offset, int length) throws javax.swing.text.BadLocationException { replace(fb, offset, length, "", null); }
        });
        JTextField lastName = field();
        JTextField firstName = field();
        JTextField middleName = field();
        JTextField nameExt = field();
        JTextField email = field();
        JPasswordField passField = new JPasswordField();
        passField.setMaximumSize(new Dimension(400, 40));
        passField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passField.setBackground(new Color(0x0f, 0x17, 0x2a));
        passField.setForeground(Color.WHITE);
        passField.setCaretColor(new Color(0x22, 0xd3, 0xee));
        passField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x33, 0x41, 0x55)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JPasswordField confirmField = new JPasswordField();
        confirmField.setMaximumSize(new Dimension(400, 40));
        confirmField.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmField.setBackground(new Color(0x0f, 0x17, 0x2a));
        confirmField.setForeground(Color.WHITE);
        confirmField.setCaretColor(new Color(0x22, 0xd3, 0xee));
        confirmField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x33, 0x41, 0x55)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        // single eye controlling both password fields
        JToggleButton eyeBtn = new JToggleButton("\uD83D\uDC41");
        eyeBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        eyeBtn.setForeground(new Color(0x94, 0xa3, 0xb8));
        eyeBtn.setBackground(new Color(0x0f, 0x17, 0x2a));
        eyeBtn.setFocusPainted(false);
        eyeBtn.setBorderPainted(true);
        eyeBtn.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, new Color(0x33, 0x41, 0x55)));
        eyeBtn.setContentAreaFilled(true);
        eyeBtn.setOpaque(true);

        JPanel passWrap = new JPanel() {
            @Override
            public void doLayout() {
                int w = getWidth(), h = getHeight();
                passField.setBounds(0, 0, w, h);
                int bw = h, bh = h;
                eyeBtn.setBounds(w - bw, 0, bw, bh);
            }

            @Override
            protected void paintComponent(Graphics g) {
                if (eyeBtn.getWidth() == 0 && getWidth() > 0) {
                    doLayout();
                }
                super.paintComponent(g);
            }
        };
        passWrap.setLayout(null);
        passWrap.setOpaque(false);
        passWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passWrap.setPreferredSize(new Dimension(300, 40));
        passWrap.setAlignmentX(Component.CENTER_ALIGNMENT);
        passWrap.add(passField);
        passWrap.add(eyeBtn);
        passWrap.setComponentZOrder(eyeBtn, 0);
        passWrap.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                passWrap.doLayout();
            }
        });
        passWrap.doLayout();

        eyeBtn.addActionListener(e -> {
            boolean show = eyeBtn.isSelected();
            passField.setEchoChar(show ? (char) 0 : '\u2022');
            confirmField.setEchoChar(show ? (char) 0 : '\u2022');
            Timer t = new Timer(40, null);
            int[] step = {0};
            t.addActionListener(ev -> {
                if (step[0] < 5) {
                    int[] sizes = {16, 12, 8, 12, 16};
                    eyeBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, sizes[step[0]]));
                    if (step[0] == 2) {
                        eyeBtn.setText(show ? "\uD83D\uDE48" : "\uD83D\uDC41");
                    }
                    step[0]++;
                } else {
                    eyeBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
                    eyeBtn.setText(show ? "\uD83D\uDC41" : "\uD83D\uDE48");
                    t.stop();
                }
            });
            t.start();
        });

        JCheckBox terms = new JCheckBox("");
        terms.setForeground(new Color(0xcb, 0xd5, 0xe1));
        terms.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        terms.setOpaque(false);
        terms.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel termsLink = new JLabel("I agree to Terms & Conditions");
        termsLink.setForeground(Theme.CYAN);
        termsLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        termsLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new TermsForm().setVisible(true);
            }
        });
        JPanel termsRow = new JPanel(new FlowLayout());
        termsRow.setOpaque(false);
        termsRow.add(terms);
        termsRow.add(termsLink);
        termsRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btn = new JButton("SIGN UP");
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setPreferredSize(new Dimension(300, 44));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(new Color(0x06, 0xb6, 0xd4));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        add(title);
        add(Box.createVerticalStrut(6));
        add(sub);
        add(Box.createVerticalStrut(24));
        add(lbl("ID Number"));
        add(Box.createVerticalStrut(4));
        add(idField);
        add(Box.createVerticalStrut(12));
        JPanel nameRow = new JPanel(new GridLayout(2, 2, 8, 8));
        nameRow.setOpaque(false);
        nameRow.setMaximumSize(new Dimension(480, 120));
        nameRow.add(wrapSignup("Last Name", lastName));
        nameRow.add(wrapSignup("First Name", firstName));
        nameRow.add(wrapSignup("Middle Name", middleName));
        nameRow.add(wrapSignup("Name Ext (opt)", nameExt));
        add(lbl("Full Name"));
        add(Box.createVerticalStrut(4));
        add(nameRow);
        add(Box.createVerticalStrut(12));
        add(lbl("Email"));
        add(Box.createVerticalStrut(4));
        add(email);
        add(Box.createVerticalStrut(12));
        add(lbl("Password"));
        add(Box.createVerticalStrut(4));
        add(passWrap);
        add(Box.createVerticalStrut(12));
        add(lbl("Confirm Password"));
        add(Box.createVerticalStrut(4));
        add(confirmField);
        add(Box.createVerticalStrut(16));
        add(termsRow);
        add(Box.createVerticalStrut(20));
        btn.addActionListener(e -> {
            String id = idField.getText().trim();
            if (!IdFormat.isValid(id)) { JOptionPane.showMessageDialog(this, "Invalid ID format (XXXX-XXXXX-XX-X)"); return; }
            String pw = new String(passField.getPassword());
            if (!pw.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$")) {
                JOptionPane.showMessageDialog(this, "Password must be 8+ chars with upper, lower, number, symbol");
                return;
            }
            if (!terms.isSelected()) {
                JOptionPane.showMessageDialog(this, "You must accept Terms & Conditions");
                return;
            }
            if (!email.getText().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                JOptionPane.showMessageDialog(this, "Invalid email");
                return;
            }
            String domain = email.getText().substring(email.getText().lastIndexOf('.') + 1).toLowerCase();
            java.util.Set<String> tlds = new java.util.HashSet<>(java.util.Arrays.asList("com", "org", "net", "edu", "gov", "io", "co", "ph"));
            if (!tlds.contains(domain)) {
                JOptionPane.showMessageDialog(this, "Invalid email domain");
                return;
            }
            String full = (lastName.getText()+" "+firstName.getText()+" "+middleName.getText()+" "+nameExt.getText()).trim();
            // do not insert into DB during signup; pass data to StudentInformationForm
            Window w = SwingUtilities.getWindowAncestor(this);
            if (w != null) {
                w.dispose();
            }
            new StudentInformationForm(true, id, full, email.getText().trim(), "", pw).setVisible(true);
        });
        add(btn);
    }

    private JPanel wrapSignup(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setForeground(new Color(0xcb, 0xd5, 0xe1));
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private JTextField field() {
        JTextField f = new JTextField();
        f.setMaximumSize(new Dimension(400, 40));
        f.setAlignmentX(Component.CENTER_ALIGNMENT);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBackground(new Color(0x0f, 0x17, 0x2a));
        f.setForeground(Color.WHITE);
        f.setCaretColor(new Color(0x22, 0xd3, 0xee));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x33, 0x41, 0x55)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return f;
    }

    private JLabel lbl(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(new Color(0xcb, 0xd5, 0xe1));
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }
}

/* ---------- Transition host with animated expand/collapse ---------- */
class TransitionHost extends JPanel {

    private final MatrixBackgroundPanel bg = new MatrixBackgroundPanel();
    private final JPanel centering = new JPanel(new GridBagLayout());
    private JComponent current;
    private JComponent pendingNext;
    private JPanel activeCardPanel;
    private float progress = 0f;
    private boolean animating = false;
    private boolean swapped = false;
    private final Timer timer;
    private final JToggleButton tabLogin = new JToggleButton("Login", true);
    private final JToggleButton tabSignup = new JToggleButton("Sign Up");

    private Dimension startSize;
    private Dimension endSize;

    TransitionHost() {
        setLayout(new BorderLayout());
        setOpaque(false);
        bg.setOpaque(true);
        add(bg, BorderLayout.CENTER);

        centering.setOpaque(false);
        centering.setMinimumSize(new Dimension(360, 520));
        bg.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(-40, 0, 0, 0);
        bg.add(centering, gbc);

        activeCardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0x1e, 0x29, 0x3b, 190));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        activeCardPanel.setOpaque(false);
        activeCardPanel.setLayout(new BorderLayout());
        activeCardPanel.setMinimumSize(new Dimension(340, 480));
        activeCardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x33, 0x41, 0x55), 1),
                BorderFactory.createEmptyBorder(28, 32, 28, 32)
        ));

        current = new LoginPanel();
        activeCardPanel.add(current, BorderLayout.CENTER);
        centering.add(activeCardPanel, new GridBagConstraints());

        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        tabs.setOpaque(false);
        styleTab(tabLogin);
        styleTab(tabSignup);

        tabLogin.addActionListener(e -> {
            if (!tabLogin.isSelected()) return;
            tabSignup.setSelected(false);
            switchTo(new LoginPanel());
        });
        tabSignup.addActionListener(e -> {
            if (!tabSignup.isSelected()) return;
            tabLogin.setSelected(false);
            switchTo(new SignupPanel());
        });

        ButtonGroup g = new ButtonGroup();
        g.add(tabLogin);
        g.add(tabSignup);
        tabLogin.setSelected(true);
        tabs.add(tabLogin);
        tabs.add(tabSignup);

        GridBagConstraints gt = new GridBagConstraints();
        gt.gridx = 0;
        gt.gridy = 0;
        gt.weightx = 1;
        gt.anchor = GridBagConstraints.NORTH;
        gt.insets = new Insets(16, 0, 0, 0);
        bg.add(tabs, gt);

        timer = new Timer(16, e -> tick());
    }

    private void styleTab(JToggleButton b) {
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(new Color(0x94, 0xa3, 0xb8));
        b.setBackground(new Color(0x1e, 0x29, 0x3b));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void switchTo(JComponent next) {
        if (next instanceof SignupPanel) {
            JScrollPane sp = new JScrollPane(next);
            sp.setOpaque(false);
            sp.getViewport().setOpaque(false);
            sp.setBorder(null);
            sp.setMinimumSize(new Dimension(340, 480));
            sp.setPreferredSize(new Dimension(360, 620));
            sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            sp.getVerticalScrollBar().setUnitIncrement(20);
            sp.getHorizontalScrollBar().setUnitIncrement(20);
            sp.getVerticalScrollBar().setUI(new ThinScrollBarUI());
            sp.getHorizontalScrollBar().setUI(new ThinScrollBarUI());
            next = sp;
        }
        if (animating || next.getClass().equals(current.getClass())) {
            return;
        }

        startSize = new Dimension(activeCardPanel.getWidth(), activeCardPanel.getHeight());
        activeCardPanel.removeAll();
        activeCardPanel.add(next, BorderLayout.CENTER);
        activeCardPanel.revalidate();
        activeCardPanel.doLayout();
        Dimension pref = next.getPreferredSize();
        if (pref == null || pref.width <= 0 || pref.height <= 0) {
            pref = next.getMinimumSize();
        }
        endSize = new Dimension(Math.max(activeCardPanel.getMinimumSize().width, pref.width),
                                Math.max(activeCardPanel.getMinimumSize().height, pref.height));

        pendingNext = next;
        animating = true;
        progress = 0f;
        timer.start();
    }

    private void tick() {
        progress += 0.05f;
        if (progress >= 1f) {
            progress = 1f;
            animating = false;
            timer.stop();
            if (pendingNext != null) {
                current = pendingNext;
                pendingNext = null;
            }
            activeCardPanel.setPreferredSize(null);
            activeCardPanel.revalidate();
            centering.revalidate();
            centering.repaint();
            return;
        }

        float t = progress;
        float eased = (float)(0.5 - 0.5 * Math.cos(Math.PI * t));
        int w = (int)(startSize.width + (endSize.width - startSize.width) * eased);
        int h = (int)(startSize.height + (endSize.height - startSize.height) * eased);
        activeCardPanel.setPreferredSize(new Dimension(w, h));
        activeCardPanel.revalidate();
        activeCardPanel.repaint();
        centering.revalidate();
        centering.repaint();
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }
}
class ThinScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
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
