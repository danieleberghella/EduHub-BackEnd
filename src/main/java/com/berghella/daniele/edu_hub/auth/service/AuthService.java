package com.berghella.daniele.edu_hub.auth.service;


import com.berghella.daniele.edu_hub.auth.dao.AuthDAO;
import com.berghella.daniele.edu_hub.auth.model.Auth;

public class AuthService {
    private final AuthDAO authDAO = new AuthDAO();

    public void addAuth(Auth auth){
        authDAO.addAuth(auth);
    }

    public Auth getAuthByEmail(String email){
        return authDAO.getAuthByEmail(email);
    }
}
