import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchDialog extends JDialog {
    private AlunoList alunoList;
    private JTable resultTable;
    private JTextField matriculaField;
    private JTextField nomeField;
    private JComboBox<String> orderByComboBox;
    private DefaultTableModel tableModel;

    public SearchDialog(JFrame parent, AlunoList alunoList) {
        super(parent, "Buscar Aluno", true);
        this.alunoList = alunoList;

        setLayout(new BorderLayout());
        setSize(600, 400);
        setLocationRelativeTo(parent);

        JPanel searchPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        searchPanel.add(new JLabel("Buscar por Matrícula:"));
        matriculaField = new JTextField();
        searchPanel.add(matriculaField);

        searchPanel.add(new JLabel("Buscar por Nome:"));
        nomeField = new JTextField();
        searchPanel.add(nomeField);

        searchPanel.add(new JLabel("Ordenar por:"));
        orderByComboBox = new JComboBox<>(new String[] {
                "Nome Crescente", "Nome Decrescente",
                "Matricula Crescente", "Matricula Decrescente"
        });
        searchPanel.add(orderByComboBox);

        add(searchPanel, BorderLayout.NORTH);

        JButton searchButton = new JButton("Buscar");
        searchButton.addActionListener(this::performSearch);
        add(searchButton, BorderLayout.SOUTH);

        // Configuração da tabela de resultados
        String[] columnNames = {"Matricula", "Nome", "Turno", "Periodo", "Enfase", "Curso"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultTable = new JTable(tableModel);
        add(new JScrollPane(resultTable), BorderLayout.CENTER);

        setVisible(true);
    }

    private void performSearch(ActionEvent e) {
        String matricula = matriculaField.getText().trim();
        String nome = nomeField.getText().trim();
        String orderBy = (String) orderByComboBox.getSelectedItem();

        // Filtrando os alunos com base nos campos preenchidos
        List<Aluno> filteredAlunos = alunoList.getAlunos().stream()
                .filter(aluno -> (matricula.isEmpty() || aluno.getMatricula().equalsIgnoreCase(matricula)) &&
                        (nome.isEmpty() || aluno.getNome().toLowerCase().contains(nome.toLowerCase())))
                .collect(Collectors.toList());

        // Ordenando com base na seleção do combobox
        if (orderBy != null) {
            switch (orderBy) {
                case "Nome Crescente":
                    filteredAlunos.sort((a, b) -> a.getNome().compareTo(b.getNome()));
                    break;
                case "Nome Decrescente":
                    filteredAlunos.sort((a, b) -> b.getNome().compareTo(a.getNome()));
                    break;
                case "Matricula Crescente":
                    filteredAlunos.sort((a, b) -> a.getMatricula().compareTo(b.getMatricula()));
                    break;
                case "Matricula Decrescente":
                    filteredAlunos.sort((a, b) -> b.getMatricula().compareTo(a.getMatricula()));
                    break;
            }
        }

        tableModel.setRowCount(0);
        for (Aluno aluno : filteredAlunos) {
            tableModel.addRow(new Object[]{
                    aluno.getMatricula(), aluno.getNome(), aluno.getTurno(),
                    aluno.getPeriodo(), aluno.getEnfase(), aluno.getCurso()
            });
        }
    }
}