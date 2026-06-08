-- Roles
INSERT INTO acesso (nome, descricao) VALUES
('GESTOR', 'Acesso total ao sistema'),
('FUNCIONARIO', 'Acesso às consultas e clientes'),
('CLIENTE', 'Acesso ao agendamento e histórico');

-- Serviços (os mesmos que estão hardcoded no frontend)
INSERT INTO servico (nome, descricao, preco, duracao) VALUES
('Limpeza de Pele', 'Tratamento profundo para remoção de impurezas e revitalização celular.', 280.00, 60),
('Massagem Relaxante', 'Equilíbrio perfeito entre técnicas ancestrais e óleos essenciais orgânicos.', 200.00, 60),
('Peeling de Diamante', 'Esfoliação mecânica controlada que promove a renovação celular, ideal para atenuar manchas, linhas finas e uniformizar a textura da pele.', 250.00, 60),
('SkinBooster', 'Tratamento de hidratação injetável que age nas camadas mais profundas da pele, restaurando a firmeza, a elasticidade e o viço.', 350.00, 60),
('Drenagem Linfática', 'Técnica de massagem suave e rítmica que otimiza o sistema linfático, perfeita para reduzir o inchaço e desintoxicar o organismo.', 220.00, 60),
('Depilação (Cera e Laser)', 'Protocolos de depilação personalizados com cera ou tecnologia a laser, focados no seu conforto e na durabilidade da pele lisa.', 80.00, 60);