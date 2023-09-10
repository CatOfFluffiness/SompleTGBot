package com.example.simpletgbot.service;

import com.example.simpletgbot.exception.ServiceException;

public interface ExchangeRatesService {

    String getUSDExchangeRate() throws ServiceException;

    String getEURExchangeRate() throws ServiceException;
}