CREATE TABLE acesso
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome      ENUM('GESTOR', 'FUNCIONARIO', 'CLIENTE') NOT NULL,
    descricao VARCHAR(45)
);


CREATE TABLE endereco
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    cep         VARCHAR(9),
    logradouro  VARCHAR(100),
    bairro      VARCHAR(45),
    cidade      VARCHAR(45),
    uf          VARCHAR(2),
    numero      VARCHAR(10),
    complemento VARCHAR(45)
);

CREATE TABLE empresa
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome_fantasia VARCHAR(45),
    cnpj          VARCHAR(18),
    email         VARCHAR(45),
    telefone      VARCHAR(15),
    endereco_id   BIGINT,
    FOREIGN KEY (endereco_id) REFERENCES endereco (id)
);


CREATE TABLE cliente
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome         VARCHAR(45),
    email        VARCHAR(45) UNIQUE,
    senha        VARCHAR(255),
    url_foto     VARCHAR(255),
    data_criacao DATETIME,
    data_nasc    DATE,
    telefone     VARCHAR(11) UNIQUE,
    endereco_id  BIGINT,
    acesso_id    BIGINT,
    token_agendamento VARCHAR(255),
    expiracao_token_agendamento DATETIME,
    FOREIGN KEY (endereco_id) REFERENCES endereco (id),
    FOREIGN KEY (acesso_id) REFERENCES acesso (id)
);

CREATE TABLE funcionario
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome         VARCHAR(45),
    email        VARCHAR(45) UNIQUE,
    senha        VARCHAR(255),
    url_foto     VARCHAR(255),
    data_criacao DATETIME,
    cpf          VARCHAR(11) UNIQUE,
    empresa_id   BIGINT,
    acesso_id    BIGINT,
    FOREIGN KEY (empresa_id) REFERENCES empresa (id),
    FOREIGN KEY (acesso_id) REFERENCES acesso (id)
);

CREATE TABLE avaliacao
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    nota        BIGINT,
    descricao VARCHAR(255),
    cliente_id BIGINT,
    FOREIGN KEY (cliente_id) REFERENCES cliente (id)
);

CREATE TABLE servico
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome         VARCHAR(45),
    descricao  VARCHAR(255),
    categoria VARCHAR(255),
    preco        DECIMAL(10, 2),
    img_url VARCHAR(255),
    duracao      BIGINT,
    avaliacao_id BIGINT,
    FOREIGN KEY (avaliacao_id) REFERENCES avaliacao (id)
);

CREATE TABLE produto
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome       VARCHAR(45),
    preco      DECIMAL(10, 2),
    quantidade BIGINT
);

CREATE TABLE consulta
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    data_hora_inicio DATETIME,
    data_hora_fim    DATETIME,
    tipo_pagamento   ENUM('DINHEIRO', 'DEBITO', 'PIX', 'CREDITO'),
    data_pagamento   DATE,
    local_consulta   ENUM('CLINICA', 'DOMICILIO'),
    cal_booking_uid VARCHAR(255),
    status_consulta VARCHAR(30),
    avaliacao_id BIGINT,
    fk_cliente       BIGINT,
    fk_funcionario   BIGINT,
    FOREIGN KEY (fk_cliente) REFERENCES cliente (id),
    FOREIGN KEY (fk_funcionario) REFERENCES funcionario (id),
    FOREIGN KEY (avaliacao_id) REFERENCES avaliacao (id)
);

CREATE TABLE consulta_servico
(
    consulta_id BIGINT NOT NULL,
    servico_id BIGINT NOT NULL,
    PRIMARY KEY (consulta_id, servico_id),
    FOREIGN KEY (consulta_id) REFERENCES consulta (id),
    FOREIGN KEY (servico_id) REFERENCES servico (id)
);

CREATE TABLE servico_produto
(
    servico_id BIGINT NOT NULL,
    produto_id BIGINT NOT NULL,
    PRIMARY KEY (servico_id, produto_id),
    FOREIGN KEY (servico_id) REFERENCES servico (id),
    FOREIGN KEY (produto_id) REFERENCES produto (id)
);

CREATE TABLE movimentacao
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    produto_id BIGINT NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    quantidade INT NOT NULL,
    data_hora DATETIME NOT NULL,
    observacao VARCHAR(255),
    FOREIGN KEY (produto_id) REFERENCES produto (id)
);
