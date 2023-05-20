import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class InstancesInfo extends JPanel{

    private Client window;

    private DefaultTableModel activeWorks = new DefaultTableModel(null, new String[]{"work_id", "label", "author", "cross"});
    private DefaultTableModel activeEditions = new DefaultTableModel(null, new String[]{"edition_id", "publisher", "label", "cross"});

    private int filterMethod = 0;
    private boolean filterSelection = true;

    private int workId = 0, editionId = 0, workLink = 0, editionLink = 0;
    private String instanceId = "";

    private Color okColor = new Color(28, 150, 18);
    private Color errorColor = new Color(153, 147, 22);
    private Color fatalColor = new Color(118, 20, 15);


    public InstancesInfo(Client window) {
        this.window = window;
        InstancesInfoPanel.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());

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

        newWorkButton.setFont(new Font(Font.DIALOG, Font.BOLD, 25));
        newWorkButton.setFocusPainted(false);
        newWorkButton.setContentAreaFilled(false);
        newWorkButton.setBorder(null);
        newWorkButton.setAlignmentX(CENTER_ALIGNMENT);

        newEditionButton.setFont(new Font(Font.DIALOG, Font.BOLD, 25));
        newEditionButton.setFocusPainted(false);
        newEditionButton.setContentAreaFilled(false);
        newEditionButton.setBorder(null);
        newEditionButton.setAlignmentX(CENTER_ALIGNMENT);

        newInstanceButton.setFont(new Font(Font.DIALOG, Font.BOLD, 25));
        newInstanceButton.setFocusPainted(false);
        newInstanceButton.setContentAreaFilled(false);
        newInstanceButton.setBorder(null);
        newInstanceButton.setAlignmentX(CENTER_ALIGNMENT);

        applyFiltersButton.setFont(new Font(Font.DIALOG, Font.BOLD, 30));
        applyFiltersButton.setFocusPainted(false);
        applyFiltersButton.setContentAreaFilled(false);
        applyFiltersButton.setBorder(null);
        applyFiltersButton.setAlignmentX(CENTER_ALIGNMENT);

        popularButton.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
        popularButton.setFocusPainted(false);
        popularButton.setContentAreaFilled(false);
        popularButton.setBorder(null);
        popularButton.setAlignmentX(CENTER_ALIGNMENT);

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


        applyFiltersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateData(true, true);
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateData(false,  true);
            }
        });


        worksCTable.setEnabled(false);
        worksCTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    e.consume();
                    int row = worksCTable.rowAtPoint(e.getPoint());
                    TableModel model = worksCTable.getModel();
                    int colCount = model.getColumnCount();
                    Object[] rowData = new Object[colCount];
                    for (int i = 0; i < colCount; ++i) {
                        rowData[i] = model.getValueAt(row, i);
                    }
                    for (var i : activeWorks.getDataVector()) {
                        if (i.elementAt(0).toString().equals(String.valueOf(rowData[0]))) {
                            return;
                        }
                    }
                    Object[] attrval = {rowData[0], rowData[1], rowData[2], "×"};
                    activeWorks.addRow(attrval);

                    selectedWorksSPane.revalidate();
                    FiltersPanel.revalidate();
                    leftPanel.revalidate();
                }
                else if (e.getButton() == MouseEvent.BUTTON3){
                    int row = worksCTable.rowAtPoint(e.getPoint());
                    TableModel model = worksCTable.getModel();
                    int colCount = model.getColumnCount();
                    Object[] rowData = new Object[colCount];
                    for (int i = 0; i<colCount; ++i){
                        rowData[i] = model.getValueAt(row, i);
                    }
                    workId = (int)rowData[0];
                    labelField.setText((String)rowData[1]);
                    authorField.setText((String)rowData[2]);
                    for (int i = 0; i < editGenreBox.getItemCount(); i++) {
                        Genre item = (Genre) editGenreBox.getItemAt(i);
                        if (item.getId() == (int)rowData[3]) {
                            editGenreBox.setSelectedItem(item);
                            break;
                        }
                    }
                    StatsPanel.setVisible(false);
                    statWorkButton.setVisible(true);
                    setFiltersVisible(true);
                    addWorkButton.setVisible(false);
                    removeWorkButton.setVisible(true);
                    saveWorkButton.setVisible(true);
                    selectedWorkInfo.setVisible(true);
                    selectedEditionInfo.setVisible(false);
                    selectedInstanceInfo.setVisible(false);
                    leftPanel.revalidate();
                }
                else if (e.getButton() == MouseEvent.BUTTON2){
                    int row = worksCTable.rowAtPoint(e.getPoint());
                    TableModel model = worksCTable.getModel();
                    int colCount = model.getColumnCount();
                    Object[] rowData = new Object[colCount];
                    for (int i = 0; i < colCount; ++i) {
                        rowData[i] = model.getValueAt(row, i);
                    }
                    workLink = (int)rowData[0];
                    workLinkField.setText(rowData[1] + " (" + rowData[2] + ")");
                    selectedEditionInfo.revalidate();
                }
            }

        });


        editionsCTable.setEnabled(false);
        editionsCTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    int row = editionsCTable.rowAtPoint(e.getPoint());
                    TableModel model = editionsCTable.getModel();
                    int colCount = model.getColumnCount();
                    Object[] rowData = new Object[colCount];
                    for (int i = 0; i < colCount; ++i) {
                        rowData[i] = model.getValueAt(row, i);
                    }
                    for (var i : activeEditions.getDataVector()) {
                        if (i.elementAt(0).toString().equals(String.valueOf(rowData[2]))) {
                            return;
                        }
                    }
                    Object[] attrval = {rowData[2], rowData[3], rowData[1], "×"};
                    activeEditions.addRow(attrval);

                    selectedEditionsSPane.revalidate();
                    FiltersPanel.revalidate();
                    leftPanel.revalidate();
                }
                else if (e.getButton() == MouseEvent.BUTTON3){

                    int row = editionsCTable.rowAtPoint(e.getPoint());
                    TableModel model = editionsCTable.getModel();
                    int colCount = model.getColumnCount();
                    Object[] rowData = new Object[colCount];
                    for (int i = 0; i<colCount; ++i){
                        rowData[i] = model.getValueAt(row, i);
                    }
                    workLink = (int)rowData[0];
                    workLinkField.setText((String)rowData[1]); // TODO add author in editions table and insert here
                    editionId = (int)rowData[2];
                    publisherField.setText((String)rowData[3]);
                    yearField.setText((String)rowData[4]);
                    countField.setText((String)rowData[5]);
                    if ((int)rowData[6] == 0){
                        editRuleBox.setSelectedIndex(0);
                    }
                    else {
                        for (int i = 0; i < editRuleBox.getItemCount(); i++) {
                            Genre item = (Genre) editRuleBox.getItemAt(i);
                            if (item.getId() == (int) rowData[6]) {
                                editRuleBox.setSelectedItem(item);
                                break;
                            }
                        }
                    }

                    StatsPanel.setVisible(false);
                    statEditionButton.setVisible(true);
                    setFiltersVisible(true);
                    addEditionButton.setVisible(false);
                    removeEditionButton.setVisible(true);
                    saveEditionButton.setVisible(true);
                    selectedWorkInfo.setVisible(false);
                    selectedEditionInfo.setVisible(true);
                    selectedInstanceInfo.setVisible(false);
                    leftPanel.revalidate();
                }
                else if (e.getButton() == MouseEvent.BUTTON2){
                    if (selectedInstanceInfo.isVisible()){
                        int row = editionsCTable.rowAtPoint(e.getPoint());
                        TableModel model = editionsCTable.getModel();
                        int colCount = model.getColumnCount();
                        Object[] rowData = new Object[colCount];
                        for (int i = 0; i < colCount; ++i) {
                            rowData[i] = model.getValueAt(row, i);
                        }
                        editionLink = (int)rowData[2];
                        editionLinkField.setText(rowData[3] + " (" + rowData[1] + ")");
                        selectedInstanceInfo.revalidate();
                    }
                    else if (selectedEditionInfo.isVisible()){
                        int row = editionsCTable.rowAtPoint(e.getPoint());
                        TableModel model = editionsCTable.getModel();
                        int colCount = model.getColumnCount();
                        Object[] rowData = new Object[colCount];
                        for (int i = 0; i < colCount; ++i) {
                            rowData[i] = model.getValueAt(row, i);
                        }
                        workLink = (int)rowData[0];
                        workLinkField.setText((String)rowData[1]);
                        selectedEditionInfo.revalidate();
                    }
                }
            }

        });

        instancesCTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            private final Color redColor = new Color(254, 157, 162, 133);
            private final Color greenColor = new Color(7, 27, 4, 52);
            private final Color whiteColor = Color.WHITE;

            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (table.getModel().getValueAt(row, 9) != null){
                    c.setBackground(greenColor);
                }
                else if ((int)table.getModel().getValueAt(row, 10) == 0) {
                    c.setBackground(redColor);
                }
                else {
                    c.setBackground(whiteColor);
                }
                return c;
            }
        });
        instancesCTable.setEnabled(false);
        instancesCTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3){
                    int row = instancesCTable.rowAtPoint(e.getPoint());
                    TableModel model = instancesCTable.getModel();
                    int colCount = model.getColumnCount();
                    Object[] rowData = new Object[colCount];
                    for (int i = 0; i<colCount; ++i){
                        rowData[i] = model.getValueAt(row, i);
                    }

                    retireButton.setVisible(rowData[9] == null);
                    setBorrowButton.setVisible(rowData[9] == null && (int)rowData[10] != 0);
                    setReturnButton.setVisible(rowData[9] == null && (int)rowData[10] == 0);
                    editionLink = (int)rowData[1];
                    editionLinkField.setText(rowData[2] + "(" + rowData[4] + ")");
                    instanceId = (String)rowData[0];
                    stocknumField.setText(instanceId);
                    stocknumField.setEnabled(false);
                    hallField.setText(String.valueOf(rowData[5]));
                    rackField.setText(String.valueOf(rowData[6]));
                    shelfField.setText(String.valueOf(rowData[7]));
                    StatsPanel.setVisible(false);
                    statInstButton.setVisible(true);
                    setFiltersVisible(true);
                    addInstButton.setVisible(false);
                    removeInstButton.setVisible(true);
                    saveInstButton.setVisible(true);
                    selectedWorkInfo.setVisible(false);
                    selectedEditionInfo.setVisible(false);
                    selectedInstanceInfo.setVisible(true);
                    leftPanel.revalidate();
                }
            }

        });


        newWorkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logArea.setText("");
                workId = 0;
                labelField.setText("");
                authorField.setText("");
                editGenreBox.setSelectedIndex(0);
                StatsPanel.setVisible(false);
                statWorkButton.setVisible(false);
                setFiltersVisible(true);
                addWorkButton.setVisible(true);
                removeWorkButton.setVisible(false);
                saveWorkButton.setVisible(false);
                selectedWorkInfo.setVisible(true);
                selectedEditionInfo.setVisible(false);
                selectedInstanceInfo.setVisible(false);
                leftPanel.revalidate();
            }
        });

        newEditionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editionId = 0;
                workLink = 0;
                logArea.setText("");
                workLinkField.setText("select with MMB");
                publisherField.setText("");
                yearField.setText("");
                countField.setText("");
                editRuleBox.setSelectedIndex(0);
                addEditionButton.setVisible(true);
                StatsPanel.setVisible(false);
                statEditionButton.setVisible(false);
                setFiltersVisible(true);
                removeEditionButton.setVisible(false);
                saveEditionButton.setVisible(false);
                selectedWorkInfo.setVisible(false);
                selectedEditionInfo.setVisible(true);
                selectedInstanceInfo.setVisible(false);
                leftPanel.revalidate();
            }
        });

        newInstanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stocknumField.setEnabled(true);
                instanceId = "";
                logArea.setText("");
                stocknumField.setText("");
                hallField.setText("");
                rackField.setText("");
                shelfField.setText("");
                editionLink = 0;
                editionLinkField.setText("select with MMB");
                StatsPanel.setVisible(false);
                statInstButton.setVisible(false);
                setFiltersVisible(true);
                retireButton.setVisible(false);
                setReturnButton.setVisible(false);
                setBorrowButton.setVisible(false);
                addInstButton.setVisible(true);
                removeInstButton.setVisible(false);
                saveInstButton.setVisible(false);
                selectedWorkInfo.setVisible(false);
                selectedEditionInfo.setVisible(false);
                selectedInstanceInfo.setVisible(true);
                leftPanel.revalidate();
            }
        });


        cancelWorkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logArea.setText("");
                workId=0;
                selectedWorkInfo.setVisible(false);
                StatsPanel.setVisible(false);
                setFiltersVisible(true);
                leftPanel.revalidate();
            }
        });

        cancelEditionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logArea.setText("");
                editionId=0;
                selectedEditionInfo.setVisible(false);
                StatsPanel.setVisible(false);
                setFiltersVisible(true);
                leftPanel.revalidate();
            }
        });

        cancelInstButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logArea.setText("");
                instanceId="";
                selectedInstanceInfo.setVisible(false);
                StatsPanel.setVisible(false);
                setFiltersVisible(true);
                leftPanel.revalidate();
            }
        });


        addWorkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logArea.setText("");
                logArea.setForeground(okColor);
                if (!checkWFilling()){
                    return;
                }
                try {
                    String query = "insert into \"WORKS\"(label, author, genre) values('" + labelField.getText() +
                            "','" + authorField.getText() +
                            "'," + ((Genre)editGenreBox.getSelectedItem()).getId() + ")";
                    int workId = (int)window.getSession().executeRetQuery(query);
                    logArea.setText((logArea.getText()+"\nOK: inserted work " + workId).trim());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    logArea.setText((logArea.getText() + "\ninsert work FATAL ERROR:\n" + ex.getMessage()).trim());
                    logArea.setForeground(fatalColor);
                    InstancesInfoPanel.revalidate();
                    return;
                }

                workId=0;
                selectedWorkInfo.setVisible(false);
                StatsPanel.setVisible(false);
                setFiltersVisible(true);
                leftPanel.revalidate();
                updateData(filterMethod != 0, false);
            }
        });

        removeWorkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logArea.setText("");
                logArea.setForeground(okColor);
                try {
                    String query = "delete from works where\n" +
                            "id = " + workId;
                    int ret = window.getSession().executeUpdate(query);
                    logArea.setText((logArea.getText()+"\nOK: deleted work " + workId).trim());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    logArea.setText((logArea.getText() + "\ndelete work FATAL ERROR:\n" + ex.getMessage()).trim());
                    logArea.setForeground(fatalColor);
                    InstancesInfoPanel.revalidate();
                    return;
                }
                workId=0;
                selectedWorkInfo.setVisible(false);
                StatsPanel.setVisible(false);
                setFiltersVisible(true);
                leftPanel.revalidate();
                updateData(filterMethod!=0, false);
            }
        });

        saveWorkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logArea.setText("");
                logArea.setForeground(okColor);
                if (!checkWFilling()){
                    return;
                }
                try {
                    String query = "update works set label = \'" + labelField.getText() +
                            "\', author = \'" + authorField.getText() +
                            "\', genre = " + ((Genre) editGenreBox.getSelectedItem()).getId() +
                            "where id = " + workId;
                    int ret = window.getSession().executeUpdate(query);
                    logArea.setText((logArea.getText()+"\nOK: updated work " + workId).trim());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    logArea.setText((logArea.getText() + "\nupdate work FATAL ERROR:\n" + ex.getMessage()).trim());
                    logArea.setForeground(fatalColor);
                    InstancesInfoPanel.revalidate();
                    return;
                }

                workId=0;
                selectedWorkInfo.setVisible(false);
                StatsPanel.setVisible(false);
                setFiltersVisible(true);
                leftPanel.revalidate();
                updateData(filterMethod!=0, false);
            }
        });


        addEditionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logArea.setText("");
                logArea.setForeground(okColor);
                if (!checkEFilling()){
                    return;
                }
                try {
                    String query = "insert into \"EDITIONS\"(work,publisher,year,count"+(((Genre)editRuleBox.getSelectedItem()).getId() > 0?",rule":"")+") values(" + workLink +
                            ",'" + publisherField.getText() + "'" +
                            (yearField.getText().length() > 0?("," + yearField.getText()):",NULL") +
                            (countField.getText().length() > 0?(",'" + countField.getText()+"'"):",NULL") +
                            (((Genre)editRuleBox.getSelectedItem()).getId()>0?("," + ((Genre)editRuleBox.getSelectedItem()).getId()):"") + ")";
                    System.out.println(query);
                    int editionId = (int)window.getSession().executeRetQuery(query);
                    logArea.setText((logArea.getText()+"\nOK: inserted edition " + editionId).trim());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    logArea.setText((logArea.getText() + "\ninsert edition FATAL ERROR:\n" + ex.getMessage()).trim());
                    logArea.setForeground(fatalColor);
                    InstancesInfoPanel.revalidate();
                    return;
                }

                editionId=0;
                selectedEditionInfo.setVisible(false);
                StatsPanel.setVisible(false);
                setFiltersVisible(true);
                leftPanel.revalidate();
                updateData(filterMethod != 0, false);
            }
        });

        removeEditionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logArea.setText("");
                logArea.setForeground(okColor);
                try {
                    String query = "delete from editions where\n" +
                            "id = " + editionId;
                    int ret = window.getSession().executeUpdate(query);
                    logArea.setText((logArea.getText()+"\nOK: deleted edition " + editionId).trim());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    logArea.setText((logArea.getText() + "\ndelete edition FATAL ERROR:\n" + ex.getMessage()).trim());
                    logArea.setForeground(fatalColor);
                    InstancesInfoPanel.revalidate();
                    return;
                }
                editionId=0;
                selectedEditionInfo.setVisible(false);
                StatsPanel.setVisible(false);
                setFiltersVisible(true);
                leftPanel.revalidate();
                updateData(filterMethod!=0, false);
            }
        });

        saveEditionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logArea.setText("");
                logArea.setForeground(okColor);
                if (!checkEFilling()){
                    return;
                }
                try {
                    String query = "update editions set work = " + workLink +
                            ", publisher = '" + publisherField.getText() + "'" +
                            (yearField.getText().length()>0?(", year = " + yearField.getText()):", year = NULL") +
                            (countField.getText().length()>0?(", count = '" + countField.getText() + "'"):", count = NULL") +
                            (((Genre)editRuleBox.getSelectedItem()).getId()>0?(", rule = " + ((Genre)editRuleBox.getSelectedItem()).getId()):", rule = NULL") +
                            "where id = " + editionId;
                    int ret = window.getSession().executeUpdate(query);
                    logArea.setText((logArea.getText()+"\nOK: updated edition " + editionId).trim());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    logArea.setText((logArea.getText() + "\nupdate edition FATAL ERROR:\n" + ex.getMessage()).trim());
                    logArea.setForeground(fatalColor);
                    InstancesInfoPanel.revalidate();
                    return;
                }

                editionId=0;
                selectedEditionInfo.setVisible(false);
                StatsPanel.setVisible(false);
                setFiltersVisible(true);
                leftPanel.revalidate();
                updateData(filterMethod!=0, false);
            }
        });


        addInstButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logArea.setText("");
                logArea.setForeground(okColor);
                if (!checkIFilling()){
                    return;
                }
                try {
                    String query = "insert into \"INSTANCES\"(stocknum, edition, library, hall, rack, shelf, entrydate) values('" + stocknumField.getText() + "'," + editionLink +
                            "," + window.getLibrarian().getLibrary() +
                            "," + hallField.getText() +
                            "," + rackField.getText() +
                            "," + shelfField.getText() +
                            ",TO_DATE('"+ LocalDate.now()+"', 'YYYY-MM-DD'))";
                    window.getSession().executeUpdate(query);
                    logArea.setText((logArea.getText()+"\nOK: added instance " + instanceId).trim());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    logArea.setText((logArea.getText() + "\ninsert instance FATAL ERROR:\n" + ex.getMessage()).trim());
                    logArea.setForeground(fatalColor);
                    InstancesInfoPanel.revalidate();
                    return;
                }

                instanceId="";
                selectedInstanceInfo.setVisible(false);
                StatsPanel.setVisible(false);
                setFiltersVisible(true);
                leftPanel.revalidate();
                updateData(filterMethod != 0, false);
            }
        });

        removeInstButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logArea.setText("");
                logArea.setForeground(okColor);
                try {
                    String query = "delete from instances where\n" +
                            "stocknum = '" + instanceId + "'";
                    int ret = window.getSession().executeUpdate(query);
                    logArea.setText((logArea.getText()+"\nOK: deleted instance " + instanceId).trim());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    logArea.setText((logArea.getText() + "\ndelete instance FATAL ERROR:\n" + ex.getMessage()).trim());
                    logArea.setForeground(fatalColor);
                    InstancesInfoPanel.revalidate();
                    return;
                }
                instanceId="";
                selectedInstanceInfo.setVisible(false);
                StatsPanel.setVisible(false);
                setFiltersVisible(true);
                leftPanel.revalidate();
                updateData(filterMethod!=0, false);
            }
        });

        saveInstButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logArea.setText("");
                logArea.setForeground(okColor);
                if (!checkIFilling()){
                    return;
                }
                try {
                    String query = "update instances set edition = " + editionLink +
                            ", library = " + window.getLibrarian().getLibrary() +
                            ", hall = " + hallField.getText() +
                            ", rack = " + rackField.getText() +
                            ", shelf = " + shelfField.getText() +
                           "where stocknum = '" + instanceId + "'";
                    int ret = window.getSession().executeUpdate(query);
                    logArea.setText((logArea.getText()+"\nOK: updated instance " + instanceId).trim());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    logArea.setText((logArea.getText() + "\nupdate instance FATAL ERROR:\n" + ex.getMessage()).trim());
                    logArea.setForeground(fatalColor);
                    InstancesInfoPanel.revalidate();
                    return;
                }

                instanceId="";
                selectedInstanceInfo.setVisible(false);
                StatsPanel.setVisible(false);
                setFiltersVisible(true);
                leftPanel.revalidate();
                updateData(filterMethod!=0, false);
            }
        });

        retireButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logArea.setText("");
                logArea.setForeground(okColor);
                try {
                    String query = "update instances set retiredate = TO_DATE('"+ LocalDate.now()+"', 'YYYY-MM-DD')\n" +
                            "where stocknum = '" + instanceId + "'";
                    int ret = window.getSession().executeUpdate(query);
                    logArea.setText((logArea.getText()+"\nOK: updated instance " + instanceId).trim());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    logArea.setText((logArea.getText() + "\nupdate instance FATAL ERROR:\n" + ex.getMessage()).trim());
                    logArea.setForeground(fatalColor);
                    InstancesInfoPanel.revalidate();
                    return;
                }

                retireButton.setVisible(false);
                setBorrowButton.setVisible(false);
                setReturnButton.setVisible(false);
                leftPanel.revalidate();
                updateData(filterMethod!=0, false);
            }
        });
















        statWorkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFiltersVisible(false);
                logArea.setText("");
                logArea.setForeground(okColor);
                String query = "select readers.id, readers.firstname, readers.middlename, readers.lastname, readers.birthdate, sexes.name sex, readers.regdate, instance, publisher, year, returned, borrowdate, until,\n" +
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
                        "join readers on readers.id = borrowings.reader\n" +
                        "join sexes on sexes.id = sex\n" +
                        "where works.id = " + workId + "\n" +
                        (statFromField.getText().length()>0 && checkDate(statFromField)
                                ? "and borrowdate > TO_DATE('" + statFromField.getText() + "', 'DD.MM.YYYY')\n"
                                : "") +
                        (statToField.getText().length()>0 && checkDate(statToField)
                                ? "and borrowdate < TO_DATE('" + statToField.getText() + "', 'DD.MM.YYYY')\n"
                                : "") + "order by returned, borrowdate";


                Object[] columnNames = {"id", "returned", "Долг", "ФИО", "Дата рождения","пол","Дата регистрации", "Экземпляр", "Издание", "Дата выдачи","Выдано до"};
                DefaultTableModel newModel = new DefaultTableModel(null, columnNames);

                try{
                    ResultSet ret = window.getSession().executeQuery(query);
                    int totalfound = 0;
                    while(ret.next()){
                        String mname = " " + ret.getString("middlename") + " ";
                        if (ret.wasNull()){
                            mname = " ";
                        }
                        Object[] rowData = {ret.getInt("id"), ret.getInt("returned"), ret.getInt("debt"), ret.getString("firstname")+mname+ret.getString("lastname"), ret.getDate("birthdate"), ret.getString("sex"), ret.getDate("regdate"), ret.getString("instance"), (ret.getString("publisher") + " (" + ret.getInt("year")+ ")"), ret.getDate("borrowdate"), ret.getDate("until")};
                        newModel.addRow(rowData);
                        totalfound++;
                    }
                    logArea.setText((logArea.getText() + "\nOK: " + (totalfound>0?totalfound + " borrowings found":"no borrowings found")).trim());
                    statsTable.setModel(newModel);
                    StatsPanel.setVisible(true);
                    statsTable.removeColumn(statsTable.getColumnModel().getColumn(2));
                    statsTable.removeColumn(statsTable.getColumnModel().getColumn(1));
                    statsTable.removeColumn(statsTable.getColumnModel().getColumn(0));
                    rightPanel.revalidate();
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

        statEditionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFiltersVisible(false);
                logArea.setText("");
                logArea.setForeground(okColor);
                String query = "select readers.id, readers.firstname, readers.middlename, readers.lastname, readers.birthdate, sexes.name sex, readers.regdate, instance, returned, borrowdate, until,\n" +
                        "  CASE \n" +
                        "    WHEN returned = 0\n" +
                        "    and until < CURRENT_DATE\n" +
                        "    THEN 1\n" +
                        "    ELSE 0\n" +
                        "  END AS debt\n" +
                        "from borrowings\n" +
                        "join instances on instances.stocknum = borrowings.instance\n" +
                        "join editions on editions.id = edition\n" +
                        "join readers on readers.id = borrowings.reader\n" +
                        "join sexes on sexes.id = sex\n" +
                        "where editions.id = " + editionId + "\n" +
                        (statFromField.getText().length()>0 && checkDate(statFromField)
                                ? "and borrowdate > TO_DATE('" + statFromField.getText() + "', 'DD.MM.YYYY')\n"
                                : "") +
                        (statToField.getText().length()>0 && checkDate(statToField)
                                ? "and borrowdate < TO_DATE('" + statToField.getText() + "', 'DD.MM.YYYY')\n"
                                : "") + "order by returned, borrowdate";


                Object[] columnNames = {"id", "returned", "Долг", "ФИО", "Дата рождения","пол","Дата регистрации", "Экземпляр","Дата выдачи","Выдано до"};
                DefaultTableModel newModel = new DefaultTableModel(null, columnNames);

                try{
                    ResultSet ret = window.getSession().executeQuery(query);
                    int totalfound = 0;
                    while(ret.next()){
                        String mname = " " + ret.getString("middlename") + " ";
                        if (ret.wasNull()){
                            mname = " ";
                        }
                        Object[] rowData = {ret.getInt("id"), ret.getInt("returned"), ret.getInt("debt"), ret.getString("firstname")+mname+ret.getString("lastname"), ret.getDate("birthdate"), ret.getString("sex"), ret.getDate("regdate"), ret.getString("instance"), ret.getDate("borrowdate"), ret.getDate("until")};
                        newModel.addRow(rowData);
                        totalfound++;
                    }
                    logArea.setText((logArea.getText() + "\nOK: " + (totalfound>0?totalfound + " borrowings found":"no borrowings found")).trim());
                    StatsPanel.setVisible(true);
                    statsTable.setModel(newModel);
                    statsTable.removeColumn(statsTable.getColumnModel().getColumn(2));
                    statsTable.removeColumn(statsTable.getColumnModel().getColumn(1));
                    statsTable.removeColumn(statsTable.getColumnModel().getColumn(0));
                    rightPanel.revalidate();
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

        statsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            private final Color redColor = new Color(254, 157, 162, 133);
            private final Color purpleColor = new Color(202, 157, 254, 125);
            private final Color whiteColor = Color.WHITE;

            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if ((int)table.getModel().getValueAt(row, 2) == 1) {
                    c.setBackground(redColor);
                }
                else if ((int)table.getModel().getValueAt(row, 1) == 0) {
                    c.setBackground(purpleColor);
                }
                else {
                    c.setBackground(whiteColor);
                }
                return c;
            }
        });

        statInstButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFiltersVisible(false);
                logArea.setText("");
                logArea.setForeground(okColor);
                String query = "select readers.id, readers.firstname, readers.middlename, readers.lastname, readers.birthdate, sexes.name sex, readers.regdate, returned, borrowdate, until,\n" +
                        "  CASE \n" +
                        "    WHEN returned = 0\n" +
                        "    and until < CURRENT_DATE\n" +
                        "    THEN 1\n" +
                        "    ELSE 0\n" +
                        "  END AS debt\n" +
                        "from borrowings\n" +
                        "join instances on instances.stocknum = borrowings.instance\n" +
                        "join readers on readers.id = borrowings.reader\n" +
                        "join sexes on sexes.id = sex\n" +
                        "where instances.stocknum = '" + instanceId + "'\n" +
                        (statFromField.getText().length()>0 && checkDate(statFromField)
                                ? "and borrowdate >= TO_DATE('" + statFromField.getText() + "', 'DD.MM.YYYY')\n"
                                : "") +
                        (statToField.getText().length()>0 && checkDate(statToField)
                                ? "and borrowdate <= TO_DATE('" + statToField.getText() + "', 'DD.MM.YYYY')\n"
                                : "") + "order by returned, borrowdate";


                Object[] columnNames = {"id", "returned", "Долг", "ФИО", "Дата рождения","пол","Дата регистрации","Дата выдачи","Выдано до"};
                DefaultTableModel newModel = new DefaultTableModel(null, columnNames);

                try{
                    ResultSet ret = window.getSession().executeQuery(query);
                    int totalfound = 0;
                    while(ret.next()){
                        String mname = " " + ret.getString("middlename") + " ";
                        if (ret.wasNull()){
                            mname = " ";
                        }
                        Object[] rowData = {ret.getInt("id"), ret.getInt("returned"), ret.getInt("debt"), ret.getString("firstname")+mname+ret.getString("lastname"), ret.getDate("birthdate"), ret.getString("sex"), ret.getDate("regdate"), ret.getDate("borrowdate"), ret.getDate("until")};
                        newModel.addRow(rowData);
                        totalfound++;
                    }
                    logArea.setText((logArea.getText() + "\nOK: " + (totalfound>0?totalfound + " borrowings found":"no borrowings found")).trim());
                    statsTable.setModel(newModel);
                    StatsPanel.setVisible(true);
                    statsTable.removeColumn(statsTable.getColumnModel().getColumn(2));
                    statsTable.removeColumn(statsTable.getColumnModel().getColumn(1));
                    statsTable.removeColumn(statsTable.getColumnModel().getColumn(0));
                    rightPanel.revalidate();
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


        closeStatsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFiltersVisible(true);
                StatsPanel.setVisible(false);
                leftPanel.revalidate();
            }
        });





















        setReturnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnInstance(instanceId);

                setBorrowButton.setVisible(true);
                setReturnButton.setVisible(false);
                leftPanel.revalidate();
                updateData(filterMethod!=0,false);
            }
        });

        setBorrowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                window.setBorrowFrame(instanceId);
            }
        });

        selectedWorksTable.setModel(activeWorks);
        selectedWorksTable.removeColumn(selectedWorksTable.getColumnModel().getColumn(0));
        selectedWorksTable.getColumn("cross").setMaxWidth(7);
        selectedWorksTable.setRowSelectionAllowed(false);
        selectedWorksTable.getTableHeader().setUI(null);
        selectedWorksTable.revalidate();
        selectedWorksSPane.revalidate();
        selectedWorksTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (selectedWorksTable.columnAtPoint(e.getPoint()) == 2) {
                    activeWorks.removeRow(selectedWorksTable.rowAtPoint(e.getPoint()));
                    FiltersPanel.revalidate();
                    leftPanel.revalidate();
                }
            }
        });


        selectedEditionsTable.setModel(activeEditions);
        selectedEditionsTable.removeColumn(selectedEditionsTable.getColumnModel().getColumn(0));
        selectedEditionsTable.getColumn("cross").setMaxWidth(7);
        selectedEditionsTable.setRowSelectionAllowed(false);
        selectedEditionsTable.getTableHeader().setUI(null);
        selectedEditionsTable.revalidate();
        selectedEditionsSPane.revalidate();
        selectedEditionsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (selectedEditionsTable.columnAtPoint(e.getPoint()) == 2) {
                    activeEditions.removeRow(selectedEditionsTable.rowAtPoint(e.getPoint()));
                    FiltersPanel.revalidate();
                    leftPanel.revalidate();
                }
            }
        });


        selectedInstanceInfo.setVisible(false);
        selectedWorkInfo.setVisible(false);
        selectedEditionInfo.setVisible(false);

        popularButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logArea.setText("");
                logArea.setForeground(okColor);
                String query = "with stats as (\n" +
                        "  select editions.work, count(borrowings.id) \"TIMESBORROWED\"\n" +
                        "  from borrowings\n" +
                        "  join instances on instances.stocknum = borrowings.instance\n" +
                        "  join editions on editions.id = instances.edition\n" +
                        "  group by editions.work\n" +
                        "), maxtimes as (\n" +
                        "  select timesborrowed\n" +
                        "  from (\n" +
                        "    select distinct timesborrowed from stats\n" +
                        "    order by timesborrowed desc\n" +
                        "  )\n" +
                        "  where rownum <=\n" +
                        "    1 --топ-N--\n" +
                        ")\n" +
                        "select works.id workid, label, author, genre, genres.name\n" +
                        "from stats\n" +
                        "join works on works.id = stats.work\n" +
                        "join genres on genres.id = works.genre\n" +
                        "where timesborrowed in (select timesborrowed from maxtimes)\n" +
                        "order by timesborrowed desc";
                try{
                    Object[] wcolumnNames = {"work", "Название", "Автор", "genre", "Жанр"};
                    DefaultTableModel wModel = new DefaultTableModel(null, wcolumnNames);
                    ResultSet ret = window.getSession().executeQuery(query);
                    int totalfound = 0;
                    while(ret.next()){
                        Object[] rowData = {ret.getInt("workid"), ret.getString("label"), ret.getString("author"), ret.getInt("genre"), ret.getString("name")};
                        wModel.addRow(rowData);
                        totalfound++;
                    }
                    logArea.setText((logArea.getText() + "\nOK: " + (totalfound>0?totalfound + " works found":"no works found")).trim());
                    worksCTable.setModel(wModel);
                    worksCTable.removeColumn(worksCTable.getColumnModel().getColumn(3));
                    worksCTable.removeColumn(worksCTable.getColumnModel().getColumn(0));
                    rightPanel.revalidate();
                }
                catch (SQLException ex) {
                    ex.printStackTrace();
                    logArea.setText((logArea.getText() + "\nselect works FATAL ERROR:\n" + ex.getMessage()).trim());
                    logArea.setForeground(fatalColor);
                }
            }
        });
    }

    private JButton applyFiltersButton;
    private JButton cancelButton;
    private JButton newInstanceButton;
    private JPanel leftPanel;
    private JScrollPane FiltersPane;
    private JPanel FiltersPanel;
    private JLabel filtersLabel;
    private JPanel selectedInstanceInfo;
    private JButton removeInstButton;
    private JButton saveInstButton;
    private JButton addInstButton;
    private JButton cancelInstButton;
    private JTextField hallField;
    private JTextField rackField;
    private JPanel rightPanel;
    private JButton backButton;
    private JLabel greetingLabel;
    private JTextField shelfField;
    private JButton setBorrowButton;
    private JButton setReturnButton;
    private JTextField fLabelField;
    private JTextField fAuthorField;
    private JTextField fPublisherField;
    private JTextField fFromYearField;
    private JTable instancesCTable;
    private JTable editionsCTable;
    private JTable worksCTable;
    private JButton newEditionButton;
    private JButton newWorkButton;
    private JTabbedPane TabPane;
    private JTable selectedWorksTable;
    private JPanel InstancesInfoPanel;
    private JTextField fStocknumField;
    private JTextField fHallField;
    private JTextField fRackField;
    private JTextField fShelfField;
    private JTable selectedEditionsTable;
    private JScrollPane selectedWorksSPane;
    private JScrollPane selectedEditionsSPane;
    private JComboBox genreBox;
    private JTextField fToYearField;
    private JPanel changeInstanceButtons;
    private JPanel selectedWorkInfo;
    private JTextField labelField;
    private JTextField authorField;
    private JComboBox editGenreBox;
    private JPanel selectedEditionInfo;
    private JTextField countField;
    private JTextField publisherField;
    private JTextField yearField;
    private JComboBox editRuleBox;
    private JPanel changeWorkButtons;
    private JPanel changeEditionButtons;
    private JButton removeWorkButton;
    private JButton saveWorkButton;
    private JButton addWorkButton;
    private JButton cancelWorkButton;
    private JButton removeEditionButton;
    private JButton saveEditionButton;
    private JButton addEditionButton;
    private JButton cancelEditionButton;
    private JTextField workLinkField;
    private JTextField editionLinkField;
    private JTextField stocknumField;
    private JCheckBox availableCheckBox;
    private JCheckBox existsCheckBox;
    private JButton retireButton;
    private JTextArea logArea;
    private JCheckBox retiredCheckBox;
    private JCheckBox borrowedCheckBox;
    private JTextField fFromEntryField;
    private JTextField fToEntryField;
    private JTextField fFromRetireField;
    private JTextField fToRetireField;
    private JScrollPane logPane;
    private JButton statInstButton;
    private JButton statWorkButton;
    private JButton statEditionButton;
    private JPanel FiltersLabel;
    private JPanel ApplyPanel;
    private JPanel StatsPanel;
    private JTextField statFromField;
    private JTextField statToField;
    private JTable statsTable;
    private JButton closeStatsButton;
    private JButton popularButton;
    private JComboBox libraryBox;

    public JPanel getPanel(){
        return InstancesInfoPanel;
    }


    public void updateConsts(){
        genreBox.removeAllItems();
        editGenreBox.removeAllItems();
        libraryBox.removeAllItems();
        genreBox.addItem(new Genre(0,"Выберите жанр"));
        editGenreBox.addItem(new Genre(0,"Выберите жанр"));
        editRuleBox.addItem(new Genre(0,"Отсутствует"));
        libraryBox.addItem(new Genre(-1,"Любая библиотека"));
        libraryBox.addItem(new Genre(0,"Данная библиотека"));
        String genreQuery = """
                select id, name
                from genres
                order by id""";
        try{
            ResultSet ret = window.getSession().executeQuery(genreQuery);
            while(ret.next()){
                genreBox.addItem(new Genre(ret.getInt("id"), ret.getString("name")));
                editGenreBox.addItem(new Genre(ret.getInt("id"), ret.getString("name")));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            logArea.setText((logArea.getText() + "\nselect genres FATAL ERROR:\n" + e.getMessage()).trim());
            logArea.setForeground(fatalColor);
            InstancesInfoPanel.revalidate();
            return;
        }
        String ruleQuery = """
                select id, description
                from rules
                order by id""";
        try{
            ResultSet ret = window.getSession().executeQuery(ruleQuery);
            while(ret.next()){
                editRuleBox.addItem(new Genre(ret.getInt("id"), ret.getString("description")));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            logArea.setText((logArea.getText() + "\nselect rules FATAL ERROR:\n" + e.getMessage()).trim());
            logArea.setForeground(fatalColor);
            InstancesInfoPanel.revalidate();
            return;
        }
        String libraryQuery = """
                select id, name
                from libraries
                order by id""";
        try{
            ResultSet ret = window.getSession().executeQuery(libraryQuery);
            while(ret.next()){
                libraryBox.addItem(new Genre(ret.getInt("id"), ret.getString("name")));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            logArea.setText((logArea.getText() + "\nselect libraries FATAL ERROR:\n" + e.getMessage()).trim());
            logArea.setForeground(fatalColor);
            InstancesInfoPanel.revalidate();
            return;
        }
    }

    public void returnInstance(String stocknum){
        logArea.setText("");
        logArea.setForeground(okColor);

        String selquery = "select id, reader, until\n" +
                "from borrowings\n" +
                "where returned = 0 and\n" +
                "instance = '" + stocknum + "'";
        int borrowingId = 0;
        int readerId = 0;
        Date untilDate = new Date(System.currentTimeMillis());
        boolean found = false;
        try{
            ResultSet ret = window.getSession().executeQuery(selquery);
            while(ret.next()){
                borrowingId = ret.getInt("id");
                readerId = ret.getInt("reader");
                untilDate = ret.getDate("until");
                found = true;
                break;
            }
        }
        catch (SQLException e) {
            logArea.setText((logArea.getText() + "\nselect borrowing FATAL ERROR:\n" + e.getMessage()).trim());
            logArea.setForeground(fatalColor);
            InstancesInfoPanel.revalidate();
            return;
        }
        if (!found) {
            logArea.setText((logArea.getText() + "\nERROR: this instance wasn't borrowed").trim());
            logArea.setForeground(fatalColor);
            InstancesInfoPanel.revalidate();
            return;
        }
        try {
            String insquery = "insert into \"RETURNS\"(borrowing, librarian, returndate) values(" +
                    borrowingId +
                    "," + window.getLibrarian().getId() +
                    ",TO_DATE('"+ LocalDate.now()+"', 'YYYY-MM-DD'))";
            window.getSession().executeUpdate(insquery);
            logArea.setText((logArea.getText()+"\nOK: returned instance " + instanceId).trim());
        } catch (SQLException ex) {
            logArea.setText((logArea.getText() + "\ninsert return FATAL ERROR:\n" + ex.getMessage()).trim());
            logArea.setForeground(fatalColor);
            InstancesInfoPanel.revalidate();
            return;
        }
    }

    public void updateData(boolean useFilters, boolean clear) {
        if (clear){
            logArea.setText("");
            logArea.setForeground(okColor);
        }
        String iquery, wquery, equery;
        if (!useFilters) {
            filterMethod = 0;

            wquery = "select works.id workid, label, author, genre, genres.name \n" +
                    "from works\n" +
                    "join genres on genres.id = genre\n" +
                    "order by label, genre";
            equery = "select works.id workid, label, editions.id editionid, publisher, year, count, rules.id rule, description\n" +
                    "from editions\n" +
                    "join works on works.id = work\n" +
                    "left join rules on rules.id = rule\n" +
                    "order by label, publisher, year, count desc";
            iquery = "select instances.stocknum, editions.id editionid, publisher edition, works.id workid, label work, hall, rack, shelf, entrydate, retiredate,\n" +
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
                    "order by workid, editionid, hall, rack, shelf";
        }
        else{
            filterMethod = 1;

            wquery = "select works.id workid, label, author, genre, genres.name \n" +
                    "from works\n" +
                    "join genres on genres.id = genre\n" + getWorksFilters("where ") +
                    "order by label, genre";


            equery = "select works.id workid, label, editions.id editionid, publisher, year, count, rules.id rule, description\n" +
                    "from editions\n" +
                    "join works on works.id = work\n" +
                    "left join rules on rules.id = rule\n";
            String efilters = getEditionsFilters("where ");
            equery = equery.concat(efilters) +
                    (activeWorks.getRowCount()>0 ? (
                            (efilters.length() > 0 ? "and ": "where ") +
                            "works.id in ("+ String.join(",",
                            Collections.list(activeWorks.getDataVector().elements()).stream()
                            .map(array -> String.valueOf(array.get(0)))
                            .toArray(String[]::new)) + ")\n" )

                            : getWorksFilters(efilters.length() > 0 ? "and ": "where ")) +
                    "order by label, publisher, year, count desc";





            iquery = "select instances.stocknum, editions.id editionid, publisher edition, works.id workid, label work, hall, rack, shelf, entrydate, retiredate,\n" +
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
                    "join works on works.id = work\n";
            String ifilters = getInstancesFilters("where ");
            if (activeEditions.getRowCount()>0) {
                ifilters = ifilters.concat(
                        (ifilters.length() > 0 ? "and ": "where ") +
                        "editions.id in ("+ String.join(",",
                        Collections.list(activeEditions.getDataVector().elements()).stream()
                                .map(array -> String.valueOf(array.get(0)))
                                .toArray(String[]::new)) + ")\n"
                );
            }
            else if (activeWorks.getRowCount()>0){
                ifilters = ifilters.concat(
                        (ifilters.length() > 0 ? "and ": "where ") +
                                "works.id in ("+ String.join(",",
                                Collections.list(activeWorks.getDataVector().elements()).stream()
                                        .map(array -> String.valueOf(array.get(0)))
                                        .toArray(String[]::new)) + ")\n"

                );
                ifilters = ifilters.concat(getEditionsFilters(ifilters.length() > 0 ? "and ": "where "));
            }
            else{
                ifilters = ifilters.concat(getWorksFilters(ifilters.length() > 0 ? "and ": "where "));
                ifilters = ifilters.concat(getEditionsFilters(ifilters.length() > 0 ? "and ": "where "));
            }
            ifilters = ifilters +
                    (availableCheckBox.isSelected()
                            ? (ifilters.length()>0 ? "and ":"where ") + "stocknum not in (\n" +
                                    "  select distinct instance \n" +
                                    "  from borrowings\n" +
                                    "  where returned = 0)\n"
                            : (borrowedCheckBox.isSelected()
                            ? (ifilters.length()>0 ? "and ":"where ") + "stocknum in (\n" +
                                    "  select distinct instance \n" +
                                    "  from borrowings\n" +
                                    "  where returned = 0)\n"
                            :""));
            ifilters = ifilters +
                    (existsCheckBox.isSelected()
                            ? (ifilters.length()>0 ? "and ":"where ") + "retiredate is null\n"
                            : (retiredCheckBox.isSelected()
                            ? (ifilters.length()>0 ? "and ":"where ") + "retiredate is not null\n"
                            : (availableCheckBox.isSelected()?"and retiredate is null\n":"")));
            iquery = iquery.concat(ifilters) +
                    "order by workid, editionid, hall, rack, shelf";

        }

        System.out.println(wquery+"\n\n");
        System.out.println(equery+"\n\n");
        System.out.println(iquery+"\n\n");
        Object[] wcolumnNames = {"work", "Название", "Автор", "genre", "Жанр"};
        Object[] ecolumnNames = {"work", "Произведение", "edition" ,"Издание", "Год издания", "Тираж", "rule", "Правило"};
        Object[] icolumnNames = {"Инвентарный номер", "edition", "Издание", "work" ,"Произведение", "Зал", "Стеллаж", "Полка", "Дата поступления", "Дата списания", "available"};
        DefaultTableModel wModel = new DefaultTableModel(null, wcolumnNames);
        DefaultTableModel eModel = new DefaultTableModel(null, ecolumnNames);
        DefaultTableModel iModel = new DefaultTableModel(null, icolumnNames);


        try{
            ResultSet ret = window.getSession().executeQuery(wquery);
            int totalfound = 0;
            while(ret.next()){
                Object[] rowData = {ret.getInt("workid"), ret.getString("label"), ret.getString("author"), ret.getInt("genre"), ret.getString("name")};
                wModel.addRow(rowData);
                totalfound++;
            }
            logArea.setText((logArea.getText() + "\nOK: " + (totalfound>0?totalfound + " works found":"no works found")).trim());
            worksCTable.setModel(wModel);
            worksCTable.removeColumn(worksCTable.getColumnModel().getColumn(3));
            worksCTable.removeColumn(worksCTable.getColumnModel().getColumn(0));
            rightPanel.revalidate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            logArea.setText((logArea.getText() + "\nselect works FATAL ERROR:\n" + e.getMessage()).trim());
            logArea.setForeground(fatalColor);
        }

        try{
            ResultSet ret = window.getSession().executeQuery(equery);
            int totalfound = 0;
            while(ret.next()){
                String year = String.valueOf(ret.getInt("year"));
                if (ret.wasNull()){
                    year = "";
                }
                Object[] rowData = {ret.getInt("workid"), ret.getString("label"), ret.getInt("editionid"), ret.getString("publisher"), year, ret.getString("count"), ret.getInt("rule"), ret.getString("description")};
                eModel.addRow(rowData);
                totalfound++;
            }
            logArea.setText((logArea.getText() + "\nOK: " + (totalfound>0?totalfound + " editions found":"no editions found")).trim());
            editionsCTable.setModel(eModel);
            editionsCTable.removeColumn(editionsCTable.getColumnModel().getColumn(6));
            editionsCTable.removeColumn(editionsCTable.getColumnModel().getColumn(2));
            editionsCTable.removeColumn(editionsCTable.getColumnModel().getColumn(0));
            rightPanel.revalidate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            logArea.setText((logArea.getText() + "\nselect editions FATAL ERROR:\n" + e.getMessage()).trim());
            logArea.setForeground(fatalColor);
        }

        try{
            ResultSet ret = window.getSession().executeQuery(iquery);
            int totalfound = 0;
            while(ret.next()){
                Object[] rowData = {ret.getString("stocknum"), ret.getInt("editionid"), ret.getString("edition"), ret.getInt("workid"), ret.getString("work"), ret.getInt("hall"), ret.getInt("rack"), ret.getInt("shelf"), ret.getDate("entrydate"), ret.getDate("retiredate"), ret.getInt("available")};
                iModel.addRow(rowData);
                totalfound++;
            }
            logArea.setText((logArea.getText() + "\nOK: " + (totalfound>0?totalfound + " instances found":"no instances found")).trim());
            instancesCTable.setModel(iModel);
            instancesCTable.removeColumn(instancesCTable.getColumnModel().getColumn(10));
            instancesCTable.removeColumn(instancesCTable.getColumnModel().getColumn(3));
            instancesCTable.removeColumn(instancesCTable.getColumnModel().getColumn(1));
            rightPanel.revalidate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            logArea.setText((logArea.getText() + "\nselect instances FATAL ERROR:\n" + e.getMessage()).trim());
            logArea.setForeground(fatalColor);
        }

    }

    private String getWorksFilters(String start){
        String query = "";
        int condCount = (fLabelField.getText().length()>0 ?1:0) + (fAuthorField.getText().length()>0 ?1:0) + (((Genre)genreBox.getSelectedItem()).getId() > 0 ?1:0);
        if (condCount > 0){
            query = query.concat(start);
        }
        int i = 0;

        if (fLabelField.getText().length()>0){
            query = query.concat("lower(label) like \'%"+fLabelField.getText().toLowerCase(Locale.ROOT)+"%\'");
            query = (++i < condCount) ? query.concat(" and\n") : query.concat("\n");
        }

        if (fAuthorField.getText().length()>0){
            query = query.concat("lower(author) like \'%"+fAuthorField.getText().toLowerCase(Locale.ROOT)+"%\'");
            query = (++i < condCount) ? query.concat(" and\n") : query.concat("\n");
        }

        if (((Genre)genreBox.getSelectedItem()).getId() > 0){
            query = query.concat("genre = "+((Genre)genreBox.getSelectedItem()).getId());
            query = (++i < condCount) ? query.concat(" and\n") : query.concat("\n");
        }

        return query;

    }

    private String getEditionsFilters(String start){
        String query = "";
        int condCount = (fPublisherField.getText().length()>0 ?1:0) + (fFromYearField.getText().length()>0 ?1:0) + (fToYearField.getText().length()>0 ?1:0);
        if (condCount > 0){
            query = query.concat(start);
        }

        int i = 0;
        if (fPublisherField.getText().length()>0){
            query = query.concat("lower(publisher) like \'%"+fPublisherField.getText().toLowerCase(Locale.ROOT)+"%\'");
            query = (++i < condCount) ? query.concat(" and\n") : query.concat("\n");
        }

        if (fFromYearField.getText().length()>0){
            query = query.concat("year >= "+fFromYearField.getText());
            query = (++i < condCount) ? query.concat(" and\n") : query.concat("\n");
        }

        if (fToYearField.getText().length()>0){
            query = query.concat("year <= "+fToYearField.getText());
            query = (++i < condCount) ? query.concat(" and\n") : query.concat("\n");
        }
        return query;
    }

    private String getInstancesFilters(String start){
        String query = "";
        int condCount = (fStocknumField.getText().length()>0 ?1:0) + (fHallField.getText().length()>0 ?1:0) + (fRackField.getText().length()>0 ?1:0) + (fShelfField.getText().length()>0 ?1:0) +
                (fFromEntryField.getText().length()>0 && checkDate(fFromEntryField)?1:0) + (fToEntryField.getText().length()>0 && checkDate(fToEntryField)?1:0) + (fFromRetireField.getText().length()>0 && checkDate(fFromRetireField)?1:0) + (fToRetireField.getText().length()>0 && checkDate(fToRetireField)?1:0)+
                (((Genre)(libraryBox.getSelectedItem())).getId() == -1 ? 0:1);
        if (condCount > 0){
            query = query.concat(start);
        }

        if (fFromEntryField.getText().length()>0 && !checkDate(fFromEntryField)){
            logArea.setText((logArea.getText() + "\nWARNING: дата поступления(с) не формате DD.MM.YYYY").trim());
            logArea.setForeground(errorColor);
        }
        if (fToEntryField.getText().length()>0 && !checkDate(fToEntryField)){
            logArea.setText((logArea.getText() + "\nWARNING: дата поступления(по) не формате DD.MM.YYYY").trim());
            logArea.setForeground(errorColor);
        }
        if (fFromRetireField.getText().length()>0 && !checkDate(fFromRetireField)){
            logArea.setText((logArea.getText() + "\nWARNING: дата списания(с) не формате DD.MM.YYYY").trim());
            logArea.setForeground(errorColor);
        }
        if (fToRetireField.getText().length()>0 && !checkDate(fToRetireField)){
            logArea.setText((logArea.getText() + "\nWARNING: дата списания(по) не формате DD.MM.YYYY").trim());
            logArea.setForeground(errorColor);
        }

        int i = 0;
        if (fStocknumField.getText().length()>0){
            query = query.concat("lower(stocknum) like \'%"+fStocknumField.getText().toLowerCase(Locale.ROOT)+"%\'");
            query = (++i < condCount) ? query.concat(" and\n") : query.concat("\n");
        }
        if (fHallField.getText().length()>0){
            query = query.concat("hall = "+fHallField.getText());
            query = (++i < condCount) ? query.concat(" and\n") : query.concat("\n");
        }
        if (fRackField.getText().length()>0){
            query = query.concat("rack = "+fRackField.getText());
            query = (++i < condCount) ? query.concat(" and\n") : query.concat("\n");
        }
        if (fShelfField.getText().length()>0){
            query = query.concat("shelf = "+fShelfField.getText());
            query = (++i < condCount) ? query.concat(" and\n") : query.concat("\n");
        }
        if (fFromEntryField.getText().length()>0 && checkDate(fFromEntryField)){
            query = query.concat("entrydate >= TO_DATE('"+fFromEntryField.getText()+"', 'DD.MM.YYYY')");
            query = (++i < condCount) ? query.concat(" and\n") : query.concat("\n");
        }
        if (fToEntryField.getText().length()>0 && checkDate(fToEntryField)){
            query = query.concat("entrydate <= TO_DATE('"+fToEntryField.getText()+"', 'DD.MM.YYYY')");
            query = (++i < condCount) ? query.concat(" and\n") : query.concat("\n");
        }
        if (fFromRetireField.getText().length()>0 && checkDate(fFromRetireField)){
            query = query.concat("retiredate >= TO_DATE('"+fFromRetireField.getText()+"', 'DD.MM.YYYY')");
            query = (++i < condCount) ? query.concat(" and\n") : query.concat("\n");
        }
        if (fToRetireField.getText().length()>0 && checkDate(fToRetireField)){
            query = query.concat("retiredate <= TO_DATE('"+fToRetireField.getText()+"', 'DD.MM.YYYY')");
            query = (++i < condCount) ? query.concat(" and\n") : query.concat("\n");
        }
        if (((Genre)(libraryBox.getSelectedItem())).getId() == 0){
            query = query.concat("library = "+ window.getLibrarian().getLibrary());
            query = (++i < condCount) ? query.concat(" and\n") : query.concat("\n");
        }
        else if (((Genre)(libraryBox.getSelectedItem())).getId() > 0){
            query = query.concat("library = "+((Genre)(libraryBox.getSelectedItem())).getId());
            query = (++i < condCount) ? query.concat(" and\n") : query.concat("\n");
        }
        return query;
    }

    private void createUIComponents() {

        selectedEditionsTable = new JTable(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        selectedWorksTable = new JTable(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        genreBox = new JComboBox<Genre>(new Genre[]{new Genre(0,"Выберите жанр")});

        editGenreBox = new JComboBox<Genre>(new Genre[]{new Genre(0,"Выберите жанр")});

    }

    private boolean checkDate(JTextField field){
        String regex = "^\\d{2}\\.\\d{2}\\.\\d{4}$";
        return field.getText().matches(regex);
    }

    private boolean checkIFilling(){
        boolean ok = true;
        if (stocknumField.getText().length()==0){
            logArea.setText((logArea.getText()+"\nERROR: инвентарный номер не задан").trim());
            logArea.setForeground(fatalColor);
            ok = false;
        }
        if (editionLink <= 0){
            logArea.setText((logArea.getText()+"\nERROR: издание не задано").trim());
            logArea.setForeground(fatalColor);
            ok = false;
        }
        if (hallField.getText().length()==0){
            logArea.setText((logArea.getText()+"\nERROR: номер зала не задан").trim());
            logArea.setForeground(fatalColor);
            ok = false;
        }
        else if(!hallField.getText().matches("\\d+")){
            logArea.setText((logArea.getText()+"\nERROR: номер зала не является числом").trim());
            logArea.setForeground(fatalColor);
            ok = false;
        }
        if (rackField.getText().length()==0){
            logArea.setText((logArea.getText()+"\nERROR: номер стеллажа не задан").trim());
            logArea.setForeground(fatalColor);
            ok = false;
        }
        else if(!rackField.getText().matches("\\d+")){
            logArea.setText((logArea.getText()+"\nERROR: номер стеллажа не является числом").trim());
            logArea.setForeground(fatalColor);
            ok = false;
        }
        if (shelfField.getText().length()==0){
            logArea.setText((logArea.getText()+"\nERROR: номер полки не задан").trim());
            logArea.setForeground(fatalColor);
            ok = false;
        }
        else if(!shelfField.getText().matches("\\d+")){
            logArea.setText((logArea.getText()+"\nERROR: номер полки не является числом").trim());
            logArea.setForeground(fatalColor);
            ok = false;
        }
        return ok;
    }

    private boolean checkWFilling(){
        boolean ok = true;
        if (labelField.getText().length()==0){
            logArea.setText((logArea.getText()+"\nERROR: название не задано").trim());
            logArea.setForeground(fatalColor);
            ok = false;
        }
        if (authorField.getText().length()==0){
            logArea.setText((logArea.getText()+"\nERROR: автор не задан").trim());
            logArea.setForeground(fatalColor);
            ok = false;
        }
        if (((Genre)editGenreBox.getSelectedItem()).getId() <= 0){
            System.out.println("ass"+((Genre)editGenreBox.getSelectedItem()).getId());
            logArea.setText((logArea.getText()+"\nERROR: жанр не задан").trim());
            logArea.setForeground(fatalColor);
            ok = false;
        }
        return ok;
    }

    private boolean checkEFilling(){
        boolean ok = true;

        if (workLink <= 0){
            logArea.setText((logArea.getText()+"\nERROR: произведение не задано").trim());
            logArea.setForeground(fatalColor);
            ok = false;
        }
        if (publisherField.getText().length()==0){
            logArea.setText((logArea.getText()+"\nERROR: издательство не задано").trim());
            logArea.setForeground(fatalColor);
            ok = false;
        }
        if (yearField.getText().length()>0 && !(yearField.getText().matches("\\d+"))){
            logArea.setText((logArea.getText()+"\nERROR: год издания зада не правильно").trim());
            logArea.setForeground(fatalColor);
            ok = false;
        }
        return ok;
    }

    public void reset(){
        logArea.setText("");
        logArea.setForeground(okColor);
        updateConsts();
        updateData(false,false);
        instanceId = "";
        workId = 0;
        editionId = 0;
        editionLink = 0;
        workLink = 0;

        fLabelField.setText("");
        fAuthorField.setText("");
        genreBox.setSelectedIndex(0);
        fPublisherField.setText("");
        fFromYearField.setText("");
        fToYearField.setText("");
        fStocknumField.setText("");
        fHallField.setText("");
        fRackField.setText("");
        fShelfField.setText("");
        existsCheckBox.setSelected(false);
        availableCheckBox.setSelected(false);
        retiredCheckBox.setSelected(false);
        borrowedCheckBox.setSelected(false);

        activeWorks.getDataVector().clear();
        activeEditions.getDataVector().clear();

        StatsPanel.setVisible(false);
        setFiltersVisible(true);

        selectedInstanceInfo.setVisible(false);
        selectedWorkInfo.setVisible(false);
        selectedEditionInfo.setVisible(false);
        leftPanel.revalidate();
        InstancesInfoPanel.revalidate();
    }

    private void setFiltersVisible(boolean visible){
        FiltersPane.setVisible(visible);
        ApplyPanel.setVisible(visible);
        FiltersLabel.setVisible(visible);
    }

}
