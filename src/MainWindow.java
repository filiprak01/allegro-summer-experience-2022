import org.kohsuke.github.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.*;

public class MainWindow extends JFrame {
    //variables
    private final String token = "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";
    private List<Map<String, Long>> maps;
    private JTextField tf_repositoryName;
    private JTable jt_repositorySelect;
    private JTable jt_repositoryDetail;
    private JPanel mainWindow;
    private JButton jb_dataButton;
    //constructor
    public MainWindow(String title) {
        super(title);
        setContentPane(mainWindow);
        String column_names1[] = {"Repositories"};
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        jb_dataButton.addActionListener(new buttonListener());
        jt_repositorySelect.getSelectionModel().addListSelectionListener(new tableListener());
    }


    public List<Map<String, Long>> getMaps() {
        return maps;
    }

    public void setMaps(List<Map<String, Long>> maps) {
        this.maps = maps;
    }

    public JTextField getTf_repositoryName() {
        return tf_repositoryName;
    }

    private void createUIComponents() {

        String header1[] = {"Repositories"};
        String header2[] = {"Language", "Bytes"};
        TableModel model1 = new DefaultTableModel(header1, 0);
        TableModel model2 = new DefaultTableModel(header2, 0);
        jt_repositoryDetail = new JTable(model2);
        jt_repositorySelect = new JTable(model1);
    }
    //listeners
    class tableListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            int k = jt_repositorySelect.getSelectedRow();
            if (k > -1) {
                Map<String, Long> map = getMaps().get(k);
                //set clear table
                String rows[] = new String[2];
                DefaultTableModel model = (DefaultTableModel) jt_repositoryDetail.getModel();
                model.setRowCount(0);
                for (Map.Entry<String, Long> entry : map.entrySet()
                ) {
                    rows[0] = entry.getKey();
                    rows[1] = String.valueOf(entry.getValue());
                    model.addRow(rows);
                }
            }
        }
    }

    class buttonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String string_username = getTf_repositoryName().getText();
            try {
                GitHub git = GitHub.connectUsingOAuth(token);
                GHUser user = git.getUser(string_username);
                PagedIterable<GHRepository> repositories_paged = user.listRepositories();
                List<GHRepository> repositories_list = repositories_paged.asList();
                List<String> repositories_names = new ArrayList<>();
                List<Map<String, Long>> languages_list = new ArrayList<>();
                for (GHRepository repository : repositories_list
                ) {
                    repositories_names.add(repository.getName());
                    //foreach repository
                    Map<String, Long> languages = repository.listLanguages();
                    languages_list.add(languages);
                }
                setMaps(languages_list);
                DefaultTableModel model = (DefaultTableModel) jt_repositorySelect.getModel();
                model.setRowCount(0);
                String data[] = new String[1];
                for (String s : repositories_names
                ) {
                    data[0] = s;
                    model.addRow(data);
                }


            } catch (IOException exc) {

            }
        }
    }
    public static void main(String[] args) throws IOException {
        MainWindow window = new MainWindow("Github repository");
    }
}
