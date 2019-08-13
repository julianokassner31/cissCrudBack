package br.com.jkassner.ciss_crud_back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.jkassner.ciss_crud_back.model.Employer;

@Repository
public interface EmployerRepository extends JpaRepository<Employer, Long>{
	@Query("select emp from Employer emp where "
			+ "lower(emp.firstname) like lower(concat('%', :query, '%')) "
			+ "or lower(emp.lastname) like lower(concat('%', :query, '%')) "
			+ "or lower(emp.email) like lower(concat('%', :query, '%')) "
			+ "or lower(emp.pis) like lower(concat('%', :query, '%'))"
			+ "or lower(concat(emp.firstname,' ', emp.lastname)) like lower(concat('%', :query, '%'))"
		)
	public List<Employer> find(@Param("query") String query);
	
	public Employer findByEmail(@Param("query") String query);
}
