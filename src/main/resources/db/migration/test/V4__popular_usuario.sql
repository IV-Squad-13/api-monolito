-- 3. POPULANDO PAPÉIS INICIAIS
-- Estes papéis devem corresponder ao seu enum Role (ADMIN, REVISOR, RELATOR)
INSERT INTO tb_papel (nm_papel)
VALUES ('ADMIN');
INSERT INTO tb_papel (nm_papel)
VALUES ('REVISOR');
INSERT INTO tb_papel (nm_papel)
VALUES ('RELATOR');

-- 4. POPULANDO UM USUÁRIO ADMINISTRADOR DE TESTE
-- SENHA: 123456 (BCRYPT ENCODE)
-- Hash gerado com BCrypt com cost=10.
-- Usuario: admin
-- Senha: 123456
INSERT INTO tb_usuario (nm_usuario, ds_email, ds_senha, fk_papel)
VALUES ('admin',
        'admin@empresa.com',
        '$2a$10$tdMmaawF8ysX6noQLlnO.OBabkT3VgofjRE.oWnU3VnVcTnu4zVzm', -- Hash para '123456'
        (SELECT id_papel FROM tb_papel WHERE nm_papel = 'ADMIN'));