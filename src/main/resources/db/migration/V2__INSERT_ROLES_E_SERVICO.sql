-- Roles
INSERT INTO acesso (nome, descricao) VALUES
('GESTOR', 'Acesso total ao sistema'),
('FUNCIONARIO', 'Acesso às consultas e clientes'),
('CLIENTE', 'Acesso ao agendamento e histórico');

-- Serviços (os mesmos que estão hardcoded no frontend)
INSERT INTO servico (nome, descricao, categoria, preco, img_url, duracao) VALUES
('Limpeza de Pele', 'Tratamento profundo para remoção de impurezas e revitalização celular.','Facial' ,280.00, '../assets/limpeza-de-pele.jpg', 60),
('Massagem Relaxante', 'Equilíbrio perfeito entre técnicas ancestrais e óleos essenciais orgânicos.','Corporal', 200.00, '../assets/massagem-relaxante.jpg',60),
('Peeling de Diamante', 'Esfoliação mecânica controlada que promove a renovação celular, ideal para atenuar manchas, linhas finas e uniformizar a textura da pele.', 'Facial', 250.00, '../assets/peeling.jpg', 60),
('Skinbooster', 'Tratamento de hidratação injetável que age nas camadas mais profundas da pele, restaurando a firmeza, a elasticidade e o viço.', 'Facial', 350.00, '../assets/skincare.jpg',60),
('Drenagem Linfática', 'Técnica de massagem suave e rítmica que otimiza o sistema linfático, perfeita para reduzir o inchaço e desintoxicar o organismo.', 'Corporal', 220.00, '../assets/drenagem.jpg',60),
('Depilação (Cera e Laser)', 'Protocolos de depilação personalizados com cera ou tecnologia a laser, focados no seu conforto e na durabilidade da pele lisa.', 'Laser', 80.00, '../assets/depilacao.jpg',60);