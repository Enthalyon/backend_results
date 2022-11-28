package com.misiontic.g1.securityBackend.services;

import com.misiontic.g1.securityBackend.models.Permission;
import com.misiontic.g1.securityBackend.models.Rol;
import com.misiontic.g1.securityBackend.repositories.PermissionRepository;
import com.misiontic.g1.securityBackend.repositories.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class RolServices {
    @Autowired
    private RolRepository rolRepository;


    @Autowired
    private PermissionRepository permissionRepository;

    /**
     *
     * @return
     */
    public List<Rol> index(){
        return (List<Rol>) this.rolRepository.findAll();
    }

    /**
     *
     * @param id
     * @return
     */
    public Optional<Rol> show(int id){
        return this.rolRepository.findById(id);
    }

    public Rol create(Rol newRol){
        if(newRol.getIdRol() == null){
            if(newRol.getName() != null)
                return this.rolRepository.save(newRol);
            else{
                // TODO return 400 code BadRequest no name
                return newRol;
            }
        }
        else{
            //TODO validate IC in DB
            return newRol;
        }
    }

    public Rol update(int id, Rol updateRol){
        if(id > 0){
            Optional<Rol> tempRol = this.show(id);
            if(tempRol.isPresent()){
                if(updateRol.getName() != null)
                    tempRol.get().setName(updateRol.getName());
                if(updateRol.getDescription() != null)
                    tempRol.get().setDescription(updateRol.getDescription());
                return this.rolRepository.save(tempRol.get());
            }
            else{
                // TODO Error 404 NotFound
                return updateRol;
            }
        }
        else {
            // TODO badRequest id < 0
            return updateRol;
        }
    }

    public boolean delete (int id) {
        Boolean success = this.show(id).map(rol -> {
            this.rolRepository.delete(rol);
            return true;
        }).orElse(false);
        return success;
    }

    public ResponseEntity<Rol> updateAddPermission (int idRol, int idPermission) {
        Optional<Rol> rol = this.rolRepository.findById(idRol);
        if (rol.isPresent()) {
            Optional<Permission> permission = this.permissionRepository.findById(idPermission);
            if (permission.isPresent()) {
                Set<Permission> tempPermissions = rol.get().getPermissions();
                if (tempPermissions.contains(permission))
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Rol has the permission.");
                else {
                    tempPermissions.add(permission.get());
                    rol.get().setPermissions(tempPermissions);
                    return new ResponseEntity<>(this.rolRepository.save(rol.get()), HttpStatus.CREATED);
                }
            }
            else
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "The provided permission.id does not exists in database");
        }
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "The provided rol.id does not exists in database");
    }


    public ResponseEntity<Boolean> validateGrant (int idRol, Permission permission){
        boolean isGrant = false;
        System.out.println(permission.getId() + " - " + permission.getMethod() + " - " + permission.getUrl());
        Optional<Rol> rol = this.rolRepository.findById(idRol);
        if(rol.isPresent()){
            for (Permission rolPermission: rol.get().getPermissions()){

                System.out.println(rolPermission.getId() + " - " + rolPermission.getMethod() + " - " + rolPermission.getUrl());
                if(rolPermission.getUrl().equals(permission.getUrl()) &&
                   rolPermission.getMethod().equals(permission.getMethod())){
                    isGrant = true;
                    break;
                }
            }
            if(isGrant)
                return new ResponseEntity<>(true, HttpStatus.OK);
            else
                return new ResponseEntity<>(false, HttpStatus. UNAUTHORIZED);
        }
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "The provided rol.id does not exists in database");
    }
}
