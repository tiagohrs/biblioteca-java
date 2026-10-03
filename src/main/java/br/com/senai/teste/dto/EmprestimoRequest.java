package br.com.senai.teste.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class EmprestimoRequest {

    @NotNull(message = "O ID do leitor é obrigatório")
    private Integer leitorId;

    @NotNull (message = "O ID do livro é obrigatório")
    private Integer livroId;

    @NotNull(message = "A data prevista de devolução é obrigatória")
    @Future(message = "A data prevista de devolução deve ser uma data futura")
    private LocalDate dataPrevistaDevolucao;

    public EmprestimoRequest() {
    }

    public Integer getLeitorId() {
        return leitorId;
    }

    public void setLeitorId(Integer leitorId) {
        this.leitorId = leitorId;
    }

    public Integer getLivroId() {
        return livroId;
    }

    public void setLivroId(Integer livroId) {
        this.livroId = livroId;
    }

    public LocalDate getDataPrevistaDevolucao() {
        return dataPrevistaDevolucao;
    }

    public void setDataPrevistaDevolucao(LocalDate dataPrevistaDevolucao) {
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
    }

}
