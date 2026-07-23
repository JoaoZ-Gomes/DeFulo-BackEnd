package com.defulo.api.infrastructure.security;

import com.defulo.api.features.evento.model.EventoManejo;
import com.defulo.api.features.fazenda.model.Fazenda;
import com.defulo.api.features.talhao.model.Talhao;
import com.defulo.api.features.usuario.model.Perfil;
import com.defulo.api.features.usuario.model.Usuario;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    public Usuario getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Usuario usuario)) {
            throw new AccessDeniedException("Usuário autenticado não encontrado.");
        }
        return usuario;
    }

    public boolean isAcessoAmplo(Usuario usuario) {
        return switch (usuario.getPerfil()) {
            case ADM, GESTOR, PREFEITURA, ENGENHEIRO -> true;
            case RTV, PRODUTOR -> false;
        };
    }

    public void exigirAcessoAoProdutor(Long produtorId) {
        Usuario usuario = getUsuarioAutenticado();
        if (isAcessoAmplo(usuario)) {
            return;
        }
        if (usuario.getPerfil() == Perfil.PRODUTOR && usuario.getId().equals(produtorId)) {
            return;
        }
        throw new AccessDeniedException("Você não tem permissão para acessar recursos deste produtor.");
    }

    public void exigirAcessoAFazenda(Fazenda fazenda) {
        exigirAcessoAoProdutor(fazenda.getProdutor().getId());
    }

    public void exigirAcessoATalhao(Talhao talhao) {
        Usuario usuario = getUsuarioAutenticado();
        if (isAcessoAmplo(usuario)) {
            return;
        }
        if (usuario.getPerfil() == Perfil.PRODUTOR
                && usuario.getId().equals(talhao.getFazenda().getProdutor().getId())) {
            return;
        }
        if (usuario.getPerfil() == Perfil.RTV
                && usuario.getTalhaoId() != null
                && usuario.getTalhaoId().equals(talhao.getId())) {
            return;
        }
        throw new AccessDeniedException("Você não tem permissão para acessar este talhão.");
    }

    public void exigirAcessoAEvento(EventoManejo evento) {
        exigirAcessoATalhao(evento.getTalhao());
    }

    public void exigirPodeGerenciarFazenda() {
        Usuario usuario = getUsuarioAutenticado();
        if (isAcessoAmplo(usuario) || usuario.getPerfil() == Perfil.PRODUTOR) {
            return;
        }
        throw new AccessDeniedException("Você não tem permissão para gerenciar fazendas.");
    }

    public void exigirPodeGerenciarTalhao() {
        Usuario usuario = getUsuarioAutenticado();
        if (isAcessoAmplo(usuario) || usuario.getPerfil() == Perfil.PRODUTOR) {
            return;
        }
        throw new AccessDeniedException("Você não tem permissão para gerenciar talhões.");
    }

    public void exigirPerfil(Perfil perfil) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario.getPerfil() != perfil) {
            throw new org.springframework.security.access.AccessDeniedException("Você não possui o perfil de acesso necessário: " + perfil.getDescricao());
        }
    }
}
