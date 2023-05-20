import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

public class LibrariansInfo extends JPanel{

    private Client window;

    private int librarianId = 0;

    private Color okColor = new Color(28, 150, 18);
    private Color errorColor = new Color(153, 147, 22);
    private Color fatalColor = new Color(118, 20, 15);


    public LibrariansInfo(Client window){
        this.window = window;
        LibrariansInfoPanel.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());

        greetingLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 50));
        filtersLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 20));

        backButton.setFont(new Font(Font.DIALOG, Font.BOLD, 40));
        backButton.setFocusPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setBorder(null);
        backButton.setAlignmentX(CENTER_ALIGNMENT);

        cancelButton.setFont(new Font(Font.DIALOG, Font.BOLD, 30));
        cancelButton.setFocusPainted(false);
        cancelButton.setContentAreaFilled(false);
        cancelButton.setBorder(null);
        cancelButton.setAlignmentX(CENTER_ALIGNMENT);

        applyFiltersButton.setFont(new Font(Font.DIALOG, Font.BOLD, 30));
        applyFiltersButton.setFocusPainted(false);
        applyFiltersButton.setContentAreaFilled(false);
        applyFiltersButton.setBorder(null);
        applyFiltersButton.setAlignmentX(CENTER_ALIGNMENT);

        logPane.setBorder(null);

        logArea.setEditable(false);
        logArea.setBackground(getBackground());
        logArea.setFont(new Font(Font.DIALOG, Font.BOLD, 15));

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                window.setMenuView();
            }
        });


        statButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showStats(librarianId);
            }
        });

        librariansTable.setShowGrid(false);
        librariansTable.setDefaultEditor(Object.class, null);
        librariansTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = librariansTable.rowAtPoint(e.getPoint());
                TableModel model = librariansTable.getModel();
                int colCount = model.getColumnCount();
                Object[] rowData = new Object[colCount];
                for (int i = 0; i<colCount; ++i){
                    rowData[i] = model.getValueAt(row, i);
                }
                librarianId = (int)rowData[0];
                fnameField.setText((String)rowData[1]);
                mnameField.setText((String)rowData[2]);
                lnameField.setText((String)rowData[3]);
                hallField.setText(String.valueOf((int)rowData[6]));
                for (int i = 0; i < libraryBox.getItemCount(); i++) {
                    Genre item = (Genre) libraryBox.getItemAt(i);
                    if (item.getId() == (int)rowData[4]) {
                        libraryBox.setSelectedItem(item);
                        break;
                    }
                }
                showStats(librarianId);

            }
        });

        applyFiltersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateData(true,true);
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateData(false, true);
            }
        });

    }

    private JPanel leftPanel;
    private JPanel FiltersLabel;
    private JLabel filtersLabel;
    private JPanel selectedLibrarianInfo;
    private JTextField fnameField;
    private JTextField lnameField;
    private JTextField mnameField;
    private JScrollPane baseFiltersPane;
    private JPanel baseFiltersPanel;
    private JTextField basefnameField;
    private JTextField baselnameField;
    private JTextField basemnameField;
    private JPanel StatsPanel;
    private JTextField statFromField;
    private JTextField statToField;
    private JTable statsTable;
    private JPanel ApplyPanel;
    private JButton applyFiltersButton;
    private JButton cancelButton;
    private JPanel rightPanel;
    private JPanel librariansPanel;
    private JScrollPane librariansPane;
    private JTable librariansTable;
    private JButton backButton;
    private JScrollPane logPane;
    private JTextArea logArea;
    private JLabel greetingLabel;
    private JPanel LibrariansInfoPanel;
    private JComboBox libraryBox;
    private JTextField hallField;
    private JTextField basehallField;
    private JComboBox baselibraryBox;
    private JButton statButton;
    private JLabel totalReadersLabel;

    private void showStats(int libid){
        logArea.setText("");
        logArea.setForeground(okColor);

        String query = "select readers.id, readers.firstname, readers.middlename, readers.lastname, readers.birthdate, sexes.name sex, readers.regdate, lastdate\n" +
                "from (\n" +
                "select reader, max(d) lastdate\n" +
                "from(\n" +
                "  select reader, max(borrowdate) d\n" +
                "  from borrowings\n" +
                "  where borrowings.librarian = " + librarianId + "\n" +
                (statFromField.getText().length()>0 && checkDate(statFromField)
                        ? "  and borrowdate >= TO_DATE('" + statFromField.getText() + "', 'DD.MM.YYYY')\n"
                        : "") +
                (statToField.getText().length()>0 && checkDate(statToField)
                        ? "  and borrowdate <= TO_DATE('" + statToField.getText() + "', 'DD.MM.YYYY')\n"
                        : "") +
                "  group by reader\n" +
                "  union all\n" +
                "  select reader, max(returndate) d\n" +
                "  from returns\n" +
                "  join borrowings on borrowings.id = returns.borrowing\n" +
                "  where returns.librarian = " + librarianId + "\n" +
                (statFromField.getText().length()>0 && checkDate(statFromField)
                        ? "  and returndate >= TO_DATE('" + statFromField.getText() + "', 'DD.MM.YYYY')\n"
                        : "") +
                (statToField.getText().length()>0 && checkDate(statToField)
                        ? "  and returndate <= TO_DATE('" + statToField.getText() + "', 'DD.MM.YYYY')\n"
                        : "") +
                "  group by reader)\n" +
                "group by reader\n" +
                ")\n" +
                "join readers on readers.id = reader\n" +
                "join sexes on sexes.id = sex";

        System.out.println(query);

        Object[] columnNames = {"id","ФИО","Пол","Дата обслуживания"};
        DefaultTableModel newModel = new DefaultTableModel(null, columnNames);

        try{
            ResultSet ret = window.getSession().executeQuery(query);
            int totalfound = 0;
            while(ret.next()){
                String mname = " " + ret.getString("middlename") + " ";
                if (ret.wasNull()){
                    mname = " ";
                }
                Object[] rowData = {ret.getInt("id"),ret.getString("firstname")+mname+ret.getString("lastname"), ret.getString("sex"), ret.getDate("lastdate")};
                newModel.addRow(rowData);
                totalfound++;
            }
            totalReadersLabel.setText(((statFromField.getText().length()>0 && checkDate(statFromField) || (statFromField.getText().length()>0 && checkDate(statFromField)) ? "За период" : "За всё время")+" обслужено читателей: "+ totalfound));
            logArea.setText((logArea.getText() + "\nOK: " + (totalfound>0?totalfound + " services found":"no services found")).trim());
            statsTable.setModel(newModel);
            statsTable.removeColumn(statsTable.getColumnModel().getColumn(0));
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            logArea.setText((logArea.getText() + "\nselect services FATAL ERROR:\n" + ex.getMessage()).trim());
            logArea.setForeground(fatalColor);
        }

        leftPanel.revalidate();
    }

    private void createUIComponents() {

        baselibraryBox = new JComboBox<Genre>(new Genre[]{new Genre(0,"Выберите библиотеку")});
        libraryBox = new JComboBox<Genre>(new Genre[]{new Genre(0,"Выберите библиотеку")});
    }

    public void updateConsts(){
        baselibraryBox.removeAllItems();
        libraryBox.removeAllItems();
        baselibraryBox.addItem(new Genre(0,"Выберите библиотеку"));
        libraryBox.addItem(new Genre(0,"Выберите библиотеку"));
        String genreQuery = """
                select id, name
                from libraries
                order by id""";
        try{
            ResultSet ret = window.getSession().executeQuery(genreQuery);
            while(ret.next()){
                baselibraryBox.addItem(new Genre(ret.getInt("id"), ret.getString("name")));
                libraryBox.addItem(new Genre(ret.getInt("id"), ret.getString("name")));

            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            logArea.setText((logArea.getText() + "\nselect genres FATAL ERROR:\n" + e.getMessage()).trim());
            logArea.setForeground(fatalColor);
            LibrariansInfoPanel.revalidate();
            return;
        }
    }

    public void updateData(boolean useFilters, boolean clear) {
        //filters contains: "type_id","attr_id","attr","value","cross"
        String query;
        if (clear){
            logArea.setText("");
            logArea.setForeground(okColor);
        }

        if (!useFilters) {
            query = "select librarians.id, firstname, middlename, lastname, library, libraries.name, libraries.address, hall\n" +
                    "from librarians\n" +
                    "join libraries on libraries.id = library\n" +
                    "order by firstname, middlename, lastname, library, hall";
        }
        else{
            query = "select librarians.id, firstname, middlename, lastname, library, libraries.name, libraries.address, hall\n" +
                    "from librarians\n" +
                    "join libraries on libraries.id = library\n" + getLibrariansFilters("where ") +
                    "order by firstname, middlename, lastname, library, hall";
        }

        System.out.println(query+"\n\n");

        Object[] columnNames = {"id", "Имя", "Отчество", "Фамилия", "libid", "Библиотека", "Зал"};
        DefaultTableModel newModel = new DefaultTableModel(null, columnNames);
        try{
            ResultSet ret = window.getSession().executeQuery(query);
            int totalfound = 0;
            while(ret.next()){
                Object[] rowData = {ret.getInt("id"),ret.getString("firstname"), ret.getString("middlename"), ret.getString("lastname"), ret.getInt("library"), ret.getString("name"), ret.getInt("hall") };
                newModel.addRow(rowData);
                totalfound++;
            }
            logArea.setText((logArea.getText() + "\nOK: " + (totalfound>0?totalfound + " librarians found":"no librarians found")).trim());
            librariansTable.setModel(newModel);
            librariansTable.removeColumn(librariansTable.getColumnModel().getColumn(4));
            librariansTable.removeColumn(librariansTable.getColumnModel().getColumn(0));
            rightPanel.revalidate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            logArea.setText((logArea.getText() + "\nselect librarians FATAL ERROR:\n" + e.getMessage()).trim());
            logArea.setForeground(fatalColor);
        }
    }

    private String getLibrariansFilters(String start){
        String query = "";
        int condCount = (basefnameField.getText().length()>0 ?1:0) +
                (baselnameField.getText().length()>0 ?1:0) +
                (basemnameField.getText().length()>0 ?1:0) +
                (basehallField.getText().length()>0 && basehallField.getText().matches("\\d+")?1:0) +
                (((Genre)baselibraryBox.getSelectedItem()).getId() > 0 ?1:0);
        if (condCount > 0){
            query = query.concat(start);
        }
        int i = 0;

        if (basehallField.getText().length()>0 && !basehallField.getText().matches("\\d+")){
            logArea.setText((logArea.getText()+"\nWARNING: номер зала задан не правильно").trim());
            logArea.setForeground(errorColor);
        }

        if (basefnameField.getText().length()>0){
            query = query.concat("lower(firstname) like \'%"+basefnameField.getText().toLowerCase(Locale.ROOT)+"%\'");
            query = (++i < condCount) ? query.concat(" and\n") : query.concat("\n");
        }
        if (baselnameField.getText().length()>0){
            query = query.concat("lower(firstname) like \'%"+baselnameField.getText().toLowerCase(Locale.ROOT)+"%\'");
            query = (++i < condCount) ? query.concat(" and\n") : query.concat("\n");
        }
        if (basemnameField.getText().length()>0){
            query = query.concat("lower(firstname) like \'%"+basemnameField.getText().toLowerCase(Locale.ROOT)+"%\'");
            query = (++i < condCount) ? query.concat(" and\n") : query.concat("\n");
        }

        if (basehallField.getText().length()>0 && basehallField.getText().matches("\\d+")){
            query = query.concat("hall = " + basehallField.getText());
            query = (++i < condCount) ? query.concat(" and\n") : query.concat("\n");
        }

        if (((Genre)baselibraryBox.getSelectedItem()).getId() > 0){
            query = query.concat("library = "+((Genre)baselibraryBox.getSelectedItem()).getId());
            query = (++i < condCount) ? query.concat(" and\n") : query.concat("\n");
        }

        return query;

    }

    private boolean checkDate(JTextField field){
        String regex = "^\\d{2}\\.\\d{2}\\.\\d{4}$";
        return field.getText().matches(regex);
    }

    public void reset(){
        logArea.setText("");
        logArea.setForeground(okColor);
        updateConsts();
        updateData(false,false);
        librarianId = 0;

        fnameField.setText("");
        lnameField.setText("");
        mnameField.setText("");
        hallField.setText("");
        libraryBox.setSelectedIndex(0);
        basefnameField.setText("");
        baselnameField.setText("");
        basemnameField.setText("");
        basehallField.setText("");
        baselibraryBox.setSelectedIndex(0);

        leftPanel.revalidate();
        LibrariansInfoPanel.revalidate();
    }

    public Container getPanel() {
        return LibrariansInfoPanel;
    }
}
