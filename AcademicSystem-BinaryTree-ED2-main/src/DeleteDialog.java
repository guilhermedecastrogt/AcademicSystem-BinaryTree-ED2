import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.stream.Collectors;

public class DeleteDialog extends JDialog {
    private AlunoList alunoList;
    private BinaryTree binaryTree;
    private JTextField matriculaField;
    private JTextField nomeField;
    private JTable resultTable;
    private DefaultTableModel tableModel;

    public DeleteDialog(JFrame parent, AlunoList alunoList, BinaryTree binaryTree) {
        super(parent, "Deletar Aluno", true);
        this.alunoList = alunoList;
        this.binaryTree = binaryTree;

        setLayout(new BorderLayout());
        setSize(600, 400);
        setLocationRelativeTo(parent);

        JPanel deletePanel = new JPanel(new GridLayout(2, 2, 10, 10));

        deletePanel.add(new JLabel("Deletar por Matrícula:"));
        matriculaField = new JTextField();
        deletePanel.add(matriculaField);

        deletePanel.add(new JLabel("Deletar por Nome:"));
        nomeField = new JTextField();
        deletePanel.add(nomeField);

        add(deletePanel, BorderLayout.NORTH);

        JButton deleteButton = new JButton("Deletar");
        deleteButton.addActionListener(this::performDelete);
        add(deleteButton, BorderLayout.SOUTH);

        String[] columnNames = {"Matricula", "Nome", "Turno", "Periodo", "Enfase", "Curso"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultTable = new JTable(tableModel);
        add(new JScrollPane(resultTable), BorderLayout.CENTER);

        updateTable(alunoList.getAlunos());

        setVisible(true);
    }

    private void performDelete(ActionEvent e) {
        String matricula = matriculaField.getText().trim();
        String nome = nomeField.getText().trim();


        List<Aluno> toDelete = alunoList.getAlunos().stream()
                .filter(aluno -> (!matricula.isEmpty() && aluno.getMatricula().equalsIgnoreCase(matricula)) ||
                        (!nome.isEmpty() && aluno.getNome().equalsIgnoreCase(nome)))
                .collect(Collectors.toList());

        if (!toDelete.isEmpty()) {
            for (Aluno aluno : toDelete) {
                alunoList.getAlunos().remove(aluno);
            }
            binaryTree.clear();
            for (Aluno aluno : alunoList.getAlunos()) {
                binaryTree.insert(aluno, true);
            }
            JOptionPane.showMessageDialog(this, "Aluno(s) deletado(s) com sucesso!");
        } else {
            JOptionPane.showMessageDialog(this, "Aluno não encontrado.");
        }


        updateTable(alunoList.getAlunos());
    }

    private void updateTable(List<Aluno> alunos) {
        tableModel.setRowCount(0);
        for (Aluno aluno : alunos) {
            tableModel.addRow(new Object[]{aluno.getMatricula(), aluno.getNome(), aluno.getTurno(), aluno.getPeriodo(), aluno.getEnfase(), aluno.getCurso()});
        }
    }
}