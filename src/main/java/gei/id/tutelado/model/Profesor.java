package gei.id.tutelado.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("P")
public class Profesor extends Usuario {

	@Column(nullable = true, unique=false)
    private String despacho;
	
	public Profesor(){
		super();
	}

	public Profesor(String correo, String nome, String apelido1, String apelido2, String despachoP) {
		super(correo, nome, apelido1, apelido2);
		despacho = despachoP;
	}

	public String getDespacho() {
		return despacho;
	}

	public void setDespacho(String despacho) {
		this.despacho = despacho;
	}

	@Override
	public String toString() {
		return "Profesor [despacho=" + despacho + "]";
	}
	
}
