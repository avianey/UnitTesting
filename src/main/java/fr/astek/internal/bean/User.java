/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.astek.internal.bean;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author dlebert
 */
@Entity
@Table(
    uniqueConstraints = {
        @UniqueConstraint(columnNames={"login"})
    }
)
public class User implements Serializable {
    
    public enum Role {
        ADMIN, CONSULTANT;
    }
    
    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    private long id;
    
    @Column(length = 32)
    private String login;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
    
}
