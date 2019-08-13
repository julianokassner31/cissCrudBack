package br.com.jkassner.ciss_crud_back;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import br.com.jkassner.ciss_crud_back.repository.EmployerRepository;

@SpringBootApplication
@ComponentScan(basePackages= {"br.com.jkassner.ciss_crud_back"})
public class CissCrudBackApplication {

	@Autowired
	EmployerRepository repository;
	
	public static void main(String[] args) {
		SpringApplication.run(CissCrudBackApplication.class, args);
	}

}
