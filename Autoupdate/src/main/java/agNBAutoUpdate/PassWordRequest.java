package agNBAutoUpdate;

import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

public class PassWordRequest extends JDialog {

    public String m_UserName;

    public String m_Password;

    public boolean m_bValid;

    private JButton Cancel;

    private JButton OK;

    private JTextField PassWord;

    private JLabel UserLabel;

    private JTextField UserName;

    private JLabel jLabel1;

    public PassWordRequest(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    private void initComponents() {
        this.UserLabel = new JLabel();
        this.UserName = new JTextField();
        this.jLabel1 = new JLabel();
        this.PassWord = new JTextField();
        this.OK = new JButton();
        this.Cancel = new JButton();
        setDefaultCloseOperation(2);
        this.UserLabel.setText("UserName");
        this.jLabel1.setText("Password");
        this.OK.setText("OK");
        this.OK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                PassWordRequest.this.OKActionPerformed(evt);
            }
        });
        this.Cancel.setText("Cancel");
        this.Cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                PassWordRequest.this.CancelActionPerformed(evt);
            }
        });
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(this.UserLabel)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(this.UserName, -2, 174, -2))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(6, 6, 6)
                                                .addComponent(this.jLabel1)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(this.PassWord)))
                                .addContainerGap(-1, 32767))
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(125, 32767)
                                .addComponent(this.OK)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(this.Cancel)
                                .addContainerGap()));
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(this.UserLabel)
                                        .addComponent(this.UserName, -2, -1, -2))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(this.PassWord, -2, -1, -2)
                                        .addComponent(this.jLabel1))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(this.OK)
                                        .addComponent(this.Cancel))
                                .addContainerGap(-1, 32767)));
        pack();
    }

    private void OKActionPerformed(ActionEvent evt) {
        this.m_UserName = this.UserName.getText();
        this.m_Password = this.PassWord.getText();
        this.m_bValid = true;
        setVisible(false);
        dispose();
    }

    private void CancelActionPerformed(ActionEvent evt) {
        setVisible(false);
        this.m_bValid = false;
        dispose();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                PassWordRequest dialog = new PassWordRequest(new JFrame(), true);
                dialog.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
}
