package com.misiontic.g1.securityBackend.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="rol")
public class Rol implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRol;
    private String name;
    private String description;

@OneToMany(cascade = {CascadeType.PERSIST}, mappedBy = "rol")
@JsonIgnoreProperties("rol")
private List<User> users;

@ManyToMany
@JoinTable(
        name= "permission_rol",
        joinColumns = @JoinColumn(name = "idRol"),
        inverseJoinColumns = @JoinColumn(name = "idPermission")
)
private Set<Permission> permissions;

    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer id) {
        this.idRol = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
