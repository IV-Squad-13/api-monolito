ALTER TABLE tb_empreendimento
    ADD tp_init VARCHAR(20);

-- Preencher valores NULL existentes com um valor padrão
UPDATE tb_empreendimento
SET tp_init = 'PADRAO'
WHERE tp_init IS NULL;

ALTER TABLE tb_empreendimento
    ALTER COLUMN tp_init SET NOT NULL;

ALTER TABLE tb_empreendimento
    ADD id_reference_doc BYTEA;