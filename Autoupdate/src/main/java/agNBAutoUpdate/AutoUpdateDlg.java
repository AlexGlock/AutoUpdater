/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package agNBAutoUpdate;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.Timer;
import javax.swing.filechooser.FileFilter;

public class AutoUpdateDlg extends JFrame {

    String DefaultPath;

    boolean bAutoRun;

    String UserName;

    String PassWord;

    private JButton Browse;

    private JButton Dismiss;

    private JButton FIND;

    private JTextField IPAddress;

    private JCheckBox RebootCheck;

    private JButton Update;

    private JLabel jLabel1;

    private JLabel jLabel2;

    private JMenu jMenu1;

    private JMenu jMenu2;

    private JMenuBar jMenuBar1;

    private JTextField tfFileName;

    protected void ParseCommandLine(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].charAt(0) == '-') {
                switch (args[i].charAt(1)) {
                    case 'I':
                    case 'i':
                        this.IPAddress.setText(args[i].substring(2));
                        break;
                    case 'F':
                    case 'f':
                        this.tfFileName.setText(args[i].substring(2));
                        break;
                    case 'A':
                    case 'a':
                        this.bAutoRun = true;
                        break;
                    case 'T':
                    case 't':
                        setAlwaysOnTop(true);
                        break;
                    case 'R':
                    case 'r':
                        this.RebootCheck.setSelected(true);
                        break;
                    case 'U':
                    case 'u':
                        this.UserName = args[i].substring(2);
                        break;
                    case 'P':
                    case 'p':
                        this.PassWord = args[i].substring(2);
                        break;
                    case '?':
                        System.out.println("Usage is \n java -Jar Autoupdate.jar <options>\nOptions are:");
                        System.out.println("-Ixxx.xxx.xxx.xxx Set IP Address");
                        System.out.println("-FFilename Set File name ");
                        System.out.println("-C         Start without GUI");
                        System.out.println("-R         Set reboot");
                        System.out.println("-A         Run Automatically");
                        System.out.println("-Uusername  Set a username");
                        System.out.println("-Ppassword Set a password");
                        System.out.println("-T  Keep dialog on top");
                        break;
                }
            }
        }
    }

    protected void GetDefaults() {
        Preferences prefsRoot = Preferences.userRoot();
        Preferences myPrefFile = prefsRoot.node("com.Autoupdate.preference");
        this.DefaultPath = myPrefFile.get("FilePath", "C:\\nburn\\bin");
        this.IPAddress.setText(myPrefFile.get("LastIp", "0.0.0.0"));
    }

    protected void SaveDefaults() {
        Preferences prefsRoot = Preferences.userRoot();
        Preferences myPrefFile = prefsRoot.node("com.Autoupdate.preference");
        myPrefFile.put("FilePath", this.DefaultPath);
        String s = this.IPAddress.getText();
        myPrefFile.put("LastIp", s);
    }

    public AutoUpdateDlg(String[] args) {
        initComponents();
        GetDefaults();
        this.bAutoRun = false;
        this.UserName = null;
        this.PassWord = null;
        ParseCommandLine(args);
        if (this.bAutoRun) {
            Timer t = new Timer(50, new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    AutoUpdateDlg.this.UpdateActionPerformed(evt);
                }
            });
            t.setRepeats(false);
            t.start();
        }
    }

    class APPFileFilter extends FileFilter {

        public boolean accept(File pathname) {
            if (pathname.isDirectory()) {
                return true;
            }
            if (pathname.getName().endsWith("_APP.s19")) {
                return true;
            }
            return false;
        }

        public String getDescription() {
            return new String("NetBurner App Files");
        }
    }

    private void initComponents() {
        this.jMenuBar1 = new JMenuBar();
        this.jMenu1 = new JMenu();
        this.jMenu2 = new JMenu();
        this.FIND = new JButton();
        this.Browse = new JButton();
        this.Dismiss = new JButton();
        this.tfFileName = new JTextField();
        this.jLabel1 = new JLabel();
        this.jLabel2 = new JLabel();
        this.RebootCheck = new JCheckBox();
        this.Update = new JButton();
        this.IPAddress = new JTextField();
        this.jMenu1.setText("File");
        this.jMenuBar1.add(this.jMenu1);
        this.jMenu2.setText("Edit");
        this.jMenuBar1.add(this.jMenu2);
        setDefaultCloseOperation(3);
        this.FIND.setText("FIND");
        this.FIND.setToolTipText("Find netburner devices");
        this.FIND.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                AutoUpdateDlg.this.FINDActionPerformed(evt);
            }
        });
        this.Browse.setText("Browse...");
        this.Browse.setToolTipText("Browse for file to update");
        this.Browse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                AutoUpdateDlg.this.BrowseActionPerformed(evt);
            }
        });
        this.Dismiss.setText("Dismiss");
        this.Dismiss.setToolTipText("Dismiss the dialog.");
        this.Dismiss.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                AutoUpdateDlg.this.DismissActionPerformed(evt);
            }
        });
        this.tfFileName.setToolTipText("File name to update device with");
        this.jLabel1.setText("File Name");
        this.jLabel2.setText("Device IP");
        this.RebootCheck.setText("Reboot.");
        this.RebootCheck.setToolTipText("Select this to reboot the device after updating.");
        this.Update.setText("Update");
        this.Update.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                AutoUpdateDlg.this.UpdateActionPerformed(evt);
            }
        });
        this.IPAddress.setText("0.0.0.0");
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(this.jLabel1)
                                        .addComponent(this.jLabel2))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(this.RebootCheck)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 134, 32767)
                                                .addComponent(this.Update, -2, 78, -2)
                                                .addGap(19, 19, 19))
                                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                        .addComponent(this.IPAddress, GroupLayout.Alignment.LEADING, -1, 290, 32767)
                                                        .addComponent(this.tfFileName, -1, 290, 32767))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)))
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(this.FIND, -1, 81, 32767)
                                        .addComponent(this.Dismiss, -1, 81, 32767)
                                        .addComponent(this.Browse, -1, 81, 32767))
                                .addContainerGap()));
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(this.jLabel2)
                                                .addComponent(this.IPAddress, -2, -1, -2))
                                        .addComponent(this.FIND))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(this.Browse)
                                        .addComponent(this.tfFileName, -2, -1, -2)
                                        .addComponent(this.jLabel1))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(this.Dismiss)
                                        .addComponent(this.RebootCheck)
                                        .addComponent(this.Update))
                                .addContainerGap(-1, 32767)));
        pack();
    }

    private void DismissActionPerformed(ActionEvent evt) {
        SaveDefaults();
        System.exit(1);
    }

    private void BrowseActionPerformed(ActionEvent evt) {
        JFileChooser fc = new JFileChooser(this.DefaultPath);
        fc.addChoosableFileFilter(new APPFileFilter());
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == 0) {
            File file = fc.getSelectedFile();
            this.tfFileName.setText(file.getPath());
            this.DefaultPath = fc.getCurrentDirectory().getPath();
        }
    }

    private void FINDActionPerformed(ActionEvent evt) {
        FindNetBurnerDevicesDlg find_dlg = new FindNetBurnerDevicesDlg(this, true);
        find_dlg.setVisible(true);
        if (find_dlg.IsValid()) {
            String s = find_dlg.SelectedDevice();
            int index = s.indexOf(" at ");
            if (index > 0) {
                index += 4;
            }
            s = s.substring(index);
            index = s.indexOf(" ");
            s = s.substring(0, index);
            if (s.indexOf("/") == 0) {
                s = s.substring(1);
            }
            this.IPAddress.setText(s);
        }
    }

    private void UpdateActionPerformed(ActionEvent evt) {
        UpdateActionDialog udlg = new UpdateActionDialog(this, this.IPAddress.getText(), this.tfFileName.getText(), this.RebootCheck.isSelected(), this.UserName, this.PassWord);
        udlg.setVisible(true);
        if (udlg.Error_Cause == 5) {
            PassWordRequest pReq = new PassWordRequest(this, true);
            pReq.setVisible(true);
            if (pReq.m_bValid) {
                this.UserName = pReq.m_UserName;
                this.PassWord = pReq.m_Password;
                UpdateActionDialog udlgp = new UpdateActionDialog(this, this.IPAddress.getText(), this.tfFileName.getText(), this.RebootCheck.isSelected(), this.UserName, this.PassWord);
                udlgp.setVisible(true);
            }
        }
        if (this.bAutoRun && udlg.Error_Cause == 0) {
            SaveDefaults();
            System.exit(0);
        }
    }

    public static void main(final String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                (new AutoUpdateDlg(args)).setVisible(true);
            }
        });
    }
}
