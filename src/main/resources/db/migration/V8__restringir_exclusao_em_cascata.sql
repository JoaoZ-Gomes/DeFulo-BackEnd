-- V8: Impede que excluir um Produtor, Fazenda, Talhão ou Engenheiro apague
-- silenciosamente registros dependentes em cascata (fazendas, talhões, eventos
-- de manejo e laudos de inspeção fitossanitária).
--
-- Antes: fk_fazenda_produtor, fk_talhao_fazenda e as FKs de inspecoes_campo eram
-- ON DELETE CASCADE, e fk_evento_talhao era ON DELETE SET NULL — ou seja, excluir
-- um produtor apagava (ou desvinculava) todo o histórico agronômico da fazenda sem
-- aviso. Agora o banco recusa a exclusão (RESTRICT) enquanto houver dependentes;
-- a camada de serviço (ProdutorService, FazendaService, TalhaoService,
-- EngenheiroService) valida isso antes e devolve uma mensagem de negócio clara —
-- esta constraint é a rede de segurança para qualquer caminho que não passe por lá
-- (ex: SyncService).
--
-- fk_evento_rtv permanece ON DELETE SET NULL: o evento em si continua válido mesmo
-- que o RTV que o registrou seja removido do sistema.

ALTER TABLE fazendas DROP CONSTRAINT fk_fazenda_produtor;
ALTER TABLE fazendas ADD CONSTRAINT fk_fazenda_produtor FOREIGN KEY (produtor_id)
    REFERENCES usuarios(id) ON DELETE RESTRICT;

ALTER TABLE talhoes DROP CONSTRAINT fk_talhao_fazenda;
ALTER TABLE talhoes ADD CONSTRAINT fk_talhao_fazenda FOREIGN KEY (fazenda_id)
    REFERENCES fazendas(id) ON DELETE RESTRICT;

ALTER TABLE eventos_manejo DROP CONSTRAINT fk_evento_talhao;
ALTER TABLE eventos_manejo ADD CONSTRAINT fk_evento_talhao FOREIGN KEY (talhao_id)
    REFERENCES talhoes(id) ON DELETE RESTRICT;

ALTER TABLE inspecoes_campo DROP CONSTRAINT fk_inspecao_talhao;
ALTER TABLE inspecoes_campo ADD CONSTRAINT fk_inspecao_talhao FOREIGN KEY (talhao_id)
    REFERENCES talhoes(id) ON DELETE RESTRICT;

ALTER TABLE inspecoes_campo DROP CONSTRAINT fk_inspecao_fazenda;
ALTER TABLE inspecoes_campo ADD CONSTRAINT fk_inspecao_fazenda FOREIGN KEY (fazenda_id)
    REFERENCES fazendas(id) ON DELETE RESTRICT;

ALTER TABLE inspecoes_campo DROP CONSTRAINT fk_inspecao_engenheiro;
ALTER TABLE inspecoes_campo ADD CONSTRAINT fk_inspecao_engenheiro FOREIGN KEY (engenheiro_id)
    REFERENCES usuarios(id) ON DELETE RESTRICT;
