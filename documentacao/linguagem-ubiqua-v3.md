# 📘 Dicionário de Linguagem Ubíqua — Sistema de Oficina Mecânica

## 🎯 Objetivo
Padronizar a comunicação entre desenvolvedores, especialistas do domínio e stakeholders, garantindo consistência nos termos utilizados no sistema.

---

## 🧩 Termos do Domínio

### 👤 Cliente
Pessoa física ou jurídica que solicita serviços para um veículo.

- Identificação: CPF ou CNPJ
- Pode possuir múltiplos veículos
- Pode aprovar ou recusar orçamentos

---

### 🔧 Mecânico
Profissional responsável por realizar diagnóstico e execução dos serviços.

- Pode:
  - realizar diagnóstico
  - executar serviços
  - registrar tempo de execução

---

### 🧑‍💼 Atendente
Responsável pelo atendimento inicial ao cliente.

- Pode:
  - cadastrar cliente
  - registrar veículo
  - abrir ordem de serviço
  
---

### 🚗 Veículo
Automóvel pertencente a um cliente e atendido pela oficina.

- Identificação: placa (única)
- Atributos:
  - marca
  - modelo
  - ano
- Possui histórico de serviços

---

### 🧾 Ordem de Serviço (OS)
Registro central que representa o atendimento completo de um veículo.

Contém:
- cliente
- veículo
- serviços
- peças
- orçamento
- status
- histórico

> É o Aggregate Root principal do sistema.
> Todas as alterações relacionadas a serviços, peças e status devem ocorrer através dela.

---

### 🔍 Diagnóstico
Processo de análise do veículo para identificar problemas.

- Realizado por: Mecânico
- Resultado:
  - serviços necessários
  - peças necessários

---

### 🛠️ Serviço
Atividade de manutenção ou reparo realizada no veículo.

Exemplos:
- troca de óleo
- alinhamento
- troca de pastilhas
- entre outros

Atributos:
- nome
- descrição
- preço
- tempo médio

---

### 🔩 Peça
Item físico utilizado na execução de um serviço.

Atributos:
- nome
- quantidade em estoque
- preço

---

### 📦 Estoque
Controle da quantidade de peças disponíveis.

- Reduzido quando uma peça é utilizada
- Utilizado para reposição e planejamento

---

### 💰 Orçamento
Estimativa de custo e tempo para execução dos serviços.

Composto por:
- serviços
- peças

Fórmula:
Total = soma(serviços) + soma(peças)

- É obrigatório para execução de serviços
- Pode ser atualizado caso novos problemas sejam identificados
- Requer aprovação do cliente para continuidade

---

### ✅ Aprovação do Orçamento
Decisão do cliente sobre o orçamento.

Pode ser:
- aprovado
- recusado

---

### 🚫 Rejeição de Orçamento
Estado intermediário entre AGUARDANDO_APROVACAO e RECEBIDA.

- Cliente recusa o orçamento
- OS retorna para estado de espera
- Pode ser reaberta com novo orçamento

---

### ⚙️ Execução
Fase onde os serviços são realizados no veículo pelo mecânico.

Inclui:
- uso de peças
- registro de tempo
- atualização de status

---

### ⏱️ Tempo de Execução
Tempo gasto para realizar um serviço.

Usado para:
- métricas
- melhoria de performance

---

### 📢 Notificação (SISTEMA EXTERNO)
Comunicação enviada ao cliente por E-mail e Whatsapp.

Exemplos:
- orçamento disponível
- serviço finalizado

---

### 🚘 Entrega
Momento em que o veículo é devolvido ao cliente.

- Marca o encerramento da OS

---

## 🔄 Status da Ordem de Serviço

Representa o estado do ciclo de vida da OS.

Valores possíveis:
- RECEBIDA
- EM_DIAGNOSTICO
- AGUARDANDO_APROVACAO
- EM_EXECUCAO
- FINALIZADA
- ENTREGUE

Regras:
- O status não pode retroceder
- A transição ocorre automaticamente conforme ações do sistema

---

## 🧠 Regras de Negócio

- Cliente deve existir antes da OS  
- Veículo pertence a um cliente  
- Placa do veículo é única  
- Não é permitido executar serviços sem aprovação  
- Estoque deve ser atualizado ao utilizar peças  
- Orçamento = serviços + peças  
- OS só pode ser finalizada após todos os serviços concluídos  

---

## 📡 Eventos de Domínio

#### OrdemServicoCriada
Disparado quando: Uma nova OS é criada
Contém: cliente_id, veiculo_id, data_criacao

#### OrcamentoAprovado
Disparado quando: Cliente aprova o orçamento
Contém: ordem_id, valor_total, data_aprovacao

#### ServicoFinalizado
Disparado quando: Um serviço é concluído
Contém: ordem_id, servico_id, data_conclusao

#### EstoqueAtualizado
Disparado quando: Peça é utilizada
Contém: peca_id, quantidade_anterior, quantidade_nova

---

## ⚠️ Exceções do Domínio

#### ClienteNãoEncontrado
- Quando: Tentar criar OS sem cliente válido

#### VeículoNãoPertenceAoCliente
- Quando: Tentar associar veículo errado à OS

#### EstoqueInsuficiente
- Quando: Tentar usar peça sem quantidade suficiente

#### StatusInválido
- Quando: Tentar transição de status não permitida

#### OrçamentoPendente
- Quando: Tentar executar serviços sem aprovação

---

## 🧱 Subdomínios

### 🎯 Core Domain
- Gestão de Ordens de Serviço

### 🧩 Supporting Domains
- Gestão de Clientes  
- Gestão de Veículos  
- Gestão de Serviços  
- Gestão de Estoque  

### 🔐 Generic Domain
- Autenticação e Autorização (JWT) 

---

## 📌 Diretrizes de Uso

- Utilizar os termos exatamente como definidos neste documento
- Evitar sinônimos no código e na documentação
  - ❌ "Chamado", "Ticket"
  - ✅ "Ordem de Serviço"
- Aplicar a linguagem ubíqua em:
  - código
  - APIs
  - documentação
  - diagramas