package com.scarlxrd.picpay.service;

import com.scarlxrd.picpay.controller.dto.TransferDto;
import com.scarlxrd.picpay.entities.Transfer;
import com.scarlxrd.picpay.entities.Wallet;
import com.scarlxrd.picpay.exception.InsufficientBalanceException;
import com.scarlxrd.picpay.exception.TransferNotAllowedForWalletTypeException;
import com.scarlxrd.picpay.exception.TransferNotAuthorizedException;
import com.scarlxrd.picpay.exception.WalletNotFoundException;
import com.scarlxrd.picpay.repository.TransferRepository;
import com.scarlxrd.picpay.repository.WalletRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
public class TransferService {

    private final TransferRepository transferRepository;
    private final AuthorizationService authorizationService;
    private final NotificationService notificationService;
    private final WalletRepository walletRepository;


    public TransferService(TransferRepository transferRepository, AuthorizationService authorizationService, NotificationService notificationService, WalletRepository walletRepository) {
        this.transferRepository = transferRepository;
        this.authorizationService = authorizationService;
        this.notificationService = notificationService;
        this.walletRepository = walletRepository;
    }

    @Transactional
    public Transfer transfer(TransferDto transferDto) {

        var sender = walletRepository.findById(transferDto.payer())
                .orElseThrow(() -> new WalletNotFoundException(transferDto.payer()));

        var receiver = walletRepository.findById(transferDto.payee())
                .orElseThrow(() -> new WalletNotFoundException(transferDto.payee()));

        validateTransfer(transferDto, sender);

        sender.debit(transferDto.value());
        receiver.credit(transferDto.value());

        var transfer = new Transfer(sender, receiver, transferDto.value());

        walletRepository.save(sender);
        walletRepository.save(receiver);
        var transferResult = transferRepository.save(transfer);

        CompletableFuture.runAsync(() -> notificationService.setNotification(transferResult));

        return transferResult;

    }

    private void validateTransfer(TransferDto transferDto, Wallet sender) {

        if (!sender.isTransferAllowedForWalletType()) {
            throw new TransferNotAllowedForWalletTypeException();
        }

        if (!sender.isBalancerEqualOrGreatherThan(transferDto.value())) {
            throw new InsufficientBalanceException();
        }

        if (!authorizationService.isAuthorized(transferDto)) {
            throw new TransferNotAuthorizedException();
        }

    }
}
