import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class Client extends JFrame {

    private LoginPanel loginPanel;
    private MenuPanel menuPanel;
    private ReadersInfo readersInfo;
    private InstancesInfo instancesInfo;
    private BorrowFrame borrowFrame;
    private LibrariansInfo librariansInfo;
    private Librarian librarian;
    private Session session = null;

    public Client(){
        super("La Biblioteca");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setLayout(new BorderLayout());
        loginPanel = new LoginPanel(this);
        menuPanel = new MenuPanel(this);
        readersInfo = new ReadersInfo(this);
        instancesInfo = new InstancesInfo(this);
        borrowFrame = new BorrowFrame(this);
        librariansInfo = new LibrariansInfo(this);

        setLoginView();
        setVisible(true);
    }

    public void setLoginView(){
        this.setContentPane(loginPanel);
        loginPanel.revalidate();
        pack();
        setVisible(true);
    }

    public void setMenuView(){
        this.setContentPane(menuPanel);
        menuPanel.revalidate();
        pack();
        setVisible(true);
    }

    public void setReadersView(){
        this.setContentPane(readersInfo.getPanel());
        readersInfo.reset();
        readersInfo.getPanel().revalidate();
        pack();
        setVisible(true);
    }

    public void setInstancesView(){
        instancesInfo.reset();
        this.setContentPane(instancesInfo.getPanel());
        instancesInfo.getPanel().revalidate();
        pack();
        setVisible(true);
    }

    public void setBorrowFrame(String instanceId){
        this.setContentPane(borrowFrame.getPanel());
        borrowFrame.getPanel().revalidate();
        pack();
        setVisible(true);
        borrowFrame.reset();
        if (!instanceId.equals("")){
            borrowFrame.setInstance(instanceId);
        }
    }

    public void setLibrariansView(){
        librariansInfo.reset();
        this.setContentPane(librariansInfo.getPanel());
        librariansInfo.getPanel().revalidate();
        pack();
        setVisible(true);
    }

    public boolean login(String username, String password) {
        try {
            session = new Session(username, password, "84.237.50.81", 1521, "xe");
            System.out.println("connected");

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        setMenuView();
        menuPanel.updateConsts();
        return true;
    }

    public void logout(){
        System.out.println("disconnected");
        setLoginView();
    }

    public void setLibrarian(Librarian librarian){
        this.librarian = librarian;
    }
    public Librarian getLibrarian(){
        return this.librarian;
    }

    public Session getSession(){
        return session;
    }

    public void setNewReaderView() {

    }
}
