import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
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

public class ReadersInfo extends JPanel{

    private Client window;
    private int readerId = 0;

    private HashMap<String, DefaultTableModel> extrainfos = new HashMap<>();
    private HashMap<String, DefaultTableModel> newextrainfos = new HashMap<>();
    private HashMap<String, DefaultTableModel> deletedextrainfos = new HashMap<>();
    private HashMap<String, DefaultTableModel> activeFilters = new HashMap<>();

    private boolean filterSelection = true;
    private int filterMethod = 0;

    private Color okColor = new Color(28, 150, 18);
    private Color errorColor = new Color(153, 147, 22);
    private Color fatalColor = new Color(118, 20, 15);

    public ReadersInfo(Client window){
        this.window = window;
        ReadersInfoPanel.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());

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

        addReaderButton.setFont(new Font(Font.DIALOG, Font.BOLD, 30));
        addReaderButton.setFocusPainted(false);
        addReaderButton.setContentAreaFilled(false);
        addReaderButton.setBorder(null);
        addReaderButton.setAlignmentX(CENTER_ALIGNMENT);

        addFilterButton.setFont(new Font(Font.DIALOG, Font.BOLD, 30));
        addFilterButton.setFocusPainted(false);
        addFilterButton.setContentAreaFilled(false);
        addFilterButton.setBorder(null);
        addFilterButton.setAlignmentX(CENTER_ALIGNMENT);

        applyFiltersButton.setFont(new Font(Font.DIALOG, Font.BOLD, 30));
        applyFiltersButton.setFocusPainted(false);
        applyFiltersButton.setContentAreaFilled(false);
        applyFiltersButton.setBorder(null);
        applyFiltersButton.setAlignmentX(CENTER_ALIGNMENT);

        addReaderInfo = new JButton("+");
        addReaderInfo.setFont(new Font(Font.DIALOG, Font.BOLD, 30));
        addReaderInfo.setMargin(new Insets(0,0,0,0));
        addReaderInfo.setFocusPainted(false);
        addReaderInfo.setContentAreaFilled(false);
        addReaderInfo.setBorder(null);
        addReaderInfo.setAlignmentX(CENTER_ALIGNMENT);

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

        statsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            private final Color redColor = new Color(254, 157, 162, 133);
            private final Color purpleColor = new Color(202, 157, 254, 125);
            private final Color whiteColor = Color.WHITE;

            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if ((int)table.getModel().getValueAt(row, 5) == 1) {
                    c.setBackground(redColor);
                }
                else if ((int)table.getModel().getValueAt(row, 2) == 0) {
                    c.setBackground(purpleColor);
                }
                else {
                    c.setBackground(whiteColor);
                }
                return c;
            }
        });

        statButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFiltersVisible(false);
                logArea.setText("");
                logArea.setForeground(okColor);
                String query = "select instance, publisher, year, label, returned, borrowdate, until,\n" +
                        "  CASE \n" +
                        "    WHEN returned = 0\n" +
                        "    and until < CURRENT_DATE\n" +
                        "    THEN 1\n" +
                        "    ELSE 0\n" +
                        "  END AS debt\n" +
                        "from borrowings\n" +
                        "join instances on instances.stocknum = borrowings.instance\n" +
                        "join editions on editions.id = edition\n" +
                        "join works on works.id = work\n" +
                        (originLibCheckBox.isSelected() || foreignLibCheckBox.isSelected() ? "join readers on readers.id = reader\n" : "") +
                        "where reader = " + readerId + "\n" +
                        (statFromField.getText().length()>0 && checkDate(statFromField)
                                ? "and borrowdate > TO_DATE('" + statFromField.getText() + "', 'DD.MM.YYYY')\n"
                                : "") +
                        (statToField.getText().length()>0 && checkDate(statToField)
                                ? "and borrowdate < TO_DATE('" + statToField.getText() + "', 'DD.MM.YYYY')\n"
                                : "") +
                        (originLibCheckBox.isSelected()
                                ? "and instances.library = readers.origlib\n"
                                :(foreignLibCheckBox.isSelected()
                                ? "and instances.library != readers.origlib\n"
                                : "")) +
                        "order by returned, borrowdate";


                Object[] columnNames = {"Экземпляр","Произведение","returned","Дата выдачи", "Выдано до", "Долг"};
                DefaultTableModel newModel = new DefaultTableModel(null, columnNames);

                try{
                    ResultSet ret = window.getSession().executeQuery(query);
                    int totalfound = 0;
                    while(ret.next()){
                        Object[] rowData = {ret.getString("instance"), ret.getString("label") + " (" + ret.getString("publisher") + ")", ret.getInt("returned"), ret.getDate("borrowdate"), ret.getDate("until"), ret.getInt("debt")};
                        newModel.addRow(rowData);
                        totalfound++;
                    }
                    logArea.setText((logArea.getText() + "\nOK: " + (totalfound>0?totalfound + " borrowings found":"no borrowings found")).trim());
                    statsTable.setModel(newModel);
                    statsTable.removeColumn(statsTable.getColumnModel().getColumn(5));
                    statsTable.removeColumn(statsTable.getColumnModel().getColumn(2));
                }
                catch (SQLException ex) {
                    ex.printStackTrace();
                    logArea.setText((logArea.getText() + "\nselect borrowings FATAL ERROR:\n" + ex.getMessage()).trim());
                    logArea.setForeground(fatalColor);
                }

                StatsPanel.setVisible(true);
                leftPanel.revalidate();
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
                readerId = (int)rowData[0];
                fnameField.setText((String)rowData[1]);
                mnameField.setText((String)rowData[2]);
                lnameField.setText((String)rowData[3]);
                sexComboBox.setSelectedItem(rowData[5]);
                DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
                birthDateField.setText((df.format((Date)rowData[4])));

                String query = "select rdtypeattr.type as type_id, attr as attr_id, rdtypename.name as type, rdtypeattr.name as attr, value from rdextra\n" +
                        "join rdtypeattr on rdextra.attr = rdtypeattr.id\n" +
                        "join rdtypename on rdtypeattr.type = rdtypename.type\n" +
                        "where reader = " + readerId;

                extrainfos.clear();
                deletedextrainfos.clear();
                newextrainfos.clear();
                try{
                    ResultSet ret = window.getSession().executeQuery(query);
                    while(ret.next()){
                        if (!extrainfos.containsKey(ret.getString("type"))){
                            extrainfos.put(ret.getString("type"), new DefaultTableModel(null, new String[]{"type_id","attr_id","attr","value","cross"}));
                        }
                        DefaultTableModel tmodel = extrainfos.get(ret.getString("type"));
                        Object[] attrval = {ret.getInt("type_id"), ret.getInt("attr_id"), ret.getString("attr"), ret.getString("value"), "×"};
                        tmodel.addRow(attrval);
                    }
                    drawExtraInfos();
                }
                catch (SQLException exp) {
                    exp.printStackTrace();
                }

                removeReaderButton.setVisible(true);
                setFiltersVisible(true);
                StatsPanel.setVisible(false);
                statButton.setVisible(true);
                saveInfoButton.setVisible(true);
                cancelRdButton.setVisible(true);
                addRdButton.setVisible(false);
                selectedReaderInfo.setVisible(true);

            }
        });


        addReaderInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAttributes();
                filterSelection = false;
            }
        });

        selectedReaderInfo.setVisible(false);

        saveInfoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logArea.setText("");
                logArea.setForeground(okColor);
                if (!checkFilling()){
                    return;
                }
                try {
                    String query = "update readers set firstname = \'" + fnameField.getText() +
                            "\', middlename = \'" + mnameField.getText() +
                            "\', lastname = \'" + lnameField.getText() +
                            "\', sex = " + (sexComboBox.getSelectedItem().equals("Мужской") ? 1: 0) +
                            ", birthdate = TO_DATE(\'" + birthDateField.getText() + "\', 'DD/MM/YYYY')" +
                            "where id = " + readerId;
                    int ret = window.getSession().executeUpdate(query);
                    logArea.setText((logArea.getText()+"\nOK: updated reader " + readerId).trim());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    logArea.setText((logArea.getText()+"\nupdate reader FATAL ERROR:\n" + ex.getMessage()).trim());
                    logArea.setForeground(fatalColor);
                    return;
                }

                boolean ok = true;
                int addedextra = 0;
                for(var i:extrainfos.entrySet()){
                    for (var v: i.getValue().getDataVector()){
                        if (v.get(3).equals("")){
                            logArea.setText((logArea.getText()+"\nWARNING: \"" + v.get(2) + "\" attribute is empty").trim());
                            logArea.setForeground(errorColor);
                            ok = false;
                            continue;
                        }
                        String query = "update rdextra set value = \'"+ v.get(3) +
                            "\' where\n" +
                            "reader = " + readerId + " and attr = " + v.get(1);
                        try {
                            int ret = window.getSession().executeUpdate(query);
                            addedextra++;
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            logArea.setText((logArea.getText() + "\ninsert rdextra FATAL ERROR:\n" + ex.getMessage()).trim());
                            logArea.setForeground(fatalColor);
                            ok = false;
                        }
                    }
                }
                if (ok && addedextra>0){
                    logArea.setText((logArea.getText() + "\nOK: updated extra info about the reader").trim());
                }


                ok = true;
                addedextra = 0;
                for(var i:deletedextrainfos.entrySet()){
                    for (var v: i.getValue().getDataVector()){

                        String query = "delete from rdextra where\n" +
                                "reader = " + readerId + " and attr = " + v.get(1);
                        try {
                            int ret = window.getSession().executeUpdate(query);
                            addedextra++;
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            logArea.setText((logArea.getText() + "\ndelete rdextra FATAL ERROR:\n" + ex.getMessage()).trim());
                            logArea.setForeground(fatalColor);
                            ok = false;
                        }
                    }
                }
                if (ok && addedextra>0){
                    logArea.setText((logArea.getText() + "\nOK: deleted extra info about the reader").trim());
                }


                ok = true;
                addedextra = 0;
                for(var i:newextrainfos.entrySet()){
                    for (var v: i.getValue().getDataVector()){
                        if (v.get(3).equals("")){
                            logArea.setText((logArea.getText()+"\nWARNING: \"" + v.get(2) + "\" attribute is empty").trim());
                            logArea.setForeground(errorColor);
                            ok = false;
                            continue;
                        }
                        String query = "insert into rdextra(reader,attr,value) values("+readerId +
                                "," + v.get(1) + ",\'"+v.get(3)+"\')";
                        try {
                            int ret = window.getSession().executeUpdate(query);
                            addedextra++;
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            logArea.setText((logArea.getText() + "\ninsert rdextra FATAL ERROR:\n" + ex.getMessage()).trim());
                            logArea.setForeground(fatalColor);
                            ok = false;
                        }
                    }
                }
                if (ok && addedextra>0){
                    logArea.setText((logArea.getText() + "\nOK: added extra info about the reader").trim());
                }


                readerId=0;
                extrainfos.clear();
                deletedextrainfos.clear();
                newextrainfos.clear();
                updateData(filterMethod!=0, false);
                setFiltersVisible(true);
                StatsPanel.setVisible(false);
                selectedReaderInfo.setVisible(false);
                leftPanel.revalidate();
            }
        });
        attributesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //attributesTable.requestFocus();
                int row = attributesTable.rowAtPoint(e.getPoint());
                TableModel model = attributesTable.getModel();
                int colCount = model.getColumnCount();
                Object[] rowData = new Object[colCount];
                for (int i = 0; i<colCount; ++i){
                    rowData[i] = model.getValueAt(row, i);
                }
                if (filterSelection) {
                    if (activeFilters.containsKey((String)rowData[2])){
                        for (var i:activeFilters.get((String)rowData[2]).getDataVector()){
                            if(i.elementAt(1).toString().equals(String.valueOf(rowData[1]))){
                                return;
                            }
                        }
                    }
                    if (!activeFilters.containsKey((String)rowData[2])){
                        activeFilters.put((String)rowData[2], new DefaultTableModel(null, new String[]{"type_id","attr_id","attr","value","cross"}));
                    }
                    DefaultTableModel tmodel = activeFilters.get((String)rowData[2]);
                    Object[] attrval = {rowData[0], rowData[1], rowData[3], "","×"};
                    tmodel.addRow(attrval);

                    drawFilters();

                    return;
                }
                //else
                if (extrainfos.containsKey((String)rowData[2])){
                    for (var i:extrainfos.get((String)rowData[2]).getDataVector()){
                        if(i.elementAt(1).toString().equals(String.valueOf(rowData[1]))){
                            return;
                        }
                    }
                }
                if (!newextrainfos.containsKey((String)rowData[2])){
                    newextrainfos.put((String)rowData[2], new DefaultTableModel(null, new String[]{"type_id","attr_id","attr","value","cross"}));
                }
                DefaultTableModel tmodel = newextrainfos.get((String)rowData[2]);
                Object[] attrval = {rowData[0], rowData[1], rowData[3], "","×"};
                tmodel.addRow(attrval);

                drawExtraInfos();

            }
        });
        cancelAttrButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attributesPanel.setVisible(false);
                readersPanel.setVisible(true);
                rightPanel.revalidate();
            }
        });
        addFilterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAttributes();
                filterSelection = true;
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
        addReaderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                readerId=0;
                deletedextrainfos.clear();
                setFiltersVisible(true);
                StatsPanel.setVisible(false);
                statButton.setVisible(false);
                extrainfos.clear();
                newextrainfos.clear();
                fnameField.setText("");
                mnameField.setText("");
                lnameField.setText("");
                birthDateField.setText("");
                attributesPanel.setVisible(false);
                removeReaderButton.setVisible(false);
                saveInfoButton.setVisible(false);
                cancelRdButton.setVisible(true);
                addRdButton.setVisible(true);
                readersPanel.setVisible(true);
                selectedReaderInfo.setVisible(true);
                drawExtraInfos();
            }
        });
        cancelRdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logArea.setText("");
                readerId=0;
                setFiltersVisible(true);
                StatsPanel.setVisible(false);
                extrainfos.clear();
                deletedextrainfos.clear();
                newextrainfos.clear();
                attributesPanel.setVisible(false);
                readersPanel.setVisible(true);
                rightPanel.revalidate();
                selectedReaderInfo.setVisible(false);
                leftPanel.revalidate();
                ReadersInfoPanel.revalidate();
            }
        });
        addRdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logArea.setText("");
                logArea.setForeground(okColor);
                if (!checkFilling()){
                    return;
                }

                try {
                    String query = "insert into \"READERS\"(firstname, middlename, lastname, birthdate, sex, origlib, regdate) values('" + fnameField.getText() +
                            "','" + mnameField.getText() +
                            "','" + lnameField.getText() +
                            "',TO_DATE('" + birthDateField.getText() + "', 'DD.MM.YYYY')" +
                            "," + (sexComboBox.getSelectedItem().equals("Мужской") ? 1: 0) +
                            ","+window.getLibrarian().getLibrary()+",TO_DATE('"+LocalDate.now()+"', 'YYYY-MM-DD'))";
                    readerId = (int)window.getSession().executeRetQuery(query);
                    logArea.setText((logArea.getText()+"\nOK: added reader " + readerId).trim());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    logArea.setText((logArea.getText() + "\ninsert reader FATAL ERROR:\n" + ex.getMessage()).trim());
                    logArea.setForeground(fatalColor);
                    ReadersInfoPanel.revalidate();
                    return;
                }


                boolean ok = true;
                int addedextra = 0;
                for(var i:newextrainfos.entrySet()){
                    for (var v: i.getValue().getDataVector()){
                        if (v.get(3).equals("")){
                            logArea.setText((logArea.getText()+"\nWARNING: \"" + v.get(2) + "\" attribute is empty").trim());
                            logArea.setForeground(errorColor);
                            ok = false;
                            continue;
                        }
                        String query = "insert into rdextra(reader,attr,value) values("+readerId +
                                "," + v.get(1) + ",\'"+v.get(3)+"\')";
                        try {
                            int ret = window.getSession().executeUpdate(query);
                            addedextra++;
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            logArea.setText((logArea.getText() + "\ninsert rdextra FATAL ERROR:\n" + ex.getMessage()).trim());
                            logArea.setForeground(fatalColor);
                            ok = false;
                        }
                    }
                }
                if (ok && addedextra>0){
                    logArea.setText((logArea.getText() + "\nOK: added extra info about the reader").trim());
                }
                readerId=0;
                extrainfos.clear();
                deletedextrainfos.clear();
                newextrainfos.clear();
                updateData(filterMethod!=0, false);
                setFiltersVisible(true);
                StatsPanel.setVisible(false);
                selectedReaderInfo.setVisible(false);
                leftPanel.revalidate();
            }
        });
        removeReaderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query1 = "delete from rdextra where\n"+
                        "reader = "+ readerId;
                String query2 = "delete from readers where\n" +
                        "id = " + readerId;
                try {
                    int ret = window.getSession().executeUpdate(query1);
                    ret = window.getSession().executeUpdate(query2);
                    logArea.setText("OK: deleted reader " + readerId);
                    logArea.setForeground(okColor);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    logArea.setText("delete reader FATAL ERROR:\n" + ex.getMessage());
                    logArea.setForeground(fatalColor);
                    ReadersInfoPanel.revalidate();
                    return;
                }
                readerId=0;
                extrainfos.clear();
                deletedextrainfos.clear();
                newextrainfos.clear();
                updateData(filterMethod!=0, false);
                setFiltersVisible(true);
                StatsPanel.setVisible(false);
                selectedReaderInfo.setVisible(false);
                leftPanel.revalidate();
                ReadersInfoPanel.revalidate();
            }
        });

        closeStatsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFiltersVisible(true);
                StatsPanel.setVisible(false);
                leftPanel.revalidate();
            }
        });
        originLibCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    foreignLibCheckBox.setSelected(false);
                }
            }
        });
        foreignLibCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    originLibCheckBox.setSelected(false);
                }
            }
        });
    }

    private void showAttributes(){
        readersPanel.setVisible(false);
        String query = "select rdtypename.type as type_id, rdtypeattr.id as attr_id, rdtypename.name as type, rdtypeattr.name as attr from rdtypeattr\n" +
                "join rdtypename on rdtypeattr.type = rdtypename.type";

        Object[] columnNames = {"type_id","attr_id", "Тип читателя", "Атрибут"};
        DefaultTableModel newModel = new DefaultTableModel(null, columnNames);
        try{
            ResultSet ret = window.getSession().executeQuery(query);
            while(ret.next()){
                Object[] rowData = {ret.getInt("type_id"),ret.getString("attr_id"), ret.getString("type"), ret.getString("attr")};
                newModel.addRow(rowData);
            }
            attributesTable.setModel(newModel);
            attributesTable.removeColumn(attributesTable.getColumnModel().getColumn(1));
            attributesTable.removeColumn(attributesTable.getColumnModel().getColumn(0));

            attributesPanel.setVisible(true);
            rightPanel.revalidate();
        }
        catch (SQLException exp) {
            exp.printStackTrace();
        }
    }

    private void drawFilters() {
        FiltersPanel.removeAll();
        FiltersPanel.revalidate();
        leftPanel.revalidate();
        FiltersPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        int j = 0;
        for ( var i : activeFilters.entrySet()){
            gbc.gridx = 0;
            gbc.gridy = j;
            gbc.anchor = GridBagConstraints.NORTH;
            gbc.fill = GridBagConstraints.WEST;
            gbc.insets = new Insets(5,5,5,3);
            gbc.weightx = 0.0;
            var la = new JLabel(i.getKey());
            la.setAlignmentX(LEFT_ALIGNMENT);
            FiltersPanel.add(la, gbc);

            var tbl = new JTable(){
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 1;
                }
            };
            tbl.setRowSelectionAllowed(false);
            tbl.setModel(i.getValue());
            tbl.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    super.focusLost(e);
                    if (tbl.isEditing()) {
                        tbl.getCellEditor().stopCellEditing();
                    }
                }
            });
            tbl.removeColumn(tbl.getColumnModel().getColumn(1));
            tbl.removeColumn(tbl.getColumnModel().getColumn(0));
            tbl.getColumn("cross").setMaxWidth(7);

            tbl.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (tbl.columnAtPoint(e.getPoint()) == 2) {
                        ((DefaultTableModel) tbl.getModel()).removeRow(tbl.rowAtPoint(e.getPoint()));
                        if (tbl.getRowCount() == 0) {
                            activeFilters.remove(i.getKey());
                            la.setVisible(false);
                            tbl.setVisible(false);
                            FiltersPanel.remove(la);
                            FiltersPanel.remove(tbl);
                        }
                        FiltersPanel.revalidate();
                        leftPanel.revalidate();
                    }
                }
            });
            gbc.gridx = 1;
            gbc.gridy = j;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.insets = new Insets(5,3,5,5);
            gbc.weightx = 1.0;
            FiltersPanel.add(tbl, gbc);
            j++;
        }
        FiltersPanel.revalidate();
        leftPanel.revalidate();
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
                    return column == 1;
                }
            };
            tbl.setRowSelectionAllowed(false);
            tbl.setModel(i.getValue());
            tbl.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    super.focusLost(e);
                    if (tbl.isEditing()) {
                        tbl.getCellEditor().stopCellEditing();
                    }
                }
            });
            tbl.removeColumn(tbl.getColumnModel().getColumn(1));
            tbl.removeColumn(tbl.getColumnModel().getColumn(0));
            tbl.getColumn("cross").setMaxWidth(7);
            tbl.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (tbl.columnAtPoint(e.getPoint()) == 2){
                        if (!deletedextrainfos.containsKey(i.getKey())){
                            deletedextrainfos.put(i.getKey(), new DefaultTableModel(null, new String[]{"type_id","attr_id","attr","value","cross"}));
                        }
                        int r = tbl.rowAtPoint(e.getPoint());
                        deletedextrainfos.get(i.getKey()).addRow(((DefaultTableModel) tbl.getModel()).getDataVector().get(r));
                        ((DefaultTableModel)tbl.getModel()).removeRow(r);
                        if (tbl.getRowCount() == 0){
                            extrainfos.remove(i.getKey());
                            la.setVisible(false);
                            tbl.setVisible(false);
                            extraInfoPane.remove(la);
                            extraInfoPane.remove(tbl);
                            extraInfoPane.revalidate();
                            selectedReaderInfo.revalidate();
                            leftPanel.revalidate();
                        }
                    }
                }
            });
            gbc.gridx = 1;
            gbc.gridy = j;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.insets = new Insets(5,3,5,5);
            gbc.weightx = 1.0;
            extraInfoPane.add(tbl, gbc);
            j++;
        }
        gbc.gridy = j;
        gbc.weightx = 0.0;
        gbc.gridx = 0;
        gbc.insets = new Insets(5,10,5,10);
        extraInfoPane.add(new JLabel("Добавить информацию"), gbc);
        gbc.gridx = 1;
        extraInfoPane.add(addReaderInfo,gbc);
        gbc.insets = null;

        j++;
        for (var i: newextrainfos.entrySet()) {
            gbc.gridx = 0;
            gbc.gridy = j;
            gbc.anchor = GridBagConstraints.NORTH;
            gbc.fill = GridBagConstraints.WEST;
            gbc.insets = new Insets(5, 5, 5, 3);
            gbc.weightx = 0.0;
            var la = new JLabel(i.getKey());
            la.setAlignmentX(LEFT_ALIGNMENT);
            extraInfoPane.add(la, gbc);

            var tbl = new JTable(){
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 1;
                }
            };
            tbl.setRowSelectionAllowed(false);
            tbl.setModel(newextrainfos.get(i.getKey()));
            tbl.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    super.focusLost(e);
                    if (tbl.isEditing()) {
                        tbl.getCellEditor().stopCellEditing();
                    }
                }
            });
            tbl.removeColumn(tbl.getColumnModel().getColumn(1));
            tbl.removeColumn(tbl.getColumnModel().getColumn(0));
            tbl.getColumn("cross").setMaxWidth(7);
            tbl.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (tbl.columnAtPoint(e.getPoint()) == 2) {
                        ((DefaultTableModel) tbl.getModel()).removeRow(tbl.rowAtPoint(e.getPoint()));
                        if (tbl.getRowCount() == 0) {
                            newextrainfos.remove(i.getKey());
                            la.setVisible(false);
                            tbl.setVisible(false);
                            extraInfoPane.remove(la);
                            extraInfoPane.remove(tbl);
                            extraInfoPane.revalidate();
                        }
                    }
                }
            });
            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.insets = new Insets(5, 3, 5, 5);
            gbc.weightx = 1.0;
            extraInfoPane.add(tbl, gbc);
            j++;
        }

        extraInfoPane.revalidate();
        selectedReaderInfo.revalidate();
        leftPanel.revalidate();
    }

    private JTable readersTable;
    private JButton addFilterButton;
    private JButton applyFiltersButton;
    private JButton addReaderButton;
    private JButton addReaderInfo;
    private JButton backButton;
    private JButton cancelButton;
    private JPanel ReadersInfoPanel;
    private JLabel filtersLabel;
    private JLabel greetingLabel;
    private JTextField fnameField;
    private JTextField mnameField;
    private JTextField lnameField;
    private JButton removeReaderButton;
    private JButton saveInfoButton;
    private JComboBox sexComboBox;
    private JTextField birthDateField;
    private JPanel selectedReaderInfo;
    private JPanel extraInfoPane;
    private JTable attributesTable;
    private JScrollPane attributesPane;
    private JScrollPane readersPane;
    private JPanel rightPanel;
    private JPanel readersPanel;
    private JPanel attributesPanel;
    private JButton cancelAttrButton;
    private JScrollPane extrainfoScrollPane;
    private JPanel leftPanel;
    private JScrollPane FiltersPane;
    private JPanel FiltersPanel;
    private JButton addRdButton;
    private JButton cancelRdButton;
    private JScrollPane baseFiltersPane;
    private JPanel baseFiltersPanel;
    private JTextField basefnameField;
    private JTextField baselnameField;
    private JTextField basemnameField;
    private JTextField basebirthDateField;
    private JComboBox basesexComboBox;
    private JTextArea logArea;
    private JTextField lastVisitField;
    private JScrollPane logPane;
    private JPanel StatsPanel;
    private JTextField statFromField;
    private JTextField statToField;
    private JButton closeStatsButton;
    private JTable statsTable;
    private JPanel FiltersLabel;
    private JButton statButton;
    private JPanel ApplyPanel;
    private JCheckBox originLibCheckBox;
    private JCheckBox foreignLibCheckBox;
    private JCheckBox debtorsCheckBox;

    private void createUIComponents() {

        basesexComboBox = new JComboBox<>(new String[]{"Любой", "Мужской", "Женский"});
        sexComboBox = new JComboBox<>(new String[]{"Мужской", "Женский"});

    }

    public JPanel getPanel(){
        return ReadersInfoPanel;
    }

    public void updateData(boolean useFilters, boolean clear) {
        //filters contains: "type_id","attr_id","attr","value","cross"
        String query;
        if (clear){
            logArea.setText("");
            logArea.setForeground(okColor);
        }

        if (!useFilters) {
            filterMethod = 0;
            query = "select readers.id, firstname, middlename, lastname, birthdate, sexes.name sex, libraries.name library, libraries.address, regdate\n" +
                    "from readers\n" +
                    "join sexes on sexes.id = sex\n" +
                    "join libraries on libraries.id = origlib\n" +
                    "order by firstname, middlename, lastname, birthdate";
        }
        else{

            if (lastVisitField.getText().length()>0 && !checkDate(lastVisitField)){
                logArea.setText((logArea.getText() + "\nWARNING: дата последнего посещения не формате DD.MM.YYYY").trim());
                logArea.setForeground(errorColor);
            }

            filterMethod=1;
            query = "select readers.id, firstname, middlename, lastname, birthdate, sexes.name sex, libraries.name library, libraries.address, regdate\n from " +
            (lastVisitField.getText().length()>0 && checkDate(lastVisitField)
                    ? "(\n" +
                    "  select reader, MAX(dateofvisit) \"LASTVISIT\"\n" +
                    "  from (\n" +
                    "    select reader, borrowdate \"DATEOFVISIT\"\n" +
                    "    from borrowings\n" +
                    "    union all\n" +
                    "    select reader, returndate \"DATEOFVISIT\"\n" +
                    "    from returns\n" +
                    "    join borrowings on borrowings.id = returns.borrowing\n" +
                    "  )\n" +
                    "  group by reader\n" +
                    ")\n" +
                    "right join readers on readers.id = reader\n"
                    : "readers\n"
            );
            if (activeFilters.size()>0) {
                query = query + "join (select reader \"RD\" from (\n";
                int fn = 0;
                for (var i : activeFilters.entrySet()) {
                    for (var f : i.getValue().getDataVector()) {
                        query = query.concat((fn == 0 ? "  " : "  intersect\n  ") + "select reader from rdextra where attr = " + f.get(1) + " and lower(value) like '%" + f.get(3).toString().toLowerCase(Locale.ROOT) + "%'\n");
                        ++fn;
                    }
                }
                query = query + ")\n) on \"RD\" = readers.id\n";
            }
            if (debtorsCheckBox.isSelected()) {
                query = query + "join (select reader \"RDd\" from borrowings\n" +
                        "  where borrowings.returned = 0 and borrowings.until <= CURRENT_DATE \n) on \"RDd\" = readers.id\n";
            }

            String ifilters = getReadersFilters("where ");
            ifilters = ifilters + (lastVisitField.getText().length()>0 && checkDate(lastVisitField)
                    ? ((ifilters.length() > 0 ? "and ": "where ") + "(lastvisit is NULL or lastvisit < TO_DATE('" + lastVisitField.getText()+"', 'DD.MM.YYYY'))\n")
                    : "");
            query = query +
                    "join libraries on libraries.id = origlib\n" +
                    "join sexes on sexes.id = sex\n" +
                    ifilters +
                    "order by firstname, middlename, lastname, birthdate";
        }

        System.out.println(query+"\n\n");

        Object[] columnNames = {"id","Имя", "Отчество", "Фамилия", "Дата рождения","пол","Дата регистрации"};
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
            attributesPanel.setVisible(false);
            readersPanel.setVisible(true);
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
                query = query.concat("birthdate = TO_DATE('" + basebirthDateField.getText() + "', 'DD.MM.YYYY')");
                query = (++i < condCount) ? query.concat(" and\n") : query.concat("\n");
        }

        if (basesexComboBox.getSelectedItem().equals("Мужской") || basesexComboBox.getSelectedItem().equals("Женский")){
            query = query.concat("sex = "+(basesexComboBox.getSelectedItem().equals("Мужской")?1:0));
            query = (++i < condCount) ? query.concat(" and\n") : query.concat("\n");
        }

        return query;

    }

    private boolean checkDate(JTextField field){
        String regex = "^\\d{2}\\.\\d{2}\\.\\d{4}$";
        return field.getText().matches(regex);
    }

    private boolean checkFilling(){
        boolean ok = true;
        if (fnameField.getText().length()==0){
            logArea.setText((logArea.getText()+"\nERROR: имя не задано").trim());
            logArea.setForeground(fatalColor);
            ok = false;
        }
        if (lnameField.getText().length()==0){
            logArea.setText((logArea.getText()+"\nERROR: фамилия не задана").trim());
            logArea.setForeground(fatalColor);
            ok = false;
        }
        if (birthDateField.getText().length()==0){
            logArea.setText((logArea.getText()+"\nERROR: дата рождения не задана").trim());
            logArea.setForeground(fatalColor);
            ok = false;
        }
        else if (!checkDate(birthDateField)){
            logArea.setText((logArea.getText()+"\nERROR: дата рождения не в формате DD.MM.YYYY").trim());
            logArea.setForeground(fatalColor);
            ok = false;
        }
        return ok;
    }

    public void reset(){
        fnameField.setText("");
        lnameField.setText("");
        mnameField.setText("");
        birthDateField.setText("");
        sexComboBox.setSelectedIndex(0);

        basefnameField.setText("");
        baselnameField.setText("");
        basemnameField.setText("");
        basebirthDateField.setText("");
        basesexComboBox.setSelectedIndex(0);

        readerId = 0;
        filterMethod = 0;
        filterSelection = false;

        StatsPanel.setVisible(false);
        setFiltersVisible(true);

        extrainfos.clear();
        deletedextrainfos.clear();
        newextrainfos.clear();
        activeFilters.clear();
        drawFilters();
        updateData(false, true);
        selectedReaderInfo.setVisible(false);
        leftPanel.revalidate();
        ReadersInfoPanel.revalidate();

    }

    private void setFiltersVisible(boolean visible){
        FiltersPane.setVisible(visible);
        ApplyPanel.setVisible(visible);
        FiltersLabel.setVisible(visible);
        baseFiltersPane.setVisible(visible);
    }

}
