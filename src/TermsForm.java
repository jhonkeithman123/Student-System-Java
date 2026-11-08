import javax.swing.*;
import java.awt.*;

public class TermsForm extends JFrame {
    public TermsForm() {
        setTitle("Terms & Conditions");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        setContentPane(root);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Theme.CARD);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        root.add(card, BorderLayout.CENTER);

        JLabel title = new JLabel("Terms & Conditions");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Theme.CYAN);
        card.add(title, BorderLayout.NORTH);

        JTextArea ta = new JTextArea("Terms & Conditions\n\nBy using this system you agree to... (placeholder)");
        ta.setEditable(false);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBackground(Theme.CARD);
        ta.setForeground(Color.WHITE);
        card.add(new JScrollPane(ta), BorderLayout.CENTER);
    }
    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new TermsForm().setVisible(true)); }
}
