import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class SavingsPlannerGUI extends JFrame {

    private static final Color HEADER_COLOR = new Color(34, 88, 126);
    private static final Color ACCENT_COLOR = new Color(46, 139, 87);

    private CardLayout cardLayout;
    private JPanel cardPanel;

    public SavingsPlannerGUI() {
        setTitle("Smart Savings Planner");
        setSize(950, 650);
        setMinimumSize(new Dimension(750, 550));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildNavPanel(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.add(new SavingsPlanPanel(), "PLAN");
        cardPanel.add(new MonthCalculatorPanel(), "CALC");
        add(cardPanel, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setBackground(HEADER_COLOR);
        header.setPreferredSize(new Dimension(0, 40));
        JLabel title = new JLabel("Smart Savings Planner");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(title);
        return header;
    }

    private JPanel buildNavPanel() {
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setPreferredSize(new Dimension(220, 0));
        nav.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        JButton planBtn = navButton("Monthly Savings Plan");
        JButton calcBtn = navButton("Month Calculator");

        planBtn.addActionListener(e -> cardLayout.show(cardPanel, "PLAN"));
        calcBtn.addActionListener(e -> cardLayout.show(cardPanel, "CALC"));

        nav.add(planBtn);
        nav.add(Box.createRigidArea(new Dimension(0, 12)));
        nav.add(calcBtn);
        nav.add(Box.createVerticalGlue());

        return nav;
    }

    private JButton navButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setFocusPainted(false);
        return btn;
    }

    private static Double parsePositiveDouble(JTextField field, String label) {
        try {
            double value = Double.parseDouble(field.getText().trim());
            if (value <= 0) {
                JOptionPane.showMessageDialog(null, label + " must be a positive number.",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            return value;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Please enter a valid number for " + label + ".",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private static Integer parsePositiveInt(JTextField field, String label) {
        try {
            int value = Integer.parseInt(field.getText().trim());
            if (value <= 0) {
                JOptionPane.showMessageDialog(null, label + " must be a positive whole number.",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            return value;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Please enter a valid whole number for " + label + ".",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private static DefaultTableModel buildTableModel() {
        return new DefaultTableModel(new Object[]{"Month", "Monthly Saving (PKR)", "Total Savings (PKR)"}, 0);
    }

    private static JTable styledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(24);
        table.setEnabled(false);
        table.getTableHeader().setReorderingAllowed(false);
        return table;
    }

    private class SavingsPlanPanel extends JPanel {
        private final JTextField initialField = new JTextField(10);
        private final JTextField targetField = new JTextField(10);
        private final JTextField monthsField = new JTextField(10);
        private final JLabel resultLabel = new JLabel(" ");
        private final DefaultTableModel tableModel = buildTableModel();

        SavingsPlanPanel() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            JPanel form = new JPanel(new GridBagLayout());
            form.setBorder(BorderFactory.createTitledBorder("Monthly Savings Plan"));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            addFormRow(form, gbc, 0, "Current monthly savings (PKR):", initialField);
            addFormRow(form, gbc, 1, "Target savings amount (PKR):", targetField);
            addFormRow(form, gbc, 2, "Number of months:", monthsField);

            JButton calcBtn = new JButton("Calculate Plan");
            calcBtn.setBackground(ACCENT_COLOR);
            calcBtn.setForeground(Color.WHITE);
            calcBtn.addActionListener(this::calculate);
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 2;
            form.add(calcBtn, gbc);

            resultLabel.setFont(new Font("SansSerif", Font.BOLD, 13));

            JPanel top = new JPanel(new BorderLayout());
            top.add(form, BorderLayout.NORTH);
            top.add(resultLabel, BorderLayout.SOUTH);

            add(top, BorderLayout.NORTH);
            add(new JScrollPane(styledTable(tableModel)), BorderLayout.CENTER);
        }

        private void calculate(ActionEvent e) {
            Double initialAmount = parsePositiveDouble(initialField, "Current monthly savings");
            if (initialAmount == null) return;
            Double targetAmount = parsePositiveDouble(targetField, "Target savings amount");
            if (targetAmount == null) return;
            Integer months = parsePositiveInt(monthsField, "Number of months");
            if (months == null) return;

            tableModel.setRowCount(0);

            if (months == 1) {
                if (initialAmount >= targetAmount) {
                    resultLabel.setText("Your target is already achieved in the 1st month!");
                } else {
                    resultLabel.setText("Your target cannot be achieved in 1 month.");
                }
                return;
            }

            if (months * initialAmount >= targetAmount) {
                resultLabel.setText("You will reach your target without increasing your monthly savings.");
                fillTable(tableModel, initialAmount, months, 0);
                return;
            }

            double difference = ((2 * targetAmount) / months - 2 * initialAmount) / (months - 1);
            difference = Math.ceil(difference);
            resultLabel.setText(String.format("Recommended monthly increase: PKR %.2f", difference));
            fillTable(tableModel, initialAmount, months, difference);
        }
    }

    private class MonthCalculatorPanel extends JPanel {
        private final JTextField currentField = new JTextField(10);
        private final JTextField increaseField = new JTextField(10);
        private final JTextField targetField = new JTextField(10);
        private final JLabel resultLabel = new JLabel(" ");
        private final DefaultTableModel tableModel = buildTableModel();

        MonthCalculatorPanel() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            JPanel form = new JPanel(new GridBagLayout());
            form.setBorder(BorderFactory.createTitledBorder("Month Calculator"));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            addFormRow(form, gbc, 0, "Current monthly savings (PKR):", currentField);
            addFormRow(form, gbc, 1, "Monthly increase in saving (PKR):", increaseField);
            addFormRow(form, gbc, 2, "Target amount (PKR):", targetField);

            JButton calcBtn = new JButton("Calculate Months Needed");
            calcBtn.setBackground(ACCENT_COLOR);
            calcBtn.setForeground(Color.WHITE);
            calcBtn.addActionListener(this::calculate);
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 2;
            form.add(calcBtn, gbc);

            resultLabel.setFont(new Font("SansSerif", Font.BOLD, 13));

            JPanel top = new JPanel(new BorderLayout());
            top.add(form, BorderLayout.NORTH);
            top.add(resultLabel, BorderLayout.SOUTH);

            add(top, BorderLayout.NORTH);
            add(new JScrollPane(styledTable(tableModel)), BorderLayout.CENTER);
        }

        private void calculate(ActionEvent e) {
            Double a = parsePositiveDouble(currentField, "Current monthly savings");
            if (a == null) return;
            Double target = parsePositiveDouble(targetField, "Target amount");
            if (target == null) return;

            double d;
            try {
                d = Double.parseDouble(increaseField.getText().trim());
                if (d < 0) {
                    JOptionPane.showMessageDialog(this, "Monthly increase cannot be negative.",
                            "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for the monthly increase.",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }

            tableModel.setRowCount(0);

            if (d == 0 && a < target) {
                resultLabel.setText("Target cannot be achieved without increasing monthly savings.");
                return;
            }

            double sum = 0.0;
            double savings = a;
            int month = 0;
            List<Object[]> rows = new ArrayList<>();

            while (sum < target && month <= 600) {
                month++;
                sum += savings;
                rows.add(new Object[]{month, String.format("%.2f", savings), String.format("%.2f", sum)});
                savings += d;
            }

            for (Object[] row : rows) {
                tableModel.addRow(row);
            }

            if (sum >= target) {
                resultLabel.setText(String.format("You will reach your target in %d months. Total savings: PKR %.2f",
                        month, sum));
            } else {
                resultLabel.setText("Target not achievable within a reasonable time frame (600 months).");
            }
        }
    }

    private static void addFormRow(JPanel form, GridBagConstraints gbc, int row, String labelText, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        form.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        form.add(field, gbc);
    }

    private static void fillTable(DefaultTableModel model, double a, int n, double d) {
        double sum = 0.0;
        double term = a;
        for (int i = 1; i <= n; i++) {
            sum += term;
            model.addRow(new Object[]{i, String.format("%.2f", term), String.format("%.2f", sum)});
            term += d;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SavingsPlannerGUI().setVisible(true));
    }
}