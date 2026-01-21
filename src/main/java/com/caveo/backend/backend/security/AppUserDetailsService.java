package com.caveo.backend.backend.security;

import com.caveo.backend.backend.dao.UtilisateurDao;
import com.caveo.backend.backend.model.Utilisateur;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    protected final UtilisateurDao utilisateurDao;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<Utilisateur> optionalUtilisateur = utilisateurDao.findByEmail(email);

        if(optionalUtilisateur.isEmpty()) {
            throw new UsernameNotFoundException(email);
        }

        return new AppUserDetails(optionalUtilisateur.get());
    }
}
