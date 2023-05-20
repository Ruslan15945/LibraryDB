import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ReadersPanel extends JPanel {
    private Client window;


    public ReadersPanel(Client window){
        this.window = window;
        setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        setLayout(new GridBagLayout());

        JLabel greetingLabel = new JLabel("Читатели");
        greetingLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 30));
        add(greetingLabel, new GridBagConstraints(0, 0, 6, 1, 0, 0, GridBagConstraints.CENTER,
                GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));

        JLabel filterLabel = new JLabel("Фильтры");
        filterLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));
        add(filterLabel, new GridBagConstraints(0, 1, 2, 1, 0, 0, GridBagConstraints.CENTER,
                GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));


        // Add list
        JTable filters = new JTable(new Object[][]{{"Университет", "НГУ"}, {"Группа", "20203"}}, new Object[]{"Атрибут", "Значение"});
        JScrollPane filtersScrollPane = new JScrollPane(filters);
        add(filtersScrollPane, new GridBagConstraints(0, 2, 2, 1, 1, 1, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 20, 10, 10), 0, 0));

        // Add table
        JTable table = new JTable(new Object[][]{{"Руслан", "Сергеевич", "Морозов"}}, new Object[]{"Имя", "Отчество", "Фамилия"});
        JScrollPane tableScrollPane = new JScrollPane(table);
        add(tableScrollPane, new GridBagConstraints(2, 2, 4, 1, 5, 1, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 10, 10, 20), 0, 0));

        JPanel lbuttonsPanel = new JPanel(new GridLayout(1, 5, 0, 0));
        lbuttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));


        JButton addFilterButton = new JButton("Добавить");
        addFilterButton.setFont(new Font(Font.DIALOG, Font.BOLD, 25));
        addFilterButton.setFocusPainted(false);
        addFilterButton.setContentAreaFilled(false);
        addFilterButton.setBorder(null);
        addFilterButton.setMargin(new Insets(10,100,10,100));
        addFilterButton.setAlignmentX(CENTER_ALIGNMENT);

        JButton performFilterButton = new JButton("Применить");
        performFilterButton.setFont(new Font(Font.DIALOG, Font.BOLD, 25));
        performFilterButton.setFocusPainted(false);
        performFilterButton.setContentAreaFilled(false);
        performFilterButton.setBorder(null);
        performFilterButton.setMargin(new Insets(10,100,10,100));
        performFilterButton.setAlignmentX(CENTER_ALIGNMENT);

        lbuttonsPanel.add(addFilterButton);
        lbuttonsPanel.add(performFilterButton);
        add(lbuttonsPanel, new GridBagConstraints(0, 3, 2, 1, 1, 0, GridBagConstraints.CENTER,
                GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));


        JButton addReaderButton = new JButton("Добавить");
        addReaderButton.setFont(new Font(Font.DIALOG, Font.BOLD, 25));
        addReaderButton.setFocusPainted(false);
        addReaderButton.setContentAreaFilled(false);
        addReaderButton.setBorder(null);
        addReaderButton.setMargin(new Insets(10,100,10,100));
        addReaderButton.setAlignmentX(CENTER_ALIGNMENT);
        add(addReaderButton, new GridBagConstraints(4, 3, 2, 1, 1, 0, GridBagConstraints.CENTER,
                GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));


        JButton backButton = new JButton("Назад");
        backButton.setFont(new Font(Font.DIALOG, Font.BOLD, 30));
        backButton.setFocusPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setBorder(null);
        backButton.setMargin(new Insets(10,100,10,100));
        backButton.setAlignmentX(CENTER_ALIGNMENT);
        add(backButton, new GridBagConstraints(5, 4, 1, 1, 0, 0, GridBagConstraints.CENTER,
                GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));

    }

}
