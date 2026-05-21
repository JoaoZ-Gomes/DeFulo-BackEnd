package com.defulo.api.infrastructure.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.defulo.api.features.usuario.model.Perfil;
import com.defulo.api.features.usuario.model.Usuario;
import com.defulo.api.features.usuario.repository.UsuarioRepository;
import com.defulo.api.features.fazenda.model.Fazenda;
import com.defulo.api.features.fazenda.repository.FazendaRepository;
import com.defulo.api.features.produtor.model.Produtor;
import com.defulo.api.features.talhao.model.Talhao;
import com.defulo.api.features.talhao.repository.TalhaoRepository;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(
            UsuarioRepository repository, 
            FazendaRepository fazendaRepository,
            TalhaoRepository talhaoRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // Criar Admin
            if (repository.findByEmail("admin@defulo.com").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setNome("Administrador DeFulo");
                admin.setEmail("admin@defulo.com");
                admin.setSenha(passwordEncoder.encode("admin123"));
                admin.setPerfil(Perfil.ADM);
                repository.save(admin);
                System.out.println("Usuário ADM criado: admin@defulo.com / admin123");
            }

            // Criar Produtor e Fazenda de teste
            if (repository.findByEmail("produtor@defulo.com").isEmpty()) {
                Produtor produtor = new Produtor();
                produtor.setNome("Carlos Silva");
                produtor.setEmail("produtor@defulo.com");
                produtor.setSenha(passwordEncoder.encode("demo"));
                produtor.setPerfil(Perfil.PRODUTOR);
                produtor.setPropriedade("Fazenda Boa Vista");
                produtor.setAreaTotal(150.0);
                produtor = repository.save(produtor);

                Fazenda fazenda = new Fazenda();
                fazenda.setNome("Fazenda Boa Vista");
                fazenda.setAreaTotal(150.0);
                fazenda.setCultura("Soja");
                fazenda.setProdutor(produtor);
                fazenda = fazendaRepository.save(fazenda);

                Talhao talhao = new Talhao();
                talhao.setNumero("01");
                talhao.setArea(50.0);
                talhao.setCultura("Soja");
                talhao.setDataPlantio(LocalDate.now().minusMonths(2));
                talhao.setLimiteCriticoUmidade(40.0);
                talhao.setFazenda(fazenda);
                talhaoRepository.save(talhao);

                System.out.println("Dados de teste criados: produtor@defulo.com / demo");
            }
        };
    }
}
