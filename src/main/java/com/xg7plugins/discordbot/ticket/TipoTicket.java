package com.xg7plugins.discordbot.ticket;

import lombok.Getter;

@Getter
public enum TipoTicket {
    BUGS("Relatar um bug"),
    DUVIDA("Tirar uma dúvida sobre algum plugin"),
    DENUNCIA("Denunciar um usuário"),
    OUTRO("Outro motivo");

    private String descricao;
    private TipoTicket(String descricao) {
        this.descricao = descricao;
    }
}
