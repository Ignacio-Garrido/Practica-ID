package gei.id.tutelado.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.TableGenerator;

@TableGenerator(name="xeradorIdsEleccions", table="taboa_ids",
pkColumnName="nome_id", pkColumnValue="idEleccion",
valueColumnName="ultimo_valor_id",
initialValue=0, allocationSize=1)

@NamedQueries ({
	@NamedQuery (name="Eleccion.recuperaPorNome",
				 query="SELECT e FROM Eleccion e WHERE e.nome=:nome"),
	@NamedQuery (name="Eleccion.eleccionsUsuarioVotante",
	 			 query="SELECT e FROM Eleccion e INNER JOIN e.votantes u WHERE u.correo=:correo"),
	@NamedQuery (name="Eleccion.tienenMenosCandidatos",
	 			 query="SELECT e FROM Eleccion e JOIN e.candidatos GROUP BY e HAVING count(*) < (SELECT count(*) FROM Eleccion el JOIN el.candidatos WHERE el.nome=:nome) "),
	@NamedQuery (name="Eleccion.contarVotantes",
				 query="SELECT count(*) FROM Eleccion e JOIN e.votantes GROUP BY e.idEleccion")
})

@Entity
public class Eleccion implements Comparable<Eleccion> {
    @Id
    @GeneratedValue(generator="xeradorIdsEleccions")
    private Long idEleccion;

    @Column(unique = true, nullable = false)
    private String nome;

    @Column(unique=false, nullable = false)
    private String centro;

    @Column(unique=false, nullable = false)
    private LocalDate fecha;
    
    @Column(unique=false, nullable = false)
    private Grupo grupoVotantes;
    
    @Column(unique=false, nullable = false)
    private Grupo grupoCandidatos;
    
    @ManyToMany (fetch = FetchType.LAZY)
    @JoinTable (name="t_elec_vot",
    		joinColumns=@JoinColumn(name="idEleccion"),
    		inverseJoinColumns=@JoinColumn(name="idUsuario"))
    private Set<Usuario> votantes = new HashSet<Usuario>();

    @ManyToMany (fetch = FetchType.LAZY)
    @JoinTable (name="t_elec_cand",
    		joinColumns=@JoinColumn(name="idEleccion"),
    		inverseJoinColumns=@JoinColumn(name="idUsuario"))
    private Set<Usuario> candidatos = new HashSet<Usuario>();

	public Long getIdEleccion() {
		return idEleccion;
	}

	public String getNome() {
		return nome;
	}

	public String getCentro() {
		return centro;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public Grupo getGrupoVotantes() {
		return grupoVotantes;
	}

	public Grupo getGrupoCandidatos() {
		return grupoCandidatos;
	}

	public Set<Usuario> getVotantes() {
		return votantes;
	}

	public Set<Usuario> getCandidatos() {
		return candidatos;
	}

	public void setIdEleccion(Long idEleccion) {
		this.idEleccion = idEleccion;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setCentro(String centro) {
		this.centro = centro;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public void setGrupoVotantes(Grupo grupoVotantes) {
		this.grupoVotantes = grupoVotantes;
	}

	public void setGrupoCandidatos(Grupo grupoCandidatos) {
		this.grupoCandidatos = grupoCandidatos;
	}

	public void setVotantes(Set<Usuario> votantes) {
		this.votantes = votantes;
	}

	public void setCandidatos(Set<Usuario> candidatos) {
		this.candidatos = candidatos;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Eleccion other = (Eleccion) obj;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Eleccion [idEleccion=" + idEleccion + ", nombre=" + nome + ", centro=" + centro
				+ ", fecha=" + fecha + ", grupoVotantes=" + grupoVotantes + ", grupoCandidatos=" + grupoCandidatos
				+ ", votantes=" + votantes + ", candidatos=" + candidatos + "]";
	}
	
	@Override
	public int compareTo(Eleccion other) {
		return (this.fecha.isBefore(other.getFecha())? -1:1);
	}
    
}
