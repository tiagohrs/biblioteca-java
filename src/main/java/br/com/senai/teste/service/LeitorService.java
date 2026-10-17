package br.com.senai.teste.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
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

    public Leitor salvarFoto(Integer id, MultipartFile arquivo) {

        Optional<Leitor> leitorEncontrado = leitorRepository.findById(id);

        if (leitorEncontrado.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Leitor não encontrado");
        }

        Leitor leitor = leitorEncontrado.get();

        String nomeArquivo = System.currentTimeMillis()
                + "_"
                + arquivo.getOriginalFilename();

        File pasta = new File("uploads/fotos-alunos");

        if (!pasta.exists()) {
            pasta.mkdirs();
        }

        File destino = new File(
                pasta,
                nomeArquivo).getAbsoluteFile();

        try {
            arquivo.transferTo(destino);
        } catch (IOException erro) {
            erro.printStackTrace();

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro ao salvar a foto do leitor");

        }

        leitor.setNomeArquivoFoto(nomeArquivo);

        return leitorRepository.save(leitor);

    }

}