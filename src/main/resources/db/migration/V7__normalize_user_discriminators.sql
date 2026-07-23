-- V7: Normalizar os valores do discriminador tipo_usuario para caixa alta.
-- Garante compatibilidade com os mapeamentos JPA do backend (ex: USUARIO, PRODUTOR, RTV, ENGENHEIRO).
-- Evita o erro 'Unknown discriminator value' ao carregar dados antigos inseridos
-- com camelcase (ex: Usuario, Produtor).

UPDATE usuarios 
SET tipo_usuario = UPPER(tipo_usuario)
WHERE tipo_usuario IS NOT NULL;
