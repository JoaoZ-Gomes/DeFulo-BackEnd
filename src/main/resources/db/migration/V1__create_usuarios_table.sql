CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    tipo_usuario VARCHAR(20) NOT NULL,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    cpf VARCHAR(14) UNIQUE,
    telefone VARCHAR(20),
    perfil VARCHAR(20) NOT NULL,
    talhao_id BIGINT,
    
    -- Campos específicos de Engenheiro
    especialidade VARCHAR(150),
    
    -- Campos específicos de RTV
    regiao VARCHAR(100),
    codigo_rtv VARCHAR(50),
    
    -- Campos específicos de Produtor
    propriedade VARCHAR(150),
    area_total DOUBLE PRECISION,
    
    -- Auditoria
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP
);

-- Índices para performance
CREATE INDEX idx_usuario_email ON usuarios(email);
CREATE INDEX idx_usuario_perfil ON usuarios(perfil);
CREATE INDEX idx_usuario_tipo ON usuarios(tipo_usuario);
