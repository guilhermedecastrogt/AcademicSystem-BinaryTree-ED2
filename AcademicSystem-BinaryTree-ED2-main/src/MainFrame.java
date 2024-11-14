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
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Painel lateral para os botões
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new GridBagLayout());
        sidePanel.setBorder(BorderFactory.createTitledBorder("Ações"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.NORTH;

        // Botões
        JButton btnIncluir = new JButton("Incluir");
        JButton btnBuscar = new JButton("Buscar");
        JButton btnListar = new JButton("Listar");
        JButton btnCarregar = new JButton("Carregar Arquivo");
        JButton btnDeletar = new JButton("Deletar");

        gbc.gridx = 0;
        gbc.gridy = 0;
        sidePanel.add(btnIncluir, gbc);
        gbc.gridy++;
        sidePanel.add(btnBuscar, gbc);
        gbc.gridy++;
        sidePanel.add(btnListar, gbc);
        gbc.gridy++;
        sidePanel.add(btnCarregar, gbc);
        gbc.gridy++;
        sidePanel.add(btnDeletar, gbc);

        // Adiciona a barra lateral no lado esquerdo da tela
        add(sidePanel, BorderLayout.WEST);

        // Configuração da tabela
        String[] columnNames = {"Matricula", "Nome", "Turno", "Periodo", "Enfase", "Curso"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Alunos"));
        add(tableScrollPane, BorderLayout.CENTER);

        // Eventos dos botões
        btnIncluir.addActionListener(this::incluirAluno);
        btnBuscar.addActionListener(this::buscarAluno);
        btnListar.addActionListener(this::listarAlunos);
        btnCarregar.addActionListener(this::carregarArquivo);
        btnDeletar.addActionListener(this::deletarAluno);

        setVisible(true);
    }

    private void incluirAluno(ActionEvent e) {
        JTextField matriculaField = new JTextField(10);
        JTextField nomeField = new JTextField(15);
        JComboBox<Turno> turnoComboBox = new JComboBox<>(Turno.values());
        JTextField periodoField = new JTextField(10);
        JTextField enfaseField = new JTextField(15);
        JTextField cursoField = new JTextField(15);

        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
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

        int result = JOptionPane.showConfirmDialog(this, panel, "Incluir Aluno", JOptionPane.OK_CANCEL_OPTION);
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
            alunoList.clear();
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
            } catch (IOException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar o arquivo: " + ex.getMessage());
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
