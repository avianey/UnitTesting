package fr.astek.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(
    name = "T_CLIENT",
    uniqueConstraints = {
        @UniqueConstraint(columnNames={"siret"})
    }
)
public class Client implements Serializable {
    
    private static final long serialVersionUID = 1L;

    public static final String ERROR_RAISON_SOCIALE = "client.raisonSociale.null";
    public static final String ERROR_SIRET_PATTERN  = "client.siret.pattern";
    public static final String ERROR_SIRET_NULL  = "client.siret.null";

    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    private long id;
    
    @Column(length = 255)
    @NotNull(message = ERROR_RAISON_SOCIALE)
    private String raisonSociale;
    
    @Pattern(regexp = "[0-9]{14}", message = ERROR_SIRET_PATTERN)
    @NotNull(message = ERROR_SIRET_NULL)
    @Column(length = 14)
    private String siret;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRaisonSociale() {
        return raisonSociale;
    }

    public void setRaisonSociale(String raisonSociale) {
        this.raisonSociale = raisonSociale;
    }

    public String getSiret() {
        return siret;
    }

    public void setSiret(String siret) {
        this.siret = siret;
    }
    
}
