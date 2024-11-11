public class Aluno {
    private int matricula;
    private String nome;
    private Turno turno;
    private int periodo;
    private String enfase;
    private String curso;

    public Aluno(int matricula, String nome, Turno turno, int periodo, String enfase, String curso) {
        this.matricula = matricula;
        this.nome = nome;
        this.turno = turno;
        this.periodo = periodo;
        this.enfase = enfase;
        this.curso = curso;
    }

    public int getMatricula() { return matricula; }
    public String getNome() { return nome; }
    public Turno getTurno() { return turno; }
    public int getPeriodo() { return periodo; }
    public String getEnfase() { return enfase; }
    public String getCurso() { return curso; }

    @Override
    public String toString() {
        return matricula + " - " + nome + " - " + turno + " - " + periodo + " - " + enfase + " - " + curso;
    }
}