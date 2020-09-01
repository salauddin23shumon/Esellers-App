package com.wstcon.gov.bd.esellers.interfaces;

import com.wstcon.gov.bd.esellers.cart.cartModel.Cart;



public interface AddorRemoveCallbacks {

    void onAddProduct(Cart cart);

    void onRemoveProduct(Cart cart);


}
