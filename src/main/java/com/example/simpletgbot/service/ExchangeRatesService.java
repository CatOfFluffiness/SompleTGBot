package com.example.simpletgbot.service;

import com.example.simpletgbot.exception.ServiceException;

import java.util.List;

public interface ExchangeRatesService {

    String getUSDExchangeRate() throws ServiceException;

    String getEURExchangeRate() throws ServiceException;

    List<String> getAllExchangeRates() throws ServiceException;
}