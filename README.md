🚗 Sistema de Gestão de Oficina Mecânica (MVP)

API back-end desenvolvida em Java + Spring Boot para gestão de ordens de serviço, clientes, veículos e estoque, com foco em boas práticas de arquitetura, qualidade e segurança.

Projeto desenvolvido como parte do Tech Challenge da pós-graduação em Arquitetura de Software.

🎯 Objetivo

Fornecer uma solução para digitalizar e organizar o fluxo operacional de uma oficina mecânica, eliminando controles manuais e proporcionando:

Rastreabilidade completa dos serviços
Gestão eficiente de clientes e veículos
Controle de peças e insumos
Acompanhamento em tempo real das ordens de serviço
🧩 Funcionalidades
🔧 Ordem de Serviço (OS)
Criação de OS com cliente (CPF/CNPJ)
Cadastro de veículo (placa, marca, modelo, ano)
Inclusão de serviços e peças
Geração automática de orçamento
Fluxo de aprovação
📊 Acompanhamento
Status da OS:
Recebida
Em diagnóstico
Aguardando aprovação
Em execução
Finalizada
Entregue
Atualização automática de status
Consulta via API
🏢 Gestão Administrativa
CRUD de:
Clientes
Veículos
Serviços
Peças/insumos
Controle de estoque
Listagem e detalhamento de OS
Monitoramento de tempo médio
🔐 Segurança
Autenticação via JWT
Proteção de endpoints administrativos
Validação de dados sensíveis (CPF/CNPJ, placa)
🧪 Qualidade
Testes unitários e de integração
Cobertura mínima de 80% nos domínios críticos
Boas práticas de Clean Code
🏗️ Arquitetura
Arquitetura em camadas
Princípios de Domain-Driven Design (DDD)
Separação de responsabilidades (Controller, Service, Domain, Repository)
Linguagem ubíqua aplicada ao domínio
🛠️ Tecnologias
Java 17+
Spring Boot
Spring Data JPA
Spring Security
JWT
Swagger/OpenAPI
Docker / Docker Compose
Banco de dados relacional (ex: PostgreSQL)
📦 Como executar o projeto
🔹 Pré-requisitos
Docker
Docker Compose
🔹 Subindo a aplicação
docker-compose up --build

A aplicação estará disponível em:

http://localhost:8080
📄 Documentação da API

Disponível via Swagger:

http://localhost:8080/swagger-ui.html
🧱 Estrutura do Projeto
src/main/java
 ├── domain         # Entidades e regras de negócio
 ├── application    # Casos de uso / serviços
 ├── infrastructure # Repositórios, config, segurança
 ├── interfaces     # Controllers (REST)
🧠 Práticas Aplicadas
Domain-Driven Design (DDD)
SOLID
Clean Architecture (adaptada ao contexto do MVP)
Separação de responsabilidades
Testabilidade
📊 Diferenciais
Modelagem orientada ao domínio
Fluxo completo de OS com estados bem definidos
Preparado para evolução para microsserviços
Estrutura pensada para escalabilidade
🎓 Contexto Acadêmico

Projeto desenvolvido como parte do Tech Challenge, com foco na aplicação prática de:

Arquitetura de Software
Modelagem de domínio
Qualidade e segurança
Boas práticas de desenvolvimento
📌 Próximos Passos
Implementação de mensageria (ex: Kafka)
Observabilidade (logs, métricas e tracing)
Deploy em cloud (OCI/AWS)
Evolução para arquitetura distribuída
