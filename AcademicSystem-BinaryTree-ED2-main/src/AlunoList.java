import java.util.LinkedList;

public class AlunoList {
    private LinkedList<Aluno> alunos;

    public AlunoList() {
        alunos = new LinkedList<>();
    }

    public void addAluno(Aluno aluno) {
        alunos.add(aluno);
    }

    public LinkedList<Aluno> getAlunos() {
        return alunos;
    }

    public void clear() {
        alunos.clear();
    }
}