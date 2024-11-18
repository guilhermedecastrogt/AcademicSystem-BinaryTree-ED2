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

        if (byName) {
            if (aluno.getNome().compareTo(root.aluno.getNome()) < 0) {
                root.left = insertRec(root.left, aluno, byName);
            } else {
                root.right = insertRec(root.right, aluno, byName);
            }
        } else {
            if (aluno.getMatricula().compareTo(root.aluno.getMatricula()) < 0) {
                root.left = insertRec(root.left, aluno, byName);
            } else {
                root.right = insertRec(root.right, aluno, byName);
            }
        }

        return root;
    }

    public Aluno search(String key, boolean byName) {
        return searchRec(root, key, byName);
    }

    private Aluno searchRec(Node root, String key, boolean byName) {
        if (root == null) {
            System.out.println("Busca falhou. Chave não encontrada: " + key);
            return null;
        }

        if (byName ? root.aluno.getNome().equals(key) : root.aluno.getMatricula().equals(key)) {
            System.out.println("Busca bem-sucedida: " + root.aluno);
            return root.aluno;
        }

        if (byName ? key.compareTo(root.aluno.getNome()) < 0 : key.compareTo(root.aluno.getMatricula()) < 0) {
            return searchRec(root.left, key, byName);
        } else {
            return searchRec(root.right, key, byName);
        }
    }

    public void delete(String key, boolean byName) {
        root = deleteRec(root, key, byName);
    }

    private Node deleteRec(Node root, String key, boolean byName) {
        if (root == null) {
            System.out.println("Chave não encontrada para exclusão: " + key);
            return null;
        }

        if (byName ? key.compareTo(root.aluno.getNome()) < 0 : key.compareTo(root.aluno.getMatricula()) < 0) {
            root.left = deleteRec(root.left, key, byName);
        } else if (byName ? key.compareTo(root.aluno.getNome()) > 0 : key.compareTo(root.aluno.getMatricula()) > 0) {
            root.right = deleteRec(root.right, key, byName);
        } else {
            System.out.println("Nó encontrado para exclusão: " + root.aluno);

            if (root.left == null && root.right == null) {
                return null;
            }
            if (root.left == null) {
                return root.right;
            } else if (root.right == null) {
                return root.left;
            }

            Node successor = findMin(root.right);
            System.out.println("Substituindo com sucessor: " + successor.aluno);
            root.aluno = successor.aluno;
            root.right = deleteRec(root.right, byName ? successor.aluno.getNome() : successor.aluno.getMatricula(), byName);
        }
        return root;
    }

    private Node findMin(Node root) {
        while (root.left != null) {
            root = root.left;
        }
        return root;
    }

    public void inOrderTraversal(List<Aluno> alunoList) {
        inOrderTraversalRec(root, alunoList);
    }

    private void inOrderTraversalRec(Node root, List<Aluno> alunoList) {
        if (root != null) {
            inOrderTraversalRec(root.left, alunoList);
            alunoList.add(root.aluno);
            inOrderTraversalRec(root.right, alunoList);
        }
    }

    public void clear() {
        root = null;
    }
}