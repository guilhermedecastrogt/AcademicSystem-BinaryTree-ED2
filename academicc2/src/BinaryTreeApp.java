import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BinaryTreeApp {
    private TreeSet<Aluno> treeByName;
    private TreeMap<String, Aluno> treeByMatricula;
    private JFrame frame;
    private DefaultTableModel tableModel;

    public BinaryTreeApp() {
        treeByName = new TreeSet<>(Comparator.comparing(Aluno::getNome));
        treeByMatricula = new TreeMap<>();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Gerenciamento de Alunos");
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        frame.add(mainPanel);

        tableModel = new DefaultTableModel(new Object[]{"Matrícula", "Nome", "Turno", "Período", "Ênfase", "Curso"}, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 1, 10, 10));
        buttonPanel.setBackground(Color.LIGHT_GRAY);
        mainPanel.add(buttonPanel, BorderLayout.WEST);

        JButton addButton = createStyledButton("Adicionar Aluno");
        JButton deleteButton = createStyledButton("Excluir Aluno");
        JButton searchButton = createStyledButton("Buscar Aluno");
        JButton listButton = createStyledButton("Listar Alunos");
        JButton loadFileButton = createStyledButton("Carregar Arquivo");

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(listButton);
        buttonPanel.add(loadFileButton);

        addButton.addActionListener(e -> openAddAlunoWindow());
        deleteButton.addActionListener(e -> openDeleteAlunoWindow());
        searchButton.addActionListener(e -> openSearchAlunoWindow());
        listButton.addActionListener(e -> listAlunos());
        loadFileButton.addActionListener(e -> loadFileIntoTree());

        frame.setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(0, 123, 255));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        return button;
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
                        treeByName.add(aluno);
                        treeByMatricula.put(matricula, aluno);
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

        JButton saveButton = createStyledButton("Salvar");
        JButton cancelButton = createStyledButton("Cancelar");

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
                treeByName.add(aluno);
                treeByMatricula.put(matricula, aluno);

                JOptionPane.showMessageDialog(addFrame, "Aluno adicionado com sucesso!");
                addFrame.dispose();
                listAlunos();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addFrame, "Período deve ser um número inteiro.");
            }
        });

        cancelButton.addActionListener(e -> addFrame.dispose());

        addFrame.setVisible(true);
    }

    private void openSearchAlunoWindow() {
        JFrame searchFrame = new JFrame("Buscar/Listar Alunos");
        searchFrame.setSize(600, 400);
        searchFrame.setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(2, 2));

        JTextField searchField = new JTextField();
        JComboBox<String> searchByField = new JComboBox<>(new String[]{"Nome", "Matrícula"});
        JButton searchButton = createStyledButton("Buscar");
        JButton closeButton = createStyledButton("Fechar");

        controlPanel.add(new JLabel("Buscar por:"));
        controlPanel.add(searchField);
        controlPanel.add(new JLabel("Tipo de Busca:"));
        controlPanel.add(searchByField);

        DefaultTableModel searchTableModel = new DefaultTableModel(
                new Object[]{"Matrícula", "Nome", "Turno", "Período", "Ênfase", "Curso"}, 0);
        JTable searchTable = new JTable(searchTableModel);
        JScrollPane scrollPane = new JScrollPane(searchTable);

        searchFrame.add(controlPanel, BorderLayout.NORTH);
        searchFrame.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(searchButton);
        buttonPanel.add(closeButton);
        searchFrame.add(buttonPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> {
            String key = searchField.getText();
            boolean byName = searchByField.getSelectedItem().equals("Nome");

            searchTableModel.setRowCount(0);

            if (byName) {
                for (Aluno aluno : treeByName) {
                    if (aluno.getNome().equalsIgnoreCase(key)) {
                        searchTableModel.addRow(new Object[]{
                                aluno.getMatricula(),
                                aluno.getNome(),
                                aluno.getTurno(),
                                aluno.getPeriodo(),
                                aluno.getEnfase(),
                                aluno.getCurso()
                        });
                    }
                }
            } else {
                Aluno aluno = treeByMatricula.get(key);
                if (aluno != null) {
                    searchTableModel.addRow(new Object[]{
                            aluno.getMatricula(),
                            aluno.getNome(),
                            aluno.getTurno(),
                            aluno.getPeriodo(),
                            aluno.getEnfase(),
                            aluno.getCurso()
                    });
                }
            }

            if (searchTableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(searchFrame, "Nenhum aluno encontrado.");
            }
        });

        closeButton.addActionListener(e -> searchFrame.dispose());

        searchFrame.setVisible(true);
    }

    private void openDeleteAlunoWindow() {
        JFrame deleteFrame = new JFrame("Excluir Aluno");
        deleteFrame.setSize(400, 200);
        deleteFrame.setLayout(new GridLayout(4, 2));

        JTextField keyField = new JTextField();
        JComboBox<String> deleteByField = new JComboBox<>(new String[]{"Nome", "Matrícula"});
        JButton deleteButton = createStyledButton("Excluir");
        JButton cancelButton = createStyledButton("Cancelar");

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

            if (byName) {
                treeByName.removeIf(aluno -> aluno.getNome().equals(key));
            } else {
                Aluno aluno = treeByMatricula.remove(key);
                if (aluno != null) {
                    treeByName.remove(aluno);
                }
            }

            JOptionPane.showMessageDialog(deleteFrame, "Aluno excluído com sucesso!");
            listAlunos();
            deleteFrame.dispose();
        });

        cancelButton.addActionListener(e -> deleteFrame.dispose());

        deleteFrame.setVisible(true);
    }

    private void listAlunos() {
        tableModel.setRowCount(0);

        for (Aluno aluno : treeByName) {
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