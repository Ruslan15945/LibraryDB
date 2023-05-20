import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;

public class BorrowFrame extends JPanel{

    private Client window;
    private int readerId = 0;
    private String instanceId = "";

    private HashMap<String, DefaultTableModel> extrainfos = new HashMap<>();

    private int filterMethod = 0;

    private Color okColor = new Color(28, 150, 18);
    private Color errorColor = new Color(153, 147, 22);
    private Color fatalColor = new Color(118, 20, 15);

    public BorrowFrame(Client window) {
        this.window = window;
        BorrowFramePanel.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());

        greetingLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 50));

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

        searchButton.setFont(new Font(Font.DIALOG, Font.BOLD, 30));
        searchButton.setFocusPainted(false);
        searchButton.setContentAreaFilled(false);
        searchButton.setBorder(null);
        searchButton.setAlignmentX(CENTER_ALIGNMENT);

        borrowButton.setFont(new Font(Font.DIALOG, Font.BOLD, 30));
        borrowButton.setFocusPainted(false);
        borrowButton.setContentAreaFilled(false);
        borrowButton.setBorder(null);
        borrowButton.setAlignmentX(CENTER_ALIGNMENT);

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

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateData(false, true);
            }
        });

        borrowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logArea.setText("");
                logArea.setForeground(okColor);
                if (readerId <= 0 || instanceId.equals(""))
                    return;

                if (untilField.getText().length() == 0){
                    logArea.setText("ERROR: дата возврата не указана");
                    logArea.setForeground(fatalColor);
                    return;
                }
                else if (!checkDate(untilField)){
                    logArea.setText("ERROR: дата возврата не в формате DD.MM.YYYY");
                    logArea.setForeground(fatalColor);
                    return;
                }

                String query = "insert into \"BORROWINGS\"(instance, reader, librarian, borrowdate, until) values('"+
                        instanceId +
                        "'," + readerId +
                        "," + window.getLibrarian().getId() +
                        ",TO_DATE('"+ LocalDate.now()+"', 'YYYY-MM-DD')"+
                        ",TO_DATE('"+ untilField.getText() + "', 'DD.MM.YYYY'))";
                try {
                    System.out.println(query);
                    window.getSession().executeUpdate(query);
                    logArea.setText((logArea.getText()+"\nOK: inserted borrowing " + instanceId).trim());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    logArea.setText((logArea.getText()+"\ninsert borrowing FATAL ERROR:\n" + ex.getMessage()).trim());
                    logArea.setForeground(fatalColor);
                    return;
                }
                instanceId = "";
                stocknumField.setText("");
                hallField.setText("");
                rackField.setText("");
                shelfField.setText("");
                editionLinkField.setText("");
                showBorrowButton();

            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateData(true, true);
            }
        });

        readersTable.setShowGrid(false);
        readersTable.setDefaultEditor(Object.class, null);
        readersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = readersTable.rowAtPoint(e.getPoint());
                TableModel model = readersTable.getModel();
                int colCount = model.getColumnCount();
                Object[] rowData = new Object[colCount];
                for (int i = 0; i<colCount; ++i){
                    rowData[i] = model.getValueAt(row, i);
                }

                DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
                setReader((int)rowData[0], (String)rowData[1],(String)rowData[2],(String)rowData[3], (String)rowData[5], df.format((Date)rowData[4]));


            }
        });

        showBorrowButton();

        stocknumField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                setInstance(stocknumField.getText());
            }
        });
        stocknumField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    setInstance(stocknumField.getText());
                }
            }
        });
    }


    private void drawExtraInfos(){

        extraInfoPane.removeAll();
        extraInfoPane.revalidate();
        selectedReaderInfo.revalidate();
        leftPanel.revalidate();
        extraInfoPane.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        int j = 0;
        for ( var i : extrainfos.entrySet()){
            gbc.gridx = 0;
            gbc.gridy = j;
            gbc.anchor = GridBagConstraints.NORTH;
            gbc.fill = GridBagConstraints.WEST;
            gbc.insets = new Insets(5,5,5,3);
            gbc.weightx = 0.0;
            var la = new JLabel(i.getKey());
            la.setAlignmentX(LEFT_ALIGNMENT);
            extraInfoPane.add(la, gbc);

            var tbl = new JTable(){
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            tbl.setRowSelectionAllowed(false);
            tbl.setModel(i.getValue());
            tbl.removeColumn(tbl.getColumnModel().getColumn(1));
            tbl.removeColumn(tbl.getColumnModel().getColumn(0));
            gbc.gridx = 1;
            gbc.gridy = j;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.insets = new Insets(5,3,5,5);
            gbc.weightx = 1.0;
            extraInfoPane.add(tbl, gbc);
            j++;
        }

        extraInfoPane.revalidate();
        selectedReaderInfo.revalidate();
        leftPanel.revalidate();
    }

    public void showBorrowButton(){
        if (readerId > 0 && !instanceId.equals("")){
            borrowButton.setEnabled(true);
        }
        else{
            borrowButton.setEnabled(false);
        }
    }


    private JButton borrowButton;
    private JPanel leftPanel;
    private JPanel selectedReaderInfo;
    private JTextField fnameField;
    private JTextField lnameField;
    private JTextField mnameField;
    private JComboBox sexComboBox;
    private JTextField birthDateField;
    private JScrollPane extrainfoScrollPane;
    private JPanel extraInfoPane;
    private JScrollPane baseFiltersPane;
    private JPanel baseFiltersPanel;
    private JTextField basefnameField;
    private JTextField baselnameField;
    private JTextField basemnameField;
    private JTextField basebirthDateField;
    private JComboBox basesexComboBox;
    private JPanel rightPanel;
    private JPanel readersPanel;
    private JScrollPane readersPane;
    private JTable readersTable;
    private JButton backButton;
    private JLabel greetingLabel;
    private JPanel BorrowFramePanel;
    private JTextField untilField;
    private JTextField hallField;
    private JTextField rackField;
    private JTextField shelfField;
    private JTextField editionLinkField;
    private JTextField stocknumField;
    private JScrollPane selectedInstanceInfo;
    private JButton searchButton;
    private JButton cancelButton;
    private JTextArea logArea;
    private JScrollPane logPane;

    private void createUIComponents() {

        basesexComboBox = new JComboBox<>(new String[]{"Любой", "Мужской", "Женский"});
        sexComboBox = new JComboBox<>(new String[]{"Мужской", "Женский"});

    }

    public JPanel getPanel(){
        return BorrowFramePanel;
    }

    public void setReader(int readerId, String firstname, String middlename, String lastname,  String sex, String birthdate){
        this.readerId = readerId;
        fnameField.setText(firstname);
        lnameField.setText(lastname);
        mnameField.setText(middlename);
        birthDateField.setText(birthdate);
        sexComboBox.setSelectedItem(sex);


        String query = "select rdtypeattr.type as type_id, attr as attr_id, rdtypename.name as type, rdtypeattr.name as attr, value from rdextra\n" +
                "join rdtypeattr on rdextra.attr = rdtypeattr.id\n" +
                "join rdtypename on rdtypeattr.type = rdtypename.type\n" +
                "where reader = " + readerId;

        extrainfos.clear();
        try{
            ResultSet ret = window.getSession().executeQuery(query);
            while(ret.next()){
                if (!extrainfos.containsKey(ret.getString("type"))){
                    extrainfos.put(ret.getString("type"), new DefaultTableModel(null, new String[]{"type_id","attr_id","attr","value"}));
                }
                DefaultTableModel tmodel = extrainfos.get(ret.getString("type"));
                Object[] attrval = {ret.getInt("type_id"), ret.getInt("attr_id"), ret.getString("attr"), ret.getString("value")};
                tmodel.addRow(attrval);
            }
            drawExtraInfos();
        }
        catch (SQLException exp) {
            exp.printStackTrace();
        }

        showBorrowButton();
    }

    public void setInstance(String instId){
        String iquery = "select instances.stocknum, editions.id editionid, publisher edition, works.id workid, label work, hall, rack, shelf, entrydate, retiredate,\n" +
                "  CASE \n" +
                "    WHEN stocknum IN (\n" +
                "      select distinct instance\n" +
                "      from borrowings\n" +
                "      where returned = 0\n" +
                "    ) THEN 0\n" +
                "    ELSE 1\n" +
                "  END AS available\n" +
                "from instances\n" +
                "join editions on editions.id = edition\n" +
                "join works on works.id = work\n" +
                "where instances.stocknum like '%" + instId + "%'\n" +
                "order by workid, editionid, hall, rack, shelf";

        try{
            ResultSet ret = window.getSession().executeQuery(iquery);
            boolean found = false;
            while(ret.next()){
                if (ret.getInt("available") == 1 && ret.getDate("retiredate") == null){
                    editionLinkField.setText(ret.getString("edition") + "(" + ret.getString("work") +")");
                    hallField.setText(String.valueOf(ret.getInt("hall")));
                    rackField.setText(String.valueOf(ret.getInt("rack")));
                    shelfField.setText(String.valueOf(ret.getInt("shelf")));
                    instanceId = ret.getString("stocknum");
                    stocknumField.setText(instanceId);
                    found = true;
                }
            }
            if (!found){
                editionLinkField.setText("");
                hallField.setText("");
                rackField.setText("");
                shelfField.setText("");
                instanceId = "";
            }
            leftPanel.revalidate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        showBorrowButton();

    }

    private boolean checkDate(JTextField field){
        String regex = "^\\d{2}\\.\\d{2}\\.\\d{4}$";
        return field.getText().matches(regex);
    }

    public void updateData(boolean useFilters, boolean clear) {
        //filters contains: "type_id","attr_id","attr","value","cross"
        if (clear){
            logArea.setText("");
            logArea.setForeground(okColor);
        }
        String query;
        if (!useFilters) {
            filterMethod = 0;
            query = "select readers.id, firstname, middlename, lastname, birthdate, sexes.name sex, libraries.name library, libraries.address, regdate\n" +
                    "from readers\n" +
                    "join sexes on sexes.id = sex\n" +
                    "join libraries on libraries.id = origlib\n" +
                    "order by firstname, middlename, lastname, birthdate";
        }
        else{

            filterMethod=1;
            query = "select readers.id, firstname, middlename, lastname, birthdate, sexes.name sex, libraries.name library, libraries.address, regdate\nfrom readers\n";

            query = query +
                    "join libraries on libraries.id = origlib\n" +
                    "join sexes on sexes.id = sex\n" +
                    getReadersFilters("where ") +
                    "order by firstname, middlename, lastname, birthdate";
        }

        System.out.println(query+"\n\n");

        Object[] columnNames = {"id","Имя", "Отчество", "Фамилия", "Дата рождения", "Пол", "Дата регистрации"};
        DefaultTableModel newModel = new DefaultTableModel(null, columnNames);
        try{
            ResultSet ret = window.getSession().executeQuery(query);
            int totalfound = 0;
            while(ret.next()){
                Object[] rowData = {ret.getInt("id"),ret.getString("firstname"), ret.getString("middlename"), ret.getString("lastname"), ret.getDate("birthdate"), ret.getString("sex"), ret.getDate("regdate")};
                newModel.addRow(rowData);
                totalfound++;
            }
            logArea.setText((logArea.getText() + "\nOK: " + (totalfound>0?totalfound + " users found":"no users found")).trim());
            readersTable.setModel(newModel);
            readersTable.removeColumn(readersTable.getColumnModel().getColumn(0));
            rightPanel.revalidate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            logArea.setText((logArea.getText() + "\nselect readers FATAL ERROR:\n" + e.getMessage()).trim());
            logArea.setForeground(fatalColor);
        }
    }


    private String getReadersFilters(String start){
        String query = "";
        int condCount = (basefnameField.getText().length()>0 ?1:0) +
                (baselnameField.getText().length()>0 ?1:0) +
                (basemnameField.getText().length()>0 ?1:0) +
                (basebirthDateField.getText().length()>0 && checkDate(basebirthDateField)?1:0) +
                (basesexComboBox.getSelectedItem().equals("Мужской") || basesexComboBox.getSelectedItem().equals("Женский")? 1: 0);
        if (basebirthDateField.getText().length()>0 && !checkDate(basebirthDateField)){
            logArea.setText((logArea.getText() + "\nWARNING: дата рождения не формате DD.MM.YYYY").trim());
            logArea.setForeground(errorColor);
        }
        if (condCount > 0){
            query = query.concat(start);
        }
        int i = 0;

        if (basefnameField.getText().length()>0){
            query = query.concat("lower(firstname) like \'%"+basefnameField.getText().toLowerCase(Locale.ROOT)+"%\'");
            query = (++i < condCount) ? query.concat(" and\n") : query.concat("\n");
        }

        if (baselnameField.getText().length()>0){
            query = query.concat("lower(lastname) like \'%"+baselnameField.getText().toLowerCase(Locale.ROOT)+"%\'");
            query = (++i < condCount) ? query.concat(" and\n") : query.concat("\n");
        }

        if (basemnameField.getText().length()>0){
            query = query.concat("lower(middlename) like \'%"+basemnameField.getText().toLowerCase(Locale.ROOT)+"%\'");
            query = (++i < condCount) ? query.concat(" and\n") : query.concat("\n");
        }

        if (basebirthDateField.getText().length()>0 && checkDate(basebirthDateField)){
            query = query.concat("birthdate = TO_DATE('"+basebirthDateField.getText()+"', 'DD.MM.YYYY')");
            query = (++i < condCount) ? query.concat(" and\n") : query.concat("\n");
        }

        if (basesexComboBox.getSelectedItem().equals("Мужской") || basesexComboBox.getSelectedItem().equals("Женский")){
            query = query.concat("sex = "+(basesexComboBox.getSelectedItem().equals("Мужской")?1:0));
            query = (++i < condCount) ? query.concat(" and\n") : query.concat("\n");
        }

        return query;

    }

    public void reset(){
        readerId = 0;
        fnameField.setText("");
        lnameField.setText("");
        mnameField.setText("");
        birthDateField.setText("");
        sexComboBox.setSelectedIndex(0);

        basefnameField.setText("");
        basemnameField.setText("");
        baselnameField.setText("");
        basebirthDateField.setText("");
        basesexComboBox.setSelectedIndex(0);

        instanceId = "";
        stocknumField.setText("");
        editionLinkField.setText("");
        hallField.setText("");
        rackField.setText("");
        shelfField.setText("");

        untilField.setText("");

        extrainfos.clear();
        drawExtraInfos();

        updateData(false, true);
    }



}


