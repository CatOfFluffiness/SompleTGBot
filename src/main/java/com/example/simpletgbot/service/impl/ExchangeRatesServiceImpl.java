package com.example.simpletgbot.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import com.example.simpletgbot.client.CbrClient;
import com.example.simpletgbot.exception.ServiceException;
import com.example.simpletgbot.service.ExchangeRatesService;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExchangeRatesServiceImpl implements ExchangeRatesService {

    private static final String USD_XPATH = "/ValCurs//Valute[@ID='R01235']/Value";
    private static final String EUR_XPATH = "/ValCurs//Valute[@ID='R01239']/Value";

    @Autowired
    private CbrClient client;

    @Override
    public String getUSDExchangeRate() throws ServiceException {
        var xmlOptional = client.getCurrencyRatesXML();
        String xml = xmlOptional.orElseThrow(
                () -> new ServiceException("Не удалось получить XML")
        );
        return extractCurrencyValueFromXML(xml, USD_XPATH);
    }

    @Override
    public String getEURExchangeRate() throws ServiceException {
        var xmlOptional = client.getCurrencyRatesXML();
        String xml = xmlOptional.orElseThrow(
                () -> new ServiceException("Не удалось получить XML")
        );
        return extractCurrencyValueFromXML(xml, EUR_XPATH);
    }

    public List<String> getAllExchangeRates() throws ServiceException {
        var xmlOptional = client.getCurrencyRatesXML();
        String xml = xmlOptional.orElseThrow(
                () -> new ServiceException("Не удалось получить XML")
        );
        List<String> exchangeRates = new ArrayList<>();
        exchangeRates.add("Курсы валют на " + LocalDate.now() + ":");

        exchangeRates.addAll(extractAllCurrencyValuesFromXML(xml));
        return exchangeRates;
    }

    private List<String> extractAllCurrencyValuesFromXML(String xml) throws ServiceException {
        var source = new InputSource(new StringReader(xml));
        try {
            var xpath = XPathFactory.newInstance().newXPath();
            var document = (Document) xpath.evaluate("/", source, XPathConstants.NODE);

            var nodeList = (NodeList) xpath.evaluate("/ValCurs/Valute", document, XPathConstants.NODESET);
            var exchangeRates = new ArrayList<String>();

            for (int i = 0; i < nodeList.getLength(); i++) {
                var valuteNode = nodeList.item(i);

                var currencyCode = xpath.evaluate("CharCode", valuteNode);
                var currencyName = xpath.evaluate("Name", valuteNode);
                var currencyValue = xpath.evaluate("Value", valuteNode);

                var exchangeRate = currencyCode + " (" + currencyName + "): " + currencyValue;
                exchangeRates.add(exchangeRate);
            }

            return exchangeRates;
        } catch (XPathExpressionException e) {
            throw new ServiceException("Не удалось распарсить XML", e);
        }
    }

    private static String extractCurrencyValueFromXML(String xml, String xpathExpression)
            throws ServiceException {
        var source = new InputSource(new StringReader(xml));
        try {
            var xpath = XPathFactory.newInstance().newXPath();
            var document = (Document) xpath.evaluate("/", source, XPathConstants.NODE);

            return xpath.evaluate(xpathExpression, document);
        } catch (XPathExpressionException e) {
            throw new ServiceException("Не удалось распарсить XML", e);
        }
    }
}
