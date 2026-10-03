package br.com.senai.teste.service;

import java.util.List;

import org.springframework.stereotype.Service;
import java.util.Optional;
import br.com.senai.teste.model.Leitor;
import br.com.senai.teste.repository.LeitorRepository;

@Service
public class LeitorService {

    private final LeitorRepository leitorRepository;

    public LeitorService(LeitorRepository leitorRepository) {
        this.leitorRepository = leitorRepository;
    }

    public Leitor cadastrar(Leitor leitor) {
        return leitorRepository.save(leitor);
    }

    public List<Leitor> listar() {
        return leitorRepository.findAll();
    }

    public Optional<Leitor> buscarPorId(Integer id) {
        return leitorRepository.findById(id);
    }

    public Optional<Leitor> atualizar(
            Integer id, Leitor novosDados) {
        Optional<Leitor> leitorEncontrado = leitorRepository.findById(id);

        if (leitorEncontrado.isEmpty()) {
            return Optional.empty();
        }

        Leitor leitor = leitorEncontrado.get();

        leitor.setNome(novosDados.getNome());
        leitor.setEmail(novosDados.getEmail());

        return Optional.of(leitorRepository.save(leitor));
    }

    public boolean excluir(Integer id) {

        if (!leitorRepository.existsById(id)) {
            return false;
        }

        leitorRepository.deleteById(id);
        return true;
    }

}