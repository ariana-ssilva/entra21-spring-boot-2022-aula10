package br.com.entra21.aula10.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.entra21.aula10.model.Exemplo;
import br.com.entra21.aula10.model.ItemNivel3;
import br.com.entra21.aula10.repository.IRepositoryExemplo;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/exemplo")
public class ExemploController {

	@Autowired
	private IRepositoryExemplo exemploRepository;

	@GetMapping()
	@ResponseStatus(HttpStatus.OK)
	public List<Exemplo> listar() {
		List<Exemplo> response = exemploRepository.findAll();
		response.forEach(exemplo -> {
			setMaturidadeNivel3(exemplo);
		});
		return response;
	}

	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public List<Exemplo> buscar (@PathVariable("id") int param){
		List<Exemplo> response = exemploRepository.findById(param).stream().toList();
		return response;
	}
	
	@GetMapping(value = "/nomeeidade/{nome}/{idade}")
	public List<Exemplo> getByNameAndAge(@PathVariable("nome") String nome, @PathVariable("idade") Integer idade){
		return exemploRepository.findByFirstNameAndAge(nome, idade);
	}
	
	@GetMapping(value = "/byAge/{age}")
	public List<Exemplo> getByAge (@PathVariable("age") int age){
		return exemploRepository.findByAge(age);
	}
	
	@GetMapping(value= "/idadeMenor/{age}")
	public List<Exemplo> getAgeLessOrEqual (@PathVariable("age")int age){
		return exemploRepository.findByAgeLessThanEqual(age);
	}
	
	@GetMapping(value="comecacom/{prefixo}/{age}")
	public List<Exemplo> getStartsWith(@PathVariable("prefixo")String sla, @PathVariable("age") Integer anos){
		return exemploRepository.findByLastNameStartingWithAndAgeLessThanEqual(sla, anos);
	}
	
	
	@PostMapping()
	@ResponseStatus(HttpStatus.CREATED)
	public @ResponseBody Exemplo adicionar (@RequestBody Exemplo novoExemplo) {
		return exemploRepository.save(novoExemplo);
	}
	
	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody Optional<Exemplo> atualizar(@PathVariable("id") int param,
			@RequestBody Exemplo exemploNovosDados){
		Exemplo atual = exemploRepository.findById(param).get();
		atual.setFirstName(exemploNovosDados.getFirstName());
		atual.setLastName(exemploNovosDados.getLastName());
		atual.setAge(exemploNovosDados.getAge());
		atual.setActive(exemploNovosDados.getActive());
		exemploRepository.save(atual);
		
		return exemploRepository.findById(param);
	}

	
	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody boolean deletar(@PathVariable("id") int id) {
		exemploRepository.deleteById(id);
		return !exemploRepository.existsById(id);
	}
	
	private void setMaturidadeNivel3(Exemplo exemplo) {
		final String PATH = "localhost:8080/exemplos";
		ArrayList<String> headers = new ArrayList<String>();
		headers.add("Accept : application/json");
		headers.add("Content-type : application/json");
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		try {
			Exemplo clone = mapper.readValue(mapper.writeValueAsString(exemplo), Exemplo.class);
			clone.setLinks(null);
			String nomeAtual = clone.getFirstName();
			clone.setFirstName("Nome diferente");
			String jsonUpdate = mapper.writeValueAsString(clone);
			clone.setFirstName(nomeAtual);
			clone.setId(null);
			String jsonCreate = mapper.writeValueAsString(clone);
			exemplo.setLinks(new ArrayList<>());
			exemplo.getLinks().add(new ItemNivel3("GET", PATH, null, null));
			exemplo.getLinks().add(new ItemNivel3("GET", PATH + "/" + exemplo.getId(), null, null));
			exemplo.getLinks().add(new ItemNivel3("POST", PATH, headers, jsonCreate));
			exemplo.getLinks().add(new ItemNivel3("PUT", PATH + "/" + exemplo.getId(), headers, jsonUpdate));
		} catch (JsonProcessingException e) {

			e.printStackTrace();

		}

	}
}
