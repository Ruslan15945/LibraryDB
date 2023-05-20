import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginPanel extends JPanel {
    private Client window;
    private JTextField usernameField;
    private JTextField passwordField;
    private JCheckBox rememberMe;


    public LoginPanel(Client window){
        this.window = window;
        setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JButton loginButton = new JButton("Войти");
        loginButton.setFont(new Font(Font.DIALOG, Font.BOLD, 50));
        loginButton.setFocusPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setBorder(null);
        loginButton.setMargin(new Insets(10,100,10,100));
        loginButton.setAlignmentX(CENTER_ALIGNMENT);

        JButton exitButton = new JButton("Выход");
        exitButton.setFont(new Font(Font.DIALOG, Font.BOLD, 50));
        exitButton.setFocusPainted(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setBorder(null);
        exitButton.setMargin(new Insets(10,100,10,100));
        exitButton.setAlignmentX(CENTER_ALIGNMENT);
        exitButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        JLabel greetingLabel = new JLabel("LA BIBLIOTECA");
        greetingLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 50));
        greetingLabel.setAlignmentX(CENTER_ALIGNMENT);
        greetingLabel.setMaximumSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width, 50));
        greetingLabel.setHorizontalAlignment(JTextField.CENTER);



        usernameField = new JTextField("20203_MOROZOV");
        usernameField.setFont(new Font(Font.DIALOG, Font.PLAIN, 40));
        usernameField.setBackground(null);
        usernameField.setMaximumSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width, 50));
        usernameField.setHorizontalAlignment(JTextField.CENTER);
        usernameField.setBorder(null);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 30));

        usernameLabel.setAlignmentX(CENTER_ALIGNMENT);
        usernameField.setAlignmentX(CENTER_ALIGNMENT);


        passwordField = new JPasswordField("as34cv_87");
        passwordField.setFont(new Font(Font.DIALOG, Font.PLAIN, 40));
        passwordField.setBackground(null);
        passwordField.setMaximumSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width, 50));
        passwordField.setHorizontalAlignment(JTextField.CENTER);
        passwordField.setBorder(null);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 30));

        passwordLabel.setAlignmentX(CENTER_ALIGNMENT);
        passwordField.setAlignmentX(CENTER_ALIGNMENT);

        rememberMe = new JCheckBox("Запомнить меня");
        rememberMe.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));
        rememberMe.setBackground(null);
        rememberMe.setMaximumSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width, 50));
        rememberMe.setHorizontalAlignment(JTextField.CENTER);
        rememberMe.setBorder(null);
        rememberMe.setAlignmentX(CENTER_ALIGNMENT);

        loginButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (window.login(usernameField.getText(),passwordField.getText())){
                    if (rememberMe.isSelected());
                        // TODO: save password
                }
            }
        });

        add(Box.createVerticalGlue());
        add(greetingLabel);
        add(Box.createVerticalGlue());
        add(usernameLabel);
        add(usernameField);
        add(Box.createVerticalGlue());
        add(passwordLabel);
        add(passwordField);
        add(Box.createVerticalGlue());
        add(rememberMe);
        add(Box.createVerticalGlue());
        add(loginButton);
        add(Box.createVerticalStrut(100));
        add(exitButton);
        add(Box.createVerticalGlue());
        setFocusable(true);
        requestFocus();

    }

}
