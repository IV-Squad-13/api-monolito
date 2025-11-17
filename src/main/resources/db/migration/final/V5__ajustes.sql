ALTER TABLE tb_empreendimento
    ADD tp_init VARCHAR(20);

ALTER TABLE tb_empreendimento
    ALTER COLUMN tp_init SET NOT NULL;

ALTER TABLE tb_empreendimento
    ADD id_reference_doc BYTEA;