package com.api.parser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ParserApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParserApplication.class, args);

		//Test
		Parser parser = new Parser();
		String test = parser.parseMT940("xml");
		System.out.println(test);
	}

}
