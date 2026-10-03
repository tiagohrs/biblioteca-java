package br.com.senai.teste.service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.senai.teste.model.Leitor;
import br.com.senai.teste.model.Emprestimo;
import br.com.senai.teste.model.Livro;
import br.com.senai.teste.repository.LeitorRepository;
import br.com.senai.teste.repository.EmprestimoRepository;
import br.com.senai.teste.repository.LivroRepository;

@Service
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final LeitorRepository leitorRepository;
    private final LivroRepository livroRepository;

    public EmprestimoService(EmprestimoRepository emprestimoRepository, LeitorRepository leitorRepository,
            LivroRepository livroRepository) {
        this.emprestimoRepository = emprestimoRepository;
        this.leitorRepository = leitorRepository;
        this.livroRepository = livroRepository;
    }

    public Optional<Emprestimo> cadastrar(Integer leitorId, Integer livroId, LocalDate dataPrevistaDevolucao) {
        Optional<Leitor> leitor = leitorRepository.findById(leitorId);
        Optional<Livro> livro = livroRepository.findById(livroId);

        if (leitor.isEmpty() || livro.isEmpty()) {
            return Optional.empty();
        }

        boolean livroEmprestado = emprestimoRepository.existsByLivroIdAndDataDevolucaoIsNull(livroId);

        if (livroEmprestado) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O livro já está emprestado");
        }

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setLeitor(leitor.get());
        emprestimo.setLivro(livro.get());
        emprestimo.setDataEmprestimo(LocalDate.now());
        emprestimo.setDataPrevistaDevolucao(dataPrevistaDevolucao);

        return Optional.of(emprestimoRepository.save(emprestimo));

    }

    public List<Emprestimo> listar() {
        return emprestimoRepository.findAll();
    }

    public Optional<Emprestimo> buscarPorId(Integer id) {
        return emprestimoRepository.findById(id);
    }

    public Optional<Emprestimo> devolver(Integer id) {
        Optional<Emprestimo> encontrado = emprestimoRepository.findById(id);

        if (encontrado.isEmpty()) {
            return Optional.empty();
        }

        Emprestimo emprestimo = encontrado.get();

        if (emprestimo.getDataDevolucao() == null) {
            emprestimo.setDataDevolucao(LocalDate.now());
            emprestimoRepository.save(emprestimo);
        }

        return Optional.of(emprestimo);
    }

    public List<Emprestimo> listarAtivos() {
        return emprestimoRepository
                .findByDataDevolucaoIsNull();
    }

    public List<Emprestimo> listarPorLeitor(Integer leitorId) {
        return emprestimoRepository.findByLeitorId(leitorId);
    }

    public List<Emprestimo> listarPorLivro(Integer livroId) {
        return emprestimoRepository.findByLivroId(livroId);
    }

    public List<Emprestimo> listarAtrasados() {

        return emprestimoRepository.findByDataPrevistaDevolucaoBeforeAndDataDevolucaoIsNull(LocalDate.now());
    }

    public List<Emprestimo> listarAtrasadosPorLeitor(Integer leitorId) {

        return emprestimoRepository.findByLeitorIdDataPrevistaDevolucaoBeforeAndDataDevolucaoIsNull(leitorId,
                LocalDate.now());
    }

    public Optional<Emprestimo> renovar(Integer id, LocalDate novaDataPrevista) {
        Optional<Emprestimo> emprestimoOptional = emprestimoRepository.findById(id);
        if (emprestimoOptional.isEmpty()) {
            return Optional.empty();
        }

        Emprestimo emprestimo = emprestimoOptional.get();

        if (emprestimo.getDataDevolucao() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Um emprestimo devolvido nao pode ser renovado");
        }

        if (!novaDataPrevista.isAfter(
            emprestimo.getDataPrevistaDevolucao())) {

        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "A nova data deve ser posterior ao prazo atual");
    }

        emprestimo.setDataPrevistaDevolucao(novaDataPrevista);
        return Optional.of(emprestimoRepository.save(emprestimo));

    }



}
