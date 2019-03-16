import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

public class SearchByDialog extends JDialog implements ActionListener {

    EmployeeDetails parent;
    JButton search, cancel;
    JTextField searchField;

    String dialog = "";
    String type;

    public SearchByDialog(EmployeeDetails parent, String type) {
        this.type = type;
        setTitle(DisplayValues.search_by + type);
        setModal(true);
        this.parent = parent;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JScrollPane scrollPane = new JScrollPane(searchPane());
        setContentPane(scrollPane);
        getRootPane().setDefaultButton(search);
        setSize(500, 190);
        setLocation(350, 250);
        setVisible(true);
    }

    public Container searchPane() {

        JPanel searchPanel = new JPanel(new GridLayout(3, 1));
        JPanel textPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        JLabel searchLabel;
        searchPanel.add(new JLabel(dialog));
        textPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        textPanel.add(searchLabel = new JLabel(DisplayValues.enter + getDialog() + DisplayValues.colon));
        searchLabel.setFont(Fonts.font1);
        textPanel.add(searchField = new JTextField(20));
        searchField.setFont(Fonts.font1);
        searchField.setDocument(new JTextFieldLimit(20));

        buttonPanel.add(search = new JButton(DisplayValues.search));
        search.addActionListener(this);
        search.requestFocus();

        buttonPanel.add(cancel = new JButton(DisplayValues.cancel));
        cancel.addActionListener(this);
        searchPanel.add(textPanel);
        searchPanel.add(buttonPanel);
        return searchPanel;
    }

    public String getDialog() {
        return dialog;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == search && type.equals("ID")) {
            try {
                Double.parseDouble(searchField.getText());
                this.parent.searchByIdField.setText(searchField.getText());
                this.parent.searchEmployeeById();
                dispose();
            } catch (NumberFormatException num) {
                // display message and set colour to text field if entry is wrong
                searchField.setBackground(Colour.red);
                JOptionPane.showMessageDialog(null, DisplayValues.id_format_error);
            }
        }
        else if (e.getSource() == search && type.equals("Surname")) {
            this.parent.searchBySurnameField.setText(searchField.getText());
            this.parent.searchEmployeeBySurname();
            dispose();
        }

        else if (e.getSource() == cancel)
            dispose();
    }

}
