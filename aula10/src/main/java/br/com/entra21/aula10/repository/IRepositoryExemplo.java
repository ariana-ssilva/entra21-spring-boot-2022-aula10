package br.com.entra21.aula10.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.entra21.aula10.model.Exemplo;

public interface IRepositoryExemplo extends JpaRepository<Exemplo, Integer> {

	public List<Exemplo> findByAge(int idade);

	public List<Exemplo> findByFirstNameAndAge(String firstName, Integer age);

	public List<Exemplo> findByAgeLessThanEqual(Integer age);

	public List<Exemplo> findByLastNameStartingWithAndAgeLessThanEqual(String prefixo, Integer age);

}
