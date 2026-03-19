# JavaCorrige - Sistema de Correção Automática de Exercícios Práticos de POO

O **JavaCorrige** é uma solução projetada para automatizar a correção de exercícios práticos de Programação Orientada a Objetos (POO) em Java. Utilizando técnicas avançadas de reflexão, o sistema analisa o código enviado pelos alunos e gera relatórios com correções de métodos, atributos e construtores, além de atribuir notas automaticamente com base nas especificações do gabarito.

---

## Visão Geral

O software automatiza o processo de avaliação de exercícios práticos de POO. As principais funcionalidades incluem:

1. **Carregamento de Arquivos**:
   - Navega pelas pastas especificadas para encontrar os arquivos `.java` dos alunos e os arquivos de gabarito.
   - Descompacta automaticamente os arquivos enviados, caso estejam compactados.

2. **Compilação do Código**:
   - Verifica se o código dos alunos compila corretamente e registra os resultados.

3. **Análise de Estruturas**:
   - Utiliza a API de reflexão para comparar o código dos alunos com os gabaritos.
   - Avalia atributos, métodos e construtores com base nas especificações do gabarito.

4. **Atribuição de Notas**:
   - Usa a anotação `@Nota` do pacote `com.javacorrige` para atribuir pontuações específicas a cada elemento (atributos, métodos e construtores).
   - Usa a anotação `@Especificacao` do pacote `com.javacorrige` para atribuir a similaridade dos nomes e se os atributos devem ser privados.

5. **Geração de Relatórios em PDF**:
   - Gera relatórios detalhados para cada aluno, contendo informações sobre a compilação e os erros encontrados, além de incluir o nome do aluno no cabeçalho do PDF.

---

## Preparando o Ambiente

### 1. Pasta de Gabarito

- **Estrutura**:
  - A pasta raiz do gabarito deve ter o nome geral do exercício.
  - Dentro da pasta raiz, crie subpastas para cada etapa ou exercício (os nomes das subpastas devem estar organizados em ordem alfanumérica).
  - Cada subpasta deve conter os arquivos `.java` que servirão como gabarito.

- **Anotação `@Nota`**:
  - Utilize a biblioteca `com.javacorrige` para atribuir notas aos elementos do gabarito:
    ```java
    @Nota(3)
    public void exemploMetodo() {
        // Exemplo de método anotado com nota
    }
    ```

- **Anotação `Especificacao`**:
  - Utilize a biblioteca `com.javacorrige` para definir se os atributos, construtores e métodos devem ser exatos. O nível de similaridade dos nomes
    e a penalidade na nota para cada item que faltar:

  ```java
  @Especificacao(atributosExatos = false, construtoresExatos = true, similaridade = 0.7, penalidade = 0.2) // construtores está presente mas penso em retirar.
  public class Classe {
     //Exemplo de classe anotado com Especificação
  }
  ```

- **Anotação `Testar`**:
  - Utilize a biblioteca `com.javacorrige` para definir testes automatizados para métodos, especificando valores de entrada e, opcionalmente, o construtor utilizado.

  ```java
  import com.javacorrige.Testar;

  public class ExemploGabarito {
     private int soma;

     public ExemploGabarito(int inicial) {
        this.soma = inicial;
     }

     @Testar(parametros = {"5", "10"}, construtor = {"0"})
     public int somar(int a, int b) {
        return a + b + soma;
     }
  }
  ```

- **Baixando a Biblioteca**:
  - Faça o download da biblioteca `com.javacorrige` no seguinte link: [javacorrigelib v1.0](https://github.com/GabrielDaniAz/javacorrigelib/releases/tag/v1.0).

### 2. Pasta de Códigos dos Alunos

- **Estrutura**:
  - A pasta raiz deve conter apenas subpastas com o nome de cada aluno.
  - Dentro de cada subpasta, devem estar os arquivos `.java` enviados pelo aluno.

- **Arquivos Compactados**:
  - Caso os arquivos sejam enviados compactados, o software descompacta automaticamente e organiza a estrutura conforme necessário.

### 3. Diretório de Saída dos Relatórios

- Informe o diretório onde deseja salvar os PDFs gerados. Caso o diretório não exista, ele será criado automaticamente.

---

## Executando o Software

Para executar o software, você tem duas opções:

1. **Via Maven:**
   Antes de rodar o programa, é importante realizar uma compilação completa para garantir que todas as dependências estejam corretas. Execute:

   ```bash
   mvn clean install
   ```

   Em seguida, rode o programa diretamente pelo Maven:

   ```bash
   mvn exec:java
   ```

2. **Via Executável:**
   Localize o arquivo executável gerado (veja as instruções abaixo) e simplesmente clique nele para iniciar o programa.

3. **Via Prompt de Comando:**
   Após gerar o arquivo `.jar` do projeto, você pode executar o software tanto no Windows quanto no Linux de forma direta pela linha de comando.

   ### 1. Configurar o Arquivo `.bat` (Windows) ou `.sh` (Linux)
   - O repositório contém um arquivo **`javacorrige.bat`** (Windows) e você pode criar um equivalente em Linux, chamado **`javacorrige.sh`**.
   - Para tornar a execução mais fácil, adicione o diretório do arquivo `.bat` ou `.sh` ao seu `PATH`.

   #### **Adicionando o Diretório ao PATH (Windows):**
   1. Copie o arquivo `javacorrige.bat` para um diretório permanente no seu sistema.
   2. Adicione esse diretório ao `PATH` do Windows:
      - Abra o **Painel de Controle**.
      - Navegue até **Sistema > Configurações Avançadas do Sistema > Variáveis de Ambiente**.
      - Na seção **Variáveis do Sistema**, localize a variável `Path` e clique em **Editar**.
      - Clique em **Novo** e adicione o caminho completo do diretório onde o `.bat` foi salvo.
   3. Salve as alterações e feche.

   Agora você pode rodar o programa pelo terminal com:

   ```cmd
   javacorrige "<diretorioGabarito>" "<diretorioAlunos>" "<diretorioPDFs>" "<passoCorrecao>"
   ```

   **Exemplo:**

   ```cmd
   javacorrige "C:\Exercicios\Gabarito" "C:\Exercicios\Alunos" "C:\Exercicios\PDFs" "Exercicio1"
   ```

   #### **Adicionando o Diretório ao PATH (Linux):**
   1. Copie o arquivo `javacorrige.sh` para um diretório permanente, como `/usr/local/bin`.
   2. Torne o arquivo executável com o comando:
      ```bash
      chmod +x /usr/local/bin/javacorrige.sh
      ```
   3. Renomeie o arquivo para `javacorrige` (opcional):
      ```bash
      mv /usr/local/bin/javacorrige.sh /usr/local/bin/javacorrige
      ```

   Agora, basta abrir o terminal dentro do diretório em que você armazenou o javaCorrige e então poderá rodar o programa diretamente:

   ```bash
   javacorrige "<diretorioGabarito>" "<diretorioAlunos>" "<diretorioPDFs>" "<passoCorrecao>"
   ```

   **Exemplo:**

   ```bash
   cd /home/Marvin/Documentos/JavaCorrige/

   javacorrige "/home/usuario/exercicios/gabarito" "/home/usuario/exercicios/alunos" "/home/usuario/exercicios/pdfs" "Exercicio1
   ```

---

## Criando o Executável

Para gerar o executável do software, siga estas etapas:

1. Certifique-se de que o JAR com dependências foi gerado. Use o comando:

   ```bash
   mvn clean package
   ```

   Isso criará o arquivo JAR no diretório `target/`.

2. Use o comando abaixo para criar um executável utilizando o `jpackage`:

   ```bash
   jpackage --input target/ --name JavaCorrige --main-jar javacorrige-1.0-SNAPSHOT-jar-with-dependencies.jar --main-class com.javacorrige.Main --type app-image --icon src/main/resources/assets/icons/javacorrige_icon.ico --dest "C:\JavaCorrige"
   ```

   Este comando:
   - Cria uma pasta chamada `JavaCorrige` no diretório especificado em `--dest`.
   - Dentro da pasta, você encontrará o executável e os arquivos necessários para rodá-lo.

3. **Alterar diretório de saída:**
   Se preferir um diretório de saída diferente, substitua `C:\JavaCorrige` no parâmetro `--dest` pelo caminho desejado.

---

Após seguir esses passos, você poderá executar o software diretamente pelo executável ou compartilhar a pasta gerada com outros usuários.

---

## Funcionamento do Sistema

1. O sistema percorre as pastas configuradas, descompacta os arquivos enviados pelos alunos (se necessário) e organiza a estrutura conforme especificado.
2. Compila os arquivos `.java` de cada aluno e registra o sucesso ou falhas de compilação.
3. Compara as classes dos alunos com os gabaritos utilizando a API de reflexão:
   - Verifica métodos, atributos e construtores presentes.
   - Avalia cada elemento com base na anotação `@Nota` do gabarito.
4. Gera relatórios detalhados em PDF para cada aluno, destacando:
   - Resultados da compilação.
   - Elementos ausentes ou incorretos.
   - Pontuações atribuídas a cada etapa.

---

## Considerações Importantes

- A ordem alfanumérica das subpastas do gabarito é utilizada pelo sistema para organizar as etapas. Certifique-se de nomear as subpastas corretamente.
- Caso algum aluno não possua arquivos `.java` correspondentes à etapa especificada, o relatório gerado indicará as ausências.
- O software é compatível com o **JDK 21**. Certifique-se de ter o JDK instalado no ambiente.

---

## Tecnologias Utilizadas

- **Java**: Linguagem principal do projeto.
- **Reflection API**: Para análise das classes e comparação com o gabarito.
- **Apache Maven**: Gerenciador de dependências e execução do projeto.
- **iText**: Biblioteca para geração de relatórios em PDF.
