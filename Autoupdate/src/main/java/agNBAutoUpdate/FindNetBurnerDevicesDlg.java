package agNBAutoUpdate;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class FindNetBurnerDevicesDlg extends JDialog {

    String selected_device;

    boolean bValid;

    DefaultListModel listModel;

    DoFinder nbf;

    private JButton CancelButton;

    private JList DeviceList;

    private JScrollPane DeviceListPane;

    private JButton OKButton;

    private JButton SearchAgainButton;

    private JLabel jLabel1;

    protected class DoFinder extends SwingWorker<ArrayList<NetBurnerDevice>, NetBurnerDevice> implements NetBurnerCoreFinder.FindNotify {

        ArrayList<NetBurnerDevice> TheList;

        int index = 0;

        DefaultListModel TheDeviceGuiList;

        public DoFinder(DefaultListModel l) {
            this.TheDeviceGuiList = l;
        }

        public ArrayList<NetBurnerDevice> doInBackground() {
            return NetBurnerCoreFinder.CoreFinderAction(this);
        }

        public void FoundADevice(NetBurnerDevice d) {
            publish(new NetBurnerDevice[]{d});
        }

        public void FindAllDone() {
            FindNetBurnerDevicesDlg.this.SearchAgainButton.setEnabled(true);
        }

        protected void process(List<NetBurnerDevice> devices) {
            for (NetBurnerDevice dev : devices) {
                if (this.TheDeviceGuiList != null) {
                    String s = dev.toString()+" MAC: "+dev.GetRootMac();
                    this.TheDeviceGuiList.addElement(s);
                }
            }
        }

        protected void done() {
        }
    }

    public boolean IsValid() {
        return this.bValid;
    }

    public String SelectedDevice() {
        return this.selected_device;
    }

    public FindNetBurnerDevicesDlg(Frame parent, boolean modal) {
        super(parent, modal);
        this.listModel = new DefaultListModel();
        initComponents();
        this.OKButton.setEnabled(false);
        Timer t = new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                FindNetBurnerDevicesDlg.this.SearchAgainButtonActionPerformed(evt);
            }
        });
        t.setRepeats(false);
        t.start();
    }

    private void initComponents() {
        this.OKButton = new JButton();
        this.CancelButton = new JButton();
        this.SearchAgainButton = new JButton();
        this.jLabel1 = new JLabel();
        this.DeviceListPane = new JScrollPane();
        this.DeviceList = new JList();
        setDefaultCloseOperation(2);
        this.OKButton.setText("OK");
        this.OKButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                FindNetBurnerDevicesDlg.this.OKButtonActionPerformed(evt);
            }
        });
        this.CancelButton.setText("Cancel");
        this.CancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                FindNetBurnerDevicesDlg.this.CancelButtonActionPerformed(evt);
            }
        });
        this.SearchAgainButton.setText("Search Again");
        this.SearchAgainButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                FindNetBurnerDevicesDlg.this.SearchAgainButtonActionPerformed(evt);
            }
        });
        this.jLabel1.setText("Select Device");
        this.DeviceList.setFont(new Font("Tahoma", 1, 12));
        this.DeviceList.setModel(this.listModel);
        this.DeviceList.setSelectionMode(0);
        this.DeviceList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt) {
                FindNetBurnerDevicesDlg.this.DeviceListValueChanged(evt);
            }
        });
        this.DeviceListPane.setViewportView(this.DeviceList);
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addContainerGap(333, 32767)
                                                .addComponent(this.OKButton)
                                                .addGap(98, 98, 98)
                                                .addComponent(this.CancelButton))
                                        .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(this.SearchAgainButton)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addComponent(this.jLabel1)))
                                                .addGap(32, 32, 32)
                                                .addComponent(this.DeviceListPane, -2, 816, -2)))
                                .addContainerGap(-1, 32767)));
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(this.jLabel1)
                                                .addGap(23, 23, 23)
                                                .addComponent(this.SearchAgainButton))
                                        .addComponent(this.DeviceListPane, -2, 201, -2))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(this.CancelButton)
                                        .addComponent(this.OKButton))
                                .addContainerGap()));
        pack();
    }

    private void OKButtonActionPerformed(ActionEvent evt) {
        this.bValid = true;
        int index = this.DeviceList.getSelectedIndex();
        if (index != -1) {
            this.selected_device = this.listModel.get(index).toString();
            this.bValid = true;
        } else {
            this.bValid = false;
        }
        setVisible(false);
        dispose();
    }

    private void CancelButtonActionPerformed(ActionEvent evt) {
        this.bValid = false;
        setVisible(false);
        dispose();
    }

    private void StartSearch() {
        this.SearchAgainButton.setEnabled(false);
        this.bValid = false;
        this.listModel.clear();
        DoFinder nbf = new DoFinder(this.listModel);
        nbf.execute();
    }

    private void SearchAgainButtonActionPerformed(ActionEvent evt) {
        StartSearch();
    }

    private void DeviceListValueChanged(ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            if (this.DeviceList.getSelectedIndex() == -1) {
                this.OKButton.setEnabled(false);
            } else {
                this.OKButton.setEnabled(true);
            }
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                FindNetBurnerDevicesDlg dialog = new FindNetBurnerDevicesDlg(new JFrame(), true);
                dialog.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
}
