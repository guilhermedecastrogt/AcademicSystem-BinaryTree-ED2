import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SearchResultFrame extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;

    public SearchResultFrame(List<Aluno> sortedAlunos) {
        setTitle("Resultado da Busca");
        setSize(600, 400);
        setLayout(new BorderLayout());

        String[] columnNames = {"Matricula", "Nome", "Turno", "Periodo", "Enfase", "Curso"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        for (Aluno aluno : sortedAlunos) {
            tableModel.addRow(new Object[]{aluno.getMatricula(), aluno.getNome(), aluno.getTurno(), aluno.getPeriodo(), aluno.getEnfase(), aluno.getCurso()});
        }

        add(new JScrollPane(table), BorderLayout.CENTER);
        setVisible(true);
    }
}
