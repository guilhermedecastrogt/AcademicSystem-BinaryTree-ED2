import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

public class MainFrame extends JFrame {
    private AlunoList alunoList;
    private BinaryTree binaryTree;
    private JTable table;
    private DefaultTableModel tableModel;

    public MainFrame() {
        alunoList = new AlunoList();
        binaryTree = new BinaryTree();

        setTitle("Sistema de Gerenciamento de Alunos");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        JButton btnIncluir = new JButton("Incluir");
        JButton btnBuscar = new JButton("Buscar");
        JButton btnListar = new JButton("Listar");
        JButton btnCarregar = new JButton("Carregar Arquivo");

        buttonPanel.add(btnIncluir);
        buttonPanel.add(btnBuscar);
        buttonPanel.add(btnListar);
        buttonPanel.add(btnCarregar);

        add(buttonPanel, BorderLayout.NORTH);

        String[] columnNames = {"Matricula", "Nome", "Turno", "Periodo", "Enfase", "Curso"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnIncluir.addActionListener(this::incluirAluno);
        btnBuscar.addActionListener(this::buscarAluno);
        btnListar.addActionListener(this::listarAlunos);
        btnCarregar.addActionListener(this::carregarArquivo);

        setVisible(true);
    }

    private void incluirAluno(ActionEvent e) {
        JTextField matriculaField = new JTextField(5);
        JTextField nomeField = new JTextField(10);
        JComboBox<Turno> turnoComboBox = new JComboBox<>(Turno.values());
        JTextField periodoField = new JTextField(5);
        JTextField enfaseField = new JTextField(10);
        JTextField cursoField = new JTextField(10);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2));
        panel.add(new JLabel("Matricula:"));
        panel.add(matriculaField);
        panel.add(new JLabel("Nome:"));
        panel.add(nomeField);
        panel.add(new JLabel("Turno:"));
        panel.add(turnoComboBox);
        panel.add(new JLabel("Periodo:"));
        panel.add(periodoField);
        panel.add(new JLabel("Enfase:"));
        panel.add(enfaseField);
        panel.add(new JLabel("Curso:"));
        panel.add(cursoField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Incluir Aluno", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int matricula = Integer.parseInt(matriculaField.getText());
            String nome = nomeField.getText();
            Turno turno = (Turno) turnoComboBox.getSelectedItem();
            int periodo = Integer.parseInt(periodoField.getText());
            String enfase = enfaseField.getText();
            String curso = cursoField.getText();

            Aluno aluno = new Aluno(matricula, nome, turno, periodo, enfase, curso);
            alunoList.addAluno(aluno);
        }
    }

    private void buscarAluno(ActionEvent e) {
        String[] options = {"Nome Crescente", "Nome Decrescente", "Matricula Crescente", "Matricula Decrescente"};
        String option = (String) JOptionPane.showInputDialog(this, "Selecione a ordem de busca:",
                "Buscar Aluno", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (option != null) {
            boolean byName = option.contains("Nome");
            boolean ascending = option.contains("Crescente");
            List<Aluno> sortedAlunos = listarOrdenado(byName, ascending);

            new SearchResultFrame(sortedAlunos);
        }
    }

    private void listarAlunos(ActionEvent e) {
        listarOrdenado(true, true);
    }

    private List<Aluno> listarOrdenado(boolean byName, boolean ascending) {
        binaryTree.clear();
        for (Aluno aluno : alunoList.getAlunos()) {
            binaryTree.insert(aluno, byName);
        }

        List<Aluno> sortedAlunos = new LinkedList<>();
        binaryTree.inOrderTraversal(sortedAlunos);
        if (!ascending) {
            sortedAlunos.sort((a, b) -> byName ? b.getNome().compareTo(a.getNome()) : b.getMatricula() - a.getMatricula());
        }

        tableModel.setRowCount(0);
        for (Aluno aluno : sortedAlunos) {
            tableModel.addRow(new Object[]{aluno.getMatricula(), aluno.getNome(), aluno.getTurno(), aluno.getPeriodo(), aluno.getEnfase(), aluno.getCurso()});
        }

        return sortedAlunos;
    }

    private void carregarArquivo(ActionEvent e) {
        alunoList.clear();
        alunoList.addAluno(new Aluno(1, "Joao", Turno.MATUTINO, 1, "Computacao", "Engenharia"));
        alunoList.addAluno(new Aluno(2, "Maria", Turno.NOTURNO, 3, "Matematica", "Licenciatura"));
        JOptionPane.showMessageDialog(this, "Dados carregados na lista!");
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}