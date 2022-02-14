package gei.id.tutelado.model;

import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OrderBy;
import javax.persistence.TableGenerator;

@NamedQueries ({
	@NamedQuery (name="Usuario.recuperaPorCorreo",
				 query="SELECT u FROM Usuario u where u.correo=:correo"),
	@NamedQuery (name="Usuario.recuperaEleccionsVotante",
	 			 query="SELECT e FROM Eleccion e LEFT OUTER JOIN FETCH e.votantes v WHERE v.correo=:correo"),
	@NamedQuery (name="Usuario.nonCandidatos",
	 			 query="SELECT p FROM Usuario p LEFT JOIN p.candidaturas c WHERE c.idEleccion IS NULL GROUP BY p.idUsuario")
})

@TableGenerator(name="xeradorIdsUsuarios", table="taboa_ids",
pkColumnName="nome_id", pkColumnValue="idUsuario",
valueColumnName="ultimo_valor_id",
initialValue=0, allocationSize=1)

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="tipo", discriminatorType=DiscriminatorType.STRING)
public class Usuario {
    @Id
    @GeneratedValue (generator="xeradorIdsUsuarios")
    private Long idUsuario;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false, unique=false)
    private String nome;
    
    @Column(nullable = false, unique=false)
    private String apelido1;
    
    @Column(nullable = false, unique=false)
    private String apelido2;
    
    @ManyToMany (mappedBy="candidatos", fetch=FetchType.LAZY)
    @OrderBy("fecha DESC")
    private SortedSet<Eleccion> candidaturas = new TreeSet<Eleccion>();
    // NOTA: necesitamos @OrderBy, ainda que a colección estea definida como LAZY, por se nalgun momento accedemos á propiedade DENTRO de sesión.
    // Garantimos así que cando Hibernate cargue a colección, o faga na orde axeitada na consulta que lanza contra a BD

    public Usuario() {	
    }
    
	public Usuario(String correo, String nome, String apelido1, String apelido2) {
		this.correo = correo;
		this.nome = nome;
		this.apelido1 = apelido1;
		this.apelido2 = apelido2;
	}
	
	public Long getIdUsuario() {
		return idUsuario;
	}

	public String getCorreo() {
		return correo;
	}

	public String getNome() {
		return nome;
	}

	public String getApelido1() {
		return apelido1;
	}

	public String getApelido2() {
		return apelido2;
	}

	public SortedSet<Eleccion> getCandidaturas() {
		return candidaturas;
	}

	public void setIdUsuario(Long idUsuario) {
		this.idUsuario = idUsuario;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setApelido1(String apelido1) {
		this.apelido1 = apelido1;
	}

	public void setApelido2(String apelido2) {
		this.apelido2 = apelido2;
	}

	public void setCandidaturas(SortedSet<Eleccion> candidaturas) {
		this.candidaturas = candidaturas;
	}
	
	// Metodo de conveniencia para asegurarnos de que actualizamos os dous extremos da asociación ao mesmo tempo
	public void engadirCandidatura(Eleccion eleccion) {
		HashSet<Usuario> update = (HashSet<Usuario>) eleccion.getCandidatos();
		update.add(this);
		eleccion.setCandidatos(update);
		// É un sorted set, engadimos sempre por orde de data (descendente)
		this.candidaturas.add(eleccion);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((correo == null) ? 0 : correo.hashCode());
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
		Usuario other = (Usuario) obj;
		if (correo == null) {
			if (other.correo != null)
				return false;
		} else if (!correo.equals(other.correo))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Usuario [idUsuario=" + idUsuario + ", correo=" + correo + ", nome=" + nome + ", apelido1=" + apelido1
				+ ", apelido2=" + apelido2 + ", candidaturas=" + candidaturas + "]";
	}
}
