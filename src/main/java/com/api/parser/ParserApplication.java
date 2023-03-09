package com.api.parser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ParserApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParserApplication.class, args);

		//Test
		Parser JSONparser = new JSONParser();
		Parser XMLparser = new XMLParser();
		String json = JSONparser.parseMT940();
		String xml = XMLparser.parseMT940();
		System.out.println(json);
		System.out.println(xml);
	}

}
