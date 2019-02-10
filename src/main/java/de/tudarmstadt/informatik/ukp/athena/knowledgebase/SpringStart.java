package de.tudarmstadt.informatik.ukp.athena.knowledgebase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// hassle free way of starting Spring without inserting any data. Useful to fool around with the api
@SpringBootApplication
public class SpringStart {
	public static void main(String[]args) {
		SpringApplication.run(SpringStart.class, args);
	}
}
