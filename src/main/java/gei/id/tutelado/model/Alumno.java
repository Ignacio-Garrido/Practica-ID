package gei.id.tutelado.model;

import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;

@Entity
@DiscriminatorValue("A")
public class Alumno extends Usuario {
	
	@ElementCollection (fetch = FetchType.LAZY)
    @CollectionTable (name="alumno_titulacions", joinColumns=@JoinColumn(name="idUsuario"))
    @Column(nullable = true, unique=false)
    private Set<String> titulacions;
	
	public Alumno(){
		super();
	}
	
	public Alumno(String correo, String nome, String apelido1, String apelido2, Set<String> titulacions) {
		super(correo, nome, apelido1, apelido2);
		this.titulacions = titulacions;
	}

	public Set<String> getTitulacions() {
		return titulacions;
	}

	public void setTitulacions(Set<String> titulacions) {
		this.titulacions = titulacions;
	}

	@Override
	public String toString() {
		return "Alumno [titulacions=" + titulacions + "]";
	}
	
}
