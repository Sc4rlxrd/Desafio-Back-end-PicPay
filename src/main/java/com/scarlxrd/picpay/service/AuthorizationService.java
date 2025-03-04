package com.scarlxrd.picpay.service;

import com.scarlxrd.picpay.client.AuthorizationClient;
import com.scarlxrd.picpay.controller.dto.TransferDto;
import com.scarlxrd.picpay.exception.PicPayException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {
    private final AuthorizationClient authorizationClient;

    public AuthorizationService(AuthorizationClient authorizationClient) {
        this.authorizationClient = authorizationClient;
    }

    public boolean isAuthorized(TransferDto transfer) {

        var resp = authorizationClient.isAuthorized();

        if (resp.getStatusCode().isError()) {
            throw new PicPayException();
        }

        return resp.getBody().authorized();
    }
}
