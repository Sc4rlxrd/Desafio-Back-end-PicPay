package com.scarlxrd.picpay.service;

import com.scarlxrd.picpay.controller.dto.CreateWalletDto;
import com.scarlxrd.picpay.entities.Wallet;
import com.scarlxrd.picpay.exception.WalletDataAlreadyExistsException;
import com.scarlxrd.picpay.repository.WalletRepository;
import org.springframework.stereotype.Service;

@Service
public class WalletService {
    public final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }
    public Wallet createWallet (CreateWalletDto dto){
        var walletDb = walletRepository.findByCpfCnpjOrEmail(dto.cpfCnpj(), dto.email());
        if (walletDb.isPresent()){
            throw new WalletDataAlreadyExistsException("CpfCnpj or Email already exists");
        }
        return walletRepository.save(dto.toWallet());
    }

}
