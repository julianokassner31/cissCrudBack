package br.com.jkassner.ciss_crud_back.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

import br.com.jkassner.ciss_crud_back.dto.EmployerDto;
import br.com.jkassner.ciss_crud_back.model.Employer;
import br.com.jkassner.ciss_crud_back.repository.EmployerRepository;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;

@RestController
@RequestMapping("/employers")
@RequestScope
public class EmployerController {

	@Autowired
	EmployerRepository employerRepository;
	
	@Autowired
	MapperFactory mapperFactory;
	
	@Autowired
	StringEncryptor stringEncryptor;
	
	Map<String, Object> res = new HashMap<>();
	
	@GetMapping
	public Map<String, Object> getEmployers(){
		return getEmployersResponse();
	}
	
	@GetMapping("employers-pageable")
	public Map<String, Object> getEmployersPageable(@RequestParam int page, @RequestParam int rows){
		return getEmployersResponse(page, rows);
	}
	
	@GetMapping("find")
	public List<EmployerDto> getEmployer(@RequestParam String query){
		return mapperFacadeEntityToDto()
				.mapAsList(employerRepository.find(query), EmployerDto.class);
	}
	
	@GetMapping("email-already-registered")
	public Boolean findByEmail(@RequestParam String query){
		Employer employer = employerRepository.findByEmail(query);
		return  employer != null ;
	}
	
	@GetMapping("{id}")
	public EmployerDto getEmployerById(@PathVariable("id") String id){
		Employer employer = employerRepository.findById(decriptId(id)).orElse(null);
		return mapperFacadeEntityToDto().map(employer, EmployerDto.class);
	}
	
	@PostMapping
	public Map<String, Object> create(@RequestBody @Valid EmployerDto employerDto) {
		Employer employer = mapperFacadeDtoToEntity().map(employerDto, Employer.class);
		employerRepository.save(employer);
		return getEmployersResponse();
	}
	
	@PutMapping
	public EmployerDto update(@RequestBody @Valid EmployerDto employerDto) {
		Employer employer = mapperFacadeDtoToEntity().map(employerDto, Employer.class);
		return mapperFacadeEntityToDto().map(employerRepository.save(employer), EmployerDto.class);
	}
	
	@DeleteMapping("{id}")
	public Map<String, Object> delete(@PathVariable String id) {
		employerRepository.deleteById(decriptId(id));
		return getEmployersResponse();
	}
	

	private MapperFacade mapperFacadeEntityToDto() {
		mapperFactory
		.classMap(Employer.class, EmployerDto.class)
			.customize(new CustomMapper<Employer, EmployerDto>(){
				 public void mapAtoB(Employer ent, EmployerDto dto, MappingContext context) {
					 dto.setId(stringEncryptor.encrypt(String.valueOf(ent.getId())));
				 }
				 
				 public void mapBtoA(EmployerDto dto, Employer ent, MappingContext context) {
					 if(dto.getId() != null) {
						 ent.setId(Long.parseLong(stringEncryptor.decrypt(dto.getId())));
					 }
				 }
			})
			.exclude("id")
			.byDefault()
			.register();
		return mapperFactory.getMapperFacade();
	}
	
	private MapperFacade mapperFacadeDtoToEntity() {
		mapperFactory
		.classMap(EmployerDto.class, Employer.class)
			.customize(new CustomMapper<EmployerDto, Employer>(){
				 public void mapAtoB(EmployerDto dto, Employer ent, MappingContext context) {
					 if(dto.getId() != null) {
						 ent.setId(Long.parseLong(stringEncryptor.decrypt(dto.getId())));
					 }
				 }
				 
				 public void mapBtoA(Employer ent, EmployerDto dto, MappingContext context) {
					 dto.setId(stringEncryptor.encrypt(String.valueOf(ent.getId())));
				 }
			})
			.exclude("id")
			.byDefault()
			.register();
		return mapperFactory.getMapperFacade();
	}
	
	private Long decriptId(String id) {
		return Long.parseLong(stringEncryptor.decrypt(id));
	}
	
	private Map<String, Object> getEmployersResponse(){
		return getEmployersResponse(0,10);
	}
	
	private Map<String, Object> getEmployersResponse(int page, int rows){
		Pageable sortedByName = PageRequest.of(page, rows, Sort.by("firstname"));
		List<EmployerDto> employers = mapperFacadeEntityToDto()
				.mapAsList(employerRepository.findAll(sortedByName), EmployerDto.class);
		long count = employerRepository.count();
		res.put("count", count);
		res.put("employers", employers);
		return res;
	}
}
