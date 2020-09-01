package com.wstcon.gov.bd.esellers.interfaces;

import com.wstcon.gov.bd.esellers.userAuth.userAuthModels.Token;

public interface AuthCompleteListener {
    void onAuthComplete(Token token);
}
