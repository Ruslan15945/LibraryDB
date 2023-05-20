import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MenuPanel extends JPanel {
    private Client window;
    private JComboBox<Librarian> librarianComboBox;

    public MenuPanel(Client window){
        this.window = window;
        setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));



        JButton logoutButton = new JButton("Выйти");
        logoutButton.setFont(new Font(Font.DIALOG, Font.BOLD, 50));
        logoutButton.setFocusPainted(false);
        logoutButton.setContentAreaFilled(false);
        logoutButton.setBorder(null);
        logoutButton.setMargin(new Insets(10,100,10,100));
        logoutButton.setAlignmentX(CENTER_ALIGNMENT);

        JButton readersButton = new JButton("Читатели");
        readersButton.setFont(new Font(Font.DIALOG, Font.BOLD, 50));
        readersButton.setFocusPainted(false);
        readersButton.setContentAreaFilled(false);
        readersButton.setBorder(null);
        readersButton.setMargin(new Insets(10,100,10,100));
        readersButton.setAlignmentX(CENTER_ALIGNMENT);

        JButton librariansButton = new JButton("Библиотекари");
        librariansButton.setFont(new Font(Font.DIALOG, Font.BOLD, 50));
        librariansButton.setFocusPainted(false);
        librariansButton.setContentAreaFilled(false);
        librariansButton.setBorder(null);
        librariansButton.setMargin(new Insets(10,100,10,100));
        librariansButton.setAlignmentX(CENTER_ALIGNMENT);

        JButton fundButton = new JButton("Библиотечный фонд");
        fundButton.setFont(new Font(Font.DIALOG, Font.BOLD, 50));
        fundButton.setFocusPainted(false);
        fundButton.setContentAreaFilled(false);
        fundButton.setBorder(null);
        fundButton.setMargin(new Insets(10,100,10,100));
        fundButton.setAlignmentX(CENTER_ALIGNMENT);

        JButton borrowButton = new JButton("Выдать книгу");
        borrowButton.setFont(new Font(Font.DIALOG, Font.BOLD, 50));
        borrowButton.setFocusPainted(false);
        borrowButton.setContentAreaFilled(false);
        borrowButton.setBorder(null);
        borrowButton.setMargin(new Insets(10,100,10,100));
        borrowButton.setAlignmentX(CENTER_ALIGNMENT);

        logoutButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                window.logout();
            }
        });
        readersButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                window.setReadersView();
            }
        });
        librariansButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                window.setLibrariansView();
            }
        });
        fundButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                window.setInstancesView();
            }
        });
        borrowButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                window.setBorrowFrame("");
            }
        });



        librarianComboBox= new JComboBox<Librarian>(new Librarian[]{new Librarian(0,0,"Выберите библиотекаря")});

        librarianComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                window.setLibrarian((Librarian)librarianComboBox.getSelectedItem());
            }
        });

        librarianComboBox.setMaximumSize(new Dimension(500, librarianComboBox.getPreferredSize().height));
        add(librarianComboBox);
        add(Box.createVerticalGlue());
        add(readersButton);
        add(Box.createVerticalStrut(80));
        add(librariansButton);
        add(Box.createVerticalStrut(80));
        add(fundButton);
        add(Box.createVerticalStrut(80));
        add(borrowButton);
        add(Box.createVerticalStrut(80));
        add(logoutButton);
        add(Box.createVerticalGlue());
        setFocusable(true);
        requestFocus();

    }

    public void updateConsts(){
        librarianComboBox.removeAllItems();
        String Query = """
                select id, firstname, lastname, middlename, library
                from librarians
                order by id""";
        try{
            ResultSet ret = window.getSession().executeQuery(Query);
            while(ret.next()){
                librarianComboBox.addItem(new Librarian(ret.getInt("id"), ret.getInt("library"), ret.getString("firstname")+" "+ret.getString("middlename")+" "+ret.getString("lastname")));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
