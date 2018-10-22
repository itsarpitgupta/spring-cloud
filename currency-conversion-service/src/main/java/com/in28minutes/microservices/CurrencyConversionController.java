package com.in28minutes.microservices;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.in28minutes.microservices.bean.CurrencyConversionBean;


@RestController
public class CurrencyConversionController {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	CurrencyExchangeServiceProxy proxy;
	
	@GetMapping(value = "/currency-converter/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrency(@PathVariable String from, @PathVariable String to, @PathVariable BigDecimal quantity) {
		Map<String,String> uriVariable = new HashMap<String,String>();
		uriVariable.put("from", from);
		uriVariable.put("to", to);
		ResponseEntity<CurrencyConversionBean> responseEntity = new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyConversionBean.class,uriVariable);
		CurrencyConversionBean response = responseEntity.getBody();
		CurrencyConversionBean currencyConversionBean = new CurrencyConversionBean(100L,from,to,response.getConversionMultiple(),quantity,
																					quantity.multiply(response.getConversionMultiple()),response.getPort());	
		return currencyConversionBean;
	}
	
	@GetMapping(value = "/currency-converter-feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrencyFeign(@PathVariable String from, @PathVariable String to, @PathVariable BigDecimal quantity) {
		CurrencyConversionBean response = proxy.retriveExchangeValue(from, to);
		logger.info("{}",response);

		CurrencyConversionBean currencyConversionBean = new CurrencyConversionBean(100L,from,to,response.getConversionMultiple(),quantity,
																					quantity.multiply(response.getConversionMultiple()),response.getPort());
		return currencyConversionBean;
	}
	
}
