package br.com.jkassner.ciss_crud_back.configuration;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.jkassner.ciss_crud_back.model.Employer;
import br.com.jkassner.ciss_crud_back.repository.EmployerRepository;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

@Configuration
public class BeansFactory {

	@Autowired
	EmployerRepository employerRepository;
	
	@Bean
	public void createEmployers() {
		
		for (int i = 0; i < 10; i++) {
			buildTable(i);
		}
	}
	
	private void buildTable(int i){
		String nameIncrement = String.valueOf(i);
		if("0".equals(nameIncrement)) {
			nameIncrement = "";
		}
		Employer juliano = new Employer("Juliano"+nameIncrement, "Kassner"+nameIncrement, "julianokassner"+nameIncrement+"@hotmail.com", 12345678910l + i);
		employerRepository.save(juliano);
		Employer pedro = new Employer("Pedro Vitor"+nameIncrement, "Kassner"+nameIncrement, "pedrovk"+nameIncrement+"@hotmail.com", 54321098765l + i);
		employerRepository.save(pedro);
		Employer debora = new Employer("Debora"+nameIncrement, "Boava"+nameIncrement, "dbb"+nameIncrement+"@hotmail.com", 98765432110l + i);
		employerRepository.save(debora);
	}
	
	@Bean
	public MapperFactory mapperFactory() {
		return  new DefaultMapperFactory.Builder()
				.mapNulls(false)
				.build();
	}
	
	@Bean(name = "encryptorBean")
	public StringEncryptor stringEncryptor() {
	    PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
	    SimpleStringPBEConfig config = new SimpleStringPBEConfig();
	    config.setPassword("password");
	    config.setAlgorithm("PBEWithMD5AndDES");
	    config.setKeyObtentionIterations("1000");
	    config.setPoolSize("1");
	    config.setProviderName("SunJCE");
	    config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
	    config.setStringOutputType("hexadecimal");
	    encryptor.setConfig(config);
	    return encryptor;
	}
}
