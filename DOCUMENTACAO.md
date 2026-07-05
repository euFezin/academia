# Documentação Completa - Sistema de Academia

## 📋 Índice

1. [Visão Geral](#visão-geral)
2. [Arquitetura do Projeto](#arquitetura-do-projeto)
3. [Camadas da Aplicação](#camadas-da-aplicação)
   - [Model (Entidades)](#model-entidades)
   - [Repository (Repositórios)](#repository-repositórios)
   - [Service (Serviços)](#service-serviços)
   - [Controller (Controladores)](#controller-controladores)
   - [Security (Segurança)](#security-segurança)
   - [Config (Configurações)](#config-configurações)
4. [Fluxo de Dados](#fluxo-de-dados)
5. [Autenticação e Autorização JWT](#autenticação-e-autorização-jwt)
6. [Relacionamentos entre Entidades](#relacionamentos-entre-entidades)

---

## 🎯 Visão Geral

Este é um sistema de gerenciamento de academia desenvolvido com **Spring Boot 3.5.7** e **Java 17**. A aplicação utiliza uma arquitetura em camadas (Layered Architecture) seguindo o padrão MVC (Model-View-Controller) adaptado para APIs REST.

### Tecnologias Principais:
- **Spring Boot** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **Spring Security** - Segurança e autenticação
- **JWT (JSON Web Token)** - Autenticação stateless
- **H2 Database** - Banco de dados em memória (desenvolvimento)
- **PostgreSQL** - Banco de dados de produção
- **Lombok** - Redução de boilerplate
- **Swagger/OpenAPI** - Documentação da API

---

## 🏗️ Arquitetura do Projeto

A aplicação segue o padrão de **Arquitetura em Camadas**:

```
┌─────────────────────────────────────┐
│      Controller (REST API)          │  ← Camada de Apresentação
├─────────────────────────────────────┤
│         Service (Lógica)            │  ← Camada de Negócio
├─────────────────────────────────────┤
│      Repository (Acesso Dados)       │  ← Camada de Persistência
├─────────────────────────────────────┤
│         Model (Entidades)           │  ← Camada de Domínio
└─────────────────────────────────────┘
```

### Estrutura de Diretórios:

```
src/main/java/br/senac/academia/
├── AcademiaApplication.java      # Classe principal
├── config/                      # Configurações
│   ├── CorsConfig.java
│   ├── OpenApiConfig.java
│   └── SecurityConfig.java
├── controller/                  # Controladores REST
│   ├── AlunoController.java
│   ├── AuthController.java
│   └── ...
├── model/                       # Entidades JPA
│   ├── Aluno.java
│   ├── Instrutor.java
│   ├── enums/                  # Enumeradores
│   └── ...
├── repository/                  # Interfaces de Repositório
│   ├── AlunoRepository.java
│   └── ...
├── security/                    # Componentes de Segurança
│   ├── JwtFilter.java
│   ├── JwtUtil.java
│   └── JwtAuthenticationEntryPoint.java
└── service/                     # Serviços de Negócio
    ├── AlunoService.java
    └── ...
```

---

## 📦 Camadas da Aplicação

### 1. Model (Entidades)

**Localização:** `br.senac.academia.model`

As classes Model representam as **entidades do domínio** e são mapeadas para tabelas no banco de dados usando **JPA (Java Persistence API)**.

#### Características:

1. **Anotações JPA:**
   - `@Entity` - Marca a classe como entidade JPA
   - `@Table(name = "nome_tabela")` - Define o nome da tabela
   - `@Id` - Marca o campo como chave primária
   - `@GeneratedValue` - Define estratégia de geração de ID
   - `@Column` - Configura colunas do banco
   - `@ManyToOne`, `@OneToMany`, `@ManyToMany` - Relacionamentos

2. **Validações (Bean Validation):**
   - `@NotBlank` - Campo não pode ser nulo ou vazio
   - `@NotNull` - Campo não pode ser nulo
   - `@Email` - Valida formato de email
   - `@Past` - Data deve ser no passado

3. **Lombok:**
   - `@Data` - Gera getters, setters, toString, equals, hashCode
   - `@NoArgsConstructor` - Construtor sem argumentos
   - `@AllArgsConstructor` - Construtor com todos os argumentos

#### Exemplo - Classe Aluno:

```java
@Entity
@Table(name = "alunos")
public class Aluno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false, length = 100)
    private String nome;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plano_id", nullable = false)
    private Plano plano;
    
    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL)
    private List<AvaliacaoFisica> avaliacoesFisicas;
}
```

#### Tipos de Relacionamentos:

- **@ManyToOne**: Muitos alunos para um plano
- **@OneToMany**: Um aluno tem muitas avaliações
- **@ManyToMany**: Muitos alunos em muitas turmas
- **@OneToOne**: Relacionamento um-para-um

#### Enums:

Localizados em `model/enums/`, representam valores fixos:
- `StatusAluno`: ATIVO, INATIVO, SUSPENSO
- `StatusInstrutor`: ATIVO, INATIVO, FERIAS, AFASTADO
- `TipoPlano`: BASICO, INTERMEDIARIO, AVANCADO, PREMIUM
- `MetodoPagamento`: CARTAO_CREDITO, PIX, BOLETO, DINHEIRO

---

### 2. Repository (Repositórios)

**Localização:** `br.senac.academia.repository`

Os repositórios são **interfaces** que estendem `JpaRepository` e fornecem métodos para acesso aos dados sem escrever SQL manualmente.

#### Características:

1. **Spring Data JPA:**
   - Herda métodos CRUD básicos automaticamente
   - Permite criar queries customizadas apenas com nomes de métodos
   - Suporta queries JPQL e SQL nativas

2. **Métodos Automáticos:**
   - `findAll()` - Busca todos os registros
   - `findById(id)` - Busca por ID
   - `save(entity)` - Salva ou atualiza
   - `deleteById(id)` - Remove por ID
   - `count()` - Conta registros

3. **Query Methods:**
   - Spring Data JPA cria queries automaticamente baseado no nome do método
   - Exemplo: `findByEmail(String email)` → `SELECT * FROM alunos WHERE email = ?`

#### Exemplo - AlunoRepository:

```java
@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    // Query automática baseada no nome do método
    Optional<Aluno> findByEmail(String email);
    Optional<Aluno> findByCpf(String cpf);
    List<Aluno> findByStatus(StatusAluno status);
    
    // Query customizada com JPQL
    @Query("SELECT AVG(YEAR(CURRENT_DATE) - YEAR(a.dataNascimento)) FROM Aluno a")
    Double calcularMediaIdade();
    
    // Query com contagem
    @Query("SELECT COUNT(a) FROM Aluno a WHERE a.status = 'ATIVO'")
    Long countAlunosAtivos();
}
```

#### Padrões de Nomenclatura:

- `findBy...` - Busca por campo
- `findBy...And...` - Busca com múltiplas condições (AND)
- `findBy...Or...` - Busca com múltiplas condições (OR)
- `countBy...` - Conta registros
- `existsBy...` - Verifica existência
- `deleteBy...` - Remove registros

---

### 3. Service (Serviços)

**Localização:** `br.senac.academia.service`

Os serviços contêm a **lógica de negócio** da aplicação. Eles atuam como intermediários entre os controllers e os repositories.

#### Responsabilidades:

1. **Validações de Negócio:**
   - Verificar regras antes de salvar
   - Validar unicidade de campos
   - Aplicar transformações de dados

2. **Orquestração:**
   - Coordenar múltiplas operações
   - Gerenciar transações
   - Tratar erros de negócio

3. **Abstração:**
   - Esconde detalhes de implementação do repository
   - Fornece interface mais amigável para controllers

#### Exemplo - AlunoService:

```java
@Service
public class AlunoService {
    @Autowired
    private AlunoRepository alunoRepository;
    
    public Aluno save(Aluno aluno) {
        // Validação de negócio: CPF único
        Optional<Aluno> alunoComCpf = alunoRepository.findByCpf(aluno.getCpf());
        if (alunoComCpf.isPresent() && 
            !alunoComCpf.get().getId().equals(aluno.getId())) {
            throw new RuntimeException("CPF já cadastrado");
        }
        
        // Validação de negócio: Email único
        Optional<Aluno> alunoComEmail = alunoRepository.findByEmail(aluno.getEmail());
        if (alunoComEmail.isPresent() && 
            !alunoComEmail.get().getId().equals(aluno.getId())) {
            throw new RuntimeException("Email já cadastrado");
        }
        
        return alunoRepository.save(aluno);
    }
}
```

#### Padrões:

- **@Service**: Marca a classe como serviço Spring
- **@Autowired**: Injeta dependências automaticamente
- **Transações**: Métodos públicos são transacionais por padrão

---

### 4. Controller (Controladores)

**Localização:** `br.senac.academia.controller`

Os controllers são a **camada de apresentação** que expõe endpoints REST para clientes externos.

#### Características:

1. **Anotações REST:**
   - `@RestController` - Combina @Controller + @ResponseBody
   - `@RequestMapping` - Define o caminho base do controller
   - `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` - Mapeiam métodos HTTP

2. **Parâmetros:**
   - `@PathVariable` - Extrai variáveis da URL (`/api/alunos/{id}`)
   - `@RequestBody` - Converte JSON do corpo da requisição em objeto Java
   - `@RequestParam` - Extrai parâmetros de query string (`?status=ATIVO`)

3. **Respostas HTTP:**
   - `ResponseEntity.ok()` - Status 200 OK
   - `ResponseEntity.notFound()` - Status 404 Not Found
   - `ResponseEntity.noContent()` - Status 204 No Content
   - `ResponseEntity.badRequest()` - Status 400 Bad Request

#### Exemplo - AlunoController:

```java
@RestController
@RequestMapping("/api/alunos")
public class AlunoController {
    @Autowired
    private AlunoService alunoService;
    
    // GET /api/alunos
    @GetMapping
    public List<Aluno> getAllAlunos() {
        return alunoService.findAll();
    }
    
    // GET /api/alunos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Aluno> getAlunoById(@PathVariable Long id) {
        Optional<Aluno> aluno = alunoService.findById(id);
        return aluno.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    // POST /api/alunos
    @PostMapping
    public Aluno createAluno(@RequestBody Aluno aluno) {
        return alunoService.save(aluno);
    }
    
    // PUT /api/alunos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Aluno> updateAluno(
            @PathVariable Long id, 
            @RequestBody Aluno alunoDetails) {
        Optional<Aluno> alunoOpt = alunoService.findById(id);
        if (alunoOpt.isPresent()) {
            Aluno aluno = alunoOpt.get();
            aluno.setNome(alunoDetails.getNome());
            // ... atualiza outros campos
            return ResponseEntity.ok(alunoService.save(aluno));
        }
        return ResponseEntity.notFound().build();
    }
    
    // DELETE /api/alunos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAluno(@PathVariable Long id) {
        if (alunoService.findById(id).isPresent()) {
            alunoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
```

#### Convenções REST:

- **GET** - Buscar dados (não modifica estado)
- **POST** - Criar novo recurso
- **PUT** - Atualizar recurso existente (substituição completa)
- **DELETE** - Remover recurso

---

### 5. Security (Segurança)

**Localização:** `br.senac.academia.security`

A camada de segurança implementa autenticação e autorização usando **JWT (JSON Web Token)**.

#### Componentes:

##### 5.1. JwtUtil

**Responsabilidade:** Gerenciar criação, validação e extração de dados de tokens JWT.

```java
@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;  // Chave secreta do application.properties
    
    @Value("${jwt.expiration}")
    private Long expiration;  // Tempo de expiração em milissegundos
    
    // Gera um novo token JWT
    public String generateToken(String username, List<String> roles) {
        return Jwts.builder()
            .setSubject(username)
            .claim("roles", roles)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }
    
    // Valida se o token é válido (assinatura e expiração)
    public boolean validateToken(String token) { ... }
    
    // Extrai o username do token
    public String getUsername(String token) { ... }
    
    // Extrai as roles do token
    public List<String> getRoles(String token) { ... }
}
```

**Funcionamento:**
1. Usa a biblioteca `io.jsonwebtoken` (JJWT)
2. Assina tokens com algoritmo HS256 (HMAC SHA-256)
3. Armazena username e roles no payload do token
4. Valida assinatura e expiração antes de aceitar tokens

##### 5.2. JwtFilter

**Responsabilidade:** Interceptar requisições HTTP e validar tokens JWT antes de chegar aos controllers.

```java
@Component
public class JwtFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) {
        String header = request.getHeader("Authorization");
        
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.getUsername(token);
                List<String> roles = jwtUtil.getRoles(token);
                
                // Cria autenticação Spring Security
                UsernamePasswordAuthenticationToken auth = 
                    new UsernamePasswordAuthenticationToken(
                        username, null, 
                        roles.stream()
                            .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                            .collect(Collectors.toList())
                    );
                
                // Define no contexto de segurança
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
```

**Fluxo:**
1. Intercepta todas as requisições
2. Verifica header `Authorization: Bearer <token>`
3. Valida o token
4. Se válido, cria autenticação Spring Security
5. Define no `SecurityContextHolder`
6. Continua a cadeia de filtros

##### 5.3. JwtAuthenticationEntryPoint

**Responsabilidade:** Tratar erros de autenticação (401 Unauthorized).

```java
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) {
        // Verifica se é endpoint público
        if (isPublicEndpoint(request.getRequestURI())) {
            return;  // Não retorna erro para endpoints públicos
        }
        
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(
            "{\"erro\": \"Token inválido ou ausente. Faça login novamente.\"}"
        );
    }
}
```

##### 5.4. SecurityConfig

**Localização:** `br.senac.academia.config.SecurityConfig`

**Responsabilidade:** Configurar regras de segurança, endpoints públicos e proteção por roles.

```java
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    
    private static final String[] PUBLIC_ENDPOINTS = {
        "/auth/login",
        "/auth/validate",
        "/v3/api-docs/**",
        "/swagger-ui/**"
    };
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())  // Desabilita CSRF para APIs REST
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()  // Endpoints públicos
                .requestMatchers(HttpMethod.POST, "/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")
                .anyRequest().authenticated()  // Demais endpoints precisam de autenticação
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Criptografia de senhas
    }
}
```

**Regras de Autorização:**
- **POST** e **DELETE** requerem role `ADMIN`
- **GET** e **PUT** requerem apenas autenticação
- Endpoints `/auth/**` são públicos

##### 5.5. AuthController

**Responsabilidade:** Endpoints de autenticação (login, validação de token).

```java
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, String> credenciais) {
        
        // Autentica usando Spring Security
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                credenciais.get("username"),
                credenciais.get("password")
            )
        );
        
        // Extrai roles
        List<String> roles = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .map(authority -> authority.replace("ROLE_", ""))
            .collect(Collectors.toList());
        
        // Gera token JWT
        String token = jwtUtil.generateToken(
            authentication.getName(), 
            roles
        );
        
        return ResponseEntity.ok(Map.of(
            "token", token,
            "username", authentication.getName(),
            "roles", roles
        ));
    }
}
```

---

### 6. Config (Configurações)

**Localização:** `br.senac.academia.config`

#### 6.1. CorsConfig

**Responsabilidade:** Configurar CORS (Cross-Origin Resource Sharing) para permitir requisições de diferentes origens.

```java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("*")  // Permite todas as origens
                    .allowedMethods("GET", "POST", "PUT", "DELETE")
                    .allowedHeaders("*");
            }
        };
    }
}
```

**CORS:** Permite que aplicações web em domínios diferentes façam requisições à API.

#### 6.2. OpenApiConfig

**Responsabilidade:** Configurar Swagger/OpenAPI para documentação automática da API.

```java
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "API Academia",
        version = "1.0",
        description = "Documentação da API da aplicação Academia"
    )
)
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .components(new Components()
                .addSecuritySchemes("bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                )
            );
    }
}
```

**Acesso:** `http://localhost:8080/swagger-ui/index.html`

---

## 🔄 Fluxo de Dados

### Fluxo Completo de uma Requisição:

```
1. Cliente faz requisição HTTP
   ↓
2. JwtFilter intercepta
   ├─ Se tem token → Valida e cria autenticação
   └─ Se não tem token → Continua (Spring Security decide)
   ↓
3. SecurityConfig verifica regras
   ├─ Endpoint público? → Permite
   ├─ Precisa autenticação? → Verifica token
   └─ Precisa role específica? → Verifica roles
   ↓
4. Controller recebe requisição
   ↓
5. Controller chama Service
   ↓
6. Service aplica lógica de negócio
   ↓
7. Service chama Repository
   ↓
8. Repository executa query no banco
   ↓
9. Dados retornam pela cadeia
   ↓
10. Controller retorna JSON para cliente
```

### Exemplo Prático - Criar Aluno:

```
POST /api/alunos
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
  "nome": "João Silva",
  "email": "joao@email.com",
  ...
}

Fluxo:
1. JwtFilter valida token → Cria autenticação ADMIN
2. SecurityConfig verifica: POST requer ADMIN ✓
3. AlunoController.createAluno() recebe requisição
4. AlunoService.save() valida CPF/Email únicos
5. AlunoRepository.save() persiste no banco
6. Retorna aluno criado como JSON
```

---

## 🔐 Autenticação e Autorização JWT

### Fluxo de Autenticação:

```
1. Cliente envia credenciais
   POST /auth/login
   {
     "username": "admin",
     "password": "1234"
   }

2. AuthController valida credenciais
   ├─ AuthenticationManager autentica
   └─ Se válido → Gera token JWT

3. Retorna token
   {
     "token": "eyJhbGciOiJIUzI1NiJ9...",
     "username": "admin",
     "roles": ["ADMIN"]
   }

4. Cliente armazena token

5. Próximas requisições incluem token
   Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

6. JwtFilter valida token em cada requisição
```

### Estrutura do Token JWT:

```
Header:
{
  "alg": "HS256",
  "typ": "JWT"
}

Payload:
{
  "sub": "admin",           // Username
  "roles": ["ADMIN"],        // Roles do usuário
  "iat": 1764733065,        // Issued at (emitido em)
  "exp": 1764736665         // Expiration (expira em)
}

Signature:
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret
)
```

### Configuração no application.properties:

```properties
jwt.secret=MinhaChaveSecretaSuperSeguraParaJWTTokenDeAcademia2024!@#$%^&*()
jwt.expiration=3600000  # 1 hora em milissegundos
```

---

## 🔗 Relacionamentos entre Entidades

### Diagrama de Relacionamentos:

```
Plano (1) ────< (N) Aluno
              │
              │ (N)
              │
              ↓
         Instrutor (1) ────< (N) Aluno

Aluno (1) ────< (N) AvaliacaoFisica

Instrutor (1) ────< (N) Aula

Turma (N) ────< (N) Aluno  (Many-to-Many)

Treino (1) ────< (N) ExercicioTreino ────> (1) Exercicio

Aluno (1) ────< (N) Pagamento ────> (1) Plano
```

### Tipos de Relacionamentos:

1. **@ManyToOne** (N:1)
   - Muitos alunos para um plano
   - Muitas avaliações para um aluno
   - **Lado "Many"** tem a chave estrangeira

2. **@OneToMany** (1:N)
   - Um plano tem muitos alunos
   - Um aluno tem muitas avaliações
   - **Lado "One"** não tem chave estrangeira

3. **@ManyToMany** (N:N)
   - Muitos alunos em muitas turmas
   - Requer tabela intermediária (`aluno_turma`)

4. **@OneToOne** (1:1)
   - Relacionamento um-para-um
   - Menos comum no sistema

### Fetch Types:

- **LAZY**: Carrega relacionamento apenas quando acessado
- **EAGER**: Carrega relacionamento imediatamente
- **Padrão**: `@ManyToOne` e `@OneToOne` são EAGER, `@OneToMany` e `@ManyToMany` são LAZY

---

## 📝 Convenções e Boas Práticas

### Nomenclatura:

- **Classes Model**: Substantivos no singular (Aluno, Instrutor)
- **Repositories**: `[Entidade]Repository` (AlunoRepository)
- **Services**: `[Entidade]Service` (AlunoService)
- **Controllers**: `[Entidade]Controller` (AlunoController)

### Anotações Importantes:

- **@Entity**: Marca classe como entidade JPA
- **@Repository**: Marca interface como repositório
- **@Service**: Marca classe como serviço
- **@RestController**: Marca classe como controller REST
- **@Autowired**: Injeta dependência automaticamente
- **@Component**: Marca classe como componente Spring

### Tratamento de Erros:

- **RuntimeException**: Para erros de negócio (CPF duplicado, etc.)
- **ResponseEntity.notFound()**: Quando recurso não existe
- **ResponseEntity.badRequest()**: Quando dados inválidos

---

## 🚀 Como Usar a API

### 1. Fazer Login:

```bash
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "1234"
}

Resposta:
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "roles": ["ADMIN"],
  "tipo": "Bearer"
}
```

### 2. Usar Token em Requisições:

```bash
GET http://localhost:8080/api/alunos
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### 3. Criar Recurso (requer ADMIN):

```bash
POST http://localhost:8080/api/alunos
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
  "nome": "João Silva",
  "email": "joao@email.com",
  "cpf": "12345678901",
  "dataNascimento": "1990-01-15",
  "telefone": "11999887766",
  "plano": { "id": 1 }
}
```

---

## 📚 Recursos Adicionais

- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **H2 Console**: `http://localhost:8080/h2-console`
- **API Docs**: `http://localhost:8080/v3/api-docs`

---

## 🔧 Configuração do Banco de Dados

### application.properties:

```properties
# H2 (Desenvolvimento)
spring.datasource.url=jdbc:h2:mem:academiadb
spring.datasource.driverClassName=org.h2.Driver
spring.h2.console.enabled=true

# JPA
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# JWT
jwt.secret=MinhaChaveSecretaSuperSeguraParaJWTTokenDeAcademia2024!@#$%^&*()
jwt.expiration=3600000
```

---

## 📖 Conclusão

Esta documentação cobre todos os aspectos principais da arquitetura e implementação do sistema de academia. Cada camada tem responsabilidades bem definidas, seguindo os princípios SOLID e boas práticas de desenvolvimento Spring Boot.

Para dúvidas específicas sobre alguma parte do código, consulte os comentários inline nos arquivos fonte ou esta documentação.


