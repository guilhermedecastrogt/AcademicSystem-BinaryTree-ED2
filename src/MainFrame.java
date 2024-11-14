import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
        JButton btnDeletar = new JButton("Deletar");

        buttonPanel.add(btnIncluir);
        buttonPanel.add(btnBuscar);
        buttonPanel.add(btnListar);
        buttonPanel.add(btnCarregar);
        buttonPanel.add(btnDeletar);

        add(buttonPanel, BorderLayout.NORTH);

        // Configuração da tabela
        String[] columnNames = {"Matricula", "Nome", "Turno", "Periodo", "Enfase", "Curso"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnIncluir.addActionListener(this::incluirAluno);
        btnBuscar.addActionListener(this::buscarAluno);
        btnListar.addActionListener(this::listarAlunos);
        btnCarregar.addActionListener(this::carregarArquivo);
        btnDeletar.addActionListener(this::deletarAluno);

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
            String matricula = matriculaField.getText();
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
        new SearchDialog(this, alunoList);
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
            sortedAlunos.sort((a, b) -> byName ? b.getNome().compareTo(a.getNome()) : b.getMatricula().compareTo(a.getMatricula()));
        }

        tableModel.setRowCount(0);
        for (Aluno aluno : sortedAlunos) {
            tableModel.addRow(new Object[]{aluno.getMatricula(), aluno.getNome(), aluno.getTurno(), aluno.getPeriodo(), aluno.getEnfase(), aluno.getCurso()});
        }

        return sortedAlunos;
    }

    private void carregarArquivo(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            alunoList.clear(); // Limpa a lista antes de carregar novos dados
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(";");
                    if (data.length == 6) {
                        String matricula = data[0];
                        String nome = data[1];
                        Turno turno = Turno.valueOf(data[2].toUpperCase());
                        int periodo = Integer.parseInt(data[3]);
                        String enfase = data[4];
                        String curso = data[5];
                        Aluno aluno = new Aluno(matricula, nome, turno, periodo, enfase, curso);
                        alunoList.addAluno(aluno);
                    }
                }
                JOptionPane.showMessageDialog(this, "Dados carregados com sucesso!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar o arquivo: " + ex.getMessage());
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Erro nos dados do arquivo: " + ex.getMessage());
            }
        }
    }

    private void deletarAluno(ActionEvent e) {
        new DeleteDialog(this, alunoList, binaryTree);
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}
