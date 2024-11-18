import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BinaryTreeApp {
    private BinaryTree treeByName;
    private BinaryTree treeByMatricula;
    private JFrame frame;
    private DefaultTableModel tableModel;

    public BinaryTreeApp() {
        treeByName = new BinaryTree();
        treeByMatricula = new BinaryTree();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Gerenciamento de Alunos");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        frame.add(panel);

        tableModel = new DefaultTableModel(new Object[]{"Matrícula", "Nome", "Turno", "Período", "Ênfase", "Curso"}, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton addButton = new JButton("Adicionar Aluno");
        JButton deleteButton = new JButton("Excluir Aluno");
        JButton searchButton = new JButton("Buscar Aluno");
        JButton listButton = new JButton("Listar Alunos");
        JButton loadFileButton = new JButton("Carregar Arquivo");

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(listButton);
        buttonPanel.add(loadFileButton);
        panel.add(buttonPanel, BorderLayout.NORTH);

        addButton.addActionListener(e -> openAddAlunoWindow());
        deleteButton.addActionListener(e -> openDeleteAlunoWindow());
        searchButton.addActionListener(e -> openSearchAlunoWindow());
        listButton.addActionListener(e -> listAlunos());
        loadFileButton.addActionListener(e -> loadFileIntoTree());

        frame.setVisible(true);
    }

    private void loadFileIntoTree() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(frame);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))) {
                String line;
                int lineNumber = 0;

                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    if (lineNumber == 1 && line.startsWith("\ufeff")) {
                        line = line.substring(1);
                    }
                    String[] parts = line.split(";");

                    if (parts.length >= 6) {
                        String matricula = parts[0].trim();
                        String nome = parts[1].trim();
                        Turno turno = Turno.valueOf(parts[2].trim().toUpperCase());
                        int periodo = Integer.parseInt(parts[3].trim());
                        String enfase = parts[4].trim();
                        String curso = parts[5].trim();

                        Aluno aluno = new Aluno(matricula, nome, turno, periodo, enfase, curso);
                        treeByName.insert(aluno, true);
                        treeByMatricula.insert(aluno, false);
                    }
                }

                listAlunos();
                JOptionPane.showMessageDialog(frame, "Arquivo carregado com sucesso!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Erro ao ler o arquivo: " + e.getMessage());
            }
        }
    }


    private void openAddAlunoWindow() {
        JFrame addFrame = new JFrame("Adicionar Aluno");
        addFrame.setSize(400, 400);
        addFrame.setLayout(new GridLayout(8, 2));

        JTextField matriculaField = new JTextField();
        JTextField nomeField = new JTextField();
        JComboBox<Turno> turnoField = new JComboBox<>(Turno.values());
        JTextField periodoField = new JTextField();
        JTextField enfaseField = new JTextField();
        JTextField cursoField = new JTextField();

        JButton saveButton = new JButton("Salvar");
        JButton cancelButton = new JButton("Cancelar");

        addFrame.add(new JLabel("Matrícula:"));
        addFrame.add(matriculaField);
        addFrame.add(new JLabel("Nome:"));
        addFrame.add(nomeField);
        addFrame.add(new JLabel("Turno:"));
        addFrame.add(turnoField);
        addFrame.add(new JLabel("Período:"));
        addFrame.add(periodoField);
        addFrame.add(new JLabel("Ênfase:"));
        addFrame.add(enfaseField);
        addFrame.add(new JLabel("Curso:"));
        addFrame.add(cursoField);
        addFrame.add(saveButton);
        addFrame.add(cancelButton);

        saveButton.addActionListener(e -> {
            try {
                String matricula = matriculaField.getText();
                String nome = nomeField.getText();
                Turno turno = (Turno) turnoField.getSelectedItem();
                int periodo = Integer.parseInt(periodoField.getText());
                String enfase = enfaseField.getText();
                String curso = cursoField.getText();

                if (matricula.isEmpty() || nome.isEmpty() || enfase.isEmpty() || curso.isEmpty()) {
                    JOptionPane.showMessageDialog(addFrame, "Todos os campos são obrigatórios!");
                    return;
                }

                Aluno aluno = new Aluno(matricula, nome, turno, periodo, enfase, curso);
                treeByName.insert(aluno, true);
                treeByMatricula.insert(aluno, false);

                JOptionPane.showMessageDialog(addFrame, "Aluno adicionado com sucesso!");
                addFrame.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addFrame, "Período deve ser um número inteiro.");
            }
        });

        cancelButton.addActionListener(e -> addFrame.dispose());

        addFrame.setVisible(true);
    }


    private void openDeleteAlunoWindow() {
        JFrame deleteFrame = new JFrame("Excluir Aluno");
        deleteFrame.setSize(400, 200);
        deleteFrame.setLayout(new GridLayout(4, 2));

        JTextField keyField = new JTextField();
        JComboBox<String> deleteByField = new JComboBox<>(new String[]{"Nome", "Matrícula"});
        JButton deleteButton = new JButton("Excluir");
        JButton cancelButton = new JButton("Cancelar");

        deleteFrame.add(new JLabel("Informe o Nome ou Matrícula:"));
        deleteFrame.add(keyField);
        deleteFrame.add(new JLabel("Excluir por:"));
        deleteFrame.add(deleteByField);
        deleteFrame.add(deleteButton);
        deleteFrame.add(cancelButton);

        deleteButton.addActionListener(e -> {
            String key = keyField.getText();
            boolean byName = deleteByField.getSelectedItem().equals("Nome");

            if (key.isEmpty()) {
                JOptionPane.showMessageDialog(deleteFrame, "Por favor, informe o Nome ou Matrícula!");
                return;
            }

            treeByName.delete(key, byName);
            treeByMatricula.delete(key, byName);

            JOptionPane.showMessageDialog(deleteFrame, "Aluno excluído com sucesso!");
            listAlunos();
            deleteFrame.dispose();
        });

        cancelButton.addActionListener(e -> deleteFrame.dispose());

        deleteFrame.setVisible(true);
    }

    private void openSearchAlunoWindow() {
        JFrame searchFrame = new JFrame("Buscar/Listar Alunos");
        searchFrame.setSize(600, 400);
        searchFrame.setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(3, 2));

        JTextField searchField = new JTextField();
        JComboBox<String> searchByField = new JComboBox<>(new String[]{"Nome", "Matrícula"});
        JComboBox<String> orderField = new JComboBox<>(new String[]{
                "Nome Crescente", "Nome Decrescente", "Matrícula Crescente", "Matrícula Decrescente"
        });
        JButton searchButton = new JButton("Buscar");
        JButton listButton = new JButton("Listar");
        JButton closeButton = new JButton("Fechar");

        controlPanel.add(new JLabel("Buscar por Nome ou Matrícula:"));
        controlPanel.add(searchField);
        controlPanel.add(new JLabel("Tipo de Busca:"));
        controlPanel.add(searchByField);
        controlPanel.add(searchButton);
        controlPanel.add(listButton);

        DefaultTableModel searchTableModel = new DefaultTableModel(
                new Object[]{"Matrícula", "Nome", "Turno", "Período", "Ênfase", "Curso"}, 0);
        JTable searchTable = new JTable(searchTableModel);
        JScrollPane searchScrollPane = new JScrollPane(searchTable);

        searchFrame.add(controlPanel, BorderLayout.NORTH);
        searchFrame.add(searchScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout());
        bottomPanel.add(new JLabel("Ordenar por:"));
        bottomPanel.add(orderField);
        bottomPanel.add(closeButton);
        searchFrame.add(bottomPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> {
            String key = searchField.getText();
            boolean byName = searchByField.getSelectedItem().equals("Nome");

            if (key.isEmpty()) {
                JOptionPane.showMessageDialog(searchFrame, "Por favor, informe o Nome ou Matrícula para buscar!");
                return;
            }

            Aluno aluno = byName ? treeByName.search(key, true) : treeByMatricula.search(key, false);
            searchTableModel.setRowCount(0);

            if (aluno != null) {
                searchTableModel.addRow(new Object[]{
                        aluno.getMatricula(),
                        aluno.getNome(),
                        aluno.getTurno(),
                        aluno.getPeriodo(),
                        aluno.getEnfase(),
                        aluno.getCurso()
                });
            } else {
                JOptionPane.showMessageDialog(searchFrame, "Aluno não encontrado!");
            }
        });

        listButton.addActionListener(e -> {
            String orderOption = (String) orderField.getSelectedItem();
            List<Aluno> alunos = new ArrayList<>();
            treeByName.inOrderTraversal(alunos);

            switch (orderOption) {
                case "Nome Crescente":
                    alunos.sort(Comparator.comparing(Aluno::getNome));
                    break;
                case "Nome Decrescente":
                    alunos.sort(Comparator.comparing(Aluno::getNome).reversed());
                    break;
                case "Matrícula Crescente":
                    alunos.sort(Comparator.comparing(Aluno::getMatricula));
                    break;
                case "Matrícula Decrescente":
                    alunos.sort(Comparator.comparing(Aluno::getMatricula).reversed());
                    break;
            }

            searchTableModel.setRowCount(0);
            for (Aluno aluno : alunos) {
                searchTableModel.addRow(new Object[]{
                        aluno.getMatricula(),
                        aluno.getNome(),
                        aluno.getTurno(),
                        aluno.getPeriodo(),
                        aluno.getEnfase(),
                        aluno.getCurso()
                });
            }
        });

        closeButton.addActionListener(e -> searchFrame.dispose());

        searchFrame.setVisible(true);
    }


    private void listAlunos() {
        List<Aluno> alunos = new ArrayList<>();
        treeByName.inOrderTraversal(alunos);

        tableModel.setRowCount(0);
        for (Aluno aluno : alunos) {
            tableModel.addRow(new Object[]{
                    aluno.getMatricula(),
                    aluno.getNome(),
                    aluno.getTurno(),
                    aluno.getPeriodo(),
                    aluno.getEnfase(),
                    aluno.getCurso()
            });
        }
    }

    public static void main(String[] args) {
        new BinaryTreeApp();
    }
}