CREATE TABLE clientes (
  id BIGSERIAL PRIMARY KEY,
  nome VARCHAR(255) NOT NULL,
  cpf VARCHAR(11) NOT NULL UNIQUE,
  data_nascimento DATE NOT NULL
);

CREATE TABLE contas (
  id BIGSERIAL PRIMARY KEY,
  numero_conta VARCHAR(20) NOT NULL UNIQUE,
  agencia VARCHAR(20) NOT NULL,
  saldo NUMERIC(19,2) NOT NULL DEFAULT 0,
  bloqueada BOOLEAN NOT NULL DEFAULT FALSE,
  cliente_id BIGINT NOT NULL REFERENCES clientes(id)
);

CREATE TABLE transacoes (
  id BIGSERIAL PRIMARY KEY,
  conta_id BIGINT NOT NULL REFERENCES contas(id),
  tipo VARCHAR(20) NOT NULL,      -- DEPOSITO | SAQUE
  valor NUMERIC(19,2) NOT NULL,
  data_hora TIMESTAMP NOT NULL
);

CREATE INDEX idx_tx_conta_data ON transacoes (conta_id, data_hora);
