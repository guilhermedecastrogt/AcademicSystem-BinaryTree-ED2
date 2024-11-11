import java.util.List;

public class BinaryTree {
    private class Node {
        Aluno aluno;
        Node left, right;

        Node(Aluno aluno) {
            this.aluno = aluno;
        }
    }

    private Node root;

    public void insert(Aluno aluno, boolean byName) {
        root = insertRec(root, aluno, byName);
    }

    private Node insertRec(Node root, Aluno aluno, boolean byName) {
        if (root == null) {
            root = new Node(aluno);
            return root;
        }

        if (byName ? aluno.getNome().compareTo(root.aluno.getNome()) < 0 : aluno.getMatricula() < root.aluno.getMatricula()) {
            root.left = insertRec(root.left, aluno, byName);
        } else {
            root.right = insertRec(root.right, aluno, byName);
        }
        return root;
    }

    public void inOrderTraversal(List<Aluno> alunoList) {
        inOrderTraversalRec(root, alunoList);
    }

    private void inOrderTraversalRec(Node root, List<Aluno> alunoList) {
        if (root != null) {
            inOrderTraversalRec(root.left, alunoList);
            alunoList.add(root.aluno);  // Adiciona o aluno na lista em ordem
            inOrderTraversalRec(root.right, alunoList);
        }
    }

    public void clear() {
        root = null;
    }
}