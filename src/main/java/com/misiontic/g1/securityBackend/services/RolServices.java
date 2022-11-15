package com.misiontic.g1.securityBackend.services;

import com.misiontic.g1.securityBackend.models.Rol;
import com.misiontic.g1.securityBackend.repositories.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolServices {
    @Autowired
    private RolRepository rolRepository;

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
}
